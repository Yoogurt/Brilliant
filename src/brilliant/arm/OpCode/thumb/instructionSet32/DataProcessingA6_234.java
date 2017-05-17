package brilliant.arm.OpCode.thumb.instructionSet32;

import static brilliant.arm.OpCode.factory.OpUtil.getShiftInt;
import brilliant.arm.OpCode.thumb.instruction32.ADD_A8_306;
import brilliant.arm.OpCode.thumb.instruction32.ADR_A8_322;
import brilliant.arm.OpCode.thumb.instruction32.BFC_A8_336;
import brilliant.arm.OpCode.thumb.instruction32.BFI_A8_338;
import brilliant.arm.OpCode.thumb.instruction32.MOVT_A8_491;
import brilliant.arm.OpCode.thumb.instruction32.MOV_A8_484;
import brilliant.arm.OpCode.thumb.instruction32.SBFX_A8_598;
import brilliant.arm.OpCode.thumb.instruction32.SSAT16_A8_654;
import brilliant.arm.OpCode.thumb.instruction32.SSAT_A8_652;
import brilliant.arm.OpCode.thumb.instruction32.SUB_A8_708;
import brilliant.arm.OpCode.thumb.instruction32.UBFX_A8_756;
import brilliant.arm.OpCode.thumb.instruction32.USAT16_A8_798;
import brilliant.arm.OpCode.thumb.instruction32.USAT_A8_796;
import brilliant.arm.OpCode.thumb.instruction32.support.ParseSupport;

class DataProcessingA6_234 {
	public static ParseSupport parse(int data) {

		int op = getShiftInt(data, 20, 5);
		int Rn = getShiftInt(data, 16, 4);

		if (op == 0b00000)
			if (Rn != 0b1111)
				return ADD_A8_306.INSTANCE;
			else
				return ADR_A8_322.INSTANCE;

		if (op == 0b00100)
			return MOV_A8_484.INSTANCE;

		if (op == 0b01010)
			if (Rn != 0b1111)
				return SUB_A8_708.INSTANCE;
			else
				return ADR_A8_322.INSTANCE;

		if (op == 0b01100)
			return MOVT_A8_491.INSTANCE;

		if (op == 0b10000 || op == 0b10010)
			return SSAT_A8_652.INSTANCE;

		if (op == 0b10010)
			return SSAT16_A8_654.INSTANCE;

		if (op == 0b10100)
			return SBFX_A8_598.INSTANCE;

		if (op == 0b10110)
			if (Rn != 0b1111)
				return BFI_A8_338.INSTANCE;
			else
				return BFC_A8_336.INSTANCE;

		if (op == 0b11000 || op == 0b11010)
			return USAT_A8_796.INSTANCE;

		if (op == 0b11010)
			return USAT16_A8_798.INSTANCE;

		if (op == 0b11100)
			return UBFX_A8_756.INSTANCE;

		throw new IllegalArgumentException("Unable to decode instruction "
				+ Integer.toBinaryString(data));
	}
}
