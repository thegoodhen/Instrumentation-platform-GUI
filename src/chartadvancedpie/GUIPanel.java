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
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import shuntingyard.CompilerException;
import shuntingyard.Token;

/**
 * A panel with tabs. Keeps the information about the individual GUI elements
 * and also handles the key presses. Also handles the individual tabs, on which
 * the different elements can be placed.
 *
 * @author thegoodhen
 */
public class GUIPanel implements IRepetitionCounter {

    Canvas canvas;
    VBox vb;
    TextArea cmdLine;
    TextArea infoTextArea;
    TextArea editorTextArea;

    private Label vFlagStatusBlimp;
    private Label modeStatusBlimp;
    private Label typedKeysBlimp;
    private Label repeatCountBlimp;
    private Label registerBlimp;
    private Label recordingStatusBlimp;
    private final Label errorStatusBlimp;

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

    private String currentCommandText = "";

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
    private SerialCommunicator sc;
    private HashMap<String, GUIAbstractAction> actionMap;

    /**
     * Parse the provided string as a sequence of key presses and perform
     * actions accordingly.
     *
     * @param s
     */
    public void handle(String s) {
	this.pkeh.handle(s, false);
    }

    /**
     * Return the global mapping manager object, responsible for managing
     * mappings, that are active when no components are being edited.
     *
     * @return the global mapping manager object, responsible for managing
     * mappings, that are active when no components are being edited.
     */
    public MappingManager getGlobalMappingManager() {
	return this.globalMapManager;
    }

    /**
     * Return the serial communicator object, used to communicate with the child
     * modules.
     *
     * @return the serial communicator object, used to communicate with the
     * child modules.
     */
    public SerialCommunicator getSerialCommunicator() {
	return this.sc;
    }

    /**
     * Set the serial communicator object, used to communicate with the child
     * modules.
     */
    public void setSerialCommunicator(SerialCommunicator sc) {
	this.sc = sc;
    }

    /**
     * Return the currently edited GUI element if any, null otherwise.
     *
     * @return the currently edited GUI element if any, null otherwise.
     */
    public GUIelement getCurrentlyEditedGUIelement() {
	return this.currentlyEditedGUIelement;
    }

    /**
     * Set the currently edited GUI element to be the one provided.
     */
    public void setCurrentlyEditedGUIelement(GUIelement ge) {
	this.currentlyEditedGUIelement = ge;
    }

    /**
     * Switch to edit mode and start editing the provided GUI element.
     */
    public void editElement(GUIelement ge) {
	this.currentlyEditedGUIelement = ge;
	ge.getMenu().setSuperMenu(getMenu());//so that escaping works correctly
	this.setMenu(ge.getMenu());
    }

    /**
     * Return to normal mode, halting the ongoing edits of a component.
     */
    public void stopEditing() {
	this.currentlyEditedGUIelement = null;
	this.setMenu(pkeh.getMainMenu());
    }

    /**
     * An action to change the register, which will be used to perform the
     * action, which will immediately follow.
     */
    private RegisterAction pickRegisterAction = new RegisterAction() {
	@Override
	public void doAction(String register) {
	    System.out.println("weeee, nastavuju wegistw na " + register);
	    setCurrentRegister(register);
	}

    };

    private RegisterSelectionMenu pickRegisterMenu = new RegisterSelectionMenu(this, "pick register", pickRegisterAction);

    /**
     * Return the letter of the currently selected register.
     *
     * @return the letter of the currently selected register.
     */
    public String getCurrentRegisterLetter() {
	return this.currentRegister;
    }

    /**
     * Return the letter of the currently selected register. Then reset this
     * letter to '%' (unnamed register).
     *
     * @return the letter of the currently selected register.
     */
    public String getCurrentRegisterLetterAndReset() {
	String s = this.getCurrentRegisterLetter();
	this.currentRegister = UNNAMED_REGISTER;
	this.lFlag = false;
	return s;
    }

    /**
     * Set the letter of the currently selected register to be the one provided.
     */
    protected void setCurrentRegister(String register) {
	this.currentRegister = register;
	if (!lFlag) {
	    this.registerBlimp.setText("(%)");
	} else {
	    this.registerBlimp.setText(register);
	}
    }

    /**
     * Return the Position object, representing which tab the user is currently
     * on, and which component is currently focused.
     *
     * @return the Position object, representing which tab the user is currently
     * on, and which component is currently focused.
     */
    public Position getCurrentPosition() {
	Position returnPosition = new Position(this.currentGUITab, this.currentGUITab.getFocusedGUIElement());
	return returnPosition;
    }

