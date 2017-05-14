package brilliant.elf.content;

import static brilliant.elf.content.ELF_Constant.ELFUnit.ELF32_Addr;
import static brilliant.elf.content.ELF_Constant.ELFUnit.ELF32_Half;
import static brilliant.elf.content.ELF_Constant.ELFUnit.ELF32_Off;
import static brilliant.elf.content.ELF_Constant.ELFUnit.ELF32_Word;
import static brilliant.elf.content.ELF_Constant.HeaderContent.EI_CALSS;
import static brilliant.elf.content.ELF_Constant.HeaderContent.EI_DATA;
import static brilliant.elf.content.ELF_Constant.HeaderContent.EI_NIDENT;
import static brilliant.elf.content.ELF_Constant.HeaderContent.ELFCLASS32;
import static brilliant.elf.content.ELF_Constant.HeaderContent.ELFCLASS64;
import static brilliant.elf.content.ELF_Constant.HeaderContent.ELFDATA2LSB;
import static brilliant.elf.content.ELF_Constant.HeaderContent.ELFDATA2MSB;
import static brilliant.elf.content.ELF_Constant.HeaderContent.ELFMagicCode;
import static brilliant.elf.content.ELF_Constant.HeaderContent.EM_386;
import static brilliant.elf.content.ELF_Constant.HeaderContent.EM_AARCH64;
import static brilliant.elf.content.ELF_Constant.HeaderContent.EM_ARM;
import static brilliant.elf.content.ELF_Constant.HeaderContent.EM_MIPS;
import static brilliant.elf.content.ELF_Constant.HeaderContent.EM_X86_64;
import static brilliant.elf.content.ELF_Constant.HeaderContent.ET_CORE;
import static brilliant.elf.content.ELF_Constant.HeaderContent.ET_DYN;
import static brilliant.elf.content.ELF_Constant.HeaderContent.ET_EXEC;
import static brilliant.elf.content.ELF_Constant.HeaderContent.ET_HIPROC;
import static brilliant.elf.content.ELF_Constant.HeaderContent.ET_LOPROC;
import static brilliant.elf.content.ELF_Constant.HeaderContent.ET_NONE;
import static brilliant.elf.content.ELF_Constant.HeaderContent.ET_REL;
import static brilliant.elf.content.ELF_Constant.HeaderContent.EV_CURRENT;

import java.io.RandomAccessFile;

import brilliant.elf.util.ByteUtil;
import brilliant.elf.util.Log;

/**
 * @author Yoogurt
 *
 */
@SuppressWarnings("all")
class ELF_Header {

	/**
	 * ELF Identification 00 - 0F(32、64 bit)
	 */
	byte[] e_ident;
	/**
	 * object file type 10 - 11(32、64 bit)
	 */
	byte[] e_type;
	/**
	 * target machine 12 - 13(32、64 bit)
	 */
	byte[] e_machine;
	/**
	 * object file version 14 - 17(32、64 bit)
	 */
	byte[] e_version;
	/**
	 * virtual entry point , it's doesn't work when ELF is a shared object 18 -
	 * 1B (64bit 18 - 1F)
	 */
	byte[] e_entry;
	/**
	 * program header table offset 1C - 1F (64 bit 20 - 27)
	 */
	byte[] e_phoff;
	/**
	 * section hreader table offset 20 - 23 (64 bit 28 - 2F)
	 */
	byte[] e_shoff;
	/**
	 * processor-specific flags 24 - 27 (64 bit 30 - 33)
	 */
	byte[] e_flags;
	/**
	 * ELF header size(the size of this class) 28 - 29 (64 bit 34 - 35)
	 */
	byte[] e_ehsize;
	/**
	 * program hreader entry size 2A - 2B (64 bit 36 - 37)
	 */
	byte[] e_phentsize;
	/**
	 * number of program header entries 2C - 2D (64 bit 38 - 39)
	 */
	byte[] e_phnum;
	/**
	 * section header entry size 2E - 2F (64 bit 3A - 3B)
	 */
	byte[] e_shentsize;
	/**
	 * number of section header entries 30 - 31 (64 bit 3C - 3D)
	 */
	byte[] e_shnum;
	/**
	 * section header table's string table entry offset 32 - 33 (64 bit 3E - 3F)
	 */
	byte[] e_shstrndex;

	ELF_Header(RandomAccessFile is) throws Exception {

		Log.e(LogConstant.DIVISION_LINE);
		Log.e(LogConstant.ELF_HEADER);
		Log.e(LogConstant.DIVISION_LINE);

		verifyElfMagicCode(is);

		if (is32Bit())
			read32BitHeader(is);
		else
			throw new ELFDecodeException("64 bit not supported yet");

		checkHeaderParameters();

		readELFHeader();
	}

