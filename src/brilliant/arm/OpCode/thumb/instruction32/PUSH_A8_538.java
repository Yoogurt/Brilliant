/*-------------------------------
 Auto Generated By AutoGenetate.java
     Don't remove or modify
        License GPL/GNU
-------------------------------*/
package brilliant.arm.OpCode.thumb.instruction32;

import static brilliant.arm.OpCode.factory.OpUtil.getShiftInt;
import static brilliant.arm.OpCode.factory.OpUtil.parseRegister;
import brilliant.arm.OpCode.thumb.instruction32.support.ParseSupport;

public class PUSH_A8_538 extends ParseSupport {

	public static final PUSH_A8_538 INSTANCE = new PUSH_A8_538();

	public String parse(int data) {

		int type = getShiftInt(data, 27, 5);
		if (type == 0b11101)
			return super.parse(data);
		else
			return EncodingT3(data); // push one register
	}

	private String EncodingT3(int data) {
		StringBuilder sb = new StringBuilder("PUSH.W");
		sb.append(" ");
		sb.append(parseRegister(getShiftInt(data, 12, 4)));
		return sb.toString();
	}

	@Override
	protected String getOpCode(int data) {
		return "PUSH";
	}

	@Override
	protected int getShift(int data) {
		return getShiftInt(data, 0, 16);
	}

	@Override
	protected boolean shifterRegisterList() {
		return true;
	}

	@Override
	public void performExecuteCommand(int data) {
	}

}