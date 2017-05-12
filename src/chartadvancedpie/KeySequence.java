/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Utility class for handling sequences of key presses.
 * @author thegoodhen
 */
public class KeySequence {

    private String sequenceString = "";

    public KeySequence(String s) {
	this.sequenceString = s;
    }

    /**
     * Take the string of this sequence and split it into individual key presses.
     * Multiple modifier keys can be pressed for each keypress - "{@literal <C-a>}"
     * is a single keypress.
     * @return  {@link ArrayList} of strings, one for each keypress, in the order they appered in the String this object was initialized with.
     */
    public ArrayList<String> getIndividualKeyPresses() {
	boolean isInsideBrackets = false;
	String currentString = "";
	ArrayList<String> returnList = new ArrayList<>();
	for (int i = 0; i < sequenceString.length(); i++) {
	    char ch = sequenceString.charAt(i);
	    if (ch == '<') {
		currentString="<";
		isInsideBrackets = true;
	    } else if (ch == '>') {
		currentString += ch;
		returnList.add(currentString);
		isInsideBrackets = false;
	    }
	    else if (isInsideBrackets) {
		currentString += ch;
	    } else {
		currentString = "" + ch;
		returnList.add(currentString);
	    }
	}
	return returnList;
    }
    public void append(String s)
    {
	this.sequenceString+=s;
    }

    @Override
    /**
     * @return the String representing the sequence
     */
    public String toString()
    {
	return this.sequenceString;
    }

    /**
     * Take the string representing this sequence, split it into the individual key presses
     * and send it to the {@link GUIPanel} provided, using the {@link GUIPanel#handle(java.lang.String) } method.
     * @param gp the {@link GUIPanel} to send the keypresses to
     * @see GUIPanel#handle(java.lang.String) 
     */
    public void execute(GUIPanel gp)
    {
	ArrayList<String> tempList=this.getIndividualKeyPresses();
	for(String s:tempList)
	{
		gp.handle(s);
	}
    }

    @Override
    public boolean equals(Object o)
    {
	if(!(o instanceof KeySequence) )
	{
	    return false;
	}
	else
	{
	    return this.sequenceString.equals(o.toString());
	}
    }

    @Override
    public int hashCode() {
	int hash = 5;
	hash = 67 * hash + Objects.hashCode(this.sequenceString);
	return hash;
    }

}
