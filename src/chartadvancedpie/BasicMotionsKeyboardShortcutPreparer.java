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
public class BasicMotionsKeyboardShortcutPreparer implements IKeyboardShortcutPreparer {

	public void prepareShortcuts(GUIKeyEventHandler gkeh) {

		GUIPanel gp = gkeh.getGUIPanel();

		JumpAction testAction = new JumpAction("previous element") {
			@Override
			public void doAction() {
				GUITab currentGUITab = gp.getCurrentGUITab();
				currentGUITab.traverseElements(true);
			}
		};
		JumpAction testAction2 = new JumpAction("next element") {

			@Override
			public void doAction() {
				GUITab currentGUITab = gp.getCurrentGUITab();
				currentGUITab.traverseElements(false);
			}
		};


		NamedGUIAction jumpToOlderPosition= new NamedGUIAction("previously visited element") {

			@Override
			public void doAction() {
				Position p=JumpHistoryManager.get(gp).getPreviousPosition();
				gp.setCurrentPosition(p);
			}
		};

		NamedGUIAction jumpToNewerPosition= new NamedGUIAction("next visited element") {

			@Override
			public void doAction() {
				Position p=JumpHistoryManager.get(gp).getNextPosition();
				gp.setCurrentPosition(p);
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
		m.addAction("o", jumpToOlderPosition);
		m.addAction("i", jumpToNewerPosition);
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
