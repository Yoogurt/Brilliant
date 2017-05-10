package com.marik.vm;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.marik.util.ByteUtil;

class MemoryMapper {

	@Deprecated
	public static int mmap(int start, int length, int prot, int flags, int fd,
			long offset) {
		throw new UnsupportedOperationException("Not implements");
	}

	/**
	 * we mmap a file into byte[]
	 */
	public static int mmap(int start, int length, byte flag, File fd,
			long offset) {
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(fd, "r");
		} catch (Exception e) {
			e.printStackTrace();
			if (fd != null)
				return -1;
		}

		if (OS.PAGE_OFFSET(start) > 0 && (flag & OS.MAP_FIXED) != 0)
			return -1;

		try {

			if ((flag & OS.MAP_FIXED) == 0)
				return mmapNotFix(length, flag, raf, offset);
			else
				return mmapFix(start, length, flag, raf, offset);

		} catch (Throwable e) {
			throw new IllegalStateException();
		} finally {
			if (raf != null)
				try {
					raf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}

	/**
	 * we mmap a file into byte[]
	 */
	public static int mmap(int start, int length, byte flag,
			RandomAccessFile raf, long offset) {

		if (OS.debug)
			System.out.println("mmap(0x" + Integer.toHexString(start) + ",0x"
					+ Integer.toHexString(length) + "," + flag + ",...,"
					+ offset + ")");

		if (OS.PAGE_OFFSET(start) > 0 && (flag & OS.MAP_FIXED) != 0)
			return -1;

		if ((flag & OS.MAP_FIXED) == 0)
			return mmapNotFix(length, flag, raf, offset);
		else
			return mmapFix(start, length, flag, raf, offset);

	}

	private static int mmapFix(int start, int length, byte flag,
			RandomAccessFile raf, long offset) {

		if (length < 0)
			throw new IllegalArgumentException("length < 0");
		if (OS.PAGE_OFFSET(start) != 0)
			return -1;

		int startIndex = (int) (start >> OS.PAGE_SHIFT);
		int endIndex = (int) ((OS.PAGE_END(length) >> OS.PAGE_SHIFT) + startIndex);
		if (OS.debug) {
			System.out.println("start " + start + " length " + length);
			System.out.println("startIndex " + startIndex + " endIndex "
					+ endIndex);
		}
		for (int i = startIndex; i < endIndex; i++)
			if (i >= OS.mFlag.length) {
				incMemory((int) OS.PAGE_END(length));
				return mmapFix(start, length, flag, raf, offset);
			}

		if (raf != null)
			try {
				raf.seek(offset);
				raf.read(OS.mMemory, start, length);

				int inc_Bit = (int) (OS.PAGE_END(length) + start) >> OS.PAGE_SHIFT;
				for (int mPtr = start >> OS.PAGE_SHIFT; mPtr < inc_Bit; mPtr++)
					OS.mFlag[mPtr] = flag;

			} catch (IOException e) {
				e.printStackTrace();
				return -1;
			}

		return start;
	}

	private static int mmapGetFreeAddress(int length) {

		int blockCount = 0;
		int lastSearch = 0;
		int needBlockCount = (int) (OS.PAGE_END(length) >> OS.PAGE_SHIFT);

		int arrayLength = OS.mFlag.length;
		for (int i = 0; i < arrayLength; i++) {
			if (OS.mFlag[i] == -1)
				if (++blockCount >= needBlockCount)
					if (needBlockCount == 1)
						return i << OS.PAGE_SHIFT;
					else
						return lastSearch << OS.PAGE_SHIFT;

				else if (lastSearch == -1)
					lastSearch = i;
				else
					;
			else {
				lastSearch = -1;
				blockCount = 0;
			}
		}
		return blockCount == needBlockCount ? (lastSearch << OS.PAGE_SHIFT)
				: -1;
	}

	private static int mmapNotFix(int length, byte flag, RandomAccessFile raf,
			long offset) {

		int startAddr = mmapGetFreeAddress(length);

		if (startAddr < 0) {
			incMemory((int) OS.PAGE_END(length));
			startAddr = mmapGetFreeAddress(length);
		}

		if (startAddr < 0)
			throw new OutOfMemoryError();

		if (raf != null)
			try {
				raf.seek(offset);
				raf.read(OS.mMemory, startAddr, length);
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}

		int inc_Bit = (int) (OS.PAGE_END(length) + startAddr) >> OS.PAGE_SHIFT;

		for (int mPtr = startAddr >> OS.PAGE_SHIFT; mPtr < inc_Bit; mPtr++)
			OS.mFlag[mPtr] = flag;

		return startAddr;
	}

	public static int unmmap(int start, int length) {

		if (length < 0)
			throw new IllegalArgumentException();

		if (OS.PAGE_OFFSET(start) > 0)
			return -1;

		int blockCount = (int) (OS.PAGE_END(length) >> OS.PAGE_SHIFT);

		int startBlock = start >> OS.PAGE_SHIFT;
		int endIndex = startBlock + blockCount;

		if (endIndex > OS.mFlag.length)
			endIndex = OS.mFlag.length;

		for (int i = startBlock; i < endIndex; i++)
			OS.mFlag[i] = -1;

		return 0;
	}

	private static void incMemory(int size) {
		if (OS.debug)
			System.out.println("inc Space : " + size);

		if (OS.PAGE_OFFSET(size) > 0)
			throw new IllegalArgumentException();

		size += OS.mMemory.length;
		if (size < 0)
			throw new OutOfMemoryError();

		byte[] result = new byte[size];
		System.arraycopy(OS.mMemory, 0, result, 0, OS.mMemory.length);
		byte[] flag = new byte[size >> OS.PAGE_SHIFT];
		System.arraycopy(OS.mFlag, 0, flag, 0, OS.mFlag.length);

		int start = OS.mFlag.length;

		int length = flag.length;
		for (; start < length; start++)
			flag[start] = -1;

		OS.mMemory = result;
		OS.mFlag = flag;

	}

	public static byte[] getMemory() {
		return OS.mMemory;
	}

	public static void main(String[] args) {

		mmap(0, 52, (byte) 0, new File("C:\\Users\\monitor\\Desktop\\test"), 0);
		System.out.println(ByteUtil.bytes2Hex(OS.mMemory, 0, 52));

	}

}
