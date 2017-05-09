/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

/**
 * Property of type float.
 * @author thegoodhen
 */
public class FloatProperty extends Property<Float>{

	public FloatProperty(int id, String name, Float initializer, GUIelement ge) {
		super(id, name, initializer,ge);
	}


	@Override
	public String getTypeName() {
		return "float";
	}


	
}
