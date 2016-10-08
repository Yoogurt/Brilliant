package com.BB.test;

import com.BB.util.Util;

public class UtilTest {

	public static void main(String[] args) {
		byte[] data1 = { 110, (byte) 0xf2, (byte) 0xff, (byte) 42 };
		byte[] data2 = { 110, (byte) 0xf2, (byte) 0xff, (byte) 42 , 0};
		System.out.println(data1.length);
		System.out.println(data2.length);
		System.out.println(Util.compare(data1, data2, false));
	}

}
