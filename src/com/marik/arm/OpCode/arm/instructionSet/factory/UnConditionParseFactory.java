package com.marik.arm.OpCode.arm.instructionSet.factory;

import com.marik.arm.OpCode.arm.instruction.factory.ParseSupport;
import com.marik.arm.OpCode.arm.instructionSet.MemoryHintAdvancedSIMDInstructions_A5_217;

import static com.marik.arm.OpCode.OpUtil.*;

public class UnConditionParseFactory {

	public static ParseSupport parseUncondition(int data) {

		int op1 = getShiftInt(data, 20, 8);
		int op = getShiftInt(data, 4, 1);
		int Rn = getShiftInt(data, 16, 4);

		if (assert0(op1, 7))
			return MemoryHintAdvancedSIMDInstructions_A5_217.parse(data);
		
		throw new IllegalArgumentException("Unable to decode instruction "
				+ Integer.toBinaryString(data));
	}

}
