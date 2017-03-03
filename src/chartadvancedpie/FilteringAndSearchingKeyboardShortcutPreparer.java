/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.ArrayList;

/**
 *
 * @author thegoodhen
 */
public class FilteringAndSearchingKeyboardShortcutPreparer implements IKeyboardShortcutPreparer {

	GUIPanel gp;

	private class selectAllAction extends NamedGUIAction {

		public selectAllAction(String name) {
			super(name);
		}

		/**
		 * Unselect all if some are selected; select all if none are
		 * selected. There is a bug where if only one is selected, it
		 * selects all, but that's ok.
		 */
		public void doAction() {
			ArrayList<GUIelement> tempList = gp.getSelectedGUIelementsList(false);
			EditHistoryManager.get(gp).startUndoGroup();
			if (tempList.size() <= 1) {
				for (GUIelement ge : gp.getCurrentGUITab().GUIList) {
					ge.setSelected(true);
				}
			} else {
				for (GUIelement ge : gp.getCurrentGUITab().GUIList) {
					ge.setSelected(false);
				}

			}
			EditHistoryManager.get(gp).endUndoGroup();
		}

	}

	private abstract class searchAction extends NamedGUIAction {

		private int setOperation = RegexUtils.SET_ON_MATCH;

		public searchAction(String name, int setOperation) {
			super(name);
			this.setOperation = setOperation;
		}

		public void doAction() {
			gp.getCmdLine().requestFocus();
			gp.addCmdLineListener((observable, oldValue, newValue) -> {
				performSearch(modifyInputString(newValue));
			});
			gp.enterPressAction = new NamedGUIAction("confirm query") {
				@Override
				public void doAction() {
					confirmSearchAction();
				}
			};
			System.out.println("searching");

		}

		public abstract String modifyInputString(String inputString);

		public void performSearch(String regex) {
			gp.getCurrentGUITab().setPreviewRegex(regex, setOperation);
		}

		public void confirmSearchAction() {
			EditHistoryManager.get(gp).startUndoGroup();
			gp.getCurrentGUITab().applySearch(setOperation);
			EditHistoryManager.get(gp).endUndoGroup();
		}

		public int getSetOperation() {
			return this.setOperation;
		}

	}

	private abstract class filterAction extends searchAction {

		public filterAction(String name, int setOperation) {
			super(name, setOperation);
		}

		public void confirmSearchAction() {
			EditHistoryManager.get(gp).startUndoGroup();
			gp.getCurrentGUITab().applyFilter(this.getSetOperation());
			EditHistoryManager.get(gp).endUndoGroup();
		}

	}

	private class regexSearchAction extends searchAction {

		public regexSearchAction(String name, int setOperation) {
			super(name, setOperation);
		}

		@Override
		public String modifyInputString(String inputString) {
			return inputString;
		}
	}

	private class fuzzySearchAction extends searchAction {

		public fuzzySearchAction(String name, int setOperation) {
			super(name, setOperation);
		}

		@Override
		public String modifyInputString(String inputString) {
			return RegexUtils.makeStringFuzzy(inputString);
		}
	}

	private class literalSearchAction extends searchAction {

		public literalSearchAction(String name, int setOperation) {
			super(name, setOperation);
		}

		@Override
		public String modifyInputString(String inputString) {
			return RegexUtils.makeRegexLiteral(inputString);
		}

	}

	private class regexFilterAction extends filterAction {

		public regexFilterAction(String name, int setOperation) {
			super(name, setOperation);
		}

		@Override
		public String modifyInputString(String inputString) {
			return inputString;
		}
	}

	private class fuzzyFilterAction extends filterAction {

		public fuzzyFilterAction(String name, int setOperation) {
			super(name, setOperation);
		}

		@Override
		public String modifyInputString(String inputString) {
			return RegexUtils.makeStringFuzzy(inputString);
		}
	}

	private class literalFilterAction extends filterAction {

