/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * A class that manages undo, redo, undo tree and other aspects of editing
 * history.
 *
 * @author thegoodhen
 */
public class EditHistoryManager {

    private LinkedList<EditAction> historyList = new LinkedList<>();//TODO: add tree
    private static EditHistoryManager instance = null;
    private static boolean undoGroupOpen = false;
    private static compositeEditAction currentCompositeAction = null;
    private final GUIPanel gp;

    private class compositeEditAction extends EditAction {

	ArrayList<EditAction> eaList = new ArrayList<>();

	public compositeEditAction() {
	    super("");
	}

	public void addAction(EditAction ea) {
	    eaList.add(ea);
	}

	public void doAction() {
	    for (EditAction ea : eaList) {
		ea.doAction();
	    }
	}

	@Override
	public void undoAction() {
	    for (int i = eaList.size() - 1; i >= 0; i--) {
		EditAction ea = eaList.get(i);
		ea.undoAction();
	    }
	}

    }

    private EditHistoryManager(GUIPanel gp) {
	this.gp=gp;
    }

    public static EditHistoryManager get(GUIPanel gp) {
	if (instance == null) {
	    instance = new EditHistoryManager(gp);
	    return instance;
	} else {
	    return instance;
	}
    }

    public void startUndoGroup() {
	undoGroupOpen = true;
	currentCompositeAction = new compositeEditAction();
    }

    public void endUndoGroup() {
	undoGroupOpen = false;
	historyList.push(currentCompositeAction);
    }

    public void addAction(EditAction ea) {
	if (undoGroupOpen) {
	    currentCompositeAction.addAction(ea);
	} else {
	    historyList.push(ea);
	}
    }

    public void undoLastAction() {
	EditAction ea = historyList.pop();
	ea.undoAction();
	Position p=ea.getPosition();
	if (p!=null) {
	    this.gp.setCurrentPosition(p);
	}
    }

}
