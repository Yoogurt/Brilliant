package brilliant.arm.OpCode.factory;

import brilliant.elf.util.Log;

public class OpUtil {

	public static final int R0 = 0x0;
	public static final int R1 = 0x1;
	public static final int R2 = 0x2;
	public static final int R3 = 0x3;
	public static final int R4 = 0x4;
	public static final int R5 = 0x5;
	public static final int R6 = 0x6;
	public static final int R7 = 0x7;
	public static final int R8 = 0x8;
	public static final int R9 = 0x9;
	public static final int R10 = 0xA;
	public static final int R11 = 0xB;
	public static final int R12 = 0xC;
	public static final int SP = 0xD;
	public static final int LR = 0xE;
	public static final int PC = 0xF;

	/**
	 * transform data into binary data and check the specific index is 0 or not
	 * 
	 * @return true means all indexes are 0 , false otherwise
	 */
	public static boolean assert0(int data, int... index) {

		for (int mIndex : index)
			if (((data >>> mIndex) & 1) == 1)
				return false;

		return true;
	}

	/**
	 * transform data into binary data and check the specific index is 1 or not
	 * 
	 * @return true means all indexes are 1 , false otherwise
	 */
	public static boolean assert1(int data, int... index) {
		for (int mIndex : index)
			if (((data >>> mIndex) & 1) == 0)
				return false;

		return true;
	}

	/**
	 * cut the data down form "form" , the rest binary data length will be
	 * "length"
	 * 
	 * for instance : getShiftInt(0b1010110 , 3 , 2)
	 * 
	 * 0b 1 0 1 0 1 1 0 from ^ here to ^ to here
	 * 
	 * result 0b10 = 2
	 */
	public static int getShiftInt(int data, int from, int length) {
		Log.e(Integer.toBinaryString(data) + "  from " + from + "  length "
				+ length);
		return (data >>> from) & ((1 << length) - 1);
	}

	/**
	 * R13 -> SP R14 -> LR R15 -> PC
	 */
	public static String parseRegister(int no) {

		if (no < 13 && no >= 0)
			return "R" + no;

		switch (no) {
		case 13:
			return "SP";
		case 14:
			return "LR";
		case 15:
			return "PC";
		default:
			throw new IllegalArgumentException("Unable to decode register "
					+ no);
		}
	}

	/**
	 * transform no into binary , and check the each index is 1 or not 1
	 * represents the R(index) register are specific
	 * 
	 * for instance : 0b 1 0 1 0 1 0 mean R5 R3 R1
	 */
	public static String parseRegisterList(int data, int discard) {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; (data >>> i) > 0; i++)
			if (((data >>> i) & 1) == 1 && discard != i)
				sb.append(parseRegister(i)).append(" , ");

		if (sb.length() > 0)
			sb.setLength(sb.length() - 3);
		return sb.toString();
	}

	public static boolean isRigisterInRegisterList(int target, int registerList) {

		return 1 == ((registerList >>> target) & 1);

	}

	public static int signExtend(int data, int length) {

		int sign = 1 & (data >>> (length - 1));

		data ^= sign << (length - 1);

		if (sign == 1)
			data |= -1 ^ ((1 << (length - 1)) - 1);

		return data;
	}

	public static int align(int data, int alignment) {
		return data / alignment * alignment;
	}

	public static int armExpandImm(int imm12) {
		imm12 &= 0xfff;

		int rotation = imm12 >>> 8;
		int result = imm12 & 0xff;

		return (result >>> (rotation << 1)) | (result << (32 - rotation << 1));
	}

	public static int thumbExpandImm(int imm12) {
		imm12 &= 0xfff;
		int high5 = imm12 >>> 7;

		if (high5 == 0b00000 || high5 == 0b00001)
			return imm12 & 0xff;
		if (high5 == 0b00010 || high5 == 0b00011)
			return ((imm12 & 0xff) << 16) | (imm12 & 0xff);
		if (high5 == 0b00100 || high5 == 0b00101)
			return ((imm12 & 0xff) << 24) | ((imm12 & 0xff) << 8);
		if (high5 == 0b00110 || high5 == 0b00111)
			return ((imm12 & 0xff) << 24) | ((imm12 & 0xff) << 16)
					| ((imm12 & 0xff) << 8) | (imm12 & 0xff);

		int base = 0b01000;
		imm12 = (imm12 & 0xff | 0x80) << 24;
		return imm12 >>> (high5 - base);
	}

	public static void main(String[] args) {
	System.out.println(Integer.toBinaryString(0b1110101111111111111011001110 >>> 1));
	}

}
