package brilliant.arm.OpCode.thumb.instructionSet32;

import static brilliant.arm.OpCode.factory.OpUtil.assert0;
import static brilliant.arm.OpCode.factory.OpUtil.assert1;
import static brilliant.arm.OpCode.factory.OpUtil.getShiftInt;
import brilliant.arm.OpCode.thumb.instruction32.LDRBT_A8_424;
import brilliant.arm.OpCode.thumb.instruction32.LDRB_A8_416;
import brilliant.arm.OpCode.thumb.instruction32.LDRB_A8_420;
import brilliant.arm.OpCode.thumb.instruction32.LDRB_A8_422;
import brilliant.arm.OpCode.thumb.instruction32.LDRSBT_A8_456;
import brilliant.arm.OpCode.thumb.instruction32.LDRSB_A8_450;
import brilliant.arm.OpCode.thumb.instruction32.LDRSB_A8_452;
import brilliant.arm.OpCode.thumb.instruction32.LDRSB_A8_454;
import brilliant.arm.OpCode.thumb.instruction32.PLD_A8_524;
import brilliant.arm.OpCode.thumb.instruction32.PLD_A8_526;
import brilliant.arm.OpCode.thumb.instruction32.PLD_A8_528;
import brilliant.arm.OpCode.thumb.instruction32.PLI_A8_530;
import brilliant.arm.OpCode.thumb.instruction32.PLI_A8_532;
import brilliant.arm.OpCode.thumb.instruction32.support.ParseSupport;

class LoadByteA6_241 {
	public static ParseSupport parse(int data) {

		int op1 = getShiftInt(data, 23, 2);
		int op2 = getShiftInt(data, 6, 6);
		int Rn = getShiftInt(data, 16, 4);
		int Rt = getShiftInt(data, 12, 4);

		if (op1 == 0b00) {
			if (op2 == 0b000000)
				if (Rn != 0b1111)
					if (Rt != 0b1111)
						return LDRB_A8_422.INSTANCE;
					else
						return PLD_A8_528.INSTANCE;
		}

		if (op1 == 0b00 || op1 == 0b01)
			if (Rn == 0b1111)
				if (Rt != 0b1111)
					return LDRB_A8_420.INSTANCE;
				else
					return PLD_A8_526.INSTANCE;

		if (op1 == 0b00) {
			if (assert1(op2, 2, 5)) {
				if (Rn != 0b1111)
					return LDRB_A8_416.INSTANCE;
			}

			if (assert0(op2, 2, 3) && assert1(op2, 4, 5))
				if (Rn != 0b1111)
					if (Rt != 0b1111)
						return LDRB_A8_416.INSTANCE;
					else
						return PLD_A8_524.INSTANCE;

			if (assert0(op2, 2) && assert1(op2, 3, 4, 5))
				if (Rn != 0b1111)
					return LDRBT_A8_424.INSTANCE;
		}

		if (op1 == 0b01)
			if (Rn != 0b1111)
				if (Rt != 0b1111)
					return LDRB_A8_416.INSTANCE;
				else
					return PLD_A8_524.INSTANCE;

		if (op1 == 0b10)
			if (op2 == 0b000000)
				if (Rn != 0b1111)
					if (Rt != 0b1111)
						return LDRSB_A8_454.INSTANCE;
					else
						return PLI_A8_532.INSTANCE;

		if (op1 == 0b10 || op1 == 0b11)
			if (Rn == 0b1111)
				if (Rt != 0b1111)
					return LDRSB_A8_452.INSTANCE;
				else
					return PLI_A8_530.INSTANCE;

		if (op1 == 0b10) {
			if (assert1(op2, 2, 5))
				if (Rn != 0b1111)
					return LDRSB_A8_450.INSTANCE;

			if (assert0(op2, 2, 3) && assert1(op2, 4, 5))
				if (Rn != 0b1111)
					if (Rt != 0b1111)
						return LDRSB_A8_450.INSTANCE;
					else
						return PLI_A8_530.INSTANCE;

			if (assert0(op2, 2) && assert1(op2, 3, 4, 5))
				if (Rn != 0b1111)
					return LDRSBT_A8_456.INSTANCE;
		}

		if (op1 == 0b11)
			if (Rn != 0b1111)
				if (Rt != 0b1111)
					return LDRSB_A8_450.INSTANCE;
				else
					return PLI_A8_530.INSTANCE;

		throw new IllegalArgumentException("Unable to decode instruction "
				+ Integer.toBinaryString(data));
	}
}
