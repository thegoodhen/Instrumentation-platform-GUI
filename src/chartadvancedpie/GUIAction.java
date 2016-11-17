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

	public void doActionWithCounterHandling(IRepetitionCounter irc)
	{
		doAction(irc);
		irc.resetRepeatCount();
	}

	@Override
	public void undoAction() {
	}
	
}
