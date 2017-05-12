/*-------------------------------
 Auto Generated By AutoGenetate.java
     Don't remove or modify
        License GPL/GNU
-------------------------------*/
package com.marik.arm.OpCode.arm.instruction;

import static com.marik.arm.OpCode.OpUtil.getShiftInt;

import com.marik.arm.OpCode.arm.instruction.support.ParseSupport;

public class CMP_A8_372 extends ParseSupport {

	public static final CMP_A8_372 INSTANCE = new CMP_A8_372();

	@Override
	protected String getOpCode(int data) {
		return "CMP";
	}

	@Override
	protected int getRd(int data) {
		return -1;
	}

	@Override
	protected int getRn(int data) {
		return getShiftInt(data, 16, 4);
	}

	@Override
	protected int getRm(int data) {
		return getShiftInt(data, 0, 4);
	}

	@Override
	protected int getS(int data) {
		return -1;
	}

	@Override
	protected int getType(int data) {
		return getShiftInt(data, 5, 2);
	}

	@Override
	protected int getShift(int data) {
		return getShiftInt(data, 7, 5);
	}

	@Override
	public void performExecuteCommand(int data) {
	}

}