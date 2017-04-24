package com.marik.arm.OpCode.thumb16.instructionSet;

import static com.marik.arm.OpCode.OpUtil.*;

import com.marik.arm.OpCode.thumb16.instruction.LDR_A8_406;
import com.marik.arm.OpCode.thumb16.instruction.LDR_A8_412;
import com.marik.arm.OpCode.thumb16.instruction.STR_A8_672;
import com.marik.arm.OpCode.thumb16.instruction.STR_A8_676;

public class LoadOrStoreSingleData_A6_227 {
	public static String parse(int data) {

		int opA = getShiftInt(data, 12, 4);
		int opB = getShiftInt(data, 9, 3);

		if (opA == 0b0101) {
			if (opB == 0b000)
				return STR_A8_676.INSTANCE.parse(data);

			if (opB == 0b001)
				throw new UnsupportedOperationException("STRH not implemets");

			if (opB == 0b010)
				throw new UnsupportedOperationException("STRB not implemets");

			if (opB == 0b011)
				throw new UnsupportedOperationException("LDRSB not implemets");

			if (opB == 0b100)
				return LDR_A8_412.INSTANCE.parse(data);

			if (opB == 0b101)
				throw new UnsupportedOperationException("LDRH not implemets");

			if (opB == 0b110)
				throw new UnsupportedOperationException("ldrb not implemets");

			if (opB == 0b111)
				throw new UnsupportedOperationException("LDRSH not implemets");
		}

		if (opA == 0b0110) {
			if (assert0(opB, 2))
				return STR_A8_672.INSTANCE.parse(data);

			if (assert1(opB, 2))
				return LDR_A8_406.INSTANCE.parse(data);
		}

		if (opA == 0b0111) {
			if (assert0(opB, 2))
				throw new UnsupportedOperationException("STRB no implements");

			if (assert1(opB, 2))
				throw new UnsupportedOperationException("LDRB no implements");
		}
		
		if (opA == 0b1000) {
			if (assert0(opB, 2))
				throw new UnsupportedOperationException("STRH no implements");

			if (assert1(opB, 2))
				throw new UnsupportedOperationException("LDRH no implements");
		}
		
		if (opA == 0b1001) {
			if (assert0(opB, 2))
				return STR_A8_672.INSTANCE.parse(data);

			if (assert1(opB, 2))
				return LDR_A8_406.INSTANCE.parse(data);
		}

		throw new IllegalArgumentException("Unable to decode instruction " + Integer.toBinaryString(data));
	}
}
