/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import shuntingyard.Token;
import shuntingyard.AbstractBuiltInFunctionToken;
import shuntingyard.TokenFactory;

/**
 *
 * @author thegoodhen
 */
public class SetLinePropertyUserFunctionTokenFactory extends TokenFactory {


	@Override
	public String getRegex() {
		return "setLineProperty";
	}

	@Override
	public Token generateInstance(String tokenString) {
		return new SetLinePropertyUserFunctionToken(tokenString);
	}



}
