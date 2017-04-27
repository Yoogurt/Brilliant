package com.marik.arm.OpCode.thumb16.instruction.factory;

import com.marik.arm.OpCode.ParseTemplate;
import static com.marik.vm.Register.*;
import com.marik.arm.OpCode.arm.instructionSet.factory.ConditionParseFactory;

public abstract class ParseSupport implements ParseTemplate {

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
			} else
				sb.append(getRm(data));
		}

		return sb.toString();
	}

	protected String getOpCode() {
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
		return 0;
	}

	protected boolean isRmMenory() {
		return false;
	}

	protected String error(int data) {
		throw new IllegalArgumentException("Unable to decode instruction " + Integer.toBinaryString(data));
	}

	public abstract void performExecuteCommand(int data);

	protected final void setRegister(int reigster, int value) {
		switch (reigster) {
		case 1:
			R1 = value;
			break;
		case 2:
			R2 = value;
			break;
		case 3:
			R3 = value;
			break;
		case 4:
			R4 = value;
			break;
		case 5:
			R5 = value;
			break;
		case 6:
			R6 = value;
			break;
		case 7:
			R7 = value;
			break;
		case 8:
			R8 = value;
			break;
		case 9:
			R9 = value;
			break;
		case 10:
			R10 = value;
			break;
		case 11:
			R11 = value;
			break;
		case 12:
			R12 = value;
			break;
		case 13:
			SP = value;
			break;
		case 14:
			LR = value;
			break;
		case 15:
			PC = value;
			break;
		default:
			throw new ThumbRuntimeException("Unable to parse register " + reigster);
		}
	}

	protected final int getRegister(int reigster) {
		switch (reigster) {
		case 1:
			return R1;
		case 2:
			return R2;
		case 3:
			return R3;
		case 4:
			return R4;
		case 5:
			return R5;
		case 6:
			return R6;
		case 7:
			return R7;
		case 8:
			return R8;
		case 9:
			return R9;
		case 10:
			return R10;
		case 11:
			return R11;
		case 12:
			return R12;
		case 13:
			return SP;
		case 14:
			return LR;
		case 15:
			return PC;
		default:
			throw new ThumbRuntimeException("Unable to parse register " + reigster);
		}
	}
}
