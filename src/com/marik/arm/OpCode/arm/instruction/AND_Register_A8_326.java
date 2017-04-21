package com.marik.arm.OpCode.arm.instruction;

import static com.marik.arm.OpCode.OpUtil.getShiftInt;

import com.marik.arm.OpCode.arm.instruction.factory.RegisterParseSupport;

public class AND_Register_A8_326 extends RegisterParseSupport{
	
	public static final AND_Register_A8_326 INSTANCE = new AND_Register_A8_326();

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
		return getShiftInt(data, 0, 4);
	}

	@Override
	protected int getType(int data) {
		return getShiftInt(data, 5, 2);
	}

	@Override
	protected int getS(int data) {
		return getShiftInt(data, 20, 1);
	}

	@Override
	protected int getShift(int data) {
		return getShiftInt(data, 7, 5);
	}

	@Override
	protected String getOpCode() {
		return "AND";
	}
	
}
