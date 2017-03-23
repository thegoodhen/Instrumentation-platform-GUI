/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.ArrayList;

/**
 * Class for managing properties of all the different GUI elements...
 *
 * @author thegoodhen
 */
public class PropertyManager {

    private static ArrayList<GUIelement> elementList = new ArrayList<>();
    private static boolean setup = false;
    private static PropertyManager pm = null;

    private PropertyManager() {
	initialize();
    }

    public static PropertyManager get() {

	if (pm == null) {
	    return new PropertyManager();
	} else {
	    return pm;
	}
    }

    /**
     * This method needs to be called before anything else.
     */
    private void initialize() {
	if (setup == false) {
	    elementList.add(new GUISlider());
	    elementList.add(new GUIChart());
	    elementList.add(new GUIStatsDisplay());
	    elementList.add(new GUIPID());
	    setup = true;
	}
    }

    /**
     * Returns the type, with the first letter capitalised (for instance, "Int")
     * of the property, given its name.
     *
     * @param name
     * @return
     */
    public String getPropertyTypeString(String name) {
	//initialize();
	Property p = getProperty(name);
	if (p == null) {
	    return null;
	} else {
	    if (name.toLowerCase().contains("line")) {
		return "Line";
	    }
	    String typeString = p.getTypeName();
	    typeString = typeString.substring(0, 1).toUpperCase() + typeString.substring(1).toLowerCase();
	    return typeString;
	}
    }

    public Property getProperty(int id) {
	Property returnProperty = null;
	for (GUIelement ge : elementList) {
	    returnProperty = ge.getPropertyById(id);
	    if (returnProperty != null) {
		break;
	    }
	}
	return returnProperty;
    }

    public Property getProperty(String name) {
	Property returnProperty = null;
	for (GUIelement ge : elementList) {
	    returnProperty = ge.getPropertyByName(name);
	    if (returnProperty != null) {
		break;
	    }
	}
	return returnProperty;

    }

    public int getPropertyIDByName(String name) {
	Property p = this.getProperty(name);
	return p.getId();
	/*
	 Integer id = -1;

	 for (GUIelement ge : elementList) {
	 id = ge.getProperty2idMap().get(p);
	 if (id != null) {
	 break;
	 }
	 }
	 return id;

	 }
	 */

    }
    }
