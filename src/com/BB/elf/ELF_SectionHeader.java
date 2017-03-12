package com.BB.elf;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;

import com.BB.util.Log;
import com.BB.util.Util;

import static com.BB.elf.ELFConstant.ELFUnit.*;
import static com.BB.elf.Constant.*;
import static com.BB.elf.ELFConstant.SectionHeaderContent.*;

public class ELF_SectionHeader {

	public class ELF_Shdr {

		/**
		 * name - index into section header string table section
		 */
		byte[] sh_name;

		/**
		 * type
		 */
		byte[] sh_type;

		/**
		 * flags
		 */
		byte[] sh_flags;

		/**
		 * address
		 */
		byte[] sh_addr;

		/**
		 * file offset
		 */
		byte[] sh_offset;

		/**
		 * section size
		 */
		byte[] sh_size;

		/**
		 * section header table index link
		 */
		byte[] sh_link;

		/**
		 * extra information
		 */
		byte[] sh_info;

		/**
		 * address alignment
		 */
		byte[] sh_addralign;

		/**
		 * section entry size
		 */
		byte[] sh_entsize;

		/**
		 * name of this section
		 */
		ELF_Section section;

		/**
		 * 
		 */
		private String name;

		private void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	private ELF_Shdr[] mInternalSectionHeaders;
	private int mStringSectionHeaderIndex;

	public ELF_SectionHeader(RandomAccessFile raf, ELF_Header header)
			throws IOException {

		Log.e(ELF_SECTION_TABLE);

		if (!locateSectionHeaderOffset(raf, header)) {
			Log.e("Section Header not found ,skipping init");
			return;
		}

		if (header.is32Bit())
			readSectionHeader32(raf, header);
		else
			readSectionHeader64(raf, header);

		namedSection();
	}

	private void readSectionHeader32(RandomAccessFile raf, ELF_Header header)
			throws IOException {

		int mSectionHeaderCount = header.getSectionHeaderNum();
		mInternalSectionHeaders = new ELF_Shdr[mSectionHeaderCount];

		mStringSectionHeaderIndex = header.getSectionStringIndex();

		for (int m = 0; m < mSectionHeaderCount; m++) {

			ELF_Shdr mS = genrateSectionHeaderStructure32();
			readSectionHeaderStructure(raf, mS);

			mInternalSectionHeaders[m] = mS;

			decodeSectionHeader32(raf, mS, header);
		}
	}

	private void readSectionHeader64(RandomAccessFile raf, ELF_Header header)
			throws IOException {

		int mSectionHeaderCount = header.getSectionHeaderNum();
		mInternalSectionHeaders = new ELF_Shdr[mSectionHeaderCount];

		for (int m = 0; m < mSectionHeaderCount; m++) {

			ELF_Shdr mS = genrateSectionHeaderStructure64();
			readSectionHeaderStructure(raf, mS);

			mInternalSectionHeaders[m] = mS;

			decodeSectionHeader32(raf, mS, header);
		}
	}

	private ELF_Shdr genrateSectionHeaderStructure32() {

		ELF_Shdr mS = new ELF_Shdr();

		mS.sh_name = new byte[ELF32_Word];

		mS.sh_type = new byte[ELF32_Word];

		mS.sh_flags = new byte[ELF32_Word];

		mS.sh_addr = new byte[ELF32_Addr];

		mS.sh_offset = new byte[ELF32_Off];

		mS.sh_size = new byte[ELF32_Word];

		mS.sh_link = new byte[ELF32_Word];

		mS.sh_info = new byte[ELF32_Word];

		mS.sh_addralign = new byte[ELF32_Word];

		mS.sh_entsize = new byte[ELF32_Word];

		return mS;

	}

