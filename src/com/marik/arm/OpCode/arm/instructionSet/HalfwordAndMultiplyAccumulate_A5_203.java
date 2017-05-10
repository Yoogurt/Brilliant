package com.marik.arm.OpCode.arm.instructionSet;

import static com.marik.arm.OpCode.OpUtil.getShiftInt;

import com.marik.arm.OpCode.arm.instruction.SMLABB_A8_620;
import com.marik.arm.OpCode.arm.instruction.SMLALBB_A8_626;
import com.marik.arm.OpCode.arm.instruction.SMLAWB_A8_630;
import com.marik.arm.OpCode.arm.instruction.SMULBB_A8_644;
import com.marik.arm.OpCode.arm.instruction.SMULWB_A8_648;
import com.marik.arm.OpCode.arm.instruction.factory.ParseSupport;

@SuppressWarnings("deprecation")
public class HalfwordAndMultiplyAccumulate_A5_203 {
	public static ParseSupport parse(int data) {

		int op1 = getShiftInt(data, 21, 2);
		int op = getShiftInt(data, 5, 1);

		switch (op1) {
		case 0b00:
			return SMLABB_A8_620.INSTANCE;
		case 0b01:
			if (op == 0b0)
				return SMLAWB_A8_630.INSTANCE;
			if (op == 0b1)
				return SMULWB_A8_648.INSTANCE;
			break;

		case 0b10:
			return SMLALBB_A8_626.INSTANCE;
		case 0b11:
			return SMULBB_A8_644.INSTANCE;
		}

		throw new IllegalArgumentException("Unable to decode instruction "
				+ Integer.toBinaryString(data));
	}
}
