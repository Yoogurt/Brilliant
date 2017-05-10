package com.marik.arm.OpCode.arm.instructionSet;

import static com.marik.arm.OpCode.OpUtil.assert0;
import static com.marik.arm.OpCode.OpUtil.assert1;
import static com.marik.arm.OpCode.OpUtil.getShiftInt;

import com.marik.arm.OpCode.arm.instruction.ADC_A8_300;
import com.marik.arm.OpCode.arm.instruction.ADD_A8_308;
import com.marik.arm.OpCode.arm.instruction.ADR_A8_322;
import com.marik.arm.OpCode.arm.instruction.AND_A8_324;
import com.marik.arm.OpCode.arm.instruction.BIC_A8_340;
import com.marik.arm.OpCode.arm.instruction.CMN_A8_364;
import com.marik.arm.OpCode.arm.instruction.CMP_A8_370;
import com.marik.arm.OpCode.arm.instruction.EOR_A8_382;
import com.marik.arm.OpCode.arm.instruction.MOV_A8_484;
import com.marik.arm.OpCode.arm.instruction.MVN_A8_504;
import com.marik.arm.OpCode.arm.instruction.ORR_A8_516;
import com.marik.arm.OpCode.arm.instruction.RSB_A8_574;
import com.marik.arm.OpCode.arm.instruction.RSC_A8_580;
import com.marik.arm.OpCode.arm.instruction.SBC_A8_592;
import com.marik.arm.OpCode.arm.instruction.SUB_A8_710;
import com.marik.arm.OpCode.arm.instruction.TEQ_A8_738;
import com.marik.arm.OpCode.arm.instruction.TST_A8_744;
import com.marik.arm.OpCode.arm.instruction.factory.ParseSupport;

public class DataProcessingImmediate_A5_199 {
	public static ParseSupport parse(int data) {

		int op = getShiftInt(data, 20, 5);
		int Rn = getShiftInt(data, 16, 4);

		if (assert0(op, 1, 2, 3, 4))
			return AND_A8_324.INSTANCE;

		if (assert0(op, 2, 3, 4) && assert1(op, 1))
			return EOR_A8_382.INSTANCE;

		if (assert0(op, 1, 3, 4) && assert1(op, 2))
			if (Rn != 0b1111)
				return SUB_A8_710.INSTANCE;
			else
				return ADR_A8_322.INSTANCE;

		if (assert0(op, 3, 4) && assert1(op, 1, 2))
			return RSB_A8_574.INSTANCE;

		if (assert0(op, 1, 2, 4) && assert1(op, 3))
			if (Rn != 0b1111)
				return ADD_A8_308.INSTANCE;
			else
				return ADR_A8_322.INSTANCE;

		if (assert0(op, 2, 4) && assert1(op, 1, 3))
			return ADC_A8_300.INSTANCE;

		if (assert0(op, 1, 4) && assert1(op, 2, 3))
			return SBC_A8_592.INSTANCE;

		if (assert0(op, 4) && assert1(1, 2, 3))
			return RSC_A8_580.INSTANCE;

		if (assert0(op, 0, 3) && assert1(op, 4))
			return DataProcessingAndMiscellaneousInstructions_A5_196
					.parse(data);

		switch (op) {
		case 0b10001:
			return TST_A8_744.INSTANCE;
		case 0b10011:
			return TEQ_A8_738.INSTANCE;
		case 0b10101:
			return CMP_A8_370.INSTANCE;
		case 0b10111:
			return CMN_A8_364.INSTANCE;
		}

		if (assert0(op, 1, 2) && assert1(op, 3, 4))
			return ORR_A8_516.INSTANCE;

		if (assert0(op, 2) && assert1(op, 1, 3, 4))
			return MOV_A8_484.INSTANCE;

		if (assert0(op, 1) && assert1(op, 2, 3, 4))
			return BIC_A8_340.INSTANCE;

		if (assert1(op, 1, 2, 3, 4))
			return MVN_A8_504.INSTANCE;

		throw new IllegalArgumentException("Unable to deocde instruction "
				+ Integer.toBinaryString(data));
	}
}
