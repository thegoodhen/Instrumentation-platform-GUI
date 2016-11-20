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
public class EditHistoryKeyboardShortcutPreparer implements IKeyboardShortcutPreparer {

	public void prepareShortcuts(GUIKeyEventHandler gkeh) {

		GUIPanel gp = gkeh.getGUIPanel();

		NamedGUIAction undoAction= new NamedGUIAction("undo") {
			@Override
			public void doAction() {
				EditHistoryManager.get().undoLastAction();
			}
		};

		Menu m = gkeh.getMainMenu();

		m.addAction(
			"u", undoAction);
		/*
		m.addAction(
			"k", testAction2);
		m.addAction("o", jumpToOlderPosition);
		m.addAction("i", jumpToNewerPosition);
		m.addAction(";", repeatLastJump);
		m.addAction(",", repeatInverseOfLastJump);
		m.addAction(
			"%", jumpToPercent);
		m.addAction(
			"G", jumpToEnd);

		Menu goMenu = new Menu(gp, "Go to", false);
		goMenu.addAction(
			"g", jumpToBeginning);
		goMenu.addAction(
			"u", toggleUnique);
		goMenu.addAction(
			"G", jumpToEnd);

		goMenu.addAction(
			"t", nextTab);
		goMenu.addAction(
			"T", prevTab);

		m.addSubMenu(
			"g", goMenu);
			*/

	}

}
