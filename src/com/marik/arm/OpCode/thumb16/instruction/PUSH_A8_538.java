package com.marik.arm.OpCode.thumb16.instruction;

import com.marik.arm.OpCode.OpUtil;
import com.marik.arm.OpCode.thumb16.instruction.factory.ParseSupport;

public class PUSH_A8_538 extends ParseSupport {

	public static final PUSH_A8_538 INSTANCE = new PUSH_A8_538();

	@Override
	public String parse(int data) {
		
		int M = OpUtil.getShiftInt(data, 8, 1);
		int registerList = OpUtil.getShiftInt(data, 0, 7);
		
		StringBuilder sb = new StringBuilder("PUSH ");
		sb.append("{");
		
		sb.append(OpUtil.parseRigisterBit(registerList, -1));
		if(M == 1)
			sb.append(" , LR");
		sb.append("}");
		
		return sb.toString();
	}

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
