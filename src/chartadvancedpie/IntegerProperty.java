/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

/**
 *
 * @author thegoodhen
 */
public class IntegerProperty extends Property<Integer>{

	public IntegerProperty(Integer initializer) {
		super(initializer);
	}

	@Override
	public String getTypeName() {
		return "int";
	}

	
}
