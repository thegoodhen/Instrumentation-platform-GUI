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
	private HashMap<String, Integer> name2IdMap = new HashMap<>();

	private HashMap<Integer, String> id2NameMap = new HashMap<>();
	private HashMap<Integer, Property> id2PropertyMap = new HashMap<>();
	private HashMap<Property, Integer> property2idMap = new HashMap<>();
	private boolean focused;
	private boolean enabled = true;
	private boolean selected = false;
	private boolean visible = true;
	private boolean matchedLastSearch = true;
	private String name = "Generic GUI Element";
	private String uniqueName;

	public void addProperty(int id, String name, Property p) {
		name2IdMap.put(name, id);
		id2NameMap.put(id, name);
		id2PropertyMap.put(id, p);
		property2idMap.put(p, id);
	}

	public void addIntegerProperty(int id, String name, int value) {
		Property p = new IntegerProperty(value);
		this.addProperty(id, name, p);
	}

	public void addFloatProperty(int id, String name, float value) {
		Property p = new FloatProperty(value);
		this.addProperty(id, name, p);
	}

	public void addStringProperty(int id, String name, String value) {
		Property p = new StringProperty(value);
		this.addProperty(id, name, p);
	}

	public HashMap<String, Integer> getName2IdMap() {
		return name2IdMap;
	}

	public void setName2IdMap(HashMap<String, Integer> name2IdMap) {
		this.name2IdMap = name2IdMap;
	}

	public HashMap<Integer, String> getId2NameMap() {
		return id2NameMap;
	}

	public void setId2NameMap(HashMap<Integer, String> id2NameMap) {
		this.id2NameMap = id2NameMap;
	}

	public HashMap<Integer, Property> getId2PropertyMap() {
		return id2PropertyMap;
	}

	public void setId2PropertyMap(HashMap<Integer, Property> id2PropertyMap) {
		this.id2PropertyMap = id2PropertyMap;
	}

	public HashMap<Property, Integer> getProperty2idMap() {
		return property2idMap;
	}

	public void setProperty2idMap(HashMap<Property, Integer> property2idMap) {
		this.property2idMap = property2idMap;
	}

	public Property getPropertyById(int id) {
		return id2PropertyMap.get(id);
	}

	public Property getPropertyByName(String name) {
		int id = name2IdMap.get(name);
		return id2PropertyMap.get(id);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	private Register reg;
	private int value;

	public GUIelement() {
		this.initialize();
	}

	final void initialize() {
		this.id2NameMap = new HashMap<>();
		this.name2IdMap = new HashMap<>();
		this.property2idMap = new HashMap<>();
		this.id2PropertyMap = new HashMap<>();
		this.addFloatProperty(0, "Value", 0);
		this.addFloatProperty(1, "Max", 100);
		this.addFloatProperty(2, "Min", 0);
		this.addFloatProperty(3, "Step", 1);
		this.addStringProperty(4, "Name", "Generic gui element");
		this.addStringProperty(5, "UniqueName", "GUI_GENERIC");
		this.addIntegerProperty(6, "Color1", 0);
		this.addIntegerProperty(7, "Color2", 200);
		this.addIntegerProperty(8, "Color3", 400);
		this.addIntegerProperty(9, "Color4", 600);
		this.addIntegerProperty(10, "Focused", 0);
		this.addIntegerProperty(11, "Selected", 0);
		this.addIntegerProperty(11, "Selected", 0);
		this.addIntegerProperty(12, "Visible", 1);
		this.addIntegerProperty(13, "Highlighted", 0);
	}

	public GUIelement(GUITab gut) {
		initialize();
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
			if (this.getGUIPanel().getVFlag()) {
				gc.setFill(Color.YELLOW);
			}
			else
			{
				gc.setFill(Color.rgb(153,153,0));
			}
		}

		if (!isEnabled()) {
			gc.strokeLine(x - 15, y + 5, x - 5, y + 5);
		} else {
			if (isFocused()) {
				gc.setFill(Color.LIME);
			}
			gc.fillOval(x - 15, y, 10, 10);
		}

		gc.strokeText(getContextDependantName(), x + 100, y + 10);
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

	public String getContextDependantName()
	{
		if(this.getGUIPanel().showUniqueNames())
		{
			return this.getUniqueName();
		}
		else
		{
			return this.getName();
		}
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

	public void copyPropertiesTo(GUIelement ge) {
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

			Matcher matcher = pattern.matcher(this.getContextDependantName());
			boolean matched = matcher.find();
			if (RegexUtils.isMismatchOperation(setOperation)) {
				matched = !matched;
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

			Matcher matcher = pattern.matcher(this.getContextDependantName());
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
			if (RegexUtils.isAdditiveOperation(setOperation)) {
				this.setSelected(true);
			} else if (RegexUtils.isSubtractiveOperation(setOperation)) {
				this.setSelected(false);
			} else if (RegexUtils.isSettingOperation(setOperation)) {
				this.setSelected(true);
			}
		} else {
			if (RegexUtils.isSettingOperation(setOperation)) {
				this.setSelected(false);
			}

		}

	}

	void applyFilter(int setOperation) {
		if (matchedLastSearch) {
			if (RegexUtils.isAdditiveOperation(setOperation)) {
				this.setVisible(true);
			} else if (RegexUtils.isSubtractiveOperation(setOperation)) {
				this.setVisible(false);
			} else if (RegexUtils.isSettingOperation(setOperation)) {
				this.setVisible(true);
			}
		} else {
			if (RegexUtils.isSettingOperation(setOperation)) {
				this.setVisible(false);
			}

		}

	}
}
