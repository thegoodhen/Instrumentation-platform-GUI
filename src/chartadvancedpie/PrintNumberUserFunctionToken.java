/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import shuntingyard.AbstractBuiltInFunctionToken;
import shuntingyard.ByteNumberToken;
import shuntingyard.FloatNumberToken;
import shuntingyard.VariableToken;
import shuntingyard.VirtualMachine;

/**
 * CLUC function, used to print a floating number in the status window of a {@link GUIPanel}. When the floating number
 * is very close to an integer ({@literal absolute difference<0.0000001}),
 * print out as an integer.
 * @author thegoodhen
 */
public class PrintNumberUserFunctionToken extends AbstractBuiltInFunctionToken {

    public PrintNumberUserFunctionToken(String tokenString) {
	super(tokenString);
	this.setReturnType(new ByteNumberToken("0"));//TODO: introduce void
	VariableToken param1 = new VariableToken("number");
	param1.setType(new FloatNumberToken());
	this.addArgument(param1);
    }

    @Override
    public void run(VirtualMachine vm) {
	float numToPrint = vm.popFloatFromStack();
	GUIVirtualMachine gvm = null;
	if (vm instanceof GUIVirtualMachine) {
	    gvm = (GUIVirtualMachine) vm;

	    //SerialCommunicator sc = ((GUIVirtualMachine) vm).getGUIPanel().getSerialCommunicator();
	    //sc.getWriter().sendInit2();
	    if ((Math.abs(numToPrint - (int) numToPrint)) < 0.0000001)//isn't a fraction then
	    {
		gvm.getGUIPanel().showText(Integer.toString((int) numToPrint) + "\n");
	    } else {

		gvm.getGUIPanel().showText(Float.toString(numToPrint) + "\n");
	    }
	}
	vm.pushByteOnStack((byte) 0);//TODO: remove when we introduce void
    }

    @Override
    public byte getBaseCode() {
	return (byte) 128;
    }

}
