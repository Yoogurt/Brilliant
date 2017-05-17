package brilliant.elf.export;

import java.util.List;
import java.util.Map;

import brilliant.elf.content.ELF;

public class ELFExport {

	public int initFunc;
	public int initArray;
	public int initArraySz;

	public int finiFunc;
	public int finiArray;
	public int finiArraySz;

	public List<ELF_Symbol> funcs;
	
	public List<ELF.FakeFuncImpl> funcImpl;
	
	public List<String> needed;

	public int rel;
	public int plt_rel;

	public int got;

}
