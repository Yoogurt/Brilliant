package com.marik.elf;

import static com.marik.elf.ELF_Constant.ELFUnit.ELF32_Addr;
import static com.marik.elf.ELF_Constant.ELFUnit.ELF32_Off;
import static com.marik.elf.ELF_Constant.ELFUnit.ELF32_Word;
import static com.marik.elf.ELF_Constant.ProgramHeaderContent.PT_DYNAMIC;
import static com.marik.elf.ELF_Constant.ProgramHeaderContent.PT_GUN_STACK;
import static com.marik.elf.ELF_Constant.ProgramHeaderContent.PT_INTERP;
import static com.marik.elf.ELF_Constant.ProgramHeaderContent.PT_LOAD;
import static com.marik.elf.ELF_Constant.ProgramHeaderContent.PT_NOTE;
import static com.marik.elf.ELF_Constant.ProgramHeaderContent.PT_NULL;
import static com.marik.elf.ELF_Constant.ProgramHeaderContent.PT_PHDR;
import static com.marik.elf.ELF_Constant.ProgramHeaderContent.PT_SHLIB;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import com.marik.implement.CastSupport;
import com.marik.util.Log;
import com.marik.util.Util;

public class ELF_ProgramHeader {

	static public class ELF_Phdr extends CastSupport {

		/**
		 * segment type
		 */
		public byte[] p_type;

		/**
		 * segment offset
		 */
		public byte[] p_offset;

		/**
		 * virtual address of segment
		 */
		public byte[] p_vaddr;

		/**
		 * physical address
		 */
		public byte[] p_paddr;

		/**
		 * number of bytes in file for segment
		 */
		public byte[] p_filesz;

		/**
		 * number of bytes in memory for segment
		 */
		public byte[] p_memsz;

		/**
		 * flag
		 */
		public byte[] p_flags;

		/**
		 * memory alignment
		 */
		public byte[] p_align;

		private ELF_ProgramHeader mOut;
		private int id;

		public ELF_Phdr(ELF_ProgramHeader mOut, int id) {
			this.mOut = mOut;
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public ELF_ProgramHeader getProgramHeader() {
			return mOut;
		}

		public long getMemoryOffset() {
			return Util.bytes2Int64(p_vaddr);
		}

		public long getMemorySize() {
			return Util.bytes2Int32(p_memsz);
		}

		public long getProgramOffset() {
			return Util.bytes2Int64(p_offset);
		}

		public int getProgramSize() {
			return Util.bytes2Int32(p_filesz);
		}

		public static ELF_Phdr reinterpret_cast(byte[] data) {
			return reinterpret_cast(data, 0);
		}

		public static ELF_Phdr reinterpret_cast(byte[] data, int startIndex) {
			throw new UnsupportedOperationException();
		}

		public static int size() {
			return 32;
		}

	}

	private ELF_Header header;

	private ELF_Phdr[] mInternalProgramHeader;

	private List<ELF_Phdr> mLoadableSegment = new ArrayList<>();

	private ELF_Phdr mDynamicSegment;

	public ELF_ProgramHeader(RandomAccessFile is, ELF_Header header, boolean check) throws Exception {

		Log.e(LogConstant.DIVISION_LINE);
		Log.e(LogConstant.ELF_PROGRAM_TABLE);
		Log.e(LogConstant.DIVISION_LINE);

		this.header = header;

		verifyPhdrNum(header);

		is.seek(header.getProgramHeaderTableOffset());

		if (header.is32Bit())
			read32ProgramHeader(is);
	}

	private void verifyPhdrNum(ELF_Header header) throws ELFDecodeException {

		int phdr_num_ = header.getProgramHeaderTableNum();

		if (phdr_num_ < 1 || phdr_num_ > 65536 / ELF_Phdr.size())
			throw new ELFDecodeException("Elf_Phdr larger than 64 KB");
	}

