package com.BB.elf;

import static com.BB.elf.ELFConstant.ELFUnit.ELF32_Addr;
import static com.BB.elf.ELFConstant.ELFUnit.ELF32_Off;
import static com.BB.elf.ELFConstant.ELFUnit.ELF32_Word;
import static com.BB.elf.ELFConstant.ELFUnit.ELF64_Addr;
import static com.BB.elf.ELFConstant.ELFUnit.ELF64_Off;
import static com.BB.elf.ELFConstant.ELFUnit.ELF64_Word;
import static com.BB.elf.ELFConstant.ELFUnit.ELF64_Xword;
import static com.BB.elf.ELFConstant.ProgramHeaderContent.PT_DYNAMIC;
import static com.BB.elf.ELFConstant.ProgramHeaderContent.PT_INTERP;
import static com.BB.elf.ELFConstant.ProgramHeaderContent.PT_LOAD;
import static com.BB.elf.ELFConstant.ProgramHeaderContent.PT_NOTE;
import static com.BB.elf.ELFConstant.ProgramHeaderContent.PT_NULL;
import static com.BB.elf.ELFConstant.ProgramHeaderContent.PT_PHDR;
import static com.BB.elf.ELFConstant.ProgramHeaderContent.PT_SHLIB;

import java.io.IOException;
import java.io.RandomAccessFile;

import com.BB.util.Log;
import com.BB.util.Util;

public class ELF_ProgramHeader {

	public class ELF_Phdr {

		/**
		 * segment type
		 */
		byte[] p_type;

		/**
		 * segment offset
		 */
		byte[] p_offset;

		/**
		 * virtual address of segment
		 */
		byte[] p_vaddr;

		/**
		 * physical address
		 */
		byte[] p_paddr;

		/**
		 * number of bytes in file for egment
		 */
		byte[] p_filesz;

		/**
		 * number of butes in menory for segment
		 */
		byte[] p_memsz;

		/**
		 * flag
		 */
		byte[] p_flags;

		/**
		 * menory alignment
		 */
		byte[] p_align;

		@Override
		public String toString() {
			return String
					.format("[p_type=%s\n p_offset=%s\n p_vaddr=%s\n p_paddr=%s\n p_filesz=%s\n p_memsz=%s\n p_flags=%s\n p_align=%s]",
							Util.bytes2Hex(p_type), Util.bytes2Hex(p_offset),
							Util.bytes2Hex(p_vaddr), Util.bytes2Hex(p_paddr),
							Util.bytes2Hex(p_filesz), Util.bytes2Hex(p_memsz),
							Util.bytes2Hex(p_flags), Util.bytes2Hex(p_align));
		}

	}

	private ELF_Phdr[] mInternalProgramHeader;
	private int[] mLoadableSegment;
	private int mLoadablePtr;

	private boolean isPT_PHDRExist;
	private boolean isPT_INTERPExist;

	public ELF_ProgramHeader(RandomAccessFile is, ELF_Header header)
			throws Exception {

		Log.e(Constant.DIVISION_LINE);
		Log.e(Constant.ELF_PROGRAM_TABLE);
		Log.e(Constant.DIVISION_LINE);

		isPT_PHDRExist = false;
		isPT_INTERPExist = false;

		if (!locateProgramHeader(header, is)) {
			Log.e("Program Header not found , skipping init");
			return;
		}

		if (header.is32Bit())
			read32ProgramHeader(is, header);
		else
			read64ProgramHeader(is, header);

	}

	private void read32ProgramHeader(RandomAccessFile is, ELF_Header header)
			throws Exception {

		long programHeaderOffset = header.getProgramHeaderTableOffset();
		if (programHeaderOffset == 0) {
			Log.e("Program Header not found , skipping init");
			return;
		}
		is.seek(programHeaderOffset);

		int mProgramHeaderCount = header.getProgramHeaderTableNum();
		mInternalProgramHeader = new ELF_Phdr[mProgramHeaderCount];
		initializeLoadableSegmentArray(mProgramHeaderCount);

		for (int m = 0; m < mProgramHeaderCount; m++) {

			ELF_Phdr ph = generate32ProgramHeaderStruture();
			read32ProgramHeaderInternal(ph, is);

			mInternalProgramHeader[m] = ph;

			switch (Util.bytes2Int32(ph.p_type, header.isLittleEndian())) {
			case PT_NULL:
				Log.e("Program Header " + (m + 1) + " is Undefine :");
				break;

			case PT_LOAD:
				Log.e("Program Header " + (m + 1)
						+ " Gives a Loadable Segment : ");

				addLoadableInfoIntoArray(m);
				break;

			case PT_DYNAMIC:
				Log.e("Program Header " + (m + 1)
						+ " Gives a dynamic link infomation :");
				// verifyDynamicSegmentBeforAllLoadableSegment32(ph);
				break;

			case PT_INTERP:
				Log.e("Program Header " + (m + 1) + " pointed a interpretor : ");
				verifyInterpretorSegment();
				break;

			case PT_NOTE:
				Log.e("Program Header " + (m + 1) + " Gives addition info : ");
				break;

			case PT_SHLIB:
				Log.e("Program Header " + (m + 1) + " is Reserve");
				break;

			case PT_PHDR:

				Log.e("Program Header " + (m + 1) + " requires verify itself");
				verifyProgramHeader32(ph, header);
				break;

			default:
				Log.e("Unknown Program Header " + (m + 1) + " Type : ");
				break;
			}

			decodeLoadableSegment(ph, header);
			Log.e(Constant.DIVISION_LINE);

		}
	}

