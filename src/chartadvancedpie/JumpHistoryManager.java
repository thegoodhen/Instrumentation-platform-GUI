/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * A class that manages jumplist (list of lately focused {@link GUIelement}s.
 * @author thegoodhen
 */
public class JumpHistoryManager {


	private static LinkedList<Position> historyList=new LinkedList<>();//TODO: add tree
	private static JumpHistoryManager instance=null;
	private GUIPanel gp;
	private static int listIndex=0;
	private static JumpAction lastJump=null;

	private JumpHistoryManager(GUIPanel gp)
	{
		this.gp=gp;
	}

	/**
	 * Singleton getter.
	 * @param gp the {@link GUIPanel} to be assigned to this {@code JumpHistoryManager}
	 * @return 
	 */
	public static JumpHistoryManager get(GUIPanel gp)
	{
		if(instance==null)
		{
			instance=new JumpHistoryManager(gp);
		return instance;
		}
		else
		{
			return instance;
		}
	}

	/**
	 * Return the {@link Position} object, determining the last focused {@link GUIelement}.
	 * If we are on the beginning of the changeList (no element was focused before), return null.
	 * Subsequent calls will return the previous one, the one before and so on.
	 * @return The {@link Position} object, determining the last focused {@link GUIelement}.
	 */
	public Position getPreviousPosition()
	{
		if(listIndex>=0)
		{
			listIndex--;
			Position p=historyList.get(listIndex);
			return p;
		}
		else
		{
			return null;
		}
	}


	/**
	 * Return the {@link Position} object, determining the next focused {@link GUIelement}.
	 * (Opposite of {@link getPreviousPosition}).
	 * If we are on the end of the changeList (no element was focused before), return null.
	 * Subsequent calls will return the previous one, the one before and so on.
	 * @return The {@link Position} object, determining the next focused {@link GUIelement}.
	 * @see #getPreviousPosition() 
	 */
	public Position getNextPosition()
	{
		if(listIndex<historyList.size()-1)
		{
			listIndex++;
			Position p= historyList.get(listIndex);
			return p;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Add the given {@link JumpAction} to the changeList.
	 * @param ea the {@link JumpAction} to add.
	 */
	public void addAction(JumpAction ea)
	{
		lastJump=ea;
		historyList.add(gp.getCurrentPosition());
		listIndex=historyList.size()-1;
	}

	/**
	 * Take the last {@link JumpAction}, added by the {@link #addAction} method and repeat it,
	 * by calling its {@code JumpAction#doAction()}.
	 * @see JumpAction#doAction() 
	 */
	public void repeatLastJump()
	{
		if(lastJump!=null)
		{
			lastJump.doAction();
		}
	}

	/**
	 * Take the last {@link JumpAction}, added by the {@link #addAction} method and repeat the inverse of it,
	 * by calling its {@code JumpAction#doInverseAction()}.
	 * @see JumpAction#doInverseAction() 
	 */
	public void repeatInverseOfLastJump()
	{
		if(lastJump!=null)
		{
			lastJump.doInverseAction();
		}
	}
	
}
