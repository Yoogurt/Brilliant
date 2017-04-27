package com.marik.vm;

import java.io.PrintStream;
import java.io.RandomAccessFile;

import com.marik.util.ByteUtil;

public class OS {

	public static boolean debug = false;

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
	public static final byte MAP_ANONYMOUS = 32; // not implements , mmap flag will carry this flag automatic

	public static final long PAGE_MASK = ~(4096 - 1);
	public static final int PAGE_SIZE = 4096;
	public static final int PAGE_SHIFT = 12;

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
		return MemoryMapper.mmap(start, length, (byte) (flags | MAP_ANONYMOUS), fd, offset);
	}

	public static byte[] getMemory() {
		return MemoryMapper.getMemory();
	}

	public static int unmmap(int start, int size) {
		return MemoryMapper.unmmap(start, size);
	}

	public static void dumpMemory() {

		dumpMemory(System.out);
	}

	public static void dumpMemory(PrintStream out) {

		dumpMemory(out , 0 , mMemory.length);
	}
	
	public static void dumpMemory(PrintStream out , int startIndex , int endIndex) {

		int line = startIndex >> 4;
		out.printf("%5s : ", line);
		
		for(int i = startIndex % 16 ; i > 0 ; i--)
			out.print("    ");
		
		for (int i = startIndex ; i < endIndex ;i++ ) {
			out.print(ByteUtil.byte2Hex(mMemory[i]) + " ");
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
