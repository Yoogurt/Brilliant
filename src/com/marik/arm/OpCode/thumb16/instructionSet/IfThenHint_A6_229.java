package com.marik.arm.OpCode.thumb16.instructionSet;

import com.marik.arm.OpCode.OpUtil;
import com.marik.arm.OpCode.thumb16.instruction.IT_A8_390;
import com.marik.arm.OpCode.thumb16.instruction.SEV_A8_606;
import com.marik.arm.OpCode.thumb16.instruction.WFE_A8_1104;
import com.marik.arm.OpCode.thumb16.instruction.WFI_A8_1106;
import com.marik.arm.OpCode.thumb16.instruction.YIELD_A8_1108;

public class IfThenHint_A6_229 {

	public static String parse(int data) {
		int opA = OpUtil.getShiftInt(data, 4, 4);
		int opB = OpUtil.getShiftInt(data, 0, 4);

		if (opB != 0b0000)
			return IT_A8_390.INSTANCE.parse(data);

		switch (opA) {
		case 0b0000:
			return "NOP";
		case 0b0001:
			return YIELD_A8_1108.INSTANCE.parse(data);
		case 0b0010:
			return WFE_A8_1104.INSTANCE.parse(data);
		case 0b0011:
			return WFI_A8_1106.INSTANCE.parse(data);
		case 0b0100:
			return SEV_A8_606.INSTANCE.parse(data);
		default:
			throw new IllegalArgumentException("Unable to decode " + Integer.toBinaryString(data));
		}
	}
}
