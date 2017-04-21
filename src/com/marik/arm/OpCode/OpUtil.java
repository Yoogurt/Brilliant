package com.marik.arm.OpCode;

public class OpUtil {

	public static boolean assertBit0(int data) {
		return assert0(data, 0);
	}

	public static boolean assertBit1(int data) {
		return assert1(data, 0);
	}

	public static boolean assert0(int data, int... index) {

		for (int mIndex : index)
			if (((data >> mIndex) & 1) == 1)
				return false;

		return true;
	}

	public static boolean assert1(int data, int... index) {
		for (int mIndex : index)
			if (((data >> mIndex) & 1) == 0)
				return false;

		return true;
	}

	public static int getShiftInt(int data, int from, int length) {
		return (data >> from) & ((1 << length) - 1);
	}

}
