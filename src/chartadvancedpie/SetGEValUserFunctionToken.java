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
public class SetGEValUserFunctionToken extends AbstractBuiltInFunctionToken{

	public SetGEValUserFunctionToken(String tokenString) {
		super(tokenString);
		this.setReturnType(new ByteNumberToken("0"));
		VariableToken param1=new VariableToken("GUIElement");
		VariableToken param2=new VariableToken("value");
		param1.setType(new IntegerNumberToken());
		param2.setType(new FloatNumberToken());
		this.addArgument(param1);
		this.addArgument(param2);
	}

	@Override
	public void run(VirtualMachine vm) {
		float val=vm.popFloatFromStack();
		int index=vm.popIntFromStack();
		
		(((GUIVirtualMachine)vm).getGUIPanel().ID2GUIMap.get(index)).setValue((int)val);//TODO:change to float! 
		vm.pushByteOnStack((byte)0);
	}

	@Override
	public byte getBaseCode() {
		return (byte)133;
	}
	
}
