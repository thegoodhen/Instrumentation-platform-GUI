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
@Deprecated
public class MainMenu extends Menu{

	public MainMenu(GUIPanel gp, String s) {
		super(gp, s, true);
		this.setSuperMenu(this);
	}
	
}
