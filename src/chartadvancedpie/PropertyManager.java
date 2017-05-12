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

    /**
     * Singleton pattern getter
     * @return the one an only instance of this class, that will ever be created.
     */
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
	    elementList.add(new GUITimer());
	    elementList.add(new GUINumericUpDown());
	    setup = true;
	}
    }

    /**
     * Returns the type, with the first letter capitalised (for instance, "Int")
     * of the property, given its name.
     *
     * @param name
     * @return the type, with the first letter capitalised (for instance, "Int")
     * of the property, given its name.
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

    /**
     * Return a {@link Property}, given its ID. This performs the search between all existing {@link GUIelement}s registered in the {@link initialize()} function.
     * Then returns the first {@link Property found}.
     * This method can be used to determine whether one of the existing {@link GUIelement}s even has a {@link Property} with this ID,
     * which type of element it is, and what is its name.
     * @param id the ID of the {@link Property} to search for
     * @return a {@link Property}, given its ID. 
     */
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

    /**
     * Return a {@link Property}, given its name. This performs the search between all existing {@link GUIelement}s registered in the {@link initialize()} function.
     * Then returns the first {@link Property found}.
     * This method can be used to determine whether one of the existing {@link GUIelement}s even has a {@link Property} with this name,
     * which type of element it is, and what is its ID.
     * @param name the name of the {@link Property} to search for
     * @return a {@link Property}, given its name. 
     */
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


    /**
     * Return a {@link Property} ID, given its name. This performs the search between all existing {@link GUIelement}s registered in the {@link initialize()} function.
     * Then returns the ID of the first matching {@link Property found}.
     * @param name the name of the {@link Property} to search for
     */
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
