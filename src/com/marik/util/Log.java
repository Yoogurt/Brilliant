package com.marik.util;

import static java.lang.System.out;

public class Log {

	public static boolean DEBUG = true;

	public static void i(String msg) {
		if (DEBUG)
			out.println("ELF " + msg);
	}

	public static void i() {
		if (DEBUG)
			out.println();
	}

	public static void e(String msg) {
		if (DEBUG)
			out.println(msg);
	}

	public static void e() {
		if (DEBUG)
			out.println();
	}
}
