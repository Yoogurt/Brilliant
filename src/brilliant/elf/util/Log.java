package brilliant.elf.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Log {

	public static PrintStream out;
	
	static{
		
		try {
			out = new PrintStream(new File("C:/Users/Administrator/Desktop/libdvm.dump"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}

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
