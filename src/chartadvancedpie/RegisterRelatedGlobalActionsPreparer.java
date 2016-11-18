/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.ArrayList;

/**
 *
 * @author thegoodhen
 */
public class RegisterRelatedGlobalActionsPreparer implements IKeyboardShortcutPreparer {

	GUIPanel gp;
	Menu m;

	private void removeOrCopyGUIElements(boolean remove) {
		ArrayList<GUIelement> tempList = gp.getSelectedGUIelementsList();
		StringBuilder sb = new StringBuilder();
		for (GUIelement ge : tempList) {
			if (sb.length() != 0) {
				sb.append("\n");
			}
			sb.append(ge.getUniqueName());
			if (remove) {
				gp.getCurrentGUITab().removeGUIelement(gp.getCurrentGUITab().getGUIElementIndex(ge));
			}
		}
		gp.setCurrentRegisterContentAndReset(sb.toString());

	}

	@Override
	public void prepareShortcuts(GUIKeyEventHandler gkeh) {
		gp = gkeh.getGUIPanel();
		m = gkeh.getMainMenu();

		NamedGUIAction yankAction = new NamedGUIAction("yank current element") {
			@Override
			public void doAction() {
				removeOrCopyGUIElements(false);
			}

			@Override
			public void doAction(IRepetitionCounter irc) {
				doAction();
			}

		};

		NamedGUIAction deleteAction = new NamedGUIAction("delete") {

			@Override
			public void doAction() {
				removeOrCopyGUIElements(true);
				//gp.getCurrentGUITab().removeGUIelement(gp.getCurrentGUITab().getFocusedGUIElementIndex());
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
				gp.getCurrentGUITab().insertGUIelement(gp.getCurrentGUITab().getFocusedGUIElementIndex(), gp.getCurrentRegisterContentAndReset(), false);
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
				gp.getCurrentGUITab().insertGUIelement(gp.getCurrentGUITab().getFocusedGUIElementIndex(), gp.getCurrentRegisterContentAndReset(), true);
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
