package com.marik.arm.OpCode.thumb16.instruction;

import com.marik.arm.OpCode.thumb16.instruction.factory.ParseSupport;

public class ADR_A8_322 implements ParseSupport {
	public static final ADR_A8_322 INSTANCE = new ADR_A8_322();

	public String parse(int data) {
		return "ADR_A8_322";
	}
}
