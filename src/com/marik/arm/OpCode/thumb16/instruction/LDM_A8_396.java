package com.marik.arm.OpCode.thumb16.instruction;

import com.marik.arm.OpCode.OpUtil;
import com.marik.arm.OpCode.thumb16.instruction.factory.ParseSupport;

public class LDM_A8_396 extends ParseSupport {
	public static final LDM_A8_396 INSTANCE = new LDM_A8_396();

	@Override
	protected String getOpCode() {
		return "LDM";
	}

	@Override
	protected String getRn(int data) {
		int Rn = OpUtil.getShiftInt(data, 8, 3);
		int registerList = OpUtil.getShiftInt(data, 0, 8);

		if (!OpUtil.isRigisterInRegisterList(Rn, registerList))
			return OpUtil.parseRegister(Rn) + "!";
		return OpUtil.parseRegister(Rn);
	}

	@Override
	protected String getRm(int data) {
		return OpUtil.parseRigisterBit(OpUtil.getShiftInt(data, 0, 8), OpUtil.getShiftInt(data, 8, 3));
	}

	@Override
	protected boolean isRmRegisterList() {
		return true;
	}

}
