/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

/**
 * Exception intended to be thrown if something goes wrong, during the communication
 * between the main module (PC) and the child modules.
 * @author thegoodhen
 */
class CommunicationException extends Exception {

	public CommunicationException(String s) {
		super(s);
	}
	
}
