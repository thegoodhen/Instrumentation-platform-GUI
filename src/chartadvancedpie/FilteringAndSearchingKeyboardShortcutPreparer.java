/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.ArrayList;



/**
 * Utility class, responsible for the preparation of the keyboard shortcuts,
 * related to searching between GUI elements to select them, or for
 * filtering (hiding) them, when they match given criteria.
 * @author thegoodhen
 */
public class FilteringAndSearchingKeyboardShortcutPreparer implements IKeyboardShortcutPreparer {

    GUIPanel gp;

    private class selectAllAction extends NamedGUIAction {

	public selectAllAction(String name) {
	    super(name);
	}

	/**
	 * Unselect all if some are selected; select all if none are selected.
	 * There is a bug where if only one is selected, it selects all, but
	 * that's ok.
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

    private class invertSelectionAction extends NamedGUIAction {

	public invertSelectionAction(String name) {
	    super(name);
	}

	/**
	 * Unselect those that are selected, select unselected.
	 */
	@Override
	public void doAction() {
	    EditHistoryManager.get(gp).startUndoGroup();
	    for (GUIelement ge : gp.getCurrentGUITab().GUIList) {
		ge.setSelected(!ge.isSelected());
	    }

	    EditHistoryManager.get(gp).endUndoGroup();
	}

    }

    /**
     * Abstraction for actions, that will perform an operation
     * on GUI elements, that match a given string in some way.
     */
    private abstract class searchAction extends NamedGUIAction {

	private int setOperation = RegexUtils.SET_ON_MATCH;

	public searchAction(String name, int setOperation) {
	    super(name);
	    this.setOperation = setOperation;
	}

	/**
	 * Focuses the commandLine, prompting user to enter a string;
	 * ensures a live preview of the elements affected is provided as the user types this string.
	 * Once the user hits enter, takes the string which was typed into the
	 * commandLine and does some action with all the elements, matching
	 * the given string.
	 * 
	 */
	public void doAction() {
	    gp.getCmdLine().requestFocus();
	    gp.addCmdLineListener((observable, oldValue, newValue) -> {
		updateSearch(modifyInputString(newValue));
	    });
	    gp.enterPressAction = new NamedGUIAction("confirm query") {
		@Override
		public void doAction() {
		    confirmSearchAction();
		}
	    };
	    System.out.println("searching");

	}

	/**
	 * Method, which can optionally be used to modify the string, given
	 * by the user, before it gets passed for further processing.
	 * For example, the updateSearch method expects a regular expression, so if the user wants to
	 * enter a literal string, RegexUtils.makeRegexLiteral method can be used
	 * to modify this string, so it will be treated as a literal string.
	 * @see RegexUtils.makeRegexLiteral
	 * @see updateSearch
	 * @see confirmSearchAction
	 * @param inputString
	 * @return 
	 */
	public abstract String modifyInputString(String inputString);

	/**
	 * Callback method which should be called everytime the user types
	 * additional letter in the commandLine.
	 * @param regex the string written in the commandLine, treated as a regex;
	 * if it's not desirable to treat it as a regex, it has to be edited by the modifyInputString
	 * method, before it's passed to this method.
	 */
	public void updateSearch(String regex) {
	    gp.getCurrentGUITab().setPreviewRegex(regex, setOperation);
	}

	/**
	 * Method, which should be called when the user presses enter, confirming
	 * the text they entered in the commandLine (the so-called query string).
	 */
	public abstract void confirmSearchAction() ;

	/**
	 * Returns the set operation (as in, operation in set theory), 
	 * relevant to this searchAction.  
	 * In  other words, whether the elements that match this search should be
	 * added to the elements that matched the previous search, whether they
	 * should replace them, or something else. 
	 * @see LiteralSelectionAction
	 * @see RegexUtils
	 * @return 
	 */
	public int getSetOperation() {
	    return this.setOperation;
	}

    }




    /**
     * Abstract for actions, that prompt the user to enter some string,
     * and then use this string to select some GUI elements.
     */
    private abstract class selectionAction extends searchAction {

	public selectionAction(String name, int setOperation) {
	    super(name, setOperation);
	}

	@Override
	public void confirmSearchAction() {
	    EditHistoryManager.get(gp).startUndoGroup();
	    gp.getCurrentGUITab().applySelection(this.getSetOperation());
	    EditHistoryManager.get(gp).endUndoGroup();
	}

    }


    /**
     * Abstract for actions, that prompt the user to enter some string,
     * and then use this string to hide some GUI elements.
     */
    private abstract class filterAction extends searchAction {

	public filterAction(String name, int setOperation) {
	    super(name, setOperation);
	}

	@Override
	public void confirmSearchAction() {
	    EditHistoryManager.get(gp).startUndoGroup();
	    gp.getCurrentGUITab().applyFilter(this.getSetOperation());
	    EditHistoryManager.get(gp).endUndoGroup();
	}

    }

