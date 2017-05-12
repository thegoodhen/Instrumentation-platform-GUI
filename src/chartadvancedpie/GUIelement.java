/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import shuntingyard.Token;

/**
 * Abstraction for the elements of the GUI interface, such as Sliders, Displays
 * or a Chart.
 *
 * @author thegoodhen
 */
public abstract class GUIelement {

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
    private boolean remoteControlled = false;

    private double min = Float.MIN_VALUE;

    private double max = Float.MAX_VALUE;

    private boolean matchedLastSearch = true;
    private int moduleGUIID = 0;//the ID assigned to the GUIelement by a module
    private String name = "Generic GUI Element";
    private String uniqueName;
    //private String tags = "";
    private static MappingManager globalElementMappingManager = null;
    private static MappingManager elementTypeMappingManager = null;
    private MappingManager thisInstanceMappingManager = null;
    private FloatPoint lastPositionDrawnTo = null;
    double dragStartMouseX;
    private double lastMousePressY;
    double dragStartMouseY;
    private Color col1 = Color.rgb(51, 77, 92);//Color.ALICEBLUE;
    private Color col2 = Color.rgb(223, 90, 73);//Color.BLACK;
    private Color col3 = Color.rgb(69, 178, 157);//Color.PURPLE;
    private Color col4 = Color.rgb(239, 201, 76);//Color.AQUA;
    long lastTimeRedrawn = 0;

    /**
     *
     * Returns the ID, assigned by the module during the initialization stage,
     * if this {@code GUIelement} was initialized by a module. Otherwise,
     * returns -1. Each {@code GUIelement} has 2 IDs - the one that was assigned
     * by this GUI application and one that was assigned by the module which
     * requested the creation of this GUIelement (if any). The ID, assigned by
     * the module is only used temporarily, to refer to the given GUI element in
     * the module-PC communication when initializing. Once initialized, the ID
     * assigned by the GUI app is used, even in the communication
     *
     * @return the ID, assigned by the module during the initialization stage,
     */
    public int getModuleGUIID() {
	return this.moduleGUIID;
    }

    /**
     * Used to set the ID, assigned by the module during the initialization
     * stage, if this {@code GUIelement} was initialized by a module.
     *
     * @param ID the module ID to set.
     * @see #getModuleGUIID()
     */
    public void setModuleGUIID(int ID) {
	this.moduleGUIID = ID;
    }

    /**
     * Register a new {@link Property} for this {@code GUIelement}. Once this
     * method is called, it is possible to set the value of this property and
     * retrieve it using the set[PropertyName] or get[PropertyName] CLUC
     * functions.
     *
     * The registration of this property is handled by correctly adjusting the
     * {@code name2IdMap}, {@code id2NameMap}, {@code property2idMap} and
     * {@code property2idMap} {@link HashMap}s.
     *
     * @param p the {@link Property} to add
     */
    public void addProperty(Property p) {
	name2IdMap.put(p.getName(), p.getId());
	id2NameMap.put(p.getId(), p.getName());
	id2PropertyMap.put(p.getId(), p);
	property2idMap.put(p, p.getId());
    }

    /*
     public void addProperty(Property p, GUIAction ga) {
     this.addProperty(p);
     this.callbackMap.put(p.getName(), ga);
     }
     */
    /**
     * Initialize a new Integer property and then add it.
     *
     * @param id the ID of this new Property, which will be used to refer to it
     * in the CLUC code, internally.
     * @param name the name of the new property
     * @param value the value to initialize the property with
     */
    public void addIntegerProperty(int id, String name, int value) {
	Property p = new IntegerProperty(id, name, value, this);
	this.addProperty(p);
    }

    /**
     * Initialize a new Float property and then add it.
     *
     * @param id the ID of this new Property, which will be used to refer to it
     * in the CLUC code, internally.
     * @param name the name of the new property
     * @param value the value to initialize the property with
     */
    public void addFloatProperty(int id, String name, float value) {
	Property p = new FloatProperty(id, name, value, this);
	this.addProperty(p);
    }

