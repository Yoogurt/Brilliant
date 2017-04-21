package com.marik.elf;

import static com.marik.elf.ELFDefinition.ELF_R_SYM;
import static com.marik.elf.ELF_Constant.DT_RelType.*;
import static com.marik.elf.ELFDefinition.ELF_R_TYPE;
import static com.marik.elf.ELFDefinition.ELF_ST_BIND;
import static com.marik.elf.ELF_Constant.ELFUnit.ELF32_Addr;
import static com.marik.elf.ELF_Constant.ELFUnit.uint32_t;
import static com.marik.elf.ELF_Constant.SHN_Info.SHN_UNDEF;
import static com.marik.elf.ELF_Constant.SHT_Info.STB_GLOBAL;
import static com.marik.elf.ELF_Constant.SHT_Info.STB_LOCAL;
import static com.marik.elf.ELF_Constant.SHT_Info.STB_WEAK;

import static com.marik.vm.OS.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.activation.UnsupportedDataTypeException;

import org.w3c.dom.ls.LSInput;

import com.marik.elf.ELF_Dynamic.Elf_Sym;
import com.marik.elf.ELF_ProgramHeader.ELF_Phdr;
import com.marik.elf.ELF_Relocate.Elf_rel;
import com.marik.elf.ELF_SectionHeader.ELF_Shdr;
import com.marik.util.Log;
import com.marik.util.Util;
import com.marik.vm.OS;

/**
 * Construct a new Elf decoder which only support arm/32bit
 * 
 * @author lingb
 *
 */
@SuppressWarnings("unused")
public class ELF {

	public static final String[] ENV = { "C:\\Users\\monitor\\Desktop\\env\\" };

	public static final int LDPATH_BUFSIZE = 512;
	public static final int LDPATH_MAX = 8;

	public static final int LDPRELOAD_BUFSIZE = 512;
	public static final int LDPRELOAD_MAX = 8;

	public static final int FLAG_LINKED = 2;

	private static final String[] SYS_CALL_STR = { "__cxa_atexit", "__cxa_finalize", "__gnu_Unwind_Find_exidx", "abort",
			"memcpy", "__cxa_begin_cleanup", "__cxa_type_match" };
	private static final Set<String> SYS_CALL;

	static {
		SYS_CALL = new TreeSet<>();
		for (String name : SYS_CALL_STR) //
			SYS_CALL.add(name);
	}

	/**
	 * Global Share Object
	 */
	private static final Map<String, ELF> gso = new LinkedHashMap<>();

	/**
	 * Global Offset Table
	 */
	private static final Map<String, Integer> GOT = new LinkedHashMap<>();

	private static final ELF[] gLdPreloads = new ELF[LDPRELOAD_MAX + 1];

	private static final ELF somain;

	/* preload */
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

			ELF elf = new ELF(file);
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

	private ELF(String file) throws Exception {
		this(new File(file));
		System.out.println("\n" + name + " loaded ! base : " + Integer.toHexString(elf_base) + " to : "
				+ Integer.toHexString(elf_base + elf_size) + "  load_bias : " + Integer.toHexString(elf_load_bias)
				+ "\n\n\n");
	}

	public static int dlsym(ELF elf, String functionName) {

		if (!elf.mEnable)
			return 0;

		if (elf == null) {
			/* linear search here */
		} else {

			Elf_Sym sym = soinfo_elf_lookup(elf, elf_hash(functionName), functionName);

			if (sym != null)
				if (ELF_ST_BIND(sym.st_info) == STB_GLOBAL && Util.bytes2Int32(sym.st_shndx) != 0)
					return Util.bytes2Int32(sym.st_value) + elf.elf_load_bias;
		}
		return 0;
	}

	public static void dlcolse(ELF elf) {

		unmmap(elf.elf_base, elf.elf_size);
		elf.mEnable = false;

	}

