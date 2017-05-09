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
 * CLUC built-in function, used to evaluate the Y-value of a line in a chart,
 * given the line letter and the X position.
 * Expects an ID of the chart, string representing the letter of this line,
 * and a float x position, in this order.
 * Can be called as CHART.evalAt("lineLetter",xpos);
 * @author thegoodhen
 */
public class EvalAtUserFunctionToken extends AbstractBuiltInFunctionToken {

    public EvalAtUserFunctionToken(String tokenString) {
	super(tokenString);
	this.setReturnType(new FloatNumberToken("0"));
	VariableToken param1 = new VariableToken("GUIElementID");
	VariableToken param2 = new VariableToken("lineLetter");
	VariableToken param3 = new VariableToken("targetValue");
	param1.setType(new IntegerNumberToken());
	param2.setType(new IntegerNumberToken());
	param3.setType(new FloatNumberToken());

	this.addArgument(param1);
	this.addArgument(param2);
	this.addArgument(param3);
    }

    @Override
    public void run(VirtualMachine vm) {
	float targetValue = vm.popFloatFromStack();
	int lineLetterLocation = vm.popIntFromStack();
	int guiElementID = vm.popIntFromStack();

	GUIelement ge = (((GUIVirtualMachine) vm).getGUIPanel().ID2GUIMap.get(guiElementID));

	if (!(ge instanceof GUIChart)) {
	    vm.pushFloatOnStack((float) -1);
	} else {
	    String lineLetter = vm.fetchStringFromHeap(lineLetterLocation);
	    PlotLine pl = ((GUIChart) ge).getLine(lineLetter, false);
	    float val;
	    if (pl != null) {
		val = (float) pl.evaluateYatX(targetValue);
	    } else {
		val = 0;//NaN?
	    }
	    vm.pushFloatOnStack(val);
	}
    }

    @Override
    public byte getBaseCode() {
	return (byte) 135;
    }

}
