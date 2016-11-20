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
	protected NamedGUIAction enterPressAction = new NamedGUIAction("do nothing");
	private String currentRegister = "%";
	public final static String UNNAMED_REGISTER = "%";

	private HashMap<String, Macro> macroMap = new HashMap<>();

	private boolean isRecordingAMacro = false;
	private Macro currentMacro;

	private boolean vFlag = false;
	private boolean nFlag = false;
	private boolean lFlag = false;
	private boolean uniqueNames = false;

	private RegisterAction pickRegisterAction = new RegisterAction() {
		@Override
		public void doAction(String register) {
			System.out.println("weeee, nastavuju wegistw na " + register);
			setCurrentRegister(register);
		}

	};

	private RegisterSelectionMenu pickRegisterMenu = new RegisterSelectionMenu(this, "pick register", pickRegisterAction);

	public String getCurrentRegisterLetter() {
		return this.currentRegister;
	}

	public String getCurrentRegisterLetterAndReset() {
		String s = this.getCurrentRegisterLetter();
		this.currentRegister = UNNAMED_REGISTER;
		this.lFlag=false;
		return s;
	}

	protected void setCurrentRegister(String register) {
		this.currentRegister = register;
	}

	public Position getCurrentPosition() {
		Position returnPosition = new Position(this.currentGUITab, this.currentGUITab.getFocusedGUIElement());
		return returnPosition;
	}

	public void setCurrentPosition(Position p) {
		if (p != null) {
			this.setCurrentGUITab(p.getGUITab());
			this.getCurrentGUITab().focusGUIelement(p.getGUIElement());
		}
	}

	public void setCurrentGUITab(GUITab gt) {
		int position = tabList.indexOf(gt);
		boolean isValid = (gt != null && position != -1);
		if (isValid) {
			this.currentGUITabIndex = position;
			this.currentGUITab = gt;
		}

	}

	public GUITab getCurrentGUITab() {
		return this.currentGUITab;
	}

	public int getGUIElementIDByName(String name) {
		if (name.equals("CGE"))//short for "current gui element"
		{
			return GUIIDMap.get(this.currentGUITab.getFocusedGUIElement());
		} else {
			GUIelement ge = GUINameMap.get(name);
			if (ge != null) {
				return GUIIDMap.get(ge);
			}

		}
		return -1;
	}

	public GUICompiler getGUICompiler() {
		return this.c;
	}

	public float handleCallBack(ArrayList<Token> program) {
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
		return getRepeatCount(true);
	}

	public int getRepeatCount(boolean reset) {

		if (repeatCountString.length() == 0) {
			return 1;
		}
		if (reset) {
			this.resetRepeatCount();
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
		this.lFlag=false;
		this.currentRegister = UNNAMED_REGISTER;
	}

	public String getCurrentRegisterContentAndReset() {
		String returnString = getRegisterContent(this.currentRegister);
		this.currentRegister = UNNAMED_REGISTER;
		this.lFlag=false;
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
		this.nFlag = false;
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
		GUIDisplay gd = new GUIDisplay(gt);
		gt.addGUIelement(gd);
		gt2.addGUIelement(gs2);
		gt2.addGUIelement(gs3);
		gt2.addGUIelement(gs4);

		//gt.addGUIelement(gs4);
		String prog = "byte test(byte n)\nprintNumber(n);\nRETURN 0;\nENDFUNCTION\n";
		String prog2 = "byte test(byte n)\nprintNumber(n+10);\nRETURN 0;\nENDFUNCTION\n";

		String prog3 = "byte step()\nTAB1_GD_DISPLAY0.setValue(TAB1_GS_SLIDER4.getValue()+20);\nRETURN 0;\nENDFUNCTION\n";
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

	public void traverseTabs(int count) {
		boolean forward = true;
		if (count > 0) {
			forward = true;
			count -= 1;
		}
		if (count < 0) {
			forward = false;
			count += 1;
		}
		this.currentGUITabIndex += count;
		traverseTabs(forward);

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

	protected void addCmdLineListener(javafx.beans.value.ChangeListener<String> listener) {
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

	protected TextField getCmdLine() {
		return this.cmdLine;
	}

	public boolean showUniqueNames() {
		return uniqueNames;
	}

	public void setUniqueNames(boolean set) {
		this.uniqueNames = set;
	}

	private class PanelKeyEventHandler implements GUIKeyEventHandler {

		Menu mainMenu;

		@Override
		public Menu getMainMenu() {
			return mainMenu;
		}

		@Override
		public void setMainMenu(Menu m) {
			this.mainMenu = m;
		}

		@Override
		public GUIPanel getGUIPanel() {
			return GUIPanel.this;
		}

		public PanelKeyEventHandler() {

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
					cmdLine.requestFocus();

					GUIPanel.this.enterPressAction = new NamedGUIAction("Run command") {
						@Override
						public void doAction() {
							pkeh.runCommand(KeyCode.ENTER, null);
						}
					};
					//cmdLine.setOnKeyPressed(event -> pkeh.runCommand(event.getCode(), null));
				}

				@Override
				public void doAction(IRepetitionCounter irc) {
				}
			};

			//ram.addRegisterAction("p", pasteAction);
			Menu m = new Menu(GUIPanel.this, "Main menu", true);

			this.mainMenu = m;
			new BasicMotionsKeyboardShortcutPreparer().prepareShortcuts(this);
			new FilteringAndSearchingKeyboardShortcutPreparer().prepareShortcuts(this);
			new MarkMotionsKeyboardShortcutPreparer().prepareShortcuts(this);
			new RegisterRelatedGlobalActionsPreparer().prepareShortcuts(this);
			new MacroRelatedActionsPreparer().prepareShortcuts(this);
			new TagKeyboardShortcutPreparer().prepareShortcuts(this);
			new EditHistoryKeyboardShortcutPreparer().prepareShortcuts(this);

			m.addAction(
				":", enterExMode);
			m.addAction(
				"a", editComponentAction);

			GUIPanel.this.setMenu(m);
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
			if (eventText.equals("V")) {
				vFlag = !vFlag;
				System.out.println("V is now:" + (vFlag ? "on" : "off"));
			} else if (eventText.equals("\"")) {
				GUIPanel.this.pickRegisterMenu.setSuperMenu(GUIPanel.this.currentMenu);
				GUIPanel.this.currentMenu = (GUIPanel.this.pickRegisterMenu);
				lFlag=true;
			} else if (isDigit(eventText)) {
				System.out.println("pressed num: " + eventText);
				GUIPanel.this.repeatCountString += eventText;
				System.out.println("Current num: " + GUIPanel.this.repeatCountString);
				nFlag = true;
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

	public ArrayList<GUIelement> getSelectedGUIelementsList() {
		return getSelectedGUIelementsList(true);
	}

	public ArrayList<GUIelement> getSelectedGUIelementsList(boolean respectVFlag) {
		ArrayList<GUIelement> returnList = new ArrayList<>();
		if (respectVFlag) {
			if (vFlag) {
				returnList = this.getCurrentGUITab().getSelectedGUIelementsList();
			} else {
				returnList.add(this.getCurrentGUITab().getFocusedGUIElement());
			}
			vFlag = false;
		} else {
			returnList = this.getCurrentGUITab().getSelectedGUIelementsList();
		}
		return returnList;

	}

	public boolean getVFlag() {
		return this.vFlag;
	}

	public boolean getNFlag() {

		return this.nFlag;
	}

	public boolean getLFlag()
	{
		return this.lFlag;
	}

	protected void startRecordingMacro(String register) {
		this.isRecordingAMacro = true;
		Macro m = new Macro();
		this.currentMacro = m;
		this.macroMap.put(register, m);
	}

	protected void stopRecordingMacro() {
		this.isRecordingAMacro = false;
	}

	protected void executeMacro(String register) {
		Macro m = this.macroMap.get(register);
		if (m != null) {
			System.out.println("executing macro");
			m.execute(this.pkeh);
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
		 getFocusedGUIElement().handleActions(ke);
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
