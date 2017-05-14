package brilliant.elf.content;

import brilliant.elf.util.ByteUtil;

class ELF_Definition {

	public static int ELF_ST_BIND(int i) {
		return i >> 4;
	}

	public static int ELF_ST_TYPE(int i) {
		return i & 0xf;
	}

	public static int ELF_ST_INFO(int b, int t) {
		return (b << 4) + (t & 0xf);
	}

	public static byte ELF_R_TYPE(byte[] i) {
		return i[0];
	}

	public static byte ELF_R_TYPE(int i) {
		return (byte) i;
	}

	public static int ELF_R_SYM(byte[] i) {
		return ByteUtil.bytes2Int32(i) >> 8;
	}

	public static int ELF_R_SYM(int i) {
		return i >> 8;
	}

	public static int ELF_R_INFO(int s, int t) {
		return (s << 8) + (byte) t;
	}
}
