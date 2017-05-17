package brilliant.arm.OpCode.thumb.instructionSet32;

import static brilliant.arm.OpCode.factory.OpUtil.getShiftInt;
import brilliant.arm.OpCode.thumb.instruction32.LDMDB_A8_402;
import brilliant.arm.OpCode.thumb.instruction32.LDM_A8_396;
import brilliant.arm.OpCode.thumb.instruction32.POP_A8_534;
import brilliant.arm.OpCode.thumb.instruction32.PUSH_A8_538;
import brilliant.arm.OpCode.thumb.instruction32.RFE_B9_2000;
import brilliant.arm.OpCode.thumb.instruction32.SRS_B9_2004;
import brilliant.arm.OpCode.thumb.instruction32.STMDB_A8_668;
import brilliant.arm.OpCode.thumb.instruction32.STM_A8_664;
import brilliant.arm.OpCode.thumb.instruction32.support.ParseSupport;

class LoadStoreMultipleA6_237 {
	public static ParseSupport parse(int data) {

		int op = getShiftInt(data, 23, 2);
		int L = getShiftInt(data, 20, 1);
		int W_Rn = getShiftInt(data, 21, 1) << 4 | getShiftInt(data, 16, 4);

		if (op == 0b00)
			if (L == 0b0)
				return SRS_B9_2004.INSTANCE;
			else if (L == 0b1)
				return RFE_B9_2000.INSTANCE;

		if (op == 0b01)
			if (L == 0b0)
				return STM_A8_664.INSTANCE;
			else {
				if (W_Rn != 0b11101)
					return LDM_A8_396.INSTANCE;
				else
					return POP_A8_534.INSTANCE;
			}

		if (op == 0b10)
			if (L == 0b0) {
				if (W_Rn != 0b11101)
					return STMDB_A8_668.INSTANCE;
				else
					return PUSH_A8_538.INSTANCE;
			} else if (L == 0b1)
				return LDMDB_A8_402.INSTANCE;

		if (op == 0b11)
			if (L == 0b0)
				return SRS_B9_2004.INSTANCE;
			else if (L == 0b1)
				return RFE_B9_2000.INSTANCE;

		throw new IllegalArgumentException("Unable to decode instruction "
				+ Integer.toBinaryString(data));
	}
}
