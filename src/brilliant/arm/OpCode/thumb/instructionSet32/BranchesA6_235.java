package brilliant.arm.OpCode.thumb.instructionSet32;

import brilliant.arm.OpCode.arm.instructionSet.MiscellaneousInstruction_A5_207;
import brilliant.arm.OpCode.thumb.instruction32.BL_A8_348;
import brilliant.arm.OpCode.thumb.instruction32.BXJ_A8_354;
import brilliant.arm.OpCode.thumb.instruction32.B_A8_334;
import brilliant.arm.OpCode.thumb.instruction32.ERET_B9_1982;
import brilliant.arm.OpCode.thumb.instruction32.HVC_B9_1984;
import brilliant.arm.OpCode.thumb.instruction32.MRS_A8_496;
import brilliant.arm.OpCode.thumb.instruction32.MRS_B9_1990;
import brilliant.arm.OpCode.thumb.instruction32.MRS_B9_1992;
import brilliant.arm.OpCode.thumb.instruction32.MSR_A8_500;
import brilliant.arm.OpCode.thumb.instruction32.MSR_B9_1994;
import brilliant.arm.OpCode.thumb.instruction32.MSR_B9_1998;
import brilliant.arm.OpCode.thumb.instruction32.SMC_B9_2002;
import brilliant.arm.OpCode.thumb.instruction32.SUBS_B9_2010;
import brilliant.arm.OpCode.thumb.instruction32.UDF_A8_758;
import brilliant.arm.OpCode.thumb.instruction32.support.ParseSupport;
import static brilliant.arm.OpCode.OpUtil.*;

class BranchesA6_235 {
	public static ParseSupport parse(int data) {

		int op1 = getShiftInt(data, 12, 3);
		int imm8 = getShiftInt(data, 0, 8);
		int op = getShiftInt(data, 20, 7);
		int op2 = getShiftInt(data, 8, 4);

		if (op1 == 0b000 || op1 == 0b010) {
			if (!assert1(op, 3, 4, 5))
				return B_A8_334.INSTANCE;

			if (assert1(imm8, 5))
				if (assert0(op, 1, 2, 6) && assert1(op, 3, 4, 5))
					return MSR_B9_1994.INSTANCE;

			if (assert0(imm8, 5)) {
				if (op == 0b0111000) {
					if (assert0(op2, 0, 1))
						return MSR_A8_500.INSTANCE;
					else if ((assert0(op2, 1) && assert1(op2, 0))
							|| (assert1(op2, 1)))
						return MSR_B9_1998.INSTANCE;
				}

				if (op == 0b0111001)
					return MSR_B9_1998.INSTANCE;
			}

			switch (op) {
			case 0b0111010:
				return ChangeProcessorStateA6_236.parse(data);

			case 0b0111011:
				return MiscellaneousInstruction_A6_237.parse(data);

			case 0b0111100:
				return BXJ_A8_354.INSTANCE;

			case 0b011101:
				if (imm8 == 0b00000000)
					return ERET_B9_1982.INSTANCE;
				else
					return SUBS_B9_2010.INSTANCE;

			case 0b0111110:
				if (assert0(imm8, 5))
					return MRS_A8_496.INSTANCE;
				else
					return MRS_B9_1992.INSTANCE;

			case 0b0111111:
				if (assert0(imm8, 5))
					return MRS_B9_1990.INSTANCE;
				else
					return MRS_B9_1992.INSTANCE;
			}
		}

		if (op1 == 0b000) {
			if (op == 0b1111110)
				return HVC_B9_1984.INSTANCE;

			if (op == 0b1111111)
				return SMC_B9_2002.INSTANCE;
		}

		if (op1 == 0b001 || op1 == 0b011)
			return B_A8_334.INSTANCE;

		if (op1 == 0b010)
			if (op == 0b1111111)
				return UDF_A8_758.INSTANCE;

		if (op1 == 0b100 || op == 0b110)
			return BL_A8_348.INSTANCE;

		if (op1 == 0b101 || op1 == 0b111)
			return BL_A8_348.INSTANCE;

		throw new IllegalArgumentException("Unable to decode instruction "
				+ Integer.toBinaryString(data));
	}
}