    /**
     * Class for selecting elements, that match a given regex.
     * @see selectionAction
     */
    private class RegexSelectionAction extends selectionAction {

	public RegexSelectionAction(String name, int setOperation) {
	    super(name, setOperation);
	}

	@Override
	public String modifyInputString(String inputString) {
	    return inputString;
	}
    }

    /**
     * Class for selecting elements, that match a given fuzzy string.
     * @see selectionAction
     */
    private class FuzzySelectionAction extends selectionAction {

	public FuzzySelectionAction(String name, int setOperation) {
	    super(name, setOperation);
	}

	@Override
	public String modifyInputString(String inputString) {
	    return RegexUtils.makeStringFuzzy(inputString);
	}
    }

    /**
     * Class for selecting elements, that match a given literal string.
     * @see selectionAction
     */
    private class LiteralSelectionAction extends selectionAction {

	public LiteralSelectionAction(String name, int setOperation) {
	    super(name, setOperation);
	}

	@Override
	public String modifyInputString(String inputString) {
	    return RegexUtils.makeRegexLiteral(inputString);
	}

    }

    /**
     * Class for filtering (showing and hiding) elements, that match a given regex.
     * @see selectionAction
     */
    private class RegexFilterAction extends filterAction {

	public RegexFilterAction(String name, int setOperation) {
	    super(name, setOperation);
	}

	@Override
	public String modifyInputString(String inputString) {
	    return inputString;
	}
    }

    /**
     * Class for filtering (showing and hiding) elements, that match a given fuzzy string.
     * @see selectionAction
     */
    private class FuzzyFilterAction extends filterAction {

	public FuzzyFilterAction(String name, int setOperation) {
	    super(name, setOperation);
	}

	@Override
	public String modifyInputString(String inputString) {
	    return RegexUtils.makeStringFuzzy(inputString);
	}
    }

    /**
     * Class for filtering (showing and hiding) elements, that match a given literal string.
     * @see selectionAction
     */
    private class LiteralFilterAction extends filterAction {

	public LiteralFilterAction(String name, int setOperation) {
	    super(name, setOperation);
	}

	@Override
	public String modifyInputString(String inputString) {
	    return RegexUtils.makeRegexLiteral(inputString);
	}

    }

