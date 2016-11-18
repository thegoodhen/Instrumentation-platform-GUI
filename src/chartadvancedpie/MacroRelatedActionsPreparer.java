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

		RegisterAction executeMacroAction = new RegisterAction() {

			@Override
			public void doAction(String register) {
				gp.executeMacro(register);
			}

			@Override
			public void doAction(String register, IRepetitionCounter irc) {
				doAction(register);
			}
		};

		RegisterAction startRecordingMacroAction = new RegisterAction() {

			@Override
			public void doAction(String register) {
				gp.startRecordingMacro(register);
				m.addAction("q", stopRecordingMacroAction);
			}

			@Override
			public void doAction(String register, IRepetitionCounter irc) {
				doAction(register);
			}
		};
		RegisterSelectionMenu startRecordingMacroMenu = new RegisterSelectionMenu(gp, "record macro", startRecordingMacroAction);

		stopRecordingMacroAction = new NamedGUIAction("stop recording macro") {

			@Override
			public void doAction() {
				gp.stopRecordingMacro();
				m.addSubMenu("q", startRecordingMacroMenu);
			}
		};

		RegisterSelectionMenu executeMacroMenu = new RegisterSelectionMenu(gp, "execute macro", executeMacroAction);

			m.addSubMenu(
				"q", startRecordingMacroMenu);
			m.addSubMenu(
				"v", executeMacroMenu);

	}

}