	private void read32ProgramHeader(RandomAccessFile is) throws Exception {

		int mProgramHeaderCount = header.getProgramHeaderTableNum();
		mInternalProgramHeader = new ELF_Phdr[mProgramHeaderCount];

		is.seek(header.getProgramHeaderTableOffset());

		for (int m = 0; m < mProgramHeaderCount; m++) {

			ELF_Phdr ph = generate32ProgramHeaderStruture(m);
			read32ProgramHeaderInternal(ph, is);

			mInternalProgramHeader[m] = ph;

			switch (Util.bytes2Int32(ph.p_type, header.isLittleEndian())) {
			case PT_NULL:
				Log.e("Program Header " + m + " type : PT_NULL");
				break;

			case PT_LOAD:
				Log.e("Program Header " + m + " type : PT_LOAD");
				addLoadableSegment(ph);
				break;

			case PT_DYNAMIC:
				mDynamicSegment = ph;
				Log.e("Program Header " + m + " type : PT_DYNAMIC");
				break;

			case PT_INTERP:
				Log.e("Program Header " + m + " type : PT_INTERP");
				verifyInterpretorSegment(ph, is);
				break;

			case PT_NOTE:
				Log.e("Program Header " + m + " type : PT_NOTE");
				break;

			case PT_SHLIB:
				Log.e("Program Header " + m + " type : PT_SHLIB");
				break;

			case PT_PHDR:
				Log.e("Program Header " + m + " type : PT_PHDR");
				break;

			case PT_GUN_STACK:
				Log.e("Program Header " + m + " type : PT_GUN_STACK");
				break;

			default:
				Log.e("Unknown Program Header " + m + " type : "
						+ Util.bytes2Int32(ph.p_type, header.isLittleEndian()));
				break;
			}

			logSegmentInfo(ph);
			Log.e(LogConstant.DIVISION_LINE);

		}

	}

	private void addLoadableSegment(ELF_Phdr ph) {
		mLoadableSegment.add(ph);
	}

	private void verifyInterpretorSegment(ELF_Phdr ph, RandomAccessFile raf) {

		try {
			long prePosition = raf.getFilePointer();
			raf.seek(Util.bytes2Int64(ph.p_offset));
			Log.e("Interpretor : " + Util.getStringFromBytes(raf));
			raf.seek(prePosition);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ELF_Phdr getDynamicSegment() {
		return mDynamicSegment;
	}

	private void read32ProgramHeaderInternal(ELF_Phdr ph, RandomAccessFile is) throws IOException {

		is.read(ph.p_type);
		is.read(ph.p_offset);
		is.read(ph.p_vaddr);
		is.read(ph.p_paddr);
		is.read(ph.p_filesz);
		is.read(ph.p_memsz);
		is.read(ph.p_flags);
		is.read(ph.p_align);

	}

	private void logSegmentInfo(ELF_Phdr ph) {

		Log.e("Segment p_offset :  " + Util.bytes2Hex(ph.p_offset));
		Log.e("Segment p_vaddr  : " + Util.bytes2Hex(ph.p_vaddr));
		Log.e("Segment p_memsz : " + Util.bytes2Hex(ph.p_memsz));

		Log.e("Segment p_filesz : " + Util.decHexSizeFormat32(ph.p_filesz, header.isLittleEndian()));
		if (header.isSharedObject() || header.isExeutable())
			Log.e("Segment will mmap directly at memory : " + Util.bytes2Hex(ph.p_paddr));

		// Util.assertAlign(Util.bytes2Int64(ph.p_align));
	}

	private ELF_Phdr generate32ProgramHeaderStruture(int id) {

		ELF_Phdr ph = new ELF_Phdr(this, id);
		ph.p_type = new byte[ELF32_Word];// 4
		ph.p_offset = new byte[ELF32_Off];// 4
		ph.p_vaddr = new byte[ELF32_Addr];// 4
		ph.p_paddr = new byte[ELF32_Addr];// 4
		ph.p_filesz = new byte[ELF32_Word];// 4
		ph.p_memsz = new byte[ELF32_Word];// 4
		ph.p_flags = new byte[ELF32_Word];// 4
		ph.p_align = new byte[ELF32_Word];// 4

		return ph;

	}

	public ELF_Phdr getProgramHeaderBySegmentPosition(int position) {

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

	public List<ELF_Phdr> getAllLoadableSegment() {
		return mLoadableSegment;
	}

	public ELF_Header getELFHeader() {
		return header;
	}

	public ELF_Phdr[] getAllDecodedProgramHeader() {
		return mInternalProgramHeader;
	}

}
