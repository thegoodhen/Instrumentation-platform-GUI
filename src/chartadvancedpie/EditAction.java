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
public abstract class EditAction extends NamedGUIAction{



	public EditAction(String name) {
		super(name);
	}

	@Override
	public void doActionWithHandling(GUIPanel gp)
	{
		this.setCount(gp.getRepeatCount(false));
		EditHistoryManager.get().addAction(this);
		doAction();
		gp.resetRepeatCount();
	}
	public abstract void undoAction();
	
}
