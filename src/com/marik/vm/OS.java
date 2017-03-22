package com.marik.vm;

import java.io.PrintStream;
import java.io.RandomAccessFile;

import com.marik.util.Util;

public class OS {
	
	public static boolean debug = true;

	/**
	 * don't pull it out , because it will change the reference while running
	 * out space
	 */
	public static byte[] mMemory = new byte[OS.PAGE_SIZE];
	/**
	 * don't pull it out , because it will change the reference while running
	 * out space
	 */
	public static byte[] mFlag = { -1 };

	@Deprecated
	public static final byte PROT_EXEC = 4;// not implements
	@Deprecated
	public static final byte PROT_READ = 1;// not implements
	@Deprecated
	public static final byte PROT_WRITE = 2;// not implements
	@Deprecated
	public static final byte PROT_NONE = 0;// not implements

	public static final byte MAP_FIXED = 16;
	@Deprecated
	public static final byte MAP_ANONYMOUS = 32; // not implements

	static final long PAGE_MASK = ~(4096 - 1);
	static final int PAGE_SIZE = 4096;
	static final int PAGE_BIT = 12;

	public static long PAGE_START(long val) {
		return val & PAGE_MASK;
	}

	public static long PAGE_OFFSET(long val) {
		return val & ~PAGE_MASK;
	}

	public static long PAGE_END(long val) {
		return PAGE_START(val + (PAGE_SIZE - 1));
	}

	public static int mmap(int start, int length, int flags, RandomAccessFile fd, long offset) {
		return MemoryMapper.mmap(start, length, (byte) flags, fd, offset);
	}

	public static byte[] getMemory() {
		return MemoryMapper.getMemory();
	}

	public static void dumpMemory() {

		dumpMemory(System.out);
	}

	public static void dumpMemory(PrintStream out) {

		int line = 0;
		out.printf("%5s : ", line);
		for (byte b : mMemory) {
			out.print(Util.byte2Hex(b) + " ");
			if (++line % 16 == 0) {
				out.println();
				out.printf("%5x : ", line);
			} else if (line % 8 == 0)
				out.print("  ");
		}
		out.println();
		out.flush();
	}

}
