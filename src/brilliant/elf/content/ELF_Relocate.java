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
import java.util.Arrays;

import brilliant.elf.util.ByteUtil;
import brilliant.elf.util.Log;
import brilliant.elf.vm.OS;

final class ELF_Relocate {

	class Elf_rel implements Comparable<Elf_rel> {
		public byte[] r_offset;

		public byte[] r_info;

		@Override
		public int compareTo(Elf_rel o) {
			return ELF_R_SYM(r_info) > ELF_R_SYM(o.r_info) ? 1 : -1;
		}

	}

	class Elf_rela extends Elf_rel {
		public byte[] r_addend;
	}

	private Elf_rel[] mInternalRelocates;
	private ELF_Dynamic mSelf;

	private boolean mRela; // are we Rela ?

	ELF_Relocate(int offset, int size, ELF_Dynamic mSelf, boolean rela) throws IOException {

		this.mSelf = mSelf;
		mRela = rela;

		if (!rela)
			readElf_Rel(offset, size >>> 3);
		else
			readElf_Rela(offset, size >>> 3);

		Arrays.sort(mInternalRelocates);
		printElf_rel();
	}

	public boolean isRela() {
		return mRela;
	}

	private void readElf_Rela(int offset, int size) throws IOException {

		mInternalRelocates = new Elf_rela[size];

		for (int i = 0; i < size; i++)
			mInternalRelocates[i] = generateElfRelocateA32(offset + mSelf.elf_load_bias + 8 * i);

	}

	private void readElf_Rel(int offset, int size) throws IOException {

		mInternalRelocates = new Elf_rel[size];

		for (int i = 0; i < size; i++)
			mInternalRelocates[i] = generateElfRelocate32(offset + mSelf.elf_load_bias + 8 * i);

	}

	private void printElf_rel() throws IOException {

		for (Elf_rel rel : mInternalRelocates) {

			byte r_info = ELF_R_TYPE(rel.r_info);

			int sym = ELF_R_SYM(rel.r_info);

			switch (r_info) {
			case R_ARM_GLOB_DAT:
				Log.e("       relocation section r_offset : " + ByteUtil.bytes2Hex(rel.r_offset)
						+ " r_info : R_ARM_GLOB_DAT " + " , sym : " + sym
						+ (sym > 0 ? " , symbol name : " + mSelf.getSymInStrTab(sym) : ""));
				Log.e(LogConstant.DIVISION_LINE);
				break;

			case R_ARM_RELATIVE:
				Log.e("       relocation section r_offset : " + ByteUtil.bytes2Hex(rel.r_offset)
						+ " r_info : R_ARM_RELATIVE" + " , sym : " + sym
						+ (sym > 0 ? " , symbol name : " + mSelf.getSymInStrTab(sym) : ""));
				Log.e(LogConstant.DIVISION_LINE);
				break;

			case R_ARM_JUMP_SLOT:
				Log.e("       relocation section r_offset : " + ByteUtil.bytes2Hex(rel.r_offset)
						+ " r_info : R_ARM_JUMP_SLOT" + " , sym : " + sym
						+ (sym > 0 ? " , symbol name : " + mSelf.getSymInStrTab(sym) : ""));
				Log.e(LogConstant.DIVISION_LINE);
				break;

			default:
				break;
			}

		}
		Log.e("Found " + mInternalRelocates.length + " Relocate Info");
	}

	public Elf_rel[] getRelocateEntry() {
		return mInternalRelocates;
	}

	private Elf_rel generateElfRelocate32(int offset) throws IOException {

		Elf_rel relocate = new Elf_rel();
		relocate.r_offset = new byte[ELF32_Addr];
		relocate.r_info = new byte[ELF32_Word];

		System.arraycopy(OS.getMainImage().getMemory(), offset, relocate.r_offset, 0, ELF32_Addr);
		System.arraycopy(OS.getMainImage().getMemory(), offset + ELF32_Addr, relocate.r_info, 0, ELF32_Word);

		return relocate;
	}

	private Elf_rela generateElfRelocateA32(int offset) throws IOException {

		Elf_rela relocate = new Elf_rela();
		relocate.r_offset = new byte[ELF32_Addr];
		relocate.r_info = new byte[ELF32_Word];
		relocate.r_addend = new byte[ELF32_Sword];

		System.arraycopy(OS.getMainImage().getMemory(), offset, relocate.r_offset, 0, ELF32_Addr);
		System.arraycopy(OS.getMainImage().getMemory(), offset + ELF32_Addr, relocate.r_info, 0, ELF32_Word);
		System.arraycopy(OS.getMainImage().getMemory(), offset + ELF32_Addr + ELF32_Word, relocate.r_addend, 0,
				ELF32_Sword);

		return relocate;
	}

}