	private void read64ProgramHeader(RandomAccessFile is, ELF_Header header)
			throws Exception {

		int mProgramHeaderCount = header.getProgramHeaderTableNum();

		mInternalProgramHeader = new ELF_Phdr[mProgramHeaderCount];

		for (int m = 0; m < mProgramHeaderCount; m++) {

			ELF_Phdr ph = generate64ProgramHeaderStruture();
			read64ProgramHeaderInternal(ph, is);

			mInternalProgramHeader[m] = ph;

			switch (Util.bytes2Int32(ph.p_type, header.isLittleEndian())) {
			case PT_NULL:
				Log.e("Program Header " + (m + 1) + " is Undefine :");
				break;

			case PT_LOAD:
				Log.e("Program Header " + (m + 1)
						+ " Gives a Loadable Segment :");
				addLoadableInfoIntoArray(m);
				break;

			case PT_DYNAMIC:
				Log.e("Program Header " + (m + 1)
						+ " Gives a dynamic link infomation :");
				verifyDynamicSegmentBeforAllLoadableSegment64(ph);
				break;

			case PT_INTERP:
				Log.e("Program Header " + (m + 1) + " pointed a interpretor : ");
				verifyInterpretorSegment();
				break;

			case PT_NOTE:
				Log.e("Program Header " + (m + 1) + " Gives addition info : ");
				break;

			case PT_SHLIB:
				Log.e("Program Header " + (m + 1) + " is Reserve : ");
				break;

			case PT_PHDR:

				Log.e("Program Header " + (m + 1)
						+ " requires verify itself : ");
				verifyProgramHeader64(ph, header);
				break;

			default:
				Log.e("Unknown Program Header " + (m + 1) + " Type : ");
				break;
			}

			decodeLoadableSegment(ph, header);
			Log.e(Constant.DIVISION_LINE);

		}

	}

	private boolean locateProgramHeader(ELF_Header header, RandomAccessFile raf)
			throws IOException {
		long programHeaderOffset = header.getProgramHeaderTableOffset();
		if (programHeaderOffset == 0)
			return false;

		raf.seek(programHeaderOffset);
		return true;
	}

	private void verifyInterpretorSegment() {
		if (!isPT_INTERPExist)
			isPT_INTERPExist = true;
		else
			throw new RuntimeException("Interpret Segment appear over once!");
	}

	@Deprecated
	private void verifyDynamicSegmentBeforAllLoadableSegment32(
			ELF_Phdr dynamicSegment, ELF_Header header) {

		for (int m = 0; m < mLoadablePtr; m++) {
			ELF_Phdr mT = mInternalProgramHeader[mLoadableSegment[m]];

			if (Util.bytes2Int32(mT.p_offset, header.isLittleEndian()) < Util
					.bytes2Int32(dynamicSegment.p_offset,
							header.isLittleEndian()))
				throw new RuntimeException(
						"Loadable Segment before Dynamic Segment , dynamic Segment : "
								+ Util.bytes2Hex(dynamicSegment.p_offset)
								+ " , Loadable Segment : "
								+ Util.bytes2Hex(mT.p_offset));
		}

	}

	private void verifyDynamicSegmentBeforAllLoadableSegment64(
			ELF_Phdr dynamicSegment) {

		for (int m = 0; m < mLoadablePtr; m++) {
			ELF_Phdr mT = mInternalProgramHeader[mLoadableSegment[m]];

			if (Util.bytes2Int64(mT.p_offset) < Util
					.bytes2Int64(dynamicSegment.p_offset))
				throw new RuntimeException(
						"Loadable Segment before Dynamic Segment , dynamic Segment : "
								+ Util.bytes2Hex(dynamicSegment.p_offset)
								+ " , Loadable Segment : "
								+ Util.bytes2Hex(mT.p_offset));
		}

	}

	private void addLoadableInfoIntoArray(int index) {

		mLoadableSegment[mLoadablePtr++] = index;

	}

	private void initializeLoadableSegmentArray(int maxHeaderCount) {

		mLoadableSegment = new int[maxHeaderCount];
		mLoadablePtr = 0;

		for (int m = 0; m < maxHeaderCount; m++)
			mLoadableSegment[m] = -1;

	}

