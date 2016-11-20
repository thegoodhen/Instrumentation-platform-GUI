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

	public IntegerProperty(int id, String name, Integer initializer) {
		super(id, name, initializer);
	}

	IntegerProperty(Property<Integer> origP) {
		super(origP);
	}

	

	@Override
	public String getTypeName() {
		return "int";
	}


	
}
