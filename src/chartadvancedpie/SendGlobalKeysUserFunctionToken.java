/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import shuntingyard.AbstractBuiltInFunctionToken;
import shuntingyard.ByteNumberToken;
import shuntingyard.IntegerNumberToken;
import shuntingyard.VariableToken;
import shuntingyard.VirtualMachine;

/**
 * CLUC function to simulate the key presses, when no {@link GUIelement} is focused.
 * 
 * Uses the {@link GUIPanel#executeMacro(java.lang.String)} method.
 * 
 * @see GUIelement#isFocused() 
 * @author thegoodhen
 */
public class SendGlobalKeysUserFunctionToken extends AbstractBuiltInFunctionToken {

    public SendGlobalKeysUserFunctionToken(String tokenString) {
	super(tokenString);
	this.setReturnType(new ByteNumberToken("0"));//TODO: introduce void
	VariableToken param1 = new shuntingyard.VariableToken("text");
	param1.setType(new IntegerNumberToken());
	this.addArgument(param1);
    }

    @Override
    public void run(VirtualMachine vm) {
	int stringAddress = vm.popIntFromStack();
	String s = vm.fetchStringFromHeap(stringAddress);

	if (vm instanceof GUIVirtualMachine) {
	    KeySequence macro = new KeySequence(s);
	    ((GUIVirtualMachine) vm).getGUIPanel().executeMacro(s);
	}
	vm.pushByteOnStack((byte) 0);//TODO: remove when we introduce void
    }

    @Override
    public byte getBaseCode() {
	return (byte) 129;
    }

}
