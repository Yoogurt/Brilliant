package com.marik.elf;

import static com.marik.elf.ELFConstant.ELFUnit.ELF32_Addr;
import static com.marik.elf.ELFConstant.ELFUnit.ELF32_Word;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_DEBUG;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_FINI;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_FINI_ARRAY;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_FINI_ARRAYSZ;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_FLAGS;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_FLAGS_1;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_ANDROID_REL;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_ANDROID_RELSZ;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_HASH;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_HIPROC;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_INIT;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_INIT_ARRAY;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_INIT_ARRAYSZ;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_JMPREL;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_LOPROC;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_NEEDED;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_NULL;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_PLTGOT;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_GNU_HASH;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_PLTREL;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_PLTRELSZ;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_REL;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_RELA;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_RELAENT;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_RELASZ;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_RELCOUNT;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_RELENT;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_RELSZ;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_RPATH;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_SONAME;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_STRSZ;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_STRTAB;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_SYMBOLIC;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_SYMENT;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_SYMTAB;
import static com.marik.elf.ELFConstant.PT_Dynamic.DT_TEXTREL;
import static com.marik.elf.ELFConstant.ProgramHeaderContent.PT_DYNAMIC;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.marik.elf.ELF_ProgramHeader.ELF_Phdr;
import com.marik.util.Log;
import com.marik.util.Util;

public class ELF_Dynamic {

	class Elf_Dyn {
		public byte[] d_val;
		public byte[] d_un;
	}

	class Elf_Sym {
		byte[] st_name; /* 名称 - index into string table 4B */
		byte[] st_value; /* 偏移地址 4B */
		byte[] st_size; /* 符号长度（ e.g. 函数的长度） 4B */
		byte st_info; /* 类型和绑定类型 1B */
		byte st_other; /* 未定义 1B */
		byte[] st_shndx; /* section header的索引号，表示位于哪个 section 中 2B */
	}

	private List<Elf_Dyn> mInternalDynamics = new ArrayList<>();

	private int mStrTabIndex = 0;
	private int mSymTabIndex = 0;

	private int mInitFunc = 0;
	private int mInitArray = 0;
	private int mInitArraySz = 0;

	private int mFiniFunc = 0;
	private int mFiniArray = 0;
	private int mFiniArraySz = 0;

	private int mHash = 0;

	private List<String> mNeededDynamicLibrary = new ArrayList<>();
	private ELF_ProgramHeader.ELF_Phdr mSelf;

	private String mDynamicLibraryName;

	private int mRel;
	private int mRelSz;

	private int mRela;
	private int mRelaSz;

	private int mJmpRel;
	private int mJmpSz;

	private List<ELF_Relocate> mRelocateSections = new ArrayList<>();

	/**
	 * we decode this in file nor memory
	 */
	ELF_Dynamic(RandomAccessFile raf, ELF_Phdr mSelf) throws IOException {

		this.mSelf = mSelf;

		if (mSelf.getProgramHeader().getELFHeader().is32Bit())
			loadDynamicSegment32(raf);
		else
			loadDynamicSegment64();

		loadRelocateSection(raf);
	}

	private void loadDynamicSegment32(RandomAccessFile raf) throws IOException {

		if (Util.bytes2Int32(mSelf.p_type) != PT_DYNAMIC)
			throw new IllegalArgumentException("Attempt to decode Dynamic Segment with a not PT_DYNAMIC ProgramHeader");

		int dynamicCount = Util.bytes2Int32(mSelf.p_filesz) / 8;

		long prePosition = raf.getFilePointer();

		raf.seek(Util.bytes2Int64(mSelf.p_offset));

		for (int i = 0; i < dynamicCount; i++) {

			Elf_Dyn dynamic = generateElfDynamicEntry32();

			raf.read(dynamic.d_un);
			raf.read(dynamic.d_val);

			if (!parseDynamicEntry(dynamic, raf)) {
				mInternalDynamics.add(dynamic);
				break;
			}

			mInternalDynamics.add(dynamic);
		}

		Log.e("   " + Constant.DIVISION_LINE);
		Log.e("   " + mInternalDynamics.size() + " DT_DYNAMIC Found");
		raf.seek(prePosition);
	}

