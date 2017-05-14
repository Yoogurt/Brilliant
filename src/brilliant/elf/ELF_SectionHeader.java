package brilliant.elf;

import static brilliant.elf.ELF_Constant.ELFUnit.ELF32_Addr;
import static brilliant.elf.ELF_Constant.ELFUnit.ELF32_Off;
import static brilliant.elf.ELF_Constant.ELFUnit.ELF32_Word;
import static brilliant.elf.ELF_Constant.ELFUnit.ELF64_Addr;
import static brilliant.elf.ELF_Constant.ELFUnit.ELF64_Off;
import static brilliant.elf.ELF_Constant.ELFUnit.ELF64_Word;
import static brilliant.elf.ELF_Constant.ELFUnit.ELF64_Xword;
import static brilliant.elf.ELF_Constant.SectionHeaderContent.SHT_DYMSYM;
import static brilliant.elf.ELF_Constant.SectionHeaderContent.SHT_DYNAMIC;
import static brilliant.elf.ELF_Constant.SectionHeaderContent.SHT_HASH;
import static brilliant.elf.ELF_Constant.SectionHeaderContent.SHT_NOBITS;
import static brilliant.elf.ELF_Constant.SectionHeaderContent.SHT_NOTE;
import static brilliant.elf.ELF_Constant.SectionHeaderContent.SHT_NULL;
import static brilliant.elf.ELF_Constant.SectionHeaderContent.SHT_NUM;
import static brilliant.elf.ELF_Constant.SectionHeaderContent.SHT_PROGBITS;
import static brilliant.elf.ELF_Constant.SectionHeaderContent.SHT_REL;
import static brilliant.elf.ELF_Constant.SectionHeaderContent.SHT_RELA;
import static brilliant.elf.ELF_Constant.SectionHeaderContent.SHT_SHLIB;
import static brilliant.elf.ELF_Constant.SectionHeaderContent.SHT_STRTAB;
import static brilliant.elf.ELF_Constant.SectionHeaderContent.SHT_SYMTAB;
import static brilliant.elf.LogConstant.DIVISION_LINE;
import static brilliant.elf.LogConstant.ELF_SECTION_TABLE;

import java.io.IOException;
import java.io.RandomAccessFile;

import brilliant.util.ByteUtil;
import brilliant.util.Log;

/**
 * @author Yoogurt
 *
 *         SectionHeaders are useless when we parse a dynamic library or
 *         executable. for Debug Only
 */
@Deprecated
class ELF_SectionHeader {

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

		public long getMemoryOffset() {
			return ByteUtil.bytes2Int64(sh_addr);
		}

		public long getMemorySize() {
			return ByteUtil.bytes2Int32(sh_size);
		}

		public long getSectionOffset() {
			return ByteUtil.bytes2Int64(sh_offset);
		}

