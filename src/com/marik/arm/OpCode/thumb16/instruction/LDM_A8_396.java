package com.marik.arm.OpCode.thumb16.instruction;

import com.marik.arm.OpCode.OpUtil;
import com.marik.arm.OpCode.thumb16.instruction.factory.ParseSupport;

public class LDM_A8_396 implements ParseSupport {
	public static final LDM_A8_396 INSTANCE = new LDM_A8_396();

	public String parse(int data) {

		int head = OpUtil.getShiftInt(data, 12, 4);
		if (head == 0b11001)
			decodeThumb16(data);

		throw new IllegalArgumentException("Unable to decode instruction " + Integer.toBinaryString(data));
	}

	private String decodeThumb16(int data) {

		StringBuilder sb = new StringBuilder("LDM");

		return sb.toString();
	}

}
