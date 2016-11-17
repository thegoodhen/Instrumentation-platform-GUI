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
public class GetPropertyIDByNameUserFunctionToken extends AbstractBuiltInFunctionToken{


	public GetPropertyIDByNameUserFunctionToken(String tokenString) {
		super(tokenString);
		this.setReturnType(new IntegerNumberToken("0"));
		VariableToken param1=new shuntingyard.VariableToken("name");
		param1.setType(new IntegerNumberToken());
		this.addArgument(param1);
	}

	@Override
	public void run(VirtualMachine vm) {
		int stringAddress=vm.popIntFromStack();
		StringBuilder sb=new StringBuilder();
		byte currentCharVal=-1;
		int index=0;
		int PID=-1;

		//TODO: move this functionality into VM!
		while(currentCharVal!=0)
		{
			currentCharVal=(byte)(vm.getHeap().get(stringAddress+index).getValue());
			char currentChar=(char)currentCharVal;
			sb.append(currentChar);
			index++;
			currentCharVal=(byte)(vm.getHeap().get(stringAddress+index).getValue());
		}

		if(vm instanceof GUIVirtualMachine)
		{
			//GUIelement ge=((GUIVirtualMachine)vm).getGUIPanel().GUINameMap.get(sb.toString());
			PID=PropertyManager.get().getPropertyIDByName(sb.toString());
		}
		vm.pushIntOnStack(PID);
	}

	@Override
	public byte getBaseCode() {
		return (byte)131;
	}
	
}