	private void read32ProgramHeaderInternal(ELF_Phdr ph, RandomAccessFile is)
			throws IOException {

		is.read(ph.p_type);
		is.read(ph.p_offset);
		is.read(ph.p_vaddr);
		is.read(ph.p_paddr);
		is.read(ph.p_filesz);
		is.read(ph.p_memsz);
		is.read(ph.p_flags);
		is.read(ph.p_align);

	}

	private void read64ProgramHeaderInternal(ELF_Phdr ph, RandomAccessFile is)
			throws IOException {

		is.read(ph.p_type);
		is.read(ph.p_flags);
		is.read(ph.p_offset);
		is.read(ph.p_vaddr);
		is.read(ph.p_paddr);
		is.read(ph.p_filesz);
		is.read(ph.p_memsz);
		is.read(ph.p_align);

	}

	private void verifyProgramHeader32(ELF_Phdr ph, ELF_Header header) {

		if (isPT_PHDRExist)
			throw new RuntimeException("Multi-PT_PHDR defined !");
		isPT_PHDRExist = true;

		if (header.getProgramHeaderTableOffset() != Util.bytes2Int32(
				ph.p_offset, header.isLittleEndian()))
			throw new RuntimeException("Program Header Offset Verify Fail");

		if (header.getProgramHeaderTableEntrySize()
				* header.getProgramHeaderTableNum() != Util.bytes2Int32(
				ph.p_filesz, header.isLittleEndian()))
			throw new RuntimeException(
					"Program Header Size Verify Fail , exspect "
							+ Util.bytes2Hex(ph.p_filesz)
							+ " , got "
							+ Integer.toHexString(header
									.getProgramHeaderTableEntrySize()
									* header.getProgramHeaderTableNum()));

		Log.e("Program Header Verify Success\n");

	}

	private void verifyProgramHeader64(ELF_Phdr ph, ELF_Header header) {

		if (isPT_PHDRExist)
			throw new RuntimeException("Multi-PT_PHDR defined !");
		isPT_PHDRExist = true;

		if (header.getProgramHeaderTableOffset() != Util
				.bytes2Int64(ph.p_offset))
			throw new RuntimeException("Program Header Offset Verify Fail");

		if (header.getProgramHeaderTableEntrySize()
				* header.getProgramHeaderTableNum() != Util
					.bytes2Int64(ph.p_filesz))
			throw new RuntimeException("Program Header Size Verify Fail");

		Log.e("Program Header Verify Success\n");

	}

	private void decodeLoadableSegment(ELF_Phdr ph, ELF_Header header) {

		Log.e("Segment at " + Util.bytes2Hex(ph.p_offset));
		Log.e("Segment would mmap at virtual memory : "
				+ Util.bytes2Hex(ph.p_vaddr));

		if (header.isSharedObject() || header.isExeutable())
			Log.e("Segment will mmap directly at memory : "
					+ Util.bytes2Hex(ph.p_paddr));

		Log.e("Segment takes "
				+ Util.decHexSizeFormat32(ph.p_filesz, header.isLittleEndian())
				+ " in elf");

		// Util.assertAlign(Util.bytes2Int64(ph.p_align));

	}

	private ELF_Phdr generate32ProgramHeaderStruture() {

		ELF_Phdr ph = new ELF_Phdr();
		ph.p_type = new byte[ELF32_Word];
		ph.p_offset = new byte[ELF32_Off];
		ph.p_vaddr = new byte[ELF32_Addr];
		ph.p_paddr = new byte[ELF32_Addr];
		ph.p_filesz = new byte[ELF32_Word];
		ph.p_memsz = new byte[ELF32_Word];
		ph.p_flags = new byte[ELF32_Word];
		ph.p_align = new byte[ELF32_Word];

		return ph;

	}

	private ELF_Phdr generate64ProgramHeaderStruture() {

		ELF_Phdr ph = new ELF_Phdr();
		ph.p_type = new byte[ELF64_Word];
		ph.p_offset = new byte[ELF64_Off];
		ph.p_vaddr = new byte[ELF64_Addr];
		ph.p_paddr = new byte[ELF64_Addr];
		ph.p_filesz = new byte[ELF64_Xword];
		ph.p_memsz = new byte[ELF64_Xword];
		ph.p_flags = new byte[ELF64_Word];
		ph.p_align = new byte[ELF64_Xword];

		return ph;

	}

	public ELF_Phdr getProgramHeaderBySegmentPosition(int position,
			ELF_Header header) {

		for (ELF_Phdr mT : mInternalProgramHeader) {
			if (Util.bytes2Int32(mT.p_offset, header.isLittleEndian()) == position) {
				return mT;
			}
		}

		return null;
	}

	public ELF_Phdr getProgramHeaderBySegmentPosition(long position) {

		for (ELF_Phdr mT : mInternalProgramHeader) {
			if (Util.bytes2Int64(mT.p_offset) == position) {
				return mT;
			}
		}

		return null;
	}

}
