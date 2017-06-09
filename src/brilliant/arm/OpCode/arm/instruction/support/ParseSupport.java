package brilliant.arm.OpCode.arm.instruction.support;

import static brilliant.arm.OpCode.factory.OpUtil.getShiftInt;
import static brilliant.arm.OpCode.factory.OpUtil.parseRegister;
import static brilliant.arm.OpCode.factory.OpUtil.parseRegisterList;
import brilliant.arm.OpCode.factory.CondFactory;
import brilliant.arm.OpCode.factory.ParseTemplate;
import brilliant.arm.OpCode.factory.TypeFactory;

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

			if (isRmMemory())
				sb.append("[");

			sb.append(parseRegister(Rm));

			if (isRmMemory())
				sb.append("] ");
		}

		int shift = getShift(data);
		int type = getType(data);

		if (!shifterUsed()) {
			if (shift != 0) {
				if (type >= 0)
					sb.append(TypeFactory.parse(type));
				parseShift(sb, shift, type >= 0 || Rm > 0 || Rn > 0 || Rd > 0);
			}
		} else {
			if (type >= 0)
				sb.append(TypeFactory.parse(type));
			parseShift(sb, shift, type >= 0 || Rm > 0 || Rn > 0 || Rd > 0);
		}

		String comment = getCommnet(data);

		if (comment != null)
			sb.append(comment);

		return sb.toString();
	}

	private void parseShift(StringBuilder sb, int imm5, boolean type) {

		sb.append(" ");

		if (type)
			sb.append(", ");

		if (shifterRegister())
			sb.append(parseRegister(imm5));
		else if (shifterRegisterList())
			sb.append("{").append(parseRegisterList(imm5, -1)).append("}");
		else if (shifterMenory())
			sb.append("[").append(parseRegister(imm5)).append("]");
		else
			sb.append("#").append(imm5);
	}

	protected String getOpCode(int data) {
		return getClass().getSimpleName().split("_")[0];
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
		throw new IllegalArgumentException("Unable to decode instruction " + Integer.toBinaryString(data) + " at "
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

	protected boolean isRmMemory() {
		return false;
	}

	protected boolean shifterUsed() {
		return false;
	}

	public abstract void performExecuteCommand(int data);
}
