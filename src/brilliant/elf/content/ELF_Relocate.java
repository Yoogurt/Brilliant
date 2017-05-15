package brilliant.elf.content;

import static brilliant.elf.content.ELF_Constant.DT_RelType.R_ARM_GLOB_DAT;
import static brilliant.elf.content.ELF_Constant.DT_RelType.R_ARM_JUMP_SLOT;
import static brilliant.elf.content.ELF_Constant.DT_RelType.R_ARM_RELATIVE;
import static brilliant.elf.content.ELF_Constant.ELFUnit.ELF32_Addr;
import static brilliant.elf.content.ELF_Constant.ELFUnit.ELF32_Sword;
import static brilliant.elf.content.ELF_Constant.ELFUnit.ELF32_Word;
import static brilliant.elf.content.ELF_Definition.ELF_R_SYM;
import static brilliant.elf.content.ELF_Definition.ELF_R_TYPE;

import java.io.IOException;
import java.io.RandomAccessFile;

import brilliant.elf.util.ByteUtil;
import brilliant.elf.util.Log;

final class ELF_Relocate {

	class Elf_rel {
		public byte[] r_offset;

		public byte[] r_info;
	}

	class Elf_rela extends Elf_rel {
		public byte[] r_addend;
	}

	private Elf_rel[] mInternalRelocates;
	private ELF_Dynamic mSelf;

	private boolean mRela; // are we Rela ?

	ELF_Relocate(RandomAccessFile raf, long offset, int size,
			ELF_Dynamic mSelf, boolean rela) throws IOException {

		this.mSelf = mSelf;
		mRela = rela;

		if (!rela)
			readElf_Rel(raf, offset, size);
		else
			readElf_Rela(raf, offset, size);

	}

	public boolean isRela() {
		return mRela;
	}

	private void readElf_Rela(RandomAccessFile raf, long offset, int size)
			throws IOException {
		long prePosition = raf.getFilePointer();

		raf.seek(offset);

		size = size >> 3; // each Elf_rel takes 8B

		mInternalRelocates = new Elf_rel[size];

		for (int i = 0; i < size; i++)
			mInternalRelocates[i] = generateElfRelocateA32(raf);

		printElf_rel(raf);

		raf.seek(prePosition);
	}

	private void readElf_Rel(RandomAccessFile raf, long offset, int size)
			throws IOException {
		long prePosition = raf.getFilePointer();

		raf.seek(offset);

		size = size >> 3; // each Elf_rel takes 8B

		mInternalRelocates = new Elf_rel[size];

		for (int i = 0; i < size; i++)
			mInternalRelocates[i] = generateElfRelocate32(raf);

		printElf_rel(raf);

		raf.seek(prePosition);
	}

	private void printElf_rel(RandomAccessFile raf) throws IOException {

		for (Elf_rel rel : mInternalRelocates) {

			Log.e(LogConstant.DIVISION_LINE);

			byte r_info = ELF_R_TYPE(rel.r_info);

			int sym = ELF_R_SYM(rel.r_info);

			switch (r_info) {
			case R_ARM_GLOB_DAT:
				Log.e("       relocation section r_offset : "
						+ ByteUtil.bytes2Hex(rel.r_offset)
						+ " r_info : R_ARM_GLOB_DAT "
						+ " , sym : "
						+ sym
						+ (sym > 0 ? " , symbol name : "
								+ mSelf.getSymInStrTab(sym, raf) : ""));
				break;

			case R_ARM_RELATIVE:
				Log.e("       relocation section r_offset : "
						+ ByteUtil.bytes2Hex(rel.r_offset)
						+ " r_info : R_ARM_RELATIVE"
						+ " , sym : "
						+ sym
						+ (sym > 0 ? " , symbol name : "
								+ mSelf.getSymInStrTab(sym, raf) : ""));
				break;

			case R_ARM_JUMP_SLOT:
				Log.e("       relocation section r_offset : "
						+ ByteUtil.bytes2Hex(rel.r_offset)
						+ " r_info : R_ARM_JUMP_SLOT"
						+ " , sym : "
						+ sym
						+ (sym > 0 ? " , symbol name : "
								+ mSelf.getSymInStrTab(sym, raf) : ""));
				break;

			default:
				break;
			}

		}

		Log.e(LogConstant.DIVISION_LINE);
		Log.e("Found " + mInternalRelocates.length + " Relocate Info");

	}

	public Elf_rel[] getRelocateEntry() {
		return mInternalRelocates;
	}

	private Elf_rel generateElfRelocate32(RandomAccessFile raf)
			throws IOException {

		Elf_rel relocate = new Elf_rel();
		relocate.r_offset = new byte[ELF32_Addr];
		relocate.r_info = new byte[ELF32_Word];

		raf.read(relocate.r_offset);
		raf.read(relocate.r_info);

		return relocate;
	}

	private Elf_rela generateElfRelocateA32(RandomAccessFile raf)
			throws IOException {

		Elf_rela relocate = new Elf_rela();
		relocate.r_offset = new byte[ELF32_Addr];
		relocate.r_info = new byte[ELF32_Word];
		relocate.r_addend = new byte[ELF32_Sword];

		raf.read(relocate.r_offset);
		raf.read(relocate.r_info);
		raf.read(relocate.r_addend);

		return relocate;
	}

}
