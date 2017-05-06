package com.marik.arm.OpCode.arm.instructionSet;

import static com.marik.arm.OpCode.OpUtil.assert0;
import static com.marik.arm.OpCode.OpUtil.assert1;
import static com.marik.arm.OpCode.OpUtil.getShiftInt;

import com.marik.arm.OpCode.ParseTemplate;
import com.marik.arm.OpCode.arm.instructionSet.factory.ConditionParseFactory;
import com.marik.arm.OpCode.arm.instructionSet.factory.UnConditionParseFactory;
import com.marik.arm.OpCode.thumb16.instruction.ADD_A8_316;
import com.marik.arm.OpCode.thumb16.instruction.ADR_A8_322;
import com.marik.arm.OpCode.thumb16.instruction.B_A8_334;
import com.marik.arm.OpCode.thumb16.instruction.LDM_A8_396;
import com.marik.arm.OpCode.thumb16.instruction.LDR_A8_410;
import com.marik.arm.OpCode.thumb16.instruction.STM_A8_664;
import com.marik.arm.OpCode.thumb16.instruction.factory.ParseSupport;

public class ArmFactory {
	public static ParseTemplate parse(int data) {

			if (!assert1(data, 28, 29, 30, 31))
				return ConditionParseFactory.parseCondition(data);
			else
				return UnConditionParseFactory.parseUncondition(data);

	}
}
