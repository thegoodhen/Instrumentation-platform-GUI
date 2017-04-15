/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import shuntingyard.HelpByteMethods;
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

    public void sendValue() {
	//System.out.println("zkousim odpalit setr (svetr)");
	int data[] = new int[7];
	Integer geId = this.ge.getGUIPanel().GUIIDMap.get(ge);
	if(geId!=11)
	{
	    return;
	}
	System.out.println("SETTING WOOOOOOOO WOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO WOOOOOOO");
	if (geId != null) {
	    data[0]=(byte)3;//command to write value
	    data[1]=(byte)(int)geId;
	    data[2]=(byte)this.getId();
	    byte[] floatBytes=HelpByteMethods.getFloatBytes((float)(Float)this.getValueSilent());
	    for(int i=0;i<4;i++)
	    {
		data[i+3]=floatBytes[i];
	    }
	}
	try {
	    this.ge.getGUIPanel().getSerialCommunicator().getWriter().sendData(data, true);
	} catch (IOException ex) {
	    Logger.getLogger(Property.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

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

    public synchronized T getValueSilent() {
	return this.value;
    }

    /**
     * Returns the value assigned to this Property after having called Java
     * callbacks and user callbacks;
     *
     * @return
     */
    public synchronized T getValue() {
	return this.getValue(true, true);
    }

    /**
     * Returns the value assigned to this Property; optionally trigers Java
     * callbacks before that (allowing it to first calculate the value before
     * returning it) and optionally also then triggers user callbacks, allowing
     * user code to react to the event of the property being read.
     *
     * @param javaCallbacks whether Java callbacks should be fired
     * @param userCallbacks whether user callbacks should be fired
     * @return
     */
    public synchronized T getValue(boolean javaCallbacks, boolean userCallbacks) {
	if (this.name.equals("RunTime")) {
	    System.out.println("kva kva");
	}
	if (javaCallbacks) {
	    if (getterPropertyCallback != null) {
		getterPropertyCallback.run(this);
	    }
	}
	if (userCallbacks) {
	    if (getEvent != null) {
		ge.getGUIPanel().handleCallBack(getEvent);
	    }
	}
	return this.value;
    }

    /**
     * Sets the value silently, that is, without firing any callbacks.
     *
     * @param newValue
     */
    public synchronized void setValueSilent(T newValue) {
	this.value = newValue;
    }

    /**
     * Set the value, firing both Java and User callbacks.
     *
     * @param newValue
     */
    public synchronized void setValue(T newValue) {
	this.setValue(newValue, true, true);
    }

    /**
     * Sets the value property, then optionally fires Java callback, then
     * optionally fires user code.
     *
     * @param newValue
     * @param javaCallbacks
     * @param userCallbacks
     */
    public synchronized void setValue(T newValue, boolean javaCallbacks, boolean userCallbacks) {
	this.value = newValue;
	if (javaCallbacks) {
	    if (this.name.equals("Value")) {
		this.ge.requestRepaint();
	    }
	    if (setterPropertyCallback != null) {
		setterPropertyCallback.run(this);
	    }
	}
	if (userCallbacks) {
	    if (setEvent != null) {
		ge.getGUIPanel().handleCallBack(setEvent);
	    }
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
	this.value = source.getValueSilent();
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
