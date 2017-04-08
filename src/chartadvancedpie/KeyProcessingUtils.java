/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import javafx.scene.input.KeyEvent;

/**
 *
 * @author thegoodhen
 */
public class KeyProcessingUtils {

    public static String createStringFromKeyEvent(KeyEvent ke) {
	String returnString = "";
	if (ke.getEventType() == KeyEvent.KEY_TYPED) {
	    if (!(ke.isAltDown() || ke.isControlDown())) {
		String chars = ke.getCharacter();
		if (!(chars.isEmpty())) {
		    returnString = chars;
		}
	    }
	} else {
	    if (ke.getEventType() == KeyEvent.KEY_PRESSED) {

		if (ke.isAltDown() || ke.isControlDown() || ke.isShiftDown())//modifier pressed
		{

		    String chars = ke.getText();
		    if (chars.isEmpty())//we're not dealing with a letter being typed
		    {
			String text = "" + ke.getCode();
			if (text.equals("CONTROL") || text.equals("ALT") || text.equals("SHIFT")) {//ignore mod keys, when they are alone
			    returnString = "";
			} else {
			    String slepice = ke.getCode().toString();
			    if (!ke.isControlDown() && !ke.isAltDown() && ke.isShiftDown() &&//only shift is pressed
				    ke.getCode().toString().length() == 1)//shift+one letter; for insance, when we press "H", two events are fired; "H" and shift+h. We need to throw away the shift+h one. The side effect is it's unfortunately impossible to use stuff like shift-1 on numeric keyboard as a command, but whatever.
			    {
				{
				    returnString = "";
				}
			    } else {
				returnString = "<" + (ke.isControlDown() ? "C-" : "") + (ke.isAltDown() ? "A-" : "") + (ke.isShiftDown() ? "S-" : "") + ke.getCode() + ">";
			    }
			}
		    } else {

			if (!ke.isControlDown() && !ke.isAltDown() && ke.isShiftDown())//only shift is pressed
			{
			    returnString = "";
			} else {
			    returnString = "<" + (ke.isControlDown() ? "C-" : "") + (ke.isAltDown() ? "A-" : "") + (ke.isShiftDown() ? "S-" : "") + ke.getText() + ">";
			}
		    }
		} else//no modifier pressed
		{
		    String chars = ke.getText();
		    if (chars.isEmpty())//we're not dealing with a letter being typed
		    {
			returnString = "<" + ke.getCode() + ">";
		    }
		}
	    }
	}
	return returnString;
    }

}
