package com.marik.arm.OpCode.thumb16.instruction;

import com.marik.arm.OpCode.thumb16.instruction.factory.ParseSupport;

public class LDR_A8_410  implements ParseSupport{

	public static final LDR_A8_410 INSTANCE = new LDR_A8_410();

	public String parse(int data) {
		return "LDR_A8_410";
	}

}
