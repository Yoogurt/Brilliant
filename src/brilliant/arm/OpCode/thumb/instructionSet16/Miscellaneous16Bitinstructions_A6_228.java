package brilliant.arm.OpCode.thumb.instructionSet16;

import static brilliant.arm.OpCode.factory.OpUtil.assert0;
import static brilliant.arm.OpCode.factory.OpUtil.assert1;
import static brilliant.arm.OpCode.factory.OpUtil.getShiftInt;
import brilliant.arm.OpCode.thumb.instruction16.ADD_A8_316;
import brilliant.arm.OpCode.thumb.instruction16.BKPT_A8_346;
import brilliant.arm.OpCode.thumb.instruction16.CBZ_A8_356;
import brilliant.arm.OpCode.thumb.instruction16.CPS_B9_1978;
import brilliant.arm.OpCode.thumb.instruction16.POP_A8_534;
import brilliant.arm.OpCode.thumb.instruction16.PUSH_A8_538;
import brilliant.arm.OpCode.thumb.instruction16.REV16_A8_564;
import brilliant.arm.OpCode.thumb.instruction16.REVSH_A8_566;
import brilliant.arm.OpCode.thumb.instruction16.REV_A8_562;
import brilliant.arm.OpCode.thumb.instruction16.SETEND_A8_604;
import brilliant.arm.OpCode.thumb.instruction16.SUB_A8_716;
import brilliant.arm.OpCode.thumb.instruction16.SXTH_A8_734;
import brilliant.arm.OpCode.thumb.instruction16.UXTB_A8_812;
import brilliant.arm.OpCode.thumb.instruction16.UXTH_A8_816;
import brilliant.arm.OpCode.thumb.instruction16.support.ParseSupport;

class Miscellaneous16Bitinstructions_A6_228 {
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

		throw new IllegalArgumentException("Unable to decode instruction "
				+ Integer.toBinaryString(data));
	}
}
