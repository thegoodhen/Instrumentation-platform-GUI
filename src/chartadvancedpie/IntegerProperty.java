/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

/**
 * Property carrying an integer value.
 * @author thegoodhen
 */
public class IntegerProperty extends Property<Integer>{

	public IntegerProperty(int id, String name, Integer initializer, GUIelement ge) {
		super(id, name, initializer, ge);
	}

	IntegerProperty(Property<Integer> origP) {
		super(origP);
	}

	

	@Override
	public String getTypeName() {
		return "int";
	}


	
}
