package com.marik.test;

import java.io.File;
import java.io.PrintStream;

import com.marik.elf.ELF;
import com.marik.vm.OS;

public class Test {

	public static void main(String[] args) throws Exception {
		 ELF.dlopen("C:\\Users\\monitor\\Desktop\\test");

		// ELF.decode("C:\\Users\\monitor\\Desktop\\Decomplied
		// File\\crackme\\lib\\armeabi\\libdata.so");
	}

}
