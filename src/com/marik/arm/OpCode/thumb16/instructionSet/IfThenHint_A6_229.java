package com.marik.arm.OpCode.thumb16.instructionSet;

import com.marik.arm.OpCode.OpUtil;

public class IfThenHint_A6_229 {

	public static String parse(int data) {
		int opA = OpUtil.getShiftInt(data, 4, 4);
		int opB = OpUtil.getShiftInt(data, 0, 4);

		if (opB != 0b0000)
			throw new UnsupportedOperationException("IT not implements");

		switch (opA) {
		case 0b0000:
			return "NOP";
		case 0b0001:
			throw new UnsupportedOperationException("YIELD not implements");
		case 0b0010:
			throw new UnsupportedOperationException("WFE not implements");
		case 0b0011:
			throw new UnsupportedOperationException("WFI not implements");
		case 0b0100:
			throw new UnsupportedOperationException("SEV not implements");
		default:
			throw new IllegalArgumentException("Unable to decode " + Integer.toBinaryString(data));
		}

	}

}