	private void read32BitHeader(RandomAccessFile is) throws Exception {

		e_type = new byte[ELF32_Half];
		is.read(e_type);

		e_machine = new byte[ELF32_Half];
		is.read(e_machine);

		e_version = new byte[ELF32_Word];
		is.read(e_version);

		e_entry = new byte[ELF32_Addr];
		is.read(e_entry);

		e_phoff = new byte[ELF32_Off];
		is.read(e_phoff);

		e_shoff = new byte[ELF32_Off];
		is.read(e_shoff);

		e_flags = new byte[ELF32_Word];
		is.read(e_flags);

		e_ehsize = new byte[ELF32_Half];
		is.read(e_ehsize);

		e_phentsize = new byte[ELF32_Half];
		is.read(e_phentsize);

		e_phnum = new byte[ELF32_Half];
		is.read(e_phnum);

		e_shentsize = new byte[ELF32_Half];
		is.read(e_shentsize);

		e_shnum = new byte[ELF32_Half];
		is.read(e_shnum);

		e_shstrndex = new byte[ELF32_Half];
		is.read(e_shstrndex);
	}

	private void verifyElfMagicCode(RandomAccessFile is) throws Exception {

		e_ident = new byte[EI_NIDENT];
		is.read(e_ident);

		if (!ByteUtil.equals(ELFMagicCode, 0, e_ident, 0, // assert Magic
															// Code correct
															// or not
				ELFMagicCode.length))
			throw new ELFDecodeException("Not a ELF File");
	}

	private void checkHeaderParameters() throws ELFDecodeException {
		switch (e_ident[EI_CALSS]) {
		case ELFCLASS32:
			Log.i("for 32 bit machine");
			break;

		case ELFCLASS64:
			Log.i("for 64 bit machine");
			throw new UnsupportedOperationException(
					"64 bit elf are not supported to decode");

		default:
			throw new ELFDecodeException("ELF illegal class file , EI_CLASS = "
					+ ByteUtil.byte2Hex(e_ident[EI_CALSS]));
		}

		switch (e_ident[EI_DATA]) {
		case ELFDATA2LSB:
			Log.i("for little endian machine");
			break;

		case ELFDATA2MSB:
			Log.i("for big endian machine");

		default:
			throw new ELFDecodeException("Unknown target endian machine");
		}

		if (ByteUtil.bytes2Int32(e_type) != ET_DYN)
			throw new ELFDecodeException();

		if (ByteUtil.bytes2Int32(e_version) != EV_CURRENT)
			throw new ELFDecodeException("Unknown ELF Version");

		if (ByteUtil.bytes2Int32(e_machine) != EM_ARM)
			throw new ELFDecodeException("elf has unexpected e_machine : "
					+ ByteUtil.bytes2Hex(e_machine));
	}

	public void readELFHeader() throws ELFDecodeException {

		Log.e();

		switch (ByteUtil.bytes2Int32(e_type, isLittleEndian())) {
		case ET_NONE:
			Log.i("Unknown ELF Type");
			break;
		case ET_REL:
			Log.i("Relocatable ELF File");
			break;
		case ET_EXEC:
			Log.i("Executable ELF File");
			break;
		case ET_DYN:
			Log.i("Shared Object ELF File");
			break;
		case ET_CORE:
			Log.i("Core ELF File");
			break;
		case ET_HIPROC:
		case ET_LOPROC:
			Log.i("Specify Architecture ELF");
			break;
		default:
			throw new ELFDecodeException(
					"Unknown ELF Type While reading ELF Header");
		}

		switch (ByteUtil.bytes2Int32(e_machine, isLittleEndian())) {
		case EM_ARM:
			Log.i("for ARM CPU(armeabi armeabi-v7a)");
			break;
		case EM_AARCH64:
			Log.i("for ARM AArch64 CPU(armeabi-v8a , 64 bit processor)");
			break;
		case EM_MIPS:
			Log.e("for MIPS CPU");
			break;
		case EM_386:
			Log.e("for Intel x86(32 bit processor)");
			break;
		case EM_X86_64:
			Log.e("for Intel x86(64 bit processor)");
			break;
		default:
			throw new ELFDecodeException(
					"Unsupport processor Architecture for this ELF !");
		}

		if (ByteUtil.bytes2Int32(e_version, isLittleEndian()) != EV_CURRENT)
			throw new ELFDecodeException("Unknown ELF Version");

		Log.e();

		Log.e("ELF Enter Entry : " + ByteUtil.bytes2Hex(e_entry));

		Log.e("Program Header Table Offset : " + ByteUtil.bytes2Hex(e_phoff)
				+ (isLittleEndian() ? " Little Endian" : " Big Endian"));
		Log.e("Program Header Table per enitity's size : "
				+ ByteUtil.bytes2Int32(e_phentsize, isLittleEndian()) + " B");
		Log.e("Program Header Table total enitities : "
				+ ByteUtil.bytes2Int32(e_phnum, isLittleEndian()));

		Log.e();

		Log.e("Section Header Table Offset : " + ByteUtil.bytes2Hex(e_shoff)
				+ (isLittleEndian() ? " Little Endian" : " Big Endian"));
		Log.e("Section Header Table per enitity's size : "
				+ ByteUtil.bytes2Int32(e_shentsize, isLittleEndian()) + " B");
		Log.e("Section Header Table total enitities : "
				+ ByteUtil.bytes2Int32(e_shnum, isLittleEndian()));
		Log.e("Section Header Table's \"String Table\" Index : "
				+ ByteUtil.bytes2Hex(e_shstrndex));

		Log.e();

		Log.e("ELF Header Flags : " + ByteUtil.bytes2Hex(e_flags));
		Log.e("ELF Header totally size : "
				+ ByteUtil.bytes2Int32(e_ehsize, isLittleEndian()) + " B");

	}

