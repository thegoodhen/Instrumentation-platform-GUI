/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author thegoodhen
 */
public class MappingManager {

    private HashMap<KeySequence, KeySequence> mappingsMap = new HashMap<>();
    private KeySequence currentKeySequence = new KeySequence("");
    private GUIPanel gp;

    public MappingManager(GUIPanel gp) {
	this.gp = gp;
    }

    public void addMapping(String source, String target) {
	mappingsMap.put(new KeySequence(source), new KeySequence(target));
    }

    public void notifyAboutKeyPress(String keyString) {
	this.currentKeySequence.append(keyString);
	boolean someSequenceFound = false;
	for (Map.Entry<KeySequence, KeySequence> e : mappingsMap.entrySet()) {
	    KeySequence ks = e.getKey();
	    if (ks.toString().startsWith(currentKeySequence.toString()))//the current sequence is the start of this sequence
	    {
		if (ks.equals(currentKeySequence)) {
		    mappingsMap.get(ks).execute(gp);
		    currentKeySequence = new KeySequence("");
		    return;
		} else {
		    someSequenceFound = true;
		}
	    }
	}
	if (!someSequenceFound) {
	    currentKeySequence.execute(gp);
	    currentKeySequence = new KeySequence("");
	}

    }

}
