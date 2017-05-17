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

import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import brilliant.arm.OpCode.OpCodeDecodeHelper;
import brilliant.arm.OpCode.OpCodeDecodeHelper.OpCodeHookCallback;
import brilliant.arm.OpCode.factory.ParseTemplate;
import brilliant.elf.content.ELF;
import brilliant.elf.export.ELF_Symbol;
import brilliant.elf.util.ByteUtil;
import brilliant.elf.vm.OS;

public class OpCodeTest {

	public static void main(String[] args) throws Exception {
		ELF elf = ELF.dlopen("C:/Users/Administrator/Desktop/libDS.so");
		// OS.dumpMemory(new
		// PrintStream("C:/Users/Administrator/Desktop/新建文本文档.txt"));

		// OpCodeDecodeHelper.decode(OS.getMemory(), 0xb68, 0xb74 - 0xb68, true,
		// new C());

		// List<ELF_Symbol> symbols = elf.dumpHashSymtab();
		//
		// String functionName = "Java_com_gemo_mintourc_util_DSUtil_decode";
		//
		// for (ELF_Symbol s : symbols) {
		// if (functionName.equals(s.name)) {
		// System.out.println(functionName);
		// dumpOpCode(s);
		// break;
		// }
		// }

		dump(elf.extractELF().funcs);
	}

	private static void dump(List<ELF_Symbol> symbols) {
		
		Collections.sort(symbols);
		
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

	private static class C implements OpCodeHookCallback {

		@Override
		public boolean exception(Throwable t) {
			t.printStackTrace();
			return true;
		}

		@Override
		public boolean Thumb32instructionDecodeDone(int current,
				int instruction, ParseTemplate ret) {
			System.out.println(Integer.toHexString(current) + " : "
					+ ByteUtil.bytes2Hex(ByteUtil.int2bytes(instruction))
					+ "  " + ret.parse(instruction));
			return true;
		}

		@Override
		public boolean Thumb16instructionDecodeDone(int current,
				short instruction, ParseTemplate ret) {
			System.out.println(Integer.toHexString(current) + " : "
					+ ByteUtil.bytes2Hex(ByteUtil.short2bytes(instruction))
					+ "  " + ret.parse(instruction));
			return true;
		}

		@Override
		public boolean ArminstructionDecodeDone(int current, int instruction,
				ParseTemplate ret) {
			System.out.println(Integer.toHexString(current) + " : "
					+ ByteUtil.bytes2Hex(ByteUtil.int2bytes(instruction))
					+ "  " + ret.parse(instruction));
			return true;
		}
	}

}
