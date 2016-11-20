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
private int id;
private String name;
public T getValue()
{
	return this.value;
}
public void setValue(T newValue)
{
	this.value=newValue;
}

public Property(int id, String name, T initializer){

	this.id=id;
	this.name=name;
	this.value=initializer;
}

public Property(Property<T> source)
{
	this.id=source.getId();
	this.name=source.getName();
	this.value=source.getValue();
}

public int getId()
{
	return this.id;
}

public String getName()
{
	return this.name;
}

/**
 * Get the string, representing the name of the type.
 * @return "string", "float" ... or such
 */
public abstract String getTypeName();

}
