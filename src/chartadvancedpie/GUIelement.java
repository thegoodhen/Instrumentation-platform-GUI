/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import shuntingyard.Token;

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
    private HashMap<String, GUIAction> callbackMap = new HashMap<>();
    private boolean focused;
    private boolean enabled = true;
    private boolean selected = false;
    private boolean visible = true;
    private boolean matchedLastSearch = true;
    private String name = "Generic GUI Element";
    private String uniqueName;
    //private String tags = "";
    private static MappingManager globalElementMappingManager = null;
    private static MappingManager elementTypeMappingManager = null;
    private MappingManager thisInstanceMappingManager = null;

    public void addProperty(Property p) {
	name2IdMap.put(p.getName(), p.getId());
	id2NameMap.put(p.getId(), p.getName());
	id2PropertyMap.put(p.getId(), p);
	property2idMap.put(p, p.getId());
    }

    public void addProperty(Property p, GUIAction ga) {
	this.addProperty(p);
	this.callbackMap.put(p.getName(), ga);
    }

    public void addIntegerProperty(int id, String name, int value) {
	Property p = new IntegerProperty(id, name, value);
	this.addProperty(p);
    }

    public void addFloatProperty(int id, String name, float value) {
	Property p = new FloatProperty(id, name, value);
	this.addProperty(p);
    }

    public void addStringProperty(int id, String name, String value) {
	Property p = new StringProperty(id, name, value);
	this.addProperty(p);
    }

    public void setIntegerProperty(String name, int value, boolean undoable) {
	if (!undoable) {
	    ((IntegerProperty) (this.getPropertyByName(name))).setValue(value);
	} else {

	    Property oldP = this.getPropertyByName(name);
	    IntegerProperty ip = new IntegerProperty(oldP.getId(), oldP.getName(), value);
	    setPropertyAction spa = new setPropertyAction(ip);
	    spa.doActionWithHandling(this.gup);
	}
    }

    public void setFloatProperty(String name, float value, boolean undoable) {
	if (!undoable) {
	    ((FloatProperty) (this.getPropertyByName(name))).setValue(value);
	} else {

	    Property oldP = this.getPropertyByName(name);
	    FloatProperty ip = new FloatProperty(oldP.getId(), oldP.getName(), value);
	    setPropertyAction spa = new setPropertyAction(ip);
	    spa.doActionWithHandling(this.gup);
	}
    }

    public void setStringProperty(String name, String value, boolean undoable) {
	if (!undoable) {
	    ((StringProperty) (this.getPropertyByName(name))).setValue(value);
	} else {

	    Property oldP = this.getPropertyByName(name);
	    StringProperty ip = new StringProperty(oldP.getId(), oldP.getName(), value);
	    setPropertyAction spa = new setPropertyAction(ip);
	    spa.doActionWithHandling(this.gup);
	}
    }

    public boolean notifyAboutKeyPress(String eventText, boolean b) {
	if (!(getThisInstanceMappingManager().notifyAboutKeyPress(eventText, false))) {
	    if (!(getElementTypeMappingManager().notifyAboutKeyPress(eventText, false))) {
		{

		    if (!(getGlobalElementMappingManager().notifyAboutKeyPress(eventText, b))) {
			return false;
		    }

		}
	    }
	}
	return true;
    }

    

    private class setPropertyAction extends EditAction {

	private Property originalProperty;
	private Property newProperty;

	public setPropertyAction(Property p) {
	    super("set " + p.getName());
	    this.newProperty = p;
	    this.originalProperty = GUIelement.this.getPropertyByName(p.getName());
	}

	@Override
	public void doAction() {
	    GUIelement.this.addProperty(newProperty);//overwrite the old one
	    GUIAction ge = GUIelement.this.callbackMap.get(newProperty.getName());
	    if (ge != null) {
		ge.doActionWithHandling(gup);
	    }
	}

	@Override
	public void undoAction() {
	    GUIelement.this.addProperty(originalProperty);//overwrite the old one
	}

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
	return ((IntegerProperty) this.getPropertyByName("Visible")).getValue() != 0;
    }

    public void setVisible(boolean visible) {
	value = visible ? 1 : 0;
	this.setIntegerProperty("Visible", value, true);
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
	this.addIntegerProperty(14, "Width", 100);
	this.addIntegerProperty(15, "Height", 20);
	this.addStringProperty(16, "Tags", "");
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
	return ((IntegerProperty) this.getPropertyByName("Selected")).getValue() != 0;
    }

    public void setSelected(boolean selected) {
	value = selected ? 1 : 0;
	this.setIntegerProperty("Selected", value, true);
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

    public static final MappingManager getGlobalElementMappingManager() {
	return globalElementMappingManager;
    }

    /**
     * Getter for the mapping manager for the specific type of GUI elements.
     * This should actually be overriden in every subclass! It's not possible to
     * make static members abstract.
     *
     * @return
     */
    public static MappingManager getElementTypeMappingManager() {
	return elementTypeMappingManager;
    }

    /**
     * Getter for the mapping manager of the specific instance of a GUI element.
     *
     * @return mapping manager of the specific instance of a GUI element.
     */
    public MappingManager getThisInstanceMappingManager() {
	return thisInstanceMappingManager;
    }

    /**
     * This method sets the containing GUI Panel of the GUI element. Note that
     * when this method is overriden, it is suggested to be the pointed where
     * MENU and ACTION initializations happen, as MENUs need GUIPanel for their
     * proper function.
     *
     * @param gup
     */
    public void setGUIPanel(GUIPanel gup) {
	this.gup = gup;
	if (globalElementMappingManager == null) {
	    globalElementMappingManager = new MappingManager(gup);
	}
	if (elementTypeMappingManager == null) {
	    elementTypeMappingManager = new MappingManager(gup);
	}
	thisInstanceMappingManager = new MappingManager(gup);
    }

    public void addMapping(String source, String target)
    {
	this.thisInstanceMappingManager.addMapping(source, target);
    }

    public static void addElementTypeMapping(String source, String target)
    {
	elementTypeMappingManager.addMapping(source, target);
    }


    public static void addGlobalElementMapping(String source, String target)
    {
	globalElementMappingManager.addMapping(source, target);
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
	    } else {
		gc.setFill(Color.rgb(153, 153, 0));
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

    public void setValue(float value) {
	this.setFloatProperty("Value", value, true);
	//this.value = value;
    }

    public float getValue() {
	return ((FloatProperty) this.getPropertyByName("Value")).getValue();
    }

    public int getHeight()
    {
	return ((IntegerProperty) this.getPropertyByName("Height")).getValue();
    }

    public int getWidth()
    {
	return ((IntegerProperty) this.getPropertyByName("Width")).getValue();
    }

    public String getName() {
	return name;
    }

    public String getUniqueName() {
	return uniqueName;
    }

    public String getContextDependantName() {
	if (this.getGUIPanel().showUniqueNames()) {
	    return this.getUniqueName();
	} else {
	    return this.getName();
	}
    }

    public String getTags() {
	return ((StringProperty) (this.getPropertyByName("Tags"))).getValue();
    }

    public void removeAllTags() {
	this.setStringProperty("Tags", "", true);
    }

    public void addTags(String t) {
	String tags = getTags();
	for (int i = 0; i < t.length(); i++) {
	    char c = t.charAt(i);
	    if (tags.indexOf(c) == -1)//doesn't contain char
	    {
		tags += c;
	    }
	}
	this.setStringProperty("Tags", tags, true);

    }

    public void removeTags(String t) {

	String tags = getTags();
	for (int i = 0; i < t.length(); i++) {
	    char c = t.charAt(i);
	    if (tags.indexOf(c) == -1)//doesn't contain char
	    {
		tags = tags.replaceAll("" + c, "");
	    }
	}
	this.setStringProperty("Tags", tags, true);
    }

    /**
     * Whether or not does the GUI element have AT LEAST one of the tags listed
     * as letters in the string t.
     *
     * @param t the string containing the possible tags
     * @return true if it cotains at least one of the tags listen in t, false
     * otherwise
     */
    public boolean hasTag(String t) {
	for (int i = 0; i < t.length(); i++) {
	    char c = t.charAt(i);
	    String tags = getTags();
	    if (tags.indexOf(c) != -1)//contains char
	    {
		return true;
	    }
	}
	return false;
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
