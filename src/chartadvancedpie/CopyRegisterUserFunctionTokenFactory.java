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
public class CopyRegisterUserFunctionTokenFactory extends TokenFactory {


	@Override
	public String getRegex() {
		return "addToRegister";
	}

	@Override
	public Token generateInstance(String tokenString) {
		return new CopyRegisterUserFunctionToken(tokenString);
	}



}
