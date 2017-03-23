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
public class newTabUserFunctionToken extends AbstractBuiltInFunctionToken {

    public newTabUserFunctionToken(String tokenString) {
	super(tokenString);
	this.setReturnType(new IntegerNumberToken("0"));
	VariableToken param1 = new shuntingyard.VariableToken("name");
	param1.setType(new IntegerNumberToken());
	this.addArgument(param1);
    }

    @Override
    public void run(VirtualMachine vm) {
	int stringAddress = vm.popIntFromStack();
	String name = vm.fetchStringFromHeap(stringAddress);
	int tabIndex=-1;
	if (vm instanceof GUIVirtualMachine) {
	    GUIPanel gp = ((GUIVirtualMachine) vm).getGUIPanel();

	    GUITab gt = new GUITab(gp,name);
	    tabIndex=gp.addGUITab(gt);
	    //gt.insertGUIelement(gt.getFocusedGUIElementIndex(), gp.getCurrentRegisterContentAndReset(), false);
	}
	vm.pushIntOnStack(tabIndex);
    }

    @Override
    public byte getBaseCode() {
	return (byte) 129;
    }

}
