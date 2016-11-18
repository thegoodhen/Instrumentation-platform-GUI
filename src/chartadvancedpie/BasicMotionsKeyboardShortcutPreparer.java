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
public class BasicMotionsKeyboardShortcutPreparer {

	public static void prepareShortcuts(GUIKeyEventHandler gkeh) {

		GUIPanel gp = gkeh.getGUIPanel();

		NamedGUIAction testAction = new NamedGUIAction("previous element") {
			@Override
			public void doAction() {
				GUITab currentGUITab = gp.getCurrentGUITab();
				currentGUITab.traverseElements(true);
			}
		};
		NamedGUIAction testAction2 = new NamedGUIAction("next element") {

			@Override
			public void doAction() {
				GUITab currentGUITab = gp.getCurrentGUITab();
				currentGUITab.traverseElements(false);
			}
		};
		NamedGUIAction jumpToPercent = new NamedGUIAction("jump to (n) % ") {

			@Override
			public void doAction() {
				//doAction(1);
				//GUIList.get(selectedElementIndex).setFocused(false);
				//selectedElementIndex = 0;//GUIList.size() - 1;
				//traverseElements(true);
			}

			@Override
			public void doAction(IRepetitionCounter irc) {
				GUITab currentGUITab = gp.getCurrentGUITab();
				currentGUITab.jumpToPercent(irc);
			}
		};

		NamedGUIAction jumpToBeginning = new NamedGUIAction("beginning") {

			@Override
			public void doAction(IRepetitionCounter irc) {
				gp.getCurrentGUITab().jumpToBeginning(irc);
			}
		};
		NamedGUIAction jumpToEnd = new NamedGUIAction("end") {

			@Override
			public void doAction(IRepetitionCounter irc) {
				gp.getCurrentGUITab().jumpToEnd(irc);
			}
		};

		NamedGUIAction nextTab = new NamedGUIAction("next tab") {

			@Override
			public void doAction() {
					//doAction(1);
				//GUIList.get(selectedElementIndex).setFocused(false);
				//selectedElementIndex = 0;//GUIList.size() - 1;
				//traverseElements(true);
			}

			@Override
			public void doAction(IRepetitionCounter irc) {
				gp.traverseTabs(true);
			}
		};
		NamedGUIAction prevTab = new NamedGUIAction("previous tab") {

			@Override
			public void doAction() {
					//doAction(1);
				//GUIList.get(selectedElementIndex).setFocused(false);
				//selectedElementIndex = 0;//GUIList.size() - 1;
				//traverseElements(true);
			}

			@Override
			public void doAction(IRepetitionCounter irc) {
				gp.traverseTabs(false);
			}
		};

		Menu m = gkeh.getMainMenu();
		m.addAction(
			"j", testAction);
		m.addAction(
			"k", testAction2);
		m.addAction(
			"%", jumpToPercent);
		m.addAction(
			"G", jumpToEnd);

		Menu goMenu = new Menu(gp, "Go to", false);
		goMenu.addAction(
			"g", jumpToBeginning);
		goMenu.addAction(
			"G", jumpToEnd);

		goMenu.addAction(
			"t", nextTab);
		goMenu.addAction(
			"T", prevTab);

		m.addSubMenu(
			"g", goMenu);

	}

}
