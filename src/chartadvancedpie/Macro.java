package chartadvancedpie;


import java.util.ArrayList;
import javafx.scene.input.KeyEvent;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * This class is related to the old way of handling Macros and is now deprecated. This class is related to the old way of handling Macros and is now deprecated.
 * @author thegoodhen
 */
@Deprecated
public class Macro {
	private ArrayList<KeyEvent> buttonList;
	public Macro()
	{
		buttonList=new ArrayList<>();
	}

	public void addKeyEvent(KeyEvent ke)
	{
		this.buttonList.add(ke);
	}

	public void execute(GUIKeyEventHandler keh)
	{
		for(KeyEvent ke:buttonList)
		{
		//keh.handle(ke);
		ke.consume();
		}
			
	}
}
