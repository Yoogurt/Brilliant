package com.marik.arm.OpCode.thumb16.instructionSet;

import static com.marik.arm.OpCode.OpUtil.*;

import com.marik.arm.OpCode.thumb16.instruction.ADD_A8_310;
import com.marik.arm.OpCode.thumb16.instruction.BLX_A8_350;
import com.marik.arm.OpCode.thumb16.instruction.BX_A8_352;
import com.marik.arm.OpCode.thumb16.instruction.CMP_A8_372;
import com.marik.arm.OpCode.thumb16.instruction.MOV_A8_486;
import com.marik.arm.OpCode.thumb16.instruction.factory.ParseSupport;

public class SpecialDataInstrucationsAndBranchAndExchange_A6_226 {

	public static ParseSupport parse(int data) {
		int OpCode = getShiftInt(data, 6, 4);

		if (OpCode == 0b0000)
			return ADD_A8_310.INSTANCE;

		if (OpCode == 0b0001 || (assert0(OpCode, 2, 3) && assert1(OpCode, 1)))
			return ADD_A8_310.INSTANCE;

		if (assert0(OpCode, 3) && assert1(OpCode, 2))
			return CMP_A8_372.INSTANCE;

		if (OpCode == 0B1000)
			return MOV_A8_486.INSTANCE;

		if (OpCode == 0b1001 || (assert0(OpCode, 2) && assert1(OpCode, 1, 3)))
			return MOV_A8_486.INSTANCE;

		if (assert0(OpCode, 1) && assert1(OpCode, 2, 3))
			return BX_A8_352.INSTANCE;

		if (assert1(OpCode, 1, 2, 3))
			return BLX_A8_350.INSTANCE;

		throw new IllegalArgumentException("Unable to decode instruction " + Integer.toBinaryString(data));
	}
}
