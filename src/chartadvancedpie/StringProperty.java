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
public class StringProperty extends Property<String>{

	public StringProperty(int id, String name, String initializer, GUIelement ge) {
		super(id, name, initializer, ge);
	}



	@Override
	public String getTypeName() {
		return "string";
	}

	
}
