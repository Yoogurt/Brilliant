package brilliant.arm.OpCode.thumb.instructionSet32;

import brilliant.arm.OpCode.thumb.instruction32.SDIV_A8_600;
import brilliant.arm.OpCode.thumb.instruction32.SMLAL_A8_626;
import brilliant.arm.OpCode.thumb.instruction32.SMLALD_A8_628;
import brilliant.arm.OpCode.thumb.instruction32.SMLAL_A8_624;
import brilliant.arm.OpCode.thumb.instruction32.SMLSLD_A8_634;
import brilliant.arm.OpCode.thumb.instruction32.SMULL_A8_646;
import brilliant.arm.OpCode.thumb.instruction32.UDIV_A8_760;
import brilliant.arm.OpCode.thumb.instruction32.UMAAL_A8_774;
import brilliant.arm.OpCode.thumb.instruction32.UMLAL_A8_776;
import brilliant.arm.OpCode.thumb.instruction32.UMULL_A8_778;
import brilliant.arm.OpCode.thumb.instruction32.support.ParseSupport;
import static brilliant.arm.OpCode.OpUtil.*;

class LongMultiplyA6_250 {
	public static ParseSupport parse(int data) {

		int op1 = getShiftInt(data, 20, 3);
		int op2 = getShiftInt(data, 4, 4);

		if (op1 == 0b000)
			if (op2 == 0b0000)
				return SMULL_A8_646.INSTANCE;

		if (op1 == 0b001)
			if (op2 == 0b1111)
				return SDIV_A8_600.INSTANCE;

		if (op1 == 0b010)
			if (op2 == 0b0000)
				return UMULL_A8_778.INSTANCE;

		if (op1 == 0b011)
			if (op2 == 0b1111)
				return UDIV_A8_760.INSTANCE;

		if (op1 == 0b100) {
			if (op2 == 0b0000)
				return SMLAL_A8_624.INSTANCE;

			if (assert0(op2, 2) && assert1(op2, 3))
				return SMLAL_A8_626.INSTANCE;

			if (assert0(op2, 1) && assert1(op2, 2, 3))
				return SMLALD_A8_628.INSTANCE;
		}

		if (op1 == 0b101)
			if (assert0(op2, 1) && assert1(op2, 2, 3))
				return SMLSLD_A8_634.INSTANCE;

		if (op1 == 0b110) {
			if (op2 == 0b0000)
				return UMLAL_A8_776.INSTANCE;

			if (op2 == 0b0110)
				return UMAAL_A8_774.INSTANCE;

		}
		throw new IllegalArgumentException("Unable to decode instruction "
				+ Integer.toBinaryString(data));
	}
}
