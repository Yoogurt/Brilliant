package com.marik.arm.OpCode.thumb16.instructionSet;

import com.marik.arm.OpCode.thumb16.instruction.ADD_A8_316;
import com.marik.arm.OpCode.thumb16.instruction.POP_A8_534;
import com.marik.arm.OpCode.thumb16.instruction.PUSH_A8_538;
import com.marik.arm.OpCode.thumb16.instruction.SUB_A8_716;

import static com.marik.arm.OpCode.OpUtil.*;

public class Miscellaneous16Bitinstructions_A6_228 {
	public static String parse(int data) {

		int OpCode = getShiftInt(data, 5, 7);

		if (assert0(OpCode, 2, 3, 4, 5, 6))
			return ADD_A8_316.INSTANCE.parse(data);

		if (assert0(OpCode, 3, 4, 5, 6) && assert1(OpCode, 2))
			return SUB_A8_716.INSTANCE.parse(data);

		if (assert0(OpCode, 4, 5, 6) && assert1(OpCode, 3))
			throw new UnsupportedOperationException("CBZ/CBNZ not implements");

		if (assert0(OpCode, 1, 2, 3, 5, 6) && assert1(OpCode, 4))
			throw new UnsupportedOperationException("SXTH not implements");

		if (assert0(OpCode, 2, 3, 5, 6) && assert1(OpCode, 4))
			throw new UnsupportedOperationException("SXTH not implements");

		if (assert0(OpCode, 1, 3, 5, 6) && assert1(OpCode, 2, 4))
			throw new UnsupportedOperationException("UXTH not implements");

		if (assert0(OpCode, 3, 5, 6) && assert1(OpCode, 1, 2, 4))
			throw new UnsupportedOperationException("UXTB not implements");

		if (assert0(OpCode, 5, 6) && assert1(OpCode, 3, 4))
			throw new UnsupportedOperationException("CBZ/CBNZ not implements");

		if (assert0(OpCode, 4, 6) && assert1(OpCode, 5))
			return PUSH_A8_538.INSTANCE.parse(data);

		if (OpCode == 0b0110010)
			throw new UnsupportedOperationException("SETEND not implements");

		if (OpCode == 0b0110011)
			throw new UnsupportedOperationException("CPS not implements");

		if (assert0(OpCode, 4, 5) && assert1(OpCode, 3, 6))
			throw new UnsupportedOperationException("CBZ/CBNZ not implements");

		if (assert0(OpCode, 1, 2, 3, 5) && assert1(OpCode, 4, 6))
			throw new UnsupportedOperationException("REV not implements");

		if (assert0(OpCode, 2, 3, 5) && assert1(OpCode, 1, 4, 6))
			throw new UnsupportedOperationException("REV16 not implements");

		if (assert0(OpCode, 5) && assert1(OpCode, 1, 2, 4, 6))
			throw new UnsupportedOperationException("CBZ/CBNZ not implements");

		if (assert0(OpCode, 3, 5) && assert1(OpCode, 3, 4, 6))
			throw new UnsupportedOperationException("REVSH not implements");

		if (assert0(OpCode, 4) && assert1(OpCode, 5, 6))
			return POP_A8_534.INSTANCE.parse(data);

		if (assert0(OpCode, 3) && assert1(OpCode, 4, 5, 6))
			throw new UnsupportedOperationException("BRKP not implements");

		if (assert1(OpCode, 3, 4, 5, 6))
			return IfThenHint_A6_229.parse(data);

		throw new IllegalArgumentException("Unable to decode instruction " + Integer.toBinaryString(data));
	}
}
