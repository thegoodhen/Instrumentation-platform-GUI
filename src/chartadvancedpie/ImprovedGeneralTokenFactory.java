/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import shuntingyard.ArgumentSeparatorTokenFactory;
import shuntingyard.DelegatingFactory;
import shuntingyard.DelegatingFactory;
import shuntingyard.LeftBracketFactory;
import shuntingyard.NumberTokenFactory;
import shuntingyard.OperatorTokenFactory;
import shuntingyard.RightBracketFactory;
import shuntingyard.StringLiteralTokenFactory;
import shuntingyard.VariableTokenFactory;

/**
 *
 * @author thegoodhen
 */
public class ImprovedGeneralTokenFactory extends DelegatingFactory {

	public ImprovedGeneralTokenFactory() {
		this.addSubFactory(new NumberTokenFactory());
		this.addSubFactory(new LeftBracketFactory());
		this.addSubFactory(new RightBracketFactory());
		this.addSubFactory(new ArgumentSeparatorTokenFactory());
		this.addSubFactory(new ExtendedFunctionTokenFactory());
		this.addSubFactory(new VariableTokenFactory());
		this.addSubFactory(new OperatorTokenFactory());
		this.addSubFactory(new StringLiteralTokenFactory());
	}
}
