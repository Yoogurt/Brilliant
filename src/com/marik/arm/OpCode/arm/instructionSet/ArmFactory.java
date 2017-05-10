package com.marik.arm.OpCode.arm.instructionSet;

import static com.marik.arm.OpCode.OpUtil.assert1;

import com.marik.arm.OpCode.arm.instruction.factory.ParseSupport;
import com.marik.arm.OpCode.arm.instructionSet.factory.ConditionParseFactory;
import com.marik.arm.OpCode.arm.instructionSet.factory.UnConditionParseFactory;

public class ArmFactory {
	public static ParseSupport parse(int data) {

		if (!assert1(data, 28, 29, 30, 31))
			return ConditionParseFactory.parseCondition(data);
		else
			return UnConditionParseFactory.parseUncondition(data);

	}
}
