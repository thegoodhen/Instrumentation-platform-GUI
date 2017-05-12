package chartadvancedpie;

import java.util.HashMap;

/**
 * A class for handling keyboard shortcuts, consisting of multiple keys.
 * Pressing keys simply translates to traversing menus or directly executing actions.
 * A menu can have actions and submenus in it. 
 * When Menu A contains a submenu B, A is said to be the supermenu of B.
 * This means that each Menu can have multiple submenus and one supermenu.
 * Each of submenus and actions
 * has one unique key assigned to it; when this key is pressed, the action is executed or the submenu is opened.
 * The menu can be displayed by the {@link GUIPanel} class as a text in the status window.
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
	this.persistent = persistent;
	this.gp = gp;
	this.setString(s);
	actionMap = new HashMap<>();
	this.setSuperMenu(this);
    }

    /**
     * Set the {@link GUIPanel} object, used to display this {@code Menu}.
     * @param gp 
     */
    public void setGUIPanel(GUIPanel gp) {
	this.gp = gp;
    }

    /**
     * @param s the string to use when looking up the action
     * @return an action, which will get executed by pressing the key sequence, expressed by the given string.
     * @see #addAction(java.lang.String, chartadvancedpie.NamedGUIAction) 
     */
    public NamedGUIAction getAction(String s) {
	return actionMap.get(s);
    }

    /**
     * 
     * @return the {@link GUIPanel}, assigned to this {@code Menu}
     */
    public GUIPanel getGUIPanel() {
	return this.gp;
    }

    /**
     * Schedule an action to be executed every time the user presses the key sequence,
     * expressed by the String provided.
     * 
     * The syntax of this string is consistent with {@link KeySequence} methods
     * and the {@link KeyProcessingUtils#createStringFromKeyEvent(javafx.scene.input.KeyEvent)} methods.
     * 
     * @param keystroke the string expressing the key sequence which, when pressed, leads to
     * the given action being fired
     * @param ga 
     * @see KeyProcessingUtils#createStringFromKeyEvent(javafx.scene.input.KeyEvent) 
     */
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
    /**
     * Add a submenu to this {@code Menu}, scheduling it to open whenever
     * the sequence of keys, indicated by the provided string is pressed.
     * 
     * This works by adding a new {@link openSubMenuAction} to this {@code Menu}.
     * 
     * @param keystroke the string expressing the key sequence which, when pressed, leads to
     * the given submenu being opened
     * @param m the submenu
     */
    public void addSubMenu(String keystroke, Menu m) {
	openSubMenuAction osma = new openSubMenuAction(m, this);
	m.setSuperMenu(this);
	actionMap.put(keystroke, osma);
    }

    /**
     * Set the menu, which should be opened, when this one is closed.
     * @param superMenu the menu, which should be opened, when this one is closed.
     */
    public void setSuperMenu(Menu superMenu) {
	this.superMenu = superMenu;
    }

    /**
     * Display this {@code Menu} in the statusWindow of the respective {@link GUIPanel}.
     * @see #setGUIPanel(chartadvancedpie.GUIPanel) 
     */
    public void showMenu() {
	gp.showText("---------------------------------\n");
	for (String s : actionMap.keySet()) {
	    NamedGUIAction nga = actionMap.get(s);
	    gp.showText(s + ": " + nga.getName() + "\n");
	}
    }

    /**
     * Run the {@link GUIAction}, or open the respective {@code Menu}, based on the
     * sequence of keys, represented by the string provided. 
     * @param keystroke the string representing the sequence of keys pressed
     * @see #addAction(java.lang.String, chartadvancedpie.NamedGUIAction) 
     */
    public void handle(String keystroke) {
	GUIAction ga = actionMap.get(keystroke);
	if (ga != null) {
	    ga.doActionWithHandling(gp);
	}
	if (!(ga instanceof openSubMenuAction))//if we opened a submenu, we should never close this menu
	{
	    this.suggestClosing();
	}
    }

    /**
     * Close this {@code Menu} if it is not persistent, do nothing otherwise.
     * @see #close() 
     * @see #setPersistent(boolean) 
     */
    public void suggestClosing() {

	if (persistent == false) {
	    close();
	}
    }


    /**
     * Close this menu. That is, start showing supermenu of this {@code Menu} instead, if such supermenu is persistent.
     * If not, also close it and continue recursively, closing supermenu of each {@code Menu}, till the first persistent is reached.
     * @see #getSuperMenu() 
     */
    public void close() {

	this.gp.setMenu(this.superMenu);
	this.superMenu.suggestClosing();
    }

    /**
     * Change the name of this {@code Menu} to the string provided.
     * @param s  the new name
     * @see #getString() 
     */
    private void setString(String s) {
	this.name = s;
    }

    /**
     * Return the name of this {@code Menu} to the string provided.
     * 
     * This text is displayed when traversing the Menu system.
     * 
     * @return the name of this {@code Menu} to the string provided.
     */
    public String getString() {
	return this.name;
    }

    /**
     * Utility class used for adding submenus to a {@code Menu}. 
     * calling {@link #doAction() } simply opens the menu added in the constructor
     * of this class.
     */
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

	@Override
	public void doActionWithHandling(GUIPanel gp) {
	    Menu.this.gp = gp;
	    this.setCount(gp.getRepeatCount(false));
	    doAction();

	}

    }

    /**
     * Set whether or not should this {@code Menu} be persistent
     * @param persistent 
     * @see isPersistent()
     */
    public void setPersistent(boolean persistent) {
	this.persistent = persistent;
    }

    /**
     * Whether this {@code Menu} is persistent. That is, whether it should close
     * automatically when one of the submenus closes or when one of the actions in this menu is executed.
     * @return whether this {@code Menu} is persistent. 
     */
    public boolean isPersistent()
    {
	return this.persistent;
    }

    public Menu getSuperMenu()
    {
	return this.superMenu;
    }
}
