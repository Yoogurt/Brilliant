package com.marik.arm.OpCode.thumb.instruction16.support;

import com.marik.arm.OpCode.ParseTemplate;
import com.marik.arm.OpCode.arm.instructionSet.factory.ConditionParseFactory;

public abstract class ParseSupport implements ParseTemplate {

	public String parse(int data) {

		data &= 0xffff;

		verify(data);

		StringBuilder sb = new StringBuilder(getOpCode(data));

		if (enableCond())
			sb.append(ConditionParseFactory.parseCondition(getCond()));

		sb.append(" ");

		String Rn = getRn(data);
		if (Rn != null)
			sb.append(Rn);

		String Rm = getRm(data);
		if (Rm != null) {
			sb.append(" , ");
			if (isRmRegisterList()) {
				sb.append("{");
				sb.append(getRm(data));
				sb.append("}");
			} else if (isRmMenory()) {
				sb.append("[");
				sb.append(getRm(data));
				sb.append("]");
			} else
				sb.append(getRm(data));
		}

		return sb.toString();
	}

	protected String getOpCode(int data) {
		return null;
	}

	protected String getRn(int data) {
		return null;
	}

	protected String getRm(int data) {
		return null;
	}

	protected void verify(int data) {

	}

	protected boolean isRmRegisterList() {
		return false;
	}

	protected boolean enableCond() {
		return false;
	}

	protected int getCond() {
		return -1;
	}

	protected boolean isRmMenory() {
		return false;
	}

	protected String error(int data) {
		throw new IllegalArgumentException("Unable to decode instruction "
				+ Integer.toBinaryString(data));
	}

	public abstract void performExecuteCommand(int data);
}
