package brilliant.elf.util;

import java.io.IOException;
import java.io.RandomAccessFile;

import brilliant.elf.vm.OS;

public class ByteUtil {

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

		StringBuilder sb = new StringBuilder();

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

	public static boolean equals(byte[] data1, int srcStartIndex, byte[] data2,
			int desStartIndex, int length) {
		if (data1.length - srcStartIndex < length
				|| data2.length - desStartIndex < length)
			throw new RuntimeException(
					"compare fail while comparing data1 and data2 , from data1 position "
							+ srcStartIndex + " to " + length + srcStartIndex
							+ " , data1 total length +" + data1.length + "\n"
							+ "from data2 position " + desStartIndex + " to "
							+ length + srcStartIndex
							+ " , data2 total length +" + data2.length);

		for (int ptr = 0; ptr < length; ptr++)
			if (data1[ptr + srcStartIndex] != data2[ptr + desStartIndex])
				return false;

		return true;
	}

	public static boolean equals(byte[] data, int startIndex, int length,
			int obj, boolean isLittleEndian) {

		int actullyValue = bytes2Int32(data, startIndex, length, isLittleEndian);

		return actullyValue == obj;
	}

	public static int byte2Int32(byte data) {
		return data < 0 ? data + 256 : data;
	}

	public static int bytes2Int32(byte[] data, int startIndex, int length,
			boolean isLittleEndian) {

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

	public static short byte2Int16(byte[] data) {
		return bytes2Int16(data, 0, data.length, true);
	}

	public static short bytes2Int16(byte[] data, int startIndex, int length,
			boolean isLittleEndian) {

		if (isLittleEndian) {

			short actullyValue = 0;

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

			short actullyValue = 0;

			for (int m = 0; m < length; m++) {
				int wordValue;
				if (data[m + startIndex] < 0)
					wordValue = data[m + startIndex] + 256;
				else
					wordValue = data[m + startIndex];

				actullyValue = (short) (actullyValue << 8 | wordValue);
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

	public static int bytes2Int32(byte[] data, int length,
			boolean isLittleEndian) {
		return bytes2Int32(data, 0, length, isLittleEndian);
	}

	public static boolean equals(byte[] data, int startIndex, int length,
			long obj, boolean isLittleEndian) {

		long actullyValue = bytes2Int64(data, startIndex, length,
				isLittleEndian);

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
					return byte2Int32(src[srcIndex]) > byte2Int32(obj[srcIndex]) ? (isExchange ? -1
							: 1)
							: (isExchange ? 1 : -1);

		} else {

			int srcIndex = 0;
			for (; srcIndex < src.length - obj.length; srcIndex++)
				if (src[srcIndex] != 0)
					return isExchange ? -1 : 1;

			for (int objIndex = 0; objIndex < obj.length; objIndex++, srcIndex++)
				if (src[srcIndex] != obj[srcIndex])
					return byte2Int32(src[srcIndex]) > byte2Int32(obj[srcIndex]) ? (isExchange ? -1
							: 1)
							: (isExchange ? 1 : -1);

		}

		return 0;

	}

	public static long bytes2Int64(byte[] data, int startIndex, int length,
			boolean isLittleEndian) {

		if (length > 8)
			throw new IllegalArgumentException(
					"cann't parse data , because it's too long");

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

	public static long bytes2Int64(byte[] data, int length,
			boolean isLittleEndian) {
		return bytes2Int32(data, 0, length, isLittleEndian);
	}

	public static long bytes2Int64(byte[] data) {
		return bytes2Int64(data, true);
	}

	public static String decHexSizeFormat32(byte[] size, boolean isLittleEndian) {
		return ByteUtil.bytes2Int32(size, isLittleEndian) + "(0x"
				+ ByteUtil.bytes2Hex(size) + ")" + "B";
	}

	public static String hexDecSizeFormat32(byte[] size, boolean isLittleEndian) {
		return "0x" + ByteUtil.bytes2Hex(size) + "("
				+ ByteUtil.bytes2Int32(size, isLittleEndian) + ")" + "B";
	}

	public static void assertAlign(long align) {
		if ((align & 1) == 1)
			throw new AssertionError();
	}

	/**
	 * @return little endian
	 */
	public static byte[] int2bytes(int val) {
		byte[] ret = new byte[4];
		for (int i = 0; i < 4; i++, val >>= 8)
			ret[i] = (byte) val;
		return ret;
	}

	public static byte[] short2bytes(short val) {
		byte[] ret = new byte[2];
		for (int i = 0; i < 2; i++, val >>= 8)
			ret[i] = (byte) val;
		return ret;
	}

	public static String getStringFromBytes(RandomAccessFile raf)
			throws IOException {

		StringBuilder sb = new StringBuilder();

		int read;
		while ((read = raf.read()) != 0 && read > 0)
			sb.append((char) read);

		return sb.toString();
	}

	public static String getStringFromMemory(int index) {
		StringBuilder sb = new StringBuilder();

		while (true)
			if (OS.mMemory[index] != 0)
				sb.append((char) OS.mMemory[index++]);
			else
				break;

		return sb.toString();
	}
}
