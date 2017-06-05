package brilliant.arm.OpCode.arm;

import static brilliant.arm.OpCode.factory.OpUtil.assert1;
import brilliant.arm.OpCode.arm.instruction.support.ParseSupport;
import brilliant.arm.OpCode.arm.instructionSet.ConditionParseFactory;
import brilliant.arm.OpCode.arm.instructionSet.UnConditionParseFactory;

public class ArmFactory {
	public static ParseSupport parse(int data) {

		if (!assert1(data, 28, 29, 30, 31))
			return ConditionParseFactory.parseCondition(data);
		else
			return UnConditionParseFactory.parseUncondition(data);

	}
}