	private ELF(File file) throws Exception {

		RandomAccessFile raf = new RandomAccessFile(file, "r");
		name = file.getName();

		try {
			findLibrary(raf);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	private void findLibrary(RandomAccessFile raf) throws Exception {

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
		reserveAddressSpace();
		loadSegments(raf);

		elf_dynamic = new ELF_Dynamic(raf, elf_phdr.getDynamicSegment());
		link_image();

		callConstructors();
		callArrays();
	}

	private void reserveAddressSpace() {

		List<ELF_Phdr> allLoadableSegment = elf_phdr.getAllLoadableSegment();

		ReserveLoadableSegment r = phdr_table_get_load_size(allLoadableSegment);

		elf_base = mmap(0, (int) (r.max_address - r.min_address), (byte) 0, null, 0);
		if (elf_base < 0)
			throw new RuntimeException("mmap fail while reservse space");

		elf_load_bias = (int) (elf_base
				- r.min_address); /*
									 * r.min_address is supposed to be 0 , in
									 * face , it is
									 */
		elf_size = (int) (r.max_address + elf_load_bias - elf_base);
	}

	private ReserveLoadableSegment phdr_table_get_load_size(List<ELF_Phdr> loadableSegment) {

		int minAddress = Integer.MAX_VALUE;
		int maxAddress = 0;

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

			int address = Util.bytes2Int32(phdr.p_vaddr);
			int memsize = Util.bytes2Int32(phdr.p_memsz);
			if (address < minAddress)
				minAddress = address;

			if (address + memsize > maxAddress)
				maxAddress = address + memsize;

		}

		ReserveLoadableSegment r = new ReserveLoadableSegment();
		r.min_address = PAGE_START(minAddress);
		r.max_address = PAGE_END(maxAddress);

		if (minAddress > maxAddress || maxAddress < 0 || minAddress < 0)
			throw new RuntimeException("can not parse phdr address");

		return r;
	}

	private void loadSegments(RandomAccessFile raf) {

		List<ELF_Phdr> phs = elf_phdr.getAllLoadableSegment();
		for (ELF_Phdr ph : phs) {

			MapEntry m = new MapEntry();

			m.seg_start = Util.bytes2Int64(ph.p_vaddr) + elf_load_bias;
			m.seg_end = m.seg_start + Util.bytes2Int64(ph.p_memsz);

			m.seg_page_start = PAGE_START(m.seg_start);
			m.seg_page_end = PAGE_END(m.seg_end);

			m.seg_file_end = m.seg_start + Util.bytes2Int64(ph.p_filesz);

			// 文件偏移
			m.file_start = Util.bytes2Int64(ph.p_offset);
			m.file_end = m.file_start + Util.bytes2Int64(ph.p_filesz);

			m.file_page_start = PAGE_START(m.file_start);
			m.file_length = m.file_end - m.file_page_start;

			if (0 > mmap((int) m.seg_page_start, (int) m.file_length, MAP_FIXED, raf, m.file_page_start))
				throw new RuntimeException("Unable to mmap segment : " + ph.toString());

			MAP.put(ph, m);
		}

		/*
		 * zero full the remain space , in java it generate automatic and we
		 * don't check elf_phdr is in memory or not
		 */

	}

	public void link_image() {

		/* extract useful informations */
		nbucket = Util.bytes2Int32(OS.getMemory(), elf_dynamic.getDT_HASH() + elf_base + uint32_t * 0, uint32_t,
				elf_header.isLittleEndian()); // value
		nchain = Util.bytes2Int32(OS.getMemory(), elf_dynamic.getDT_HASH() + elf_base + uint32_t * 1, uint32_t,
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
				throw new RuntimeException("Unable to load depend library " + name);

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

		if (ELF_R_TYPE(rel.r_info) == R_ARM_RELATIVE || ELF_R_TYPE(rel.r_info) == R_ARM_IRELATIVE
				|| ELF_R_TYPE(rel.r_info) == R_ARM_ABS32 || ELF_R_TYPE(rel.r_info) == R_ARM_REL32)
			return Util.bytes2Int32(OS.getMemory(), reloc_addr, ELF32_Addr, elf_header.isLittleEndian()); // Extract
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
	/**
	 * 
	 */
	/**
	 * 
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
				int reloc = Util.bytes2Int32(rel.r_offset) + elf_load_bias;
				int sym = ELF_R_SYM(rel.r_info);
				int type = ELF_R_TYPE(rel.r_info);
				int addend = get_addend(rel, reloc);

				String sym_name = Util.getStringFromMemory(Util.bytes2Int32(
						Elf_Sym.reinterpret_cast(OS.getMemory(), symtab + Elf_Sym.size() * sym).st_name) + strtab);

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
					if (null == (s = soinfo_do_lookup(this, sym_name, lsi, needed))) {

						s = Elf_Sym.reinterpret_cast(OS.getMemory(), symtab + sym * Elf_Sym.size());

						/*
						 * we only allow STB_WEAK which is compiling with key
						 * word "extern" when we can't found symbol
						 * 
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
							throw new RuntimeException("unknown weak reloc type " + Util.bytes2Hex(rel.r_info));
						}
						/*
						 * sym_address == 0 , we don't care STB_WEAK Symbol's
						 * address
						 * 
						 */
						Log.e(" Got a STB_WEAK Reference : " + sym_name);

					} else {// we got a definition

						sym_address = lsi[0].elf_load_bias + Util.bytes2Int32(s.st_value);

						Log.e("Found Sym : " + sym_name + " at : " + Integer.toHexString(sym_address));
					}

				} else
					s = null;

				/*---------------------------------------------------------------------------------------*/

				switch (type) {
				case R_ARM_GLOB_DAT:

					System.arraycopy(Util.int2bytes(sym_address), 0, OS.getMemory(), reloc,
							ELF32_Addr);/*
										 * reinterpret_cast<Elf32_Addr*>(reloc)
										 * = sym_addr;
										 */

					Log.e("name : " + sym_name + " R_GENERIC_GLB_DAT at " + Util.bytes2Hex(Util.int2bytes(reloc))
							+ " , relocating to 0x" + Integer.toHexString(sym_address));
					break;
				case R_ARM_JUMP_SLOT:

					System.arraycopy(Util.int2bytes(sym_address), 0, OS.getMemory(), reloc, ELF32_Addr);

					Log.e("name : " + sym_name + " R_GENERIC_JUMP_SLOT at " + Util.bytes2Hex(Util.int2bytes(reloc))
							+ " , relocating to 0x" + Integer.toHexString(sym_address));
					break;

				case R_ARM_ABS32:
					System.arraycopy(Util.int2bytes(sym_address + addend), 0, OS.getMemory(), reloc, ELF32_Addr);

					Log.e("name : " + sym_name + " R_ARM_ABS32 at " + Util.bytes2Hex(Util.int2bytes(reloc))
							+ " , relocating to 0x" + Integer.toHexString(sym_address + addend));
					break;

				case R_ARM_REL32:
					System.arraycopy(Util.int2bytes(addend + sym_address - Util.bytes2Int32(rel.r_offset)), 0,
							OS.getMemory(), reloc, ELF32_Addr);

					Log.e("name : " + sym_name + " R_ARM_REL32 at " + Util.bytes2Hex(Util.int2bytes(reloc))
							+ " , relocating to 0x"
							+ Integer.toHexString(addend + sym_address - Util.bytes2Int32(rel.r_offset)));
					break;
				case R_ARM_RELATIVE: { // *reinterpret_cast<ElfW(Addr)*>(reloc)
										// = (load_bias + addend);

					System.arraycopy(Util.int2bytes(elf_load_bias + addend), 0, OS.getMemory(), reloc, ELF32_Addr);
					/* relocate here*/
					Log.e("local sym : " + sym_name + " reloc : " + Util.bytes2Hex(OS.getMemory(), reloc, 4)
							+ "  become : " + Integer.toHexString(elf_load_bias + addend));

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
		Log.e("Calling -> " + Util.bytes2Hex(Util.int2bytes(func)));
	}

	private void callConstructors() {

		if (hasInitFunc) {
			Log.e("Calling Constructor : " + Util.bytes2Hex(Util.int2bytes(init_func)));
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

		for (int n = Util.bytes2Int32(OS.getMemory(), (int) (si.bucket + (hash % si.nbucket) * uint32_t), uint32_t,
				si.elf_header.isLittleEndian()); n != 0; n = Util.bytes2Int32(OS.getMemory(), si.chain + n * uint32_t,
						uint32_t, si.elf_header.isLittleEndian())) {

			Elf_Sym s = Elf_Sym.reinterpret_cast(OS.getMemory(), symtab + (n * Elf_Sym.size()));

			if (!name.equals(Util.getStringFromMemory(strtab + Util.bytes2Int32(s.st_name))))
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
					if (Util.bytes2Int32(s.st_shndx) == SHN_UNDEF)
						continue;

					return s;

				default:
					Log.e("Unknown Bind Type : " + Util.byte2Hex(s.st_info));
				}
			}
		}
		return null;
	}

	public static Elf_Sym soinfo_do_lookup(ELF si, String name, ELF[] lsi, ELF[] needed) {

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

		/*
		 * for (int i = 0; needed[i] != NULL; i++) {
		 * DEBUG("%s: looking up %s in %s", si->name, name, needed[i]->name); s
		 * = soinfo_elf_lookup(needed[i], elf_hash, name); if (s != NULL) { lsi
		 * = needed[i]; goto done; } }
		 */

		return s;
	}

	private boolean isSystemCall(String name) {
		return SYS_CALL.contains(name);
	}

	public static void main(String[] args) throws FileNotFoundException {
		ELF elf = ELF.dlopen("C:\\Users\\monitor\\Desktop\\Decomplied File\\crackme\\lib\\armeabi\\libdata.so");

		String name = "Jni_OnLoad";

		System.out.println("  --  search  " + name + " at : " + ELF.dlsym(elf, name));

		// ELF elf = ELF.dlopen("C:\\Users\\monitor\\Desktop\\env\\libc.so");
		// String name = "__cxa_atexit";
		// Elf_Sym s = soinfo_elf_lookup(elf, elf_hash(name), name);
		// if (s == null)
		// System.out.println("unable to find " + name);
		// else
		// System.out.println("found " + name);
		//
		// System.out.println(Util.byte2Hex(s.st_info));
		// System.out.println(Util.bytes2Hex(s.st_shndx));
	}

}
