package brilliant.arm.OpCode.thumb.instructionSet32;

import brilliant.arm.OpCode.thumb.instruction32.support.ParseSupport;
import static brilliant.arm.OpCode.OpUtil.*;

public class Thumb32Factory {

	public static ParseSupport parse(int data) {

		int op1 = getShiftInt(data, 27, 2);
		int op2 = getShiftInt(data, 20, 7);
		int op = getShiftInt(data, 15, 1);

		if (op1 == 0b01) {
			if (assert0(op2, 2, 5, 6))
				return LoadStoreMultipleA6_237.parse(data);

			if (assert0(op2, 5, 6) && assert1(op2, 2))
				return LoadStoreExclusiveA6_238.parse(data);

			if (assert0(op2, 6) && assert1(op2, 5))
				return DataProcessingA6_243.parse(data);

			if (assert1(op2, 6))
				return CoprocessorA6_251.parse(data);
		}

		if (op1 == 0b10) {

			if (assert0(op2, 5))
				if (op == 0b0)
					return DataProcessingA6_231.parse(data);

			if (assert1(op2, 5))
				if (op == 0b0)
					return DataProcessingA6_234.parse(data);

			if (op == 0b1)
				return BranchesA6_235.parse(data);
		}

		if (op1 == 0b11) {
			if (assert0(op2, 0, 4, 5, 6))
				return StoreSingleDataA6_242.parse(data);

			if (assert0(op2, 1, 2, 5, 6) && assert1(op2, 0))
				return LoadByteA6_241.parse(data);

			if (assert0(op2, 2, 5, 6) && assert1(op2, 0, 1))
				return LoadHalfwordA6_240.parse(data);

			if (assert0(op2, 1, 5, 6) && assert1(op1, 0, 2))
				return LoadWordA6_239.parse(data);

			if (assert0(op2, 5, 6) && assert1(op2, 0, 1, 2))
				throw new UnsupportedOperationException("UNDEFINED Instruction");

			if (assert0(op2, 0, 5, 6) && assert1(op2, 4))
				return AdvancedSIMDA7_275.parse(data);

			if (assert0(op2, 4, 6) && assert1(op2, 5))
				return DataProcessingA6_245.parse(data);

			if (assert0(op2, 3, 6) && assert1(op2, 4, 5))
				return MultiplyA6_249.parse(data);

			if (assert0(op2, 6) && assert1(op2, 3, 4, 5))
				return LongMultiplyA6_250.parse(data);

			if (assert1(op2, 6))
				return CoprocessorA6_251.parse(data);
		}

		throw new IllegalArgumentException("Unable to decode instruction "
				+ Integer.toBinaryString(data));
	}
}