	public boolean isSharedObject() {
		return ByteUtil.bytes2Int32(e_type, isLittleEndian()) == ET_DYN;
	}

	public boolean isExeutable() {
		return ByteUtil.bytes2Int32(e_type, isLittleEndian()) == ET_EXEC;
	}

	public boolean is32Bit() {
		return e_ident[EI_CALSS] == ELFCLASS32;
	}

	public boolean isLittleEndian() {
		return e_ident[EI_DATA] == ELFDATA2LSB;
	}

	public long getELFEntry() {
		if (is32Bit())
			return ByteUtil.bytes2Int32(e_entry, isLittleEndian());
		return ByteUtil.bytes2Int64(e_entry, isLittleEndian());
	}

	public long getProgramHeaderTableOffset() {
		if (is32Bit())
			return ByteUtil.bytes2Int32(e_phoff, isLittleEndian());
		return ByteUtil.bytes2Int64(e_phoff, isLittleEndian());
	}

	public int getProgramHeaderTableEntrySize() {
		return ByteUtil.bytes2Int32(e_phentsize, isLittleEndian());
	}

	public int getProgramHeaderTableNum() {
		return ByteUtil.bytes2Int32(e_phnum, isLittleEndian());
	}

	public long getSectionHeaderTableOffset() {
		if (is32Bit())
			return ByteUtil.bytes2Int32(e_shoff, isLittleEndian());
		return ByteUtil.bytes2Int64(e_shoff, isLittleEndian());
	}

	public int getSectionHeaderTableEntrySize() {
		return ByteUtil.bytes2Int32(e_shentsize, isLittleEndian());
	}

	public int getSectionHeaderNum() {
		return ByteUtil.bytes2Int32(e_shnum, isLittleEndian());
	}

	public int getSectionStringIndex() {
		return ByteUtil.bytes2Int32(e_shstrndex, isLittleEndian());
	}

	/**
	 * we would like to assume that an elf wouldn't larger than 4GB
	 */
	public int getHeaderSize() {
		return ByteUtil.bytes2Int32(e_ehsize, isLittleEndian());
	}

	@Override
	public String toString() {
		return String
				.format("[e_ident = %s\n e_type = %s\n e_machine = %s\n e_version = %s\n e_entry = %s\n e_phoff = %s\n e_shoff = %s\n e_flags = %s\n e_ehsize = %s\n e_phentsize = %s\n e_phnum = %s\n e_shentsize = %s\n e_shnum = %s\n e_shstrndex = %s]",
						ByteUtil.bytes2Hex(e_ident),
						ByteUtil.bytes2Hex(e_type),
						ByteUtil.bytes2Hex(e_machine),
						ByteUtil.bytes2Hex(e_version),
						ByteUtil.bytes2Hex(e_entry),
						ByteUtil.bytes2Hex(e_phoff),
						ByteUtil.bytes2Hex(e_shoff),
						ByteUtil.bytes2Hex(e_flags),
						ByteUtil.bytes2Hex(e_ehsize),
						ByteUtil.bytes2Hex(e_phentsize),
						ByteUtil.bytes2Hex(e_phnum),
						ByteUtil.bytes2Hex(e_shentsize),
						ByteUtil.bytes2Hex(e_shnum),
						ByteUtil.bytes2Hex(e_shstrndex));
	}

}
