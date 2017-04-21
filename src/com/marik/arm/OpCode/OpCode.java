package com.marik.arm.OpCode;

import static com.marik.arm.OpCode.OpUtil.*;

import com.marik.util.Util;

public class OpCode {

	public static String decode(byte[] data) {
		return decode(Util.bytes2Int32(data));
	}

	public static String decode(int data) {

		int cond = getShiftInt(data, 28, 4);

		if (!assert1(cond, 0, 1, 2, 3))
			return ConditionParseFactory.parseCondition(data);
		else
			return UnConditionParseFactory.parseUncondition(data);
	}
	
	public static void main(String[] args) {
		System.out.println(decode(0x016000B0));
	}

}
