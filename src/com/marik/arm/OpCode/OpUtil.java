package com.marik.arm.OpCode;

public class OpUtil {
	
	/**
	 * transform data into binary data and check the specific index is 0 or not
	 * 
	 * @return true means all indexes are 0 , false otherwise
	 */
	public static boolean assert0(int data, int... index) {

		for (int mIndex : index)
			if (((data >> mIndex) & 1) == 1)
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
			if (((data >> mIndex) & 1) == 0)
				return false;

		return true;
	}

	/**
	 * cut the data down form "form" , the rest binary data length will be
	 * "length"
	 * 
	 * for instance : getShiftInt(0b1010110 , 3 , 2)
	 * 
	 * 0b 1 0 1 0 1 1 0 
	 * from     ^ here 
	 * to     ^ to here
	 * 
	 * result 0b10 = 2
	 */
	public static int getShiftInt(int data, int from, int length) {
		return (data >> from) & ((1 << length) - 1);
	}

	/**
	 * R13 -> SP R14 -> LR R15 -> PC
	 */
	public static String parseRegister(int no) {

		if (no < 13)
			return "R" + no;

		switch (no) {
		case 13:
			return "SP";
		case 14:
			return "LR";
		case 15:
			return "PC";
		default:
			throw new IllegalArgumentException("Unable to decode register " + no);
		}
	}

	/**
	 * transform no into binary , and check the each index is 1 or not 1
	 * represents the R(index) register are specific
	 * 
	 * for instance : 0b 1 0 1 0 1 0 mean R5 R3 R1
	 */
	public static String parseRigisterBit(int data, int discard) {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; data >> i > 0; i++)
			if (((data >> i) & 1) == 1 && discard != i)
				sb.append(parseRegister(i)).append(" , ");

		if (sb.length() > 0)
			sb.setLength(sb.length() - 3);
		return sb.toString();
	}

	public static boolean isRigisterInRegisterList(int target, int registerList) {

		return 1 == ((registerList >> target) & 1);

	}

	public static int signExtend(int data, int length) {

		int sign = 1 & (data >> (length - 1));

		data ^= sign << (length - 1);

		if (sign == 1)
			data |= -1 ^ ((1 << (length - 1)) - 1);

		return data;
	}

	public static void main(String[] args) {
		int no = 0b11111100001;
		System.out.println(Integer.toBinaryString(signExtend(no, 11)));
		System.out.println(signExtend(no, 11));
	}

}
