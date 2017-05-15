package brilliant.arm.OpCode.arm.instructionSet;

import static brilliant.arm.OpCode.OpUtil.*;
import brilliant.arm.OpCode.ParseTemplate;
import brilliant.arm.OpCode.arm.instruction.support.ParseSupport;

public class ConditionParseFactory {

	public static ParseSupport parseCondition(int data) {
		int op1 = getShiftInt(data, 25, 3);
		int op = getShiftInt(data, 4, 1);

		if (assert0(op1, 1, 2))
			return DataProcessingAndMiscellaneousInstructions_A5_196.parse(data);//finish

		if (assert0(op1, 0, 2) && assert1(op1, 1))
			return LoadAndStore_A5_208.parse(data);//finish

		if (assert0(op1, 2) && assert1(op1, 0, 1))
			if (assert0(op , 0))
				return LoadAndStore_A5_208.parse(data);//finish
			else
				return MediaInstruction_A5_209.parse(data);//unimplemented

		if (assert0(op1, 1) && assert1(op1, 2))
			return BranchWithLinkAndBlockDataTransfer_A5_214.parse(data);

		if (assert1(op1, 1, 2))
			return CoprocessorInstructionAndSupervisorCall_A5_215.parse(data);

		throw new IllegalStateException("can't parse instruction " + Integer.toBinaryString(data));
	}

}
