package com.marik.arm.OpCode.thumb.instruction32.support;

import static com.marik.arm.OpCode.OpUtil.getShiftInt;
import static com.marik.arm.OpCode.OpUtil.parseRegister;
import static com.marik.arm.OpCode.OpUtil.parseRegisterList;

import com.marik.arm.OpCode.CondFactory;
import com.marik.arm.OpCode.ParseTemplate;
import com.marik.arm.OpCode.arm.instructionSet.factory.TypeFactory;

public abstract class ParseSupport implements ParseTemplate {

	public String parse(int data) {

		String jump = verify(data);
		if (jump != null)
			return jump;

		StringBuilder sb = new StringBuilder(getOpCode(data));

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

			if (isRnMemory())
				sb.append("[");
			sb.append(parseRegister(Rn));
			if (isRnMemory())
				sb.append("]");

			if (isRnwback(data)) {
				sb.append("!");
			}
		}

		if (Rm != -1) {
			if (Rd != -1 || Rn != -1)
				sb.append(" , ");
			sb.append(parseRegister(Rm)).append(" ");
		}

		int imm5 = getShift(data);
		int type = getType(data);

		if (imm5 != 0) {
			if (type >= 0) {
				sb.append(TypeFactory.parse(type));
				parseShift(sb, imm5, true , Rd == -1 && Rn == -1 && Rm == -1 && type == -1);
			} else
				parseShift(sb, imm5, false , Rd == -1 && Rn == -1 && Rm == -1 && type == -1);
		}
		String comment = getCommnet(data);

		if (comment != null)
			sb.append(comment);

		return sb.toString();
	}

	private void parseShift(StringBuilder sb, int imm5, boolean type,
			boolean dot) {
		if (type)
			sb.append(" ");
		else {
			if (!dot)
				sb.append(" , ");
			if (shifterRegister())
				sb.append(parseRegister(imm5));
			else if (shifterRegisterList())
				sb.append("{").append(parseRegisterList(imm5, -1)).append("}");
			else if (shifterMenory())
				sb.append("[").append(parseRegister(imm5)).append("]");
			else
				sb.append("#").append(imm5);
		}
	}

	protected String getOpCode(int data) {
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
				+ Integer.toBinaryString(data) + " at "
				+ getClass().getSimpleName().split("_")[0]);
	}

	protected int getS(int data) {
		return -1;
	}

	protected int getType(int data) {
		return -1;
	}

	protected int getShift(int data) {
		return 0;
	}

	protected boolean shifterRegister() {
		return false;
	}

	protected boolean shifterRegisterList() {
		return false;
	}

	protected boolean shifterMenory() {
		return false;
	}

	protected boolean isRnMemory() {
		return false;
	}

	protected String getCommnet(int data) {
		return null;
	}

	protected boolean isRnwback(int data) {
		return false;
	}

	public abstract void performExecuteCommand(int data);
}
