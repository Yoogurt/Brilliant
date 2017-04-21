package com.marik.arm.OpCode.arm.instruction.factory;

public abstract class ParseSupport {
	protected abstract int getS(int data);

	protected abstract int getRn(int data);

	protected abstract int getRd(int data);

	protected abstract int getShift(int data);

	protected abstract int getType(int data);

	protected abstract int getRm(int data);

	protected abstract String getOpCode();
}
