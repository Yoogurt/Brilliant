package com.marik.arm.OpCode.thumb.instructionSet16;

import com.marik.arm.OpCode.OpUtil;
import com.marik.arm.OpCode.thumb.instruction16.IT_A8_390;
import com.marik.arm.OpCode.thumb.instruction16.NOP_A8_510;
import com.marik.arm.OpCode.thumb.instruction16.SEV_A8_606;
import com.marik.arm.OpCode.thumb.instruction16.WFE_A8_1104;
import com.marik.arm.OpCode.thumb.instruction16.WFI_A8_1106;
import com.marik.arm.OpCode.thumb.instruction16.YIELD_A8_1108;
import com.marik.arm.OpCode.thumb.instruction16.support.ParseSupport;

class IfThenHint_A6_229 {

	public static ParseSupport parse(int data) {
		int opA = OpUtil.getShiftInt(data, 4, 4);
		int opB = OpUtil.getShiftInt(data, 0, 4);

		if (opB != 0b0000)
			return IT_A8_390.INSTANCE;

		switch (opA) {
		case 0b0000:
			return NOP_A8_510.INSTANCE;
		case 0b0001:
			return YIELD_A8_1108.INSTANCE;
		case 0b0010:
			return WFE_A8_1104.INSTANCE;
		case 0b0011:
			return WFI_A8_1106.INSTANCE;
		case 0b0100:
			return SEV_A8_606.INSTANCE;
		default:
			throw new IllegalArgumentException("Unable to decode " + Integer.toBinaryString(data));
		}
	}
}
