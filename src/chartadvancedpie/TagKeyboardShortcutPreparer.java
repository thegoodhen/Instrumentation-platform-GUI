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
public class TagKeyboardShortcutPreparer implements IKeyboardShortcutPreparer {

	public void prepareShortcuts(GUIKeyEventHandler gkeh) {

		GUIPanel gp = gkeh.getGUIPanel();

		EditAction toggleTag = new EditAction("toggle tag") {

			@Override
			public void doAction() {
				ArrayList<GUIelement> geList = gp.getSelectedGUIelementsList();

				boolean lFlag = gp.getLFlag();
				String letter = gp.getCurrentRegisterLetterAndReset();
				for (GUIelement ge : geList) {

					if (lFlag) {
						if (ge.hasTag(letter)) {
							ge.removeTags(letter);
						} else {
							ge.addTags(letter);
							System.out.println("tags" + ge.getTags());
						}
					} else {
						ge.removeAllTags();
					}
				}
			}

			@Override
			public void undoAction() {
				throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
			}
		};

		EditAction selectAllWithATag = new EditAction("visually select all containing tag") {

			@Override
			public void doAction() {
				ArrayList<GUIelement> geList = gp.getCurrentGUITab().GUIList;
				EditHistoryManager.get(gp).startUndoGroup();
				boolean lFlag = gp.getLFlag();
				String letter = gp.getCurrentRegisterLetterAndReset();
				if (lFlag) {
					for (GUIelement ge : geList) {

						if (ge.hasTag(letter)) {
							ge.setSelected(true);
						}
					}
				}

				EditHistoryManager.get(gp).endUndoGroup();
			}

			@Override
			public void undoAction() {
				throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
			}
		};
		Menu m = gkeh.getMainMenu();

		Menu tagMenu = new Menu(gp, "tag", false);
		tagMenu.addAction(
			"t", toggleTag);
		tagMenu.addAction(
			"v", selectAllWithATag);

		m.addSubMenu(
			"t", tagMenu);

	}

}
