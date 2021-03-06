/*-------------------------------
 Auto Generated By AutoGenetate.java
     Don't remove or modify
        License GPL/GNU
-------------------------------*/
package brilliant.arm.OpCode.thumb.instruction32;

import static brilliant.arm.OpCode.factory.OpUtil.getShiftInt;
import static brilliant.arm.OpCode.factory.OpUtil.signExtend;
import brilliant.arm.OpCode.thumb.instruction32.support.ParseSupport;

public class B_A8_334 extends ParseSupport {

	public static final B_A8_334 INSTANCE = new B_A8_334();

	@Override
	public String parse(int data) {
		int type = getShiftInt(data, 12, 1);

		if (type == 0b0)
			return EncodingT3(data);
		else
			return EncodingT4(data);
	}

	public String EncodingT3(int data) {
		return super.parse(data);
	}

	public String EncodingT4(int data) {

		StringBuilder sb = new StringBuilder("B.W #");
		int S = getShiftInt(data, 26, 1);
		int I1 = ~(getShiftInt(data, 13, 1) ^ S) & 1;
		int I2 = ~(getShiftInt(data, 11, 1) ^ S) & 1;
		int imm10 = getShiftInt(data, 16, 10);
		int imm11 = getShiftInt(data, 0, 11);
		sb.append(signExtend((S << 24) | (I1 << 23) | (I2 << 22)
				| (imm10 << 12) | (imm11 << 1), 25));

		return sb.toString();
	}

	@Override
	protected String getOpCode(int data) {
		return "B";
	}

	protected int getCond(int data) {
		return getShiftInt(data, 22, 4);
	};

	@Override
	protected int getShift(int data) {
		int S = getShiftInt(data, 26, 1);
		int J1 = getShiftInt(data, 13, 1);
		int J2 = getShiftInt(data, 11, 1);
		int imm6 = getShiftInt(data, 16, 6);
		int imm11 = getShiftInt(data, 0, 11);
		return signExtend((S << 20) | (J2 << 19) | (J1 << 18) | (imm6 << 12)
				| (imm11 << 1), 21);
	}

	@Override
	public void performExecuteCommand(int data) {
	}

}