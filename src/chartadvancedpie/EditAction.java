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
    private Position pos=null;



	public EditAction(String name) {
		super(name);
	}

	public Position getPosition()
	{
	    return this.pos;
	}

	@Override
	public void doActionWithHandling(GUIPanel gp)
	{
		this.setCount(gp.getRepeatCount(false));
		this.pos=gp.getCurrentPosition();
		EditHistoryManager.get(gp).addAction(this);
		doAction();
		gp.resetRepeatCount();
	}
	public abstract void undoAction();
	
}
