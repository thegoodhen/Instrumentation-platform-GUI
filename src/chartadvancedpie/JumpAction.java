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
public abstract class JumpAction extends NamedGUIAction{



	public JumpAction(String name) {
		super(name);
	}

	@Override
	public void doActionWithHandling(GUIPanel gp)
	{
		this.setCount(gp.getRepeatCount(false));
		doAction();
		JumpHistoryManager.get(gp).addAction(this);
		gp.resetCurrentCommandText();
		gp.resetRepeatCount();
	}

	/**
	 * Do the opposite action; do the backward motion to the one executed
	 * by the previously ran "doAction"
	 */
	public void doInverseAction()
	{
		this.setCount(-this.getCount());
		this.doAction();
		this.setCount(-this.getCount());
	}
	
}
