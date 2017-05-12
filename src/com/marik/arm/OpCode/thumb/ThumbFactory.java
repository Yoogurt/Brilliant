package com.marik.arm.OpCode.thumb;

import com.marik.arm.OpCode.ParseTemplate;
import com.marik.arm.OpCode.thumb.instructionSet16.Thumb16Factory;
import com.marik.arm.OpCode.thumb.instructionSet32.Thumb32Factory;

import static com.marik.arm.OpCode.OpUtil.*;

public class ThumbFactory {

	public static final ParseTemplate parse(int data, boolean halfword) {

		if (halfword)
			switch (getShiftInt(data, 11, 5)) {
			case 0b11101:
			case 0b11110:
			case 0b11111:
				return null;
			default:
				return Thumb16Factory.parse(data);
			}
		else
			return Thumb32Factory.parse(data);
	}
}
