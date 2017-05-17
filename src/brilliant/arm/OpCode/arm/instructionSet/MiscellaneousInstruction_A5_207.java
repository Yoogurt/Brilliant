package brilliant.arm.OpCode.arm.instructionSet;

import static brilliant.arm.OpCode.factory.OpUtil.assert0;
import static brilliant.arm.OpCode.factory.OpUtil.assert1;
import static brilliant.arm.OpCode.factory.OpUtil.getShiftInt;
import brilliant.arm.OpCode.arm.instruction.BKPT_A8_346;
import brilliant.arm.OpCode.arm.instruction.BLX_A8_350;
import brilliant.arm.OpCode.arm.instruction.BXJ_A8_354;
import brilliant.arm.OpCode.arm.instruction.BX_A8_352;
import brilliant.arm.OpCode.arm.instruction.CLZ_A8_362;
import brilliant.arm.OpCode.arm.instruction.ERET_B9_1982;
import brilliant.arm.OpCode.arm.instruction.HVC_B9_1984;
import brilliant.arm.OpCode.arm.instruction.MRS_A8_496;
import brilliant.arm.OpCode.arm.instruction.MRS_B9_1992;
import brilliant.arm.OpCode.arm.instruction.MSR_A8_500;
import brilliant.arm.OpCode.arm.instruction.MSR_B9_1994;
import brilliant.arm.OpCode.arm.instruction.MSR_B9_1998;
import brilliant.arm.OpCode.arm.instruction.SMC_B9_2002;
import brilliant.arm.OpCode.arm.instruction.support.ParseSupport;

@SuppressWarnings("deprecation")
public class MiscellaneousInstruction_A5_207 {

	public static ParseSupport parse(int data) {

		int op2 = getShiftInt(data, 4, 3);
		int B = getShiftInt(data, 9, 1);
		int op = getShiftInt(data, 21, 2);
		int op1 = getShiftInt(data, 16, 4);

		switch (op2) {
		case 0b000:
			if (B == 0b1) {
				if (assert0(op, 0))
					return MRS_B9_1992.INSTANCE;
				else if (assert1(op, 0))
					return MSR_B9_1994.INSTANCE;
			}
			if (B == 0b0) {
				if (assert0(op, 0))
					return MRS_A8_496.INSTANCE;
				if (op == 0b01) {
					if (assert0(op1, 0, 1))
						return MSR_A8_500.INSTANCE;
					if ((assert0(op1, 1) && assert1(op1, 0))
							|| (assert1(op1, 1)))
						return MSR_B9_1998.INSTANCE;
				}
				if (op == 0b11)
					return MSR_B9_1998.INSTANCE;
			}
			break;
		case 0b001:
			if (op == 0b01)
				return BX_A8_352.INSTANCE;
			if (op == 0b11)
				return CLZ_A8_362.INSTANCE;
			break;
		case 0b010:
			if (op == 0b01)
				return BXJ_A8_354.INSTANCE;
			break;
		case 0b011:
			if (op == 0b01)
				return BLX_A8_350.INSTANCE;
			break;

		case 0b101:
			return StauratingAdditionAndSubtraction_A5_202.parse(data);

		case 0b110:
			if (op == 0b11)
				return ERET_B9_1982.INSTANCE;
			break;
		case 0b111:
			switch (op) {
			case 0b01:
				return BKPT_A8_346.INSTANCE;
			case 0b10:
				return HVC_B9_1984.INSTANCE;
			case 0b11:
				return SMC_B9_2002.INSTANCE;
			}
			break;
		}

		throw new IllegalArgumentException("Unable to decode insruction "
				+ Integer.toBinaryString(data));
	}
}
