/*-------------------------------
 Auto Generated By AutoGenetate.java
     Don't remove or modify
        License GPL/GNU
-------------------------------*/
package brilliant.arm.OpCode.thumb.instruction32;

import static brilliant.arm.OpCode.factory.OpUtil.getShiftInt;
import static brilliant.arm.OpCode.factory.OpUtil.thumbExpandImm;
import brilliant.arm.OpCode.thumb.instruction32.support.ParseSupport;

public class TEQ_A8_738 extends ParseSupport {

	public static final TEQ_A8_738 INSTANCE = new TEQ_A8_738();

	@Override
	protected String getOpCode(int data) {
		return "TEQ";
	}

	@Override
	protected int getRn(int data) {
		return getShiftInt(data, 16, 4);
	}

	@Override
	protected int getShift(int data) {
		return thumbExpandImm(getShiftInt(data, 26, 1) << 11
				| getShiftInt(data, 12, 3) << 8 | getShiftInt(data, 0, 8));
	}

	@Override
	public void performExecuteCommand(int data) {
	}

}