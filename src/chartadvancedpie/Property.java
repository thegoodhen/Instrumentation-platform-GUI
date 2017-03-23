/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
    private PropertyCallback getterPropertyCallback = null;

    public PropertyCallback getGetterPropertyCallback() {
	return getterPropertyCallback;
    }

    public void setGetterPropertyCallback(PropertyCallback getterPropertyCallback) {
	this.getterPropertyCallback = getterPropertyCallback;
    }

    public PropertyCallback getSetterPropertyCallback() {
	return setterPropertyCallback;
    }

    public void setSetterPropertyCallback(PropertyCallback setterPropertyCallback) {
	this.setterPropertyCallback = setterPropertyCallback;
    }
    private PropertyCallback setterPropertyCallback = null;

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
	    String slepice = getSetEventString();
	    c.compile(getSetEventString());
	    setEvent = c.getByteCodeAL();
	    System.out.println("SUCCESS");
	} catch (Exception ex) {
	    //Logger.getLogger(Property.class.getName()).log(Level.SEVERE, null, ex);
	}

    }

    private String getGetEventString() {
	return this.ge.getUniqueName() + "_" + name + "_G" + "();\n";
    }

    private String getSetEventString() {
	return this.ge.getUniqueName() + "_" + name + "_S" + "();\n";
	//return "slepice();\n";
	//return "EVENT_" + this.ge.getUniqueName() + "_S" + name + "();\n";
    }

    public T getValueSilent() {
	return this.value;
    }

    /**
     * runs Java callbacks, then user callback, then returns the value
     *
     * @return
     */
    public T getValue() {
	if (this.name.equals("RunTime")) {
	    System.out.println("kva kva");
	}
	if (getterPropertyCallback != null) {
	    getterPropertyCallback.run(this);
	}
	if (getEvent != null) {
	    ge.getGUIPanel().handleCallBack(getEvent);
	}
	return this.value;
    }

    public void setValueSilent(T newValue) {
	this.value = newValue;
    }

    /**
     * Sets the value property, then fires Java callback, then user code.
     *
     * @param newValue
     */
    public void setValue(T newValue) {
	this.value = newValue;
	if (setterPropertyCallback != null) {
	    setterPropertyCallback.run(this);
	}
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

    public Property<T> makeCopy() {
	Class<?> thisClass = this.getClass();
	System.out.println(thisClass.getName());

	for (Constructor<?> constructor : thisClass.getConstructors()) {
	    if (constructor.getParameterTypes().length == 4) {
		try {
		    Property<T> p = (Property<T>) constructor.newInstance(id, name, value, ge);
		    return p;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
		    Logger.getLogger(GUIelement.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InvocationTargetException ex) {
		    Logger.getLogger(Property.class.getName()).log(Level.SEVERE, null, ex);
		}
	    }
	}
	return null;
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
