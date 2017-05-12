package com.marik.arm.OpCode.thumb.instructionSet16;

import static com.marik.arm.OpCode.OpUtil.*;

import com.marik.arm.OpCode.thumb.instruction16.LDRB_A8_416;
import com.marik.arm.OpCode.thumb.instruction16.LDRB_A8_422;
import com.marik.arm.OpCode.thumb.instruction16.LDRH_A8_440;
import com.marik.arm.OpCode.thumb.instruction16.LDRH_A8_446;
import com.marik.arm.OpCode.thumb.instruction16.LDRSB_A8_454;
import com.marik.arm.OpCode.thumb.instruction16.LDRSH_A8_462;
import com.marik.arm.OpCode.thumb.instruction16.LDR_A8_406;
import com.marik.arm.OpCode.thumb.instruction16.LDR_A8_412;
import com.marik.arm.OpCode.thumb.instruction16.STRB_A8_678;
import com.marik.arm.OpCode.thumb.instruction16.STRB_A8_682;
import com.marik.arm.OpCode.thumb.instruction16.STRH_A8_698;
import com.marik.arm.OpCode.thumb.instruction16.STRH_A8_702;
import com.marik.arm.OpCode.thumb.instruction16.STR_A8_672;
import com.marik.arm.OpCode.thumb.instruction16.STR_A8_676;
import com.marik.arm.OpCode.thumb.instruction16.support.ParseSupport;

class LoadOrStoreSingleData_A6_227 {
	public static ParseSupport parse(int data) {

		int opA = getShiftInt(data, 12, 4);
		int opB = getShiftInt(data, 9, 3);

		if (opA == 0b0101) {
			if (opB == 0b000)
				return STR_A8_676.INSTANCE;

			if (opB == 0b001)
				return STRH_A8_702.INSTANCE;

			if (opB == 0b010)
				return STRB_A8_682.INSTANCE;

			if (opB == 0b011)
				return LDRSB_A8_454.INSTANCE;

			if (opB == 0b100)
				return LDR_A8_412.INSTANCE;

			if (opB == 0b101)
				return LDRH_A8_446.INSTANCE;

			if (opB == 0b110)
				return LDRB_A8_422.INSTANCE;

			if (opB == 0b111)
				return LDRSH_A8_462.INSTANCE;

			if (opA == 0b0110) {
				if (assert0(opB, 2))
					return STR_A8_672.INSTANCE;

				if (assert1(opB, 2))
					return LDR_A8_406.INSTANCE;
			}

			if (opA == 0b0111) {
				if (assert0(opB, 2))
					return STRB_A8_678.INSTANCE;

				if (assert1(opB, 2))
					return LDRB_A8_416.INSTANCE;
			}
		}

		if (opA == 0b1000) {
			if (assert0(opB, 2))
				return STRH_A8_698.INSTANCE;

			if (assert1(opB, 2))
				return LDRH_A8_440.INSTANCE;
		}

		if (opA == 0b1001) {
			if (assert0(opB, 2))
				return STR_A8_672.INSTANCE;

			if (assert1(opB, 2))
				return LDR_A8_406.INSTANCE;
		}
		throw new IllegalArgumentException("Unable to decode instruction " + Integer.toBinaryString(data));
	}
}