    /**
     * Initialize a new String property and then add it.
     *
     * @param id the ID of this new Property, which will be used to refer to it
     * in the CLUC code, internally.
     * @param name the name of the new property
     * @param value the value to initialize the property with
     */
    public void addStringProperty(int id, String name, String value) {
	Property p = new StringProperty(id, name, value, this);
	this.addProperty(p);
    }

    /**
     *
     * @deprecated !!!This method should not be used, or should be reimplemented
     * first!!! It doesn't fire the callbacks at all and replaces the
     * {@code Property} with a new one, which discards all callbacks and
     * negatively alters some other functionality. Use
     * {@code getPropertyByName(name).setValue(value)} instead.
     * @param name
     * @param value
     * @param undoable
     */
    public void setIntegerProperty(String name, int value, boolean undoable) {
	if (!undoable) {
	    ((IntegerProperty) (this.getPropertyByName(name))).setValue(value);
	} else {

	    Property oldP = this.getPropertyByName(name);
	    IntegerProperty ip = new IntegerProperty(oldP.getId(), oldP.getName(), value, this);
	    setPropertyAction spa = new setPropertyAction(ip);
	    spa.doActionWithHandling(this.gup);
	}
    }

    /**
     *
     * @deprecated !!!This method should not be used, or should be reimplemented
     * first!!! It doesn't fire the callbacks at all and replaces the
     * {@code Property} with a new one, which discards all callbacks and
     * negatively alters some other functionality. Use
     * {@code getPropertyByName(name).setValue(value)} instead.
     * @param name
     * @param value
     * @param undoable
     */
    public void setFloatProperty(String name, float value, boolean undoable) {
	if (!undoable) {
	    ((FloatProperty) (this.getPropertyByName(name))).setValue(value);
	} else {

	    Property oldP = this.getPropertyByName(name);
	    FloatProperty ip = new FloatProperty(oldP.getId(), oldP.getName(), value, this);
	    setPropertyAction spa = new setPropertyAction(ip);
	    spa.doActionWithHandling(this.gup);
	}
    }

    /**
     *
     * @deprecated !!!This method should not be used, or should be reimplemented
     * first!!! It doesn't fire the callbacks at all and replaces the
     * {@code Property} with a new one, which discards all callbacks and
     * negatively alters some other functionality. Use
     * {@code getPropertyByName(name).setValue(value)} instead.
     * @param name
     * @param value
     * @param undoable
     */
    public void setStringProperty(String name, String value, boolean undoable) {
	if (!undoable) {
	    ((StringProperty) (this.getPropertyByName(name))).setValue(value);
	} else {

	    Property oldP = this.getPropertyByName(name);
	    StringProperty ip = new StringProperty(oldP.getId(), oldP.getName(), value, this);
	    setPropertyAction spa = new setPropertyAction(ip);
	    spa.doActionWithHandling(this.gup);
	}
    }

    /**
     * Do some action, provided a String to parse as a sequence of key presses.
     * First, an attempt will be made to find an instance-specific mapping for
     * this element. If none was found, an element type mapping and a global
     * mapping will be searched for, in this order.
     *
     * If a mapping is found, the presses of the target sequence of this mapping
     * will be taken into account instead and according actions will be
     * performed. If no mapping was found, either nothing happens (when the
     * argument {@code runLiteralWhenNoMatch} is false) or the input String
     * sequence ({@code  eventText}) will be used to simulate the key presses.
     *
     * @param eventText
     * @param runLiteralWhenNoMatch
     * @return
     */
    public boolean notifyAboutKeyPress(String eventText, boolean runLiteralWhenNoMatch) {
	if (!(getThisInstanceMappingManager().notifyAboutKeyPress(eventText, false))) {
	    if (!(getElementTypeMappingManager().notifyAboutKeyPress(eventText, false))) {
		{

		    if (!(getGlobalElementMappingManager().notifyAboutKeyPress(eventText, runLiteralWhenNoMatch))) {
			return false;
		    }

		}
	    }
	}
	return true;
    }

    /**
     * Compile the bytecode for all CLUC events, that can be fired by this
     * {@code GUIelement}, including the getters and setters of the individual
     * {@link Property} objects.
     */
    public void recompileEvents() {
	for (Property p : this.property2idMap.keySet()) {
	    p.recompile();
	}
    }

