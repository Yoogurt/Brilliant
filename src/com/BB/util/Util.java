package com.BB.util;

public class Util {

	public static String byte2Hex(byte data) {

		int internal;

		if (data < 0)
			internal = 256 + data;
		else
			internal = data;

		String result = Integer.toHexString(internal);
		if (result.length() < 2)
			result = 0 + result;
		else if (result.length() > 2)
			result = result.substring(result.length() - 2, result.length());
		return result;
	}

	public static String bytes2Hex(byte[] data, int startIndex, int length) {

		StringBuilder sb = new StringBuilder((length - startIndex) << 1 + 1);

		for (int m = 0; m < length; m++)
			sb.append(byte2Hex(data[m + startIndex])).append(" ");

		return sb.toString().trim();

	}

	public static String bytes2Hex(byte[] data) {

		StringBuilder sb = new StringBuilder(data.length << 1 + 1);

		for (byte tmp : data)
			sb.append(byte2Hex(tmp)).append(" ");

		return sb.toString().trim();

	}

	public static boolean equals(byte[] data1, int srcStartIndex, byte[] data2, int objStartIndex, int length) {
		if (data1.length - srcStartIndex < length || data2.length - objStartIndex < length)
			throw new RuntimeException("compare fail while comparing data1 and data2 , from data1 position "
					+ srcStartIndex + " to " + length + srcStartIndex + " , data1 total length +" + data1.length + "\n"
					+ "from data2 position " + objStartIndex + " to " + length + srcStartIndex
					+ " , data2 total length +" + data2.length);

		for (int ptr = 0; ptr < length; ptr++)
			if (data1[ptr + srcStartIndex] != data2[ptr + objStartIndex])
				return false;

		return true;
	}

	public static boolean equals(byte[] data, int startIndex, int length, int obj, boolean isLittleEndian) {

		int actullyValue = bytes2Int32(data, startIndex, length, isLittleEndian);

		return actullyValue == obj;
	}

	public static int byte2Int32(byte data) {
		return data < 0 ? data + 256 : data;
	}

	public static int bytes2Int32(byte[] data, int startIndex, int length, boolean isLittleEndian) {

		if (isLittleEndian) {

			int actullyValue = 0;

			for (int m = 0; m < length; m++) {
				int wordValue = 0;
				if (data[m + startIndex] < 0)
					wordValue = data[m + startIndex] + 256;
				else
					wordValue = data[m + startIndex];

				actullyValue |= wordValue << (m << 3);
			}
			return actullyValue;

		} else {

			int actullyValue = 0;

			for (int m = 0; m < length; m++) {
				int wordValue;
				if (data[m + startIndex] < 0)
					wordValue = data[m + startIndex] + 256;
				else
					wordValue = data[m + startIndex];

				actullyValue = actullyValue << 8 | wordValue;
			}
			return actullyValue;
		}

	}

	public static int bytes2Int32(byte[] data) {
		return bytes2Int32(data, 0, data.length, true);
	}

	public static int bytes2Int32(byte[] data, boolean isLittleEndian) {

		return bytes2Int32(data, 0, data.length, isLittleEndian);

	}

	public static int bytes2Int32(byte[] data, int length) {
		return bytes2Int32(data, 0, length, true);
	}

	public static int bytes2Int32(byte[] data, int length, boolean isLittleEndian) {
		return bytes2Int32(data, 0, length, isLittleEndian);
	}

	public static boolean equals(byte[] data, int startIndex, int length, long obj, boolean isLittleEndian) {

		long actullyValue = bytes2Int64(data, startIndex, length, isLittleEndian);

		return actullyValue == obj;
	}

	public static int compare(byte[] src, byte[] obj) {
		return compare(src, obj, true);
	}

	public static int compare(byte[] src, byte[] obj, boolean isLittleEndian) {

		boolean isExchange = false;

		if (src.length < obj.length) {
			byte[] tmp = src;
			src = obj;
			obj = tmp;
			isExchange = true;
		}

		if (isLittleEndian) {

			int srcIndex = src.length - 1;
			for (; srcIndex > obj.length - 1; srcIndex--)
				if (src[srcIndex] != 0)
					return isExchange ? -1 : 1;

			for (; srcIndex > -1; srcIndex--)
				if (src[srcIndex] != obj[srcIndex])
					return byte2Int32(src[srcIndex]) > byte2Int32(obj[srcIndex]) ? (isExchange ? -1 : 1)
							: (isExchange ? 1 : -1);

		} else {

			int srcIndex = 0;
			for (; srcIndex < src.length - obj.length; srcIndex++)
				if (src[srcIndex] != 0)
					return isExchange ? -1 : 1;

			for (int objIndex = 0; objIndex < obj.length; objIndex++, srcIndex++)
				if (src[srcIndex] != obj[srcIndex])
					return byte2Int32(src[srcIndex]) > byte2Int32(obj[srcIndex]) ? (isExchange ? -1 : 1)
							: (isExchange ? 1 : -1);

		}

		return 0;

	}

	public static long bytes2Int64(byte[] data, int startIndex, int length, boolean isLittleEndian) {

		if (isLittleEndian) {

			long actullyValue = 0;

			for (int m = 0; m < length; m++) {
				int wordValue = 0;
				if (data[m + startIndex] < 0)
					wordValue = data[m + startIndex] + 256;
				else
					wordValue = data[m + startIndex];

				actullyValue |= wordValue << (m << 3);
			}
			return actullyValue;

		} else {

			int actullyValue = 0;

			for (int m = 0; m < length; m++) {
				int wordValue;
				if (data[m + startIndex] < 0)
					wordValue = data[m + startIndex] + 256;
				else
					wordValue = data[m + startIndex];

				actullyValue = actullyValue << 8 | wordValue;
			}
			return actullyValue;
		}

	}

	public static long bytes2Int64(byte[] data, boolean isLittleEndian) {

		return bytes2Int64(data, 0, data.length, isLittleEndian);

	}

	public static long bytes2Int64(byte[] data, int length) {
		return bytes2Int64(data, 0, length, true);
	}

	public static long bytes2Int64(byte[] data, int length, boolean isLittleEndian) {
		return bytes2Int32(data, 0, length, isLittleEndian);
	}

	public static long bytes2Int64(byte[] data) {
		return bytes2Int64(data, true);
	}

	public static String decHexSizeFormat32(byte[] size, boolean isLittleEndian) {
		return Util.bytes2Int32(size, isLittleEndian) + "(0x" + Util.bytes2Hex(size) + ")" + "B";
	}

	public static String hexDecSizeFormat32(byte[] size, boolean isLittleEndian) {
		return "0x" + Util.bytes2Hex(size) + "(" + Util.bytes2Int32(size, isLittleEndian) + ")" + "B";
	}

	public static void assertAlign(long align) {
		if ((align & 1) == 1)
			throw new AssertionError();
	}

	public static void main(String[] args) {

		byte[] a = { (byte) 0x12, (byte) 0x34, (byte) 0xff };

		System.out.println(equals(a, 0, 3, 0xff3412, true));

	}

}
