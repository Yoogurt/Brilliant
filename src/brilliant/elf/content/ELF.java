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
import static brilliant.elf.content.ELF_Constant.ST_TYPE.STT_FILE;
import static brilliant.elf.content.ELF_Constant.ST_TYPE.STT_FUNC;
import static brilliant.elf.content.ELF_Constant.ST_TYPE.STT_HIPROC;
import static brilliant.elf.content.ELF_Constant.ST_TYPE.STT_LOPROC;
import static brilliant.elf.content.ELF_Constant.ST_TYPE.STT_NOTYPE;
import static brilliant.elf.content.ELF_Constant.ST_TYPE.STT_OBJECT;
import static brilliant.elf.content.ELF_Constant.ST_TYPE.STT_SECTION;
import static brilliant.elf.content.ELF_Definition.ELF_R_SYM;
import static brilliant.elf.content.ELF_Definition.ELF_R_TYPE;
import static brilliant.elf.content.ELF_Definition.ELF_ST_BIND;
import static brilliant.elf.content.ELF_Definition.ELF_ST_TYPE;
import static brilliant.elf.vm.OS.MAP_FIXED;
import static brilliant.elf.vm.OS.PAGE_END;
import static brilliant.elf.vm.OS.PAGE_START;
import static brilliant.elf.vm.OS.getMemory;
import static brilliant.elf.vm.OS.mmap;
import static brilliant.elf.vm.OS.unmmap;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.activation.UnsupportedDataTypeException;

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

	public static final int LDPATH_BUFSIZE = 512;
	public static final int LDPATH_MAX = 8;

	public static final int LDPRELOAD_BUFSIZE = 512;
	public static final int LDPRELOAD_MAX = 8;

	public static final int FLAG_LINKED = 2;

	private static final String[] SYS_CALL_STR = { "__cxa_atexit",
			"__cxa_finalize", "__gnu_Unwind_Find_exidx", "abort", "memcpy",
			"__cxa_begin_cleanup", "__cxa_type_match" };
	private static final Set<String> SYS_CALL;

	static {
		SYS_CALL = new TreeSet<>();
		for (String name : SYS_CALL_STR)
			//
			SYS_CALL.add(name);
	}

	/**
	 * Global Share Object
	 */
	private static final Map<String, ELF> gso = new LinkedHashMap<>();

	private static final ELF[] gLdPreloads = new ELF[LDPRELOAD_MAX + 1];

	private static final ELF somain;

	/* main executable file , in android usually is app_process , ignore */
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

	private Map<ELF_Phdr, MapEntry> MAP = new HashMap<>();

	private boolean mEnable = false;
	private int mFlag = 0;

	private String name;

	private ELF_Header elf_header;
	private ELF_ProgramHeader elf_phdr;
	private ELF_Dynamic elf_dynamic;

	private ELF[] needed;

	private int nbucket;
	private int nchain;
	private int bucket;
	private int chain;

	private int symtab;
	private int symsz;

	private int strtab;

	private int init_func;
	private boolean hasInitFunc = false;

	private int init_array;
	private int init_array_sz;
	private boolean hasInitArray = false;

	private int fini_func;
	private boolean hasFiniFunc = false;

	private int fini_array;
	private int fini_array_sz;
	private boolean hasFiniArray = false;

	private int elf_base;
	private int elf_load_bias;
	private int elf_size;

	private boolean hasDT_SYMBOLIC;

	public ELF_Header getElf_header() {
		return elf_header;
	}

	public ELF_ProgramHeader getElf_phdr() {
		return elf_phdr;
	}

	public static ELF dlopen(String file) {

		if (gso.containsKey(file))
			return gso.get(file);

		try {

			ELF elf = new ELF(file, true);
			elf.mEnable = true;

			gso.put(file, elf);
			return elf;

		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	public static final String dlerror() {
		return null;
	}

	private ELF(String file, boolean do_load) throws Exception {
		this(new File(file), do_load);
		System.out.println("\n" + name + " loaded ! base : "
				+ Integer.toHexString(elf_base) + " to : "
				+ Integer.toHexString(elf_base + elf_size) + "  load_bias : "
				+ Integer.toHexString(elf_load_bias) + "\n\n\n");
	}

	public static int dlsym(ELF elf, String functionName) {

		if (!elf.mEnable)
			return 0;

		if (elf == null) {
			/* linear search here , do nothing*/
		} else {

			Elf_Sym sym = soinfo_elf_lookup(elf, elf_hash(functionName),
					functionName);

			if (sym != null)
				if (ELF_ST_BIND(sym.st_info) == STB_GLOBAL
						&& ByteUtil.bytes2Int32(sym.st_shndx) != 0)
					return ByteUtil.bytes2Int32(sym.st_value)
							+ elf.elf_load_bias;
		}
		return 0;
	}

	public static void dlcolse(ELF elf) {

		unmmap(elf.elf_base, elf.elf_size);
		elf.mEnable = false;

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

	private void findLibrary(RandomAccessFile raf, boolean do_load)
			throws Exception {

		elf_header = new ELF_Header(raf);
		/*
		 * big endian will be supported someday rather than now
		 */
		if (!elf_header.isLittleEndian())
			throw new UnsupportedDataTypeException(
					"ELFDecoder don't support big endian architecture");

		/*
		 * 64bit will be supported someday rather than now
		 */
		if (!elf_header.is32Bit())
			throw new UnsupportedDataTypeException(
					"ELFDecoder don't support except 32 bit architecture");

		elf_phdr = new ELF_ProgramHeader(raf, elf_header, false);

		if (!do_load)
			new ELF_SectionHeader(raf, elf_header);
		else {
			reserveAddressSpace();
			loadSegments(raf);
		}

		elf_dynamic = new ELF_Dynamic(raf, elf_phdr.getDynamicSegment());

		if (do_load) {
			link_image();

			callConstructors();
			callArrays();
		}
	}

	private void reserveAddressSpace() throws ELFDecodeException {

		List<ELF_Phdr> allLoadableSegment = elf_phdr.getAllLoadableSegment();

		ReserveLoadableSegment r = phdr_table_get_load_size(allLoadableSegment);

		elf_base = mmap(0, (int) (r.max_address - r.min_address), (byte) 0,
				null, 0);
		if (elf_base < 0)
			throw new RuntimeException("mmap fail while reservse space");

		elf_load_bias = (int) (elf_base - r.min_address); /*
														 * r.min_address is
														 * supposed to be 0 , in
														 * face , it is
														 */
		elf_size = (int) (r.max_address + elf_load_bias - elf_base);
	}

	private ReserveLoadableSegment phdr_table_get_load_size(
			List<ELF_Phdr> loadableSegment) throws ELFDecodeException {

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

			if (0 > mmap((int) m.seg_page_start, (int) m.file_length,
					MAP_FIXED, raf, m.file_page_start))
				throw new RuntimeException("Unable to mmap segment : "
						+ ph.toString());

			MAP.put(ph, m);
		}

		/*
		 * zero full the remain space , in java it generate automatic and we
		 * don't check elf_phdr is in memory or not
		 */

	}

	public void link_image() {

		/* extract some useful informations */
		nbucket = ByteUtil.bytes2Int32(OS.getMemory(), elf_dynamic.getDT_HASH()
				+ elf_base + uint32_t * 0, uint32_t,
				elf_header.isLittleEndian()); // value
		nchain = ByteUtil.bytes2Int32(OS.getMemory(), elf_dynamic.getDT_HASH()
				+ elf_base + uint32_t * 1, uint32_t,
				elf_header.isLittleEndian()); // value
		bucket = elf_dynamic.getDT_HASH() + elf_base + 8; // pointer
		chain = elf_dynamic.getDT_HASH() + elf_base + 8 + (nbucket << 2); // pointer

		symtab = elf_dynamic.getDT_SYMTAB() + elf_base; // pointer

		strtab = elf_dynamic.getDT_STRTAB() + elf_base; // pointer

		hasDT_SYMBOLIC = elf_dynamic.getDT_SYMBOLIC();

		if (elf_dynamic.getDT_INIT() != 0) {
			init_func = elf_dynamic.getDT_INIT() + elf_base; // pointer
			hasInitFunc = true;
		}

		if (elf_dynamic.getDT_INIT_ARRAY() != 0) {
			init_array = elf_dynamic.getDT_INIT_ARRAY() + elf_base; // pointer
			init_array_sz = elf_dynamic.getDT_INIT_ARRAYSZ() / ELF32_Addr;
			hasInitArray = true;
		}

		if (elf_dynamic.getDT_FINI() != 0) {
			fini_func = elf_dynamic.getDT_FINI() + elf_base; // pointer
			hasFiniFunc = true;
		}
		if (elf_dynamic.getDT_FINI_ARRAY() != 0) {
			fini_array = elf_dynamic.getDT_FINI_ARRAY() + elf_base;// pointer
			fini_array_sz = elf_dynamic.getDT_FINI_ARRAYSZ() / ELF32_Addr;
			hasFiniArray = false;
		}

		/* verify some parameters */
		if (nbucket == 0)
			throw new RuntimeException("empty/missing DT_HASH");
		if (strtab == 0)
			throw new RuntimeException("empty/missing DT_STRTAB");
		if (symtab == 0)
			throw new RuntimeException("empty/missing DT_SYMTAB");

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
				throw new RuntimeException("Unable to load depend library "
						+ name);

			this.needed[count++] = elf;
		}
		soinfo_relocate();
	}

	/**
	 * 
	 * It appears above Android 5.0
	 * 
	 */
	public int get_addend(Elf_rel rel, int reloc_addr) {

		if (ELF_R_TYPE(rel.r_info) == R_ARM_RELATIVE
				|| ELF_R_TYPE(rel.r_info) == R_ARM_IRELATIVE
				|| ELF_R_TYPE(rel.r_info) == R_ARM_ABS32
				|| ELF_R_TYPE(rel.r_info) == R_ARM_REL32)
			return ByteUtil.bytes2Int32(OS.getMemory(), reloc_addr, ELF32_Addr,
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

		int sym_addr = 0;

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

				String sym_name = ByteUtil.getStringFromMemory(ByteUtil
						.bytes2Int32(Elf_Sym.reinterpret_cast(OS.getMemory(),
								symtab + Elf_Sym.size() * sym).st_name)
						+ strtab);

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
					if (null == (s = soinfo_do_lookup(this, sym_name, lsi,
							needed))) {

						s = Elf_Sym.reinterpret_cast(OS.getMemory(), symtab
								+ sym * Elf_Sym.size());

						/*
						 * we only allow STB_WEAK which is compiling with key
						 * word "extern" when we can't found symbol
						 */

						if (ELF_ST_BIND(s.st_info) != STB_WEAK)
							throw new RuntimeException(
									"cannot locate symbol \""
											+ sym_name
											+ "\" referenced by \""
											+ name
											+ "\"... s.st_info "
											+ Integer
													.toHexString(ELF_ST_BIND(s.st_info))
											+ " , s at "
											+ Integer.toHexString(symtab + sym
													* Elf_Sym.size()));

						switch (type) {

						case R_ARM_JUMP_SLOT:
						case R_ARM_GLOB_DAT:
						case R_ARM_ABS32:
						case R_ARM_RELATIVE:
							break;

						case R_ARM_COPY:
						default:
							throw new RuntimeException(
									"unknown weak reloc type "
											+ ByteUtil.bytes2Hex(rel.r_info));
						}
						/*
						 * sym_address == 0 , we don't care STB_WEAK Symbol's
						 * address
						 */
						Log.e("Got a STB_WEAK Reference : " + sym_name);

					} else {// we got a definition

						sym_address = lsi[0].elf_load_bias
								+ ByteUtil.bytes2Int32(s.st_value);

						Log.e("Found Sym : " + sym_name + " at : "
								+ Integer.toHexString(sym_address));
					}

				} else
					s = null;

				/*---------------------------------------------------------------------------------------*/

				switch (type) {
				case R_ARM_GLOB_DAT:

					Log.e("name : "
							+ sym_name
							+ " R_GENERIC_GLB_DAT at "
							+ ByteUtil.bytes2Hex(ByteUtil.int2bytes(reloc))
							+ " , relocating to 0x"
							+ Integer.toHexString(sym_address)
							+ " , previours values : "
							+ ByteUtil.bytes2Int32(getMemory(), reloc,
									ELF32_Addr, true));

					System.arraycopy(ByteUtil.int2bytes(sym_address), 0,
							OS.getMemory(), reloc, ELF32_Addr);/*
																 * reinterpret_cast<
																 * Elf32_Addr
																 * *>(reloc) =
																 * sym_addr;
																 */
					break;
				case R_ARM_JUMP_SLOT:

					Log.e("name : "
							+ sym_name
							+ " R_GENERIC_JUMP_SLOT at "
							+ ByteUtil.bytes2Hex(ByteUtil.int2bytes(reloc))
							+ " , relocating to 0x"
							+ Integer.toHexString(sym_address)
							+ " , previours values : 0x"
							+ Integer.toHexString(ByteUtil.bytes2Int32(
									getMemory(), reloc, ELF32_Addr, true)));

					System.arraycopy(ByteUtil.int2bytes(sym_address), 0,
							OS.getMemory(), reloc, ELF32_Addr);

					break;

				case R_ARM_ABS32:
					Log.e("name : "
							+ sym_name
							+ " R_ARM_ABS32 at "
							+ ByteUtil.bytes2Hex(ByteUtil.int2bytes(reloc))
							+ " , relocating to 0x"
							+ Integer.toHexString(sym_address + addend)
							+ " , previours values : 0x"
							+ Integer.toHexString(ByteUtil.bytes2Int32(
									getMemory(), reloc, ELF32_Addr, true)));
					System.arraycopy(ByteUtil.int2bytes(sym_address + addend),
							0, OS.getMemory(), reloc, ELF32_Addr);

					break;

				case R_ARM_REL32:
					Log.e("name : "
							+ sym_name
							+ " R_ARM_REL32 at "
							+ ByteUtil.bytes2Hex(ByteUtil.int2bytes(reloc))
							+ " , relocating to 0x"
							+ Integer.toHexString(addend + sym_address
									- ByteUtil.bytes2Int32(rel.r_offset))
							+ " , previours values : "
							+ Integer.toHexString(ByteUtil.bytes2Int32(
									getMemory(), reloc, ELF32_Addr, true)));
					System.arraycopy(
							ByteUtil.int2bytes(addend + sym_address
									- ByteUtil.bytes2Int32(rel.r_offset)), 0,
							OS.getMemory(), reloc, ELF32_Addr);

					break;
				case R_ARM_RELATIVE: { // *reinterpret_cast<ElfW(Addr)*>(reloc)
										// = (load_bias + addend);
					Log.e("local sym : "
							+ sym_name
							+ " reloc : "
							+ ByteUtil.bytes2Hex(OS.getMemory(), reloc, 4)
							+ "  become : "
							+ Integer.toHexString(elf_load_bias + addend)
							+ " , previours values : "
							+ Integer.toHexString(ByteUtil.bytes2Int32(
									getMemory(), reloc, ELF32_Addr, true)));

					System.arraycopy(
							ByteUtil.int2bytes(elf_load_bias + addend), 0,
							OS.getMemory(), reloc, ELF32_Addr);
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
			Log.e("Calling Constructor : "
					+ ByteUtil.bytes2Hex(ByteUtil.int2bytes(init_func)));
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
			h ^= g >> 24;
		}
		
		return h;
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

		for (int n = ByteUtil.bytes2Int32(OS.getMemory(),
				(int) (si.bucket + (hash % si.nbucket) * uint32_t), uint32_t,
				si.elf_header.isLittleEndian()); n != 0; n = ByteUtil
				.bytes2Int32(OS.getMemory(), si.chain + n * uint32_t, uint32_t,
						si.elf_header.isLittleEndian())) {

			Elf_Sym s = Elf_Sym.reinterpret_cast(OS.getMemory(), symtab
					+ (n * Elf_Sym.size()));

			if (!name.equals(ByteUtil.getStringFromMemory(strtab
					+ ByteUtil.bytes2Int32(s.st_name))))
				// Log.e("finding name : "
				// + name
				// + " but we found : "
				// + Util.getStringFromMemory(strtab
				// + Util.bytes2Int32(s.st_name)) + " , continue ...");
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

	public static Elf_Sym soinfo_do_lookup(ELF si, String name, ELF[] lsi,
			ELF[] needed) {

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

	private void dumpHashSymtab() {
		/*
		 * for (int n = ByteUtil.bytes2Int32(OS.getMemory(), (int) (si.bucket +
		 * (hash % si.nbucket) * uint32_t), uint32_t,
		 * si.elf_header.isLittleEndian()); n != 0; n =
		 * ByteUtil.bytes2Int32(OS.getMemory(), si.chain + n * uint32_t,
		 * uint32_t, si.elf_header.isLittleEndian()))
		 */

		int i = 0;

		for (int hash = 0; hash < nbucket; hash++) {
			for (int n = ByteUtil.bytes2Int32(OS.getMemory(),
					(int) (bucket + hash * uint32_t), uint32_t, true); n != 0; n = ByteUtil
					.bytes2Int32(OS.getMemory(), chain + n * uint32_t,
							uint32_t, elf_header.isLittleEndian())) {

				Elf_Sym s = Elf_Sym.reinterpret_cast(OS.getMemory(), symtab
						+ (n * Elf_Sym.size()));

				String symname = ByteUtil.getStringFromMemory(strtab
						+ ByteUtil.bytes2Int32(s.st_name));
				String value = ByteUtil.bytes2Hex(s.st_value);

				int size = ByteUtil.bytes2Int32(s.st_size);
				int shndx = ByteUtil.bytes2Int32(s.st_shndx);

				String bind = null;
				String type = null;
				int other = ByteUtil.byte2Int32(s.st_other);

				switch (ELF_ST_BIND(s.st_info)) {
				case STB_LOCAL:
					bind = "STB_LOCAL";
					break;
				case STB_GLOBAL:
					bind = "STB_GLOBAL";
					break;
				case STB_WEAK:
					bind = "STB_WEAK";
					break;
				}
				switch (ELF_ST_TYPE(s.st_info)) {
				case STT_NOTYPE:
					type = "STT_NOTYPE";
					break;
				case STT_OBJECT:
					type = "STT_OBJECT";
					break;
				case STT_FUNC:
					type = "STT_FUNC";
					break;
				case STT_SECTION:
					type = "STT_SECTION";
					break;
				case STT_FILE:
					type = "STT_FILE";
					break;
				case STT_LOPROC:
					type = "STT_LOPROC";
					break;
				case STT_HIPROC:
					type = "STT_HIPROC";
					break;
				}
				System.out
						.printf("symbol name : %-30s  value : %-12s  size : %-8d  BIND : %-10s  TYPE : %-10s  OTHER : %-5d  SHNDX : %-8d\n",
								symname, value, size, bind, type, other, shndx);
				i++;
			}

			Log.e("--------------------------hash change ------------------------------");

		}

		System.out.println("total sym : " + i);
	}

	// private void dumpSymtab() {
	// /*
	// * for (int n = ByteUtil.bytes2Int32(OS.getMemory(), (int) (si.bucket +
	// * (hash % si.nbucket) * uint32_t), uint32_t,
	// * si.elf_header.isLittleEndian()); n != 0; n =
	// * ByteUtil.bytes2Int32(OS.getMemory(), si.chain + n * uint32_t,
	// * uint32_t, si.elf_header.isLittleEndian()))
	// */
	// for (int n = 0;; n++) {
	//
	// Elf_Sym s = Elf_Sym.reinterpret_cast(OS.getMemory(), symtab + (n *
	// Elf_Sym.size()));
	//
	// if(ByteUtil.)
	//
	// String symname = ByteUtil.getStringFromMemory(strtab +
	// ByteUtil.bytes2Int32(s.st_name));
	//
	// int size = ByteUtil.bytes2Int32(s.st_size);
	//
	// String bind = null;
	// String type = null;
	// int other = ByteUtil.byte2Int32(s.st_other);
	//
	// switch (ELF_ST_BIND(s.st_info)) {
	// case STB_LOCAL:
	// bind = "STB_LOCAL";
	// break;
	// case STB_GLOBAL:
	// bind = "STB_GLOBAL";
	// break;
	// case STB_WEAK:
	// bind = "STB_WEAK";
	// break;
	// }
	// switch (ELF_ST_TYPE(s.st_info)) {
	// case STT_NOTYPE:
	// type = "STT_NOTYPE";
	// break;
	// case STT_OBJECT:
	// type = "STT_OBJECT";
	// break;
	// case STT_FUNC:
	// type = "STT_FUNC";
	// break;
	// case STT_SECTION:
	// type = "STT_SECTION";
	// break;
	// case STT_FILE:
	// type = "STT_FILE";
	// break;
	// case STT_LOPROC:
	// type = "STT_LOPROC";
	// break;
	// case STT_HIPROC:
	// type = "STT_HIPROC";
	// break;
	// }
	// System.out.printf("symbol name : %-30s size : %-5d BIND : %-9s TYPE :
	// %-10s OTHER : %-5d\n", symname,
	// size, bind, type, other);
	// try {
	// Thread.sleep(1000);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// }
	// }

	private boolean isSystemCall(String name) {
		return SYS_CALL.contains(name);
	}

	public static void main(String[] args) throws Exception {

		OS.debug = true;

		ELF elf = new ELF(
				"C:/Users/Administrator/Desktop/libdvm.so", false);
		// ELF elf = new ELF("C:/Users/monitor/Desktop/env/libc.so", true);
		elf.dumpHashSymtab();

	}

}
