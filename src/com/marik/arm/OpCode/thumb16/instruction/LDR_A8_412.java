package com.marik.arm.OpCode.thumb16.instruction;

import com.marik.arm.OpCode.OpUtil;
import com.marik.arm.OpCode.thumb16.instruction.factory.ParseSupport;

public class LDR_A8_412 extends ParseSupport {

	public static LDR_A8_412 INSTANCE = new LDR_A8_412();
	
	@Override
	protected String getOpCode() {
		return "LDR";
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