		public literalFilterAction(String name, int setOperation) {
			super(name, setOperation);
		}

		@Override
		public String modifyInputString(String inputString) {
			return RegexUtils.makeRegexLiteral(inputString);
		}

	}

	public void prepareShortcuts(GUIKeyEventHandler gkeh) {
		Menu m = gkeh.getMainMenu();
		gp = gkeh.getGUIPanel();

		NamedGUIAction toggleFocusedElementSelectionAction = new NamedGUIAction("toggle selection on focused") {
			@Override
			public void doAction() {
				boolean toggleBool= gp.getCurrentGUITab().getFocusedGUIElement().isSelected();
				gp.getCurrentGUITab().getFocusedGUIElement().setSelected(!toggleBool);
			}

		};


		NamedGUIAction hideFocusedElementAction = new NamedGUIAction("toggle selection on focused") {
			@Override
			public void doAction() {
				ArrayList<GUIelement> theList=gp.getSelectedGUIelementsList();
				for(GUIelement ge:theList)
				{
					ge.setVisible(false);
				}
			}
		};

		m.addAction("/", new regexSearchAction("search using regex", RegexUtils.SET_ON_MATCH));

		/**
		 * Search related stuff below
		 */
		Menu searchMenu = new Menu(gp, "Search", false);
		Menu addToSearchMenu = new Menu(gp, "Add", false);
		Menu subtractFromSearchMenu = new Menu(gp, "subtract", false);

		searchMenu.addAction("a", new selectAllAction("select all"));
		searchMenu.addAction("v", toggleFocusedElementSelectionAction);

		searchMenu.addSubMenu("+", addToSearchMenu);
		searchMenu.addSubMenu("-", subtractFromSearchMenu);
		searchMenu.addAction(
			"f", new fuzzySearchAction("fuzzy search", RegexUtils.SET_ON_MATCH));
		searchMenu.addAction(
			"r", new regexSearchAction("search using regex", RegexUtils.SET_ON_MATCH));
		searchMenu.addAction(
			"l", new literalSearchAction("search for a literal string", RegexUtils.SET_ON_MATCH));

		searchMenu.addAction(
			"F", new fuzzySearchAction("MISMATCH fuzzy search", RegexUtils.SET_ON_MISMATCH));
		searchMenu.addAction(
			"R", new regexSearchAction("MISMATCH search using regex", RegexUtils.SET_ON_MISMATCH));
		searchMenu.addAction(
			"L", new literalSearchAction("MISMATCH search for a literal string", RegexUtils.SET_ON_MISMATCH));

		addToSearchMenu.addAction("f", new fuzzySearchAction("add items matching fuzzy query", RegexUtils.ADD_ON_MATCH));
		addToSearchMenu.addAction("r", new regexSearchAction("add items matching regex query", RegexUtils.ADD_ON_MATCH));
		addToSearchMenu.addAction("l", new literalSearchAction("add items matching a literal string", RegexUtils.ADD_ON_MATCH));

		addToSearchMenu.addAction("F", new fuzzySearchAction("add items NOT matching fuzzy query", RegexUtils.ADD_ON_MISMATCH));
		addToSearchMenu.addAction("R", new regexSearchAction("add items NOT matching regex query", RegexUtils.ADD_ON_MISMATCH));
		addToSearchMenu.addAction("L", new literalSearchAction("add items NOT matching a literal string", RegexUtils.ADD_ON_MISMATCH));

		subtractFromSearchMenu.addAction("f", new fuzzySearchAction("remove items matching fuzzy query from search", RegexUtils.SUBTRACT_ON_MATCH));
		subtractFromSearchMenu.addAction("r", new regexSearchAction("remove items matching regex query from search", RegexUtils.SUBTRACT_ON_MATCH));
		subtractFromSearchMenu.addAction("l", new literalSearchAction("remove items matching a literal string from search", RegexUtils.SUBTRACT_ON_MATCH));

		subtractFromSearchMenu.addAction("F", new fuzzySearchAction("remove items NOT matching fuzzy query from search", RegexUtils.SUBTRACT_ON_MISMATCH));
		subtractFromSearchMenu.addAction("R", new regexSearchAction("remove items NOT matching regex query from search", RegexUtils.SUBTRACT_ON_MISMATCH));
		subtractFromSearchMenu.addAction("L", new literalSearchAction("remove items NOT matching a literal string from search", RegexUtils.SUBTRACT_ON_MISMATCH));

		m.addSubMenu(
			"v", searchMenu);

		/**
		 * Filtering related stuff below
		 */
		Menu filterMenu = new Menu(gp, "filter", false);

		Menu addToFilterMenu = new Menu(gp, "add", false);
		Menu subtractFromFilterMenu = new Menu(gp, "subtract", false);
		filterMenu.addSubMenu("+", addToFilterMenu);
		filterMenu.addSubMenu("-", subtractFromFilterMenu);
		filterMenu.addAction("f", hideFocusedElementAction);
		filterMenu.addAction(
			"s", new fuzzyFilterAction("fuzzy filter", RegexUtils.SET_ON_MATCH));
		filterMenu.addAction(
			"r", new regexFilterAction("filter using regex", RegexUtils.SET_ON_MATCH));
		filterMenu.addAction(
			"l", new literalFilterAction("filter using a literal string", RegexUtils.SET_ON_MATCH));

		filterMenu.addAction(
			"S", new fuzzyFilterAction("filter on MISMATCH of fuzzy filter", RegexUtils.SET_ON_MISMATCH));
		filterMenu.addAction(
			"R", new regexFilterAction("filter on MISMATCH with regex", RegexUtils.SET_ON_MISMATCH));
		filterMenu.addAction(
			"L", new literalFilterAction("filter on MISMATCH with a literal string", RegexUtils.SET_ON_MISMATCH));

		addToFilterMenu.addAction("s", new fuzzyFilterAction("add items matching fuzzy query", RegexUtils.ADD_ON_MATCH));
		addToFilterMenu.addAction("r", new regexFilterAction("add items matching regex query", RegexUtils.ADD_ON_MATCH));
		addToFilterMenu.addAction("l", new literalFilterAction("add items matching a literal string", RegexUtils.ADD_ON_MATCH));

		addToFilterMenu.addAction("S", new fuzzyFilterAction("add items NOT matching fuzzy query", RegexUtils.ADD_ON_MISMATCH));
		addToFilterMenu.addAction("R", new regexFilterAction("add items NOT matching regex query", RegexUtils.ADD_ON_MISMATCH));
		addToFilterMenu.addAction("L", new literalFilterAction("add items NOT matching a literal string", RegexUtils.ADD_ON_MISMATCH));

		subtractFromFilterMenu.addAction("s", new fuzzyFilterAction("remove items matching fuzzy query from filter", RegexUtils.SUBTRACT_ON_MATCH));
		subtractFromFilterMenu.addAction("r", new regexFilterAction("remove items matching regex query from filter", RegexUtils.SUBTRACT_ON_MATCH));
		subtractFromFilterMenu.addAction("l", new literalFilterAction("remove items matching a literal string from filter", RegexUtils.SUBTRACT_ON_MATCH));

		subtractFromFilterMenu.addAction("S", new fuzzyFilterAction("remove items NOT matching fuzzy query from filter", RegexUtils.SUBTRACT_ON_MISMATCH));
		subtractFromFilterMenu.addAction("R", new regexFilterAction("remove items NOT matching regex query from filter", RegexUtils.SUBTRACT_ON_MISMATCH));
		subtractFromFilterMenu.addAction("L", new literalFilterAction("remove items NOT matching a literal string from filter", RegexUtils.SUBTRACT_ON_MISMATCH));

		m.addSubMenu(
			"f", filterMenu);
	}

}
