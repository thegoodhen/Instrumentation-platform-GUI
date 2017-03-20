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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
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
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
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

    //private HashMap<String, Macro> macroMap = new HashMap<>();
    private boolean isRecordingAMacro = false;
    private KeySequence currentMacro;
    private String currentMacroRegister = "";

    private boolean vFlag = false;
    private boolean nFlag = false;
    private boolean lFlag = false;
    private boolean uniqueNames = false;

    MappingManager globalMapManager = new MappingManager(this);
    private GUIelement currentlyEditedGUIelement = null;
    private final CanvasPane canvasPane;
    private String userCode;

    public void handle(String s) {
	this.pkeh.handle(s, false);
    }

    public GUIelement getCurrentlyEditedGUIelement() {
	return this.currentlyEditedGUIelement;
    }

    public void setCurrentlyEditedGUIelement(GUIelement ge) {
	this.currentlyEditedGUIelement = ge;
    }

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
	this.lFlag = false;
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

    public void recompileEventsForAll() {
	for (GUIelement ge : this.GUIIDMap.keySet()) {
	    ge.recompileEvents();
	}
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

    public GUIVirtualMachine GetGUIVirtualMachine() {
	return this.vm;
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

    /**
     * Sets the content on the current register (unnamed register, or a named
     * register if specified by the user); then resets the "current register" to
     * default (unnamed register).
     *
     * @param content
     */
    public void setCurrentRegisterContentAndReset(String content) {
	setRegisterContent(this.currentRegister, content);
	this.lFlag = false;
	this.currentRegister = UNNAMED_REGISTER;
    }

    /**
     * Returns the content on the current register (unnamed register, or a named
     * register if specified by the user); then resets the "current register" to
     * default (unnamed register).
     *
     * @return the content of current register
     */
    public String getCurrentRegisterContentAndReset() {
	String returnString = getRegisterContent(this.currentRegister);
	this.currentRegister = UNNAMED_REGISTER;
	this.lFlag = false;
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

    public CanvasPane getCanvasPane() {
	return this.canvasPane;
    }

    public GUIPanel() {
	super(null);

	pkeh = new PanelKeyEventHandler();
	canvasPane = new CanvasPane(400, 200);
	canvas = canvasPane.getCanvas();//new Canvas(400, 200);
	canvas.widthProperty().addListener(observable -> this.getCurrentGUITab().paintGUIelements());
	canvas.widthProperty().addListener(observable -> this.getCurrentGUITab().paintGUIelements());

	vb = new VBox(8);
	//vb.getChildren().add(canvas);
	cmdLine = new TextField("Slepice");
	statusLine = new TextArea("Kokodak") {
	    @Override
	    public void requestFocus() {

	    }
	};
	cmdLine.setStyle("-fx-text-inner-color: gray;");
	//cmdLine.setOnKeyPressed(event -> pkeh.escapeKeyPressed(event.getCode(), null));
	this.enterPressAction = new NamedGUIAction("confirm") {
	    public void doAction() {

	    }
	};

	cmdLine.setOnKeyPressed(event -> pkeh.keyPressed(event, null));
		//cmdLine.setPrefRowCount(1);

	//statusLine.setRotate(40);//wow, funky
	statusLine.setEditable(false);
	statusLine.setPrefRowCount(200);
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
	EventHandler<KeyEvent> theHandler = new EventHandler<KeyEvent>() {

	    @Override
	    public void handle(KeyEvent ke) {
		//gs.paint(gc, 10, 10);
				/*
		 System.out.println(ke.getCharacter());
		 System.out.println(ke.toString());
		 System.out.println(ke.getText());
		 */
		String eventString = KeyProcessingUtils.createStringFromKeyEvent(ke);
		System.out.println(eventString);
		//createLetter(ke.getText());
		if (!eventString.isEmpty()) {
		    pkeh.handle(eventString, true);
		}
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
	};
	canvas.addEventHandler(KeyEvent.KEY_TYPED,
		theHandler);
	canvas.addEventHandler(KeyEvent.KEY_PRESSED,
		theHandler);
	// Fill the Canvas with a Blue rectnagle when the user double-clicks
	canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent t) {
		GUIPanel.this.handleMousePress(t);
		if (t.getClickCount() > 1) {
		    reset(canvas, Color.BLUE);
		}
	    }
	});
	canvas.setOnScroll((ScrollEvent event) -> {
	    double deltaY = event.getDeltaY();
	    GUIPanel.this.getCurrentGUITab().sendMouseScroll(event);

	});
	canvas.setOnMouseDragged((MouseEvent event) -> {
	    GUIPanel.this.getCurrentGUITab().sendMouseDrag(event);

	});

	//actionMap.put("j", testAction);
	//actionMap.put("k", testAction2);
	GUITab gt = new GUITab(this, "tab1");
	GUITab gt2 = new GUITab(this, "tab2");
	this.addGUITab(gt);
	this.addGUITab(gt2);
	currentGUITab = gt2;

	c = InterpreterFacade.prepareCompiler(this);
	//String eventTest="byte EVENT_TAB1_GS_SLIDER0_SHeight()\nprintText(\"kokodak\");\nRETURN 0;\nENDFUNCTION\n";
	//String eventTest="byte slepice()\nprintText(\"kokonkdak\");\nCGE.setValue(30);\nRETURN 0;\nENDFUNCTION\n";
	//String eventTest="byte slepice()\nCGE.setValue(30);\nRETURN 0;\nENDFUNCTION\n";
	//String eventTest="byte slepice()\nTAB2_GS_SLIDER0.setValue(30);\nRETURN 0;\nENDFUNCTION\n";
	//String eventTest="byte slepice()\nprintText(\"kdak\");\nprintText(\"ptak\");\nRETURN 0;\nENDFUNCTION\n";
	//String eventTest="byte slepice()\nprintText(\"kodak\");\nprintNumber(setFloatProperty(findGE(\"CGE\"), 50, getPropertyIdByName(\"Value\")));\nprintText(\"kdak\");\nprintText(\"ptak\");\nRETURN 0;\nENDFUNCTION\n";
	//String eventTest="byte slepice()\nsetFloatProperty(findGE(\"CGE\"), 50, getPropertyIdByName(\"Value\"));\nRETURN 0;\nENDFUNCTION\n";
	//String eventTest = "printText(\"Koroptev\");\n";
	//String eventTest2 = "printText(\"Koroptev\");\n";
	String eventTest = "byte slepice()\nprintText(\"Koroptev\");\nRETURN 0;\nENDFUNCTION\n";
	String eventTest2 = "byte sklepice()\nprintText(\"Koroptev\");\nRETURN 0;\nENDFUNCTION\n";
	//String eventTest="byte slepice()\nprintText(\"Kokodak\");\nprintText(\"Kokodak\");\nRETURN 0;\nENDFUNCTION\n";
	//String eventTest="byte slepice()\nfindGE(\"CGE\");\nRETURN 0;\nENDFUNCTION\n";
	c.compile(eventTest);
	c.compile(eventTest2);

	GUISlider gs2 = new GUISlider(gt2);
	GUISlider gs3 = new GUISlider(gt2);
	GUISlider gs4 = new GUISlider(gt2);
	GUIDisplay gd = new GUIDisplay(gt);
	GUIStatsDisplay gsd=new GUIStatsDisplay(gt2);
	gt2.addGUIelement(gsd);
	gt.addGUIelement(gd);
	gt2.addGUIelement(gs2);
	gt2.addGUIelement(gs3);
	gt2.addGUIelement(gs4);

	//gt.addGUIelement(gs4);
	String prog = "byte test(byte n)\nprintNumber(n);\nRETURN 0;\nENDFUNCTION\n";
	String prog2 = "byte test(byte n)\nprintNumber(n);\nRETURN 0;\nENDFUNCTION\n";
	//String prog2 = "byte test(byte n)\nprintNumber(n+10);\nRETURN 0;\nENDFUNCTION\n";

	String prog3 = "byte step()\nTAB1_GS_SLIDER0.setValue(TAB2_GS_SLIDER0.getValue()+20);\nRETURN 0;\nENDFUNCTION\n";
	String prog4 = "byte TAB1_GS_SLIDER0_Value_S()\nprintNumber(15);\nRETURN 0;\nENDFUNCTION\n";
	String prog5 = "byte TAB2_GENERIC_GUI_ELEMENT_GENERIC0_Sample()\nprintText(\"kokodak\");\nTAB2_GENERIC_GUI_ELEMENT_GENERIC0.setLineY(\"a\",TAB2_GS_SLIDER0.getValue()*10);\nRETURN 0;\nENDFUNCTION\n";
	//String prog6 = "byte TAB1_GS_SLIDER3_Value_S()\nprintNumber(20);\nRETURN 0;\nENDFUNCTION\n";
	c.compile(prog);
	c.compile(prog2);
	c.compile(prog3);
	c.compile(prog4);
	c.compile(prog5);
	//c.compile(prog6);
	vm = new GUIVirtualMachine(this);
		//vm.setProgram(c.getByteCodeAL());
	//vm.runProgram();

	//String funcCall="slepice();\n";
	//c.compile(funcCall);
	//vm.setProgram(c.getByteCodeAL());
	//vm.runProgram();
	String userCode = "";
	this.globalMapManager.addMapping("j", "k");
	this.globalMapManager.addMapping("k", "j");
	this.globalMapManager.addMapping("=", ":CGE.setValue(50)<ENTER>");
	this.recompileEventsForAll();

	final Stage dialog = new Stage();
	dialog.initModality(Modality.APPLICATION_MODAL);
	//dialog.initOwner(primaryStage);
	VBox dialogVbox = new VBox(20);
	//dialogVbox.getChildren().add(new Text("This is a Dialog"));
	TextArea editorTextArea = new TextArea("kokodak");
	editorTextArea.setPrefRowCount(500);
	dialogVbox.getChildren().add(editorTextArea);
	Scene dialogScene = new Scene(dialogVbox, 500, 500);
	dialog.setScene(dialogScene);

	Button btn = new Button();
	btn.setText("Save and recompile");
	btn.setOnAction(
		new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent event) {
			GUIPanel.this.userCode = editorTextArea.getText();
			GUIPanel.this.recompileUserCode();
		    }
		});
	dialogVbox.getChildren().add(btn);
	dialog.show();
    }

    public void recompileUserCode() {
	c.compile(this.userCode);
	this.recompileEventsForAll();
    }

    private void handleMousePress(MouseEvent me) {
	if (currentGUITab != null) {
	    currentGUITab.sendMousePress(me);
	}
    }

    public Canvas getCanvas() {
	return canvas;
    }

    public VBox getVbox() {
	vb.setFillWidth(true);
	return vb;
    }

    /**
     * Resets the canvas to its original look by filling in a rectangle covering
     * its entire width and height. Color.BLUE is used in this demo.
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
	//GUIPanel.this.enterPressAction = new NamedGUIAction("do nothing");

	GUIPanel.this.enterPressAction = new NamedGUIAction("Run command") {
	    @Override
	    public void doAction() {
		pkeh.runCommand(KeyCode.ENTER, null);
	    }
	};
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
		    currentGUITab.editCurrentComponent(true);
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

	public void keyPressed(KeyEvent keyEvent, Stage dialog) {
	    if (keyEvent.getCode() == KeyCode.ESCAPE) {
		canvas.requestFocus();
		GUIPanel.this.resetCmdLineListeners();
	    }
	    if (keyEvent.getCode() == KeyCode.ENTER && !keyEvent.isShiftDown()) {
		sendEnterPressForCmdLine();
	    }
	}

	public void sendEnterPressForCmdLine() {
	    canvas.requestFocus();
	    GUIPanel.this.enterPressAction.doAction();
	    GUIPanel.this.resetCmdLineListeners();
	    if (isRecordingAMacro) {
		currentMacro.append(GUIPanel.this.getCmdLine().getText() + "<ENTER>");
	    }
	    //cmdLine.setDisable(true);

	}

	public void handle(String eventText, boolean respectMappings) {
	    if (GUIPanel.this.getCmdLine().isFocused()) {
		if (eventText.equals("<ENTER>")) {
		    sendEnterPressForCmdLine();
		    return;
		} else {
		    String currentText;
		    if (!GUIPanel.this.getCmdLine().getSelectedText().isEmpty()) {
			currentText = "";
		    } else {
			currentText = GUIPanel.this.getCmdLine().getText();
		    }
		    GUIPanel.this.getCmdLine().setText(currentText + eventText);
		}
	    } else {
		if (respectMappings) {
		    GUIelement cge = GUIPanel.this.getCurrentlyEditedGUIelement();
		    if (cge != null) {
			cge.notifyAboutKeyPress(eventText, true);
		    } else {
			GUIPanel.this.globalMapManager.notifyAboutKeyPress(eventText, true);
		    }
		} else {
		    handle(eventText);
		}
	    }
	}

	/**
	 * rename this!
	 *
	 * @param eventText
	 */
	public void handle(String eventText) {
	    //String eventText = ke.getCharacter();//ke.getText();

	    if (eventText.equals("<ESCAPE>")) {
		GUIPanel.this.currentMenu.close();
		return;
	    }

	    if (isRecordingAMacro && !eventText.equals("q")) {
		if (currentMacro != null) {
		    currentMacro.append(eventText);
		    //currentMacro.addKeyEvent(ke);
		}
	    }
	    if (eventText.equals("V")) {
		vFlag = !vFlag;
		System.out.println("V is now:" + (vFlag ? "on" : "off"));
	    } else if (eventText.equals("\"")) {
		GUIPanel.this.pickRegisterMenu.setSuperMenu(GUIPanel.this.currentMenu);
		GUIPanel.this.currentMenu = (GUIPanel.this.pickRegisterMenu);
		lFlag = true;
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
		if (!vFlag) {
		    c.compile("printNumber(" + GUIPanel.this.cmdLine.getText() + ");\n");//added the printNumber for convenience, not sure if the best
		    //vm=new GUIVirtualMachine(GUIPanel.this);//TODO: NO NO NO
		    vm.setProgram(c.getByteCodeAL());
		    vm.runProgram();
		} else {
		    GUIelement backupGE = this.getGUIPanel().getCurrentGUITab().getFocusedGUIElement();
		    for (GUIelement ge : this.getGUIPanel().getCurrentGUITab().getSelectedGUIelementsList()) {
			this.getGUIPanel().getCurrentGUITab().focusGUIelement(ge);

			c.compile(GUIPanel.this.cmdLine.getText()+";\n");
			vm.setProgram(c.getByteCodeAL());
			vm.runProgram();

		    }
		    this.getGUIPanel().getCurrentGUITab().focusGUIelement(backupGE);
		}
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

    public boolean getLFlag() {
	return this.lFlag;
    }

    protected void startRecordingMacro(String register) {
	this.isRecordingAMacro = true;
	this.currentMacroRegister = register;
	//Macro m = new Macro();
	this.currentMacro = new KeySequence("");
	//this.macroMap.put(register, m);
    }

    protected void stopRecordingMacro() {
	this.setRegisterContent(currentMacroRegister, currentMacro.toString());
	this.isRecordingAMacro = false;
    }

    protected void executeMacro(String sequenceOfKeys) {
	//Macro m = this.macroMap.get(register);
	KeySequence macro = new KeySequence(sequenceOfKeys);
	this.setMenu(pkeh.getMainMenu());
	System.out.println("executing macro");
	System.out.println(sequenceOfKeys);
	macro.execute(this);
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
	if (pkeh != null && m != null) {
	    if (m.equals(pkeh.getMainMenu())) {
		setCurrentlyEditedGUIelement(null);//when we end editing the component, we are
		//in the "normal mode"
	    }
	}
	this.currentMenu = m;
	m.showMenu();
    }

    private static class CanvasPane extends Pane {

	private final Canvas canvas;

	public CanvasPane(double width, double height) {
	    canvas = new Canvas(width, height);
	    getChildren().add(canvas);
	}

	public Canvas getCanvas() {
	    return canvas;
	}

	@Override
	protected void layoutChildren() {
	    final double x = snappedLeftInset();
	    final double y = snappedTopInset();
	    final double w = snapSize(getWidth()) - x - snappedRightInset();
	    final double h = snapSize(getHeight()) - y - snappedBottomInset();
	    canvas.setLayoutX(x);
	    canvas.setLayoutY(y);
	    canvas.setWidth(w);
	    canvas.setHeight(h);
	}
    }

}
