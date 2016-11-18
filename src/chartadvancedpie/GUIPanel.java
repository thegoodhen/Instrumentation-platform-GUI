/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.jmx.MXNodeAlgorithm;
import com.sun.javafx.jmx.MXNodeAlgorithmContext;
import com.sun.javafx.sg.prism.NGNode;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import shuntingyard.Token;

/**
 *
 * @author thegoodhen
 */
public class GUIPanel extends GUIelement implements IRepetitionCounter {

	Canvas canvas;
	VBox vb;
	TextField cmdLine;
	TextArea statusLine;
	private int selectedElementIndex = 0;
	ArrayList<GUIelement> GUIList = new ArrayList<>();
	ArrayList<GUITab> tabList = new ArrayList<>();
	private Menu currentMenu;
	private String repeatCountString = "";
	private GUITab currentGUITab;
	private int currentGUITabIndex = 0;
	private GUICompiler c;
	private GUIVirtualMachine vm;

	PanelKeyEventHandler pkeh;

	HashMap<String, GUIelement> quickMarkMap = new HashMap<>();
	HashMap<String, String> registerMap = new HashMap<>();
	HashMap<GUIelement, Integer> GUIIDMap = new HashMap<>();
	HashMap<Integer, GUIelement> ID2GUIMap = new HashMap<>();
	HashMap<String, GUIelement> GUINameMap = new HashMap<>();
	private int totalGUIelementCount = 0;
	private ArrayList<javafx.beans.value.ChangeListener> cmdLineListenerList = new ArrayList<>();
	private NamedGUIAction enterPressAction = new NamedGUIAction("do nothing");
	private String currentRegister = "%";
	public final static String UNNAMED_REGISTER = "%";

	public GUITab getCurrentGUITab()
	{
		return this.currentGUITab;
	}

	public int getGUIElementIDByName(String name)
	{
		if(name.equals("CGE"))//short for "current gui element"
		{
			return GUIIDMap.get(this.currentGUITab.getCurrentGUIElement());
		}
		else
		{
			GUIelement ge=GUINameMap.get(name);
			if(ge!=null)
			{
				return GUIIDMap.get(ge);
			}
			
		}
		return -1;
	}

	public GUICompiler getGUICompiler()
	{
		return this.c;
	}

	public float handleCallBack(ArrayList<Token> program)
	{
		this.vm.setProgram(program);
		vm.runProgram();
		return 0;
	}
	
	public void registerGUIelement(GUIelement ge) {
		ge.setGUIPanel(this);
		GUIIDMap.put(ge, totalGUIelementCount);
		ID2GUIMap.put(totalGUIelementCount, ge);
		GUINameMap.put(ge.getUniqueName(), ge);
		totalGUIelementCount++;
	}

	public int getRepeatCount() throws RuntimeException {
		if (repeatCountString.length() == 0) {
			return 1;
		}
		return Integer.parseInt(this.repeatCountString);
	}

	public String getRegisterContent(String registerName) {
		if (registerName.equals("%")) {

			final Clipboard clipboard = Clipboard.getSystemClipboard();
			final ClipboardContent cContent = new ClipboardContent();
			if (clipboard.getString() == null) {
				return "";
			}

			return clipboard.getString();
		}
		if (!registerMap.containsKey(registerName)) {
			return "";
		} else {
			return registerMap.get(registerName);
		}
	}

	public void setCurrentRegisterContentAndReset(String content) {
		setRegisterContent(this.currentRegister, content);
		this.currentRegister = UNNAMED_REGISTER;
	}

	public String getCurrentRegisterContentAndReset() {
		String returnString = getRegisterContent(this.currentRegister);
		this.currentRegister = UNNAMED_REGISTER;
		return returnString;
	}

	public void setRegisterContent(String registerName, String content) {
		if (registerName.charAt(0) >= 'a' && registerName.charAt(0) <= 'z')//change register
		{
			registerMap.put(registerName, content);
		} else if (registerName.charAt(0) >= 'A' && registerName.charAt(0) <= 'Z')//change register
		{
			registerMap.put(registerName.toLowerCase(), registerMap.get(registerName.toLowerCase()) + content);
		} else if (registerName.charAt(0) == '%')//unnamed register
		{
			registerMap.put(registerName, content);
			final Clipboard clipboard = Clipboard.getSystemClipboard();
			final ClipboardContent cContent = new ClipboardContent();
			cContent.putString(content);
			clipboard.setContent(cContent);
		}
	}

