package brilliant.arm.OpCode.thumb.instructionSet32;

import static brilliant.arm.OpCode.factory.OpUtil.assert0;
import static brilliant.arm.OpCode.factory.OpUtil.assert1;
import static brilliant.arm.OpCode.factory.OpUtil.getShiftInt;
import brilliant.arm.OpCode.thumb.instruction32.STRBT_A8_684;
import brilliant.arm.OpCode.thumb.instruction32.STRB_A8_678;
import brilliant.arm.OpCode.thumb.instruction32.STRB_A8_682;
import brilliant.arm.OpCode.thumb.instruction32.STRHT_A8_704;
import brilliant.arm.OpCode.thumb.instruction32.STRH_A8_698;
import brilliant.arm.OpCode.thumb.instruction32.STRH_A8_702;
import brilliant.arm.OpCode.thumb.instruction32.STRT_A8_706;
import brilliant.arm.OpCode.thumb.instruction32.STR_A8_672;
import brilliant.arm.OpCode.thumb.instruction32.STR_A8_676;
import brilliant.arm.OpCode.thumb.instruction32.support.ParseSupport;

class StoreSingleDataA6_242 {
	public static ParseSupport parse(int data) {

		int op1 = getShiftInt(data, 21, 3);
		int op2 = getShiftInt(data, 6, 6);

		if (op1 == 0b000) {
			if (assert1(op2, 2, 5))
				return STRB_A8_678.INSTANCE;

			if (assert0(op2, 2, 3) && assert1(op2, 4, 5))
				return STRB_A8_678.INSTANCE;
		}

		if (op1 == 0b100)
			return STRB_A8_678.INSTANCE;

		if (op1 == 0b000) {
			if (op2 == 0b000000)
				return STRB_A8_682.INSTANCE;

			if (assert0(op2, 2) && assert1(op1, 3, 4, 5))
				return STRBT_A8_684.INSTANCE;
		}

		if (op1 == 0b001) {

			if (assert0(op2, 2, 5))
				return STRH_A8_698.INSTANCE;

			if (assert0(op2, 2, 3) && assert1(op2, 4, 5))
				return STRH_A8_698.INSTANCE;
		}

		if (op1 == 0b101)
			return STRH_A8_698.INSTANCE;

		if (op1 == 0b001) {
			if (op2 == 0b000000)
				return STRH_A8_702.INSTANCE;

			if (assert0(op2, 2) && assert1(op2, 3, 4, 5))
				return STRHT_A8_704.INSTANCE;
		}

		if (op1 == 0b010) {
			if (assert1(op1, 2, 5))
				return STR_A8_672.INSTANCE;

			if (assert0(op2, 2, 3) && assert1(op2, 4, 5))
				return STR_A8_672.INSTANCE;
		}

		if (op1 == 0b110)
			return STR_A8_672.INSTANCE;

		if (op1 == 0b010) {
			if (op2 == 0b000000)
				return STR_A8_676.INSTANCE;

			if (assert0(op2, 2) && assert1(op2, 3, 4, 5))
				return STRT_A8_706.INSTANCE;
		}

		throw new IllegalArgumentException("Unable to decode instruction "
				+ Integer.toBinaryString(data));
	}
}
