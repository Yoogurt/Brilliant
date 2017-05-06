package com.marik.test;

import java.io.File;
import java.io.RandomAccessFile;

import com.marik.arm.OpCode.OpCode;
import com.marik.elf.ELF;
import com.marik.util.ByteUtil;
import com.marik.vm.OS;

public class Optest {
	public static void main(String[] args) throws Exception {

		ELF elf = ELF.dlopen("C:/Users/monitor/Desktop/Decomplied File/500彩票/lib/armeabi/libesunlib.so");

		int length = 0x21c2 - 0x219a;
		int startIndex = 0x219a;
		byte[] buffer = new byte[length];

		System.arraycopy(OS.getMemory(), startIndex, buffer, 0, length);
		byte[] command = new byte[2];

		for (int ptr = 0; ptr < buffer.length; ptr += 2) {
			System.arraycopy(buffer, ptr, command, 0, 2);
			String OpCodes = null;
			try {
				int com = ByteUtil.bytes2Int32(command);
				OpCodes = OpCode.decodeThumb16(com);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Not Thumb code");
				continue;
			}
			System.out.println(OpCodes);
		}
	}
}
