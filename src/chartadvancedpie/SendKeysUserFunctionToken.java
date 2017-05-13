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
 *
 * @author thegoodhen
 */


/**
 * CLUC function to simulate focusing of some {@link GUIelement} and then sending key presses to it.
 * The ID of the {@link GUIelement} is the very first argument, the keypresses are the second one.
 * 
 * Uses the {@link KeySequence#execute(chartadvancedpie.GUIPanel)} method.
 * 
 * @see GUIelement#isFocused() 
 * @author thegoodhen
 */

public class SendKeysUserFunctionToken extends AbstractBuiltInFunctionToken {

    public SendKeysUserFunctionToken(String tokenString) {
	super(tokenString);
	this.setReturnType(new ByteNumberToken("0"));//TODO: introduce void
	VariableToken param1 = new shuntingyard.VariableToken("GUIElementID");
	VariableToken param2 = new shuntingyard.VariableToken("KeysString");
	param1.setType(new IntegerNumberToken());
	param2.setType(new IntegerNumberToken());
	this.addArgument(param1);
	this.addArgument(param2);
    }

    @Override
    public void run(VirtualMachine vm) {

	int stringLocation = vm.popIntFromStack();
	int guiElementID = vm.popIntFromStack();
	String s = vm.fetchStringFromHeap(stringLocation);
	GUIelement ge = ((GUIVirtualMachine) vm).getGUIPanel().ID2GUIMap.get(guiElementID);
	if (ge == null) {
	    vm.pushByteOnStack((byte) 1);
	    return;
	}

	if (vm instanceof GUIVirtualMachine) {
	    GUIPanel gp = ((GUIVirtualMachine) vm).getGUIPanel();
	    gp.editElement(ge);
	    KeySequence seq=new KeySequence(s);
	    seq.execute(gp);
	    gp.stopEditing();

	}
	vm.pushByteOnStack((byte) 0);//TODO: remove when we introduce void
    }

    @Override
    public byte getBaseCode() {
	return (byte) 129;
    }

}