    /**
     * Set the Position object, representing which tab the user is currently on,
     * and which component is currently focused. This will change the current
     * tab and focus the given element on it, if possible. Otherwise, it has no
     * effect.
     */
    public void setCurrentPosition(Position p) {
	if (p != null) {
	    this.setCurrentGUITab(p.getGUITab());
	    this.getCurrentGUITab().focusGUIelement(p.getGUIElement());
	}
    }

    /**
     * Set the current GUI tab to the one specified. This will switch tabs. Has
     * no effect, if the GUI tab provided is null, or if no such tab exists in
     * the list of GUItabs, stored in the tabList field of this class.
     *
     */
    public void setCurrentGUITab(GUITab gt) {
	int position = tabList.indexOf(gt);
	boolean isValid = (gt != null && position != -1);
	if (isValid) {
	    this.currentGUITabIndex = position;
	    this.currentGUITab = gt;
	}

    }

    /**
     * Returns the GUITab object, representing the tab the user is currently
     * viewing.
     *
     * @return the GUITab object, representing the tab the user is currently
     * viewing.
     */
    public GUITab getCurrentGUITab() {
	return this.currentGUITab;
    }

    /**
     * Instruct all components to recompile the bytecode of their callbacks.
     * This function should automatically be called whenever the user code
     * changes.
     */
    public void recompileEventsForAll() {
	for (GUIelement ge : this.GUIIDMap.keySet()) {
	    ge.recompileEvents();
	}
    }

    /**
     * Returns the ID of the GUI element with the UNIQUENAME provided
     *
     * @param name
     * @return the ID of the GUI element with the UNIQUENAME provided
     * @see addGUIelement
     */
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

    /**
     * Get the compiler object, used by this panel to run user code and also
     * compile the callbacks of the individual component.
     *
     * @return the compiler object, used by this panel to run user code and also
     * compile the callbacks of the individual component.
     */
    public GUICompiler getGUICompiler() {
	return this.c;
    }

    /**
     * Run a program, defined by the provided bytecode (an ArrayList of Tokens).
     *
     * @param program the program to run, defined by its bytecode as ArrayList
     * of Tokens.
     * @return Always 0. TODO: Return 1 on error.
     */
    public float handleCallBack(ArrayList<Token> program) {
	this.vm.setProgram(program);
	vm.runProgram();
	return 0;
    }

    /**
     * Inform this GUIPanel about a new GUIelement. It should never be necessary
     * to call this function manually, as it is automatically called whenever a
     * new GUIelement is placed on a GUITab, which is located on this GUIPanel.
     * A new unique ID is assigned to this element. Several hashmaps are
     * updated, which make it easy to later reference this GUIelement by its ID
     * (which is how user code or any CLUC code refers to the GUI element) and
     * also by its Unique name.
     *
     * @param ge the GUIelement to add
     */
    public void registerGUIelement(GUIelement ge) {
	ge.setGUIPanel(this);
	GUIIDMap.put(ge, totalGUIelementCount);
	ID2GUIMap.put(totalGUIelementCount, ge);
	GUINameMap.put(ge.getUniqueName(), ge);
	totalGUIelementCount++;
    }

    /**
     * Return the repeat count, which should be used for the next action. Reset
     * the repeat count to 1 afterwards. 
     * 
     * This is semantically identical to calling getRepeatCount(true).
     * 
     * The repeat count is a number, which
     * can be provided by the user. Once provided, it is passed to the next
     * action which is run by the user. This action can then use it; usually,
     * the meaning of this number is the number of times said action should be
     * executed.
     * 
     *
     * @return the repeat count, which should be used for the next action.
     * @throws RuntimeException
     */
    @Override
    public int getRepeatCount() throws RuntimeException {
	return getRepeatCount(true);
    }

    /**
     * Return the repeat count, which should be used for the next action. Reset
     * the repeat count to 1 afterwards. This repeat count is a number, which
     * can be provided by the user. Once provided, it is passed to the next
     * action which is run by the user. This action can then use it; usually,
     * the meaning of this number is the number of times said action should be
     * executed.
     *
     * @param reset whether or not should the repeat count be reset after it's
     * returned.
     * @return the repeat count, which should be used for the next action.
     * @throws RuntimeException
     */
    public int getRepeatCount(boolean reset) {

	if (repeatCountString.length() == 0) {
	    return 1;
	}
	if (reset) {
	    this.resetRepeatCount();
	}
	return Integer.parseInt(this.repeatCountString);

    }

