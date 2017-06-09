package brilliant.arm.OpCode.factory;

public class AModeFactory {

	public static final String parse(int P, int U, int Rn) {
		if (Rn == OpUtil.SP) {

			if (P == 0 && U == 0)
				return "FA";

			if (P == 1 && U == 0)
				return "EA";

			if (P == 0 && U == 1)
				return "FD";

			if (P == 1 && U == 1)
				return "ED";
		} else {
			if (P == 0 && U == 0)
				return "DA";

			if (P == 1 && U == 0)
				return "DB";

			if (P == 0 && U == 1)
				return "IA";

			if (P == 1 && U == 1)
				return "IB";
		}
		throw new IllegalArgumentException();
	}

}
