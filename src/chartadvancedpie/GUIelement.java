/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 *
 * @author thegoodhen
 */
public abstract class GUIelement extends Container implements Subscriber {

	public static final int SLIDER = 0;
	public static final int READOUT = 1;
	public static final int CHECKBOX = 2;
	public static final int SELECTBOX = 3;
	private Menu menu;

	private GUIPanel gup;
	private GUITab gut;
	HashMap<String, GUIAbstractAction> actionMap = new HashMap<>();
	private boolean focused;
	private boolean enabled = true;
	private boolean selected = false;
	private boolean visible = true;
	private boolean matchedLastSearch = true;
	private String name = "Generic GUI Element";
	private String uniqueName;

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	private Register reg;
	private int value;

	public GUIelement() {

	}

	public GUIelement(GUITab gut) {
		this.gut = gut;
		if (gut != null) {
			recalculateUniqueName(gut);
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void setRegister(Register reg) {
		this.reg = reg;
	}

	public Register getRegister() {
		return this.reg;
	}

	public final GUIPanel getGUIPanel() {
		return gup;
	}

	/**
	 * This method sets the containing GUI Panel of the GUI element. Note
	 * that when this method is overriden, it is suggested to be the pointed
	 * where MENU and ACTION initializations happen, as MENUs need GUIPanel
	 * for their proper function.
	 *
	 * @param gup
	 */
	public void setGUIPanel(GUIPanel gup) {
		this.gup = gup;
	}

	public void setGUITab(GUITab gut) {
		this.setGUIPanel(gut.getGUIPanel());
		this.gut = gut;
	}

	public Menu getMenu() {
		return this.menu;
	}

	public void setMenu(Menu m) {
		this.menu = m;
	}

	@Override
	public void subscribeToAll() {
		for (Variable v : this.getVariableList().values()) {
			v.addSubscriber(this);
		}
		for (Variable v : this.getRegister().getVariableList().values()) {
			v.addSubscriber(this);
		}
	}

	@Override
	public void unsubscribeFromAll() {

		for (Variable v : this.getVariableList().values()) {
			v.removeSubscriber(this);
		}
		for (Variable v : this.getRegister().getVariableList().values()) {
			v.removeSubscriber(this);
		}
	}

	public void addAction(String s, GUIAbstractAction gaa) {
		actionMap.put(s, gaa);
	}

	public void handleActions(KeyEvent ke) {
		GUIAbstractAction gaa = actionMap.get(ke.getText());
		if (gaa != null) {
			gaa.doAction();
		}
	}

	public void setEnabled(boolean enabled) {
		//SHOULD REFRESH
		this.enabled = enabled;
	}

	public boolean isFocused() {
		return focused;
	}

	public void setFocused(boolean selected) {
		this.focused = selected;
	}

	public void paint(GraphicsContext gc, double x, double y) {
		gc.setFill(Color.BLUE);
		gc.setStroke(Color.WHITE);

		if (matchedLastSearch) {
			gc.setLineWidth(3);
			gc.setStroke(Color.RED);
			gc.strokeOval(x - 15, y, 10, 10);
			gc.setStroke(Color.WHITE);
			gc.setLineWidth(1);
		}

		if (isSelected()) {
			gc.setFill(Color.YELLOW);
		}

		if (!isEnabled()) {
			gc.strokeLine(x - 15, y + 5, x - 5, y + 5);
		} else {
			if (isFocused()) {
				gc.setFill(Color.LIME);
			}
			gc.fillOval(x - 15, y, 10, 10);
		}

		gc.strokeText(getUniqueName(), x + 100, y + 10);
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	public String getName() {
		return name;
	}

	public String getUniqueName() {
		return uniqueName;
	}

	public String getGUIelementName() {
		return "GENERIC";
	}

	public void recalculateUniqueName(GUITab gt) {
		int i = 0;
		this.uniqueName = normalizeName(gt.getName() + " " + getName() + " " + getGUIelementName());

		while (gt.getGUIPanel().GUINameMap.containsKey(this.uniqueName + i)) {
			i++;
		}
		this.uniqueName += i;
	}

	private String normalizeName(String name) {
		String returnString = name;
		returnString = returnString.toUpperCase();
		returnString = returnString.replaceAll(" ", "_");
		returnString = returnString.replaceAll("[^A-Z]_[0-9]", "");
		return returnString;
	}

public abstract GUIelement makeCopy();

public void copyPropertiesTo(GUIelement ge)
{
		ge.setEnabled(this.isEnabled());
		ge.setName(this.getName());
		ge.setValue(this.getValue());
}

	public void setName(String name) {
		this.name = name;
	}

	public void update() {
		if (this.getRegister() != null && this.getRegister().getValue() != null) {
			this.getRegister().getValue().setValue(this.getValue());
			this.getRegister().getValue().notifyAboutChangeFromGUI();
		}
	}

	void setFilterRegex(String regex) {

		try {
			Pattern pattern = Pattern.compile(regex);

			Matcher matcher = pattern.matcher(this.getName());
			if (matcher.find()) {
				this.setVisible(true);
			} else {
				this.setVisible(false);
			}
		} catch (Exception e) {

		}
	}

	void setPreviewRegex(String regex, int setOperation) {

		try {
			if ("".equals(regex)) {
				this.matchedLastSearch = false;//otherwise, empty query matches anything
				return;
			}
			Pattern pattern = Pattern.compile(regex);

			Matcher matcher = pattern.matcher(this.getName());
			boolean matched = matcher.find();
			if(RegexUtils.isMismatchOperation(setOperation))
			{
				matched=!matched;
			}
			if (matched) {
				this.matchedLastSearch = true;
			} else {
				this.matchedLastSearch = false;
			}
		} catch (Exception e) {

		}

	}

	void setSelectionRegex(String regex, int setOperation) {

		try {
			if ("".equals(regex)) {
				this.setSelected(false);//otherwise, empty query matches anything
				return;
			}
			Pattern pattern = Pattern.compile(regex);

			Matcher matcher = pattern.matcher(this.getName());
			boolean matched = matcher.find();
			if (RegexUtils.isMismatchOperation(setOperation)) {
				matched = !matched;
			}
			if (matched) {
				if (RegexUtils.isAdditiveOperation(setOperation)) {
					this.setSelected(true);
				}
			} else {
				if (RegexUtils.isSubtractiveOperation(setOperation)) {
					this.setSelected(false);
				}
			}
		} catch (Exception e) {

		}
	}

	void applySearch(int setOperation) {


		if (matchedLastSearch) {
			if(RegexUtils.isAdditiveOperation(setOperation))
			{
				this.setSelected(true);
			}
			else if(RegexUtils.isSubtractiveOperation(setOperation))
			{
				this.setSelected(false);
			}
			else if(RegexUtils.isSettingOperation(setOperation))
			{
				this.setSelected(true);
			}
		}
		else
		{
			if(RegexUtils.isSettingOperation(setOperation))
			{
				this.setSelected(false);
			}

		}

	}

	void applyFilter(int setOperation) {
		if (matchedLastSearch) {
			if(RegexUtils.isAdditiveOperation(setOperation))
			{
				this.setVisible(true);
			}
			else if(RegexUtils.isSubtractiveOperation(setOperation))
			{
				this.setVisible(false);
			}
			else if(RegexUtils.isSettingOperation(setOperation))
			{
				this.setVisible(true);
			}
		}
		else
		{
			if(RegexUtils.isSettingOperation(setOperation))
			{
				this.setVisible(false);
			}

		}


	}
}
