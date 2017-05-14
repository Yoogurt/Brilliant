package brilliant.arm.OpCode.arm.instruction.factory;

public class ShiftFactory {
	public static String parse(int type, int imm5) {

		if (imm5 > 32)
			throw new IllegalArgumentException("cann't parse Shift immediate to big " + imm5);

		switch (type) {
		case 0b00:
			if (imm5 != 0)
				return "LSL #" + imm5;
			else
				return null;
		case 0b01:
			if (imm5 != 0)
				return "LSR #" + imm5;
			else
				return null;
		case 0b10:
			if (imm5 != 0)
				return "ASR #" + imm5;
			else
				return null;
		case 0b11:
			if (imm5 != 0)
				return "RRX #" + imm5;
			else
				return null;
		default:
			throw new IllegalArgumentException("cann't parse Shift type " + Integer.toBinaryString(type));
		}
	}
}
