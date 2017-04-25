package com.marik.arm.OpCode.thumb16.instructionSet;

import static com.marik.arm.OpCode.OpUtil.*;

import com.marik.arm.OpCode.thumb16.instruction.ADC_A8_302;
import com.marik.arm.OpCode.thumb16.instruction.AND_A8_326;
import com.marik.arm.OpCode.thumb16.instruction.ASR_A8_332;
import com.marik.arm.OpCode.thumb16.instruction.BIC_A8_342;
import com.marik.arm.OpCode.thumb16.instruction.CMN_A8_366;
import com.marik.arm.OpCode.thumb16.instruction.CMP_A8_372;
import com.marik.arm.OpCode.thumb16.instruction.EOR_A8_384;
import com.marik.arm.OpCode.thumb16.instruction.LSL_A8_470;
import com.marik.arm.OpCode.thumb16.instruction.LSR_A8_474;
import com.marik.arm.OpCode.thumb16.instruction.MUL_A8_502;
import com.marik.arm.OpCode.thumb16.instruction.MVN_A8_506;
import com.marik.arm.OpCode.thumb16.instruction.ORR_A8_518;
import com.marik.arm.OpCode.thumb16.instruction.ROR_A8_570;
import com.marik.arm.OpCode.thumb16.instruction.RSB_A8_574;
import com.marik.arm.OpCode.thumb16.instruction.SBC_A8_594;
import com.marik.arm.OpCode.thumb16.instruction.TST_A8_746;

public class DataProcessing_A6_225 {

	public static String parse(int data) {
		int OpCode = getShiftInt(data, 6, 4);

		switch (OpCode) {
		case 0b0000:
			return AND_A8_326.INSTANCE.parse(data);
		case 0b0001:
			return EOR_A8_384.INSTANCE.parse(data);
		case 0b0010:
			return LSL_A8_470.INSTANCE.parse(data);
		case 0b0011:
			return LSR_A8_474.INSTANCE.parse(data);
		case 0b0100:
			return ASR_A8_332.INSTANCE.parse(data);
		case 0b0101:
			return ADC_A8_302.INSTANCE.parse(data);
		case 0b0110:
			return SBC_A8_594.INSTANCE.parse(data);
		case 0b0111:
			return ROR_A8_570.INSTANCE.parse(data);
		case 0b1000:
			return TST_A8_746.INSTANCE.parse(data);
		case 0b1001:
			return RSB_A8_574.INSTANCE.parse(data);
		case 0b1010:
			return CMP_A8_372.INSTANCE.parse(data);
		case 0b1011:
			return CMN_A8_366.INSTANCE.parse(data);
		case 0b1100:
			return ORR_A8_518.INSTANCE.parse(data);
		case 0b1101:
			return MUL_A8_502.INSTANCE.parse(data);
		case 0b1110:
			return BIC_A8_342.INSTANCE.parse(data);
		case 0b1111:
			return MVN_A8_506.INSTANCE.parse(data);
		default:
			throw new IllegalArgumentException("Unable to decode instruction " + Integer.toBinaryString(data));
		}
	}
}
