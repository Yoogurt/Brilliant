package brilliant.arm.OpCode.factory;

public class CondFactory {

	private static final String[] CONDITION_EXECUTION = { "EQ", "NE", "CS",
			"CC", "MI", "PL", "VS", "VC", "HI", "LS", "GE", "LT", "GT", "LE",
			"" };

	public static String parse(int cond) {
		return CONDITION_EXECUTION[cond];
	}
}
