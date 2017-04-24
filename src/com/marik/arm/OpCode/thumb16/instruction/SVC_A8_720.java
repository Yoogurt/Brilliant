package com.marik.arm.OpCode.thumb16.instruction;

import com.marik.arm.OpCode.thumb16.instruction.factory.ParseSupport;

public class SVC_A8_720 extends ParseSupport {

	public static final SVC_A8_720 INSTANCE = new SVC_A8_720();
	
	@Override
	protected String getOpCode() {
		return null;
	}

	@Override
	protected String getRn(int data) {
		return null;
	}

	@Override
	protected String getRm(int data) {
		return null;
	}

}
