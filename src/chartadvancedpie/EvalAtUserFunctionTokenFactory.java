/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import shuntingyard.Token;
import shuntingyard.TokenFactory;

/**
 *
 * @author thegoodhen
 */
public class EvalAtUserFunctionTokenFactory extends TokenFactory {


	@Override
	public String getRegex() {
		return "evalAt";
	}

	@Override
	public Token generateInstance(String tokenString) {
		return new EvalAtUserFunctionToken(tokenString);
	}



}
