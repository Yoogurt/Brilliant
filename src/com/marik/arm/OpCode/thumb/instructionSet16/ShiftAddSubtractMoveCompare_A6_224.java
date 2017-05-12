package com.marik.arm.OpCode.thumb.instructionSet16;

import static com.marik.arm.OpCode.OpUtil.*;

import com.marik.arm.OpCode.thumb.instruction16.ADD_A8_306;
import com.marik.arm.OpCode.thumb.instruction16.ADD_A8_310;
import com.marik.arm.OpCode.thumb.instruction16.ASR_A8_330;
import com.marik.arm.OpCode.thumb.instruction16.CMP_A8_370;
import com.marik.arm.OpCode.thumb.instruction16.LSL_A8_468;
import com.marik.arm.OpCode.thumb.instruction16.LSR_A8_472;
import com.marik.arm.OpCode.thumb.instruction16.MOV_A8_484;
import com.marik.arm.OpCode.thumb.instruction16.SUB_A8_708;
import com.marik.arm.OpCode.thumb.instruction16.SUB_A8_712;
import com.marik.arm.OpCode.thumb.instruction16.support.ParseSupport;

class ShiftAddSubtractMoveCompare_A6_224 {

	public static ParseSupport parse(int data) {

		int OpCode = getShiftInt(data, 9, 5);

		if (assert0(OpCode, 2, 3, 4))
			return LSL_A8_468.INSTANCE;

		if (assert0(OpCode, 3, 4) && assert1(OpCode, 2))
			return LSR_A8_472.INSTANCE;

		if (assert0(OpCode, 2, 4) && assert1(OpCode, 3))
			return ASR_A8_330.INSTANCE;

		switch (OpCode) {
		case 0b01100:
			return ADD_A8_310.INSTANCE;
		case 0b01101:
			return SUB_A8_712.INSTANCE;
		case 0b01110:
			return ADD_A8_306.INSTANCE;
		case 0b01111:
			return SUB_A8_708.INSTANCE;
		}

		if (assert0(OpCode, 2, 3) && assert1(OpCode, 4))
			return MOV_A8_484.INSTANCE;

		if (assert0(OpCode, 3) && assert1(OpCode, 2, 4))
			return CMP_A8_370.INSTANCE;

		if (assert0(OpCode, 2) && assert1(OpCode, 3, 4))
			return ADD_A8_306.INSTANCE;

		if (assert1(OpCode, 2, 3, 4))
			return SUB_A8_708.INSTANCE;

		throw new IllegalArgumentException("Unable to decode instruction " + Integer.toBinaryString(data));
	}

}
