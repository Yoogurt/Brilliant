package brilliant.arm.OpCode.thumb.instructionSet16;

import static brilliant.arm.OpCode.factory.OpUtil.getShiftInt;
import brilliant.arm.OpCode.thumb.instruction16.ADC_A8_302;
import brilliant.arm.OpCode.thumb.instruction16.AND_A8_326;
import brilliant.arm.OpCode.thumb.instruction16.ASR_A8_332;
import brilliant.arm.OpCode.thumb.instruction16.BIC_A8_342;
import brilliant.arm.OpCode.thumb.instruction16.CMN_A8_366;
import brilliant.arm.OpCode.thumb.instruction16.CMP_A8_372;
import brilliant.arm.OpCode.thumb.instruction16.EOR_A8_384;
import brilliant.arm.OpCode.thumb.instruction16.LSL_A8_470;
import brilliant.arm.OpCode.thumb.instruction16.LSR_A8_474;
import brilliant.arm.OpCode.thumb.instruction16.MUL_A8_502;
import brilliant.arm.OpCode.thumb.instruction16.MVN_A8_506;
import brilliant.arm.OpCode.thumb.instruction16.ORR_A8_518;
import brilliant.arm.OpCode.thumb.instruction16.ROR_A8_570;
import brilliant.arm.OpCode.thumb.instruction16.RSB_A8_574;
import brilliant.arm.OpCode.thumb.instruction16.SBC_A8_594;
import brilliant.arm.OpCode.thumb.instruction16.TST_A8_746;
import brilliant.arm.OpCode.thumb.instruction16.support.ParseSupport;

class DataProcessing_A6_225 {

	public static ParseSupport parse(int data) {
		int OpCode = getShiftInt(data, 6, 4);

		switch (OpCode) {
		case 0b0000:
			return AND_A8_326.INSTANCE;
		case 0b0001:
			return EOR_A8_384.INSTANCE;
		case 0b0010:
			return LSL_A8_470.INSTANCE;
		case 0b0011:
			return LSR_A8_474.INSTANCE;
		case 0b0100:
			return ASR_A8_332.INSTANCE;
		case 0b0101:
			return ADC_A8_302.INSTANCE;
		case 0b0110:
			return SBC_A8_594.INSTANCE;
		case 0b0111:
			return ROR_A8_570.INSTANCE;
		case 0b1000:
			return TST_A8_746.INSTANCE;
		case 0b1001:
			return RSB_A8_574.INSTANCE;
		case 0b1010:
			return CMP_A8_372.INSTANCE;
		case 0b1011:
			return CMN_A8_366.INSTANCE;
		case 0b1100:
			return ORR_A8_518.INSTANCE;
		case 0b1101:
			return MUL_A8_502.INSTANCE;
		case 0b1110:
			return BIC_A8_342.INSTANCE;
		case 0b1111:
			return MVN_A8_506.INSTANCE;
		default:
			throw new IllegalArgumentException("Unable to decode instruction "
					+ Integer.toBinaryString(data));
		}
	}
}
