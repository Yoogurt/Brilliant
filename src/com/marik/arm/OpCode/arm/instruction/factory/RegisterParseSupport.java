package com.marik.arm.OpCode.arm.instruction.factory;

import static com.marik.arm.OpCode.OpUtil.getShiftInt;

import com.marik.arm.OpCode.CondFactory;
import com.marik.arm.OpCode.OpUtil;

public abstract class RegisterParseSupport extends ParseSupport {

	public final String parse(int data) {

		int S = getS(data);
		int Rn = getRn(data);
		int Rd = getRd(data);
		int imm5 = getShift(data);
		int type = getType(data);
		int Rm = getRm(data);
		int cond = getShiftInt(data, 28, 4);

		StringBuilder sb = new StringBuilder(getOpCode());

		if (S == 1)
			sb.append("S");

		sb.append(CondFactory.parse(cond));

		sb.append(" ");

		sb.append(OpUtil.parseRegister(Rd));
		sb.append(" , ");
		sb.append(OpUtil.parseRegister(Rn));
		sb.append(" , ");
		sb.append(OpUtil.parseRegister(Rm));

		String shift = ShiftFactory.parse(type, imm5);
		if (shift != null)
			sb.append(" , ").append(shift);

		return sb.toString();

	}

}
