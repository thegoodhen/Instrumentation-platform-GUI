/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

/**
 * Utility class that binds together the GUITab an element is in with the 
 * element, which is now focused
 * @author thegoodhen
 */
public class Position {
	private GUITab gt;
	private GUIelement ge;
	public Position(GUITab gt, GUIelement ge)
	{
		this.gt=gt;
		this.ge=ge;
	}

	public GUITab getGUITab()
	{
		return gt;
	}
	public GUIelement getGUIElement()
	{
		return ge;
	}
	
}
