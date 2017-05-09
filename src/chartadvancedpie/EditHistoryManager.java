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

    /**
     * Utility class for managing undo groups. Sometimes many consecutive
     * actions happen, for instance when executing a user code. However,
     * it is still desirable to undo all of those actions in "one go", that is,
     * with a single undo. This utility class is intended to group multiple edit
     * actions, so they can be undone at once.
     */
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

    /**
     * Singleton pattern getter.
     * @param gp
     * @return 
     */
    public static EditHistoryManager get(GUIPanel gp) {
	if (instance == null) {
	    instance = new EditHistoryManager(gp);
	    return instance;
	} else {
	    return instance;
	}
    }

    /**
     * Indicate that the next actions recorded by calls to the addAction
     * method, up to the first call to the endUndoGroup function, should
     * be treated as a single action (undone at once, when requested).
     * @see endUndoGroup
     * @see addAction
     */
    public void startUndoGroup() {
	undoGroupOpen = true;
	currentCompositeAction = new compositeEditAction();
    }

    /**
     * Indicate that this is the end of the current undo group.
     * @see startUndoGroup
     */
    public void endUndoGroup() {
	undoGroupOpen = false;
	historyList.push(currentCompositeAction);
    }

    /**
     * Add action to the current undo group, if any, or to the list of actions, if none
     * undo group is currently active. This allows such action to be undone later.
     * @see startUndoGroup
     * @see endUndoGroup
     * @param ea 
     */
    public void addAction(EditAction ea) {
	if (undoGroupOpen) {
	    currentCompositeAction.addAction(ea);
	} else {
	    historyList.push(ea);
	}
    }

    /**
     * Undoes last action and jumps to the position, related to this undone
     * action.
     */
    public void undoLastAction() {
	EditAction ea = historyList.pop();
	ea.undoAction();
	Position p=ea.getPosition();
	if (p!=null) {
	    this.gp.setCurrentPosition(p);
	}
    }

}
