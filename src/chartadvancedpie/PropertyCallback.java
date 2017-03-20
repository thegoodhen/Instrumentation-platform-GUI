/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

/**
 * Wrapper for a runnable code, ran whenever a getter or setter of a property is called.
 * This class does NOT contain the user code of the getter or setter, written in my language; 
 * instead, it contains Java code that's executed before the getter or setter call itself.
 * This allows, among other things, the value of the property to be dynamically updated before it's read.
 * Effectively it means that it's not necessary to constantly update a property for it to have an up-to-date value once it's read.
 * Instead, if calculating the value of the property is computationally intensive, it can be updated on demand; this class serves such purpose.
 * @author thegoodhen
 */
public abstract class PropertyCallback<T> {
    public void run(Property<T> p)
    {

    }
    
}
