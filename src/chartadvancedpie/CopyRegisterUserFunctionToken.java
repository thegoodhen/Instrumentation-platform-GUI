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
 * CLUC built-in function, used to copy content between registers.
 * Accepts 2 string arguments. First one is the source register (from which we
 * want to copy) and the second one is the target register (to which we want to
 * copy).
 * @author thegoodhen
 */
public class CopyRegisterUserFunctionToken extends AbstractBuiltInFunctionToken {

    public CopyRegisterUserFunctionToken(String tokenString) {
	super(tokenString);
	this.setReturnType(new ByteNumberToken("0"));//TODO: introduce void
	//VariableToken param1 = new shuntingyard.VariableToken("GUIElementID");
	VariableToken param1 = new shuntingyard.VariableToken("SourceRegister");
	VariableToken param2 = new shuntingyard.VariableToken("TargetRegister");
	//param1.setType(new IntegerNumberToken());
	param1.setType(new IntegerNumberToken());
	param2.setType(new IntegerNumberToken());
	this.addArgument(param1);
	this.addArgument(param2);
	//this.addArgument(param3);
    }

    @Override
    public void run(VirtualMachine vm) {

	int targetRegisterLocation = vm.popIntFromStack();
	int sourceRegisterLocation = vm.popIntFromStack();
	//int guiElementID = vm.popIntFromStack();

	String targetRegisterName = vm.fetchStringFromHeap(targetRegisterLocation);
	String sourceRegisterName = vm.fetchStringFromHeap(sourceRegisterLocation);
	//GUIelement ge = ((GUIVirtualMachine) vm).getGUIPanel().ID2GUIMap.get(guiElementID);
	/*
	 if (ge == null) {
	 vm.pushByteOnStack((byte) 1);
	 return;
	 }
	 */

	if (vm instanceof GUIVirtualMachine) {
	    GUIPanel gp = ((GUIVirtualMachine) vm).getGUIPanel();

	    String sourceRegString=gp.getRegisterContent(sourceRegisterName);
	    gp.setRegisterContent(targetRegisterName, sourceRegString);
	    //gp.getGlobalMappingManager().addMapping(sourceString, targetString);

	}
	vm.pushByteOnStack((byte) 0);//TODO: remove when we introduce void
    }

    @Override
    public byte getBaseCode() {
	return (byte) 129;
    }

}
