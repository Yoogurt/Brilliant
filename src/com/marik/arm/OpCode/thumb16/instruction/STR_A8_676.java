package com.marik.arm.OpCode.thumb16.instruction;

import com.marik.arm.OpCode.OpUtil;
import com.marik.arm.OpCode.thumb16.instruction.factory.ParseSupport;

public class STR_A8_676 extends ParseSupport {

	public static final STR_A8_676 INSTANCE = new STR_A8_676();

	@Override
	protected String getOpCode() {
		return "STR";
	}

	@Override
	protected String getRn(int data) {
		return OpUtil.parseRegister(OpUtil.getShiftInt(data, 0, 3));
	}

	@Override
	protected String getRm(int data) {
		return OpUtil.parseRegister(OpUtil.getShiftInt(data, 3, 3)) + " , "
				+ OpUtil.parseRegister(OpUtil.getShiftInt(data, 6, 3));
	}

	@Override
	protected boolean isRmMenory() {
		return true;
	}

}
