package chartadvancedpie;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * A {@link GUIAction}, which can have a named assigned. This name is then displayed
 * when traversing {@link Menu}s.
 * @author thegoodhen
 */
public class NamedGUIAction extends GUIAction{
	private String name;

	public NamedGUIAction(String name)
	{
		this.name=name;
	}

	public void setName(String name)
	{
		this.name=name;
	}
	
	public String getName()
	{
		return this.name;
	}
	
}
