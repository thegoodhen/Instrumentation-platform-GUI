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
public class RegisterRelatedGlobalActionsPreparer implements IKeyboardShortcutPreparer {

	@Override
	public void prepareShortcuts(GUIKeyEventHandler gkeh) {
		GUIPanel gp = gkeh.getGUIPanel();
		Menu m = gkeh.getMainMenu();
		NamedGUIAction yankAction = new NamedGUIAction("yank current element") {
			@Override
			public void doAction() {
				gp.getCurrentGUITab().copyGUIelement(gp.getCurrentGUITab().getCurrentGUIElementIndex());
			}

			@Override
			public void doAction(IRepetitionCounter irc) {
				doAction();
			}

		};

		NamedGUIAction deleteAction = new NamedGUIAction("delete") {

			@Override
			public void doAction() {
				gp.getCurrentGUITab().removeGUIelement(gp.getCurrentGUITab().getCurrentGUIElementIndex());
				//GUIPanel.this.setMark(register);
			}

			@Override
			public void doAction(IRepetitionCounter irc) {
				doAction();
			}
		};

		NamedGUIAction elementPasteLinkAction = new NamedGUIAction("paste link (don't copy)") {

			@Override
			public void doAction() {
				gp.getCurrentGUITab().insertGUIelement(gp.getCurrentGUITab().getCurrentGUIElementIndex(), gp.getCurrentRegisterContentAndReset(), false);
				//GUIPanel.this.setMark(register);
			}

			@Override
			public void doAction(IRepetitionCounter irc) {
				doAction();
			}
		};

		NamedGUIAction elementPasteCopyAction = new NamedGUIAction("paste copy)") {

			@Override
			public void doAction() {
				gp.getCurrentGUITab().insertGUIelement(gp.getCurrentGUITab().getCurrentGUIElementIndex(), gp.getCurrentRegisterContentAndReset(), true);
				//GUIPanel.this.setMark(register);
			}

			@Override
			public void doAction(IRepetitionCounter irc) {
				doAction();
			}
		};

		Menu yankMenu;
		Menu deleteMenu;
		Menu pasteMenu;
		yankMenu = new Menu(gp, "yank (copy)", false);
		yankMenu.addAction("e", yankAction);
		deleteMenu = new Menu(gp, "delete", false);
		deleteMenu.addAction("e", deleteAction);
		pasteMenu = new Menu(gp, "paste", false);
		pasteMenu.addAction("l", elementPasteLinkAction);
		pasteMenu.addAction("c", elementPasteCopyAction);
		m.addSubMenu("y", yankMenu);
		m.addSubMenu("d", deleteMenu);
		m.addSubMenu("p", pasteMenu);
	}

}
