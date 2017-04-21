package com.marik.arm.OpCode.thumb16.instruction;

import com.marik.arm.OpCode.thumb16.instruction.factory.ParseSupport;

public class STM_A8_664 implements ParseSupport {

	public static final STM_A8_664 INSTANCE = new STM_A8_664();

	public String parse(int data) {
		return "STM_A8_664";
	}

}
