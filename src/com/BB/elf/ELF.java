package com.BB.elf;

import java.io.File;
import java.io.IOException;

import com.BB.util.LocatableInPutStream;

public class ELF {
	
	private ELF_Header elf_header;
	private ELF_ProgramHeader elf_phdr;

	public ELF(String file) throws Exception {
		this(new File(file));
	}

	public ELF(File file) throws Exception {
		
		LocatableInPutStream lis = new LocatableInPutStream(file);
		
		try {
			
			elf_header = new ELF_Header(lis);
			
			if(lis.getNextReadPosition() == elf_header.getHeaderSize())
				elf_phdr = new ELF_ProgramHeader(lis, elf_header);    
			
		} catch (IOException e) {
			throw new Exception("verify elf header fail", e);
		}
	}
	
	@Override
	public String toString() {
		return "ELF Header : \n" + elf_header.toString();
	}

}
