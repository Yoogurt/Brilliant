package brilliant.arm.OpCode.thumb.instructionSet32;

import brilliant.arm.OpCode.thumb.instruction32.LDRT_A8_466;
import brilliant.arm.OpCode.thumb.instruction32.LDR_A8_406;
import brilliant.arm.OpCode.thumb.instruction32.LDR_A8_410;
import brilliant.arm.OpCode.thumb.instruction32.LDR_A8_412;
import brilliant.arm.OpCode.thumb.instruction32.support.ParseSupport;
import static brilliant.arm.OpCode.OpUtil.*;

class LoadWordA6_239 {
	public static ParseSupport parse(int data) {

		int op1 = getShiftInt(data, 23, 2);
		int op2 = getShiftInt(data, 6, 6);
		int Rn = getShiftInt(data, 16, 4);

		if (op1 == 0b00)
			if (op2 == 0b000000)
				if (Rn != 0b1111)
					return LDR_A8_412.INSTANCE;

		if (op1 == 0b00) {
			if (assert1(op2, 2, 5))
				if (Rn != 0b1111)
					return LDR_A8_406.INSTANCE;

			if (assert0(op2, 2, 3) && assert1(op2, 4, 5))
				return LDR_A8_406.INSTANCE;
		}

		if (op1 == 0b01)
			if (Rn != 0b1111)
				return LDR_A8_406.INSTANCE;

		if (op1 == 0b00)
			if (assert0(op2, 2) && assert1(op2, 3, 4, 5))
				if (Rn != 0b1111)
					return LDRT_A8_466.INSTANCE;

		if (op1 == 0b00 || op1 == 0b01)
			return LDR_A8_410.INSTANCE;

		throw new IllegalArgumentException("Unable to decode instruction "
				+ Integer.toBinaryString(data));
	}
}
