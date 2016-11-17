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
import shuntingyard.VariableToken;
import shuntingyard.VirtualMachine;

/**
 *
 * @author thegoodhen
 */
public class getGEValUserFunctionToken extends AbstractBuiltInFunctionToken {

	public getGEValUserFunctionToken(String tokenString) {
		super(tokenString);
		this.setReturnType(new FloatNumberToken("0"));//TODO: introduce void
		VariableToken param1 = new VariableToken("number");
		param1.setType(new IntegerNumberToken());
		this.addArgument(param1);
	}

	@Override
	public void run(VirtualMachine vm) {
		int GUIId = vm.popIntFromStack();
		float geValue=-1;
		GUIVirtualMachine gvm = null;
		if (vm instanceof GUIVirtualMachine) {
			gvm = (GUIVirtualMachine) vm;
			GUIelement ge=gvm.getGUIPanel().ID2GUIMap.get(GUIId);
			if(ge!=null)//TODO: throw errors
			{
				geValue=ge.getValue();
			}
		}
		vm.pushFloatOnStack(geValue);//TODO: remove when we introduce void
	}

	@Override
	public byte getBaseCode() {
		return (byte) 128;
	}

}