	private void loadDynamicSegment64() {
		throw new UnsupportedOperationException("not implements");
	}

	private Elf_Dyn generateElfDynamicEntry32() {
		Elf_Dyn newDynamic = new Elf_Dyn();

		newDynamic.d_un = new byte[ELF32_Word];
		newDynamic.d_val = new byte[ELF32_Addr];

		return newDynamic;
	}

	private boolean parseDynamicEntry(Elf_Dyn dynamic, RandomAccessFile raf) throws IOException {

		switch (Util.bytes2Int32(dynamic.d_un)) {
		case DT_NULL:
			return false;
		case DT_NEEDED: // elf necessary library
			String name = getStrTabIndexString(Util.bytes2Int32(dynamic.d_val), raf);
			storeNeededDynamicLibraryName(name);
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "Need Dynamic Library : " + name);
			break;
		case DT_PLTRELSZ:
			mJmpSz = (int) getVal(dynamic.d_val);
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_PLTRELSZ " + +getVal(dynamic.d_val));
			break;
		case DT_PLTGOT:
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_PLTGOT at " + Util.bytes2Hex(dynamic.d_val));
			break;
		case DT_HASH:
			readDT_HASH(dynamic);
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_HASH at " + Util.bytes2Hex(dynamic.d_val));
			break;
		case DT_STRTAB:
			readDT_STRTAB(dynamic);
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_STRTAB at " + Util.bytes2Hex(dynamic.d_val));
			break;
		case DT_SYMTAB:
			readDT_SYMTAB(dynamic);
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_SYMTAB at " + Util.bytes2Hex(dynamic.d_val));
			break;
		case DT_RELA:
			readDT_RELA(dynamic);
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_RELA at " + Util.bytes2Hex(dynamic.d_val));
			break;
		case DT_RELASZ:
			mRelaSz = getVal(dynamic.d_val);
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_RELASZ : " + +getVal(dynamic.d_val));
			break;
		case DT_RELAENT:
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_RELAENT at " + Util.bytes2Hex(dynamic.d_val));
			break;
		case DT_STRSZ:
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_STRSZ : " + +getVal(dynamic.d_val));
			break;
		case DT_SYMENT:
			assertSYMENT(dynamic);
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_SYMENT : " + +getVal(dynamic.d_val));
			break;
		case DT_INIT:
			readDT_INIT(dynamic);
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_INIT at " + Util.bytes2Hex(dynamic.d_val));
			break;
		case DT_FINI:
			readDT_FINI(dynamic);
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_FINI at " + Util.bytes2Hex(dynamic.d_val));
			break;
		case DT_SONAME:
			mDynamicLibraryName = getStrTabIndexString(Util.bytes2Int32(dynamic.d_val), raf);
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "My Dynamic Library : " + mDynamicLibraryName);
			break;
		case DT_RPATH:
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_RPATH at " + Util.bytes2Hex(dynamic.d_val));
			break;
		case DT_SYMBOLIC:
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_SYMBOLIC at " + Util.bytes2Hex(dynamic.d_val));
			break;
		case DT_REL:
			readDT_REL(dynamic);
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_REL at " + Util.bytes2Hex(dynamic.d_val));
			break;
		case DT_RELSZ:
			mRelSz = (int) getVal(dynamic.d_val);
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_RELSZ : " + getVal(dynamic.d_val));
			break;
		case DT_RELENT:
			assertRELENT(dynamic);
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_RELENT : " + +getVal(dynamic.d_val));
			break;
		case DT_PLTREL:
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_PLTREL at " + Util.bytes2Hex(dynamic.d_val));
			break;
		case DT_DEBUG:
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_DEBUG at " + Util.bytes2Hex(dynamic.d_val));
			break;
		case DT_TEXTREL:
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_TEXTREL at " + Util.bytes2Hex(dynamic.d_val));
			break;
		case DT_JMPREL:
			readDT_JMPREL(dynamic);
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_JMPREL at " + Util.bytes2Hex(dynamic.d_val));
			break;
		case DT_LOPROC:
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_LOPROC at " + Util.bytes2Hex(dynamic.d_val));
			break;
		case DT_HIPROC:
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_HIPROC at " + Util.bytes2Hex(dynamic.d_val));
			break;
		case DT_INIT_ARRAY:
			readDT_INIT_ARRAY(dynamic);
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_INIT_ARRAY at " + Util.bytes2Hex(dynamic.d_val));
			break;
		case DT_FINI_ARRAY:
			readDT_FINI_ARRAY(dynamic);
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_FINI_ARRAY at " + Util.bytes2Hex(dynamic.d_val));
			break;
		case DT_RELCOUNT:
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_RELCOUNT : " + +getVal(dynamic.d_val) + " (ignore)");
			break;
		case DT_FINI_ARRAYSZ:
			readDT_FINI_ARRAYSZ(dynamic);
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_FINI_ARRAYSZ : " + getVal(dynamic.d_val));
			break;
		case DT_INIT_ARRAYSZ:
			readDT_INIT_ARRAYSZ(dynamic);
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_INIT_ARRAYSZ : " + getVal(dynamic.d_val));
			break;
		case DT_FLAGS_1:
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_FLAGS_1 : " + getVal(dynamic.d_val));
			break;
		case DT_FLAGS:
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_FLAGS : " + getVal(dynamic.d_val));
			break;
		case DT_GNU_HASH:
			readDT_GNU_HASH(dynamic);
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_GNU_HASH : " + Util.bytes2Hex(dynamic.d_val));
			break;
		case DT_ANDROID_REL:
			readDT_ANDROID_REL(dynamic);
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_ANDROID_REL : " + Util.bytes2Hex(dynamic.d_val));
			break;
		case DT_ANDROID_RELSZ:
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "DT_ANDROID_RELSZ : " + getVal(dynamic.d_val));
			break;
		default:
			Log.e("   " + Constant.DIVISION_LINE);
			Log.e("   " + "Unknown DT type " + Util.bytes2Hex(dynamic.d_un));
			break;
		}
		return true;
	}

	private void readDT_RELA(Elf_Dyn dynamic) {
		if (mRela == 0)
			mRela = (int) getVal(dynamic.d_val);
		else
			throw new IllegalStateException("DT_RELA appear over once");
	}

	private void readDT_JMPREL(Elf_Dyn dynamic) {
		if (mJmpRel == 0)
			mJmpRel = (int) getVal(dynamic.d_val);
		else
			throw new IllegalStateException("DT_JMPREL appear over once");
	}

	private void readDT_REL(Elf_Dyn dynamic) {
		if (mRel == 0)
			mRel = (int) getVal(dynamic.d_val);
		else
			throw new IllegalStateException("DT_REL appear over once");
	}

	private void readDT_ANDROID_REL(Elf_Dyn dynamic) {
		throw new UnsupportedOperationException("DT_ANDROID_REL no implements");
	}

	private void readDT_GNU_HASH(Elf_Dyn dynamic) {
		throw new UnsupportedOperationException("DT_GNU_HASH no implements");
	}

	private void assertSYMENT(Elf_Dyn dynamic) {
		if (Util.bytes2Int32(dynamic.d_val) != 0x10) // Elf_Sym takes 0x10B
			throw new AssertionError("assert fail , SYMENT != 0x10");
	}

	private void assertRELENT(Elf_Dyn dynamic) {
		if (Util.bytes2Int32(dynamic.d_val) != 0x8) // Elf_Sym takes 0x10B
			throw new AssertionError("assert fail , SYMENT != 0x10");
	}

	private void readDT_HASH(Elf_Dyn dynamic) {
		if (mHash == 0)
			mHash = Util.bytes2Int32(dynamic.d_val);
		else
			throw new IllegalStateException("DT_HASH appear over once");
	}

	private void readDT_INIT_ARRAY(Elf_Dyn dynamic) {
		if (mInitArray == 0)
			mInitArray = Util.bytes2Int32(dynamic.d_val);
		else
			throw new IllegalStateException("mInitArray appear over once");
	}

	private void readDT_INIT(Elf_Dyn dynamic) {
		if (mInitFunc == 0)
			mInitFunc = Util.bytes2Int32(dynamic.d_val);
		else
			throw new IllegalStateException("DT_INIT appear over once");
	}

	private void readDT_SYMTAB(Elf_Dyn dynamic) {
		if (mSymTabIndex == 0)
			mSymTabIndex = Util.bytes2Int32(dynamic.d_val);
		else
			throw new IllegalStateException("DT_SYMTAB appear over once");
	}

	private void readDT_STRTAB(Elf_Dyn dynamic) {
		if (mStrTabIndex == 0)
			mStrTabIndex = Util.bytes2Int32(dynamic.d_val);
		else
			throw new IllegalStateException("DT_STRTAB appear over once");
	}

	private void readDT_FINI(Elf_Dyn dynamic) {
		if (mFiniFunc == 0)
			mFiniFunc = Util.bytes2Int32(dynamic.d_val);
		else
			throw new IllegalStateException("DT_FINI appear over once");
	}

	private void readDT_FINI_ARRAY(Elf_Dyn dynamic) {
		if (mFiniArray == 0)
			mFiniArray = Util.bytes2Int32(dynamic.d_val);
		else
			throw new IllegalStateException("DT_FINI_ARRAY appear over once");
	}

	private void readDT_FINI_ARRAYSZ(Elf_Dyn dynamic) {
		if (mFiniArraySz == 0)
			mFiniArraySz = Util.bytes2Int32(dynamic.d_val);
		else
			throw new IllegalStateException("DT_FINI_ARRAYSZ appear over once");
	}

	private void readDT_INIT_ARRAYSZ(Elf_Dyn dynamic) {
		if (mInitArraySz == 0)
			mInitArraySz = Util.bytes2Int32(dynamic.d_val);
		else
			throw new IllegalStateException("DT_INIT_ARRAYSZ appear over once");
	}

	private String getStrTabIndexString(int index, RandomAccessFile raf) throws IOException {
		if (mStrTabIndex == 0)
			throw new IllegalStateException("Unable to find Library Name");

		long prePosition = raf.getFilePointer();

		raf.seek(index + mStrTabIndex);

		String name = Util.getStringFromBytes(raf);

		raf.seek(prePosition);

		return name;
	}

	String getSymInStrTab(int sym, RandomAccessFile raf) throws IOException {
		if (mStrTabIndex == 0 || mSymTabIndex == 0 || sym <= 0)
			return null;

		long prePointer = raf.getFilePointer();

		byte[] st_name = new byte[4];
		raf.seek(mSymTabIndex + 0x10 * sym); // Elf_sym takes 0x10 B
		raf.read(st_name);

		try {
			return getStrTabIndexString(Util.bytes2Int32(st_name), raf);
		} catch (Exception e) {
			throw new IllegalStateException();
		} finally {
			raf.seek(prePointer);
		}
	}

	public int getDT_INIT() {
		return mInitFunc;
	}

	public int getDT_INIT_ARRAY() {
		return mInitArray;
	}

	public int getDT_INIT_ARRAYSZ() {
		return mInitArraySz;
	}

	public int getDT_FINI() {
		return mFiniFunc;
	}

	public int getDT_FINI_ARRAY() {
		return mFiniArray;
	}

	public int getDT_FINI_ARRAYSZ() {
		return mFiniArraySz;
	}

	public int getDT_STRTAB() {
		return mStrTabIndex;
	}

	public int getDT_SYMTAB() {
		return mSymTabIndex;
	}

	public List<ELF_Relocate> getRelocateSections() {
		return mRelocateSections;
	}

	public int getDT_REL() {
		return mRel;
	}

	public int getDT_PLTREL() {
		return mJmpRel;
	}

	public int getDT_RELSZ() {
		return mRelSz;
	}

	public int getDT_PLTRELSZ() {
		return mJmpSz;
	}

	public int getDT_HASH() {
		return mHash;
	}

	private void storeNeededDynamicLibraryName(String name) {
		mNeededDynamicLibrary.add(name);
	}

	private int getVal(byte[] data) {
		return Util.bytes2Int32(data);
	}

	private void loadRelocateSection(RandomAccessFile raf) throws IOException {
		mRelocateSections.add(new ELF_Relocate(raf, mRel, mRelSz, this, false));
		mRelocateSections.add(new ELF_Relocate(raf, mJmpRel, mJmpSz, this, false));

		if (mRela != 0 && mRelaSz != 0)
			mRelocateSections.add(new ELF_Relocate(raf, mRela, mRelaSz, this, true));
	}
}
