package com.marik.test;

import java.io.File;
import java.io.RandomAccessFile;

import com.marik.arm.OpCode.OpCode;
import com.marik.util.ByteUtil;

public class Optest {
	public static void main(String[] args) throws Exception {
		String file = "C:/Users/monitor/Desktop/Decomplied File/libtest.so";
		int length = 0xcf0 - 0xc88;
		RandomAccessFile fis = new RandomAccessFile(new File(file), "r");
		byte[] buffer = new byte[length];

		fis.seek(0xc88);
		fis.read(buffer);

		byte[] command = new byte[2];

		for (int ptr = 0; ptr < buffer.length; ptr += 2) {
			System.arraycopy(buffer, ptr, command, 0, 2);
			String OpCodes = null;
			try {
//				OpCodes = OpCode.decodeThumb16(Util.bytes2Int32(command));
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Not Thumb code");
				continue;
			}
			System.out.println(OpCodes);
		}
		fis.close();
	}
}
