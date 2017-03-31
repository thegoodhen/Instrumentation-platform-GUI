/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import javafx.scene.paint.Color;
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
public class RGBAUserFunctionToken extends AbstractBuiltInFunctionToken {

    public RGBAUserFunctionToken(String tokenString) {
	super(tokenString);
	this.setReturnType(new FloatNumberToken("0"));
	VariableToken param1 = new VariableToken("r");
	VariableToken param2 = new VariableToken("g");
	VariableToken param3 = new VariableToken("b");
	VariableToken param4 = new VariableToken("a");
	param1.setType(new IntegerNumberToken());
	param2.setType(new IntegerNumberToken());
	param3.setType(new IntegerNumberToken());
	param4.setType(new IntegerNumberToken());
	this.addArgument(param1);
	this.addArgument(param2);
	this.addArgument(param3);
	this.addArgument(param4);
    }

    @Override
    public void run(VirtualMachine vm) {
	int a = vm.popIntFromStack();
	int b = vm.popIntFromStack();
	int g = vm.popIntFromStack();
	int r = vm.popIntFromStack();

	if (r >= 0 && r <= 255 && g >= 0 && g <= 255 && b >= 0 && b <= 255 &&a>=0 && a<=255) {
	    Color c = Color.rgb(r, g, b, (a) / 255.0);
	    vm.pushFloatOnStack( ColorManager.get().floatFromColor(c));
	} else {

	    vm.pushFloatOnStack(0.0F);
	}
    }

    @Override
    public byte getBaseCode() {
	return (byte) 135;
    }

}
