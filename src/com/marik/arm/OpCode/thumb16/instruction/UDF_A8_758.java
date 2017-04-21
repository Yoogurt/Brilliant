package com.marik.arm.OpCode.thumb16.instruction;

import com.marik.arm.OpCode.thumb16.instruction.factory.ParseSupport;

public class UDF_A8_758 implements ParseSupport {
	
	public static final UDF_A8_758 INSTANCE = new UDF_A8_758();

	@Override
	public String parse(int data) {
		return "UDF_A8_758";
	}

}
