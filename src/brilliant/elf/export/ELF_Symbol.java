package brilliant.elf.export;

public final class ELF_Symbol implements Comparable<ELF_Symbol> {

	public String name;
	public int address;
	public int size;

	public int bind;
	public int type;

	public int other;
	public int shndx;

	@Override
	public int compareTo(ELF_Symbol o) {
		return address > o.address ? 1 : -1;
	}

}
