package com.marik.arm.OpCode.thumb16.instructionSet;

import com.marik.arm.OpCode.thumb16.instruction.B_A8_334;
import com.marik.arm.OpCode.thumb16.instruction.factory.ParseSupport;

class ConditionalBranchAndSupervisorCall_A6_229 {
	public static ParseSupport parse(int data) {
		return B_A8_334.INSTANCE;
	}
}
