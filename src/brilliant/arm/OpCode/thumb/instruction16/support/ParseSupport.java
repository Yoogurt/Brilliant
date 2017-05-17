package brilliant.arm.OpCode.thumb.instruction16.support;

import brilliant.arm.OpCode.arm.instructionSet.ConditionParseFactory;
import brilliant.arm.OpCode.factory.ParseTemplate;

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

		String comment = getComment(data);
		if (comment != null)
			sb.append(" ").append(comment);

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

	protected String getComment(int data) {
		return null;
	}

	protected String error(int data) {
		throw new IllegalArgumentException("Unable to decode instruction "
				+ Integer.toBinaryString(data));
	}

	public abstract void performExecuteCommand(int data);
}
