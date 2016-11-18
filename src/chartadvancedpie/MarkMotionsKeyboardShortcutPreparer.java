/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

/**
 *
 * @author thegoodhen
 */
public class MarkMotionsKeyboardShortcutPreparer implements IKeyboardShortcutPreparer {

	public void prepareShortcuts(GUIKeyEventHandler gkeh) {
		GUIPanel gp=gkeh.getGUIPanel();
		Menu m=gkeh.getMainMenu();
			RegisterAction setMarkAction = new RegisterAction() {

				@Override
				public void doAction(String register) {
					if (register.charAt(0) >= 'a' && register.charAt(0) <= 'z')//local mark
					{
						gp.getCurrentGUITab().setMark(register);
					}
					//GUIPanel.this.setMark(register);
				}

				@Override
				public void doAction(String register, IRepetitionCounter irc) {
					doAction(register);
				}
			};

			RegisterAction jumpToMarkAction = new RegisterAction() {

				@Override
				public void doAction(String register) {
					if (register.charAt(0) >= 'a' && register.charAt(0) <= 'z')//local mark
					{
						gp.getCurrentGUITab().jumpToMark(register);
					}
					//GUIPanel.this.jumpToMark(register);
				}

				@Override
				public void doAction(String register, IRepetitionCounter irc) {
					doAction(register);
				}
			};
			
			Menu setMarkMenu = new RegisterSelectionMenu(gp, "set mark", setMarkAction);
			Menu jumpToMarkMenu = new RegisterSelectionMenu(gp, "jump to mark", jumpToMarkAction);

			m.addSubMenu(
				"m", setMarkMenu);
			m.addSubMenu(
				"'", jumpToMarkMenu);


}

}
