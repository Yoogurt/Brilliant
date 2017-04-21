package com.marik.arm.OpCode;

import static com.marik.arm.OpCode.OpUtil.*;

import com.marik.arm.OpCode.instructionSet.BranchWithLinkAndBlockDataTransfer_A5_214;
import com.marik.arm.OpCode.instructionSet.CoprocessorInstructionAndSupervisorCall_A5_215;
import com.marik.arm.OpCode.instructionSet.DataProcessingAndMiscellaneousInstructions_A5_190;
import com.marik.arm.OpCode.instructionSet.LoadAndStoreWord_A5_208;
import com.marik.arm.OpCode.instructionSet.MediaInstruction_A5_209;

public class ConditionParseFactory {

	public static String parseCondition(int data) {
		int op1 = getShiftInt(data, 25, 3);
		int op = getShiftInt(data, 4, 1);

		if (assert0(op1, 1, 2))
			return DataProcessingAndMiscellaneousInstructions_A5_190.parse(data);

		if (assert0(op1, 0, 2) && assert1(op1, 1))
			return LoadAndStoreWord_A5_208.parse(data);

		if (assert0(op1, 2) && assert1(op1, 0, 1))
			if (assertBit0(op))
				return LoadAndStoreWord_A5_208.parse(data);
			else
				return MediaInstruction_A5_209.parse(data);

		if (assert0(op1, 1) && assert1(op1, 2))
			return BranchWithLinkAndBlockDataTransfer_A5_214.parse(data);

		if (assert1(op1, 1, 2))
			return CoprocessorInstructionAndSupervisorCall_A5_215.parse(data);

		throw new IllegalStateException("can't parse instruction " + Integer.toBinaryString(data));
	}

}
