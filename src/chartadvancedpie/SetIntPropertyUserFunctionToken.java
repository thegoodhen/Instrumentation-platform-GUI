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
 * CLUC function to set some {@link IntegerProperty} of some {@link GUIElement} to a value.
 * First argument is the ID of the {@link GUIElement}. Second is the target value
 * to set this {@link IntegerProperty} to.
 * Finally, the 3rd argument is the unique ID of this {@link Property}.
 * 
 * This ordering is a bit counterintuitive, but it is so for technical reasons,
 * beyond the scope of this document.
 * 
 * This function isn't usually called by the user directly. It's used
 * when compiling setter calls, such as {@code SLIDER3.setSelected(0);} by the
 * {@link DotNotationParser}.
 * 
 * @see Property#getId() 
 * @see DotNotationParser
 * @author thegoodhen
 */
public class SetIntPropertyUserFunctionToken extends AbstractBuiltInFunctionToken {

	public SetIntPropertyUserFunctionToken(String tokenString) {
		super(tokenString);
		this.setReturnType(new ByteNumberToken("0"));
		VariableToken param1 = new VariableToken("GUIElementID");
		VariableToken param2 = new VariableToken("targetValue");
		VariableToken param3 = new VariableToken("propertyID");
		param1.setType(new IntegerNumberToken());
		param2.setType(new IntegerNumberToken());
		param3.setType(new IntegerNumberToken());
		this.addArgument(param1);
		this.addArgument(param2);
		this.addArgument(param3);
	}

	@Override
	public void run(VirtualMachine vm) {
		int propertyId = vm.popIntFromStack();
		int targetValue = vm.popIntFromStack();
		int guiElementID = vm.popIntFromStack();

		Property p = (((GUIVirtualMachine) vm).getGUIPanel().ID2GUIMap.get(guiElementID)).getPropertyById(propertyId);
		p.setValue(targetValue);

		if (!(p instanceof IntegerProperty)) {
			vm.pushByteOnStack((byte)1);
		} else {
			vm.pushByteOnStack((byte)0);
		}
	}

	@Override
	public byte getBaseCode() {
		return (byte) 134;
	}

}
