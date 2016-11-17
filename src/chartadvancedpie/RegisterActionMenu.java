/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.HashMap;

/**
 * A menu with RegisterActions; it gets assigned a specific register and then
 * all the RegisterActions inside are given this register whenever they're run.
 *
 * @author thegoodhen
 */
public class RegisterActionMenu extends Menu {

	private String register="";

	public RegisterActionMenu(GUIPanel gp, String s) {
		super(gp, s, false);
	}

	public void setRegister(String register) {
		this.register = register;
	}

	public String getRegister() {
		return this.register;
	}

	public void addRegisterAction(String keystroke, RegisterAction ra) {
		this.addAction(keystroke, ra);
	}

	public void addAction(String keystroke, GUIAction ga) {
		if (!(ga instanceof RegisterAction)) {
			throw new RuntimeException("Only RegisterActions can be added to a RegisterActionMenu!");
		} else {
			this.addAction(keystroke, ga);
		}
	}


	public void handleAction(String keystroke) {
		RegisterAction ra = (RegisterAction)this.getAction(keystroke);
		if (ra != null) {
			ra.doActionWithCounterHandling(register, this.getGUIPanel());
		}
		this.suggestClosing();
	}

}
