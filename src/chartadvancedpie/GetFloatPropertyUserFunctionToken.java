/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import shuntingyard.AbstractBuiltInFunctionToken;
import shuntingyard.ByteNumberToken;
import shuntingyard.FloatNumberToken;
import shuntingyard.IntegerNumberToken;
import shuntingyard.NumericByteCodeToken;
import shuntingyard.VariableToken;
import shuntingyard.VirtualMachine;

/**
 * CLUC function, which returns the float value of some float property, given the GUIElementID this {@link Property} belongs to
 * and the PropertyID. This function is used internally and isn't usually typed by the user into the user code directly.
 * Dot notation, such as {@code ELEMENT.setValue(10);} is internally converted to calls to this CLUC function by {@link DotNotationParser}.
 * @see DotNotationParser
 * @author thegoodhen
 */
public class GetFloatPropertyUserFunctionToken extends AbstractBuiltInFunctionToken {

	public GetFloatPropertyUserFunctionToken(String tokenString) {
		super(tokenString);
		this.setReturnType(new FloatNumberToken("0"));
		VariableToken param1 = new VariableToken("GUIElementID");
		VariableToken param2 = new VariableToken("propertyID");
		param1.setType(new IntegerNumberToken());
		param2.setType(new IntegerNumberToken());
		this.addArgument(param1);
		this.addArgument(param2);
	}

	@Override
	public void run(VirtualMachine vm) {
		int propertyId = vm.popIntFromStack();
		int guiElementID = vm.popIntFromStack();

		Property p = (((GUIVirtualMachine) vm).getGUIPanel().ID2GUIMap.get(guiElementID)).getPropertyById(propertyId);

		if (!(p instanceof FloatProperty)) {
			vm.pushFloatOnStack((float)-1);
		} else {
			vm.pushFloatOnStack(((FloatProperty) p).getValue());
		}
	}

	@Override
	public byte getBaseCode() {
		return (byte) 135;
	}

}
