package com.marik.arm.OpCode.arm.instruction.factory;

import com.marik.arm.OpCode.CondFactory;
import com.marik.arm.OpCode.ParseTemplate;

import static com.marik.arm.OpCode.OpUtil.*;

import com.marik.arm.OpCode.arm.instructionSet.factory.ConditionParseFactory;
import com.marik.arm.OpCode.arm.instructionSet.factory.TypeFactory;

public abstract class ParseSupport implements ParseTemplate {

	public String parse(int data) {

		String jump = verify(data);
		if (jump != null)
			return jump;

		StringBuilder sb = new StringBuilder(getOpCode());

		int cond = getCond(data);
		if (cond != -1)
			sb.append(CondFactory.parse(getCond(data)));

		if (getS(data) == 0b1)
			sb.append("S");

		int Rd = getRd(data);
		int Rn = getRn(data);
		int Rm = getRm(data);

		sb.append(" ");
		if (Rd != -1)
			sb.append(parseRegister(getRd(data)));

		if (Rn != -1) {
			if (Rd != -1)
				sb.append(" , ");
			sb.append(parseRegister(Rn));
		}

		if (Rm != -1) {
			if (Rd != -1 || Rn != -1)
				sb.append(" , ");
			sb.append(parseRegister(Rm));

			int imm5 = getShift(data);
			int type = getType(data);

			if (imm5 > 0) {
				if (type >= 0) {
					sb.append("{");
					sb.append(TypeFactory.parse(type));
					if (!shifterRegister())
						sb.append(" , #").append(imm5).append("}");
					else if (shifterRegisterList())
						sb.append(parseRigisterBit(imm5, -1)).append("}");
					else
						sb.append(" , ").append(parseRegister(imm5))
								.append("}");
				} else
					sb.append(" , #").append(imm5);
			}
		}
		return sb.toString();
	}

	protected String getOpCode() {
		return null;
	}

	protected int getRd(int data) {
		return -1;
	}

	protected int getRn(int data) {
		return -1;
	}

	protected int getRm(int data) {
		return -1;
	}

	protected String verify(int data) {
		return null;
	}

	protected int getCond(int data) {
		return getShiftInt(data, 28, 4);
	}

	protected String error(int data) {
		throw new IllegalArgumentException("Unable to decode instruction "
				+ Integer.toBinaryString(data) +" at "+ getClass().getSimpleName().split("_")[0]);
	}

	protected int getS(int data) {
		return 1;
	}

	protected int getType(int data) {
		return -1;
	}

	protected int getShift(int data) {
		return -1;
	}

	protected boolean shifterRegister() {
		return false;
	}

	protected boolean shifterRegisterList() {
		return false;
	}

	public abstract void performExecuteCommand(int data);
}
