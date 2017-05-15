package brilliant.arm.OpCode;

import brilliant.arm.OpCode.arm.instructionSet.ArmFactory;
import brilliant.arm.OpCode.thumb.ThumbFactory;
import brilliant.elf.util.ByteUtil;
import brilliant.elf.vm.Register;
import brilliant.elf.vm.Register.RegisterIllegalStateExeception;

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
		decodeArm(data, 0, data.length);
	}

	private static void decodeArm(int[] data, int start, int size) {
		int current;
		for (int i = 0; i < size; i++) {
			current = i + start;
			System.out.println(ArmFactory.parse(current).parse(current));
		}
	}

	public static void decodeThumb(int[] data) {
		decodeThumb(data, 0, data.length);
	}

	public static void decodeThumb(byte[] data, int start, int size,
			boolean isLittleEndian) {

		int current;

		for (int i = 0; i < size; i += 2) {

			current = start + i;

			int opCode = ByteUtil.bytes2Int32(data, current, 2, isLittleEndian);

			ParseTemplate ret = ThumbFactory.parse(opCode, true);

			if (ret != null)
				System.out.println(ret.parse(opCode));
			else {

				if (i + 2 >= size)
					throw new IllegalArgumentException(
							"Unable to decode instruction "
									+ Integer.toBinaryString(opCode));

				int command = (opCode << 16)
						| ByteUtil.bytes2Int32(data, current + 2, 2,
								isLittleEndian);

				i += 2;
				ret = ThumbFactory.parse(command, false);
				if (ret != null)
					System.out.println(ret.parse(command));
				else
					throw new IllegalArgumentException(
							"Unable to decode instruction "
									+ Integer.toBinaryString(command));
			}
		}
	}

	public static void decodeThumbWithHex(byte[] data, int start, int size,
			boolean isLittleEndian) {

		int current;

		for (int i = 0; i < size; i += 2) {

			current = start + i;

			int opCode = ByteUtil.bytes2Int32(data, current, 2, isLittleEndian);

			ParseTemplate ret = ThumbFactory.parse(opCode, true);
			
			System.out.print(Integer.toHexString(current) + " : ");

			if (ret != null)
				System.out.println(ByteUtil.bytes2Hex(ByteUtil
						.int2bytes(opCode)) + "  " + ret.parse(opCode));
			else {

				if (i + 2 >= size)
					throw new IllegalArgumentException(
							"Unable to decode instruction "
									+ Integer.toBinaryString(opCode));

				int command = (opCode << 16)
						| ByteUtil.bytes2Int32(data, current + 2, 2,
								isLittleEndian);

				i += 2;
				ret = ThumbFactory.parse(command, false);
				if (ret != null)
					System.out.println(ByteUtil.bytes2Hex(ByteUtil
							.int2bytes(opCode)) + "  " + ret.parse(command));
				else
					throw new IllegalArgumentException(
							"Unable to decode instruction "
									+ Integer.toBinaryString(command));
			}
		}
	}

	public static void decodeThumb(int[] data, int start, int size) {

		int current;

		for (int i = 0; i < size; i++) {

			current = start + i;

			ParseTemplate opcode = ThumbFactory.parse(data[current], true);

			if (opcode != null)
				System.out.println(opcode.parse(data[current]));
			else {

				if (i + 1 >= size)
					throw new IllegalArgumentException(
							"Unable to decode instruction "
									+ Integer.toBinaryString(data[current]));

				int command = data[current] << 16 | data[1 + current];
				i++;
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

	public static void main(String[] args) {
		// access thumb mode
		// decodeArm1(0xea00002a);
		byte[] comm = { (byte) 0xc0, (byte) 0xfb, (byte) 0xd1, (byte) 0x10,
				(byte) 0x90, (byte) 0xfa, (byte) 0x90, (byte) 0xf1 };
		decodeThumb(comm, 0, comm.length, true);
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
