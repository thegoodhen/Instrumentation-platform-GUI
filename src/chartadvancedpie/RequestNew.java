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

    public int[] getData() {
	return arr;
    }

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
