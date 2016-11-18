/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

/**
 * Utility interface intended to keep the cluttering of already cluttered files to
 * a minimum by throwing the keyboard shortcut initialisation out of them,
 * into separate files.
 * @author thegoodhen
 */
public interface IKeyboardShortcutPreparer {
	public void prepareShortcuts(GUIKeyEventHandler gkeh);
	
}
