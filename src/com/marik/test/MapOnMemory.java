package com.marik.test;

import java.util.List;

import com.marik.elf.ELF;
import com.marik.elf.ELF_ProgramHeader;
import com.marik.elf.ELF_ProgramHeader.ELF_Phdr;
import com.marik.util.Util;
import com.marik.vm.OS;

public class MapOnMemory {

	public static void main(String[] args) throws Exception {
	ELF elf =  ELF.decode("C:\\Users\\monitor\\Desktop\\test");
		ELF_ProgramHeader ph = elf.getElf_phdr();
		List<ELF_ProgramHeader.ELF_Phdr> m = ph.getAllLoadableSegment();
		for (ELF_Phdr a : m) {

			long seg_start = Util.bytes2Int64(a.p_vaddr);
			long seg_end = seg_start + Util.bytes2Int64(a.p_memsz);

			long seg_page_start = OS.PAGE_START(seg_start);
			long seg_page_end = OS.PAGE_END(seg_end);

			long seg_file_end = seg_start + Util.bytes2Int64(a.p_filesz);

			// 文件偏移
			long file_start = Util.bytes2Int64(a.p_offset);
			long file_end = file_start + Util.bytes2Int64(a.p_filesz);

			long file_page_start = OS.PAGE_START(file_start);
			long file_length = file_end - file_page_start;

			System.out.println("seg_start : " + Long.toHexString(seg_start));
			System.out.println("seg_end : " + Long.toHexString(seg_end));
			System.out.println("seg_page_start : " + Long.toHexString(seg_page_start));
			System.out.println("seg_page_end : " + Long.toHexString(seg_page_end));
			System.out.println("seg_file_end : " + Long.toHexString(seg_file_end));
			System.out.println("file_start : " + Long.toHexString(file_start));
			System.out.println("file_end : " + Long.toHexString(file_end));
			System.out.println("file_page_start : " + Long.toHexString(file_page_start));
			System.out.println("file_length : " + Long.toHexString(file_length));
			System.out.println();
			System.out.println("Segment p_vaddr  : " + Util.bytes2Hex(a.p_vaddr));
			System.out.println("Segment p_memsz : " + Util.bytes2Hex(a.p_memsz));
			System.out.println("Segment p_offset :  " + Util.bytes2Hex(a.p_offset));
			System.out.println("Segment p_filesz : " + Util.bytes2Hex(a.p_filesz));

			System.out.println("mmap(reinterpret_cast<void*>(0x" + Long.toHexString(seg_page_start) + ")," + file_length
					+ ",PFLAGS_TO_PROT(phdr->p_flags),MAP_FIXED|MAP_PRIVATE,fd_,0x" + Long.toHexString(file_page_start)
					+ ");");
			System.out.println();
			System.out.println();
			System.out.println();

		}
	}
}
