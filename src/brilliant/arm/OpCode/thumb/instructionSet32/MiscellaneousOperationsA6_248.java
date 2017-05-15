package brilliant.arm.OpCode.thumb.instructionSet32;

import brilliant.arm.OpCode.thumb.instruction32.CLZ_A8_362;
import brilliant.arm.OpCode.thumb.instruction32.QADD_A8_540;
import brilliant.arm.OpCode.thumb.instruction32.QDADD_A8_548;
import brilliant.arm.OpCode.thumb.instruction32.QDSUB_A8_550;
import brilliant.arm.OpCode.thumb.instruction32.QSUB_A8_554;
import brilliant.arm.OpCode.thumb.instruction32.RBIT_A8_560;
import brilliant.arm.OpCode.thumb.instruction32.REV16_A8_564;
import brilliant.arm.OpCode.thumb.instruction32.REVSH_A8_566;
import brilliant.arm.OpCode.thumb.instruction32.REV_A8_562;
import brilliant.arm.OpCode.thumb.instruction32.SEL_A8_602;
import brilliant.arm.OpCode.thumb.instruction32.support.ParseSupport;
import static brilliant.arm.OpCode.OpUtil.*;

public class MiscellaneousOperationsA6_248 {

	public static ParseSupport parse(int data) {

		int op1 = getShiftInt(data, 20, 2);
		int op2 = getShiftInt(data, 4, 2);

		if (op1 == 0b00)
			switch (op2) {
			case 0b00:
				return QADD_A8_540.INSTANCE;
			case 0b01:
				return QDADD_A8_548.INSTANCE;
			case 0b10:
				return QSUB_A8_554.INSTANCE;
			case 0b11:
				return QDSUB_A8_550.INSTANCE;
			}

		if (op1 == 0b01)
			switch (op2) {
			case 0b00:
				return REV_A8_562.INSTANCE;
			case 0b01:
				return REV16_A8_564.INSTANCE;
			case 0b10:
				return RBIT_A8_560.INSTANCE;
			case 0b11:
				return REVSH_A8_566.INSTANCE;
			}

		if (op1 == 0b10 && op2 == 0b00)
			return SEL_A8_602.INSTANCE;

		if (op1 == 0b11 && op2 == 0b00)
			return CLZ_A8_362.INSTANCE;

		throw new IllegalArgumentException("Unable to decode instruction "
				+ Integer.toBinaryString(data));
	}
}
