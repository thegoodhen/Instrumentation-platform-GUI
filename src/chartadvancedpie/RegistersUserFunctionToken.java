/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.Map;
import shuntingyard.AbstractBuiltInFunctionToken;
import shuntingyard.ByteNumberToken;
import shuntingyard.IntegerNumberToken;
import shuntingyard.VariableToken;
import shuntingyard.VirtualMachine;

/**
 * CLUC function to display the content of all registers in the status window
 * of the {@link GUIPanel}.
 * @author thegoodhen
 */
public class RegistersUserFunctionToken extends AbstractBuiltInFunctionToken {

    public RegistersUserFunctionToken(String tokenString) {
	super(tokenString);
	this.setReturnType(new ByteNumberToken("0"));//TODO: introduce void
	//VariableToken param1=new shuntingyard.VariableToken("text");
	//param1.setType(new IntegerNumberToken());
	//this.addArgument(param1);
    }

    @Override
    public void run(VirtualMachine vm) {

	StringBuilder sb = new StringBuilder();

	if (vm instanceof GUIVirtualMachine) {
	    GUIPanel gp = ((GUIVirtualMachine) vm).getGUIPanel();
	    for(Map.Entry<String, String> e:gp.registerMap.entrySet())
	    {
		sb.append(e.getKey()).append(": \n");
		sb.append(e.getValue()).append("\n\n");
	    }
		    gp.showText(sb.toString() + "\n");
	}
	vm.pushByteOnStack((byte) 0);//TODO: remove when we introduce void
    }

    @Override
    public byte getBaseCode() {
	return (byte) 129;
    }

}
