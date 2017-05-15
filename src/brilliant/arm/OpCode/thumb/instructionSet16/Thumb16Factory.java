package brilliant.arm.OpCode.thumb.instructionSet16;

import static brilliant.arm.OpCode.OpUtil.assert0;
import static brilliant.arm.OpCode.OpUtil.assert1;
import static brilliant.arm.OpCode.OpUtil.getShiftInt;
import brilliant.arm.OpCode.ParseTemplate;
import brilliant.arm.OpCode.thumb.instruction16.ADD_A8_316;
import brilliant.arm.OpCode.thumb.instruction16.ADR_A8_322;
import brilliant.arm.OpCode.thumb.instruction16.B_A8_334;
import brilliant.arm.OpCode.thumb.instruction16.LDM_A8_396;
import brilliant.arm.OpCode.thumb.instruction16.LDR_A8_410;
import brilliant.arm.OpCode.thumb.instruction16.STM_A8_664;
import brilliant.arm.OpCode.thumb.instruction16.support.ParseSupport;

public class Thumb16Factory {
	public static ParseSupport parse(int data) {

		int OpCode = getShiftInt(data, 10, 6);

		if (assert0(OpCode, 4, 5))/* 00xxxx */
			return ShiftAddSubtractMoveCompare_A6_224.parse(data);

		if (OpCode == 0b010000)/* 010000 */
			return DataProcessing_A6_225.parse(data);

		if (OpCode == 0b010001)/* 010001 */
			return SpecialDataInstrucationsAndBranchAndExchange_A6_226.parse(data);

		if (assert0(OpCode, 2, 3, 5) && assert1(OpCode, 1, 4))/* 01001x */
			return LDR_A8_410.INSTANCE;

		if ((assert0(OpCode, 5, 3) && assert1(OpCode, 2, 4)) || (assert0(OpCode, 5) && assert1(OpCode, 3, 4))
				|| (assert0(OpCode, 3, 4)
						&& assert1(OpCode, 5)))/* 0101xx 011xxx 100xxx */
			return LoadOrStoreSingleData_A6_227.parse(data);

		if (assert0(OpCode, 1, 2, 4) && assert1(OpCode, 3, 5))/* 10100x */
			return ADR_A8_322.INSTANCE;

		if (assert0(OpCode, 2, 4) && assert1(OpCode, 1, 3, 5))/* 10101x */
			return ADD_A8_316.INSTANCE;

		if (assert0(OpCode, 4) && assert1(OpCode, 2, 3, 5))
			return Miscellaneous16Bitinstructions_A6_228.parse(data);

		if (assert0(OpCode, 1, 2, 3) && assert1(OpCode, 4, 5))
			return STM_A8_664.INSTANCE;

		if (assert0(OpCode, 2, 3) && assert1(OpCode, 1, 4, 5))
			return LDM_A8_396.INSTANCE;

		if (assert0(OpCode, 3) && assert1(OpCode, 2, 4, 5))
			return ConditionalBranchAndSupervisorCall_A6_229.parse(data);

		if (assert0(OpCode, 1, 2) && assert1(OpCode, 3, 4, 5))
			return B_A8_334.INSTANCE;

		throw new IllegalArgumentException("cann't decode instruction " + Integer.toBinaryString(data));
	}
}