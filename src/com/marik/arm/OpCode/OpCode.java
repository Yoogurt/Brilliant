package com.marik.arm.OpCode;

import static com.marik.arm.OpCode.OpUtil.*;

import com.marik.arm.OpCode.arm.instructionSet.ArmFactory;
import com.marik.arm.OpCode.arm.instructionSet.factory.ConditionParseFactory;
import com.marik.arm.OpCode.arm.instructionSet.factory.UnConditionParseFactory;
import com.marik.arm.OpCode.thumb16.instruction.factory.ParseSupport;
import com.marik.arm.OpCode.thumb16.instructionSet.Thumb16Factory;
import com.marik.util.ByteUtil;
import com.marik.vm.Register;
import com.marik.vm.Register.RegisterIllegalStateExeception;

public class OpCode {

	public static String decode(byte[] data) {
		int command = ByteUtil.bytes2Int32(data);

		switch (Register.getT()) {
		case 0:
			return decodeArm(command);

		case 1:
			return decodeThumb16(command);
		default:
			throw new RegisterIllegalStateExeception(
					"Flag Rigister has accessed an unpredictable state");
		}
	}

	public static String decode(int data) {
		switch (Register.getT()) {
		case 0:
			return decodeArm(data);
		case 1:
			return decodeThumb16(data);
		default:
			throw new RegisterIllegalStateExeception(
					"Flag Rigister has accessed an unpredictable state");
		}
	}

	private static String decodeArm(int data) {
		return ArmFactory.parse(data).parse(data);
	}

	public static String decodeThumb16(int data) {
		return Thumb16Factory.parse(data).parse(data);
	}

	public static ParseTemplate decodeThumb32(int data) {
		throw new UnsupportedOperationException("Thumb 32 do not implements");
	}

	public static void main(String[] args) {
		// access thumb mode
		decodeArm1(0b11100100111100000010000000001111);
	}

	private static void decodeArm1(int... opcode) {
		Register.setT(0);
		for (int x : opcode) {
			System.out.println(decode(x));
		}
	}
}
