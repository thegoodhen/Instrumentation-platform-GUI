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
 * Class for managing properties of all the different GUI elements...
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

    public String getHints(String source) {
	this.filterHints(source);

	StringBuilder sb = new StringBuilder();
	for (String s : hintList) {
	    sb.append(s).append("\n");

	}
	return sb.toString();
    }

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
