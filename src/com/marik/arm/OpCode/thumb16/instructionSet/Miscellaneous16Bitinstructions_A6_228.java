package com.marik.arm.OpCode.thumb16.instructionSet;

import com.marik.arm.OpCode.thumb16.instruction.ADD_A8_316;
import com.marik.arm.OpCode.thumb16.instruction.BKPT_A8_346;
import com.marik.arm.OpCode.thumb16.instruction.CBZ_A8_356;
import com.marik.arm.OpCode.thumb16.instruction.CPS_B9_1978;
import com.marik.arm.OpCode.thumb16.instruction.POP_A8_534;
import com.marik.arm.OpCode.thumb16.instruction.PUSH_A8_538;
import com.marik.arm.OpCode.thumb16.instruction.REV16_A8_564;
import com.marik.arm.OpCode.thumb16.instruction.REVSH_A8_566;
import com.marik.arm.OpCode.thumb16.instruction.REV_A8_562;
import com.marik.arm.OpCode.thumb16.instruction.SETEND_A8_604;
import com.marik.arm.OpCode.thumb16.instruction.SUB_A8_716;
import com.marik.arm.OpCode.thumb16.instruction.SXTH_A8_734;
import com.marik.arm.OpCode.thumb16.instruction.UXTB_A8_812;
import com.marik.arm.OpCode.thumb16.instruction.UXTH_A8_816;
import com.marik.arm.OpCode.thumb16.instruction.factory.ParseSupport;

import static com.marik.arm.OpCode.OpUtil.*;

public class Miscellaneous16Bitinstructions_A6_228 {
	public static ParseSupport parse(int data) {

		int OpCode = getShiftInt(data, 5, 7);

		if (assert0(OpCode, 2, 3, 4, 5, 6))
			return ADD_A8_316.INSTANCE;

		if (assert0(OpCode, 3, 4, 5, 6) && assert1(OpCode, 2))
			return SUB_A8_716.INSTANCE;

		if (assert0(OpCode, 4, 5, 6) && assert1(OpCode, 3))
			return CBZ_A8_356.INSTANCE;

		if (assert0(OpCode, 1, 2, 3, 5, 6) && assert1(OpCode, 4))
			return SXTH_A8_734.INSTANCE;

		if (assert0(OpCode, 2, 3, 5, 6) && assert1(OpCode, 4))
			return SXTH_A8_734.INSTANCE;

		if (assert0(OpCode, 1, 3, 5, 6) && assert1(OpCode, 2, 4))
			return UXTH_A8_816.INSTANCE;

		if (assert0(OpCode, 3, 5, 6) && assert1(OpCode, 1, 2, 4))
			return UXTB_A8_812.INSTANCE;

		if (assert0(OpCode, 5, 6) && assert1(OpCode, 3, 4))
			return CBZ_A8_356.INSTANCE;

		if (assert0(OpCode, 4, 6) && assert1(OpCode, 5))
			return PUSH_A8_538.INSTANCE;

		if (OpCode == 0b0110010)
			return SETEND_A8_604.INSTANCE;

		if (OpCode == 0b0110011)
			return CPS_B9_1978.INSTANCE;

		if (assert0(OpCode, 4, 5) && assert1(OpCode, 3, 6))
			return CBZ_A8_356.INSTANCE;

		if (assert0(OpCode, 1, 2, 3, 5) && assert1(OpCode, 4, 6))
			return REV_A8_562.INSTANCE;

		if (assert0(OpCode, 2, 3, 5) && assert1(OpCode, 1, 4, 6))
			return REV16_A8_564.INSTANCE;

		if (assert0(OpCode, 5) && assert1(OpCode, 1, 2, 4, 6))
			return CBZ_A8_356.INSTANCE;

		if (assert0(OpCode, 3, 5) && assert1(OpCode, 3, 4, 6))
			return REVSH_A8_566.INSTANCE;

		if (assert0(OpCode, 4) && assert1(OpCode, 5, 6))
			return POP_A8_534.INSTANCE;

		if (assert0(OpCode, 3) && assert1(OpCode, 4, 5, 6))
			return BKPT_A8_346.INSTANCE;

		if (assert1(OpCode, 3, 4, 5, 6))
			return IfThenHint_A6_229.parse(data);

		throw new IllegalArgumentException("Unable to decode instruction " + Integer.toBinaryString(data));
	}
}
