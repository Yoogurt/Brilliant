package com.marik.arm.OpCode.thumb.instructionSet32;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

import com.marik.arm.OpCode.thumb.instruction32.ADC_A8_302;
import com.marik.arm.OpCode.thumb.instruction32.ADD_A8_310;
import com.marik.arm.OpCode.thumb.instruction32.AND_A8_326;
import com.marik.arm.OpCode.thumb.instruction32.BIC_A8_342;
import com.marik.arm.OpCode.thumb.instruction32.CMN_A8_366;
import com.marik.arm.OpCode.thumb.instruction32.CMP_A8_372;
import com.marik.arm.OpCode.thumb.instruction32.EOR_A8_384;
import com.marik.arm.OpCode.thumb.instruction32.MVN_A8_506;
import com.marik.arm.OpCode.thumb.instruction32.ORN_A8_514;
import com.marik.arm.OpCode.thumb.instruction32.ORR_A8_518;
import com.marik.arm.OpCode.thumb.instruction32.PKH_A8_522;
import com.marik.arm.OpCode.thumb.instruction32.RSB_A8_576;
import com.marik.arm.OpCode.thumb.instruction32.SBC_A8_594;
import com.marik.arm.OpCode.thumb.instruction32.SUB_A8_712;
import com.marik.arm.OpCode.thumb.instruction32.TEQ_A8_740;
import com.marik.arm.OpCode.thumb.instruction32.TST_A8_746;
import com.marik.arm.OpCode.thumb.instruction32.support.ParseSupport;

import static com.marik.arm.OpCode.OpUtil.*;

class DataProcessingA6_243 {
	public static ParseSupport parse(int data) {

		int op = getShiftInt(data, 21, 4);
		int Rn = getShiftInt(data, 16, 4);
		int Rd_S = getShiftInt(data, 8, 4) << 1 | getShiftInt(data, 20, 1);

		if (op == 0b0000)
			if (Rd_S != 0b11111)
				return AND_A8_326.INSTANCE;
			else
				return TST_A8_746.INSTANCE;

		if (op == 0b0001)
			return BIC_A8_342.INSTANCE;

		if (op == 0b0010)
			if (Rn != 0b1111)
				return ORR_A8_518.INSTANCE;
			else
				return MoveRegisterA3_244.parse(data);

		if (op == 0b0011)
			if (Rn != 0b1111)
				return ORN_A8_514.INSTANCE;
			else
				return MVN_A8_506.INSTANCE;

		if (op == 0b0100)
			if (Rd_S != 0b11111)
				return EOR_A8_384.INSTANCE;
			else
				return TEQ_A8_740.INSTANCE;

		if (op == 0b0110)
			return PKH_A8_522.INSTANCE;

		if (op == 0b1000)
			if (Rd_S != 0b11111)
				return ADD_A8_310.INSTANCE;
			else
				return CMN_A8_366.INSTANCE;

		if (op == 0b1010)
			return ADC_A8_302.INSTANCE;

		if (op == 0b1011)
			return SBC_A8_594.INSTANCE;

		if (op == 0b1101)
			if (Rd_S != 0b11111)
				return SUB_A8_712.INSTANCE;
			else
				return CMP_A8_372.INSTANCE;

		if (op == 0b1110)
			return RSB_A8_576.INSTANCE;

		throw new IllegalArgumentException("Unable to decode instruction "
				+ Integer.toBinaryString(data));
	}
}
