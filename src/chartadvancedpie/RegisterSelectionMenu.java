package chartadvancedpie;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Menu that lets the user choose one register and then either runs a specific
 * RegisterAction, passing the name of the register to it, or opens a
 * RegisterActionMenu, passing the register name to it, so that when a
 * RegisterAction of the RegisterActionMenu is run later, it gets passed the
 * corresponding register.
 *
 * @author thegoodhen
 */
public class RegisterSelectionMenu extends Menu {

	private RegisterAction ra;

	public RegisterSelectionMenu(GUIPanel gp, String s, RegisterAction ra) {
		super(gp, s, false);
		this.setPersistent(false);
		this.ra = ra;
	}

	public RegisterSelectionMenu(GUIPanel gp, String s, RegisterActionMenu ram) {
		super(gp, s, false);
		this.setPersistent(false);
		ram.setSuperMenu(this);
		this.ra = new openRegisterActionSubMenuAction(ram, gp.getMenu());
	}

	public void setGenericRegisterAction(RegisterAction ra) {
		this.ra = ra;
	}

	public void showMenu()//TODO: this will interact with the GUIPanel
	{
		System.out.print("a-z to select register");
	}

	@Override
	public void handleAction(String keystroke) {
		if (keystroke.length() == 1) {
			int asciiCode = (int) keystroke.charAt(0);
			if ((asciiCode >= 'A' && asciiCode <= 'Z') || (asciiCode >= 'a' && asciiCode <= 'z')) {
				//close();
				this.ra.doAction(keystroke);

					this.suggestClosing();

			}
		}
	}

	private class openRegisterActionSubMenuAction extends RegisterAction {

		RegisterActionMenu ram;

		public openRegisterActionSubMenuAction(RegisterActionMenu ram, Menu superMenu) {
			super();
			this.ram = ram;
			ram.setSuperMenu(superMenu);
		}

		@Override
		public void doAction() {
		}

		

		@Override
		public void doAction(String register) {
			ram.setRegister(register);
			RegisterSelectionMenu.this.getGUIPanel().setMenu(ram);
		}

		@Override
		public void doAction(String register, IRepetitionCounter irc) {
			doAction(register);
		}

	}
}
