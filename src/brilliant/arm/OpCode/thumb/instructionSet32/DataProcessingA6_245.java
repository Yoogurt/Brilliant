package brilliant.arm.OpCode.thumb.instructionSet32;

import brilliant.arm.OpCode.thumb.instruction32.ASR_A8_332;
import brilliant.arm.OpCode.thumb.instruction32.LSL_A8_470;
import brilliant.arm.OpCode.thumb.instruction32.LSR_A8_474;
import brilliant.arm.OpCode.thumb.instruction32.ROR_A8_570;
import brilliant.arm.OpCode.thumb.instruction32.SXTAB16_A8_726;
import brilliant.arm.OpCode.thumb.instruction32.SXTAB_A8_724;
import brilliant.arm.OpCode.thumb.instruction32.SXTAH_A8_728;
import brilliant.arm.OpCode.thumb.instruction32.SXTB16_A8_732;
import brilliant.arm.OpCode.thumb.instruction32.SXTB_A8_730;
import brilliant.arm.OpCode.thumb.instruction32.SXTH_A8_734;
import brilliant.arm.OpCode.thumb.instruction32.UXTAB16_A8_808;
import brilliant.arm.OpCode.thumb.instruction32.UXTAB_A8_806;
import brilliant.arm.OpCode.thumb.instruction32.UXTAH_A8_810;
import brilliant.arm.OpCode.thumb.instruction32.UXTB16_A8_814;
import brilliant.arm.OpCode.thumb.instruction32.UXTB_A8_812;
import brilliant.arm.OpCode.thumb.instruction32.UXTH_A8_816;
import brilliant.arm.OpCode.thumb.instruction32.support.ParseSupport;
import static brilliant.arm.OpCode.OpUtil.*;

class DataProcessingA6_245 {
	public static ParseSupport parse(int data) {

		int op1 = getShiftInt(data, 20, 4);
		int op2 = getShiftInt(data, 4, 4);
		int Rn = getShiftInt(data, 16, 4);

		if (op1 == 0b0000 || op1 == 0b0001)
			if (op2 == 0b0000)
				return LSL_A8_470.INSTANCE;

		if (op1 == 0b0010 || op1 == 0b0011)
			if (op2 == 0b0000)
				return LSR_A8_474.INSTANCE;

		if (op1 == 0b0100 || op1 == 0b0101)
			if (op2 == 0b0000)
				return ASR_A8_332.INSTANCE;

		if (op1 == 0b0110 || op1 == 0b01111)
			if (op2 == 0b0000)
				return ROR_A8_570.INSTANCE;

		if (op1 == 0b0000)
			if (assert1(op2, 3))
				if (Rn != 0b1111)
					return SXTAH_A8_728.INSTANCE;
				else
					return SXTH_A8_734.INSTANCE;

		if (op1 == 0b0001)
			if (assert1(op2, 3))
				if (Rn != 0b1111)
					return UXTAH_A8_810.INSTANCE;
				else
					return UXTH_A8_816.INSTANCE;

		if (op1 == 0b0010)
			if (assert1(op2, 3))
				if (Rn != 0b1111)
					return SXTAB16_A8_726.INSTANCE;
				else
					return SXTB16_A8_732.INSTANCE;

		if (op1 == 0b0011)
			if (assert1(op2, 3))
				if (Rn != 0b1111)
					return UXTAB16_A8_808.INSTANCE;
				else
					return UXTB16_A8_814.INSTANCE;

		if (op1 == 0b0100)
			if (assert1(op2, 3))
				if (Rn != 0b1111)
					return SXTAB_A8_724.INSTANCE;
				else
					return SXTB_A8_730.INSTANCE;

		if (op1 == 0b0101)
			if (assert1(op2, 3))
				if (Rn != 0b1111)
					return UXTAB_A8_806.INSTANCE;
				else
					return UXTB_A8_812.INSTANCE;

		if (assert1(op1, 3)) {
			if (assert0(op2, 2, 3))
				return ParallelAddition_A6_246.parse(data);

			if (assert0(op2, 3) && assert1(op2, 2))
				return ParallelAddition_A6_247.parse(data);

			if (assert0(op1, 2))
				if (assert0(op2, 2) && assert1(op2, 3))
					return MiscellaneousOperationsA6_248.parse(data);
		}

		throw new IllegalArgumentException("Unable to decode instruction "
				+ Integer.toBinaryString(data));
	}
}
