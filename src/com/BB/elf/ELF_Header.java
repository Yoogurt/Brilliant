package com.BB.elf;

import static com.BB.elf.ELFConstant.ELFUnit.ELF32_Addr;
import static com.BB.elf.ELFConstant.ELFUnit.ELF32_Half;
import static com.BB.elf.ELFConstant.ELFUnit.ELF32_Off;
import static com.BB.elf.ELFConstant.ELFUnit.ELF32_Word;
import static com.BB.elf.ELFConstant.ELFUnit.ELF64_Addr;
import static com.BB.elf.ELFConstant.ELFUnit.ELF64_Half;
import static com.BB.elf.ELFConstant.ELFUnit.ELF64_Off;
import static com.BB.elf.ELFConstant.ELFUnit.ELF64_Word;
import static com.BB.elf.ELFConstant.HeaderContent.EI_CALSS;
import static com.BB.elf.ELFConstant.HeaderContent.EI_DATA;
import static com.BB.elf.ELFConstant.HeaderContent.EI_NIDENT;
import static com.BB.elf.ELFConstant.HeaderContent.EI_VERSION;
import static com.BB.elf.ELFConstant.HeaderContent.ELFCLASS32;
import static com.BB.elf.ELFConstant.HeaderContent.ELFCLASS64;
import static com.BB.elf.ELFConstant.HeaderContent.ELFDATA2LSB;
import static com.BB.elf.ELFConstant.HeaderContent.ELFDATA2MSB;
import static com.BB.elf.ELFConstant.HeaderContent.EM_386;
import static com.BB.elf.ELFConstant.HeaderContent.EM_AARCH64;
import static com.BB.elf.ELFConstant.HeaderContent.EM_ARM;
import static com.BB.elf.ELFConstant.HeaderContent.EM_MIPS;
import static com.BB.elf.ELFConstant.HeaderContent.EM_X86_64;
import static com.BB.elf.ELFConstant.HeaderContent.ET_CORE;
import static com.BB.elf.ELFConstant.HeaderContent.ET_DYN;
import static com.BB.elf.ELFConstant.HeaderContent.ET_EXEC;
import static com.BB.elf.ELFConstant.HeaderContent.ET_HIPROC;
import static com.BB.elf.ELFConstant.HeaderContent.ET_LOPROC;
import static com.BB.elf.ELFConstant.HeaderContent.ET_NONE;
import static com.BB.elf.ELFConstant.HeaderContent.ET_REL;
import static com.BB.elf.ELFConstant.HeaderContent.EV_CURRENT;
import static com.BB.elf.ELFConstant.HeaderContent.StandardELFMagicCode;

import java.io.RandomAccessFile;

import com.BB.util.BytesBuilder;
import com.BB.util.Log;
import com.BB.util.Util;

/**
 * @author 国斌
 *
 */
/**
 * @author 国斌
 *
 */
public class ELF_Header {

	/**
	 * ELF Identification 00 - 0F(32、64 bit)
	 */
	private byte[] e_ident;
	/**
	 * object file type 10 - 11(32、64 bit)
	 */
	private byte[] e_type;
	/**
	 * target machine 12 - 13(32、64 bit)
	 */
	private byte[] e_machine;
	/**
	 * object file version 14 - 17(32、64 bit)
	 */
	private byte[] e_version;
	/**
	 * virtual entry point , it's doesn't work when ELF is a shared object 18 -
	 * 1B (64bit 18 - 1F)
	 */
	private byte[] e_entry;
	/**
	 * program header table offset 1C - 1F (64 bit 20 - 27)
	 */
	private byte[] e_phoff;
	/**
	 * section hreader table offset 20 - 23 (64 bit 28 - 2F)
	 */
	private byte[] e_shoff;
	/**
	 * processor-specific flags 24 - 27 (64 bit 30 - 33)
	 */
	private byte[] e_flags;
	/**
	 * ELF header size(the size of this class) 28 - 29 (64 bit 34 - 35)
	 */
	private byte[] e_ehsize;
	/**
	 * program hreader entry size 2A - 2B (64 bit 36 - 37)
	 */
	private byte[] e_phentsize;
	/**
	 * number of program header entries 2C - 2D (64 bit 38 - 39)
	 */
	private byte[] e_phnum;
	/**
	 * sectin hreader entry size 2E - 2F (64 bit 3A - 3B)
	 */
	private byte[] e_shentsize;
	/**
	 * number of section header entries 30 - 31 (64 bit 3C - 3D)
	 */
	private byte[] e_shnum;
	/**
	 * section header table's string table entry offset 32 - 33 (64 bit 3E - 3F)
	 */
	private byte[] e_shstrndex;

