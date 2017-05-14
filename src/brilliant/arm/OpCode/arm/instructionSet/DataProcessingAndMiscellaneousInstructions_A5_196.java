package brilliant.arm.OpCode.arm.instructionSet;

import static brilliant.arm.OpCode.OpUtil.assert0;
import static brilliant.arm.OpCode.OpUtil.assert1;
import static brilliant.arm.OpCode.OpUtil.getShiftInt;
import brilliant.arm.OpCode.arm.instruction.MOVT_A8_491;
import brilliant.arm.OpCode.arm.instruction.MOV_A8_484;
import brilliant.arm.OpCode.arm.instruction.support.ParseSupport;

public class DataProcessingAndMiscellaneousInstructions_A5_196 {
	public static ParseSupport parse(int data) {

		int op = getShiftInt(data, 25, 1);
		int op1 = getShiftInt(data, 20, 5);
		int op2 = getShiftInt(data, 4, 4);

		if (op == 0b0) {
			if (!assert0(op1, 0, 3) || !assert1(op1, 4))/* op1 != 10xx0 */
				if (assert0(op2, 0))/* xxx0 */
					return DataProcessingRegister_A5_197.parse(data);
				else if (assert0(op2, 3))/* 0xx1 */
					return DataProcessingRigsterShiftedRegister_A5_198
							.parse(data);

			if (assert0(op1, 0, 3) && assert1(op1, 4))/* 10xx0 */
				if (assert0(op2, 3))/* 0xxx */
					return MiscellaneousInstruction_A5_207.parse(data);
				else if (assert0(op2, 0))/* 1xx0 */
					return HalfwordAndMultiplyAccumulate_A5_203.parse(data);

			if (assert0(op1, 4))/* 0xxx */
				if (op2 == 0b1001)
					return MultiplyAndMultiplyAccumulate_A5_202.parse(data);

			if (assert1(op1, 4))/* 1xxx */
				if (op2 == 0b1001)
					return SynachronizationPrimitives_A5_205.parse(data);

			if (!assert0(op1, 4) || !assert1(op1, 1))/* not 0xx1x */
				if (op2 == 0b1011)
					return ExtraLoadOrStoreInstructions_A5_203.parse(data);
				else if (assert1(op2, 0, 2, 3))/* 11x1 */
					return ExtraLoadOrStoreInstructions_A5_203.parse(data);

			if (assert0(op1, 0, 4) && assert1(op1, 1))/* 0xx10 */
				if (assert1(op2, 0, 2, 3))/* 11x1 */
					return ExtraLoadOrStoreInstructions_A5_203.parse(data);

			if (assert0(op1, 4) && assert1(op1, 1))/* 0xx1x */
				if (op2 == 0b1011)
					return ExtraLoadOrStoreInstructionUnprivileged_A5_204.parse(data);

			if (assert0(op1, 4) && assert1(op1, 0, 1))/* 0xx11 */
				if (assert1(op2, 0, 2, 3))/* 11x1 */
					return ExtraLoadOrStoreInstructionUnprivileged_A5_204.parse(data);

		}
		
		if(op == 0b1){
			
			if(!(assert0(op1 , 0,3) && assert1(op1 , 4)))
				return DataProcessingImmediate_A5_199.parse(data);
			
			if(op1 == 0b10000)
				return MOV_A8_484.INSTANCE;
			
			if(op1 == 0b10100)
				return MOVT_A8_491.INSTANCE;
			
			if(assert0(op1 , 0,3)&& assert1(op1 , 1,4))
				return MSRImmediate_A5_206.parse(data);
		}

		throw new IllegalArgumentException("cann't parse instruction "
				+ Integer.toBinaryString(data));
	}

}
