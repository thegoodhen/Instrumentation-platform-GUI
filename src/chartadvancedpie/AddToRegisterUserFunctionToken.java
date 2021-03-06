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
 * Take a register, provided as the first string argument, and append text to it,
 * provided as a second string argument.
 * @author thegoodhen
 */
public class AddToRegisterUserFunctionToken extends AbstractBuiltInFunctionToken {

    public AddToRegisterUserFunctionToken(String tokenString) {
	super(tokenString);
	this.setReturnType(new ByteNumberToken("0"));//TODO: introduce void
	//VariableToken param1 = new shuntingyard.VariableToken("GUIElementID");
	VariableToken param1 = new shuntingyard.VariableToken("Register");
	VariableToken param2 = new shuntingyard.VariableToken("TargetString");
	//param1.setType(new IntegerNumberToken());
	param1.setType(new IntegerNumberToken());
	param2.setType(new IntegerNumberToken());
	this.addArgument(param1);
	this.addArgument(param2);
	//this.addArgument(param3);
    }

    @Override
    public void run(VirtualMachine vm) {

	int targetStringLocation = vm.popIntFromStack();
	int sourceStringLocation = vm.popIntFromStack();
	//int guiElementID = vm.popIntFromStack();

	String targetString = vm.fetchStringFromHeap(targetStringLocation);
	String registerName = vm.fetchStringFromHeap(sourceStringLocation);
	//GUIelement ge = ((GUIVirtualMachine) vm).getGUIPanel().ID2GUIMap.get(guiElementID);
	/*
	 if (ge == null) {
	 vm.pushByteOnStack((byte) 1);
	 return;
	 }
	 */

	if (vm instanceof GUIVirtualMachine) {
	    GUIPanel gp = ((GUIVirtualMachine) vm).getGUIPanel();

	    gp.setRegisterContent(registerName, gp.getRegisterContent(registerName)+targetString);
	    //gp.getGlobalMappingManager().addMapping(sourceString, targetString);

	}
	vm.pushByteOnStack((byte) 0);//TODO: remove when we introduce void
    }

    @Override
    public byte getBaseCode() {
	return (byte) 129;
    }

}