	private byte[] mInternalMMap;

	public ELF_Header(RandomAccessFile is) throws Exception {

		Log.e(Constant.DIVISION_LINE);
		Log.e(Constant.ELF_HEADER);
		Log.e(Constant.DIVISION_LINE);

		verifyElfMagicCode(is);

		if (is32Bit())
			read32BitHeader(is);
		else
			read64BitHeader(is);

		readELFHeader();

		mInternalMMap = toCompletelyHeader();
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

	private void read64BitHeader(RandomAccessFile is) throws Exception {

		e_type = new byte[ELF64_Half];
		is.read(e_type);

		e_machine = new byte[ELF64_Half];
		is.read(e_machine);

		e_version = new byte[ELF64_Word];
		is.read(e_version);

		e_entry = new byte[ELF64_Addr];
		is.read(e_entry);

		e_phoff = new byte[ELF64_Off];
		is.read(e_phoff);

		e_shoff = new byte[ELF64_Off];
		is.read(e_shoff);

		e_flags = new byte[ELF64_Word];
		is.read(e_flags);

		e_ehsize = new byte[ELF64_Half];
		is.read(e_ehsize);

		e_phentsize = new byte[ELF64_Half];
		is.read(e_phentsize);

		e_phnum = new byte[ELF64_Half];
		is.read(e_phnum);

		e_shentsize = new byte[ELF64_Half];
		is.read(e_shentsize);

		e_shnum = new byte[ELF64_Half];
		is.read(e_shnum);

		e_shstrndex = new byte[ELF64_Half];
		is.read(e_shstrndex);

	}

	private void verifyElfMagicCode(RandomAccessFile is) throws Exception {

		e_ident = new byte[EI_NIDENT];
		is.read(e_ident);

		if (!Util.equals(StandardELFMagicCode, 0, e_ident, 0,
				StandardELFMagicCode.length))
			throw new Exception("Not a ELF File");

		switch (e_ident[EI_CALSS]) {
		case ELFCLASS32:
			Log.i("for 32 bit machine");
			break;

		case ELFCLASS64:
			Log.i("for 64 bit machine");
			break;

		default:
			throw new Exception("ELF illegal class file , EI_CLASS = "
					+ Util.byte2Hex(e_ident[EI_CALSS]));
		}

		switch (e_ident[EI_DATA]) {
		case ELFDATA2LSB:
			Log.i("for little endian machine");
			break;

		case ELFDATA2MSB:
			Log.i("for big endian machine");

		default:
			throw new Exception("Unknown target endian machine");
		}

		if (e_ident[EI_VERSION] != EV_CURRENT)
			throw new Exception("Unknown ELF Version");

	}

	public void readELFHeader() throws Exception {

		Log.e();

		switch (Util.bytes2Int32(e_type, isLittleEndian())) {
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
			throw new Exception("Unknown ELF Type While reading ELF Header");
		}

		switch (Util.bytes2Int32(e_machine, isLittleEndian())) {
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
			throw new Exception(
					"Unsupport processor Architecture for this ELF !");
		}

		if (Util.bytes2Int32(e_version, isLittleEndian()) != EV_CURRENT)
			throw new Exception("Unknown ELF Version");

		Log.e();

		Log.e("ELF Enter Entry : " + Util.bytes2Hex(e_entry));

		Log.e("Program Header Table Offset : " + Util.bytes2Hex(e_phoff)
				+ (isLittleEndian() ? " Little Endian" : " Big Endian"));
		Log.e("Program Header Table per enitity's size : "
				+ Util.bytes2Int32(e_phentsize, isLittleEndian()) + " B");
		Log.e("Program Header Table total enitities : "
				+ Util.bytes2Int32(e_phnum, isLittleEndian()));

		Log.e();

		Log.e("Section Header Table Offset : " + Util.bytes2Hex(e_shoff)
				+ (isLittleEndian() ? " Little Endian" : " Big Endian"));
		Log.e("Section Header Table per enitity's size : "
				+ Util.bytes2Int32(e_shentsize, isLittleEndian()) + " B");
		Log.e("Section Header Table total enitities : "
				+ Util.bytes2Int32(e_shnum, isLittleEndian()));
		Log.e("Section Header Table's \"String Table\" Index : "
				+ Util.bytes2Hex(e_shstrndex));

		Log.e();

		Log.e("ELF Header Flags : " + Util.bytes2Hex(e_flags));
		Log.e("ELF Header totally size : "
				+ Util.bytes2Int32(e_ehsize, isLittleEndian()) + " B");

	}

	public boolean isSharedObject() {
		return Util.bytes2Int32(e_type, isLittleEndian()) == ET_DYN;
	}

	public boolean isExeutable() {
		return Util.bytes2Int32(e_type, isLittleEndian()) == ET_EXEC;
	}

	public boolean is32Bit() {
		return e_ident[EI_CALSS] == ELFCLASS32;
	}

	public boolean isLittleEndian() {
		return e_ident[EI_DATA] == ELFDATA2LSB;
	}

	public long getELFEntry() {
		if (is32Bit())
			return Util.bytes2Int32(e_entry, isLittleEndian());
		return Util.bytes2Int64(e_entry, isLittleEndian());
	}

	public long getProgramHeaderTableOffset() {
		if (is32Bit())
			return Util.bytes2Int32(e_phoff, isLittleEndian());
		return Util.bytes2Int64(e_phoff, isLittleEndian());
	}

	public int getProgramHeaderTableEntrySize() {
		return Util.bytes2Int32(e_phentsize, isLittleEndian());
	}

	public int getProgramHeaderTableNum() {
		return Util.bytes2Int32(e_phnum, isLittleEndian());
	}

	public long getSectionHeaderTableOffset() {
		if (is32Bit())
			return Util.bytes2Int32(e_shoff, isLittleEndian());
		return Util.bytes2Int64(e_shoff, isLittleEndian());
	}

	public int getSectionHeaderTableEntrySize() {
		return Util.bytes2Int32(e_shentsize, isLittleEndian());
	}

	public int getSectionHeaderNum() {
		return Util.bytes2Int32(e_shnum, isLittleEndian());
	}

	public int getSectionStringIndex() {
		return Util.bytes2Int32(e_shstrndex, isLittleEndian());
	}

	/**
	 * we would like to assume that an elf wouldn't larger than 4GB
	 */
	public int getHeaderSize() {
		return Util.bytes2Int32(e_ehsize, isLittleEndian());
	}

	public byte[] toCompletelyHeader() {

		BytesBuilder bb = new BytesBuilder(Util.bytes2Int32(e_ehsize,
				isLittleEndian()));
		bb.append(e_ident).append(e_type).append(e_machine).append(e_version)
				.append(e_entry).append(e_phoff).append(e_shoff)
				.append(e_flags).append(e_ehsize).append(e_phentsize)
				.append(e_phnum).append(e_shentsize).append(e_shnum)
				.append(e_shentsize);

		return bb.trim2Bytes();

	}

	@Override
	public String toString() {
		return String
				.format("[e_ident = %s\n e_type = %s\n e_machine = %s\n e_version = %s\n e_entry = %s\n e_phoff = %s\n e_shoff = %s\n e_flags = %s\n e_ehsize = %s\n e_phentsize = %s\n e_phnum = %s\n e_shentsize = %s\n e_shnum = %s\n e_shstrndex = %s]",
						Util.bytes2Hex(e_ident), Util.bytes2Hex(e_type),
						Util.bytes2Hex(e_machine), Util.bytes2Hex(e_version),
						Util.bytes2Hex(e_entry), Util.bytes2Hex(e_phoff),
						Util.bytes2Hex(e_shoff), Util.bytes2Hex(e_flags),
						Util.bytes2Hex(e_ehsize), Util.bytes2Hex(e_phentsize),
						Util.bytes2Hex(e_phnum), Util.bytes2Hex(e_shentsize),
						Util.bytes2Hex(e_shnum), Util.bytes2Hex(e_shstrndex));
	}

}
