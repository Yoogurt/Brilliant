package brilliant.arm.OpCode.arm.instructionSet;

import static brilliant.arm.OpCode.OpUtil.assert0;
import static brilliant.arm.OpCode.OpUtil.assert1;
import static brilliant.arm.OpCode.OpUtil.getShiftInt;
import brilliant.arm.OpCode.arm.instruction.BL_A8_348;
import brilliant.arm.OpCode.arm.instruction.B_A8_334;
import brilliant.arm.OpCode.arm.instruction.LDMDA_A8_400;
import brilliant.arm.OpCode.arm.instruction.LDMDB_A8_402;
import brilliant.arm.OpCode.arm.instruction.LDMIB_A8_404;
import brilliant.arm.OpCode.arm.instruction.LDM_A8_398;
import brilliant.arm.OpCode.arm.instruction.LDM_B9_1986;
import brilliant.arm.OpCode.arm.instruction.LDM_B9_1988;
import brilliant.arm.OpCode.arm.instruction.POP_A8_536;
import brilliant.arm.OpCode.arm.instruction.PUSH_A8_538;
import brilliant.arm.OpCode.arm.instruction.STMDA_A8_666;
import brilliant.arm.OpCode.arm.instruction.STMDB_A8_668;
import brilliant.arm.OpCode.arm.instruction.STMIB_A8_670;
import brilliant.arm.OpCode.arm.instruction.STM_A8_664;
import brilliant.arm.OpCode.arm.instruction.STM_B9_2008;
import brilliant.arm.OpCode.arm.instruction.support.ParseSupport;

@SuppressWarnings("deprecation")
public class BranchWithLinkAndBlockDataTransfer_A5_214 {
	public static ParseSupport parse(int data) {

		int op = getShiftInt(data, 20, 6);
		int R = getShiftInt(data, 15, 1);
		int Rn = getShiftInt(data, 16, 4);

		if (assert0(op, 0, 2, 3, 4, 5))
			return STMDA_A8_666.INSTANCE;

		if (assert0(op, 2, 3, 4, 5) && assert1(op, 0))
			return LDMDA_A8_400.INSTANCE;

		if (assert0(op, 0, 2, 4, 5) && assert1(op, 3))
			return STM_A8_664.INSTANCE;

		if (op == 0b001001)
			return LDM_A8_398.INSTANCE;

		if (op == 0b001011)
			if (Rn != 0b1101)
				return LDM_A8_398.INSTANCE;
			else
				return POP_A8_536.INSTANCE;

		if (op == 0b010000)
			return STMDB_A8_668.INSTANCE;

		if (op == 0b010010)
			if (Rn != 0b1101)
				return STMDB_A8_668.INSTANCE;
			else
				return PUSH_A8_538.INSTANCE;

		if (assert0(op, 2, 3, 5) && assert1(op, 0, 4))
			return LDMDB_A8_402.INSTANCE;

		if (assert0(op, 0, 2, 5) && assert1(op, 3, 4))
			return STMIB_A8_670.INSTANCE;

		if (assert0(op, 2, 5) && assert1(op, 0, 3, 4))
			return LDMIB_A8_404.INSTANCE;

		if (assert0(op, 0, 5) && assert1(op, 2))
			return STM_B9_2008.INSTANCE;

		if (assert0(op, 5) && assert1(op, 0, 2))
			if (R == 0b0)
				return LDM_B9_1988.INSTANCE;
			else if (R == 0b1)
				return LDM_B9_1986.INSTANCE;

		if (assert0(op, 4) && assert1(op, 5))
			return B_A8_334.INSTANCE;

		if (assert1(op, 4, 5))
			return BL_A8_348.INSTANCE;

		throw new IllegalArgumentException("Unable to decode instruction "
				+ Integer.toBinaryString(data));
	}
}
