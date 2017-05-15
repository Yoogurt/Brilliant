/*-------------------------------
 Auto Generated By AutoGenetate.java
     Don't remove or modify
        License GPL/GNU
-------------------------------*/
package brilliant.arm.OpCode.thumb.instruction32;

import brilliant.arm.OpCode.TypeFactory;
import brilliant.arm.OpCode.thumb.instruction32.support.ParseSupport;
import static brilliant.elf.vm.OS.*;
import static brilliant.elf.vm.Register.*;
import static brilliant.arm.OpCode.OpUtil.*;

public class UXTAB16_A8_808 extends ParseSupport {

	public static final UXTAB16_A8_808 INSTANCE = new UXTAB16_A8_808();

	@Override
	protected String getOpCode(int data) {
		return "UXTAB16";
	}

	@Override
	protected int getRd(int data) {
		return getShiftInt(data, 8, 4);
	}

	@Override
	protected int getRn(int data) {
		return getShiftInt(data, 16, 4);
	}

	@Override
	protected int getRm(int data) {
		return getShiftInt(data, 0, 4);
	}

	@Override
	protected int getType(int data) {
		return TypeFactory.ROR; // default ror
	}

	@Override
	protected int getShift(int data) {
		return getShiftInt(data, 4, 2) << 3;
	}

	@Override
	public void performExecuteCommand(int data) {
	}

}