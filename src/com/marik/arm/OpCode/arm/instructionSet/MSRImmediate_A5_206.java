package com.marik.arm.OpCode.arm.instructionSet;

import static com.marik.arm.OpCode.OpUtil.assert0;
import static com.marik.arm.OpCode.OpUtil.assert1;
import static com.marik.arm.OpCode.OpUtil.getShiftInt;

import com.marik.arm.OpCode.arm.instruction.DBG_A8_377;
import com.marik.arm.OpCode.arm.instruction.MSR_A8_498;
import com.marik.arm.OpCode.arm.instruction.MSR_B8_1996;
import com.marik.arm.OpCode.arm.instruction.NOP_A8_510;
import com.marik.arm.OpCode.arm.instruction.SEV_A8_606;
import com.marik.arm.OpCode.arm.instruction.WFE_A8_1104;
import com.marik.arm.OpCode.arm.instruction.WFI_A8_1106;
import com.marik.arm.OpCode.arm.instruction.YIELD_A8_1108;
import com.marik.arm.OpCode.arm.instruction.factory.ParseSupport;

@SuppressWarnings("deprecation")
public class MSRImmediate_A5_206 {
	public static ParseSupport parse(int data) {

		int op = getShiftInt(data, 22, 1);
		int op1 = getShiftInt(data, 16, 4);
		int op2 = getShiftInt(data, 0, 8);

		if (op == 0b0) {
			if (op1 == 0b0000) {
				switch (op2) {
				case 0b00000000:
					return NOP_A8_510.INSTANCE;
				case 0b00000001:
					return YIELD_A8_1108.INSTANCE;
				case 0b00000010:
					return WFE_A8_1104.INSTANCE;
				case 0b00000011:
					return WFI_A8_1106.INSTANCE;
				case 0b00000100:
					return SEV_A8_606.INSTANCE;
				}

				if (assert1(op2, 4, 5, 6, 7))
					return DBG_A8_377.INSTANCE;
			}

			if (op1 == 0b0100 || (assert0(op1, 0, 1) && assert1(op1, 3)))
				return MSR_A8_498.INSTANCE;

			if ((assert0(op1, 1) && assert1(op1, 0)) || (assert1(op1, 1)))
				return MSR_B8_1996.INSTANCE;
		}

		if (op == 0b1) 
			return MSR_B8_1996.INSTANCE;

		throw new IllegalArgumentException("Unable to decode instruction "
				+ Integer.toBinaryString(data));
	}
}
