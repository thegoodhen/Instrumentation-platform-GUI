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
 * CLUC built-in function to force casting from float to integer,
 * forcing a lossy conversion if necessary.
 * @author thegoodhen
 */
public class CastToIntUserFunctionToken extends AbstractBuiltInFunctionToken {

    public CastToIntUserFunctionToken(String tokenString) {
	super(tokenString);
	this.setReturnType(new IntegerNumberToken("0"));//TODO: introduce void
	VariableToken param1 = new shuntingyard.VariableToken("floatToCast");
	param1.setType(new FloatNumberToken());
	this.addArgument(param1);
    }

    @Override
    public void run(VirtualMachine vm) {
	float f = vm.popFloatFromStack();
	vm.pushIntOnStack((int) f);//TODO: remove when we introduce void
    }

    @Override
    public byte getBaseCode() {
	return (byte) 129;
    }

}
