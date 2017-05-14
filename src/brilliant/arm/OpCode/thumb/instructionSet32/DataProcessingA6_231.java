package brilliant.arm.OpCode.thumb.instructionSet32;

import static brilliant.arm.OpCode.OpUtil.getShiftInt;
import brilliant.arm.OpCode.thumb.instruction32.ADC_A8_300;
import brilliant.arm.OpCode.thumb.instruction32.ADD_A8_306;
import brilliant.arm.OpCode.thumb.instruction32.AND_A8_324;
import brilliant.arm.OpCode.thumb.instruction32.BIC_A8_340;
import brilliant.arm.OpCode.thumb.instruction32.CMN_A8_364;
import brilliant.arm.OpCode.thumb.instruction32.CMP_A8_370;
import brilliant.arm.OpCode.thumb.instruction32.EOR_A8_382;
import brilliant.arm.OpCode.thumb.instruction32.MOV_A8_484;
import brilliant.arm.OpCode.thumb.instruction32.MVN_A8_504;
import brilliant.arm.OpCode.thumb.instruction32.ORN_A8_512;
import brilliant.arm.OpCode.thumb.instruction32.ORR_A8_516;
import brilliant.arm.OpCode.thumb.instruction32.RSB_A8_574;
import brilliant.arm.OpCode.thumb.instruction32.SBC_A8_592;
import brilliant.arm.OpCode.thumb.instruction32.SUB_A8_708;
import brilliant.arm.OpCode.thumb.instruction32.TEQ_A8_738;
import brilliant.arm.OpCode.thumb.instruction32.TST_A8_744;
import brilliant.arm.OpCode.thumb.instruction32.support.ParseSupport;

class DataProcessingA6_231 {
	public static ParseSupport parse(int data) {
		int op = getShiftInt(data, 21, 4);
		int Rn = getShiftInt(data, 16, 4);
		int Rd_S = getShiftInt(data, 8, 4) << 1 | getShiftInt(data, 20, 1);

		if (op == 0b0000)
			if (Rd_S != 0b11111)
				return AND_A8_324.INSTANCE;
			else
				return TST_A8_744.INSTANCE;

		if (op == 0b0001)
			return BIC_A8_340.INSTANCE;

		if (op == 0b0010)
			if (Rn != 0b1111)
				return ORR_A8_516.INSTANCE;
			else
				return MOV_A8_484.INSTANCE;

		if (op == 0b0011)
			if (Rn != 0b1111)
				return ORN_A8_512.INSTANCE;
			else
				return MVN_A8_504.INSTANCE;

		if (op == 0b0100)
			if (Rd_S != 0b11111)
				return EOR_A8_382.INSTANCE;
			else
				return TEQ_A8_738.INSTANCE;

		if (op == 0b1000)
			if (Rd_S != 0b11111)
				return ADD_A8_306.INSTANCE;
			else
				return CMN_A8_364.INSTANCE;

		if (op == 0b1010)
			return ADC_A8_300.INSTANCE;

		if (op == 0b1011)
			return SBC_A8_592.INSTANCE;

		if (op == 0b1101)
			if (Rd_S != 0b11111)
				return SUB_A8_708.INSTANCE;
			else
				return CMP_A8_370.INSTANCE;

		if (op == 0b1110)
			return RSB_A8_574.INSTANCE;

		throw new IllegalArgumentException("Unable to decode instruction "
				+ Integer.toBinaryString(data));
	}
}
