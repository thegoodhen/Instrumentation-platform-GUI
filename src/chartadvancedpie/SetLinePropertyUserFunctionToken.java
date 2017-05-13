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




/**
 * CLUC function to set some {@link FloatProperty} of some {@link PlotLine} of some {@link GUIChart} to a value.
 * First argument is the ID of the {@link GUIElement}. Second is the letter of the line,
 * under which it is stored in the {@link GUIChart}.
 * Third argument is the value to set this {@link FloatProperty} to.
 * Finally, the 4th argument is the unique ID of this {@link Property}.
 * 
 * This ordering is a bit counterintuitive, but it is so for technical reasons,
 * beyond the scope of this document.
 * 
 * This function isn't usually called by the user directly. It's used
 * when compiling setter calls, such as {@code CHART1.setLineX(20);} by the
 * {@link DotNotationParser}.
 * 
 * Returns 0 if everything went well or -1, if the {@link GUIelement} reffered
 * by the ID is not a {@link GUIChart}.
 * 
 * 
 * @see DotNotationParser
 * @see Property#getId() 
 * @author thegoodhen
 */
public class SetLinePropertyUserFunctionToken extends AbstractBuiltInFunctionToken {

    public SetLinePropertyUserFunctionToken(String tokenString) {
	super(tokenString);
	this.setReturnType(new FloatNumberToken("0"));
	VariableToken param1 = new VariableToken("GUIElementID");
	VariableToken param2 = new VariableToken("lineLetter");
	VariableToken param3 = new VariableToken("targetValue");
	VariableToken param4 = new VariableToken("propertyID");
	param1.setType(new IntegerNumberToken());
	param2.setType(new IntegerNumberToken());
	param3.setType(new FloatNumberToken());
	param4.setType(new IntegerNumberToken());

	this.addArgument(param1);
	this.addArgument(param2);
	this.addArgument(param3);
	this.addArgument(param4);
    }

    @Override
    public void run(VirtualMachine vm) {
	int propertyId = vm.popIntFromStack();
	float targetValue = vm.popFloatFromStack();
	int lineLetterLocation = vm.popIntFromStack();
	int guiElementID = vm.popIntFromStack();

	GUIelement ge = (((GUIVirtualMachine) vm).getGUIPanel().ID2GUIMap.get(guiElementID));

	if (!(ge instanceof GUIChart)) {
	    vm.pushFloatOnStack((float) -1);
	}
	else
	{
	    String lineLetter=vm.fetchStringFromHeap(lineLetterLocation);
	   ((GUIChart) ge).setLineProperty(lineLetter, propertyId, targetValue);
	    vm.pushFloatOnStack((float) 0);
	}
    }

    @Override
    public byte getBaseCode() {
	return (byte) 135;
    }

}