    /**
     * Method used to react to a mouse scroll event provided.
     *
     * @param event the {@link ScrollEvent}
     */
    void sendMouseScroll(ScrollEvent event) {

    }

    /**
     * Method used to react to a mouse drag event provided.
     *
     * @param event the {@link MouseEvent}
     */
    void sendMouseDrag(MouseEvent event) {

    }

    /**
     * Method used to react to a mouse press event provided.
     *
     * @param event the {@link MouseEvent}
     */
    void sendMousePress(MouseEvent event) {

    }

    /**
     * Return whether or not the point defined by the coordinates provided is
     * within the bounds of this {@code GUIelement}. That is,
     *
     * {@code
     * return (x > fp.x && x < fp.x + this.getWidth() && y > fp.y && y < fp.y + this.getHeight());
     * }
     * where fp.x and fp.y are the coordinates the {@code GUIelement} appears on.
     *
     * @param x
     * @param y
     * @return whether or not the point defined by the coordinates provided is
     * within the bounds of this {@code GUIelement}
     */
    public boolean isWithinBounds(double x, double y) {
	FloatPoint fp = this.getLastPositionDrawnTo();
	return (x > fp.x && x < fp.x + this.getWidth() && y > fp.y && y < fp.y + this.getHeight());
    }

    /**
     * @return the minimum the {@code Value} {@link Property} can be set to.
     */
    public float getMin() {
	return (float) min;
    }

    /**
     * @return the maximum the {@code Value} {@link Property} can be set to.
     */
    public float getMax() {
	return (float) max;
    }

    @Deprecated
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

    /**
     * Set the {@link HashMap} that maps the names of {@link Property} objects to their
     * IDs, which are unique for each {@link Property}
     * @param name2IdMap 
     */
    public void setName2IdMap(HashMap<String, Integer> name2IdMap) {
	this.name2IdMap = name2IdMap;
    }

    /**
     * @return the {@link HashMap} that maps the IDs of {@link Property} objects to their
     * names, which are unique for each {@link Property}
     */
    public HashMap<Integer, String> getId2NameMap() {
	return id2NameMap;
    }

    /**
     * @return the first color, used to paint this GUI element. This color should be used for the background of this {@code GUIelement}.
     */
    public Color getColor1() {
	return col1;
    }

    /**
     * @return the second color, used to paint this GUI element. This color should be used for the foreground of this {@code GUIelement}.
     */
    public Color getColor2() {
	return col2;
    }

    /**
     * 
     * @return the 3rd color, used to paint this GUI element.
     */
    public Color getColor3() {
	return col3;
    }

    /**
     * 
     * @return the 4th color, used to paint this GUI element.
     */
    public Color getColor4() {
	return col4;
    }


    /**
     * Set the {@link HashMap} that maps the IDs of {@link Property} objects to their
     * names, which are unique for each {@link Property}
     * @param id2NameMap the new {@link HashMap}
     */
    public void setId2NameMap(HashMap<Integer, String> id2NameMap) {
	this.id2NameMap = id2NameMap;
    }

    /**
     * @return the {@link HashMap} that maps the IDs of {@link Property} objects to these
     * objects themselves.
     */
    public HashMap<Integer, Property> getId2PropertyMap() {
	return id2PropertyMap;
    }

    /**
     * Set the {@link HashMap} that maps the IDs of {@link Property} objects to these
     * objects themselves.
     * @param id2PropertyMap the new {@link HashMap}
     */
    public void setId2PropertyMap(HashMap<Integer, Property> id2PropertyMap) {
	this.id2PropertyMap = id2PropertyMap;
    }

    @Deprecated
    public HashMap<Property, Integer> getProperty2idMap() {
	return property2idMap;
    }

    /**
     * Set the {@link HashMap} that maps the  {@link Property} objects to
     * their respective unique integer IDs
     * @param property2idMap the new {@link HashMap}
     */
    public void setProperty2idMap(HashMap<Property, Integer> property2idMap) {
	this.property2idMap = property2idMap;
    }

