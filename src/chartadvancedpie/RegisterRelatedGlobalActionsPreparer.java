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

    private void removeOrCopyGUIElementValue(boolean remove) {
	ArrayList<GUIelement> tempList = gp.getSelectedGUIelementsList();
	StringBuilder sb = new StringBuilder();
	for (GUIelement ge : tempList) {
	    if (sb.length() != 0) {
		sb.append("\n");
	    }
	    sb.append(ge.getValue());
	    if (remove) {
		ge.setValue(0);
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

	NamedGUIAction yankValueAction = new NamedGUIAction("yank (copy) value of current element(s)") {
	    @Override
	    public void doAction() {
		removeOrCopyGUIElementValue(false);
	    }

	};

	EditAction deleteAction = new EditAction("delete current element(s)") {

	    GUITab gt;
	    ArrayList<GUIelement> currentList = new ArrayList<>();

	    @Override
	    public void doAction() {
		gt = gp.getCurrentGUITab();
		currentList = (ArrayList<GUIelement>) gp.getCurrentGUITab().GUIList.clone();
		removeOrCopyGUIElements(true);
		//gp.getCurrentGUITab().removeGUIelement(gp.getCurrentGUITab().getFocusedGUIElementIndex());
		//GUIPanel.this.setMark(register);
	    }

	    @Override
	    public void doAction(IRepetitionCounter irc) {
		doAction();
	    }

	    @Override
	    public void undoAction() {
		gt.GUIList = currentList;
	    }
	};

	NamedGUIAction elementPasteLinkAction = new NamedGUIAction("paste link (don't copy)") {

	    @Override
	    public void doAction() {
		String[] elementNames = gp.getCurrentRegisterContentAndReset().split("\\r?\\n");
		for (String eName : elementNames) {
		    gp.getCurrentGUITab().insertGUIelement(gp.getCurrentGUITab().getFocusedGUIElementIndex(), eName, false);
		}
		//GUIPanel.this.setMark(register);
	    }

	    @Override
	    public void doAction(IRepetitionCounter irc) {
		doAction();
	    }
	};

	NamedGUIAction elementPasteValueAction = new NamedGUIAction("paste value") {

	    @Override
	    public void doAction() {

		String[] valuesStrings = gp.getCurrentRegisterContentAndReset().split("\\r?\\n");//TODO: strip lines not containing numbers
		ArrayList<GUIelement> tempList = gp.getSelectedGUIelementsList();
		Double[] values = new Double[valuesStrings.length];

		try {
		    for (int i = 0; i < values.length; i++) {
			values[i] = Double.parseDouble(valuesStrings[i]);
		    }
		} catch (Exception e) {
			gp.showError("Error parsing the register content.");
			return;
		}

		if (tempList.size() == values.length) {
		    for (int i = 0; i < tempList.size(); i++) {
			tempList.get(i).setValue((float) (double) values[i]);
		    }
		} else {
		    gp.showError("Number of selected elements ("+tempList.size()+") is inconsistent with how many numbers should be pasted ("+values.length+"). Aborting.");
		}
	    }

	    @Override
	    public void doAction(IRepetitionCounter irc) {
		doAction();
	    }
	};

	NamedGUIAction elementPasteCopyAction = new NamedGUIAction("paste copy)") {

	    @Override
	    public void doAction() {

		String[] elementNames = gp.getCurrentRegisterContentAndReset().split("\\r?\\n");
		for (String eName : elementNames) {
		    gp.getCurrentGUITab().insertGUIelement(gp.getCurrentGUITab().getFocusedGUIElementIndex(), eName, true);
		}
		//GUIPanel.this.setMark(register);
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
	yankMenu.addAction("v", yankValueAction);
	deleteMenu = new Menu(gp, "delete", false);
	deleteMenu.addAction("e", deleteAction);
	pasteMenu = new Menu(gp, "paste", false);
	pasteMenu.addAction("l", elementPasteLinkAction);
	pasteMenu.addAction("c", elementPasteCopyAction);
	pasteMenu.addAction("v", elementPasteValueAction);
	m.addSubMenu("y", yankMenu);
	m.addSubMenu("d", deleteMenu);
	m.addSubMenu("p", pasteMenu);
    }

}
