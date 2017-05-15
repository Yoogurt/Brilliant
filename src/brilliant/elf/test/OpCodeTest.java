package brilliant.elf.test;

import static brilliant.elf.content.ELF_Constant.STB_Info.STB_GLOBAL;
import static brilliant.elf.content.ELF_Constant.STB_Info.STB_LOCAL;
import static brilliant.elf.content.ELF_Constant.STB_Info.STB_WEAK;
import static brilliant.elf.content.ELF_Constant.ST_TYPE.STT_FILE;
import static brilliant.elf.content.ELF_Constant.ST_TYPE.STT_FUNC;
import static brilliant.elf.content.ELF_Constant.ST_TYPE.STT_HIPROC;
import static brilliant.elf.content.ELF_Constant.ST_TYPE.STT_LOPROC;
import static brilliant.elf.content.ELF_Constant.ST_TYPE.STT_NOTYPE;
import static brilliant.elf.content.ELF_Constant.ST_TYPE.STT_OBJECT;
import static brilliant.elf.content.ELF_Constant.ST_TYPE.STT_SECTION;

import java.util.List;

import brilliant.arm.OpCode.OpCode;
import brilliant.elf.content.ELF;
import brilliant.elf.export.ELF_Symbol;
import brilliant.elf.vm.OS;
import brilliant.elf.vm.Register;

public class OpCodeTest {

	public static final int ARM = 0x0;
	public static final int THUMB = 0x1;

	public static void main(String[] args) throws Exception {
		ELF elf = ELF.dlopen("C:/Users/Administrator/Desktop/libDS.so");

		List<ELF_Symbol> symbols = elf.dumpHashSymtab();

		String functionName = "Java_com_gemo_mintourc_util_DSUtil_decode";

		for (ELF_Symbol s : symbols) {
			if (functionName.equals(s.name)) {
				System.out.println(functionName);
				dumpOpCode(s);
				break;
			}
		}
	}

	private static void dump(List<ELF_Symbol> symbols) {
		for (ELF_Symbol s : symbols) {

			String bind = null;
			String type = null;

			switch (s.bind) {
			case STB_LOCAL:
				bind = "STB_LOCAL";
				break;
			case STB_GLOBAL:
				bind = "STB_GLOBAL";
				break;
			case STB_WEAK:
				bind = "STB_WEAK";
				break;
			}
			switch (s.type) {
			case STT_NOTYPE:
				type = "STT_NOTYPE";
				break;
			case STT_OBJECT:
				type = "STT_OBJECT";
				break;
			case STT_FUNC:
				type = "STT_FUNC";
				break;
			case STT_SECTION:
				type = "STT_SECTION";
				break;
			case STT_FILE:
				type = "STT_FILE";
				break;
			case STT_LOPROC:
				type = "STT_LOPROC";
				break;
			case STT_HIPROC:
				type = "STT_HIPROC";
				break;
			}
			System.out
					.printf("symbol name : %-30s  value : %-12s  size : %-8d  BIND : %-10s  TYPE : %-10s  OTHER : %-5d  SHNDX : %-8d\n",
							s.name, s.address, s.size, bind, type, s.other,
							s.shndx);
		}
	}

	private static void dumpOpCode(ELF_Symbol sym) {

		System.out.println("address : 0x" + Integer.toHexString(sym.address));
		System.out.println("size : 0x" + Integer.toHexString(sym.size));

		int mode = sym.address & 1;
		
		int start = sym.address & (-1 ^ 1);
		
		System.out.println("start : 0x" + Integer.toHexString(start));

		if (mode == 0x0)
			return;
		else
			OpCode.decodeThumbWithHex(OS.getMemory(), start, sym.size, true);

	}

}
