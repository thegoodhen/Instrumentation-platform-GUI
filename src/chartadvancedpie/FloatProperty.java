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
public class FloatProperty extends Property<Float>{

	public FloatProperty(Float initializer) {
		super(initializer);
	}

	@Override
	public String getTypeName() {
		return "float";
	}

	
}
