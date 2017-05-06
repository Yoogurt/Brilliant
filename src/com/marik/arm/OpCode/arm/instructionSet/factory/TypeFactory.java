package com.marik.arm.OpCode.arm.instructionSet.factory;

public class TypeFactory {

	private static final String[] TYPE = { "LSL", "LSR", "ASR", "ROR" };

	public static String parse(int type) {
		return TYPE[type];
	}

}
