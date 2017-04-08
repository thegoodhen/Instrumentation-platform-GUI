package chartadvancedpie;

import java.util.HashMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author thegoodhen
 */
public class Menu {

	private GUIPanel gp;
	private HashMap<String, NamedGUIAction> actionMap;
	private Menu superMenu;
	private String name;
	private boolean persistent = true;

	public Menu(GUIPanel gp, String s, boolean persistent) {
		this.persistent=persistent;
		this.gp = gp;
		this.setString(s);
		actionMap = new HashMap<>();
		this.setSuperMenu(this);
	}

	public void setGUIPanel(GUIPanel gp) {
		this.gp = gp;
	}

	public NamedGUIAction getAction(String s)
	{
		return actionMap.get(s);
	}

	public GUIPanel getGUIPanel()
	{
		return this.gp;
	}

	public void addAction(String keystroke, NamedGUIAction ga) {
			actionMap.put(keystroke, ga);
		//this.addAction(keystroke, ga, true);
	}

	/*
	public void addAction(String keystroke, NamedGUIAction ga, boolean closeWhenDone) {
		if (!closeWhenDone) {
			actionMap.put(keystroke, ga);
		} else {
			NamedGUIAction performAndCloseAction = new NamedGUIAction(ga.getName()) {
				@Override
				public void doAction() {
					ga.doAction();
					Menu.this.close();
				}

				@Override
				public void doAction(int repeatCount) {
					ga.doAction(repeatCount);
					Menu.this.close();
					gp.resetRepeatCount();
					//Menu.this.gp.setMenu(Menu.this.superMenu);

				}
			};
			actionMap.put(keystroke, performAndCloseAction);
		}

	}
	*/

	public void addSubMenu(String keystroke, Menu m) {
		openSubMenuAction osma = new openSubMenuAction(m, this);
		m.setSuperMenu(this);
		actionMap.put(keystroke, osma);
	}

	public void setSuperMenu(Menu superMenu) {
		this.superMenu = superMenu;
	}

	public void showMenu()
	{
			gp.showText("---------------------------------\n");
		for (String s : actionMap.keySet()) {
			NamedGUIAction nga = actionMap.get(s);
			gp.showText(s + ": " + nga.getName()+"\n");
		}
	}

	public void handleAction(String keystroke) {
		GUIAction ga = actionMap.get(keystroke);
		if (ga != null) {
			ga.doActionWithHandling(gp);
		} 
		if(!(ga instanceof openSubMenuAction) )//if we opened a submenu, we should never close this menu
		{
			this.suggestClosing();
		}
	}

	public void suggestClosing()
	{

			if (persistent == false) {
				close();
			}
	}

	public void handle(String keystroke) {
	    /*
		if (!keystroke.equals("x")) {//TODO: try esc
			handleAction(keystroke);
		} else {
			//close();//I implemented escaping elsewhere, this is now deprecated
		}
		    */
			handleAction(keystroke);
	}

	public void close() {

		this.gp.setMenu(this.superMenu);
		this.superMenu.suggestClosing();
	}

	private void setString(String s) {
		this.name = s;
	}

	public String getString() {
		return this.name;
	}

	protected class openSubMenuAction extends NamedGUIAction {

		Menu m;

		public openSubMenuAction(Menu m, Menu superMenu) {
			super("> " + m.getString());
			this.m = m;
			m.setSuperMenu(superMenu);
		}

		@Override
		public void doAction() {
			gp.setMenu(m);
		}



	}

	public void setPersistent(boolean persistent)
	{
		this.persistent=persistent;
	}
}
