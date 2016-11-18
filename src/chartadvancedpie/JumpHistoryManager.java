/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * A class that manages undo, redo, undo tree and other aspects of editing history.
 * @author thegoodhen
 */
public class JumpHistoryManager {


	private static LinkedList<Position> historyList=new LinkedList<>();//TODO: add tree
	private static JumpHistoryManager instance=null;
	private GUIPanel gp;
	private static int listIndex=0;

	private JumpHistoryManager(GUIPanel gp)
	{
		this.gp=gp;
	}

	public static JumpHistoryManager get(GUIPanel gp)
	{
		if(instance==null)
		{
		return new JumpHistoryManager(gp);
		}
		else
		{
			return instance;
		}
	}

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

	public void addAction(JumpAction ea)
	{
		historyList.add(gp.getCurrentPosition());
		listIndex=historyList.size()-1;
	}
	
}
