/*-------------------------------
 Auto Generated By AutoGenetate.java
     Don't remove or modify
        License GPL/GNU
-------------------------------*/
package brilliant.arm.OpCode.arm.instruction;

import brilliant.arm.OpCode.arm.instruction.support.ParseSupport;
import brilliant.elf.vm.Register;

import static brilliant.arm.OpCode.factory.OpUtil.*;

public class MRS_A8_496 extends ParseSupport {

	public static final MRS_A8_496 INSTANCE = new MRS_A8_496();

	@Override
	protected int getRd(int data) {
		return getShiftInt(data, 12, 4);
	}

	@Override
	protected int getRm(int data) {
		boolean read = getShiftInt(data, 22, 1) == 1;

		if (read) {
			int mode = Register.getM();

			if (mode == Register.CPU_MODE_USER || mode == Register.CPU_MODE_SYSTEM)
				error(data);
			else
				return SPSR;
		} else {
			return CPSR;
		}

		return 0;
	}

	@Override
	public void performExecuteCommand(int data) {
	}

}