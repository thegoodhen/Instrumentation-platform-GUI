/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import shuntingyard.VirtualMachine;

/**
 * An extension of the regular {@link VirtualMachine}, allowing it to carry a reference to
 * a {@link GUIPanel}.
 * @author thegoodhen
 */
public class GUIVirtualMachine extends VirtualMachine {
	public GUIVirtualMachine(GUIPanel gp)
	{
		this.gp=gp;
	}

	private GUIPanel gp;

	public void setGUIPanel(GUIPanel gp) {
		this.gp = gp;
	}

	public GUIPanel getGUIPanel() {
		return this.gp;
	}

}
