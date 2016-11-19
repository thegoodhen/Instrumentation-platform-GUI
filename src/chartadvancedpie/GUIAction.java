package chartadvancedpie;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
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
	
	public int getCount()
	{
		return count;
	}

	protected void setCount(int count)
	{
		this.count=count;
	}

	public GUIPanel getGUIPanel()
	{
		return this.gp;
	}

	public void doActionWithHandling(GUIPanel gp)
	{
		this.gp=gp;
		count=gp.getRepeatCount(false);
		doAction();
		//gp.resetRepeatCount();
		//gp.resetRepeatCount();
	}

	
}
