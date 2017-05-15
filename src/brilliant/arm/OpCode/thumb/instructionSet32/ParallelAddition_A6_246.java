package brilliant.arm.OpCode.thumb.instructionSet32;

import brilliant.arm.OpCode.thumb.instruction32.QADD16_A8_542;
import brilliant.arm.OpCode.thumb.instruction32.QADD8_A8_544;
import brilliant.arm.OpCode.thumb.instruction32.QASX_A8_546;
import brilliant.arm.OpCode.thumb.instruction32.QSAX_A8_552;
import brilliant.arm.OpCode.thumb.instruction32.QSUB16_A8_556;
import brilliant.arm.OpCode.thumb.instruction32.QSUB8_A8_558;
import brilliant.arm.OpCode.thumb.instruction32.SADD16_A8_586;
import brilliant.arm.OpCode.thumb.instruction32.SADD8_A8_558;
import brilliant.arm.OpCode.thumb.instruction32.SASX_A8_590;
import brilliant.arm.OpCode.thumb.instruction32.SHADD16_A8_608;
import brilliant.arm.OpCode.thumb.instruction32.SHADD8_A8_610;
import brilliant.arm.OpCode.thumb.instruction32.SHASX_A8_612;
import brilliant.arm.OpCode.thumb.instruction32.SHSAX_A8_614;
import brilliant.arm.OpCode.thumb.instruction32.SHSUB16_A8_616;
import brilliant.arm.OpCode.thumb.instruction32.SHSUB8_A8_618;
import brilliant.arm.OpCode.thumb.instruction32.SSAX_A8_656;
import brilliant.arm.OpCode.thumb.instruction32.SSUB16_A8_658;
import brilliant.arm.OpCode.thumb.instruction32.SSUB8_A8_660;
import brilliant.arm.OpCode.thumb.instruction32.support.ParseSupport;
import static brilliant.arm.OpCode.OpUtil.*;

public class ParallelAddition_A6_246 {

	public static ParseSupport parse(int data) {

		int op1 = getShiftInt(data, 20, 3);
		int op2 = getShiftInt(data, 4, 2);

		if (op1 == 0b001 && op2 == 0b00)
			return SADD16_A8_586.INSTANCE;

		if (op1 == 0b010 && op2 == 0b00)
			return SASX_A8_590.INSTANCE;

		if (op1 == 0b110 && op2 == 0b00)
			return SSAX_A8_656.INSTANCE;

		if (op1 == 0b101 && op2 == 0b00)
			return SSUB16_A8_658.INSTANCE;

		if (op1 == 0b000 && op2 == 0b00)
			return SADD8_A8_558.INSTANCE;

		if (op1 == 0b100 && op2 == 0b00)
			return SSUB8_A8_660.INSTANCE;

		if (op1 == 0b001 && op2 == 0b01)
			return QADD16_A8_542.INSTANCE;

		if (op1 == 0b010 && op2 == 0b01)
			return QASX_A8_546.INSTANCE;

		if (op1 == 0b110 && op2 == 0b01)
			return QSAX_A8_552.INSTANCE;

		if (op1 == 0b101 && op2 == 0b01)
			return QSUB16_A8_556.INSTANCE;

		if (op1 == 0b000 && op2 == 0b01)
			return QADD8_A8_544.INSTANCE;

		if (op1 == 0b100 && op2 == 0b01)
			return QSUB8_A8_558.INSTANCE;

		if (op1 == 0b001 && op2 == 0b10)
			return SHADD16_A8_608.INSTANCE;

		if (op1 == 0b010 && op2 == 0b10)
			return SHASX_A8_612.INSTANCE;

		if (op1 == 0b110 && op2 == 0b10)
			return SHSAX_A8_614.INSTANCE;

		if (op1 == 0b101 && op2 == 0b10)
			return SHSUB16_A8_616.INSTANCE;

		if (op1 == 0b000 && op2 == 0b10)
			return SHADD8_A8_610.INSTANCE;

		if (op1 == 0b100 && op2 == 0b10)
			return SHSUB8_A8_618.INSTANCE;

		throw new IllegalArgumentException("Unable to decode instruction "
				+ Integer.toBinaryString(data));
	}
}
