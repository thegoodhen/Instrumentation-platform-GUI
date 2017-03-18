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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

/**
 *
 * @author thegoodhen
 */
public class GUITab extends GUIelement implements IRepetitionCounter {

    Canvas canvas;
    private int selectedElementIndex = 0;
    ArrayList<GUIelement> GUIList = new ArrayList<>();
    private Menu currentMenu;
    private String repeatCountString = "";

    //PanelKeyEventHandler pkeh;
    HashMap<String, GUIelement> quickMarkMap = new HashMap<>();
    private int scrollOffset = 0;
    //private final GUIPanel gp;

    public int getRepeatCount() throws RuntimeException {
	if (repeatCountString.length() == 0) {
	    return 1;
	}
	return Integer.parseInt(this.repeatCountString);
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
	    targetElement.setFocused(true);
	    this.focusGUIelement(GUIList.indexOf(targetElement));
	}
    }

    public GUITab(GUIPanel gp, String name) {
	super(null);
	this.setName(name);
	this.setGUIPanel(gp);
	//pkeh = new PanelKeyEventHandler();
	canvas = gp.getCanvas();
	//canvas.setFocusTraversable(true);
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
	/*
	 canvas.addEventHandler(KeyEvent.KEY_TYPED,
	 new EventHandler<KeyEvent>() {

	 @Override
	 public void handle(KeyEvent ke) {
	 //gs.paint(gc, 10, 10);
	 System.out.println(ke.getCharacter());
	 //createLetter(ke.getText());
	 pkeh.handle(ke);
	 paintGUIelements();
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

	 */
	//actionMap.put("j", testAction);
	//actionMap.put("k", testAction2);
	GUIChart gc = new GUIChart(this);
	addGUIelement(gc);
	GUISlider gs = new GUISlider(this);
	addGUIelement(gs);
	GUISlider gs2 = new GUISlider(this);
	GUISlider gs3 = new GUISlider(this);
	GUISlider gs4 = new GUISlider(this);
	GUICheckBox gs5 = new GUICheckBox(this);
	addGUIelement(gs2);
	addGUIelement(gs4);
	addGUIelement(gs3);
	addGUIelement(gs4);
	addGUIelement(gs5);
	gs3.setEnabled(true);
	gs2.setEnabled(false);
	gs4.setEnabled(true);
	gs5.setEnabled(true);

    }

    public Canvas getCanvas() {
	return canvas;
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

    public GUIelement getFocusedGUIElement() {
	return GUIList.get(selectedElementIndex);
    }

    public int getFocusedGUIElementIndex() {
	return selectedElementIndex;
    }

    public int getGUIElementIndex(GUIelement ge) {
	return GUIList.indexOf(ge);
    }

    ArrayList<GUIelement> getSelectedGUIelementsList() {
	ArrayList<GUIelement> returnList = new ArrayList<>();
	for (GUIelement ge : GUIList) {
	    if (ge.isSelected()) {
		returnList.add(ge);
	    }
	}
	return returnList;
    }

    void insertGUIelement(int currentGUIElementIndex, String name, boolean copy) {
	GUIelement ge = this.getGUIPanel().GUINameMap.get(name);
	if (ge != null) {
	    if (copy) {
		ge = ge.makeCopy();
	    }
	    this.addGUIelement(ge, currentGUIElementIndex);
	    for (GUIelement ge2 : GUIList) {
		ge2.setFocused(false);
	    }
	    ge.setFocused(true);
	}
    }

    @Override
    public GUIelement makeCopy() {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /*
     Apparently, my code is so unbelievabely robust, that even such a drastic change, as commenting
     out around 600 LOC has no impact on its functionality whatsoever. Way to go! ^>^
     */
    /*
     private class PanelKeyEventHandler implements GUIKeyEventHandler {

     Menu mainMenu;
     private boolean isRecordingAMacro = false;
     private HashMap<String, Macro> macroMap;
     private Macro currentMacro;
     private Menu startRecordingMacroMenu;
     private final RegisterSelectionMenu executeMacroMenu;
     private NamedGUIAction stopRecordingMacroAction;

     public PanelKeyEventHandler() {
     macroMap = new HashMap<>();

     NamedGUIAction editComponentAction = new NamedGUIAction("edit component") {
     @Override
     public void doAction() {
     editCurrentComponent();
     }
     };

     NamedGUIAction testAction = new NamedGUIAction("previous element") {
     @Override
     public void doAction() {
     traverseElements(true);
     }
     };
     NamedGUIAction testAction2 = new NamedGUIAction("next element") {

     @Override
     public void doAction() {
     traverseElements(false);
     }
     };
     NamedGUIAction jumpToPercent = new NamedGUIAction("jump to (n) % ") {

     @Override
     public void doAction() {
     //doAction(1);
     //GUIList.get(selectedElementIndex).setFocused(false);
     //selectedElementIndex = 0;//GUIList.size() - 1;
     //traverseElements(true);
     }

     @Override
     public void doAction(IRepetitionCounter irc) {
     }
     };

     NamedGUIAction jumpToBeginning = new NamedGUIAction("beginning") {

     @Override
     public void doAction() {
     //doAction(1);
     //GUIList.get(selectedElementIndex).setFocused(false);
     //selectedElementIndex = 0;//GUIList.size() - 1;
     //traverseElements(true);
     }

     @Override
     public void doAction(IRepetitionCounter irc) {
     int newIndex = irc.getRepeatCount();
     GUIList.get(selectedElementIndex).setFocused(false);
     if (newIndex - 1 < GUIList.size() && (newIndex - 1 >= 0)) {
     selectedElementIndex = newIndex - 1;
     }
     }
     };
     NamedGUIAction jumpToEnd = new NamedGUIAction("end") {
     @Override
     public void doAction() {
     //doAction(1);
     //GUIList.get(selectedElementIndex).setFocused(false);
     //selectedElementIndex = 0;//GUIList.size() - 1;
     //traverseElements(true);
     }

     @Override
     public void doAction(IRepetitionCounter irc) {
     int newIndex = irc.getRepeatCount();
     if (newIndex == 1) {
     GUIList.get(selectedElementIndex).setFocused(false);
     selectedElementIndex = GUIList.size() - 2;
     traverseElements(true);

     } else if (newIndex - 1 < GUIList.size() && (newIndex - 1 >= 0)) {
     GUIList.get(selectedElementIndex).setFocused(false);
     selectedElementIndex = newIndex - 1;
     }
     }
     };

     RegisterAction setMarkAction = new RegisterAction() {

     @Override
     public void doAction(String register) {
     GUITab.this.setMark(register);
     }

     @Override
     public void doAction(String register, IRepetitionCounter irc) {
     doAction(register);
     }

     };

     RegisterAction jumpToMarkAction = new RegisterAction() {

     @Override
     public void doAction(String register) {
     GUITab.this.jumpToMark(register);
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

     Menu m = new Menu(GUITab.this.getGUIPanel(), "Main menu", true);

     this.mainMenu = m;

     m.addAction(
     "j", testAction);
     m.addAction(
     "k", testAction2);
     m.addAction("a", editComponentAction);
     m.addAction("%", jumpToPercent);

     Menu goMenu = new Menu(GUITab.this.getGUIPanel(), "Go to", false);

     goMenu.addAction(
     "g", jumpToBeginning);
     goMenu.addAction(
     "G", jumpToEnd);
     m.addAction("G", jumpToEnd);
     m.addSubMenu(
     "g", goMenu);

     Menu setMarkMenu = new RegisterSelectionMenu(GUITab.this.getGUIPanel(), "set mark", setMarkAction);
     Menu jumpToMarkMenu = new RegisterSelectionMenu(GUITab.this.getGUIPanel(), "set mark", jumpToMarkAction);

     m.addSubMenu(
     "m", setMarkMenu);
     m.addSubMenu(
     "¨", jumpToMarkMenu);

     startRecordingMacroMenu = new RegisterSelectionMenu(GUITab.this.getGUIPanel(), "record macro", startRecordingMacroAction);

     executeMacroMenu = new RegisterSelectionMenu(GUITab.this.getGUIPanel(), "execute macro", executeMacroAction);

     mainMenu.addSubMenu(
     "q", PanelKeyEventHandler.this.startRecordingMacroMenu);
     mainMenu.addSubMenu(
     "v", PanelKeyEventHandler.this.executeMacroMenu);
     GUITab.this.setMenu(m);
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

     public void handle(KeyEvent ke) {
     String eventText = ke.getCharacter();//ke.getText();
     if (isRecordingAMacro && eventText.equals("q")) {
     if (currentMacro != null) {
     currentMacro.addKeyEvent(ke);
     }
     }
     if (isDigit(eventText)) {
     System.out.println("pressed num: " + eventText);
     GUITab.this.repeatCountString += eventText;
     System.out.println("Current num: " + GUITab.this.repeatCountString);
     } else {

     GUITab.this.currentMenu.handle(eventText);
     //GUITab.this.resetRepeatCount(); //leave this to others!
     }
     }

     private boolean isDigit(String s) {
     return (s.length() == 1 && s.charAt(0) > 47 && s.charAt(0) < 58);
     }

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
     return GUITab.this.getGUIPanel();
     }
     }
     */
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
	this.addGUIelement(ge, GUIList.size());
    }

    public void addGUIelement(GUIelement ge, int position) {
	ge.setGUITab(this);
	GUIList.add(position, ge);
	ge.recalculateUniqueName(this);//TODO: this will cause a huge mess, fix it
	this.getGUIPanel().registerGUIelement(ge);

    }

    public void removeGUIelement(int index) {
	//String name=getUniqueName(index);
	GUIList.get(index).setFocused(false);
	GUIList.remove(index);
	//return name;
    }

    public String getUniqueName(int index) {

	return GUIList.get(index).getUniqueName();
	//this.getGUIPanel().setCurrentRegisterContentAndReset(elementUniqueName);
	//this.getGUIPanel().registerMap.put(register, elementUniqueName);
    }

    public void applySearch(int setOperation) {
	for (GUIelement ge : GUIList) {
	    ge.applySearch(setOperation);
	    paintGUIelements();
	}
    }

    public void applyFilter(int setOperation) {
	for (GUIelement ge : GUIList) {
	    ge.applyFilter(setOperation);
	    paintGUIelements();
	}
    }

    public void setPreviewRegex(String regex, int setOperation) {
	for (GUIelement ge : GUIList) {
	    ge.setPreviewRegex(regex, setOperation);
	    paintGUIelements();
	}
    }

    public void paintGUIelements() {
	int offset = 50 + scrollOffset;
	int fixedOffset = 10;

	GraphicsContext gc = canvas.getGraphicsContext2D();
	gc.setFill(Color.BLACK);//clear everything
	gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
	for (GUIelement ge : GUIList) {
	    if (ge.isVisible()) {
		ge.paint(canvas.getGraphicsContext2D(), 10, offset);
		offset += ge.getHeight();
		offset += fixedOffset;
	    }
	}
    }

    public void addAction(String s, GUIAbstractAction gaa) {
	actionMap.put(s, gaa);
    }

    /*
     public void setMenu(Menu m) {
     this.currentMenu = m;
     m.showMenu();
     }
     */
    public void editCurrentComponent(boolean interactive) {
	GUIelement currentElement = GUITab.this.getFocusedGUIElement();
	getGUIPanel().setCurrentlyEditedGUIelement(currentElement);
	currentElement.getMenu().setSuperMenu(getGUIPanel().getMenu());
	GUITab.this.getGUIPanel().setMenu(currentElement.getMenu());
    }

    public void focusGUIelement(int index) {
	GUIList.get(selectedElementIndex).setFocused(false);
	if (index < 0) {
	    index = 0;
	}
	if (index > this.GUIList.size() - 1) {
	    index = this.GUIList.size() - 1;
	}
	selectedElementIndex = index;
	GUIList.get(selectedElementIndex).setFocused(true);
	if (!GUIList.get(index).isEnabled()) {
	    traverseElements(true);
	}
    }

    public void focusGUIelement(GUIelement ge) {
	this.focusGUIelement(this.getGUIElementIndex(ge));
    }

    public int getGUIListSize() {
	return this.GUIList.size();
    }

    public ArrayList<GUIelement> getGUIList() {
	return this.GUIList;
    }

    public void jumpToPercent(IRepetitionCounter irc) {
	int percent = irc.getRepeatCount();
	int newIndex = (int) Math.round(((float) GUIList.size() / 100) * percent);
	focusGUIelement(newIndex);
    }

    public void traverseElements(int steps) {
	GUIList.get(selectedElementIndex).setFocused(false);
	boolean forward = true;
	if (steps > 0) {
	    forward = true;
	    steps -= 1;
	}
	if (steps < 0) {
	    forward = false;
	    steps += 1;
	}
	selectedElementIndex += steps;
	traverseElements(forward);
    }

    public void traverseElements(boolean forward) {
	if (selectedElementIndex > GUIList.size() - 1) {
	    selectedElementIndex = GUIList.size() - 1;
	}
	if (selectedElementIndex < 0) {
	    selectedElementIndex = 0;
	}
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
	FloatPoint lastPos = GUIList.get(selectedElementIndex).getLastPositionDrawnTo();

	double maxYpos = this.getGUIPanel().getCanvas().getHeight() * 0.9;
	double minYpos = this.getGUIPanel().getCanvas().getHeight() * 0.1;
	if (lastPos.y > maxYpos) {
	    this.scrollOffset -= (lastPos.y - maxYpos);
	}

	if (lastPos.y < minYpos) {
	    this.scrollOffset += (minYpos - lastPos.y);
	}
    }

    void sendMouseScroll(ScrollEvent event) {
	this.getFocusedGUIElement().sendMouseScroll(event);
    }

    void sendMousePress(MouseEvent event) {
	double sceneX = event.getSceneX();
	double sceneY = event.getSceneY();
	System.out.println("x: " + sceneX + " a y: " + sceneY);
	for (GUIelement ge : this.GUIList) {
	    FloatPoint fp = ge.getLastPositionDrawnTo();
	    double h = ge.getHeight();
	    double w = ge.getWidth();
	    if (sceneX > fp.x && sceneX < fp.x + w && sceneY > fp.y && sceneY < fp.y + h)//we clicked on it!
	    {
		if (!ge.isFocused()) {
		    this.focusGUIelement(ge);
		} else//was focused, we pass the press to it
		{
			ge.sendMousePress(event);
		}
		this.paintGUIelements();
		break;
	    }
	}
    }

    void sendMouseDrag(MouseEvent event) {
	this.getFocusedGUIElement().sendMouseDrag(event);
    }
}
