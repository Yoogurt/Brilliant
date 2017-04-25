package com.marik.arm.OpCode.thumb16.instructionSet;
import static com.marik.arm.OpCode.OpUtil.*;

import com.marik.arm.OpCode.thumb16.instruction.ADD_A8_310;
import com.marik.arm.OpCode.thumb16.instruction.CMP_A8_372;

public class SpecialDataInstrucationsAndBranchAndExchange_A6_226 {

	public static String parse(int data) {
		int OpCode = getShiftInt(data, 6, 4);
		
		if(OpCode == 0b0000)
			return ADD_A8_310.INSTANCE.parse(data);
		
		if(OpCode == 0b0001 || (assert0(OpCode, 2,3) && assert1(OpCode , 1)))
			return ADD_A8_310.INSTANCE.parse(data);
		
		if(assert0(OpCode , 3) && assert1(OpCode , 2))
			return null;
		
		return null;
	}
}
