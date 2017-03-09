/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import shuntingyard.Token;

/**
 * Class that should replace the obsolete Variable class with generics.
 *
 * @author thegoodhen
 */
public abstract class Property<T> {

    private T value;
    private int id;
    private String name;
    private ArrayList<Token> getEvent;
    private ArrayList<Token> setEvent;
    private GUIelement ge;

    public void recompile() {
	try {
	GUICompiler c = ge.getGUIPanel().getGUICompiler();
	    c.compile(getGetEventString());
	    getEvent = c.getByteCodeAL();
	    System.out.println("SUCCESS");
	} catch (Exception ex) {
	    //Logger.getLogger(Property.class.getName()).log(Level.SEVERE, null, ex);

	}
	try {
	GUICompiler c = ge.getGUIPanel().getGUICompiler();
	    String slepice=getSetEventString();
	    c.compile(getSetEventString());
	    setEvent = c.getByteCodeAL();
	    System.out.println("SUCCESS");
	} catch (Exception ex) {
	    //Logger.getLogger(Property.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    private String getGetEventString() {
	return "EVENT_" + this.ge.getUniqueName() + "_G" + name + "();\n";
    }

    private String getSetEventString() {
	return "slepice();\n";
	//return "EVENT_" + this.ge.getUniqueName() + "_S" + name + "();\n";
    }

    public T getValue() {
	if (getEvent != null) {
	    ge.getGUIPanel().handleCallBack(getEvent);
	}
	return this.value;
    }

    public void setValue(T newValue) {
	this.value = newValue;
	if (setEvent != null) {
	    ge.getGUIPanel().handleCallBack(setEvent);
	}
    }

    public Property(int id, String name, T initializer, GUIelement ge) {

	this.id = id;
	this.name = name;
	this.value = initializer;
	this.ge = ge;
	this.recompile();
    }

    public Property(Property<T> source) {
	this.id = source.getId();
	this.name = source.getName();
	this.value = source.getValue();
    }

    public int getId() {
	return this.id;
    }

    public String getName() {
	return this.name;
    }

    /**
     * Get the string, representing the name of the type.
     *
     * @return "string", "float" ... or such
     */
    public abstract String getTypeName();

}
