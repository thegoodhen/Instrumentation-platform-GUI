/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

/**
 * Utility class, responsible for the preparation of the keyboard shortcuts,
 * related to motions, that is, for traversing the list of GUI elements.
 * @author thegoodhen
 */
public class BasicMotionsKeyboardShortcutPreparer implements IKeyboardShortcutPreparer {

	public void prepareShortcuts(GUIKeyEventHandler gkeh) {

		GUIPanel gp = gkeh.getGUIPanel();

		JumpAction testAction2 = new JumpAction("previous element") {
			@Override
			public void doAction() {
				GUITab currentGUITab = gp.getCurrentGUITab();
				//currentGUITab.traverseElements(true);
				currentGUITab.traverseElements(-this.getCount());
			}
		};
		JumpAction testAction = new JumpAction("next element") {

			@Override
			public void doAction() {
				GUITab currentGUITab = gp.getCurrentGUITab();
				//currentGUITab.traverseElements(false);
				currentGUITab.traverseElements(this.getCount());
			}
		};

		NamedGUIAction jumpToOlderPosition = new NamedGUIAction("previously visited element") {

			@Override
			public void doAction() {
				Position p = JumpHistoryManager.get(gp).getPreviousPosition();
				gp.setCurrentPosition(p);
			}
		};

		NamedGUIAction jumpToNewerPosition = new NamedGUIAction("next visited element") {

			@Override
			public void doAction() {
				Position p = JumpHistoryManager.get(gp).getNextPosition();
				gp.setCurrentPosition(p);
			}
		};

		NamedGUIAction repeatLastJump = new NamedGUIAction("repeat last jump") {

			@Override
			public void doAction() {
				JumpHistoryManager.get(gp).repeatLastJump();
			}
		};

		NamedGUIAction repeatInverseOfLastJump = new NamedGUIAction("repeat inverse of last jump") {

			@Override
			public void doAction() {
				JumpHistoryManager.get(gp).repeatInverseOfLastJump();
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

		JumpAction jumpToBeginning = new JumpAction("beginning") {

			@Override
			public void doAction() {
				if (gp.getNFlag())//number specified
				{
					gp.getCurrentGUITab().focusGUIelement(this.getCount());

				}
				else
				{
					
					gp.getCurrentGUITab().focusGUIelement(0);
				}
			}
		};


		EditAction toggleUnique= new EditAction("toggle type of names displayed") {

			@Override
			public void doAction() {
				gp.setUniqueNames(!(gp.showUniqueNames()));
			}

			@Override
			public void undoAction() {
				throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
			}
		};


		JumpAction jumpToEnd = new JumpAction("end") {

			@Override
			public void doAction() {
				if (gp.getNFlag())//number specified
				{
					gp.getCurrentGUITab().focusGUIelement(this.getCount());
				}
				else
				{
					
					gp.getCurrentGUITab().focusGUIelement(gp.getCurrentGUITab().getGUIListSize()-1);
				}
			}
		};

		JumpAction nextTab = new JumpAction("next tab") {


			@Override
			public void doAction() {
				gp.traverseTabs(this.getCount());
			}
		};
		JumpAction prevTab = new JumpAction("previous tab") {

			@Override
			public void doAction() {
				gp.traverseTabs(-this.getCount());
			}
		};

		Menu m = gkeh.getMainMenu();
		m.addAction(
			"j", testAction);
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

	}

}
