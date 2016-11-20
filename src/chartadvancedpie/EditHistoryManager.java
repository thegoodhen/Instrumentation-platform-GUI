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

	private EditHistoryManager() {

	}

	public static EditHistoryManager get() {
		if (instance == null) {
			instance = new EditHistoryManager();
			return instance;
		} else {
			return instance;
		}
	}

	public void addAction(EditAction ea) {
		historyList.push(ea);
	}

	public void undoLastAction() {
		EditAction ea = historyList.pop();
		ea.undoAction();
	}

}
