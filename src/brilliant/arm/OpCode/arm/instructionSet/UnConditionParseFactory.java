package brilliant.arm.OpCode.arm.instructionSet;

import brilliant.arm.OpCode.arm.instruction.BL_A8_348;
import brilliant.arm.OpCode.arm.instruction.CDP_A8_358;
import brilliant.arm.OpCode.arm.instruction.LDC_A8_392;
import brilliant.arm.OpCode.arm.instruction.LDC_A8_394;
import brilliant.arm.OpCode.arm.instruction.MCRR_A8_478;
import brilliant.arm.OpCode.arm.instruction.MCR_A8_476;
import brilliant.arm.OpCode.arm.instruction.MRC_A8_492;
import brilliant.arm.OpCode.arm.instruction.MRRC_A8_494;
import brilliant.arm.OpCode.arm.instruction.RFE_B9_2000;
import brilliant.arm.OpCode.arm.instruction.SRS_B9_2006;
import brilliant.arm.OpCode.arm.instruction.support.ParseSupport;
import static brilliant.arm.OpCode.OpUtil.*;

public class UnConditionParseFactory {

	public static ParseSupport parseUncondition(int data) {

		int op1 = getShiftInt(data, 20, 8);
		int op = getShiftInt(data, 4, 1);
		int Rn = getShiftInt(data, 16, 4);

		if (assert0(op1, 7))
			return MemoryHintAdvancedSIMDInstructions_A5_217.parse(data);

		if (assert0(op1, 0, 5, 6) && assert1(op1, 2, 7))
			return SRS_B9_2006.INSTANCE;

		if (assert0(op1, 2, 5, 6) && assert1(op1, 0, 7))
			return RFE_B9_2000.INSTANCE;

		if (assert0(op1, 6) && assert1(op1, 5, 7))
			return BL_A8_348.INSTANCE;

		if (assert0(op1, 0, 5) && assert1(op, 6, 7)
				|| !(assert0(op1, 1, 3, 4, 5) && assert1(op1, 0, 6, 7)))
			if (Rn != 0b1111)
				return LDC_A8_392.INSTANCE;
			else
				return LDC_A8_394.INSTANCE;

		if (op1 == 0b11000100)
			return MCRR_A8_478.INSTANCE;

		if (op1 == 0b11000101)
			return MRRC_A8_494.INSTANCE;

		if (assert0(op1, 4) && assert1(op1, 5, 6, 7))
			if (op == 0b0)
				return CDP_A8_358.INSTANCE;

		if (assert0(op1, 0, 4) && assert1(op1, 5, 6, 7))
			if (op == 0b1)
				return MCR_A8_476.INSTANCE;

		if (assert0(op1, 4) && assert1(op1, 0, 5, 6, 7))
			return MRC_A8_492.INSTANCE;

		throw new IllegalArgumentException("Unable to decode instruction "
				+ Integer.toBinaryString(data));
	}
}