	private void decodeSectionHeader32(RandomAccessFile raf, ELF_Shdr mS,
			ELF_Header header) {

		Log.e(DIVISION_LINE);

		switch (Util.bytes2Int32(mS.sh_type, header.isLittleEndian())) {

		case SHT_NULL:/* 0 */
			Log.e("This Section is Unvalueable");
			loadSectionFromSectionHeader(raf, header, mS);
			break;

		case SHT_PROGBITS:/* 1 */
			Log.e("This Section Header is about program defined information , at "
					+ Util.bytes2Hex(mS.sh_offset));
			loadSectionFromSectionHeader(raf, header, mS);
			break;

		case SHT_SYMTAB:/* 2 */
			Log.e("This Section Header is a symbol table section, at "
					+ Util.bytes2Hex(mS.sh_offset));
			loadSectionFromSectionHeader(raf, header, mS);
			break;

		case SHT_STRTAB:/* 3 */
			Log.e("This Section Header is a string table section , at "
					+ Util.bytes2Hex(mS.sh_offset));

			if (mInternalSectionHeaders[mStringSectionHeaderIndex] == mS)
				Log.e("This is a Section contains other Section's Name");

			loadSectionFromSectionHeader(raf, header, mS);
			break;

		case SHT_RELA:/* 4 */
			Log.e("This Section Header is a relocation section with addrnds , at "
					+ Util.bytes2Hex(mS.sh_offset));
			loadSectionFromSectionHeader(raf, header, mS);
			break;

		case SHT_HASH:/* 5 */
			Log.e("This Section Header is about symbol hash table , at "
					+ Util.bytes2Hex(mS.sh_offset));
			loadSectionFromSectionHeader(raf, header, mS);
			break;

		case SHT_DYNAMIC:/* 6 */
			Log.e("This is a dynamic Section Header , at "
					+ Util.bytes2Hex(mS.sh_offset));
			loadSectionFromSectionHeader(raf, header, mS);
			break;

		case SHT_NOTE:/* 7 */
			Log.e("This is a note Section Header , at "
					+ Util.bytes2Hex(mS.sh_offset));
			loadSectionFromSectionHeader(raf, header, mS);
			break;

		case SHT_REL:/* 9 */
			Log.e("This is a relation section without addends , at "
					+ Util.bytes2Hex(mS.sh_offset));
			loadSectionFromSectionHeader(raf, header, mS);
			break;

		case SHT_SHLIB:/* 10 */
			Log.e("This is a reserved section for unknown purpose , at "
					+ Util.bytes2Hex(mS.sh_offset));
			loadSectionFromSectionHeader(raf, header, mS);
			break;

		case SHT_DYMSYM:/* 11 */
			Log.e("This is a dynamic symbol table section , at "
					+ Util.bytes2Hex(mS.sh_offset));
			loadSectionFromSectionHeader(raf, header, mS);
			break;

		case SHT_NUM:/* 12 */
			Log.e("This is the number of types Section Header , at "
					+ Util.bytes2Hex(mS.sh_offset));
			loadSectionFromSectionHeader(raf, header, mS);
			break;

		case SHT_NOBITS:/* 8 */
			Log.e("This is a not space Section Header");
			break;
		default:
			Log.e("Unknown Section Header Type ! at "
					+ Util.bytes2Hex(mS.sh_offset));
			break;
		}

	}

	private void readSectionHeaderStructure(RandomAccessFile raf, ELF_Shdr mS)
			throws IOException {

		raf.read(mS.sh_name);
		raf.read(mS.sh_type);
		raf.read(mS.sh_flags);
		raf.read(mS.sh_addr);
		raf.read(mS.sh_offset);
		raf.read(mS.sh_size);
		raf.read(mS.sh_link);
		raf.read(mS.sh_info);
		raf.read(mS.sh_addralign);
		raf.read(mS.sh_entsize);

	}

	private ELF_Shdr genrateSectionHeaderStructure64() {

		ELF_Shdr mS = new ELF_Shdr();

		mS.sh_name = new byte[ELF64_Word];

		mS.sh_type = new byte[ELF64_Word];

		mS.sh_flags = new byte[ELF64_Xword];

		mS.sh_addr = new byte[ELF64_Addr];

		mS.sh_offset = new byte[ELF64_Off];

		mS.sh_size = new byte[ELF64_Xword];

		mS.sh_link = new byte[ELF64_Word];

		mS.sh_info = new byte[ELF64_Word];

		mS.sh_addralign = new byte[ELF64_Xword];

		mS.sh_entsize = new byte[ELF64_Xword];

		return mS;

	}

	private void namedSection() {

		ELF_Shdr mStringSection = mInternalSectionHeaders[mStringSectionHeaderIndex];

		for (ELF_Shdr mS : mInternalSectionHeaders)
			mS.setName(mStringSection.section.getStringAtIndex(mS.sh_name));
	}

	private boolean locateSectionHeaderOffset(RandomAccessFile raf,
			ELF_Header header) throws IOException {
		long mSectionOffset = header.getSectionHeaderTableOffset();
		if (mSectionOffset == 0)
			return false;
		raf.seek(mSectionOffset);
		return true;
	}

	private void loadSectionFromSectionHeader(RandomAccessFile raf,
			ELF_Header header, ELF_Shdr mS) {
		try {
			long index = raf.getFilePointer();
			mS.section = new ELF_Section(raf, header, mS);
			raf.seek(index);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to decode Section");
		}
	}

	public ELF_Section getSectionByName(String name) {
		for (ELF_Shdr section_header : mInternalSectionHeaders)
			if (Objects.equals(name, section_header.name))
				return section_header.section;

		return null;
	}

}