    @Override
    public void prepareShortcuts(GUIKeyEventHandler gkeh) {
	Menu m = gkeh.getMainMenu();
	gp = gkeh.getGUIPanel();

	NamedGUIAction toggleFocusedElementSelectionAction = new NamedGUIAction("toggle selection on focused") {
	    @Override
	    public void doAction() {
		boolean toggleBool = gp.getCurrentGUITab().getFocusedGUIElement().isSelected();
		gp.getCurrentGUITab().getFocusedGUIElement().setSelected(!toggleBool);
	    }

	};

	NamedGUIAction hideFocusedElementAction = new NamedGUIAction("toggle selection on focused") {
	    @Override
	    public void doAction() {
		ArrayList<GUIelement> theList = gp.getSelectedGUIelementsList();
		for (GUIelement ge : theList) {
		    ge.setVisible(false);
		}
	    }
	};

	m.addAction("/", new RegexSelectionAction("search using regex", RegexUtils.SET_ON_MATCH));

	/**
	 * Search related stuff below
	 */
	Menu searchMenu = new Menu(gp, "Search", false);
	Menu addToSearchMenu = new Menu(gp, "Add", false);
	Menu subtractFromSearchMenu = new Menu(gp, "subtract", false);

	searchMenu.addAction("a", new selectAllAction("select all"));
	searchMenu.addAction("i", new invertSelectionAction("invert selection"));
	searchMenu.addAction("v", toggleFocusedElementSelectionAction);

	searchMenu.addSubMenu("+", addToSearchMenu);
	searchMenu.addSubMenu("-", subtractFromSearchMenu);
	searchMenu.addAction("f", new FuzzySelectionAction("fuzzy search", RegexUtils.SET_ON_MATCH));
	searchMenu.addAction("r", new RegexSelectionAction("search using regex", RegexUtils.SET_ON_MATCH));
	searchMenu.addAction("l", new LiteralSelectionAction("search for a literal string", RegexUtils.SET_ON_MATCH));

	searchMenu.addAction("F", new FuzzySelectionAction("MISMATCH fuzzy search", RegexUtils.SET_ON_MISMATCH));
	searchMenu.addAction("R", new RegexSelectionAction("MISMATCH search using regex", RegexUtils.SET_ON_MISMATCH));
	searchMenu.addAction("L", new LiteralSelectionAction("MISMATCH search for a literal string", RegexUtils.SET_ON_MISMATCH));

	addToSearchMenu.addAction("f", new FuzzySelectionAction("add items matching fuzzy query", RegexUtils.ADD_ON_MATCH));
	addToSearchMenu.addAction("r", new RegexSelectionAction("add items matching regex query", RegexUtils.ADD_ON_MATCH));
	addToSearchMenu.addAction("l", new LiteralSelectionAction("add items matching a literal string", RegexUtils.ADD_ON_MATCH));

	addToSearchMenu.addAction("F", new FuzzySelectionAction("add items NOT matching fuzzy query", RegexUtils.ADD_ON_MISMATCH));
	addToSearchMenu.addAction("R", new RegexSelectionAction("add items NOT matching regex query", RegexUtils.ADD_ON_MISMATCH));
	addToSearchMenu.addAction("L", new LiteralSelectionAction("add items NOT matching a literal string", RegexUtils.ADD_ON_MISMATCH));

	subtractFromSearchMenu.addAction("f", new FuzzySelectionAction("remove items matching fuzzy query from search", RegexUtils.SUBTRACT_ON_MATCH));
	subtractFromSearchMenu.addAction("r", new RegexSelectionAction("remove items matching regex query from search", RegexUtils.SUBTRACT_ON_MATCH));
	subtractFromSearchMenu.addAction("l", new LiteralSelectionAction("remove items matching a literal string from search", RegexUtils.SUBTRACT_ON_MATCH));

	subtractFromSearchMenu.addAction("F", new FuzzySelectionAction("remove items NOT matching fuzzy query from search", RegexUtils.SUBTRACT_ON_MISMATCH));
	subtractFromSearchMenu.addAction("R", new RegexSelectionAction("remove items NOT matching regex query from search", RegexUtils.SUBTRACT_ON_MISMATCH));
	subtractFromSearchMenu.addAction("L", new LiteralSelectionAction("remove items NOT matching a literal string from search", RegexUtils.SUBTRACT_ON_MISMATCH));

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
	filterMenu.addAction("s", new FuzzyFilterAction("fuzzy filter", RegexUtils.SET_ON_MATCH));
	filterMenu.addAction("r", new RegexFilterAction("filter using regex", RegexUtils.SET_ON_MATCH));
	filterMenu.addAction("l", new LiteralFilterAction("filter using a literal string", RegexUtils.SET_ON_MATCH));

	filterMenu.addAction("S", new FuzzyFilterAction("filter on MISMATCH of fuzzy filter", RegexUtils.SET_ON_MISMATCH));
	filterMenu.addAction("R", new RegexFilterAction("filter on MISMATCH with regex", RegexUtils.SET_ON_MISMATCH));
	filterMenu.addAction("L", new LiteralFilterAction("filter on MISMATCH with a literal string", RegexUtils.SET_ON_MISMATCH));

	addToFilterMenu.addAction("s", new FuzzyFilterAction("add items matching fuzzy query", RegexUtils.ADD_ON_MATCH));
	addToFilterMenu.addAction("r", new RegexFilterAction("add items matching regex query", RegexUtils.ADD_ON_MATCH));
	addToFilterMenu.addAction("l", new LiteralFilterAction("add items matching a literal string", RegexUtils.ADD_ON_MATCH));

	addToFilterMenu.addAction("S", new FuzzyFilterAction("add items NOT matching fuzzy query", RegexUtils.ADD_ON_MISMATCH));
	addToFilterMenu.addAction("R", new RegexFilterAction("add items NOT matching regex query", RegexUtils.ADD_ON_MISMATCH));
	addToFilterMenu.addAction("L", new LiteralFilterAction("add items NOT matching a literal string", RegexUtils.ADD_ON_MISMATCH));

	subtractFromFilterMenu.addAction("s", new FuzzyFilterAction("remove items matching fuzzy query from filter", RegexUtils.SUBTRACT_ON_MATCH));
	subtractFromFilterMenu.addAction("r", new RegexFilterAction("remove items matching regex query from filter", RegexUtils.SUBTRACT_ON_MATCH));
	subtractFromFilterMenu.addAction("l", new LiteralFilterAction("remove items matching a literal string from filter", RegexUtils.SUBTRACT_ON_MATCH));

	subtractFromFilterMenu.addAction("S", new FuzzyFilterAction("remove items NOT matching fuzzy query from filter", RegexUtils.SUBTRACT_ON_MISMATCH));
	subtractFromFilterMenu.addAction("R", new RegexFilterAction("remove items NOT matching regex query from filter", RegexUtils.SUBTRACT_ON_MISMATCH));
	subtractFromFilterMenu.addAction("L", new LiteralFilterAction("remove items NOT matching a literal string from filter", RegexUtils.SUBTRACT_ON_MISMATCH));

	m.addSubMenu(
		"f", filterMenu);
    }

}
