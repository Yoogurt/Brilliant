package com.marik.arm.OpCode.thumb.instructionSet16;

import com.marik.arm.OpCode.thumb.instruction16.B_A8_334;
import com.marik.arm.OpCode.thumb.instruction16.support.ParseSupport;

class ConditionalBranchAndSupervisorCall_A6_229 {
	public static ParseSupport parse(int data) {
		return B_A8_334.INSTANCE;
	}
}
