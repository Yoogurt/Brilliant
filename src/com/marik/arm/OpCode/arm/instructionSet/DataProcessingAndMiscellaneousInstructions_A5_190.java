package com.marik.arm.OpCode.arm.instructionSet;

import static com.marik.arm.OpCode.OpUtil.assert0;
import static com.marik.arm.OpCode.OpUtil.assert1;
import static com.marik.arm.OpCode.OpUtil.getShiftInt;

public class DataProcessingAndMiscellaneousInstructions_A5_190 {
	public static String parse(int data) {

		int op = getShiftInt(data, 25, 1);
		int op1 = getShiftInt(data, 20, 5);
		int op2 = getShiftInt(data, 4, 4);

		if (assert0(op, 0)) {
			if (!assert0(op1, 0, 3) || !assert1(op1, 4))/* op1 != 10xx0 */
				if (assert0(op2, 0))/* xxx0 */
					return DataProcessingRegister_A5_197(data);
				else if (assert0(op2, 3))/* 0xx1 */
					return DataProcessingRigsterShiftedRegister_A5_198(data);

			if (assert0(op1, 0, 3) && assert1(op1, 4))/* 10xx0 */
				if (assert0(op2, 3))/* 0xxx */
					return MiscellaneousInstruction_A5_207(data);
				else if (assert0(op2, 0))/* 1xx0 */
					return HalfwordAndMultiplyAccumulate_A5_203(data);

			if (assert0(op1, 4))/* 0xxx */
				if (op2 == 0b1001)
					return MultiplyAndMultiplyAccumulate_A5_202(data);

			if (assert1(op1, 4))/* 1xxx */
				if (op2 == 0b1001)
					return SynachronizationPrimitives_A5_205(data);

			if (!assert0(op1, 4) || !assert1(op1, 1))/* not 0xx1x */
				if (op2 == 0b1011)
					return ExtraLoadOrStoreInstructions_A5_203(data);
				else if (assert1(op2, 0, 2, 3))/* 11x1 */
					return ExtraLoadOrStoreInstructions_A5_203(data);

			if (assert0(op1, 0, 4) && assert1(op1, 1))/* 0xx10 */
				if (assert1(op2, 0, 2, 3))/* 11x1 */
					return ExtraLoadOrStoreInstructions_A5_203(data);

			if (assert0(op1, 4) && assert1(op1, 1))/* 0xx1x */
				if (op2 == 0b1011)
					return ExtraLoadOrStoreInstructionUnprivileged(data);

			if (assert0(op1, 4) && assert1(op1, 0, 1))/* 0xx11 */
				if (assert1(op2, 0, 2, 3))/* 11x1 */
					return ExtraLoadOrStoreInstructionUnprivileged(data);

		} else {

		}

		throw new IllegalArgumentException("cann't parse instruction " + Integer.toBinaryString(data));
	}

	private static String ExtraLoadOrStoreInstructionUnprivileged(int data) {
		return "ExtraLoadOrStoreInstructionUnprivileged";
	}

	private static String ExtraLoadOrStoreInstructions_A5_203(int data) {
		return "ExtraLoadOrStoreInstructions_A5_203";
	}

	private static String SynachronizationPrimitives_A5_205(int data) {
		return "SynachronizationPrimitives_A5_205";
	}

	private static String MultiplyAndMultiplyAccumulate_A5_202(int data) {
		return "MultiplyAndMultiplyAccumulate_A5_202";
	}

	private static String HalfwordAndMultiplyAccumulate_A5_203(int data) {
		return "HalfwordAndMultiplyAccumulate_A5_203";
	}

	private static String MiscellaneousInstruction_A5_207(int data) {
		return "MiscellaneousInstruction_A5_207";
	}

	private static String DataProcessingRigsterShiftedRegister_A5_198(int data) {
		return "DataProcessingRigsterShiftedRegister_A5_198";
	}

	private static String DataProcessingRegister_A5_197(int data) {

		int op = getShiftInt(data, 20, 5);

		if (assert0(op, 1, 2, 3, 4))
			return null;

		if (assert0(op, 2, 3, 4) && assert1(op, 1))
			return null;

		return null;
	}

}
