package com.marik.arm.OpCode.thumb16.instruction;

import com.marik.arm.OpCode.thumb16.instruction.factory.ParseSupport;

public class ADD_A8_316 implements ParseSupport{

	public static final ADD_A8_316 INSTANCE = new ADD_A8_316();

	public String parse(int data) {
		return "ADD_A8_316";
	}

}
