package com.marik.arm.OpCode.thumb16.instruction;

import com.marik.arm.OpCode.OpUtil;
import com.marik.arm.OpCode.thumb16.instruction.factory.ParseSupport;

public class SUB_A8_716 extends ParseSupport {

	public static final SUB_A8_716 INSTANCE = new SUB_A8_716();

	@Override
	protected String getOpCode() {
		return "SUB";
	}

	@Override
	protected String getRn(int data) {
		return "SP";
	}

	@Override
	protected String getRm(int data) {
		return "SP , #" + (OpUtil.zeroExtend(OpUtil.getShiftInt(data, 0, 7), 2));
	}

}
