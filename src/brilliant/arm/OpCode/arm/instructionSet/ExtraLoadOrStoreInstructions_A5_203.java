package brilliant.arm.OpCode.arm.instructionSet;

import static brilliant.arm.OpCode.factory.OpUtil.assert0;
import static brilliant.arm.OpCode.factory.OpUtil.assert1;
import static brilliant.arm.OpCode.factory.OpUtil.getShiftInt;
import brilliant.arm.OpCode.arm.instruction.LDRD_A8_426;
import brilliant.arm.OpCode.arm.instruction.LDRD_A8_428;
import brilliant.arm.OpCode.arm.instruction.LDRD_A8_430;
import brilliant.arm.OpCode.arm.instruction.LDRH_A8_442;
import brilliant.arm.OpCode.arm.instruction.LDRH_A8_444;
import brilliant.arm.OpCode.arm.instruction.LDRH_A8_446;
import brilliant.arm.OpCode.arm.instruction.LDRSB_A8_450;
import brilliant.arm.OpCode.arm.instruction.LDRSB_A8_452;
import brilliant.arm.OpCode.arm.instruction.LDRSB_A8_454;
import brilliant.arm.OpCode.arm.instruction.LDRSH_A8_458;
import brilliant.arm.OpCode.arm.instruction.LDRSH_A8_460;
import brilliant.arm.OpCode.arm.instruction.LDRSH_A8_462;
import brilliant.arm.OpCode.arm.instruction.STRD_A8_686;
import brilliant.arm.OpCode.arm.instruction.STRD_A8_688;
import brilliant.arm.OpCode.arm.instruction.STRH_A8_700;
import brilliant.arm.OpCode.arm.instruction.STRH_A8_702;
import brilliant.arm.OpCode.arm.instruction.support.ParseSupport;

@SuppressWarnings("deprecation")
public class ExtraLoadOrStoreInstructions_A5_203 {
	public static ParseSupport parse(int data) {

		int op2 = getShiftInt(data, 5, 2);
		int op1 = getShiftInt(data, 20, 5);
		int Rn = getShiftInt(data, 16, 4);

		if (op2 == 0b01) {
			if (assert0(op1, 0, 2))
				return STRH_A8_702.INSTANCE;
			if (assert0(op1, 2) && assert1(op1, 0))
				return LDRH_A8_446.INSTANCE;
			if (assert0(op1, 0) && assert1(op1, 2))
				return STRH_A8_700.INSTANCE;
			if (assert1(op1, 0, 2))
				if (Rn != 0b1111)
					return LDRH_A8_442.INSTANCE;
				else
					return LDRH_A8_444.INSTANCE;
		}

		if (op2 == 0b10) {
			if (assert0(op1, 0, 2))
				return LDRD_A8_430.INSTANCE;
			if (assert0(op1, 2) && assert1(op1, 0))
				return LDRSB_A8_454.INSTANCE;
			if (assert0(op1, 0) && assert1(op1, 2))
				if (Rn != 0b1111)
					return LDRD_A8_426.INSTANCE;
				else
					return LDRD_A8_428.INSTANCE;

			if (assert1(op1, 0, 2))
				if (Rn != 0b1111)
					return LDRSB_A8_450.INSTANCE;
				else
					return LDRSB_A8_452.INSTANCE;
		}

		if (op2 == 0b11) {
			if (assert0(op1, 0, 2))
				return STRD_A8_688.INSTANCE;
			if (assert0(op1, 2) && assert1(op1, 0))
				return LDRSH_A8_462.INSTANCE;
			if (assert0(op1, 0) && assert1(op1, 2))
				return STRD_A8_686.INSTANCE;
			if (assert1(op1, 0, 2))
				if (Rn != 0b1111)
					return LDRSH_A8_458.INSTANCE;
				else
					return LDRSH_A8_460.INSTANCE;
		}

		throw new IllegalArgumentException("Unable to decode instruction "
				+ Integer.toBinaryString(data));
	}
}
