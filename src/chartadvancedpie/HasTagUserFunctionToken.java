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
 *
 * @author thegoodhen
 */
public class HasTagUserFunctionToken extends AbstractBuiltInFunctionToken {

    public HasTagUserFunctionToken(String tokenString) {
	super(tokenString);
	this.setReturnType(new ByteNumberToken("0"));
	VariableToken param1 = new VariableToken("GUIElementID");
	VariableToken param2 = new VariableToken("TagString");
	param1.setType(new IntegerNumberToken());
	param2.setType(new IntegerNumberToken());
	this.addArgument(param1);
	this.addArgument(param2);
    }

    @Override
    public void run(VirtualMachine vm) {
	int stringLocation = vm.popIntFromStack();
	int guiElementID = vm.popIntFromStack();

	String tagString = vm.fetchStringFromHeap(stringLocation);
	GUIelement ge=((GUIVirtualMachine) vm).getGUIPanel().ID2GUIMap.get(guiElementID);
	if(ge.hasTag(tagString))
	{
	    vm.pushByteOnStack((byte)1);
	}
	else
	{
	    vm.pushByteOnStack((byte)0);
	}
	//Property p = (((GUIVirtualMachine) vm).getGUIPanel().ID2GUIMap.get(guiElementID)).getPropertyById(propertyId);
    }

    @Override
    public byte getBaseCode() {
	return (byte) 135;
    }

}
