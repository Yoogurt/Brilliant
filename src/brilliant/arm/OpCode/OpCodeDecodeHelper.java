package brilliant.arm.OpCode;

import brilliant.arm.OpCode.arm.instructionSet.ArmFactory;
import brilliant.arm.OpCode.factory.ParseTemplate;
import brilliant.arm.OpCode.thumb.ThumbFactory;
import brilliant.elf.content.ELF;
import brilliant.elf.util.ByteUtil;
import brilliant.elf.vm.OS;
import brilliant.elf.vm.Register;
import brilliant.elf.vm.Register.RegisterIllegalStateExeception;

public final class OpCodeDecodeHelper {

	private static void decode(byte[] data, int start, int size,
			boolean isLittleEndian, OpCodeHookCallback callback) {

		if (callback == null)
			throw new NullPointerException("callback must no tbe null");

		int mode = start & 1;
		Register.setT(mode);

		start &= -1 ^ 1;

		switch (Register.getT()) {
		case 0:
			decodeArm(data, start, size, isLittleEndian, callback);
			break;
		case 1:
			decodeThumb(data, start, size, isLittleEndian, callback);
			break;
		default:
			throw new RegisterIllegalStateExeception(
					"Flag Rigister has accessed an unpredictable state");
		}
		callback.onFinish();
	}

	public static void decode(int start, int size, ELF elf,
			OpCodeHookCallback callback) {
		decode(OS.getMemory(), start, size, elf.isLittleEndian(), callback);
	}

	public static boolean isArm(int position) {
		return 0b0 == (position & 1);
	}

	public static boolean isThumb(int position) {
		return 0b1 == (position & 1);
	}

	private static void decodeArm(byte[] data, int start, int size,
			boolean isLittleEndian, OpCodeHookCallback callback) {

		int current;

		for (int i = 0; i < size; i += 4) {

			current = start + i;
			Register.PC = current + 8;

			int opCode = ByteUtil.bytes2Int32(data, current, 4, isLittleEndian);
			ParseTemplate ret = null;

			try {

				ret = ArmFactory.parse(opCode);

			} catch (Throwable t) {
				if (!callback.exception(t, current, opCode))
					break;
				continue;
			}

			if (ret != null) {
				if (!callback.ArminstructionDecodeDone(current, opCode, ret))
					break;
			} else if (!callback.exception(
					new IllegalArgumentException(
							"Unable to decode instruction "
									+ Integer.toBinaryString(opCode)), current,
					opCode))
				break;
		}
	}

	private static void decodeThumb(byte[] data, int start, int size,
			boolean isLittleEndian, OpCodeHookCallback callback) {

		int current;

		for (int i = 0; i <= size; i += 2) {

			current = start + i;
			Register.PC = current + 4;

			int opCode = ByteUtil.bytes2Int32(data, current, 2, isLittleEndian);

			ParseTemplate ret = null;
			try {

				ret = ThumbFactory.parse(opCode, true);

			} catch (Throwable t) {
				if (!callback.exception(t, current, opCode))
					break;
				continue;
			}

			if (ret != null) {
				if (!callback.Thumb16instructionDecodeDone(current,
						(short) opCode, ret))
					break;
			}

			else {

				if (i + 2 >= size)
					if (!callback.exception(
							new IllegalArgumentException(
									"Unable to decode instruction "
											+ Integer.toBinaryString(opCode)),
							current, opCode))
						break;

				int command = (opCode << 16)
						| ByteUtil.bytes2Int32(data, current + 2, 2,
								isLittleEndian);

				i += 2;

				try {

					ret = ThumbFactory.parse(command, false);

				} catch (Throwable t) {
					if (!callback.exception(t, current, command))
						break;
					continue;
				}

				if (ret != null) {
					if (!callback.Thumb32instructionDecodeDone(current,
							command, ret))
						break;
				} else if (!callback.exception(
						new IllegalArgumentException(
								"Unable to decode instruction "
										+ Integer.toBinaryString(command)),
						current, command))
					break;
			}
		}
	}

	public static void main(String[] arg) {
		int data = 0xebfffece;
		System.out.println(ArmFactory.parse(data).parse(data));
	}

	public interface OpCodeHookCallback {
		public boolean ArminstructionDecodeDone(int current, int instruction,
				ParseTemplate ret);

		public boolean Thumb16instructionDecodeDone(int current,
				short instruction, ParseTemplate ret);

		public boolean Thumb32instructionDecodeDone(int current,
				int instruction, ParseTemplate ret);

		public boolean exception(Throwable t, int current, int instruction);

		public void onFinish();
	}
}
