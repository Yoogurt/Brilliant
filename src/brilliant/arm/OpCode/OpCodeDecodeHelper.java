package brilliant.arm.OpCode;

import brilliant.arm.OpCode.arm.instructionSet.ArmFactory;
import brilliant.arm.OpCode.factory.ParseTemplate;
import brilliant.arm.OpCode.thumb.ThumbFactory;
import brilliant.elf.util.ByteUtil;
import brilliant.elf.vm.Register;
import brilliant.elf.vm.Register.RegisterIllegalStateExeception;

public final class OpCodeDecodeHelper {

	public static void decode(byte[] data, int start, int size,
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
	}

	private static void decodeArm(byte[] data, int start, int size,
			boolean isLittleEndian, OpCodeHookCallback callback) {

		int current;

		for (int i = 0; i < size; i += 4) {

			current = start + i;
			Register.PC = current + 8;

			int opCode = ByteUtil.bytes2Int32(data, current, 4, isLittleEndian);

			ParseTemplate ret = ArmFactory.parse(opCode);

			if (ret != null) {
				if (!callback.ArminstructionDecodeDone(current, opCode, ret))
					break;
			} else if (!callback.exception(new IllegalArgumentException(
					"Unable to decode instruction "
							+ Integer.toBinaryString(opCode))))
				break;
		}
	}

	private static void decodeThumb(byte[] data, int start, int size,
			boolean isLittleEndian, OpCodeHookCallback callback) {

		int current;

		for (int i = 0; i <= size; i += 2) {

			current = start + i;
			Register.PC = current + 4;

			short opCode = ByteUtil.bytes2Int16(data, current, 2,
					isLittleEndian);

			ParseTemplate ret = ThumbFactory.parse(opCode, true);

			if (ret != null)
				if (!callback
						.Thumb16instructionDecodeDone(current, opCode, ret))
					break;

				else {

					if (i + 2 >= size)
						if (!callback.exception(new IllegalArgumentException(
								"Unable to decode instruction "
										+ Integer.toBinaryString(opCode))))
							break;

					int command = (opCode << 16)
							| ByteUtil.bytes2Int32(data, current + 2, 2,
									isLittleEndian);

					i += 2;
					ret = ThumbFactory.parse(command, false);
					if (ret != null) {
						if (!callback.Thumb32instructionDecodeDone(current,
								command, ret))
							break;
					} else if (!callback
							.exception(new IllegalArgumentException(
									"Unable to decode instruction "
											+ Integer.toBinaryString(command))))
						break;
				}
		}
	}

	public interface OpCodeHookCallback {
		public boolean ArminstructionDecodeDone(int current, int instruction,
				ParseTemplate ret);

		public boolean Thumb16instructionDecodeDone(int current,
				short instruction, ParseTemplate ret);

		public boolean Thumb32instructionDecodeDone(int current,
				int instruction, ParseTemplate ret);

		public boolean exception(Throwable t);
	}
}
