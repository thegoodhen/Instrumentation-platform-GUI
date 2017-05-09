/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

/**
 * Abstraction for any action, which didn't just result in the change of focus
 * or the way data is viewed, but actually changed properties of elements or more.
 * @author thegoodhen
 */
public abstract class EditAction extends NamedGUIAction {

    private Position pos = null;

    public EditAction(String name) {
	super(name);
    }

    /**
     * Get the Position object, assigned to this action. The position determines,
     * which tab was the user viewing, and which element was focused, when this
     * action happened. This is useful for storing the changelist.
     * @return the Position object, representing the currently focused element,
     * when this action happened.
     */
    public Position getPosition() {
	return this.pos;
    }

    @Override
    public void doActionWithHandling(GUIPanel gp) {
	this.setCount(gp.getRepeatCount(false));
	this.pos = gp.getCurrentPosition();
	EditHistoryManager.get(gp).addAction(this);
	doAction();
	gp.resetRepeatCount();
	gp.resetCurrentCommandText();
    }

    /**
     * Undo this action. This method is a part of the UNDO mechanism, which
     * needs to be reimplemented.
     */
    public abstract void undoAction();

}