    /**
     * Return the {@link Property} object, provided its unique ID.
     * This method is semantically equivalent with
     * {@code  getId2PropertyMap().get(id);}
     * @param id the id of the {@link Property}
     * @return the {@link Property} object, provided its unique ID.
     */
    public Property getPropertyById(int id) {
	return id2PropertyMap.get(id);
    }

    /**
     * Return the {@link Property} object, provided its unique name.
     * This method is semantically equivalent to
     * {@code  getId2PropertyMap.get(getName2IdMap.get(name));}
     * @param name the name of the {@link Property}
     * @return the {@link Property} object, provided its unique name.
     */
    public Property getPropertyByName(String name) {
	Integer id = name2IdMap.get(name);
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
	FloatProperty valp = new FloatProperty(0, "Value", 0.0F, this);

	valp.setSetterPropertyCallback(new PropertyCallback<Float>() {
	    @Override
	    public void run(Property<Float> p) {
		float theVal = p.getValueSilent();
		if (theVal < GUIelement.this.getMin()) {
		    p.setValueSilent(getMin());
		}

		if (theVal > GUIelement.this.getMax()) {
		    p.setValueSilent(getMax());
		}
	    }
	});

	this.addProperty(valp);

	//this.addFloatProperty(0, "Value", 0);
	FloatProperty maxp = new FloatProperty(1, "Max", 100F, this);

	maxp.setSetterPropertyCallback(new PropertyCallback<Float>() {
	    @Override
	    public void run(Property<Float> p) {
		max = p.getValueSilent();
	    }
	});
	FloatProperty minp = new FloatProperty(2, "Min", 0F, this);

	minp.setSetterPropertyCallback(new PropertyCallback<Float>() {
	    @Override
	    public void run(Property<Float> p) {
		min = p.getValueSilent();
	    }
	});
	this.addProperty(minp);
	this.addProperty(maxp);
	//this.addFloatProperty(1, "Max", 100);
	//this.addFloatProperty(2, "Min", 0);
	this.addFloatProperty(3, "Step", 1);
	//this.addStringProperty(4, "Name", "Generic gui element");

	StringProperty namep = new StringProperty(4, "Name", "Generic gui element", this);
	namep.setSetterPropertyCallback(new PropertyCallback<String>() {
	    @Override
	    public void run(Property<String> p) {
		System.out.println("ted se setlo jmeno na " + p.getValueSilent());
		name = p.getValueSilent();
	    }
	});
	this.addProperty(namep);

	this.addStringProperty(5, "UniqueName", "GUI_GENERIC");
	FloatProperty col1p = new FloatProperty(6, "Color1", 0.0F, this);

	col1p.setSetterPropertyCallback(new PropertyCallback<Float>() {
	    @Override
	    public void run(Property<Float> p) {
		GUIelement.this.col1 = ColorManager.get().colorFromFloat(p.getValueSilent());
	    }

	});

	col1p.setGetterPropertyCallback(new PropertyCallback<Float>() {
	    @Override
	    public void run(Property<Float> p) {
		p.setValueSilent(ColorManager.get().floatFromColor(col1));
	    }

	});
	this.addProperty(col1p);

	FloatProperty col2p = new FloatProperty(7, "Color2", 0.0F, this);

	col2p.setSetterPropertyCallback(new PropertyCallback<Float>() {
	    @Override
	    public void run(Property<Float> p) {
		System.out.println("KODAK BARVICKY UJI UJI");
		GUIelement.this.col2 = ColorManager.get().colorFromFloat(p.getValueSilent());
	    }

	});

	col2p.setGetterPropertyCallback(new PropertyCallback<Float>() {
	    @Override
	    public void run(Property<Float> p) {
		p.setValueSilent(ColorManager.get().floatFromColor(col2));
	    }

	});
	this.addProperty(col2p);

	FloatProperty col3p = new FloatProperty(8, "Color3", 0.0F, this);

	col3p.setSetterPropertyCallback(new PropertyCallback<Float>() {
	    @Override
	    public void run(Property<Float> p) {
		GUIelement.this.col3 = ColorManager.get().colorFromFloat(p.getValueSilent());
	    }

	});

	col3p.setGetterPropertyCallback(new PropertyCallback<Float>() {
	    @Override
	    public void run(Property<Float> p) {
		p.setValueSilent(ColorManager.get().floatFromColor(col3));
	    }

	});
	this.addProperty(col3p);

	FloatProperty col4p = new FloatProperty(9, "Color4", 0.0F, this);

	col4p.setSetterPropertyCallback(new PropertyCallback<Float>() {
	    @Override
	    public void run(Property<Float> p) {
		GUIelement.this.col4 = ColorManager.get().colorFromFloat(p.getValueSilent());
	    }

	});

	col4p.setGetterPropertyCallback(new PropertyCallback<Float>() {
	    @Override
	    public void run(Property<Float> p) {
		p.setValueSilent(ColorManager.get().floatFromColor(col4));
	    }

	});
	this.addProperty(col4p);

	/*
	 this.addIntegerProperty(6, "Color1", 0);
	 this.addIntegerProperty(7, "Color2", 200);
	 this.addIntegerProperty(8, "Color3", 400);
	 this.addIntegerProperty(9, "Color4", 600);
	 */
	this.addIntegerProperty(10, "Focused", 0);
	this.addIntegerProperty(11, "Selected", 0);
	this.addIntegerProperty(11, "Selected", 0);
	this.addIntegerProperty(12, "Visible", 1);
	this.addIntegerProperty(13, "Highlighted", 0);
	this.addIntegerProperty(14, "Width", 100);
	this.addIntegerProperty(15, "Height", 20);
	this.addStringProperty(16, "Tags", "");

	IntegerProperty pRemote = new IntegerProperty(17, "RemoteControlled", 0, this);

	pRemote.setSetterPropertyCallback(new PropertyCallback<Integer>() {
	    @Override
	    public void run(Property<Integer> p) {
		GUIelement.this.remoteControlled = (p.getValueSilent() == 1);
	    }

	});

	pRemote.setGetterPropertyCallback(new PropertyCallback<Integer>() {
	    @Override
	    public void run(Property<Integer> p) {
		//GUIelement.this.remoteControlled=(p.getValueSilent()==1);
		GUIelement.this.getPropertyByName("RemoteControlled").setValueSilent(remoteControlled ? 1 : 0);
	    }

	});

	this.addProperty(pRemote);

	this.lastPositionDrawnTo = new FloatPoint(0, 0);
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

    /**
     * Add an instance-specific mapping to this particular instance of
     * {@code GUIelement}. Whenever a sequence of keys that translates to the
     * {@code source} is pressed, it will get treated as if the {@code target}
     * sequence was pressed instead. This is ensured by using
     * {@link MappingManager}s internally.
     *
     * @param source the source String of the mapping
     * @param target the target String of the mapping
     */
    public void addMapping(String source, String target) {
	this.thisInstanceMappingManager.addMapping(source, target);
    }

    /**
     * Add an elementType-specific mapping to this particular type of
     * {@code GUIelement}. Whenever a sequence of keys that translates to the
     * {@code source} is pressed, it will get treated as if the {@code target}
     * sequence was pressed instead. This is ensured by using
     * {@link MappingManager}s internally. TODO: i
     *
     * @param source the source String of the mapping
     * @param target the target String of the mapping
     */
    public static void addElementTypeMapping(String source, String target) {
	elementTypeMappingManager.addMapping(source, target);
    }

    /**
     * Add an global mapping, applicable for all {@code GUIelement}s, when they
     * are being edited. Whenever a sequence of keys that translates to the
     * {@code source} is pressed, it will get treated as if the {@code target}
     * sequence was pressed instead. This is ensured by using
     * {@link MappingManager}s internally.
     *
     * @param source the source String of the mapping
     * @param target the target String of the mapping
     */
    public static void addGlobalElementMapping(String source, String target) {
	globalElementMappingManager.addMapping(source, target);
    }

    /**
     * Set the respective {@link GUITab} this {@code GUIelement} is on to the
     * one provided.
     *
     * @param gut
     */
    public void setGUITab(GUITab gut) {
	this.setGUIPanel(gut.getGUIPanel());
	this.gut = gut;
    }

    /**
     *
     * @return the {@link Menu}, which will get displayed whenever this
     * {@code GUIelement} is being edited.
     */
    public Menu getMenu() {
	return this.menu;
    }

    /**
     *
     * Set the {@link Menu}, which will get displayed whenever this
     * {@code GUIelement} is being edited.
     */
    public void setMenu(Menu m) {
	this.menu = m;
    }

    /*
     public void addAction(String s, GUIAbstractAction gaa) {
     actionMap.put(s, gaa);
     }
     */

    /*
     public void handleActions(KeyEvent ke) {
     GUIAbstractAction gaa = actionMap.get(ke.getText());
     if (gaa != null) {
     gaa.doAction();
     }
     }
     */
    public void setEnabled(boolean enabled) {
	//SHOULD REFRESH
	this.enabled = enabled;
    }

    /**
     *
     * @return whether this {@code GUIelement} is focused
     */
    public boolean isFocused() {
	return focused;
    }

    /**
     * Focus this {@code GUIelement}
     */
    public void setFocused(boolean focused) {
	this.focused = focused;
    }

    /**
     * @return the last position, to which this {@code GUIelement} was drawn
     * (the upper left point). The position is expressed in pixels, relatively
     * to the {@link GUIPanel#canvasPane} it's being drawn to. The coordinate
     * system is consistent with JavaFX (left to right, down to up).
     */
    public FloatPoint getLastPositionDrawnTo() {
	return this.lastPositionDrawnTo;
    }

    /**
     * @deprecated @return a short description of this GUI element.
     */
    public String shortDesc() {
	return "Generic GUI element";
    }

    /**
     * Call the {@link GUITab} this {@code GUIelement} is placed on and request
     * it to repaint it sometime later. If this {@code GUIelement} has been
     * repainted recently (later than 30ms ago), do nothing (to prevent
     * excessive redrawing, which would be resource-heavy). This method uses the {@link Platform#runLater(java.lang.Runnable)
     * } Method, so it can be considered thread-safe. (TODO: can it tho?) It is
     * the preferred method for refreshing an element.
     */
    public void requestRepaint() {

	if (System.currentTimeMillis() > lastTimeRedrawn + 30) {//prevent the element from requesting a redraw too often
	    lastTimeRedrawn = System.currentTimeMillis();
	    Platform.runLater(() -> {
		if (GUIelement.this.isVisible()) {
		    GUIelement.this.gut.repaintElement(GUIelement.this);
		}
	    });
	}
    }

    /**
     * Paint this {@code GUIelement} on some place on the
     * {@link GraphicsContext} provided, on position, determined by the x and y
     * arguments.
     *
     * @param gc the {@link GraphicsContext} to draw to
     * @param x the x position to draw to
     * @param y the y position to draw to
     */
    public void paint(GraphicsContext gc, double x, double y) {
	this.lastPositionDrawnTo = new FloatPoint(x, y);
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

	gc.strokeText(getContextDependantName(), x + this.getWidth() + 10, y + 10);
	gc.setStroke(Color.CRIMSON);
	gc.strokeText(getTags(), x + this.getWidth() + 10, y + 20);
    }

    /**
     * Set the Value {@link Property} to a new number, firing the Java callbacks
     * as well as CLUC callbacks afterwards. Said callbacks might modify this
     * number, so subsequent call to {@link getValue} may not yield a number
     * identical to the one just set.
     *
     * @param value the number to set the Value Property to.
     */
    public void setValue(float value) {
	this.getPropertyByName("Value").setValue(value);
	//this.setFloatProperty("Value", value, true);
	//this.value = value;
    }

    /**
     * @return the number stored in the Value {@link Property}, firing the Java
     * callbacks as well as CLUC callbacks beforehand. Said callbacks might
     * modify this number, so this call to {@code getValue} may not yield a
     * number identical to the one just set by the {@link setValue} method.
     */
    public float getValue() {
	return ((FloatProperty) this.getPropertyByName("Value")).getValue(true, false);
    }

    /**
     *
     * @return the preferred height of this element in pixels.
     */
    public int getHeight() {
	return ((IntegerProperty) this.getPropertyByName("Height")).getValue();
    }

    /**
     *
     * @return the preferred width of this element in pixels.
     */
    public int getWidth() {
	return ((IntegerProperty) this.getPropertyByName("Width")).getValue();
    }

    /**
     * @return the user-readable name of this element.
     * @see #getUniqueName()
     */
    public String getName() {
	return name;
    }

    /**
     * Returns the UniqueName of this element. Only one element can have this
     * name. This name is used in the CLUC code to refer to this element. No
     * whitespace or characters other from capital letter, numbers and
     * underscores are allowed in this name; it cannot begin with a number.
     *
     * @return the UniqueName of this element.
     *
     */
    public String getUniqueName() {
	return uniqueName;
    }

    /**
     * Either returns the name of this element (same as calling {@link getName},
     * or the UniqueName (same as calling {@link getUniqueName}). Which one of
     * those it is depends on the current settings
     * ({@link GUIPanel#showUniqueNames()}).
     *
     * @return the name or unique name, based on
     * {@link GUIPanel#showUniqueNames()}.
     */
    public String getContextDependantName() {
	if (this.getGUIPanel().showUniqueNames()) {
	    return this.getUniqueName();
	} else {
	    return this.getName();
	}
    }

    /**
     * Return a string, representing all the tags stored in this element. Tag is
     * a letter, assigned to the element by the user; it can later be used to
     * perform a selection of elements with this letter or to otherwise access
     * it.
     *
     * @return
     */
    public String getTags() {
	return ((StringProperty) (this.getPropertyByName("Tags"))).getValue();
    }

    /**
     * Remove all the tags, assigned to this {@code GUIelement}
     * @see #getTags() 
     */
    public void removeAllTags() {
	this.setStringProperty("Tags", "", true);
    }

    /**
     * Add letter tags, one for each letter of the string provided, if this tag
     * is not already present for this element.
     *
     * @param t the string, containing the tags to add
     */
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

    /**
     * Remove all tags, letter of which matches one of the letters in the
     * provided string
     *
     * @param t the string, representing the tags, which should be removed.
     */
    public void removeTags(String t) {

	String tags = getTags();
	for (int i = 0; i < t.length(); i++) {
	    char c = t.charAt(i);
	    if (tags.indexOf(c) != -1)//contains char
	    {
		tags = tags.replaceAll("" + c, "");
	    }
	}
	this.setStringProperty("Tags", tags, true);
    }

    /**
     * Whether or not does the GUI element have ALL of the tags listed as
     * letters in the string t.
     *
     * @param t the string containing the possible tags
     * @return true if it cotains ALL of the tags listen in t, false otherwise
     */
    public boolean hasTag(String t) {
	for (int i = 0; i < t.length(); i++) {
	    char c = t.charAt(i);
	    String tags = getTags();
	    if (tags.indexOf(c) == -1)//contains char
	    {
		return false;
	    }
	}
	return true;
    }

    /**
     * 
     * @return the generic name of this type of {@code GUIelement}.
     */
    public String getGUIelementName() {
	return "GENERIC";
    }

    /**
     * Regenerate the UniqueName, based on the current user readable name and current tab.
     * @see #getUniqueName() 
     * @see #getName() 
     * @param gt the current {@link GUITab}
     */
    public void recalculateUniqueName(GUITab gt) {
	int i = 0;
	this.uniqueName = normalizeName(gt.getName() + " " + getName() + " " + getGUIelementName());
	this.uniqueName = normalizeName(getName());

	while (gt.getGUIPanel().GUINameMap.containsKey(this.uniqueName + i)) {
	    i++;
	}
	this.uniqueName += i;
    }

    /**
     * Normalize the human-readable name, by turning it to uppercase, replacing whitespaces
     * with underscores, and removing all characters differnt from capital letters, underscores and numbers.
     * @param name
     * @return the normalized name, formed from the human-readable name, by turning it to uppercase, replacing whitespaces
     * with underscores, and removing all characters differnt from capital letters, underscores and numbers.
     */
    private String normalizeName(String name) {
	String returnString = name;
	returnString = returnString.toUpperCase();
	returnString = returnString.replaceAll(" ", "_");
	returnString = returnString.replaceAll("[^A-Z]_[0-9]", "");
	return returnString;
    }

    /**
     * 
     * @return a deep copy of this {@code GUIelement}. All {@link Property} objects are also duplicated.
     * The callbacks are not preserved.
     * @see #copyPropertiesTo(chartadvancedpie.GUIelement) 
     */
    public GUIelement makeCopy() {
	Class<?> thisClass = this.getClass();
	System.out.println(thisClass.getName());

	for (Constructor<?> constructor : thisClass.getConstructors()) {
	    if (constructor.getParameterTypes().length == 1) {
		Class<?> paramType = constructor.getParameterTypes()[0];
		if (paramType.getCanonicalName().equals(GUITab.class.getCanonicalName())) {
		    try {
			GUIelement ge = (GUIelement) constructor.newInstance(this.gut);
			this.copyPropertiesTo(ge);
			return ge;
		    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			Logger.getLogger(GUIelement.class.getName()).log(Level.SEVERE, null, ex);
		    }
		}
	    }
	}
	return null;
    }

    /**
     * Copy all the {@link Property} objects of this {@code GUIelement} to the {@code GUIelement} provided.
     * This works by iterating through the properthies in this {@code GUIelement}, finding matching ones
     * by name in the target {@code GUIelement} and then transfering the values between the matching ones, using the
     * {@code Property.setValue(value,true,false)} calls. This means that {@code Property} objects that the source
     * {@code GUIelement} has, but the target lacks will not be transfered. Java callbacks are fired during this operation;
     * CLUC callbacks are surpressed.
     * @param ge 
     */
    public void copyPropertiesTo(GUIelement ge) {
	for (Property p : this.id2PropertyMap.values()) {
	    Property targetP = ge.getPropertyByName(p.getName());
	    if (targetP != null) {
		targetP.setValue(p.getValue(), true, false);
	    }
	    //ge.addProperty(p.makeCopy());
	}
    }

    /**
     * Set the user-readable name to the one provided.
     * @param name the new user-readable name
     */
    public void setName(String name) {
	this.name = name;
    }

    public void update() {
	if (this.getRegister() != null && this.getRegister().getValue() != null) {
	    this.getRegister().getValue().setValue(this.getValue());
	    this.getRegister().getValue().notifyAboutChangeFromGUI();
	}
    }

    /*
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
     */
    /**
     * Set the regular expression, used for previewing which elements would be affected if {@link #applySelection(int)}
     * or {@link #applyFilter} would get called right now by user pressing the enter key, whilst typing in the query search string.
     * This method should be called whenever the user presses a key when typing the query string,
     * @param regex the regex used
     * @param setOperation the set operation
     * @see #applyFilter(int) 
     * @see #applySelection(int) 
     */
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

    /**
     * If this {@code GUIelement} matched the last search, it applies this
     * search by manipulating, whether or not is this element selected by
     * accessing the {@code Selected} {@link Property}. The way the selection
     * will be manipulated depends on the {@code setOperation} provided, with
     * respect to the {@link RegexUtils#isSettingOperation(int)}, {@link RegexUtils#isAdditiveOperation(int) }
     * {@link RegexUtils#isSubtractiveOperation(int) }and {@link RegexUtils#isMismatchOperation(int)
     * }
     * methods.
     *
     * @param setOperation the type of operation, determining if the already
     * selecte components should be made unselected and more.
     * @see RegexUtils
     */
    void applySelection(int setOperation) {

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

    /**
     * If this {@code GUIelement} matched the last search, it applies this
     * search by manipulating the visibilty of this component by accessing the
     * {@code Visible} {@link Property}. The way the visibility will be
     * manipulated depends on the {@code setOperation} provided, with respect to
     * the {@link RegexUtils#isSettingOperation(int)}, {@link RegexUtils#isAdditiveOperation(int) }
     * {@link RegexUtils#isSubtractiveOperation(int) }and {@link RegexUtils#isMismatchOperation(int)
     * }
     * methods.
     *
     * @param setOperation the type of operation, determining if the already
     * visible components should be made invisible and more.
     * @see RegexUtils
     */
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
