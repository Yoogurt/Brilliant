package brilliant.arm.OpCode.thumb.instructionSet32;

import brilliant.arm.OpCode.thumb.instruction32.support.ParseSupport;
import static brilliant.arm.OpCode.OpUtil.*;

class LoadWordA6_239 {
	public static ParseSupport parse(int data) {
		
		int op1 = getShiftInt(data, 23, 2);
		int op2 = getShiftInt(data, 6, 6);
		int Rn = getShiftInt(data, 16, 4);
		
		throw new IllegalArgumentException("Unable to decode instruction "
				+ Integer.toBinaryString(data));
	}
}
