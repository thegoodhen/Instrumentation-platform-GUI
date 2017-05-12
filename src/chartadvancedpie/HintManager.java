/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for generating autocompletion of the CLUC code.
 *
 * @author thegoodhen
 */
public class HintManager {

    private static ArrayList<GUIelement> elementList = new ArrayList<>();
    private static boolean setup = false;
    private static HintManager pm = null;
    private GUIPanel gp;
    private String lastSource = "KOKODAK";
    private String parsedName = "";//parsed name of the GUI element, functions of which we are accessing.
    ArrayList<String> hintList;
    final int GUI_NAME = 0;
    final int GUI_FUNCTION = 1;
    final int GUI_CALLBACK = 2;
    int currentMode = GUI_NAME;

    private HintManager(GUIPanel gp) {
	hintList = new ArrayList<>();
	this.gp = gp;
    }

    /**
     * Singleton getter
     * @param gp the {@link GUIPanel} to assign to this {@code HintManager}
     * @return 
     */
    public static HintManager get(GUIPanel gp) {

	if (pm == null) {
	    return new HintManager(gp);
	} else {
	    return pm;
	}
    }

    /**
     * Take the whole line and remove everything up to the thing we are now
     * trying to autocomplete.
     *
     * @param source
     * @return
     */
    private String stripSource(String source) {

	Pattern dotP = Pattern.compile(".*(?:^|[^A-Z_0-9])([A-Z_0-9]+)(\\.[A-Za-z0-9_]+)$");
	Matcher m1 = dotP.matcher(source);
	if (m1.find()) {
	   currentMode = GUI_FUNCTION;
	    this.parsedName = m1.group(1);
	    return m1.group(1) + m1.group(2);
	}
	    Pattern p = Pattern.compile(".*(?:^|[^A-Z_0-9])([A-Z_0-9]+)$");
	Matcher m = p.matcher(source);
	if (m.find()) {
	    currentMode = GUI_NAME;
	    return m.group(1);
	}
	return "";
    }

    /**
     * 
     * @param source the string typed by the user
     * @return newline-separated list of full autocompletion matches
     */
    public String getHints(String source) {
	this.filterHints(source);

	StringBuilder sb = new StringBuilder();
	for (String s : hintList) {
	    sb.append(s).append("\n");

	}
	return sb.toString();
    }

    /**
     * Return the longest text that can be appended to the one typed by the user (which is the argument provided).
     * 
     * This means that when the text already typed by the user isn't enough to uniquely determine the autocompletion,
     * this method will fill out the overlapping part of all autocompletion suggestions.
     * For instance, if the user types "SL" and both "SLIDER1" and "SLIDER2" would be valid autocompletition results,
     * this method would return the text to be appended, common for both of those results: "IDER".
     * @param source the string typed by the user which should be filled
     * @return the c
     */
    public String fillString(String source) {
	source = this.stripSource(source);
	filterHints(source);
	if (!hintList.isEmpty()) {
	    String firstString = hintList.get(0);
	    int numberCount = 0;
	    boolean breakout = false;
	    for (int i = source.length(); i < firstString.length(); i++) {

		for (String s : this.hintList) {
		    if (s.length() > i) {
			if (s.charAt(i) != firstString.charAt(i)) {
			    breakout = true;
			    break;
			}
		    } else {
			breakout = true;
			break;
		    }

		}
		if (breakout) {
		    numberCount = i - source.length();
		    break;
		}
	    }
	    if(!breakout)
	    {
		    numberCount = firstString.length() - source.length();
	    }

	    return firstString.substring(source.length(), source.length() + numberCount);
	}
	return "";
    }

    /**
     * Update all the the generated autocompletion hints if necessary; otherwise don't regenerate them
     * and just remove the ones no longer applicable.
     * @param source the source string the user has typed
     */
    private void filterHints(String source) {

	source = this.stripSource(source);
	if (!source.startsWith(lastSource) || (!lastSource.contains(".") && (source.contains(".")))) {//we didn't just type more letters to specify the query more
	    this.generateHints(source);
	}

	Iterator<String> iter = hintList.iterator();
	while (iter.hasNext()) {
	    if (!iter.next().startsWith(source)) {
		iter.remove();
	    }
	}
	lastSource = source;
    }

    /**
     * Internally update the list of hints, based on what the user has typed.
     * @param source the string typed by the user
     */
    public void generateHints(String source) {
	if (currentMode == GUI_FUNCTION) {
	    int id=this.gp.getGUIElementIDByName(this.parsedName);
	    GUIelement ge=gp.ID2GUIMap.get(id);
	    if (ge != null) {

		
		for (Property p : ge.getId2PropertyMap().values()) {
		    this.hintList.add(this.parsedName+".get"+p.getName());
		    this.hintList.add(this.parsedName+".set"+p.getName());
		}
	    }
	} else {
	    this.hintList.add("CGE");
	    for (GUIelement ge : gp.GUINameMap.values()) {
		this.hintList.add(ge.getUniqueName());
	    }
	}
    }

}
