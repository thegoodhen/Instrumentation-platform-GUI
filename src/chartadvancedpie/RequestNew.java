/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstraction of a request, sent by the connected module.
 * Replacement for the old request class, which is now @Deprecated.
 *
 * @author thegoodhen
 */
public class RequestNew {
    static ArrayList<Runnable> resolversList;

    private int[] arr;

    public RequestNew(int[] arr, GUIPanel gp) {
	this.arr = arr;
    }

    /**
     * Return the data of the request, as an array of bytes, stored as integers from 0 to 255. The first
     * integer determines how many numbers are going to follow. The second one determines the type of the request
     * an the rest are parameters, related to this request type that differ from type to type.
     * 
     * @return the data of the request, as an array of integers.
     */
    public int[] getData() {
	return arr;
    }

    /**
     * Take the request and resolve it, doing the action this request asks us to do.
     */
    public void resolve() {

	try {
	    Thread.sleep(1000);
	} catch (InterruptedException ex) {
	    Logger.getLogger(RequestNew.class.getName()).log(Level.SEVERE, null, ex);
	}
	for (int i = 0; i < arr.length; i++) {
	    System.out.println("slepice" + i);
	}
    }
}
