package brilliant.arm.OpCode;

public class TypeFactory {

	public static final int LSL = 0;
	public static final int LSR = 1;
	public static final int ASR = 2;
	public static final int ROR = 3;

	private static final String[] TYPE = { "LSL", "LSR", "ASR", "ROR" };

	public static String parse(int type) {
		return TYPE[type];
	}

}
