/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import javafx.scene.input.KeyEvent;

/**
 * Interface for handlers of key presses.
 * @author thegoodhen
 */
public interface GUIKeyEventHandler {
	public void handle(String s);
	public Menu getMainMenu();
	public void setMainMenu(Menu m);
	public GUIPanel getGUIPanel();
}
