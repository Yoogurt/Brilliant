package com.marik.arm.OpCode;

import com.marik.arm.OpCode.arm.instructionSet.ArmFactory;
import com.marik.arm.OpCode.thumb.ThumbFactory;
import com.marik.vm.Register;
import com.marik.vm.Register.RegisterIllegalStateExeception;

public class OpCode {

	public static void decode(int[] data) {
		switch (Register.getT()) {
		case 0:
			decodeArm(data);
			break;
		case 1:
			decodeThumb(data);
			break;
		default:
			throw new RegisterIllegalStateExeception(
					"Flag Rigister has accessed an unpredictable state");
		}
	}

	private static void decodeArm(int[] data) {
		for (int i : data)
			System.out.println(ArmFactory.parse(i).parse(i));
	}

	public static void decodeThumb(int[] data) {

		int length = data.length;
		for (int i = 0; i < length; i++) {

			ParseTemplate opcode = ThumbFactory.parse(data[i], true);

			if (opcode != null)
				System.out.println(opcode.parse(data[i]));
			else {
				int command = data[i] << 16 | data[++i];
				opcode = ThumbFactory.parse(command, false);
				if (opcode != null)
					System.out.println(opcode.parse(command));
				else
					throw new IllegalArgumentException(
							"Unable to decode instruction "
									+ Integer.toBinaryString(command));
			}
		}
	}

	public static ParseTemplate decodeThumb32(int data) {
		throw new UnsupportedOperationException("Thumb 32 do not implements");
	}

	public static void main(String[] args) {
		// access thumb mode
		decodeArm1(0xea00002a);
	}

	private static void decodeArm1(int... opcode) {
		Register.setT(0);
		decode(opcode);
	}
	
	private static void decodeThumb1(int... opcode) {
		Register.setT(1);
		decode(opcode);
	}
}
