package com.marik.arm.OpCode;

import static com.marik.arm.OpCode.OpUtil.*;

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
			return decodeArm(command).parse(command);

		case 1:
			return decodeThumb16(command).parse(command);
		default:
			throw new RegisterIllegalStateExeception("Flag Rigister has accessed an unpredictable state");
		}
	}

	public static String decode(int data) {

		switch (Register.getT()) {
		case 0:
			return decodeArm(data).parse(data);

		case 1:
			return decodeThumb16(data).parse(data);
		default:
			throw new RegisterIllegalStateExeception("Flag Rigister has accessed an unpredictable state");
		}
	}

	public static ParseTemplate decodeArm(int data) {

		if (!assert1(data, 28, 29, 30, 31))
			return ConditionParseFactory.parseCondition(data);
		else
			return UnConditionParseFactory.parseUncondition(data);
	}

	public static ParseTemplate decodeThumb16(int data) {
		return Thumb16Factory.parse(data & 0xffff);
	}

	public static ParseTemplate decodeThumb32(int data) {
		throw new UnsupportedOperationException("Thumb 32 do not implements");
	}

	public static void main(String[] args) {
		Register.setT(1); // access thumb mode

		int code = 0x4d1d;
		System.out.println(decodeThumb16(code).parse(code));
	}

}
