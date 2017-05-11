/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * A read-only GUI element, used to display a numeric value.
 * @author thegoodhen
 */
public class GUIDisplay extends GUIelement {

    public GUIDisplay() {

    }

    public GUIDisplay(GUITab gut) {
	super(gut);
	this.setName("Display");
	this.setGUIPanel(gut.getGUIPanel());

		//super(r);
	//actionMap.put("l", testAction);
	//actionMap.put("h", testAction2);
    }

    //private int value=50;
    public void setValue(int value)//TODO: change this to like IntegerProperty or something and make it generic! :3
    {
	super.setValue(value);
	super.update();
    }

    @Override
    public String shortDesc() {
	return "GUI element - display";
    }

    @Override
    public void setGUITab(GUITab gut) {
	super.setGUITab(gut);

	RegisterAction yankAction = new RegisterAction("yank (copy)") {

	    @Override
	    public void doAction(String register) {
		if ((register.charAt(0) >= 'a' && register.charAt(0) <= 'z') || (register.charAt(0) >= 'A' && register.charAt(0) <= 'Z')) {
		    GUIDisplay.this.getGUIPanel().setRegisterContent(register, Float.toString(GUIDisplay.this.getValue()));
		}
		//GUIPanel.this.setMark(register);
	    }

	    @Override
	    public void doAction(String register, IRepetitionCounter irc) {
		doAction(register);
	    }
	};

	NamedGUIAction unnamedRegisterYankAction = new NamedGUIAction("yank (copy)") {
	    @Override
	    public void doAction() {

		GUIDisplay.this.getGUIPanel().setRegisterContent("%", Float.toString(GUIDisplay.this.getValue()));
	    }

	    @Override
	    public void doAction(IRepetitionCounter irc) {
		GUIDisplay.this.getGUIPanel().setRegisterContent("%", Float.toString(GUIDisplay.this.getValue()));
	    }

	};

	NamedGUIAction unnamedRegisterPasteAction = new NamedGUIAction("paste") {
	    @Override
	    public void doAction() {

		String content = GUIDisplay.this.getGUIPanel().getRegisterContent("%");
		try {
		    GUIDisplay.this.setValue((int) Float.parseFloat(content));
		} catch (Exception e) {

		}

	    }

	    @Override
	    public void doAction(IRepetitionCounter irc) {
		doAction();
	    }

	};

	RegisterActionMenu ram = new RegisterActionMenu(this.getGUIPanel(), "kokodak");
	ram.addRegisterAction("y", yankAction);
	Menu selectRegMenu = new RegisterSelectionMenu(GUIDisplay.this.getGUIPanel(), "access register", ram);

	this.setMenu(new Menu(gut.getGUIPanel(), "display menu", true));
	this.getMenu().addAction("y", unnamedRegisterYankAction);
	this.getMenu().addAction("p", unnamedRegisterPasteAction);
	this.getMenu().addSubMenu("\"", selectRegMenu);
	ram.setSuperMenu(this.getMenu());
    }

    /*
    public String getName() {
	return "Display";
    }

    @Override
    public String getGUIelementName() {
	return "DISPLAY";
    }
    */

    public void paint(GraphicsContext gc, double x, double y) {
	super.paint(gc, x, y);
	/*
	 if (getRegister() != null) {
	 gc.strokeText(getRegister().getName().toString(), x, y + 20);
	 }
	 */
	gc.setFill(this.getColor1());

	Font f = gc.getFont();
	gc.setFont(new Font("default", this.getHeight()));
	gc.fillText(Float.toString(this.getValue()), x, y+this.getHeight());
	gc.setFont(f);
    }

    @Override
    public GUIelement makeCopy() {
	GUIDisplay cb = new GUIDisplay();
	this.copyPropertiesTo(cb);
	return cb;
    }
}
