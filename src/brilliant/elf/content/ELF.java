package brilliant.elf.content;

import static brilliant.elf.content.ELF_Constant.DT_RelType.R_ARM_ABS32;
import static brilliant.elf.content.ELF_Constant.DT_RelType.R_ARM_COPY;
import static brilliant.elf.content.ELF_Constant.DT_RelType.R_ARM_GLOB_DAT;
import static brilliant.elf.content.ELF_Constant.DT_RelType.R_ARM_IRELATIVE;
import static brilliant.elf.content.ELF_Constant.DT_RelType.R_ARM_JUMP_SLOT;
import static brilliant.elf.content.ELF_Constant.DT_RelType.R_ARM_REL32;
import static brilliant.elf.content.ELF_Constant.DT_RelType.R_ARM_RELATIVE;
import static brilliant.elf.content.ELF_Constant.ELFUnit.ELF32_Addr;
import static brilliant.elf.content.ELF_Constant.ELFUnit.uint32_t;
import static brilliant.elf.content.ELF_Constant.SHN_Info.SHN_UNDEF;
import static brilliant.elf.content.ELF_Constant.STB_Info.STB_GLOBAL;
import static brilliant.elf.content.ELF_Constant.STB_Info.STB_LOCAL;
import static brilliant.elf.content.ELF_Constant.STB_Info.STB_WEAK;
import static brilliant.elf.content.ELF_Definition.ELF_R_SYM;
import static brilliant.elf.content.ELF_Definition.ELF_R_TYPE;
import static brilliant.elf.content.ELF_Definition.ELF_ST_BIND;
import static brilliant.elf.vm.OS.MAP_FIXED;
import static brilliant.elf.vm.OS.PAGE_END;
import static brilliant.elf.vm.OS.PAGE_START;

import java.io.File;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.activation.UnsupportedDataTypeException;
import javax.management.RuntimeErrorException;

import brilliant.elf.content.ELF_Dynamic.Elf_Sym;
import brilliant.elf.content.ELF_ProgramHeader.ELF_Phdr;
import brilliant.elf.content.ELF_Relocate.Elf_rel;
import brilliant.elf.util.ByteUtil;
import brilliant.elf.util.Log;
import brilliant.elf.vm.OS;

/**
 * Construct a new Elf decoder which only support arm/32bit
 * 
 * @author Yoogurt
 *
 */
@SuppressWarnings("all")
public class ELF {

	public static final String[] ENV = { "env/" };

	/**
	 * Global Loaded Object
	 */
	private static final Map<String, ELF> glo = new LinkedHashMap<>();

	/* main executable */
	private static final ELF somain;

	/* main executable file , in android , usually is app_process , ignore */
	static {
		somain = null;
	}

	private static class ReserveLoadableSegment {
		long min_address;
		long max_address;
	}

	private static class MapEntry {
		long seg_start;
		long seg_end;

		long seg_page_start;
		long seg_page_end;

		long seg_file_end;

		// file offset
		long file_start;
		long file_end;

		long file_page_start;
		long file_length;
	}

	/* for disassembler */
	public static class FakeFuncImpl {
		public String function;
		public int address;

		FakeFuncImpl(String function, int address) {
			this.function = function;
			this.address = address;
		}
	}

	public static boolean forDisassmebler = true;

	private Map<ELF_Phdr, MapEntry> MAP = new HashMap<>();

	private boolean mEnable = false;

	/* full name of this file */
	private String name;

	private ELF_Header elf_header;
	private ELF_ProgramHeader elf_phdr;
	private ELF_Dynamic elf_dynamic;

	/* current elf needs file */
	private ELF[] needed;

	/* elf hash */
	private int nbucket;
	private int nchain;
	private int bucket;
	private int chain;
	/* elf hash */

	/* gnu hash */
	private boolean is_gnu_hash = false;

	private long gnu_hash;
	private int gnu_nbucket;
	private int gnu_maskwords;
	private int gnu_shift2;
	private int gnu_bloom_filter;
	private int gnu_bucket;
	private int gnu_chain;
	/* gnu hash */

	/* symbol table */
	private int symtab;

	/* string table */
	private int strtab;

	private int pltgot;

	/* initialized function */
	private int init_func;
	private boolean hasInitFunc = false;

	private int init_array;
	private int init_array_sz;
	private boolean hasInitArray = false;
	/* initialized function */

	/* finished function */
	private int fini_func;
	private boolean hasFiniFunc = false;

	private int fini_array;
	private int fini_array_sz;
	private boolean hasFiniArray = false;
	/* initialize function */

	/* elf base address , usually be 0 */
	private int elf_base;

