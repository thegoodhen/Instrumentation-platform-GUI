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

				if (ke.isAltDown() || ke.isControlDown())//modifier pressed
				{

					String chars = ke.getText();
					if (chars.isEmpty())//we're not dealing with a letter being typed
					{
						String text = ""+ke.getCode();
						if (text.equals("CONTROL") || text.equals("ALT") || text.equals("SHIFT")) {
							returnString = "";
						} else {
							returnString = "<" + (ke.isControlDown() ? "C-" : "") + (ke.isAltDown() ? "A-" : "") + (ke.isShiftDown() ? "S-" : "") + ke.getCode() + ">";
						}
					} else {

						returnString = "<" + (ke.isControlDown() ? "C-" : "") + (ke.isAltDown() ? "A-" : "") + (ke.isShiftDown() ? "S-" : "") + ke.getText() + ">";
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
