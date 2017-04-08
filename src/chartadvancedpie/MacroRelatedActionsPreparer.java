/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

/**
 *
 * @author thegoodhen
 */
public class MacroRelatedActionsPreparer implements IKeyboardShortcutPreparer {

    NamedGUIAction stopRecordingMacroAction = new NamedGUIAction("kokodak");

    @Override
    public void prepareShortcuts(GUIKeyEventHandler gkeh) {

	GUIPanel gp = gkeh.getGUIPanel();
	Menu m = gkeh.getMainMenu();

	/*
	RegisterAction executeMacroAction = new RegisterAction() {

	    @Override
	    public void doAction(String register) {
		String macroText = gp.getRegisterContent(register);
		gp.executeMacro(macroText);
	    }

	    @Override
	    public void doAction(String register, IRepetitionCounter irc) {
		doAction(register);
	    }
	};
		*/

	NamedGUIAction executeMacroAction = new NamedGUIAction("execute macro")
	{
	    @Override
	    public void doAction()
	    {
		String macroText = gp.getCurrentRegisterContentAndReset();
		gp.executeMacro(macroText);
	    }
	};

	NamedGUIAction startRecordingMacroAction = new NamedGUIAction("record macro") {

	    @Override
	    public void doAction() {
		gp.startRecordingMacro(gp.getCurrentRegisterLetterAndReset());
		m.addAction("q", stopRecordingMacroAction);
	    }

	};
	//RegisterSelectionMenu startRecordingMacroMenu = new RegisterSelectionMenu(gp, "record macro", startRecordingMacroAction);

	stopRecordingMacroAction = new NamedGUIAction("stop recording macro") {

	    @Override
	    public void doAction() {
		gp.stopRecordingMacro();
		m.addAction("q", startRecordingMacroAction);
		//m.addSubMenu("q", startRecordingMacroMenu);
	    }
	};

	//RegisterSelectionMenu executeMacroMenu = new RegisterSelectionMenu(gp, "execute macro", executeMacroAction);

	//m.addSubMenu(
		//"q", startRecordingMacroMenu);
	m.addAction("q", startRecordingMacroAction);
	m.addAction("!", executeMacroAction);
	//m.addSubMenu(
		//"!", executeMacroMenu);

    }

}