	/* elf load offset */
	private int elf_load_bias;

	/* takes space in memory */
	private int elf_size;

	private boolean hasDT_SYMBOLIC;

	public ELF_Header getElf_header() {
		return elf_header;
	}

	public ELF_ProgramHeader getElf_phdr() {
		return elf_phdr;
	}

	public static ELF dlopen(String file) {

		if (glo.containsKey(file))
			return glo.get(file);

		try {

			ELF elf = new ELF(file, true);
			elf.mEnable = true;

			glo.put(file, elf);
			return elf;

		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	public static final String dlerror() {
		return null;
	}

	public boolean isLittleEndian() {
		return elf_header.isLittleEndian();
	}

	private ELF(String file, boolean do_load) throws Exception {
		this(new File(file), do_load);
		System.out.println("\n" + name + " loaded ! base : " + Integer.toHexString(elf_base) + " to : "
				+ Integer.toHexString(elf_base + elf_size) + "  load_bias : " + Integer.toHexString(elf_load_bias)
				+ "\n\n\n");
	}

	public static int dlsym(ELF elf, String functionName) {

		if (!elf.mEnable || forDisassmebler)
			return -1;

		if (elf != null) {
			Elf_Sym sym = soinfo_elf_lookup(elf, elf_hash(functionName), functionName);

			if (sym != null)
				if (ELF_ST_BIND(sym.st_info) == STB_GLOBAL && ByteUtil.bytes2Int32(sym.st_shndx) != 0)
					return ByteUtil.bytes2Int32(sym.st_value) + elf.elf_load_bias;
		}
		return 0;
	}

	public static void dlcolse() {

		OS.getMainImage().reset();
		glo.clear();

	}

	private ELF(File file, boolean do_load) throws Exception {

		RandomAccessFile raf = new RandomAccessFile(file, "r");
		name = file.getName();

		try {
			findLibrary(raf, do_load);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private void findLibrary(RandomAccessFile raf, boolean do_load) throws Exception {

		elf_header = new ELF_Header(raf);
		/*
		 * big endian will be supported someday rather than now
		 */
		if (!elf_header.isLittleEndian())
			throw new UnsupportedDataTypeException("ELFDecoder don't support big endian architecture");

		/*
		 * 64bit will be supported someday rather than now
		 */
		if (!elf_header.is32Bit())
			throw new UnsupportedDataTypeException("ELFDecoder don't support except 32 bit architecture");

		elf_phdr = new ELF_ProgramHeader(raf, elf_header, false);

		if (!do_load)
			new ELF_SectionHeader(raf, elf_header);
		else {
			reserveAddressSpace();
			loadSegments(raf);
		}

		elf_dynamic = new ELF_Dynamic(raf, elf_phdr.getDynamicSegment(), elf_load_bias);

		if (do_load) {
			link_image();

			callConstructors();
			callArrays();
		}
	}

	private void reserveAddressSpace() throws ELFDecodeException {

		List<ELF_Phdr> allLoadableSegment = elf_phdr.getAllLoadableSegment();

		ReserveLoadableSegment r = phdr_table_get_load_size(allLoadableSegment);

		elf_base = OS.getMainImage().mmap(0, (int) (r.max_address - r.min_address), (byte) 0, null, 0);
		if (elf_base < 0)
			throw new RuntimeException("mmap fail while reservse space");

		elf_load_bias = (int) (elf_base
				- r.min_address); /*
									 * r.min_address is supposed to be 0 , in
									 * face , it is
									 */
		Log.e("load_bias " + elf_load_bias + "  elf_base " + elf_base + " r.min_address " + r.min_address);
		elf_size = (int) (r.max_address + elf_load_bias - elf_base);
	}

	private ReserveLoadableSegment phdr_table_get_load_size(List<ELF_Phdr> loadableSegment) throws ELFDecodeException {

		int minAddress = Integer.MAX_VALUE;
		int maxAddress = Integer.MIN_VALUE;

		for (ELF_Phdr phdr : loadableSegment) {/*
												 * if (phdr->p_vaddr <
												 * min_vaddr) { min_vaddr =
												 * phdr->p_vaddr; // min virtual
												 * address } if (phdr->p_vaddr +
												 * phdr->p_memsz > max_vaddr) {
												 * max_vaddr = phdr->p_vaddr +
												 * phdr->p_memsz; // max virtual
												 * address }
												 */

			int address = ByteUtil.bytes2Int32(phdr.p_vaddr);
			int memsize = ByteUtil.bytes2Int32(phdr.p_memsz);
			if (address < minAddress)
				minAddress = address;

			if (address + memsize > maxAddress)
				maxAddress = address + memsize;

		}

		ReserveLoadableSegment r = new ReserveLoadableSegment();
		r.min_address = PAGE_START(minAddress);
		r.max_address = PAGE_END(maxAddress);

		Log.e("min_address 0x" + Long.toHexString(r.min_address));
		Log.e("max_address 0x" + Long.toHexString(r.max_address));

		if (minAddress > maxAddress || maxAddress < 0 || minAddress < 0)
			throw new ELFDecodeException("can not parse phdr address");

		return r;
	}

	private void loadSegments(RandomAccessFile raf) {

		List<ELF_Phdr> phs = elf_phdr.getAllLoadableSegment();
		for (ELF_Phdr ph : phs) {

			MapEntry m = new MapEntry();

			m.seg_start = ByteUtil.bytes2Int64(ph.p_vaddr) + elf_load_bias;
			m.seg_end = m.seg_start + ByteUtil.bytes2Int64(ph.p_memsz);

			m.seg_page_start = PAGE_START(m.seg_start);
			m.seg_page_end = PAGE_END(m.seg_end);

			m.seg_file_end = m.seg_start + ByteUtil.bytes2Int64(ph.p_filesz);

			// file offset
			m.file_start = ByteUtil.bytes2Int64(ph.p_offset);
			m.file_end = m.file_start + ByteUtil.bytes2Int64(ph.p_filesz);

			m.file_page_start = PAGE_START(m.file_start);
			m.file_length = m.file_end - m.file_page_start;

			if (0 > OS.getMainImage().mmap((int) m.seg_page_start, (int) m.file_length, MAP_FIXED, raf,
					m.file_page_start))
				throw new RuntimeException("Unable to mmap segment : " + ph.toString());

			MAP.put(ph, m);
		}

		/*
		 * zero full the remain space , in java it generate automatic and we
		 * don't check elf_phdr is in memory or not
		 */

	}

	private void link_image() {

		if (elf_dynamic.getDT_HASH() > 0) {
			/* extract some useful informations */
			nbucket = ByteUtil.bytes2Int32(OS.getMainImage().getMemory(),
					elf_dynamic.getDT_HASH() + elf_load_bias + uint32_t * 0, uint32_t, elf_header.isLittleEndian()); // value
			nchain = ByteUtil.bytes2Int32(OS.getMainImage().getMemory(),
					elf_dynamic.getDT_HASH() + elf_load_bias + uint32_t * 1, uint32_t, elf_header.isLittleEndian()); // value
			bucket = elf_dynamic.getDT_HASH() + elf_load_bias + 8; // pointer
			chain = elf_dynamic.getDT_HASH() + elf_load_bias + 8 + (nbucket << 2); // pointer
		}

		if (elf_dynamic.getDT_GNU_HASH() > 0) {
			gnu_nbucket = ByteUtil.bytes2Int32(OS.getMainImage().getMemory(),
					elf_dynamic.getDT_GNU_HASH() + elf_load_bias + uint32_t * 0, uint32_t, isLittleEndian());
			gnu_maskwords = ByteUtil.bytes2Int32(OS.getMainImage().getMemory(),
					elf_dynamic.getDT_GNU_HASH() + elf_load_bias + uint32_t * 2, uint32_t, isLittleEndian());
			gnu_shift2 = ByteUtil.bytes2Int32(OS.getMainImage().getMemory(),
					elf_dynamic.getDT_GNU_HASH() + elf_load_bias + uint32_t * 3, uint32_t, isLittleEndian());
			gnu_bloom_filter = elf_dynamic.getDT_GNU_HASH() + elf_load_bias + 16; // pointer
			gnu_bucket = gnu_bloom_filter + gnu_maskwords; // pointer

			gnu_chain = gnu_bucket + gnu_nbucket - ByteUtil.bytes2Int32(OS.getMainImage().getMemory(),
					elf_dynamic.getDT_GNU_HASH() + elf_load_bias + uint32_t * 1, uint32_t, isLittleEndian()); // pointer

			if (!ByteUtil.powerof2(gnu_maskwords))
				throw new IllegalArgumentException("invalid maskwords for gnu_hash = 0x"
						+ Integer.toHexString(gnu_maskwords) + " , in \"" + name + "\" expecting power to two");

			--gnu_maskwords;

			gnu_hash = gnu_hash(name);
			is_gnu_hash = true;
		}

		symtab = elf_dynamic.getDT_SYMTAB() + elf_load_bias; // pointer

		strtab = elf_dynamic.getDT_STRTAB() + elf_load_bias; // pointer

		pltgot = elf_dynamic.getDT_PLTGOT() + elf_load_bias;

		hasDT_SYMBOLIC = elf_dynamic.getDT_SYMBOLIC();

		if (elf_dynamic.getDT_INIT() != 0) {
			init_func = elf_dynamic.getDT_INIT() + elf_load_bias; // pointer
			hasInitFunc = true;
		}

		if (elf_dynamic.getDT_INIT_ARRAY() != 0) {
			init_array = elf_dynamic.getDT_INIT_ARRAY() + elf_load_bias; // pointer
			init_array_sz = elf_dynamic.getDT_INIT_ARRAYSZ() / ELF32_Addr;
			hasInitArray = true;
		}

		if (elf_dynamic.getDT_FINI() != 0) {
			fini_func = elf_dynamic.getDT_FINI() + elf_load_bias; // pointer
			hasFiniFunc = true;
		}
		if (elf_dynamic.getDT_FINI_ARRAY() != 0) {
			fini_array = elf_dynamic.getDT_FINI_ARRAY() + elf_load_bias;// pointer
			fini_array_sz = elf_dynamic.getDT_FINI_ARRAYSZ() / ELF32_Addr;
			hasFiniArray = false;
		}

		/* verify some parameters */
		if (elf_dynamic.getDT_HASH() <= 0 && elf_dynamic.getDT_GNU_HASH() <= 0)
			throw new RuntimeException("empty/mission DT_HASH and DT_GNU_HASH , new hash type in the future ?");
		if (strtab == 0)
			throw new RuntimeException("empty/missing DT_STRTAB");
		if (symtab == 0)
			throw new RuntimeException("empty/missing DT_SYMTAB");

		if (!forDisassmebler) {

			List<String> needed = elf_dynamic.getNeedLibraryName();
			this.needed = new ELF[needed.size()];

			/* Link elf requires libraries */
			int count = 0;
			for (String name : needed) {
				ELF elf = null;

				for (String env : ENV) {
					elf = dlopen(env + name);
					if (elf != null)
						break;
				}
				if (elf == null)
					throw new RuntimeException("Unable to load depend library " + name);

				this.needed[count++] = elf;
			}

			soinfo_relocate();

		} else { // extension for disassembler , we don't import necessary
					// library

			mFakeFuncImpl = allocFakeGotImpl(extractFakeFunction());
			fake_soinfo_relocate();

		}

	}

	private List<FakeFuncImpl> mFakeFuncImpl;

	private List<FakeFuncImpl> allocFakeGotImpl(List<String> functions) {

		int start;

		/* each function takes 4B */
		if ((start = OS.getMainImage().mmap(0, functions.size() << 2, 0, null, 0)) < 0)
			return null;

		if (PAGE_START(start) != start)
			throw new RuntimeException("FATAL : mmap() in an unaligned address");

		List<FakeFuncImpl> impl = new LinkedList<FakeFuncImpl>();

		for (String f : functions) {
			impl.add(new FakeFuncImpl(f, start));
			start += 4;
		}
		return impl;
	}

	private int soinfo_fake_lookup(String name) {

		for (FakeFuncImpl f : mFakeFuncImpl)
			if (f.function.equals(name))
				return f.address;

		throw new RuntimeException("Unable to find fake function");
	}

	private List<String> extractFakeFunction() {

		List<ELF_Relocate> rels = elf_dynamic.getRelocateSections();

		List<String> fake_relocation = new ArrayList<String>();

		for (ELF_Relocate r : rels) {

			Elf_rel[] entries = r.getRelocateEntry();
			for (Elf_rel rel : entries) {
				/*
				 * ElfW(Addr) reloc = static_cast<ElfW(Addr)>(rel->r_offset +
				 * load_bias);
				 */
				int sym = ELF_R_SYM(rel.r_info);
				int type = ELF_R_TYPE(rel.r_info);
				Log.e("strtab " + strtab);
				Log.e("sym " + sym);
				Log.e("symtab " + symtab);

				String sym_name = ByteUtil.getStringFromMemory(ByteUtil.bytes2Int32(
						Elf_Sym.reinterpret_cast(OS.getMainImage().getMemory(), symtab + Elf_Sym.size() * sym).st_name)
						+ strtab, OS.getMainImage());

				if (type == 0)
					continue;

				if (sym != 0) {
					/* we need to construct a fake function to fill GOT */
					fake_relocation.add(sym_name);
				}
			}
		}
		return fake_relocation;
	}

	/**
	 * what is relocation ? relocation fix a pointer which point at somewhere in
	 * file , but we need to let it point to memory correct
	 */
	public void fake_soinfo_relocate() {

		List<ELF_Relocate> rels = elf_dynamic.getRelocateSections();

		for (ELF_Relocate r : rels) {

			Elf_rel[] entries = r.getRelocateEntry();
			for (Elf_rel rel : entries) {
				/*
				 * ElfW(Addr) reloc = static_cast<ElfW(Addr)>(rel->r_offset +
				 * load_bias);
				 */
				int reloc = ByteUtil.bytes2Int32(rel.r_offset) + elf_load_bias;
				int sym = ELF_R_SYM(rel.r_info);
				int type = ELF_R_TYPE(rel.r_info);
				int addend = get_addend(rel, reloc);

				String sym_name = ByteUtil.getStringFromMemory(ByteUtil.bytes2Int32(
						Elf_Sym.reinterpret_cast(OS.getMainImage().getMemory(), symtab + Elf_Sym.size() * sym).st_name)
						+ strtab, OS.getMainImage());

				int sym_address = 0;

				if (type == 0)
					continue;

				if (sym != 0) {
					sym_address = soinfo_fake_lookup(sym_name);
					Log.e("Found fake Sym : " + sym_name + " at : " + Integer.toHexString(sym_address));
				}

				/*---------------------------------------------------------------------------------------*/

				switch (type) {
				case R_ARM_GLOB_DAT:

					Log.e("name : " + sym_name + " R_GENERIC_GLB_DAT at "
							+ ByteUtil.bytes2Hex(ByteUtil.int2bytes(reloc)) + " , relocating to 0x"
							+ Integer.toHexString(sym_address) + " , previours values : "
							+ ByteUtil.bytes2Int32(OS.getMainImage().getMemory(), reloc, ELF32_Addr, true));

					System.arraycopy(ByteUtil.int2bytes(sym_address), 0, OS.getMainImage().getMemory(), reloc,
							ELF32_Addr);/*
										 * reinterpret_cast< Elf32_Addr
										 * *>(reloc) = sym_addr;
										 */
					break;
				case R_ARM_JUMP_SLOT:

					Log.e("name : " + sym_name + " R_GENERIC_JUMP_SLOT at "
							+ ByteUtil.bytes2Hex(ByteUtil.int2bytes(reloc)) + " , relocating to 0x"
							+ Integer.toHexString(sym_address) + " , previours values : 0x" + Integer.toHexString(
									ByteUtil.bytes2Int32(OS.getMainImage().getMemory(), reloc, ELF32_Addr, true)));

					System.arraycopy(ByteUtil.int2bytes(sym_address), 0, OS.getMainImage().getMemory(), reloc,
							ELF32_Addr);

					break;

				case R_ARM_ABS32:
					Log.e("name : " + sym_name + " R_ARM_ABS32 at " + ByteUtil.bytes2Hex(ByteUtil.int2bytes(reloc))
							+ " , relocating to 0x" + Integer.toHexString(sym_address + addend)
							+ " , previours values : 0x" + Integer.toHexString(
									ByteUtil.bytes2Int32(OS.getMainImage().getMemory(), reloc, ELF32_Addr, true)));
					System.arraycopy(ByteUtil.int2bytes(sym_address + addend), 0, OS.getMainImage().getMemory(), reloc,
							ELF32_Addr);

					break;

				case R_ARM_REL32:
					Log.e("name : " + sym_name + " R_ARM_REL32 at " + ByteUtil.bytes2Hex(ByteUtil.int2bytes(reloc))
							+ " , relocating to 0x"
							+ Integer.toHexString(addend + sym_address - ByteUtil.bytes2Int32(rel.r_offset))
							+ " , previours values : " + Integer.toHexString(
									ByteUtil.bytes2Int32(OS.getMainImage().getMemory(), reloc, ELF32_Addr, true)));
					System.arraycopy(ByteUtil.int2bytes(addend + sym_address - ByteUtil.bytes2Int32(rel.r_offset)), 0,
							OS.getMainImage().getMemory(), reloc, ELF32_Addr);

					break;
				case R_ARM_RELATIVE: { // *reinterpret_cast<ElfW(Addr)*>(reloc)
										// = (load_bias + addend);
					Log.e("local sym : " + sym_name + " reloc : "
							+ ByteUtil.bytes2Hex(OS.getMainImage().getMemory(), reloc, 4) + "  become : "
							+ Integer.toHexString(elf_load_bias + addend) + " , previours values : "
							+ Integer.toHexString(
									ByteUtil.bytes2Int32(OS.getMainImage().getMemory(), reloc, ELF32_Addr, true)));

					System.arraycopy(ByteUtil.int2bytes(elf_load_bias + addend), 0, OS.getMainImage().getMemory(),
							reloc, ELF32_Addr);
					/* relocate here */

				}
					break;

				default:
					throw new RuntimeException("unknown weak reloc type" + type);
				}
				Log.e();
			}
		}
	}

	/**
	 * 
	 * It appears above Android 5.0
	 * 
	 */
	public int get_addend(Elf_rel rel, int reloc_addr) {

		if (ELF_R_TYPE(rel.r_info) == R_ARM_RELATIVE || ELF_R_TYPE(rel.r_info) == R_ARM_IRELATIVE
				|| ELF_R_TYPE(rel.r_info) == R_ARM_ABS32 || ELF_R_TYPE(rel.r_info) == R_ARM_REL32)
			return ByteUtil.bytes2Int32(OS.getMainImage().getMemory(), reloc_addr, ELF32_Addr,
					elf_header.isLittleEndian()); // Extract
		// reloc_addr(pointer)'s
		// value

		return 0; /*
					 * if (ELFW(R_TYPE)(rel->r_info) == R_ARM_RELATIVE ||
					 * ELFW(R_TYPE)(rel->r_info) == R_ARM_IRELATIVE) { return
					 * *reinterpret_cast<ElfW(Addr)*>(reloc_addr); } return 0;
					 */
	}

	/**
	 * what is relocation ? relocation fix a pointer which point at somewhere in
	 * file , but we need to let it point to memory correct
	 */
	public void soinfo_relocate() {

		List<ELF_Relocate> rels = elf_dynamic.getRelocateSections();

		Elf_Sym s = null;

		for (ELF_Relocate r : rels) {

			Elf_rel[] entries = r.getRelocateEntry();
			for (Elf_rel rel : entries) {
				/*
				 * ElfW(Addr) reloc = static_cast<ElfW(Addr)>(rel->r_offset +
				 * load_bias);
				 */
				int reloc = ByteUtil.bytes2Int32(rel.r_offset) + elf_load_bias;
				int sym = ELF_R_SYM(rel.r_info);
				int type = ELF_R_TYPE(rel.r_info);
				int addend = get_addend(rel, reloc);

				String sym_name = ByteUtil.getStringFromMemory(ByteUtil.bytes2Int32(
						Elf_Sym.reinterpret_cast(OS.getMainImage().getMemory(), symtab + Elf_Sym.size() * sym).st_name)
						+ strtab, OS.getMainImage());

				int sym_address = 0;

				if (type == 0)
					continue;

				if (sym != 0) {
					/*
					 * sym > 0 means the symbol are global , not in this file ,
					 * we will search it
					 * 
					 * byte[] st_name = new byte[4];
					 * System.arraycopy(OS.getMemory(), symtab + sym
					 * Elf_Sym.size(), st_name, 0, 4); // we get st_name(index)
					 * from symtab
					 */
					ELF[] lsi = new ELF[1];
					if (null == (s = find_sym_by_name(this, sym_name, lsi, needed))) {

						s = Elf_Sym.reinterpret_cast(OS.getMainImage().getMemory(), symtab + sym * Elf_Sym.size());

						/*
						 * we only allow STB_WEAK which is compiling with key
						 * word "extern" when we can't found symbol
						 */

						if (ELF_ST_BIND(s.st_info) != STB_WEAK)
							throw new RuntimeException("cannot locate symbol \"" + sym_name + "\" referenced by \""
									+ name + "\"... s.st_info " + Integer.toHexString(ELF_ST_BIND(s.st_info))
									+ " , s at " + Integer.toHexString(symtab + sym * Elf_Sym.size()));

						switch (type) {

						case R_ARM_JUMP_SLOT:
						case R_ARM_GLOB_DAT:
						case R_ARM_ABS32:
						case R_ARM_RELATIVE:
							break;

						case R_ARM_COPY:
						default:
							throw new RuntimeException("unknown weak reloc type " + ByteUtil.bytes2Hex(rel.r_info));
						}
						/*
						 * sym_address == 0 , we don't care STB_WEAK Symbol's
						 * address
						 */
						Log.e("Got a STB_WEAK Reference : " + sym_name);

					} else {// we got a definition

						sym_address = lsi[0].elf_load_bias + ByteUtil.bytes2Int32(s.st_value);

						Log.e("Found Sym : " + sym_name + " at : " + Integer.toHexString(sym_address));
					}

				} else
					s = null;

				/*---------------------------------------------------------------------------------------*/

				switch (type) {
				case R_ARM_GLOB_DAT:

					Log.e("name : " + sym_name + " R_GENERIC_GLB_DAT at "
							+ ByteUtil.bytes2Hex(ByteUtil.int2bytes(reloc)) + " , relocating to 0x"
							+ Integer.toHexString(sym_address) + " , previours values : "
							+ ByteUtil.bytes2Int32(OS.getMainImage().getMemory(), reloc, ELF32_Addr, true));

					System.arraycopy(ByteUtil.int2bytes(sym_address), 0, OS.getMainImage().getMemory(), reloc,
							ELF32_Addr);/*
										 * reinterpret_cast< Elf32_Addr
										 * *>(reloc) = sym_addr;
										 */
					break;
				case R_ARM_JUMP_SLOT:

					Log.e("name : " + sym_name + " R_GENERIC_JUMP_SLOT at "
							+ ByteUtil.bytes2Hex(ByteUtil.int2bytes(reloc)) + " , relocating to 0x"
							+ Integer.toHexString(sym_address) + " , previours values : 0x" + Integer.toHexString(
									ByteUtil.bytes2Int32(OS.getMainImage().getMemory(), reloc, ELF32_Addr, true)));

					System.arraycopy(ByteUtil.int2bytes(sym_address), 0, OS.getMainImage().getMemory(), reloc,
							ELF32_Addr);

					break;

				case R_ARM_ABS32:
					Log.e("name : " + sym_name + " R_ARM_ABS32 at " + ByteUtil.bytes2Hex(ByteUtil.int2bytes(reloc))
							+ " , relocating to 0x" + Integer.toHexString(sym_address + addend)
							+ " , previours values : 0x" + Integer.toHexString(
									ByteUtil.bytes2Int32(OS.getMainImage().getMemory(), reloc, ELF32_Addr, true)));
					System.arraycopy(ByteUtil.int2bytes(sym_address + addend), 0, OS.getMainImage().getMemory(), reloc,
							ELF32_Addr);

					break;

				case R_ARM_REL32:
					Log.e("name : " + sym_name + " R_ARM_REL32 at " + ByteUtil.bytes2Hex(ByteUtil.int2bytes(reloc))
							+ " , relocating to 0x"
							+ Integer.toHexString(addend + sym_address - ByteUtil.bytes2Int32(rel.r_offset))
							+ " , previours values : " + Integer.toHexString(
									ByteUtil.bytes2Int32(OS.getMainImage().getMemory(), reloc, ELF32_Addr, true)));
					System.arraycopy(ByteUtil.int2bytes(addend + sym_address - ByteUtil.bytes2Int32(rel.r_offset)), 0,
							OS.getMainImage().getMemory(), reloc, ELF32_Addr);

					break;
				case R_ARM_RELATIVE: { // *reinterpret_cast<ElfW(Addr)*>(reloc)
										// = (load_bias + addend);
					Log.e("local sym : " + sym_name + " reloc : "
							+ ByteUtil.bytes2Hex(OS.getMainImage().getMemory(), reloc, 4) + "  become : "
							+ Integer.toHexString(elf_load_bias + addend) + " , previours values : "
							+ Integer.toHexString(
									ByteUtil.bytes2Int32(OS.getMainImage().getMemory(), reloc, ELF32_Addr, true)));

					System.arraycopy(ByteUtil.int2bytes(elf_load_bias + addend), 0, OS.getMainImage().getMemory(),
							reloc, ELF32_Addr);
					/* relocate here */

				}
					break;

				default:
					throw new RuntimeException("unknown weak reloc type" + type);
				}

				Log.e();
			}
		}
	}

	private void callFunction(int func) {
		Log.e("Calling -> " + ByteUtil.bytes2Hex(ByteUtil.int2bytes(func)));
	}

	private void callConstructors() {

		if (hasInitFunc) {
			Log.e("Calling Constructor : " + ByteUtil.bytes2Hex(ByteUtil.int2bytes(init_func)));
			callFunction(init_func);
		} else
			Log.e("Constructor DT_INIT no found , skipping ... ");

	}

	private void callArrays() {

		if (hasInitArray)
			for (int i = 0; i < init_array_sz; i++)
				callFunction(init_array + (i << 2));
		else
			Log.e("InitArray DT_INIARR no found , skipping ...");

	}

	private static long elf_hash(String name) {

		char[] name_array = name.toCharArray();
		long h = 0, g;

		for (char n : name_array) {
			h = (h << 4) + n;
			g = h & 0xf0000000;
			h ^= g;
			h ^= g >>> 24;
		}
		return h & 0xffffffff;
	}

	/*
	 * static Elf32_Sym* soinfo_elf_lookup(soinfo* si, unsigned hash, const
	 * char* name) { Elf32_Sym* symtab = si->symtab; const char* strtab =
	 * si->strtab;
	 * 
	 * TRACE_TYPE(LOOKUP, "SEARCH %s in %s@0x%08x %08x %d", name, si->name,
	 * si->base, hash, hash % si->nbucket);
	 * 
	 * for (unsigned n = si->bucket[hash % si->nbucket]; n != 0; n =
	 * si->chain[n]) { Elf32_Sym* s = symtab + n; if (strcmp(strtab +
	 * s->st_name, name)) continue;
	 * 
	 * 
	 * switch(ELF32_ST_BIND(s->st_info)){ case STB_GLOBAL: case STB_WEAK: if
	 * (s->st_shndx == SHN_UNDEF) { continue; }
	 * 
	 * TRACE_TYPE(LOOKUP, "FOUND %s in %s (%08x) %d", name, si->name,
	 * s->st_value, s->st_size); return s; } }
	 * 
	 * return NULL; }
	 */
	private static Elf_Sym soinfo_elf_lookup(ELF si, long hash, String name) {

		if (si == null)
			return null;

		int symtab = si.symtab;
		int strtab = si.strtab;

		for (int n = ByteUtil.bytes2Int32(OS.getMainImage().getMemory(),
				(int) (si.bucket + (hash % si.nbucket) * uint32_t), uint32_t,
				si.elf_header.isLittleEndian()); n != 0; n = ByteUtil.bytes2Int32(OS.getMainImage().getMemory(),
						si.chain + n * uint32_t, uint32_t, si.elf_header.isLittleEndian())) {

			Elf_Sym s = Elf_Sym.reinterpret_cast(OS.getMainImage().getMemory(), symtab + (n * Elf_Sym.size()));

			if (!name.equals(ByteUtil.getStringFromMemory(strtab + ByteUtil.bytes2Int32(s.st_name), OS.getMainImage())))
				continue;
			else {

				switch (ELF_ST_BIND(s.st_info)) {
				case STB_LOCAL:
					Log.e("  Found  " + name + " , but it's local");
					break;

				case STB_GLOBAL:
				case STB_WEAK:
					if (ByteUtil.bytes2Int32(s.st_shndx) == SHN_UNDEF)
						continue;

					return s;

				default:
					Log.e("Unknown Bind Type : " + ByteUtil.byte2Hex(s.st_info));
				}
			}
		}
		return null;
	}

	private static Elf_Sym find_sym_by_name(ELF si, String name, ELF[] lsi, ELF[] needed) {

		if (si.is_gnu_hash)
			return gnu_lookup(si, name, lsi, needed);
		else
			return elf_lookup(si, name, lsi, needed);
	}

	private static Elf_Sym gnu_lookup(ELF si, String name, ELF[] lsi, ELF[] needed) {
		return null;
	}

	private static Elf_Sym elf_lookup(ELF si, String name, ELF[] lsi, ELF[] needed) {
		long elf_hash = elf_hash(name);
		Elf_Sym s = null;

		do {

			if (si != null/* && somain != null */) {// somain is null

				if (si == somain) {
					s = soinfo_elf_lookup(si, elf_hash, name);
					if (s != null) {
						lsi[0] = si;
						break;
					}
				} else {

					if (!si.hasDT_SYMBOLIC) {
						s = soinfo_elf_lookup(somain, elf_hash, name);
						if (s != null) {
							lsi[0] = somain;
							break;
						}
					} // did not find sym in somain

					s = soinfo_elf_lookup(si, elf_hash, name);
					if (s != null) {
						lsi[0] = si;
						break;
					}

					if (si.hasDT_SYMBOLIC) {
						s = soinfo_elf_lookup(somain, elf_hash, name);
						if (s != null) {
							lsi[0] = somain;
							break;
						}
					} // did not find sym in somain
				}

				// ignore preload

				for (int i = 0; i < needed.length; i++) {

					s = soinfo_elf_lookup(needed[i], elf_hash, name);
					if (s != null) {
						lsi[0] = needed[i];
						break;
					}
				}
			}

		} while (false);
		return s;
	}

	private static long gnu_hash(String name) {
		long h = 5381;
		char[] array = name.toCharArray();

		for (int i = 0; i < array.length; i++) {
			h += (h << 5) + (byte) array[i];
		}

		return h & 0xffffffffL;
	}

	public static void main(String[] args) {

		long i = 0xffffffff;

		System.out.println(i);

		// try {
		//
		// ELF elf = new
		// ELF("C:\\Users\\monitor\\Desktop\\hw_lib\\test\\system\\lib\\libgui.so",
		// true);
		//
		// } catch (Exception e) {
		//
		// e.printStackTrace();
		// }
		//
		// try {
		// OS.getMainImage().dumpMemory(new
		// PrintStream("C:\\Users\\monitor\\Desktop\\dump.txt"));
		// } catch (Exception e) {
		// // TODO: handle exception
		// }

	}

}
