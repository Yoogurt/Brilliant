package com.marik.util;

import com.marik.elf.ELF;
import com.marik.elf.ELF_Header;
import com.marik.vm.OS;

public class OpCode {

	public static String decode(byte[] data) {
		return decode(Util.bytes2Int32(data));
	}

	public static String decode(int data) {

		String cond = parseCond(data);

		return "Unknown OpCode";
	}

	private static String parseCond(int data) {
		int cond = getShiftInt(data, 21, 4);
		switch (cond) {
		case 0xC:
			return "LDR";

		default:
			return "???";
		}
	}

	private static int getShiftInt(int data, int from, int length) {
		return (data >> from) & ((1 << length) - 1);
	}

	public static void main(String[] args) throws Exception {

		Log.DEBUG = false;
		OS.debug = false;

		ELF elf = ELF.decode("C:\\Users\\monitor\\Desktop\\test");

		ELF_Header header = elf.elf_header;
		int entry = (int) header.getELFEntry();
		int offset = 0;
		System.out.println(Util.bytes2Hex(OS.getMemory(), entry + offset, 4));
		System.out.println(Util.bytes2Hex(OS.getMemory(), entry + 8, 4));
	}

}
