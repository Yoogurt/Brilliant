package com.marik.util;

import com.marik.elf.ELF;
import com.marik.elf.ELF_Header;
import com.marik.vm.OS;

public class OpCode {

	private static final String[] COND_CODE = { "EQ", "NE", "CS", "CC", "MI", "PL", "VS", "VC", "HI", "LS", "GE", "LT",
			"GT", "LE", "AL", "??" };

	private static final String[] OP_CODE = { "MOV", "MVN", "ADD", "SUB", "RSB", "ADC", "SBC", "RSC", "AND", "ORR",
			"EOR", "BIC", "CMP", "CMN", "TST", "TEQ" };

	public static String decode(byte[] data) {
		return decode(Util.bytes2Int32(data));
	}

	public static String decode(int data) {

		String cond = parseCond(data);
		String opcode = parseOpcode(data);

		System.out.println(Util.bytes2Hex(Util.int2bytes(data)));
		return new StringBuilder().append(opcode).append(cond).toString();
	}

	private static String parseCond(int data) {
		int cond = getShiftInt(data, 28, 4);
		return COND_CODE[cond];
	}

	private static String parseOpcode(int data) {
		int opcodeType = getShiftInt(data, 2, 4);

		return OP_CODE[opcodeType];
	}

	private static int getShiftInt(int data, int from, int length) {
		return (data >> from) & ((1 << length) - 1);
	}

	public static void main(String[] args) throws Exception {

		Log.DEBUG = false;
		OS.debug = false;

		ELF elf = ELF.dlopen("C:\\Users\\monitor\\Desktop\\test");

		ELF_Header header = elf.elf_header;
		int entry = (int) header.getELFEntry();
		int offset = 0;
		System.out.println(decode(Util.bytes2Int32(OS.getMemory(), entry + offset, 4, true)));
		System.out.println(decode(Util.bytes2Int32(OS.getMemory(), entry + 8, 4, true)));
	}

}