		public int getSectionSize() {
			return ByteUtil.bytes2Int32(sh_size);
		}
	}

	private ELF_Header header;

	private ELF_Shdr[] mInternalSectionHeaders;
	private int mStringSectionHeaderIndex;

	ELF_SectionHeader(RandomAccessFile raf, ELF_Header header)
			throws IOException {

		Log.e(ELF_SECTION_TABLE);

		this.header = header;

		locateSectionHeaderOffset(raf);

		if (header.is32Bit())
			readSectionHeader32(raf);
		else
			readSectionHeader64(raf);

		namedSection();

		printSectionHeader();

	}

	private void readSectionHeader32(RandomAccessFile raf) throws IOException {

		int mSectionHeaderCount = header.getSectionHeaderNum();
		mInternalSectionHeaders = new ELF_Shdr[mSectionHeaderCount];

		mStringSectionHeaderIndex = header.getSectionStringIndex();

		for (int m = 0; m < mSectionHeaderCount; m++) {

			ELF_Shdr mS = genrateSectionHeaderStructure32();
			readSectionHeaderStructure(raf, mS);

			mInternalSectionHeaders[m] = mS;

			loadSectionFromSectionHeader(raf, mS);

			if (mInternalSectionHeaders[mStringSectionHeaderIndex] == mS)
				Log.e("This is a Section contains other Section's Name");
		}
	}

	private void readSectionHeader64(RandomAccessFile raf) throws IOException {

		int mSectionHeaderCount = header.getSectionHeaderNum();
		mInternalSectionHeaders = new ELF_Shdr[mSectionHeaderCount];

		mStringSectionHeaderIndex = header.getSectionStringIndex();

		for (int m = 0; m < mSectionHeaderCount; m++) {

			ELF_Shdr mS = genrateSectionHeaderStructure64();
			readSectionHeaderStructure(raf, mS);

			mInternalSectionHeaders[m] = mS;

			loadSectionFromSectionHeader(raf, mS);
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

	private void printSectionHeader() {

		for (ELF_Shdr mS : mInternalSectionHeaders) {

			Log.e(DIVISION_LINE);

			switch (ByteUtil.bytes2Int32(mS.sh_type, header.isLittleEndian())) {

			case SHT_NULL:/* 0 */
				Log.e("This Section is Unvalueable");
				break;

			case SHT_PROGBITS:/* 1 */
				Log.e("Section : " + mS.getName() + " , type : SHT_PROGBITS "
						+ ", at " + ByteUtil.bytes2Hex(mS.sh_offset)
						+ " , size : " + ByteUtil.bytes2Int32(mS.sh_size));
				break;

			case SHT_SYMTAB:/* 2 */
				Log.e("Section : " + mS.getName()
						+ " , type : SHT_SYMTAB , at "
						+ ByteUtil.bytes2Hex(mS.sh_offset) + " , size : "
						+ ByteUtil.bytes2Int32(mS.sh_size));
				break;

			case SHT_STRTAB:/* 3 */
				Log.e("Section : " + mS.getName()
						+ " , type : SHT_STRTAB , at "
						+ ByteUtil.bytes2Hex(mS.sh_offset) + " , size : "
						+ ByteUtil.bytes2Int32(mS.sh_size));

				if (mInternalSectionHeaders[mStringSectionHeaderIndex] == mS)
					Log.e("This is a Section contains other Section's Name");
				break;

			case SHT_RELA:/* 4 */
				Log.e("Section : " + mS.getName() + " , type : SHT_RELA , at "
						+ ByteUtil.bytes2Hex(mS.sh_offset) + " , size : "
						+ ByteUtil.bytes2Int32(mS.sh_size));
				break;

			case SHT_HASH:/* 5 */
				Log.e("Section : " + mS.getName() + " , type : SHT_HASH , at "
						+ ByteUtil.bytes2Hex(mS.sh_offset) + " , size : "
						+ ByteUtil.bytes2Int32(mS.sh_size));
				break;

			case SHT_DYNAMIC:/* 6 */
				Log.e("Section : " + mS.getName()
						+ " , type : SHT_DYNAMIC , at "
						+ ByteUtil.bytes2Hex(mS.sh_offset) + " , size : "
						+ ByteUtil.bytes2Int32(mS.sh_size));
				break;

			case SHT_NOTE:/* 7 */
				Log.e("Section : " + mS.getName() + " , type : SHT_NOTE , at "
						+ ByteUtil.bytes2Hex(mS.sh_offset) + " , size : "
						+ ByteUtil.bytes2Int32(mS.sh_size));
				break;

			case SHT_REL:/* 9 */
				Log.e("Section : " + mS.getName() + " , type : SHT_REL , at "
						+ ByteUtil.bytes2Hex(mS.sh_offset) + " , size : "
						+ ByteUtil.bytes2Int32(mS.sh_size));
				break;

			case SHT_SHLIB:/* 10 */
				Log.e("Section : " + mS.getName() + " , type : SHT_SHLIB , at "
						+ ByteUtil.bytes2Hex(mS.sh_offset) + " , size : "
						+ ByteUtil.bytes2Int32(mS.sh_size));
				break;

			case SHT_DYMSYM:/* 11 */
				Log.e("Section : " + mS.getName()
						+ " , type : SHT_DYMSYM , at "
						+ ByteUtil.bytes2Hex(mS.sh_offset) + " , size : "
						+ ByteUtil.bytes2Int32(mS.sh_size));
				break;

			case SHT_NUM:/* 12 */
				Log.e("Section : " + mS.getName() + " , type : SHT_NUM , at "
						+ ByteUtil.bytes2Hex(mS.sh_offset) + " , size : "
						+ ByteUtil.bytes2Int32(mS.sh_size));
				break;

			case SHT_NOBITS:/* 8 */
				Log.e("Section : " + mS.getName()
						+ " , type : SHT_NOBITS , at "
						+ ByteUtil.bytes2Hex(mS.sh_offset) + " , size : "
						+ ByteUtil.bytes2Int32(mS.sh_size));
				break;
			default:
				Log.e("Section : " + mS.getName()
						+ " , type : Unknown Section Header Type ! at "
						+ ByteUtil.bytes2Hex(mS.sh_offset) + " , size : "
						+ ByteUtil.bytes2Int32(mS.sh_size));
				break;
			}
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

	public ELF_Header getELFHeader() {
		return header;
	}

	private void namedSection() {

		ELF_Shdr mStringSection = mInternalSectionHeaders[mStringSectionHeaderIndex];

		for (ELF_Shdr mS : mInternalSectionHeaders)
			mS.setName(mStringSection.section.getStringAtIndex(mS.sh_name));
	}

	private void locateSectionHeaderOffset(RandomAccessFile raf)
			throws IOException {
		long mSectionOffset = header.getSectionHeaderTableOffset();
		raf.seek(mSectionOffset);
	}

	private void loadSectionFromSectionHeader(RandomAccessFile raf, ELF_Shdr mS) {
		try {
			long index = raf.getFilePointer();
			mS.section = new ELF_Section(raf, header, mS);
			raf.seek(index);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to decode Section");
		}
	}

	public ELF_Shdr[] getAllDecodedSectionHeader() {
		return mInternalSectionHeaders;
	}

}
