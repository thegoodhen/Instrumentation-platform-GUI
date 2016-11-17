/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

/**
 * Class that should replace the obsolete Variable class with generics.
 * @author thegoodhen
 */
public abstract class Property<T> {
private T value;
public T getValue()
{
	return this.value;
}
public void setValue(T newValue)
{
	this.value=newValue;
}
public Property(T initializer){

	this.value=initializer;
}

/**
 * Get the string, representing the name of the type.
 * @return "string", "float" ... or such
 */
public abstract String getTypeName();

}
