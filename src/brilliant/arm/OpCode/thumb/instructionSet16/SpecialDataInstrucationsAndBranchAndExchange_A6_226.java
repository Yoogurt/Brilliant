package brilliant.arm.OpCode.thumb.instructionSet16;

import static brilliant.arm.OpCode.factory.OpUtil.assert0;
import static brilliant.arm.OpCode.factory.OpUtil.assert1;
import static brilliant.arm.OpCode.factory.OpUtil.getShiftInt;
import brilliant.arm.OpCode.thumb.instruction16.ADD_A8_310;
import brilliant.arm.OpCode.thumb.instruction16.BLX_A8_350;
import brilliant.arm.OpCode.thumb.instruction16.BX_A8_352;
import brilliant.arm.OpCode.thumb.instruction16.CMP_A8_372;
import brilliant.arm.OpCode.thumb.instruction16.MOV_A8_486;
import brilliant.arm.OpCode.thumb.instruction16.support.ParseSupport;

class SpecialDataInstrucationsAndBranchAndExchange_A6_226 {

	public static ParseSupport parse(int data) {
		int OpCode = getShiftInt(data, 6, 4);

		if (OpCode == 0b0000)
			return ADD_A8_310.INSTANCE;

		if (OpCode == 0b0001 || (assert0(OpCode, 2, 3) && assert1(OpCode, 1)))
			return ADD_A8_310.INSTANCE;

		if (assert0(OpCode, 3) && assert1(OpCode, 2))
			return CMP_A8_372.INSTANCE;

		if (OpCode == 0B1000)
			return MOV_A8_486.INSTANCE;

		if (OpCode == 0b1001 || (assert0(OpCode, 2) && assert1(OpCode, 1, 3)))
			return MOV_A8_486.INSTANCE;

		if (assert0(OpCode, 1) && assert1(OpCode, 2, 3))
			return BX_A8_352.INSTANCE;

		if (assert1(OpCode, 1, 2, 3))
			return BLX_A8_350.INSTANCE;

		throw new IllegalArgumentException("Unable to decode instruction "
				+ Integer.toBinaryString(data));
	}
}