	protected void setRepeatCount(int repeatCount) {
		this.repeatCountString = Integer.toString(repeatCount);
	}

	public void resetRepeatCount() {
		this.repeatCountString = "";
	}

	void setMark(String mark) {
		quickMarkMap.put(mark, GUIList.get(selectedElementIndex));
	}

	void jumpToMark(String mark) {
		GUIelement targetElement = quickMarkMap.get(mark);
		if (targetElement != null) {
			GUIList.get(selectedElementIndex).setFocused(false);
			quickMarkMap.get(mark).setFocused(true);
		}
	}

	public void showText(String text) {
		if (this.statusLine != null) {
			this.statusLine.appendText(text);
			this.statusLine.end();
		}
	}

	public GUIPanel() {
		super(null);

		pkeh = new PanelKeyEventHandler();
		canvas = new Canvas(400, 200);
		vb = new VBox(8);
		vb.getChildren().add(canvas);
		cmdLine = new TextField("Slepice");
		statusLine = new TextArea("Kokodak");
		cmdLine.setStyle("-fx-text-inner-color: gray;");
		cmdLine.setOnKeyPressed(event -> pkeh.escapeKeyPressed(event.getCode(), null));
		this.enterPressAction = new NamedGUIAction("confirm") {
			public void doAction() {

			}
		};

		cmdLine.setOnKeyPressed(event -> pkeh.enterKeyPressed(event, null));
		//cmdLine.setPrefRowCount(1);

		//statusLine.setRotate(40);//wow, funky
		statusLine.setEditable(false);
		statusLine.setPrefRowCount(2);
		statusLine.setFocusTraversable(false);
		vb.getChildren().addAll(cmdLine, statusLine);
		canvas.setFocusTraversable(true);
		// Clear away portions as the user drags the mouse
		canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {

				//gc.clearRect(e.getX() - 2, e.getY() - 2, 5, 5);
			}
		});

		/*
		 canvas.addEventHandler(KeyEvent.KEY_PRESSED,
		 new EventHandler<KeyEvent>() {

		 @Override
		 public void handle(KeyEvent ke) {
		 //gs.paint(gc, 10, 10);
		 System.out.println(ke.getText());
		 //createLetter(ke.getText());
		 pkeh.handle(ke);
		 paintGUIelements();
		 ke.consume();
		 }
		 }
		 );
		 */
		canvas.addEventHandler(KeyEvent.KEY_TYPED,
			new EventHandler<KeyEvent>() {

				@Override
				public void handle(KeyEvent ke) {
					//gs.paint(gc, 10, 10);
					System.out.println(ke.getCharacter());
					//createLetter(ke.getText());
					pkeh.handle(ke);
					GraphicsContext gc = canvas.getGraphicsContext2D();
					currentGUITab.paintGUIelements();
					gc.setStroke(Color.GRAY);
					for (int i = 0; i < tabList.size(); i++) {
						if (i == currentGUITabIndex) {

							gc.setStroke(Color.WHITE);
						} else {

							gc.setStroke(Color.GRAY);
						}
						gc.strokeText(tabList.get(i).getName(), 60 * i, 10);
					}
					ke.consume();
				}
			});
		// Fill the Canvas with a Blue rectnagle when the user double-clicks
		canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent t) {
				if (t.getClickCount() > 1) {
					reset(canvas, Color.BLUE);
				}
			}
		});
		//actionMap.put("j", testAction);
		//actionMap.put("k", testAction2);

		GUITab gt = new GUITab(this, "tab1");
		GUITab gt2 = new GUITab(this, "tab2");
		this.addGUITab(gt);
		this.addGUITab(gt2);
		currentGUITab = gt2;
		GUISlider gs2 = new GUISlider(gt2);
		GUISlider gs3 = new GUISlider(gt2);
		GUISlider gs4 = new GUISlider(gt2);
		GUIDisplay gd=new GUIDisplay(gt);
		gt.addGUIelement(gd);
		gt2.addGUIelement(gs2);
		gt2.addGUIelement(gs3);
		gt2.addGUIelement(gs4);
		
		//gt.addGUIelement(gs4);

		String prog = "byte test(byte n)\nprintNumber(n);\nRETURN 0;\nENDFUNCTION\n";
		String prog2 = "byte test(byte n)\nprintNumber(n+10);\nRETURN 0;\nENDFUNCTION\n";

		String prog3="byte step()\nTAB1_GD_DISPLAY0.setValue(TAB1_GS_SLIDER4.getValue()+20);\nRETURN 0;\nENDFUNCTION\n";
		c = InterpreterFacade.prepareCompiler(this);
		c.compile(prog);
		c.compile(prog2);
		c.compile(prog3);
		vm = new GUIVirtualMachine(this);
		//vm.setProgram(c.getByteCodeAL());
		//vm.runProgram();

	}

	public Canvas getCanvas() {
		return canvas;
	}

	public VBox getVbox() {
		return vb;
	}

	/**
	 * Resets the canvas to its original look by filling in a rectangle
	 * covering its entire width and height. Color.BLUE is used in this
	 * demo.
	 *
	 * @param canvas The canvas to reset
	 * @param color The color to fill
	 */
	private void reset(Canvas canvas, Color color) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(color);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	protected void traverseTabs(boolean forward) {
		if (forward) {
			this.currentGUITabIndex++;
		} else {
			this.currentGUITabIndex--;
		}
		if (this.currentGUITabIndex < 0) {
			this.currentGUITabIndex = this.tabList.size() - 1;
		}
		if (this.currentGUITabIndex >= this.tabList.size()) {
			this.currentGUITabIndex = 0;
		}
		this.currentGUITab = this.tabList.get(this.currentGUITabIndex);
		System.out.println(this.currentGUITab.hashCode());
	}

	private void addCmdLineListener(javafx.beans.value.ChangeListener<String> listener) {
		this.cmdLine.textProperty().addListener(listener);
		this.cmdLineListenerList.add(listener);
	}

	private void resetCmdLineListeners() {
		for (javafx.beans.value.ChangeListener cl : this.cmdLineListenerList) {
			this.cmdLine.textProperty().removeListener(cl);
		}
		GUIPanel.this.enterPressAction = new NamedGUIAction("do nothing");
	}

	@Override
	public GUIelement makeCopy() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private class PanelKeyEventHandler implements GUIKeyEventHandler {

		Menu mainMenu;
		private boolean isRecordingAMacro = false;
		private HashMap<String, Macro> macroMap;
		private Macro currentMacro;
		private Menu startRecordingMacroMenu;
		private final RegisterSelectionMenu executeMacroMenu;
		private NamedGUIAction stopRecordingMacroAction;
		Menu yankMenu;
		Menu deleteMenu;
		Menu pasteMenu;

		@Override
		public Menu getMainMenu() {
			return mainMenu;
		}

		@Override
		public void setMainMenu(Menu m) {
			this.mainMenu=m;
		}

		@Override
		public GUIPanel getGUIPanel() {
			return GUIPanel.this;
		}

		private abstract class searchAction extends NamedGUIAction {

			private int setOperation = RegexUtils.SET_ON_MATCH;
			public searchAction(String name, int setOperation) {
				super(name);
				this.setOperation = setOperation;
			}

			public void doAction() {
				cmdLine.requestFocus();
				GUIPanel.this.addCmdLineListener((observable, oldValue, newValue) -> {
					performSearch(modifyInputString(newValue));
				});
				GUIPanel.this.enterPressAction = new NamedGUIAction("confirm query") {
					@Override
					public void doAction() {
						confirmSearchAction();
					}
				};
				System.out.println("searching");

			}

			public abstract String modifyInputString(String inputString);

			public void performSearch(String regex) {
				GUIPanel.this.currentGUITab.setPreviewRegex(regex, setOperation);
			}

			public void confirmSearchAction() {
				GUIPanel.this.currentGUITab.applySearch(setOperation);
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
				GUIPanel.this.currentGUITab.applyFilter(this.getSetOperation());
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

		public PanelKeyEventHandler() {
			macroMap = new HashMap<>();

			NamedGUIAction editComponentAction = new NamedGUIAction("edit component") {
				@Override
				public void doAction() {
					currentGUITab.editCurrentComponent();
				}
			};


		
			NamedGUIAction enterExMode = new NamedGUIAction("execute command") {
				@Override
				public void doAction() {
					//doAction(1);
					//GUIList.get(selectedElementIndex).setFocused(false);
					//selectedElementIndex = 0;//GUIList.size() - 1;
					//traverseElements(true);
				}

				@Override
				public void doAction(IRepetitionCounter irc) {
					cmdLine.requestFocus();

					GUIPanel.this.enterPressAction = new NamedGUIAction("Run command") {
						@Override
						public void doAction() {
							pkeh.runCommand(KeyCode.ENTER, null);
						}
					};
					//cmdLine.setOnKeyPressed(event -> pkeh.runCommand(event.getCode(), null));
				}
			};

			RegisterAction setMarkAction = new RegisterAction() {

				@Override
				public void doAction(String register) {
					if (register.charAt(0) >= 'a' && register.charAt(0) <= 'z')//local mark
					{
						GUIPanel.this.currentGUITab.setMark(register);
					}
					//GUIPanel.this.setMark(register);
				}

				@Override
				public void doAction(String register, IRepetitionCounter irc) {
					doAction(register);
				}
			};

			RegisterAction jumpToMarkAction = new RegisterAction() {

				@Override
				public void doAction(String register) {
					if (register.charAt(0) >= 'a' && register.charAt(0) <= 'z')//local mark
					{
						GUIPanel.this.currentGUITab.jumpToMark(register);
					}
					//GUIPanel.this.jumpToMark(register);
				}

				@Override
				public void doAction(String register, IRepetitionCounter irc) {
					doAction(register);
				}
			};

			RegisterAction startRecordingMacroAction = new RegisterAction() {

				@Override
				public void doAction(String register) {
					startRecordingMacro(register);
					PanelKeyEventHandler.this.mainMenu.addAction("q", PanelKeyEventHandler.this.stopRecordingMacroAction);
				}

				@Override
				public void doAction(String register, IRepetitionCounter irc) {
					doAction(register);
				}
			};

			stopRecordingMacroAction = new NamedGUIAction("stop recording macro") {

				@Override
				public void doAction() {
					stopRecordingMacro();
					PanelKeyEventHandler.this.mainMenu.addSubMenu("q", PanelKeyEventHandler.this.startRecordingMacroMenu);
				}
			};

			RegisterAction executeMacroAction = new RegisterAction() {

				@Override
				public void doAction(String register) {
					executeMacro(register);
				}

				@Override
				public void doAction(String register, IRepetitionCounter irc) {
					doAction(register);
				}
			};

			NamedGUIAction yankAction = new NamedGUIAction("yank current element") {
				@Override
				public void doAction() {
					GUIPanel.this.currentGUITab.copyGUIelement(GUIPanel.this.currentGUITab.getCurrentGUIElementIndex());
				}

				@Override
				public void doAction(IRepetitionCounter irc) {
					doAction();
				}

			};

			NamedGUIAction deleteAction = new NamedGUIAction("delete") {

				@Override
				public void doAction() {
					GUIPanel.this.currentGUITab.removeGUIelement(GUIPanel.this.currentGUITab.getCurrentGUIElementIndex());
					//GUIPanel.this.setMark(register);
				}

				@Override
				public void doAction(IRepetitionCounter irc) {
					doAction();
				}
			};

			NamedGUIAction elementPasteLinkAction = new NamedGUIAction("paste link (don't copy)") {

				@Override
				public void doAction() {
					GUIPanel.this.currentGUITab.insertGUIelement(GUIPanel.this.currentGUITab.getCurrentGUIElementIndex(),GUIPanel.this.getCurrentRegisterContentAndReset(),false);
					//GUIPanel.this.setMark(register);
				}

				@Override
				public void doAction(IRepetitionCounter irc) {
					doAction();
				}
			};


			NamedGUIAction elementPasteCopyAction = new NamedGUIAction("paste copy)") {

				@Override
				public void doAction() {
					GUIPanel.this.currentGUITab.insertGUIelement(GUIPanel.this.currentGUITab.getCurrentGUIElementIndex(),GUIPanel.this.getCurrentRegisterContentAndReset(),true);
					//GUIPanel.this.setMark(register);
				}

				@Override
				public void doAction(IRepetitionCounter irc) {
					doAction();
				}
			};

			//ram.addRegisterAction("p", pasteAction);
			Menu m = new Menu(GUIPanel.this, "Main menu", true);

			this.mainMenu = m;
			BasicMotionsKeyboardShortcutPreparer.prepareShortcuts(this);
			m.addAction(
				"/", new regexSearchAction("search using regex", RegexUtils.SET_ON_MATCH));
			m.addAction(
				":", enterExMode);
			m.addAction(
				"a", editComponentAction);


			/**
			 * Search related stuff below
			 */
			Menu searchMenu = new Menu(GUIPanel.this, "Search", false);
			Menu addToSearchMenu = new Menu(GUIPanel.this, "Add", false);
			Menu subtractFromSearchMenu = new Menu(GUIPanel.this, "subtract", false);

			searchMenu.addSubMenu("a", addToSearchMenu);
			searchMenu.addSubMenu("s", subtractFromSearchMenu);
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
				"s", searchMenu);

			/**
			 * Filtering related stuff below
			 */
			Menu filterMenu = new Menu(GUIPanel.this, "filter", false);

			Menu addToFilterMenu = new Menu(GUIPanel.this, "add", false);
			Menu subtractFromFilterMenu = new Menu(GUIPanel.this, "subtract", false);
			filterMenu.addSubMenu("a", addToFilterMenu);
			filterMenu.addSubMenu("s", subtractFromFilterMenu);
			filterMenu.addAction(
				"f", new fuzzyFilterAction("fuzzy filter", RegexUtils.SET_ON_MATCH));
			filterMenu.addAction(
				"r", new regexFilterAction("filter using regex", RegexUtils.SET_ON_MATCH));
			filterMenu.addAction(
				"l", new literalFilterAction("filter using a literal string", RegexUtils.SET_ON_MATCH));

			filterMenu.addAction(
				"F", new fuzzyFilterAction("filter on MISMATCH of fuzzy filter", RegexUtils.SET_ON_MISMATCH));
			filterMenu.addAction(
				"R", new regexFilterAction("filter on MISMATCH with regex", RegexUtils.SET_ON_MISMATCH));
			filterMenu.addAction(
				"L", new literalFilterAction("filter on MISMATCH with a literal string", RegexUtils.SET_ON_MISMATCH));

			addToFilterMenu.addAction("f", new fuzzyFilterAction("add items matching fuzzy query", RegexUtils.ADD_ON_MATCH));
			addToFilterMenu.addAction("r", new regexFilterAction("add items matching regex query", RegexUtils.ADD_ON_MATCH));
			addToFilterMenu.addAction("l", new literalFilterAction("add items matching a literal string", RegexUtils.ADD_ON_MATCH));

			addToFilterMenu.addAction("F", new fuzzyFilterAction("add items NOT matching fuzzy query", RegexUtils.ADD_ON_MISMATCH));
			addToFilterMenu.addAction("R", new regexFilterAction("add items NOT matching regex query", RegexUtils.ADD_ON_MISMATCH));
			addToFilterMenu.addAction("L", new literalFilterAction("add items NOT matching a literal string", RegexUtils.ADD_ON_MISMATCH));

			subtractFromFilterMenu.addAction("f", new fuzzyFilterAction("remove items matching fuzzy query from filter", RegexUtils.SUBTRACT_ON_MATCH));
			subtractFromFilterMenu.addAction("r", new regexFilterAction("remove items matching regex query from filter", RegexUtils.SUBTRACT_ON_MATCH));
			subtractFromFilterMenu.addAction("l", new literalFilterAction("remove items matching a literal string from filter", RegexUtils.SUBTRACT_ON_MATCH));

			subtractFromFilterMenu.addAction("F", new fuzzyFilterAction("remove items NOT matching fuzzy query from filter", RegexUtils.SUBTRACT_ON_MISMATCH));
			subtractFromFilterMenu.addAction("R", new regexFilterAction("remove items NOT matching regex query from filter", RegexUtils.SUBTRACT_ON_MISMATCH));
			subtractFromFilterMenu.addAction("L", new literalFilterAction("remove items NOT matching a literal string from filter", RegexUtils.SUBTRACT_ON_MISMATCH));

			m.addSubMenu(
				"f", filterMenu);

			Menu setMarkMenu = new RegisterSelectionMenu(GUIPanel.this, "set mark", setMarkAction);
			Menu jumpToMarkMenu = new RegisterSelectionMenu(GUIPanel.this, "jump to mark", jumpToMarkAction);

			m.addSubMenu(
				"m", setMarkMenu);
			m.addSubMenu(
				"'", jumpToMarkMenu);

			startRecordingMacroMenu = new RegisterSelectionMenu(GUIPanel.this, "record macro", startRecordingMacroAction);

			executeMacroMenu = new RegisterSelectionMenu(GUIPanel.this, "execute macro", executeMacroAction);

			mainMenu.addSubMenu(
				"q", PanelKeyEventHandler.this.startRecordingMacroMenu);
			mainMenu.addSubMenu(
				"v", PanelKeyEventHandler.this.executeMacroMenu);

			yankMenu= new Menu(GUIPanel.this, "yank (copy)", false);
			yankMenu.addAction("e", yankAction);
			deleteMenu= new Menu(GUIPanel.this, "delete", false);
			deleteMenu.addAction("e", deleteAction);
			pasteMenu= new Menu(GUIPanel.this, "paste", false);
			pasteMenu.addAction("l", elementPasteLinkAction);
			pasteMenu.addAction("c", elementPasteCopyAction);
			m.addSubMenu("y", yankMenu);
			m.addSubMenu("d", deleteMenu);
			m.addSubMenu("p", pasteMenu);
			GUIPanel.this.setMenu(m);
		}

		private void startRecordingMacro(String register) {
			this.isRecordingAMacro = true;
			Macro m = new Macro();
			this.currentMacro = m;
			this.macroMap.put(register, m);
		}

		private void stopRecordingMacro() {
			this.isRecordingAMacro = false;
		}

		private void executeMacro(String register) {
			Macro m = this.macroMap.get(register);
			if (m != null) {
				System.out.println("executing macro");
				m.execute(this);
			}
		}

		public void escapeKeyPressed(KeyCode keyCode, Stage dialog) {
			if (keyCode == KeyCode.ESCAPE) {
				canvas.requestFocus();
				GUIPanel.this.resetCmdLineListeners();
				//cmdLine.setDisable(true);
			}
		}

		public void enterKeyPressed(KeyEvent keyEvent, Stage dialog) {
			if (keyEvent.getCode() == KeyCode.ENTER && !keyEvent.isShiftDown()) {
				canvas.requestFocus();
				GUIPanel.this.enterPressAction.doAction();
				GUIPanel.this.resetCmdLineListeners();
				//cmdLine.setDisable(true);
			}
		}

		public void handle(KeyEvent ke) {
			String eventText = ke.getCharacter();//ke.getText();
			if (isRecordingAMacro && eventText.equals("q")) {
				if (currentMacro != null) {
					currentMacro.addKeyEvent(ke);
				}
			}
			if (isDigit(eventText)) {
				System.out.println("pressed num: " + eventText);
				GUIPanel.this.repeatCountString += eventText;
				System.out.println("Current num: " + GUIPanel.this.repeatCountString);
			} else {

				GUIPanel.this.currentMenu.handle(eventText);
				//GUIPanel.this.resetRepeatCount(); //leave this to others!
			}
		}

		private boolean isDigit(String s) {
			return (s.length() == 1 && s.charAt(0) > 47 && s.charAt(0) < 58);
		}

		private void runCommand(KeyCode code, Object object) {
			if (code == KeyCode.ENTER) {
				//c=InterpreterFacade.prepareCompiler(GUIPanel.this);
				c.compile("printNumber(" + GUIPanel.this.cmdLine.getText() + ");\n");//added the printNumber for convenience, not sure if the best
				//vm=new GUIVirtualMachine(GUIPanel.this);//TODO: NO NO NO
				vm.setProgram(c.getByteCodeAL());
				vm.runProgram();
			}
		}
	}

	public void handleActions(KeyEvent ke) {
		GUIAbstractAction gaa = actionMap.get(ke.getText());
		/*
		 if (gaa != null) {
		 gaa.doAction();
		 }
		 else
		 {
		 getCurrentGUIElement().handleActions(ke);
		 }
		 */
		this.currentMenu.handle(ke.getText());
	}

	public void addGUIelement(GUIelement ge) {
		ge.setGUIPanel(this);
		GUIList.add(ge);
	}

	public void addGUITab(GUITab gt) {
		gt.setGUIPanel(this);
		tabList.add(gt);
	}

	public void traverseElements(boolean forward) {
		GUIList.get(selectedElementIndex).setFocused(false);
		if (forward) {
			do {
				selectedElementIndex++;
				if (selectedElementIndex >= GUIList.size()) {
					selectedElementIndex = 0;
				}
			} while (!GUIList.get(selectedElementIndex).isEnabled());
		} else {
			do {
				selectedElementIndex--;
				if (selectedElementIndex < 0) {
					selectedElementIndex = GUIList.size() - 1;
				}
			} while (!GUIList.get(selectedElementIndex).isEnabled());
		}

		GUIList.get(selectedElementIndex).setFocused(true);
	}

	public void addAction(String s, GUIAbstractAction gaa) {
		actionMap.put(s, gaa);
	}

	public Menu getMenu() {
		return this.currentMenu;
	}

	public void setMenu(Menu m) {
		this.currentMenu = m;
		m.showMenu();
	}

}