    /**
     * Get the virtual machine, used to executed the user code and the code from
     * the commandLine.
     * @return the virtual machine, used to executed the user code and the code from
     * the commandLine.
     */
    public GUIVirtualMachine GetGUIVirtualMachine() {
	return this.vm;
    }

    /**
     * Get the content of the register, determined by the letter, provided as
     * a string.
     * 
     * Also handles clipboard access and other special registers.
     * @param registerName the letter (or character) of the register in question.
     * @return the content of the register, determined by the letter, provided as
     * a string.
     */
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
	this.setCurrentRegister(UNNAMED_REGISTER);
	//this.currentRegister = UNNAMED_REGISTER;
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
	//this.currentRegister = UNNAMED_REGISTER;
	this.setCurrentRegister(UNNAMED_REGISTER);
	this.lFlag = false;
	return returnString;
    }

    /**
     * Sets the content of register registerName to the String content provided.
     * @param registerName the letter of register, content of which should be set
     * @param content the new content
     */
    public void setRegisterContent(String registerName, String content) {
	if (registerName.charAt(0) >= 'a' && registerName.charAt(0) <= 'z')//change register
	{
	    registerMap.put(registerName, content);
	} else if (registerName.charAt(0) >= 'A' && registerName.charAt(0) <= 'Z')//change register
	{
	    registerMap.put(registerName.toLowerCase(), registerMap.get(registerName.toLowerCase()) + "\n" + content);
	} else if (registerName.charAt(0) == '%')//unnamed register
	{

	    for (int i = 9; i > 0; i--) {
		String sourceRegName = "" + (i - 1);
		String targetRegName = "" + i;
		String currentString = registerMap.get(sourceRegName);
		if (currentString != null) {
		    registerMap.put(targetRegName, currentString);
		}
	    }

	    final Clipboard clipboard = Clipboard.getSystemClipboard();
	    final ClipboardContent cContent = new ClipboardContent();
	    String currentString = getRegisterContent("%");
	    registerMap.put("0", currentString);
	    registerMap.put(registerName, content);
	    cContent.putString(content);
	    clipboard.setContent(cContent);
	}
    }

    /**
     * Set the repeat count to the value provided
     * @param repeatCount the value to set the repeat count to
     * @see #getRepeatCount()
     */
    protected void setRepeatCount(int repeatCount) {
	this.repeatCountString = Integer.toString(repeatCount);
    }

    /**
     * Resets the repeat count
     * @see #getRepeatCount()
     */
    public void resetRepeatCount() {
	this.repeatCountString = "";
	repeatCountBlimp.setText("(1)");
	this.nFlag = false;
    }



    /**
     * Show given text at the statusWindow, at the bottom.
     * @param text the text to show
     */
    public void showText(String text) {
	if (this.infoTextArea != null) {
	    this.infoTextArea.appendText(text);
	    this.infoTextArea.end();
	}
    }

    /**
     * Show given text at the statusWindow, at the bottom. Also flash
     * a warning "ERROR" blimp in the statusLine.
     * @param text the text to show
     */
    public void showError(String text) {
	showText(text + "\n");//TODO: make it red
	setErrorStatus();
	Timer clearErrorTimer = new Timer();

	TimerTask theTask = new TimerTask() {
	    @Override
	    public void run() {
		GUIPanel.this.resetErrorStatus();
	    }
	};

	clearErrorTimer.schedule(theTask, 3000);
    }

    /**
     * Returns the {@link CanvasPane}, which the GUI elements are drawn to.
     * @treatAsPrivate
     * @return  the {@link CanvasPane}, which the GUI elements are drawn to.
     */
    public CanvasPane getCanvasPane() {
	return this.canvasPane;
    }

    public GUIPanel() {
	//super(null);

	actionMap = new HashMap<>();
	pkeh = new PanelKeyEventHandler();
	canvasPane = new CanvasPane(400, 200);
	canvas = canvasPane.getCanvas();//new Canvas(400, 200);

	canvas.widthProperty()
		.addListener(observable -> this.getCurrentGUITab().paintGUIelements());
	canvas.widthProperty()
		.addListener(observable -> this.getCurrentGUITab().paintGUIelements());

	vb = new VBox(8);
	//vb.getChildren().add(canvas);
	cmdLine = new TextArea("Slepice");

	cmdLine.setPrefRowCount(
		10);
	infoTextArea = new TextArea("Kokodak") {
	    @Override
	    public void requestFocus() {

	    }
	};

	cmdLine.setStyle(
		"-fx-text-inner-color: gray;");

	editorTextArea = new TextArea("kokodak");

	editorTextArea.setPrefRowCount(
		500);
	//cmdLine.setOnKeyPressed(event -> pkeh.escapeKeyPressed(event.getCode(), null));

	this.enterPressAction = new NamedGUIAction("confirm") {
	    public void doAction() {

	    }
	};

	cmdLine.setOnKeyPressed(event
		-> pkeh.keyPressed(event, null));
	editorTextArea.setOnKeyPressed(event
		-> pkeh.userCodeEditorKeyPressed(event, null));
		//cmdLine.setPrefRowCount(1);

	//statusLine.setRotate(40);//wow, funky
	infoTextArea.setEditable(
		false);
	infoTextArea.setPrefRowCount(
		200);
	infoTextArea.setFocusTraversable(
		false);

	vFlagStatusBlimp = new Label("vFlag: OFF");

	vFlagStatusBlimp.setTextFill(Color.YELLOW);

	vFlagStatusBlimp.setStyle(
		"-fx-background-color: black");

	modeStatusBlimp = new Label("[NORMAL]");

	modeStatusBlimp.setTextFill(Color.WHITE);

	modeStatusBlimp.setStyle(
		"-fx-background-color: black");

	typedKeysBlimp = new Label("abcd");

	typedKeysBlimp.setTextFill(Color.WHITE);

	typedKeysBlimp.setStyle(
		"-fx-background-color: black");

	repeatCountBlimp = new Label("1");

	repeatCountBlimp.setTextFill(Color.WHITE);

	repeatCountBlimp.setStyle(
		"-fx-background-color: black");

	registerBlimp = new Label("(%)");

	registerBlimp.setTextFill(Color.WHITE);

	registerBlimp.setStyle(
		"-fx-background-color: black");

	recordingStatusBlimp = new Label("");

	recordingStatusBlimp.setTextFill(Color.RED);

	recordingStatusBlimp.setStyle(
		"-fx-background-color: black");

	errorStatusBlimp = new Label("");

	errorStatusBlimp.setTextFill(Color.BLACK);

	errorStatusBlimp.setStyle(
		"-fx-background-color: red");

	TilePane hb = new TilePane();

	hb.setHgap(
		10);

	hb.setStyle(
		"-fx-background-color: black");
	hb.setOrientation(Orientation.HORIZONTAL);

	hb.getChildren()
		.addAll(recordingStatusBlimp, modeStatusBlimp, vFlagStatusBlimp, registerBlimp, repeatCountBlimp, typedKeysBlimp, errorStatusBlimp);

	vb.getChildren()
		.addAll(cmdLine, infoTextArea, hb);
	canvas.setFocusTraversable(
		true);
	// Clear away portions as the user drags the mouse
	canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
		new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent e
		    ) {

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
	canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
		new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent t
		    ) {
			GUIPanel.this.handleMousePress(t);
			if (t.getClickCount() > 1) {
			    reset(canvas, Color.BLUE);
			}
		    }
		}
	);
	canvas.setOnScroll(
		(ScrollEvent event) -> {
		    double deltaY = event.getDeltaY();
		    GUIPanel.this.getCurrentGUITab().sendMouseScroll(event);

		}
	);
	canvas.setOnMouseDragged(
		(MouseEvent event) -> {
		    GUIPanel.this.getCurrentGUITab().sendMouseDrag(event);

		}
	);

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
	//c.compile(eventTest);
	//c.compile(eventTest2);

	GUISlider gs2 = new GUISlider(gt2);
	GUISlider gs3 = new GUISlider(gt2);
	GUISlider gs4 = new GUISlider(gt2);
	GUIDisplay gd = new GUIDisplay(gt);
	GUITimer gti = new GUITimer(gt);
	GUIStatsDisplay gsd = new GUIStatsDisplay(gt2);
	GUIPID pid = new GUIPID(gt2);

	System.out.println(
		"pipka kokon:");
	pid.getPropertyByName(
		"P").setValue(10F);
	GUIPID pid2 = (GUIPID) pid.makeCopy();
	GUINumericUpDown gnud = new GUINumericUpDown(gt2);

	gt2.addGUIelement(gnud);

	gt2.addGUIelement(pid);

	gt2.addGUIelement(pid2);

	gt2.addGUIelement(gsd);

	gt.addGUIelement(gd);

	gt2.addGUIelement(gs2);

	gt2.addGUIelement(gs3);

	gt2.addGUIelement(gs4);

	gt2.addGUIelement(gti);

	//gt.addGUIelement(gs4);
	String prog = "byte kokon()\n"
		+ "IF (123)\n"
		+ "printNumber(10);\n"
		+ "ENDIF\n"
		+ "RETURN 0;\n"
		+ "ENDFUNCTION\n";
	//String prog = "byte test(byte n)\nprintNumber(n);\nRETURN 0;\nENDFUNCTION\n";
	String prog2 = "byte test(byte n)\nprintNumber(n);\nRETURN 0;\nENDFUNCTION\n";
	//String prog2 = "byte test(byte n)\nprintNumber(n+10);\nRETURN 0;\nENDFUNCTION\n";

	String prog3 = "byte step()\nTAB1_GS_SLIDER0.setValue(TAB2_GS_SLIDER0.getValue()+20);\nRETURN 0;\nENDFUNCTION\n";
	String prog4 = "byte TAB1_GS_SLIDER0_Value_S()\nprintNumber(15);\nRETURN 0;\nENDFUNCTION\n";
	String prog5 = "byte TAB2_GENERIC_GUI_ELEMENT_GENERIC0_Sample()\nprintText(\"kokodak\");\nTAB2_GENERIC_GUI_ELEMENT_GENERIC0.setLineY(\"a\",TAB2_GS_SLIDER0.getValue()*10);\nRETURN 0;\nENDFUNCTION\n";
	//String prog6 = "byte TAB1_GS_SLIDER3_Value_S()\nprintNumber(20);\nRETURN 0;\nENDFUNCTION\n";
	//c.compile(prog);
	/*
	 c.compile(prog2);
	 c.compile(prog3);
	 c.compile(prog4);
	 c.compile(prog5);
	 */
	//c.compile(prog6);
	vm = new GUIVirtualMachine(this);
		//vm.setProgram(c.getByteCodeAL());
	//vm.runProgram();

	//String funcCall="slepice();\n";
	//c.compile(funcCall);
	//vm.setProgram(c.getByteCodeAL());
	//vm.runProgram();
	String userCode = "";
	//this.globalMapManager.addMapping("j", "k");
	//this.globalMapManager.addMapping("k", "j");
	//this.globalMapManager.addMapping("=", ":CGE.setValue(50)<ENTER>");
	//this.recompileEventsForAll();

	final Stage dialog = new Stage();

	dialog.initModality(Modality.APPLICATION_MODAL);
	//dialog.initOwner(primaryStage);
	VBox dialogVbox = new VBox(20);

	//dialogVbox.getChildren().add(new Text("This is a Dialog"));
	dialogVbox.getChildren()
		.add(editorTextArea);
	Scene dialogScene = new Scene(dialogVbox, 500, 500);

	dialog.setScene(dialogScene);

	Button btn = new Button();

	btn.setText(
		"Save and recompile");
	btn.setOnAction(
		new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent event
		    ) {
			GUIPanel.this.userCode = editorTextArea.getText();
			GUIPanel.this.recompileUserCode();
		    }
		}
	);
	dialogVbox.getChildren()
		.add(btn);
	dialog.show();

	System.out.println(HintManager.get(this).fillString("I TAB1_GS"));
	/*
	 sc = new SerialCommunicator(this);
	 try {
	 sc.connect("COM3");
	 //Thread.sleep(5000);
	 sc.getWriter().sendInit();
	 } catch (Exception ex) {
	 Logger.getLogger(GUIPanel.class.getName()).log(Level.SEVERE, null, ex);
	 }
	 */
    }

    /**
     * Start showing the ERROR blimp in the status line.
     */
    private void setErrorStatus() {
	Platform.runLater(() -> this.errorStatusBlimp.setText("ERROR!"));
    }

    /**
     * Stop showing the ERROR blimp in the status line.
     */
    private void resetErrorStatus() {
	Platform.runLater(() -> this.errorStatusBlimp.setText(""));
    }

    /**
     * Recompile the user code into a bytecode using the GUICompiler.
     * @see #getGUICompiler()
     */
    public void recompileUserCode() {
	try {
	    c.compile(this.userCode);
	} catch (CompilerException ex) {
	    this.showError(ex.getMessage());
	}
	this.recompileEventsForAll();
    }

    private void handleMousePress(MouseEvent me) {
	if (currentGUITab != null) {
	    currentGUITab.sendMousePress(me);
	}
    }

    @Deprecated
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

    /**
     * Switch the currently selected tab to be the nth next in the list, where
     * n is the number provided. N can also be negative, in such a case, switch
     * to the -nth previous tab.
     * @param count 
     */
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

    /**
     * Switch the currently selected tab to be the next one in the list (if 
     * the boolean provided is true) or to be the previous one (if the boolean
     * provided is false).
     * @param forward whether to switch to the next (true) or previous (false)
     */
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

    /**
     * Add a {@link #ChangeListener} to the list of listeners, events of which get fired,
     * once the enter is pressed.
     * @param listener 
     */
    protected void addCmdLineListener(javafx.beans.value.ChangeListener<String> listener) {
	this.cmdLine.textProperty().addListener(listener);
	this.cmdLineListenerList.add(listener);
    }

    /**
     * Reverts the behavior of an enter press, which follows after text is typed into the
     * commandLine to the default state, which is compiling this text as a command and running it.
     */
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

    /*
     @Override
     public GUIelement makeCopy() {
     throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
     }
     */

    /**
     * Returns the {@link #TextArea}, representing the commandLine
     * @return 
     */
    protected TextArea getCmdLine() {
	return this.cmdLine;
    }

    /**
     * Returns, whether UniqueNames of components are shown (true) or if the human readable names
     * are shown (false).
     * Unique name is a name of the component, which can be used to uniquely identify it, using
     * the name2IdMap and ID2GUIMap.
     * 
     * @return whether UniqueNames of components are shown (true) or if the human readable names
     * are shown (false).
     */
    public boolean showUniqueNames() {
	return uniqueNames;
    }

    /**
     * Set, whether UniqueNames of components are shown (true) or if the human readable names
     * are shown (false).
     * */
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
		    modeStatusBlimp.setText("[EDIT]");
		    modeStatusBlimp.setTextFill(Color.BLACK);
		    modeStatusBlimp.setStyle("-fx-background-color: greenyellow");

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
		    cmdLine.selectAll();

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

	public void userCodeEditorKeyPressed(KeyEvent keyEvent, Stage dialog) {
	    if (keyEvent.getCode() == KeyCode.TAB) {
		String autoComplete = HintManager.get(GUIPanel.this).fillString(GUIPanel.this.editorTextArea.getText());
		editorTextArea.insertText(editorTextArea.getCaretPosition(), autoComplete);
		keyEvent.consume();
	    }
	}

	public void keyPressed(KeyEvent keyEvent, Stage dialog) {
	    if (keyEvent.getCode() == KeyCode.ESCAPE) {
		canvas.requestFocus();
		GUIPanel.this.resetCmdLineListeners();
	    }
	    if (keyEvent.getCode() == KeyCode.ENTER) {
		sendEnterPressForCmdLine(keyEvent.isShiftDown());
		keyEvent.consume();
	    }
	    if (keyEvent.getCode() == KeyCode.TAB) {
		sendTabPressForCmdLine();
		keyEvent.consume();
	    }
	}

	public void sendTabPressForCmdLine() {

	    String autoComplete = HintManager.get(GUIPanel.this).fillString(GUIPanel.this.cmdLine.getText());
	    GUIPanel.this.showText("--------------------\n");
	    GUIPanel.this.showText(HintManager.get(GUIPanel.this).getHints(GUIPanel.this.cmdLine.getText()));
	    cmdLine.insertText(cmdLine.getCaretPosition(), autoComplete);
	}

	public void sendEnterPressForCmdLine(boolean shiftPressed) {
	    if (!shiftPressed) {
		canvas.requestFocus();
		GUIPanel.this.enterPressAction.doAction();
		cmdLine.setPrefRowCount(1);
		GUIPanel.this.resetCmdLineListeners();
		if (isRecordingAMacro) {
		    currentMacro.append(GUIPanel.this.getCmdLine().getText() + "<ENTER>");
		}
	    } else {
		cmdLine.setPrefRowCount(cmdLine.getPrefRowCount() + 1);
		cmdLine.insertText(cmdLine.getCaretPosition(), "\n");
		System.out.println("shift pressed");
	    }
	    //cmdLine.setDisable(true);

	}

	public void handle(String eventText, boolean respectMappings) {
	    if (respectMappings && (!isDigit(eventText))) {
		GUIPanel.this.currentCommandText += eventText;
		currentCommandText = currentCommandText.replaceAll("\\r|\\n", "");
		if (currentCommandText.length() > 8) {
		    currentCommandText = currentCommandText.substring(0, 8);
		}
		GUIPanel.this.typedKeysBlimp.setText(currentCommandText);
	    }
	    typedKeysBlimp.setTextFill(Color.WHITE);
	    if (GUIPanel.this.getCmdLine().isFocused()) {

		if (eventText.equals("<ENTER>")) {
		    sendEnterPressForCmdLine(false);
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

		vFlagStatusBlimp.setText("vFlag: " + (vFlag ? "on" : "off"));

		if (!vFlag) {
		    vFlagStatusBlimp.setTextFill(Color.YELLOW);
		    vFlagStatusBlimp.setStyle("-fx-background-color: black");
		} else {
		    vFlagStatusBlimp.setTextFill(Color.BLACK);
		    vFlagStatusBlimp.setStyle("-fx-background-color: yellow");
		}

	    } else if (eventText.equals("\"")) {
		GUIPanel.this.pickRegisterMenu.setSuperMenu(GUIPanel.this.currentMenu);
		GUIPanel.this.currentMenu = (GUIPanel.this.pickRegisterMenu);
		lFlag = true;
	    } else if (isDigit(eventText) && !((GUIPanel.this.currentMenu) instanceof RegisterSelectionMenu)) { //instanceof, so we allow numeric registers... Stupid hotfix, I know
		System.out.println("pressed num: " + eventText);
		GUIPanel.this.repeatCountString += eventText;
		repeatCountBlimp.setText(repeatCountString);
		System.out.println("Current num: " + GUIPanel.this.repeatCountString);
		nFlag = true;
	    } else {

		GUIPanel.this.currentMenu.handle(eventText);
		//GUIPanel.this.resetRepeatCount(); //leave this to others!
	    }
	}

	private boolean isDigit(String s) {
	    return (s.length() == 1 && s.charAt(0) > '0' && s.charAt(0) < '9');
	}

	private void runCommand(KeyCode code, Object object) {
	    if (code == KeyCode.ENTER) {
		//c=InterpreterFacade.prepareCompiler(GUIPanel.this);
		if (!vFlag) {
		    try {
			c.compile("printNumber(" + GUIPanel.this.cmdLine.getText() + ");\n");//added the printNumber for convenience, not sure if the best
			//vm=new GUIVirtualMachine(GUIPanel.this);//TODO: NO NO NO
			vm.setProgram(c.getByteCodeAL());
			vm.runProgram();
		    } catch (CompilerException ex) {
			try {
			    c.compile(GUIPanel.this.cmdLine.getText() + "\n");
			    //vm=new GUIVirtualMachine(GUIPanel.this);//TODO: NO NO NO
			    vm.setProgram(c.getByteCodeAL());
			    vm.runProgram();
			} catch (CompilerException ex1) {
			    GUIPanel.this.showError(ex.getMessage());
			    GUIPanel.this.showError(ex1.getMessage());
			}
		    }
		} else {
		    GUIelement backupGE = this.getGUIPanel().getCurrentGUITab().getFocusedGUIElement();
		    for (GUIelement ge : this.getGUIPanel().getSelectedGUIelementsList(true)) {
			this.getGUIPanel().getCurrentGUITab().focusGUIelement(ge);

			try {
			    c.compile(GUIPanel.this.cmdLine.getText() + "\n");
			    vm.setProgram(c.getByteCodeAL());
			    vm.runProgram();
			} catch (CompilerException ex) {
			    GUIPanel.this.showError(ex.getMessage());
			}

		    }
		    this.getGUIPanel().getCurrentGUITab().focusGUIelement(backupGE);
		}
	    }
	}
    }

    /**
     * Get the ArrayList, containing all the currently selected GUIelements.
     * No particular ordering is imposed.
     * This method is semantically equivalent to {@link #getSelectedGUIelementsList(true)}
     * @return the ArrayList, containing all the currently selected GUIelements.
     */
    public ArrayList<GUIelement> getSelectedGUIelementsList() {
	return getSelectedGUIelementsList(true);
    }

    /**
     * Get the ArrayList, containing all the currently selected GUIelements.
     * No particular ordering is imposed.
     * This method makes calls to the {@code GUITab#getSelectedGUIelementsList}.
     * 
     * When respectVFlag is off, return an arrayList with only one element,
     * representing the currently focused GUIelement instead.
     * 
     * @param respectVFlag  When respectVFlag is off, return an arrayList with only one element,
     * representing the currently focused GUIelement instead.
     * @return the ArrayList, containing all the currently selected GUIelements; if none are selected, return an empty list if
     * respectVFlag is off, return an ArrayList containing the focused element if respectVFlag is on.
     */
    public ArrayList<GUIelement> getSelectedGUIelementsList(boolean respectVFlag) {
	ArrayList<GUIelement> returnList = new ArrayList<>();
	if (respectVFlag) {
	    if (vFlag) {
		returnList = this.getCurrentGUITab().getSelectedGUIelementsList();
		if (returnList.isEmpty()) {
		    this.showError("Warning: vFlag is on, but no elements were selected!");//Fail, this aint visible
		}
	    } else {
		returnList.add(this.getCurrentGUITab().getFocusedGUIElement());
	    }
	    //vFlag = false;
	} else {
	    returnList = this.getCurrentGUITab().getSelectedGUIelementsList();
	}
	return returnList;

    }

    /**
     * Return, whether the VFlag is currently on.
     * This flag determines, whether the next action should apply to the
     * focused element only, or to all the elements selected.
     * @return whether the VFlag is currently on.
     */
    public boolean getVFlag() {
	return this.vFlag;
    }

    /**
     * Return, whether the NFlag is currently on. That is, whether the user
     * has provided any numeric argument
     * @see #getCount
     * @return whether the NFlag is currently on. That is, whether the user
     * has provided any numeric argument
     */
    public boolean getNFlag() {

	return this.nFlag;
    }

    /**
     * Return, whether the LFlag is currently on. That is, whether the user
     * has provided any letter argument
     * @see #getCurrentRegisterLetter()
     * @return whether the NFlag is currently on. That is, whether the user
     * has provided any numeric argument
     */
    public boolean getLFlag() {
	return this.lFlag;
    }

    /**
     * Start recording the keypresses that are about to follow as a string
     * inside a register, letter of which is determined by the argument provided.
     * @param register the register to record the macro into
     */
    protected void startRecordingMacro(String register) {
	this.isRecordingAMacro = true;
	this.currentMacroRegister = register;
	this.recordingStatusBlimp.setText("[REC: " + register + "]");
	//Macro m = new Macro();
	this.currentMacro = new KeySequence("");
	//this.macroMap.put(register, m);
    }

    /**
     * Stop recording the current macro if any is being recorded. Do nothing otherwise.
     */
    protected void stopRecordingMacro() {
	this.setRegisterContent(currentMacroRegister, currentMacro.toString());
	this.recordingStatusBlimp.setText("");
	this.isRecordingAMacro = false;
    }

    /**
     * Execute a macro, simulating the subsequent presses of the key sequence provided.
     * This method is similar to {#handle}, but also resets the current menu to the main menu.
     * @param sequenceOfKeys 
     * @see PanelKeyEventHandler#getMainMenu() 
     * @see this#handle()
     */
    protected void executeMacro(String sequenceOfKeys) {
	//Macro m = this.macroMap.get(register);
	KeySequence macro = new KeySequence(sequenceOfKeys);
	this.setMenu(pkeh.getMainMenu());
	System.out.println("executing macro");
	System.out.println(sequenceOfKeys);
	macro.execute(this);
    }


    /**
     * Add another tab to this panel. This tab will then be assigned a unique
     * number, based on how many tabs have already been on this panel when this
     * new one was added.
     * @param gt the tab to add.
     * @return the number, which was assigned to this tab.
     */
    public int addGUITab(GUITab gt) {
	gt.setGUIPanel(this);
	tabList.add(gt);
	return tabList.size() - 1;
    }

    /**
     * @return the Label object, displaying the current mode in the statusLine (Normal/Edit).
     */
    public Label getModeStatusBlimp() {
	return this.modeStatusBlimp;
    }


    /**
     * @return the currently active {@link Menu}
     */
    public Menu getMenu() {
	return this.currentMenu;
    }

    /**
     * Reset the string, which stores all the keypresses that occured before a command was run to an empty string.
     */
    public void resetCurrentCommandText() {
	this.currentCommandText = "";
    }

    /**
     * Set the currently active menu to the one provided. If this menu is equal
     * ({@link #equals()}) with the main menu of this class, also reset the current mode to normal (which reflects in the
     * statusLine).
     * @param m 
     * @see
     */
    public void setMenu(Menu m) {
	if (pkeh != null && m != null) {
	    if (m.equals(pkeh.getMainMenu())) {
		setCurrentlyEditedGUIelement(null);//when we end editing the component, we are
		//in the "normal mode"
		if (modeStatusBlimp != null) {
		    modeStatusBlimp.setStyle("-fx-background-color: black");
		    modeStatusBlimp.setTextFill(Color.WHITE);
		    this.modeStatusBlimp.setText(("[NORMAL]"));
		    //typedKeysBlimp.setTextFill(Color.DARKGRAY);
		    GUIPanel.this.typedKeysBlimp.setText("");
		    this.currentCommandText = "";
		    GUIPanel.this.resetRepeatCount();
		    //GUIPanel.this.setCurrentRegister("%");
		    ////this.setRegister("%");
		}
	    }

	}
	this.currentMenu = m;
	m.showMenu();

    }

    /**
     * A simple class intended as a wrapper for {@link #Canvas}, to allow
     * it to autoresize on window size changes.
     */
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
