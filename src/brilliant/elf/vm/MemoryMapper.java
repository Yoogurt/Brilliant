package brilliant.elf.vm;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

class MemoryMapper {

	/**
	 * we mmap a file into byte[]
	 */
	public static int mmap(int start, int length, byte flag, File fd, long offset, OS os) {
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
				return mmapNotFix(length, flag, raf, offset, os);
			else
				return mmapFix(start, length, flag, raf, offset, os);

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
	public static int mmap(int start, int length, byte flag, RandomAccessFile raf, long offset, OS os) {

		if (OS.debug)
			System.out.println("mmap(0x" + Integer.toHexString(start) + ",0x" + Integer.toHexString(length) + "," + flag
					+ ",...," + offset + ")");

		if (OS.PAGE_OFFSET(start) > 0 && (flag & OS.MAP_FIXED) != 0)
			return -1;

		if ((flag & OS.MAP_FIXED) == 0)
			return mmapNotFix(length, flag, raf, offset, os);
		else
			return mmapFix(start, length, flag, raf, offset, os);

	}

	private static int mmapFix(int start, int length, byte flag, RandomAccessFile raf, long offset, OS os) {

		if (length < 0)
			throw new IllegalArgumentException("length < 0");
		if (OS.PAGE_OFFSET(start) != 0)
			return -1;

		int startIndex = (int) (start >> OS.PAGE_SHIFT);
		int endIndex = (int) ((OS.PAGE_END(length) >> OS.PAGE_SHIFT) + startIndex);
		if (OS.debug) {
			System.out.println("start " + start + " length " + length);
			System.out.println("startIndex " + startIndex + " endIndex " + endIndex);
		}
		for (int i = startIndex; i < endIndex; i++)
			if (i >= os.mFlag.length) {
				incMemory((int) OS.PAGE_END(length), os);
				return mmapFix(start, length, flag, raf, offset, os);
			}

		if (raf != null)
			try {
				raf.seek(offset);
				raf.read(os.mMemory, start, length);

				int inc_Bit = (int) (OS.PAGE_END(length) + start) >> OS.PAGE_SHIFT;
				for (int mPtr = start >> OS.PAGE_SHIFT; mPtr < inc_Bit; mPtr++)
					os.mFlag[mPtr] = flag;

			} catch (IOException e) {
				e.printStackTrace();
				return -1;
			}

		return start;
	}

	private static int mmapGetFreeAddress(int length, OS os) {

		int blockCount = 0;
		int lastSearch = 0;
		int needBlockCount = (int) (OS.PAGE_END(length) >> OS.PAGE_SHIFT);

		int arrayLength = os.mFlag.length;
		for (int i = 0; i < arrayLength; i++) {
			if (os.mFlag[i] == -1)
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
		return blockCount == needBlockCount ? (lastSearch << OS.PAGE_SHIFT) : -1;
	}

	private static int mmapNotFix(int length, byte flag, RandomAccessFile raf, long offset, OS os) {

		int startAddr = mmapGetFreeAddress(length, os);

		if (startAddr < 0) {
			incMemory((int) OS.PAGE_END(length), os);
			startAddr = mmapGetFreeAddress(length, os);
		}

		if (startAddr < 0)
			throw new OutOfMemoryError();

		if (raf != null)
			try {
				raf.seek(offset);
				raf.read(os.mMemory, startAddr, length);
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}

		int inc_Bit = (int) (OS.PAGE_END(length) + startAddr) >> OS.PAGE_SHIFT;

		for (int mPtr = startAddr >> OS.PAGE_SHIFT; mPtr < inc_Bit; mPtr++)
			os.mFlag[mPtr] = flag;

		return startAddr;
	}

	public static int unmmap(int start, int length, OS os) {

		if (length < 0)
			throw new IllegalArgumentException();

		if (OS.PAGE_OFFSET(start) > 0)
			return -1;

		int blockCount = (int) (OS.PAGE_END(length) >> OS.PAGE_SHIFT);

		int startBlock = start >> OS.PAGE_SHIFT;
		int endIndex = startBlock + blockCount;

		if (endIndex > os.mFlag.length)
			endIndex = os.mFlag.length;

		for (int i = startBlock; i < endIndex; i++)
			os.mFlag[i] = -1;

		return 0;
	}

	private static void incMemory(int size, OS os) {
		if (OS.debug)
			System.out.println("inc Space : " + size);

		if (OS.PAGE_OFFSET(size) > 0)
			throw new IllegalArgumentException();

		size += os.mMemory.length;
		if (size < 0)
			throw new OutOfMemoryError();

		byte[] result = new byte[size];
		System.arraycopy(os.mMemory, 0, result, 0, os.mMemory.length);
		byte[] flag = new byte[size >> OS.PAGE_SHIFT];
		System.arraycopy(os.mFlag, 0, flag, 0, os.mFlag.length);

		int start = os.mFlag.length;

		int length = flag.length;
		for (; start < length; start++)
			flag[start] = -1;

		os.mMemory = result;
		os.mFlag = flag;

	}
}
