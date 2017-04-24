package com.marik.arm.OpCode.thumb16.instruction.factory;

import com.marik.arm.OpCode.arm.instructionSet.factory.ConditionParseFactory;

public abstract class ParseSupport {

	public String parse(int data) {

		data &= 0xffff;

		verify(data);

		StringBuilder sb = new StringBuilder(getOpCode());

		if (enableCond())
			sb.append(ConditionParseFactory.parseCondition(getCond()));

		sb.append(" ");

		sb.append(getRn(data));

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
			}else sb.append(getRm(data));
		}

		return sb.toString();
	}

	protected abstract String getOpCode();

	protected abstract String getRn(int data);

	protected abstract String getRm(int data);

	protected void verify(int data) {

	}

	protected boolean isRmRegisterList() {
		return false;
	}

	protected boolean enableCond() {
		return false;
	}

	protected int getCond() {
		return 0;
	}

	protected boolean isRmMenory() {
		return false;
	}
}
