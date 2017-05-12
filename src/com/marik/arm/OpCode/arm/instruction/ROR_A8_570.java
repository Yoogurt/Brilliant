/*-------------------------------
 Auto Generated By AutoGenetate.java
     Don't remove or modify
        License GPL/GNU
-------------------------------*/
package com.marik.arm.OpCode.arm.instruction;

import static com.marik.arm.OpCode.OpUtil.getShiftInt;

import com.marik.arm.OpCode.arm.instruction.support.ParseSupport;

public class ROR_A8_570 extends ParseSupport {

	public static final ROR_A8_570 INSTANCE = new ROR_A8_570();

	@Override
	protected String getOpCode(int data) {
		return "ROR";
	}

	@Override
	protected int getRd(int data) {
		return getShiftInt(data, 12, 4);
	}

	@Override
	protected int getRn(int data) {
		return getShiftInt(data, 0, 4);
	}

	@Override
	protected int getRm(int data) {
		return getShiftInt(data, 8, 4);
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
		return 0;
	}

	@Override
	public void performExecuteCommand(int data) {
	}

}