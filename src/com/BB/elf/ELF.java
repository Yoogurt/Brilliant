package com.BB.elf;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.BB.util.Log;
import com.BB.util.Util;

public class ELF {

	private ELF_Header elf_header;
	private ELF_ProgramHeader elf_phdr;
	private ELF_SectionHeader elf_shdr;

	public ELF(String file) throws Exception {
		this(new File(file));
	}

	public ELF(File file) throws Exception {
		
		RandomAccessFile lis = new RandomAccessFile(file , "r");
		
		try {
			
			elf_header = new ELF_Header(lis);
			
		elf_phdr = new ELF_ProgramHeader(lis, elf_header);    
			
			elf_shdr = new ELF_SectionHeader(lis, elf_header);
			
			
//			Log.e(Constant.DIVISION_LINE);
//			
//			ELF_Section section= elf_shdr.getSectionByName(".text");
//			
//			Log.e(String.valueOf(section.s_data.length));
//			Log.e(Util.bytes2Hex(section.getHeader().sh_size));
			
		} catch (IOException e) {
			throw new Exception("verify elf header fail", e);
		}
	}

	@Override
	public String toString() {
		return "ELF Header : \n" + elf_header.toString();
	}

}
