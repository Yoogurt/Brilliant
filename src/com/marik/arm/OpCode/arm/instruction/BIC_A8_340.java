/*-------------------------------
 Auto Generated By AutoGenetate.java
     Don't remove or modify
        License GPL/GNU
-------------------------------*/
package com.marik.arm.OpCode.arm.instruction;

import static com.marik.arm.OpCode.OpUtil.armExpandImm;
import static com.marik.arm.OpCode.OpUtil.getShiftInt;

import com.marik.arm.OpCode.arm.instruction.support.ParseSupport;

public class BIC_A8_340 extends ParseSupport {

	public static final BIC_A8_340 INSTANCE = new BIC_A8_340();

	@Override
	protected String getOpCode(int data) {
		return "BIC";
	}

	@Override
	protected int getRd(int data) {
		return getShiftInt(data, 12, 4);
	}

	@Override
	protected int getRn(int data) {
		return getShiftInt(data, 16, 4);
	}

	@Override
	protected int getRm(int data) {
		return -1;
	}

	@Override
	protected int getS(int data) {
		return getShiftInt(data, 20, 1);
	}

	@Override
	protected int getType(int data) {
		return -1;
	}

	@Override
	protected int getShift(int data) {
		return armExpandImm(getShiftInt(data, 0, 12));
	}

	@Override
	public void performExecuteCommand(int data) {
	}

}