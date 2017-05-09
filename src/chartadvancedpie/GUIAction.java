package chartadvancedpie;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Action, affecting or related to the GUI elements.
 * @author thegoodhen
 */
public class GUIAction extends GUIAbstractAction{
	private int count=1;
	private GUIPanel gp;

	@Override
	public void doAction() {
	}

	public void doAction(IRepetitionCounter irc)
	{
		int times=irc.getRepeatCount();
		for(int i=0;i<times;i++)
		{
			doAction();
		}
	}
	
	/**
	 * Get the count associated with this action.
	 * When the doAction method is called, a number is optionally assigned to it.
	 * This is the user-provided number, which usually indicates, how many times
	 * the action should be run. This getter can be used to later access this number.
	 * @return 
	 */
	public int getCount()
	{
		return count;
	}

	/**
	 * Sets the count.
	 * @see getCount
	 * @param count 
	 */
	protected void setCount(int count)
	{
		this.count=count;
	}

	public GUIPanel getGUIPanel()
	{
		return this.gp;
	}

	/**
	 * Call doAction, optionally also doing some other stuff, such as
	 * reseting the repeat count or calling undo system related functionality.
	 * @param gp 
	 */
	public void doActionWithHandling(GUIPanel gp)
	{
		this.gp=gp;
		count=gp.getRepeatCount(false);
		doAction();
		gp.resetRepeatCount();//TODO: not sure why this was commented out... maybe by uncommenting it, I have summoned the devils upon us
		gp.resetCurrentCommandText();
		//gp.resetRepeatCount();
	}

	
}
