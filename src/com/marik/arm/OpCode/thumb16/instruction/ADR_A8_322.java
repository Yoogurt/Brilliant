package com.marik.arm.OpCode.thumb16.instruction;

import com.marik.arm.OpCode.OpUtil;
import com.marik.arm.OpCode.thumb16.instruction.factory.ParseSupport;
/**
 * PC = current + 4 + imm8
 */
public class ADR_A8_322 extends ParseSupport {
	public static final ADR_A8_322 INSTANCE = new ADR_A8_322();

	@Override
	protected String getOpCode() {
		return "ADR";
	}

	@Override
	protected String getRn(int data) {
		return OpUtil.parseRegister(OpUtil.getShiftInt(data, 8, 3));
	}

	@Override
	protected String getRm(int data) {
		return "#" + OpUtil.zeroExtend(OpUtil.getShiftInt(data, 0, 8), 2);
	}
}
