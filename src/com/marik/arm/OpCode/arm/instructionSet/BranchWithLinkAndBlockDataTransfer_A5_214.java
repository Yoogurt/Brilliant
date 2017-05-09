package com.marik.arm.OpCode.arm.instructionSet;

import com.marik.arm.OpCode.ParseTemplate;
import static com.marik.arm.OpCode.OpUtil.*;

public class BranchWithLinkAndBlockDataTransfer_A5_214 {
	public static ParseTemplate parse(int data) {
		
		int op = getShiftInt(data, 20, 6);
		int R = getShiftInt(data, 15, 1);
		int Rn = getShiftInt(data, 16, 4);
		
		return null;
	}
}
