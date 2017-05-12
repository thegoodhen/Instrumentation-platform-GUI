/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import shuntingyard.Token;

/**
 * GUI element, used for displaying multiple plot lines at once or histograms.
 *
 * @author thegoodhen
 */
public class GUIChart extends GUIelement {

    TreeMap<Character, PlotLine> linesList = new TreeMap<>();
    char currentLineChar = 'a';
    int minXPixelTickSize = 50;
    int maxXPixelTickSize = 100;
    int minYPixelTickSize = 20;
    int maxYPixelTickSize = 50;
    double currentXTickSize = 5;
    double currentYTickSize = 5;
    //Following 2 represent a tick size, step of which is a decadic order of magnitude
    double currentRoundXTickSize = 10;
    double currentRoundYTickSize = 10;
    Timer sampleTimer = null;
    boolean isRecording = false;
    ArrayList<Token> sampleEvent = null;
    private float autoScalePaddingCoeff = 0.8F;//a value between 0 and 1, determining how many % (normalized to 1) of the total space on the chart should be filled up by the chart once autoscale triggers
    private double lastScaleXWhenTickSizeChanged = 0;
    private double lastXTickSizeWhenTickSizeChanged = 0;
    private double dragStartPlotY;
    private double dragStartPlotX;
    private long lastRecordingStartTime;
    private boolean histogramView = false;
    private double plotXBackup = 0;
    private double plotYBackup = 0;
    private double histoXBackup = 0;
    private double histoYBackup = 0;
    private double plotXScaleBackup = 1;
    private double plotYScaleBackup = 1;
    private double histoXScaleBackup = 1;
    private double histoYScaleBackup = 1;

    public final void addProperthies() {

	this.addFloatProperty(104, "LineX", 0);
	this.addFloatProperty(105, "LineY", 0);
	this.addFloatProperty(106, "LineWidth", 2);
	this.addFloatProperty(107, "LineSamples", 0);
	this.addFloatProperty(108, "LineColor", 2.0F);
	this.addIntegerProperty(109, "AutoScaleMode", 0);
	FloatProperty runTime = new FloatProperty(150, "RunTime", -1.0F, this);
	this.addProperty(runTime);
    }

    public GUIChart() {
	super();
	this.addProperthies();
	//TODO: make sure it's not necessary to do this duplicately

    }

    public GUIChart(GUITab gut) {
	super(gut);
	this.addProperthies();
	this.setName("Chart");
		//super(r);

	//actionMap.put("l", testAction);
	//actionMap.put("h", testAction2);
    }

    /**
     * Add a new line, setting the color of it in such a way, that it's easily
     * distinguishable from the other colors of lines already in the chart.
     *
     * @param pl
     */
    public void addLine(PlotLine pl) {
	this.linesList.put(pl.getCharacter(), pl);
	pl.lineColor = ColorManager.get().getNthColor(linesList.size());
    }


    /*
     public void addNewLine()
     {
     PlotLine pl=new PlotLine(currentLineChar,this);

     }
     */
    /**
     * Stops recording of all lines.
     */
    public void stopRecording() {
	if (!isRecording) {
	    return;
	}
	isRecording = false;
	sampleTimer.cancel();
	sampleTimer = null;

    }

    /**
     * Starts an automatic recording of all relevant lines.
     *
     * @see sampleAllRelevant
     */
    public void startRecording() {
	if (isRecording) {
	    return;
	} else {

	    isRecording = true;
	}
	lastRecordingStartTime = System.currentTimeMillis();
	if (sampleTimer == null) {
	    sampleTimer = new Timer();
	}
	sampleTimer.schedule(
		new TimerTask() {
		    @Override
		    public void run() {
			//System.out.println("kokodak");
			if (sampleEvent != null) {
			    //Platform.runLater(() -> {//TODO: only surround the necessary stuff in runLater!
			    GUIChart.this.getGUIPanel().handleCallBack(sampleEvent);//call the user event
			    //});
			    GUIChart.this.sampleAllRelevant();
			}
		    }
		}, 0, 100);
    }

    /**
     * Sets the current line character to the one provided.
     *
     * @param c the character to set the current line char to.
     */
    public void setCurrentLineChar(char c) {
	this.currentLineChar = c;
    }

    /**
     * Focus next line n times (nth next line, alphabetically ordered by their
     * letters). For instance(focusNextLine(2,true) focuses line "c", if line
     * "a" was focused. Negative n is valid, and focuses a previous line.
     *
     * @param nth
     * @param onlyThoseContainingData if false, also selects ghost lines (those
     * that have not yet been initialized).
     */
    void focusNextLine(int nth, boolean onlyThoseContainingData) {
	int dir = nth;

	if (nth > 0) {
	    dir = 1;
	} else {
	    dir = -1;
	}

	this.currentLineChar += nth;
	if (this.currentLineChar > 'z') {
	    this.currentLineChar = 'a';
	}
	if (this.currentLineChar < 'a') {
	    this.currentLineChar = 'z';
	}

	if (onlyThoseContainingData) {
	    if (currentLineChar == 'a') {
		System.out.println("kokoko");
	    }
	    PlotLine pl = this.linesList.get(currentLineChar);
	    while (pl == null || (!this.linesList.get(currentLineChar).isVisible()) || (pl.getPointCount() == 0)) {
		focusNextLine(dir, false);
		pl = this.linesList.get(currentLineChar);
	    }
	}
    }

    //private int value=50;
    @Override
    public void setValue(float value)//TODO: change this to like IntegerProperty or something and make it generic! :3
    {
	super.setValue(value);
	super.update();
    }

    @Override
    public String shortDesc() {
	return "GUI element - chart";
    }

    @Override
    public void setGUIPanel(GUIPanel gup) {
	super.setGUIPanel(gup);
	this.addIntegerProperty(14, "Width", 300);
	this.addIntegerProperty(15, "Height", 100);
	this.addFloatProperty(100, "PlotScaleX", 1);
	this.addFloatProperty(101, "PlotScaleY", 1);
	this.addFloatProperty(102, "PlotX", 0);
	this.addFloatProperty(103, "PlotY", 0);
	this.addLine(new PlotLine('a', this));
	this.setPlotY((50));
	this.setPlotScaleX((2F));
	this.setPlotScaleY((0.1F));
	FloatProperty runTime = new FloatProperty(150, "RunTime", -1.0F, this);
	runTime.setGetterPropertyCallback(new PropertyCallback<Float>() {
	    @Override
	    public void run(Property<Float> p) {
		System.out.println("slepice v optice kdaka velmi velice");
		if (!isRecording) {
		    p.setValue((float) -1);
		} else {
		    p.setValue((float) (System.currentTimeMillis() - lastRecordingStartTime) / 1000);
		}
	    }
	}
	);
	this.addProperty(runTime);

    }

    @Override
    public void setGUITab(GUITab gut) {
	super.setGUITab(gut);

	NamedGUIAction switchViewModeAction = new NamedGUIAction("histogram/normal") {
	    @Override
	    public void doAction() {
		if (GUIChart.this.histogramView) {
		    GUIChart.this.histogramView = false;
		    histoXBackup = (float) GUIChart.this.getPlotX();
		    histoYBackup = (float) GUIChart.this.getPlotY();
		    histoXScaleBackup = (float) GUIChart.this.getPlotScaleX();
		    histoYScaleBackup = (float) GUIChart.this.getPlotScaleY();
		    GUIChart.this.setPlotX((float) plotXBackup);
		    GUIChart.this.setPlotY((float) plotYBackup);
		    GUIChart.this.setPlotScaleX((float) plotXScaleBackup);
		    GUIChart.this.setPlotScaleY((float) plotYScaleBackup);
		} else {
		    GUIChart.this.histogramView = true;
		    plotXBackup = (float) GUIChart.this.getPlotX();
		    plotYBackup = (float) GUIChart.this.getPlotY();
		    plotXScaleBackup = (float) GUIChart.this.getPlotScaleX();
		    plotYScaleBackup = (float) GUIChart.this.getPlotScaleY();
		    GUIChart.this.setPlotX((float) histoXBackup);
		    GUIChart.this.setPlotY((float) histoYBackup);
		    GUIChart.this.setPlotScaleX((float) histoXScaleBackup);
		    GUIChart.this.setPlotScaleY((float) histoYScaleBackup);
		}
		//GUIChart.this.histogramView = !GUIChart.this.histogramView;
		GUIChart.super.update();

	    }
	};

	/**
	 * Action for scaling up along the X axis
	 */
	NamedGUIAction scaleXUpAction = new NamedGUIAction("hscale++") {
	    @Override
	    public void doAction() {
		GUIChart.this.scaleAlongOrigin(1 + (0.01 * this.getCount()), 1, GUIChart.this.getWidth() / 2, GUIChart.this.getHeight() / 2);
		//GUIChart.this.setPlotScaleX((float) GUIChart.this.getPlotScaleX() * 1.01F);
		System.out.println("kokodak");
		GUIChart.super.update();

	    }
	};

	/**
	 * Action for scaling down along the X axis
	 */
	NamedGUIAction scaleXDownAction = new NamedGUIAction("hscale--") {
	    @Override
	    public void doAction() {
		GUIChart.this.scaleAlongOrigin(1 - (0.01 * this.getCount()), 1, GUIChart.this.getWidth() / 2, GUIChart.this.getHeight() / 2);
		//GUIChart.this.setPlotScaleX((float) GUIChart.this.getPlotScaleX() * 0.99F);
		System.out.println("kokodak");
		GUIChart.super.update();

	    }
	};

	/**
	 * Action for scaling up along the Y axis
	 */
	NamedGUIAction scaleYUpAction = new NamedGUIAction("vscale++") {
	    @Override
	    public void doAction() {
		GUIChart.this.scaleAlongOrigin(1, 1 + (this.getCount() * 0.01), GUIChart.this.getWidth() / 2, GUIChart.this.getHeight() / 2);
		//GUIChart.this.setPlotScaleX((float) GUIChart.this.getPlotScaleX() * 1.01F);
		System.out.println("kokodak");
		GUIChart.super.update();

	    }
	};

	/**
	 * Action for scaling down along the Y axis
	 */
	NamedGUIAction scaleYDownAction = new NamedGUIAction("vscale--") {
	    @Override
	    public void doAction() {
		GUIChart.this.scaleAlongOrigin(1, 1 - (this.getCount() * 0.01), GUIChart.this.getWidth() / 2, GUIChart.this.getHeight() / 2);
		//GUIChart.this.setPlotScaleX((float) GUIChart.this.getPlotScaleX() * 0.99F);
		System.out.println("kokodak");
		GUIChart.super.update();

	    }
	};

	/**
	 * Action for focusing the next existing line.
	 */
	NamedGUIAction nextExistingLine = new NamedGUIAction("next existing line") {
	    @Override
	    public void doAction() {
		//GUIChart.this.setPlotScaleX((float) GUIChart.this.getPlotScaleX() * 0.99F);
		GUIChart.this.focusNextLine(this.getCount(), true);
		System.out.println(GUIChart.this.currentLineChar);
		GUIChart.super.update();

	    }
	};

	/**
	 * Action for focusing the previous existing line.
	 */
	NamedGUIAction prevExistingLine = new NamedGUIAction("previous existing line") {
	    @Override
	    public void doAction() {
		//GUIChart.this.setPlotScaleX((float) GUIChart.this.getPlotScaleX() * 0.99F);
		GUIChart.this.focusNextLine(-this.getCount(), true);
		System.out.println(GUIChart.this.currentLineChar);
		GUIChart.super.update();

	    }
	};

	/**
	 * Action for focusing the next line, whether it exists (=has data and
	 * is initialized) or not.
	 */
	NamedGUIAction nextLine = new NamedGUIAction("next line") {
	    @Override
	    public void doAction() {
		//GUIChart.this.setPlotScaleX((float) GUIChart.this.getPlotScaleX() * 0.99F);
		GUIChart.this.focusNextLine(this.getCount(), false);
		System.out.println(GUIChart.this.currentLineChar);
		GUIChart.super.update();

	    }
	};

	/**
	 * Action for focusing the previous line, whether it exists (=has data
	 * and is initialized) or not.
	 */
	NamedGUIAction prevLine = new NamedGUIAction("previous line") {
	    @Override
	    public void doAction() {
		//GUIChart.this.setPlotScaleX((float) GUIChart.this.getPlotScaleX() * 0.99F);
		GUIChart.this.focusNextLine(-this.getCount(), false);
		System.out.println(GUIChart.this.currentLineChar);
		GUIChart.super.update();

	    }
	};

	/**
	 * Action for moving the viewed portion of the chart down.
	 */
	NamedGUIAction moveDown = new NamedGUIAction("move down") {
	    @Override
	    public void doAction() {
		//GUIChart.this.setPlotScaleX((float) GUIChart.this.getPlotScaleX() * 0.99F);
		GUIChart.this.setPlotY((float) GUIChart.this.getPlotY() + (5) * this.getCount());
		GUIChart.super.update();

	    }
	};

	/**
	 * Action for moving the viewed portion of the chart up.
	 */
	NamedGUIAction moveUp = new NamedGUIAction("move up") {
	    @Override
	    public void doAction() {
		//GUIChart.this.setPlotScaleX((float) GUIChart.this.getPlotScaleX() * 0.99F);
		GUIChart.this.setPlotY((float) GUIChart.this.getPlotY() - (5) * this.getCount());
		GUIChart.super.update();

	    }
	};

	/**
	 * Action for moving the viewed portion of the chart left.
	 */
	NamedGUIAction moveLeft = new NamedGUIAction("move up") {
	    @Override
	    public void doAction() {
		//GUIChart.this.setPlotScaleX((float) GUIChart.this.getPlotScaleX() * 0.99F);
		GUIChart.this.setPlotX((float) GUIChart.this.getPlotX() - (5) * this.getCount());
		GUIChart.super.update();

	    }
	};

	/**
	 * Action for moving the viewed portion of the chart right.
	 */
	NamedGUIAction moveRight = new NamedGUIAction("move right") {
	    @Override
	    public void doAction() {
		//GUIChart.this.setPlotScaleX((float) GUIChart.this.getPlotScaleX() * 0.99F);
		GUIChart.this.setPlotX((float) GUIChart.this.getPlotX() + (5) * this.getCount());
		GUIChart.super.update();

	    }
	};

	/**
	 * Action for forcing the sampling (adding a sample at the current
	 * location) for all the relevant lines.
	 */
	NamedGUIAction sampleAction = new NamedGUIAction("sample line(s)") {
	    @Override
	    public void doAction() {
		//GUIChart.this.setPlotScaleX((float) GUIChart.this.getPlotScaleX() * 0.99F);

		boolean vflag = GUIChart.this.getGUIPanel().getVFlag();
		if (vflag) {

		    for (Map.Entry<Character, PlotLine> entry : GUIChart.this.linesList.entrySet()) {
			PlotLine pl = entry.getValue();
			if (pl.isSelected()) {
			    pl.sample();
			}
		    }
		} else {
		    PlotLine pl = GUIChart.this.linesList.get(GUIChart.this.currentLineChar);
		    if (pl == null) {
			pl = new PlotLine(GUIChart.this.currentLineChar, GUIChart.this);
			GUIChart.this.addLine(pl);
		    }

		    pl.sample();
		}

		GUIChart.super.update();

	    }
	};

	/**
	 * Action for coppying the currently focused line into a given register.
	 *
	 */
	NamedGUIAction yankAction = new NamedGUIAction("yank (copy) current line") {
	    @Override
	    public void doAction() {
		boolean vflag = GUIChart.this.getGUIPanel().getVFlag();
		if (vflag) {

		    StringBuilder sb = new StringBuilder();
		    for (Map.Entry<Character, PlotLine> entry : GUIChart.this.linesList.entrySet()) {
			PlotLine pl = entry.getValue();
			if (pl != null && pl.isSelected()) {
			    sb.append(pl.toString());
			    sb.append("\n");
			}
			GUIChart.this.getGUIPanel().setCurrentRegisterContentAndReset(sb.toString());
		    }
		} else {
		    PlotLine currentLine = GUIChart.this.linesList.get(GUIChart.this.currentLineChar);
		    if (currentLine != null) {
			GUIChart.this.getGUIPanel().setCurrentRegisterContentAndReset(currentLine.toString());
		    }
		}
	    }
	};

	/**
	 * Action for pasting a line from a given register, overwriting it,
	 * creating it, if it doesn't exist yet (ghost was focused).
	 *
	 */
	NamedGUIAction pasteOverwriteAction = new NamedGUIAction("paste with overwriting") {
	    @Override
	    public void doAction() {
		/*
		 ArrayList<Double> test = GUIChart.this.parseDoublesFromString("-1.2,\t 2.3, \t 3.4,  kokon slepice   4.5 10e05, 		50.35, 10.3e10");
		 ArrayList<Double> test2 = GUIChart.this.parseDoublesFromString("2, -3, 4");
		 ArrayList<Double> test3 = GUIChart.this.parseDoublesFromString("-2,3,-4e05");
		 ArrayList<Double> test4 = GUIChart.this.parseDoublesFromString("1 000, -2 000, 3 000");
		 ArrayList<Double> test5 = GUIChart.this.parseDoublesFromString("-1, 000, 000; 2, 000, 000; -3, 000; -5e04");
		 ArrayList<Double> test6 = GUIChart.this.parseDoublesFromString("1,2, -2,3, 3,4, -5,6");
		 ArrayList<Double> test7 = GUIChart.this.parseDoublesFromString("1,2 -2,3 3,4 -5,6");
		 ArrayList<Double> test8 = GUIChart.this.parseDoublesFromString("123,45; 23,232; 343,43");//TODO:FIX THIS
		 */
		String s = GUIChart.this.getGUIPanel().getCurrentRegisterContentAndReset();
		PlotLine pl = new PlotLine(s, currentLineChar, GUIChart.this);
		GUIChart.this.addLine(pl);
		//GUIChart.this.linesList.put(currentLineChar, pl);
		System.out.println("kdak");
	    }
	};
	NamedGUIAction selectOneAction = new NamedGUIAction("select current line") {
	    @Override
	    public void doAction() {
		char c = GUIChart.this.getGUIPanel().getCurrentRegisterLetterAndReset().charAt(0);

		PlotLine currentLine = GUIChart.this.getPlotLineByChar(c);
		if (currentLine != null) {
		    if (!currentLine.isSelected()) {
			currentLine.setSelected(true);
		    } else {
			currentLine.setSelected(false);
		    }
		}
	    }
	};

	/**
	 * Action for selecting/unselecting all lines.
	 */
	NamedGUIAction selectAllAction = new NamedGUIAction("select/deselect all") {
	    @Override
	    public void doAction() {

		boolean atLeastOneWasSelected = false;
		for (Map.Entry<Character, PlotLine> entry : GUIChart.this.linesList.entrySet()) {
		    PlotLine pl = entry.getValue();
		    if (pl.isSelected()) {
			pl.setSelected(false);
			atLeastOneWasSelected = true;
		    }
		}
		if (!atLeastOneWasSelected) {

		    for (Map.Entry<Character, PlotLine> entry : GUIChart.this.linesList.entrySet()) {
			PlotLine pl = entry.getValue();
			pl.setSelected(true);
		    }
		}
	    }
	};

	/**
	 * Action for inverting the current selection (unselecting selected and
	 * selecting unselected).
	 */
	NamedGUIAction invertSelectionAction = new NamedGUIAction("invertSelection") {
	    @Override
	    public void doAction() {

		for (Map.Entry<Character, PlotLine> entry : GUIChart.this.linesList.entrySet()) {
		    PlotLine pl = entry.getValue();
		    if (pl.isSelected()) {
			pl.setSelected(false);
		    } else {
			pl.setSelected(true);
		    }
		}
	    }
	};

	/**
	 * Action for turning the recording of the given lines on or off,
	 * toggling between the two. Can operate on multiple lines when the
	 * vFlag is on.
	 */
	NamedGUIAction toggleRecordingAction = new NamedGUIAction("Toggle recording") {
	    @Override
	    public void doAction() {

		if (this.getGUIPanel().getVFlag()) { //multiple lines selected, should operate on the selected ones
		    for (Map.Entry<Character, PlotLine> entry : GUIChart.this.linesList.entrySet()) {
			PlotLine pl = entry.getValue();
			if (pl.isSelected()) {
			    if (!pl.isBeingRecorded()) {
				pl.reset();
				pl.setRecorded(true);
			    } else {
				pl.setRecorded(false);
			    }
			}
		    }
		} else {
		    PlotLine pl = GUIChart.this.getPlotLineByChar(GUIChart.this.getGUIPanel().getCurrentRegisterLetterAndReset().charAt(0));

		    if (pl == null) {
			pl = new PlotLine(GUIChart.this.currentLineChar, GUIChart.this);
			GUIChart.this.addLine(pl);
		    }
		    if (!pl.isBeingRecorded()) {
			pl.reset();
			pl.setRecorded(true);
		    } else {
			pl.setRecorded(false);
		    }
		}
		boolean isSomeLineRecorded = false;
		for (Map.Entry<Character, PlotLine> entry : GUIChart.this.linesList.entrySet()) {
		    PlotLine pl = entry.getValue();
		    if (pl.isBeingRecorded()) {
			isSomeLineRecorded = true;
			break;
		    }
		}
		if (isSomeLineRecorded) {
		    GUIChart.this.startRecording();
		} else {
		    GUIChart.this.stopRecording();
		}
	    }

	};

	/**
	 * Action for stopping the recording of all lines at once.
	 */
	NamedGUIAction stopAllRecordingAction = new NamedGUIAction("Stop all recording") {
	    @Override
	    public void doAction() {

		if (this.getGUIPanel().getVFlag()) { //multiple lines selected, should operate on the selected ones
		    for (Map.Entry<Character, PlotLine> entry : GUIChart.this.linesList.entrySet()) {
			PlotLine pl = entry.getValue();
			pl.setRecorded(false);
		    }
		}
		GUIChart.this.stopRecording();
	    }

	};

	/**
	 * Action for autoscaling the selection along the Y axis.
	 */
	NamedGUIAction autoScaleAction = new NamedGUIAction("Autoscale selection along Y") {
	    @Override
	    public void doAction() {
		autoScaleY();
	    }

	};

	/**
	 * Action for autoscaling the selection along the X axis.
	 */
	NamedGUIAction autoScaleXAction = new NamedGUIAction("Autoscale selection along X") {
	    @Override
	    public void doAction() {
		autoScaleX();
	    }

	};

	this.setMenu(new Menu(gut.getGUIPanel(), "slider menu", true));
	this.getMenu().addAction("a", autoScaleAction);
	this.getMenu().addAction("z", autoScaleXAction);
	this.getMenu().addAction("l", scaleXUpAction);
	this.getMenu().addAction("h", switchViewModeAction);
	//this.getMenu().addAction("j", scaleYUpAction);
	//this.getMenu().addAction("k", scaleYDownAction);

	this.getMenu().addAction("J", nextLine);
	this.getMenu().addAction("K", prevLine);
	this.getMenu().addAction("j", nextExistingLine);
	this.getMenu().addAction("k", prevExistingLine);
	GUIPanel gp = gut.getGUIPanel();
	Menu yankMenu = new Menu(gp, "yank (copy)", false);
	yankMenu.addAction("y", yankAction);
	this.getMenu().addSubMenu("y", yankMenu);
	Menu recordingMenu = new Menu(gp, "recording", false);
	recordingMenu.addAction("r", toggleRecordingAction);
	recordingMenu.addAction("s", stopAllRecordingAction);
	this.getMenu().addSubMenu("r", recordingMenu);
	this.getMenu().addAction("s", sampleAction);
	Menu selectionMenu = new Menu(gp, "visually select", false);
	selectionMenu.addAction("v", selectOneAction);
	selectionMenu.addAction("a", selectAllAction);
	selectionMenu.addAction("i", invertSelectionAction);
	this.getMenu().addSubMenu("v", selectionMenu);

	Menu pasteMenu = new Menu(gp, "paste", false);
	pasteMenu.addAction("p", pasteOverwriteAction);
	Menu moveMenu = new Menu(gut.getGUIPanel(), "move", true);
	moveMenu.addAction("j", moveDown);
	moveMenu.addAction("k", moveUp);
	moveMenu.addAction("l", moveRight);
	moveMenu.addAction("h", moveLeft);
	moveMenu.addAction("w", scaleYUpAction);
	moveMenu.addAction("s", scaleYDownAction);
	moveMenu.addAction("a", scaleXDownAction);
	moveMenu.addAction("d", scaleXUpAction);
	this.getMenu().addSubMenu("m", moveMenu);
	this.getMenu().addSubMenu("p", pasteMenu);

    }

    /**
     * Autoscales the chart along the X axis. This means the currently visible
     * local minimum in X will be taken, the currently visible local maximum in
     * X will be taken and the chart will be repositioned and scaled in such a
     * way, that these 2 are positioned on the screen with left and right
     * margins, specified by the autoScalePaddingCoeff constant.
     */
    public void autoScaleX() {

	double maxDrawnY = (GUIChart.this.getPlotY()) / GUIChart.this.getPlotScaleY();
	double minDrawnY = -(GUIChart.this.getHeight() - (GUIChart.this.getPlotY())) / GUIChart.this.getPlotScaleY();
	System.out.println("minDrawnY: " + minDrawnY);
	System.out.println("maxDrawnY: " + maxDrawnY);
	//double minDrawnX = -GUIChart.this.getPlotX() * GUIChart.this.getPlotScaleX();//minimum value of x displayed in chart at current scale
	//double maxDrawnX = (-GUIChart.this.getPlotX() + GUIChart.this.getWidth()) * GUIChart.this.getPlotScaleX();//maximum value of x displayed in chart at current scale
	ArrayList<PlotLine> theList = new ArrayList<>();

	if (this.getGUIPanel().getVFlag()) { //multiple lines selected, should operate on the selected ones
	    for (Map.Entry<Character, PlotLine> entry : GUIChart.this.linesList.entrySet()) {
		PlotLine pl = entry.getValue();
		if (pl.isSelected()) {
		    theList.add(pl);
		}
	    }
	} else {
	    PlotLine pl = GUIChart.this.getPlotLineByChar(GUIChart.this.getGUIPanel().getCurrentRegisterLetterAndReset().charAt(0));
	    theList.add(pl);
	}
	GUIChart.this.autoScaleXForRange(theList, minDrawnY, maxDrawnY);

    }

    /**
     * Autoscales the chart along the Y axis. This means the currently visible
     * local minimum in Y will be taken, the currently visible local maximum in
     * Y will be taken and the chart will be repositioned and scaled in such a
     * way, that these 2 are positioned on the screen with up and down margins,
     * specified by the autoScalePaddingCoeff constant.
     */
    public void autoScaleY() {
	double minDrawnX = -(GUIChart.this.getPlotX()) / GUIChart.this.getPlotScaleX();
	double maxDrawnX = (GUIChart.this.getWidth() - (GUIChart.this.getPlotX())) / GUIChart.this.getPlotScaleX();
	//double minDrawnX = -GUIChart.this.getPlotX() * GUIChart.this.getPlotScaleX();//minimum value of x displayed in chart at current scale
	//double maxDrawnX = (-GUIChart.this.getPlotX() + GUIChart.this.getWidth()) * GUIChart.this.getPlotScaleX();//maximum value of x displayed in chart at current scale
	ArrayList<PlotLine> theList = new ArrayList<>();

	if (this.getGUIPanel().getVFlag()) { //multiple lines selected, should operate on the selected ones
	    for (Map.Entry<Character, PlotLine> entry : GUIChart.this.linesList.entrySet()) {
		PlotLine pl = entry.getValue();
		if (pl.isSelected()) {
		    theList.add(pl);
		}
	    }
	} else {
	    PlotLine pl = GUIChart.this.getPlotLineByChar(GUIChart.this.getGUIPanel().getCurrentRegisterLetterAndReset().charAt(0));
	    theList.add(pl);
	}
	GUIChart.this.autoScaleYForRange(theList, minDrawnX, maxDrawnX);

    }

    /**
     * Adds a data point to all selected lines on their respective cursor
     * positions (if there are any selected and the vFlag is on), or to the
     * currently focused line otherwise. Then optionally performs autoscaling.
     * Whether or not it does that and how exactly will it perform the
     * autoscale, depends on the AutoScaleMode property.
     */
    public void sampleAllRelevant() {
	for (Map.Entry<Character, PlotLine> entry : GUIChart.this.linesList.entrySet()) {
	    PlotLine pl = entry.getValue();
	    if (pl.isBeingRecorded()) {
		pl.sample();
	    }
	}
	int autoScaleMode = (int) this.getPropertyByName("AutoScaleMode").getValue();
	if (autoScaleMode == 2) {
	    Platform.runLater(() -> {
		autoScaleY();
	    });
	}
	if (autoScaleMode == 3) {
	    Platform.runLater(new Runnable() {
		public void run() {
		    autoScaleX();
		    autoScaleY();
		}

	    });
	}
    }

    @Override
    void sendMouseScroll(ScrollEvent event) {
	double deltaY = event.getDeltaY();
	double eventX = event.getSceneX();
	double eventY = event.getSceneY();
	FloatPoint fp = this.getLastPositionDrawnTo();
	double amount = 0.01;
	if (eventX > fp.x && eventX < fp.x + this.getWidth() && eventY > fp.y && eventY < fp.y + this.getHeight())//is within bounds
	{
	    this.scaleAlongOrigin(1 + deltaY * amount, 1 + deltaY * amount, eventX - fp.x, eventY - fp.y);
	}
	this.requestRepaint();
    }

    public void increaseValue(boolean forward, boolean fast) {
	byte increase = 1;
	if (fast) {
	    increase = 10;
	}
	if (!forward) {
	    increase *= -1;
	}
	this.setValue(this.getValue() + increase);
	System.out.println(this.getValue());
	super.update();
    }

    /**
     * Scales the chart by amount, where amount is expressed as percents
     * normalised to 1 (1= keep current scale).
     *
     * @param amountX amount of scale along the X axis in percent normalised to
     * 1. 0.5=scale to half; 0.9: scale to 90%; 1=keep current size
     * @param amountY amount of scale along the Y axis in percent normalised to
     * 1. 0.5=scale to half; 0.9: scale to 90%; 1=keep current size
     * @param originX X coordinate of the origin point to perform the transform
     * around; expressed in pixels, in coordinates relative to the top left
     * corner of the GUIChart element.
     * @param originY Y coordinate of the origin point to perform the transform
     * around; expressed in pixels, in coordinates relative to the top left
     * corner of the GUIChart element.
     */
    public void scaleAlongOrigin(double amountX, double amountY, double originX, double originY) {
	double originXInPixelsWithRespectToPlotZero = (originX - this.getPlotX());
	double originYInPixelsWithRespectToPlotZero = (originY - this.getPlotY());
	double newOriginXBeforeCorrection = (originXInPixelsWithRespectToPlotZero * amountX + this.getPlotX());//where the OriginX would be after the scale with respect to the coordinate system of the GUIChart left top corner
	double newOriginYBeforeCorrection = (originYInPixelsWithRespectToPlotZero * amountY + this.getPlotY());

	this.setPlotScaleX((float) (this.getPlotScaleX() * amountX));
	this.setPlotScaleY((float) (this.getPlotScaleY() * amountY));
	this.setPlotX((float) (this.getPlotX() - (newOriginXBeforeCorrection - originX)));
	this.setPlotY((float) (this.getPlotY() - (newOriginYBeforeCorrection - originY)));
	System.out.println("Origin: " + originX);
	System.out.println("newC: " + newOriginXBeforeCorrection);
	System.out.println("Ax: " + this.getPlotX());
	//System.out.println("Y: "+this.getPlotY());
    }

    public void paint(GraphicsContext gc, double x, double y) {
	super.paint(gc, x, y);
	gc.setFill(Color.GREENYELLOW);
	if (getRegister() != null) {
	    gc.strokeText(getRegister().getName().toString(), x, y + 20);
	}
	gc.fillRect(x, y, this.getWidth(), this.getHeight());
	gc.setFill(this.getColor1());
	gc.fillRect(x, y, this.getWidth(), this.getHeight());
	//gc.fillRect(x+this.getWidth(), y+this.getHeight()/3,this.getWidth()*(2/3), this.getHeight()*(2/3));
	if (histogramView) {
	    paintHistograms(gc, x, y, x + this.getWidth(), y + this.getHeight());//TODO: limit amount of calls to getWidth and getHeight
	} else {
	    paintLines(gc, x, y, x + this.getWidth(), y + this.getHeight());//TODO: limit amount of calls to getWidth and getHeight
	}
	paintTicks(gc, x, y, x + this.getWidth(), y + this.getHeight());
	drawLegend(gc, x + this.getWidth() - 50, y + 50);
    }

    @Override
    public GUIelement makeCopy() {
	GUIChart cb = new GUIChart();
	this.copyPropertiesTo(cb);
	return cb;
    }

    /**
     * Get a PlotLine object, provided a char. If this char is '%', return the
     * currently focused line.
     *
     * @param c the letter of the line to be returned
     * @return line, which has the letter provided by the c
     */
    public PlotLine getPlotLineByChar(char c) {
	if (c == '%') {
	    return this.linesList.get(this.currentLineChar);
	} else {
	    return this.linesList.get(c);
	}
    }

    /**
     * Draws a rectangle on a GraphicsContext gc, clamping its size between
     * minX, minY and maxX, maxY. Use fillcolor and stroke color to determine
     * the color of the fill and the outline. Use Color.TRANSPARENT to not draw
     * any outline or fill.
     *
     * @param gc - the graphic context to draw to
     * @param x1 - the X position of the upper left corner of the rectangle.
     * @param y1 - the Y position of the upper left corner of the rectangle.
     * @param x2 - the X position of the lower right corner of the rectangle
     * @param y2 - the Y position of the lower right corner of the rectangle
     * @param minX - the minimum X position to draw to. The part of the
     * rectangle left to this X will get cropped (won't display).
     * @param minY - the minimum Y position to draw to. The part of the
     * rectangle down to this Y will get cropped (won't display).
     * @param maxX - the maximum X position to draw to. The part of the
     * rectangle right to this X will get cropped (won't display).
     * @param maxY - the maximum Y position to draw to. The part of the
     * rectangle up to this Y will get cropped (won't display).
     * @param fillCol - the color to use for the fill
     * @param strokeCol - the color to use for the stroke (outline)
     */
    private void strokeBorderedRectangle(GraphicsContext gc, double x1, double y1, double x2, double y2, double minX, double minY, double maxX, double maxY, Color fillCol, Color strokeCol) {
	FloatPoint p1 = new FloatPoint(x1, y1);
	FloatPoint p2 = new FloatPoint(x2, y2);

	FloatPoint topLeftCorner = new FloatPoint(minX, minY);
	FloatPoint bottomRightCorner = new FloatPoint(maxX, maxY);

	FloatPoint leftPoint = FloatPoint.getLeftMostPoint(p1, p2);
	FloatPoint rightPoint = FloatPoint.getRightMostPoint(p1, p2);
	FloatPoint upPoint = FloatPoint.getUpMostPoint(p1, p2);
	FloatPoint downPoint = FloatPoint.getDownMostPoint(p1, p2);

	leftPoint = FloatPoint.getRightMostPoint(leftPoint, topLeftCorner);
	rightPoint = FloatPoint.getLeftMostPoint(rightPoint, bottomRightCorner);

	upPoint = FloatPoint.getDownMostPoint(upPoint, topLeftCorner);
	downPoint = FloatPoint.getUpMostPoint(downPoint, bottomRightCorner);

	gc.setFill(fillCol);
	gc.setStroke(strokeCol);
	gc.fillRect(leftPoint.x, upPoint.y, rightPoint.x - leftPoint.x, downPoint.y - upPoint.y);
	gc.setLineWidth(4);
	gc.strokeRect(leftPoint.x, upPoint.y, rightPoint.x - leftPoint.x, downPoint.y - upPoint.y);
	//gc.strokeRect(200,100,-200,200);

    }

    @Override
    public int getHeight() {
	int preferredHeight = ((IntegerProperty) this.getPropertyByName("Height")).getValueSilent();
	int preferredWidth = ((IntegerProperty) this.getPropertyByName("Width")).getValueSilent();
	return (int) (((double) getWidth() / preferredWidth) * preferredHeight);
    }

    @Override
    public int getWidth() {
	int preferredWidth = ((IntegerProperty) this.getPropertyByName("Width")).getValueSilent();
	return Math.max(preferredWidth, (int) (this.getGUIPanel().getCanvas().getWidth() * 0.8));
    }

    /**
     * Draws a line on a GraphicsContext gc, clamping its size between minX,
     * minY and maxX, maxY. Use fillcolor and stroke color to determine the
     * color of the fill and the outline. Use Color.TRANSPARENT to not draw any
     * outline or fill.
     *
     * @param gc - the graphic context to draw to
     * @param x1 - the X position of the upper left point of the line
     * @param y1 - the Y position of the upper left point of the line
     * @param x2 - the X position of the lower right point of the line
     * @param y2 - the Y position of the lower right point of the line
     * @param minX - the minimum X position to draw to. The part of the line
     * left to this X will get cropped (won't display).
     * @param minY - the minimum Y position to draw to. The part of the line
     * down to this Y will get cropped (won't display).
     * @param maxX - the maximum X position to draw to. The part of the line
     * right to this X will get cropped (won't display).
     * @param maxY - the maximum Y position to draw to. The part of the line up
     * to this Y will get cropped (won't display).
     */
    private void strokeBorderedLine(GraphicsContext gc, double x1, double y1, double x2, double y2, double minX, double maxX, double minY, double maxY) {
	double xSmaller = Math.min(x1, x2);
	double xBigger = Math.max(x1, x2);

	FloatPoint p1 = new FloatPoint(x1, y1);
	FloatPoint p2 = new FloatPoint(x2, y2);

	FloatPoint leftPoint = FloatPoint.getLeftMostPoint(p1, p2);
	FloatPoint rightPoint = FloatPoint.getRightMostPoint(p1, p2);
	FloatPoint upPoint = FloatPoint.getUpMostPoint(p1, p2);
	FloatPoint downPoint = FloatPoint.getDownMostPoint(p1, p2);
	if (upPoint == null || downPoint == null || leftPoint == null || rightPoint == null) {
	    System.out.println("kokodak");
	    return;
	}

	if (leftPoint.x > minX && rightPoint.x < maxX && upPoint.y > minY && downPoint.y < maxY) {
	    gc.strokeLine(leftPoint.x, leftPoint.y, rightPoint.x, rightPoint.y);
	} else {
	    FloatPoint vert;//point of intersection between the line being drawn and vertical lines
	    FloatPoint hor;

	    if (leftPoint.x < minX && rightPoint.x > minX)//outside the left border
	    {
		double yIntersect = findVertLineIntersect(x1, y1, x2, y2, minX);
		if (yIntersect > minY && yIntersect < maxY) {
		    gc.strokeLine(minX, yIntersect, rightPoint.x, rightPoint.y);
		    gc.setFill(Color.AQUA);
		    gc.fillOval(minX - 2, yIntersect - 2, 4, 4);
		}
	    }
	    if (leftPoint.x < maxX && rightPoint.x > maxX) {

		double yIntersect = findVertLineIntersect(x1, y1, x2, y2, maxX);
		if (yIntersect > minY && yIntersect < maxY) {
		    gc.strokeLine(maxX, yIntersect, leftPoint.x, leftPoint.y);
		    gc.setFill(Color.AQUA);
		    gc.fillOval(maxX - 2, yIntersect - 2, 4, 4);
		}
	    }
	    if (upPoint.y < minY && downPoint.y > minY) {
		double xIntersect = findHorLineIntersect(x1, y1, x2, y2, minY);
		if (xIntersect > minX && xIntersect < maxX) {
		    gc.strokeLine(xIntersect, minY, downPoint.x, downPoint.y);
		    gc.setFill(Color.AQUA);
		    gc.fillOval(xIntersect - 2, minY - 2, 4, 4);
		}
	    }
	    if (upPoint.y < maxY && downPoint.y > maxY) {
		double xIntersect = findHorLineIntersect(x1, y1, x2, y2, maxY);
		if (xIntersect > minX && xIntersect < maxX) {
		    gc.strokeLine(xIntersect, maxY, upPoint.x, upPoint.y);
		    gc.setFill(Color.AQUA);
		    gc.fillOval(xIntersect - 2, maxY - 2, 4, 4);
		}
	    }

	}
    }

    /**
     * Finds the Y coordinate of an intersection of a line, specified by two
     * points (x1, y1, x2, y2) with a vertical line, points of which have a
     * given X.
     *
     * @param x1 - the x coordinate of the first point, which determines the
     * line
     * @param y1 - the x coordinate of the first point, which determines the
     * line
     * @param x2 - the x coordinate of the first point, which determines the
     * line
     * @param y2 - the x coordinate of the first point, which determines the
     * line
     * @param lineX - the x coordinate of all the points, that define the
     * vertcal line.
     * @return the X coordinate of an intersection of a line, specified by two
     * points (x1, y1, x2, y2) with a
     */
    double findVertLineIntersect(double x1, double y1, double x2, double y2, double lineX) {
	double A = (y1 - y2) / (x1 - x2);
	double B = y1 - A * x1;

	return A * lineX + B;
    }

    /**
     * Finds the X coordinate of an intersection of a line, specified by two
     * points (x1, y1, x2, y2) with a horizontal line, points of which have a
     * given Y.
     *
     * @param x1 - the x coordinate of the first point, which determines the
     * line
     * @param y1 - the x coordinate of the first point, which determines the
     * line
     * @param x2 - the x coordinate of the first point, which determines the
     * line
     * @param y2 - the x coordinate of the first point, which determines the
     * line
     * @param lineY - the y coordinate of all the points, that define the
     * horizontal line.
     * @return the X coordinate of an intersection of a line, specified by two
     * points (x1, y1, x2, y2) with a horizontal line, points of which have a
     * given Y.
     */
    double findHorLineIntersect(double x1, double y1, double x2, double y2, double lineY) {
	double A = (y1 - y2) / (x1 - x2);
	double B = y1 - A * x1;

	return (lineY - B) / A;
    }

    /**
     * Get the PlotX property, used to determine the X position of the chart
     * origin the point at which the axis are (0, 0)
     *
     * @return the PlotX property, used to determine the X position of the chart
     * origin the point at which the axis are (0, 0)
     */
    double getPlotX() {
	FloatProperty fp = (FloatProperty) this.getPropertyByName("PlotX");
	return fp.getValue();
    }

    /**
     * Get the PlotY property, used to determine the X position of the chart
     * origin the point at which the axis are (0, 0)
     *
     * @return the PlotY property, used to determine the X position of the chart
     * origin the point at which the axis are (0, 0)
     */
    double getPlotY() {
	FloatProperty fp = (FloatProperty) this.getPropertyByName("PlotY");
	return fp.getValue();
    }

    /**
     * Set the PlotX property, used to determine the X position of the chart
     * origin the point at which the axis are (0, 0)
     */
    void setPlotX(float x) {
	this.getPropertyByName("PlotX").setValue(x);
    }

    /**
     * Set the PlotY property, used to determine the Y position of the chart
     * origin the point at which the axis are (0, 0)
     */
    void setPlotY(float y) {
	this.getPropertyByName("PlotY").setValue(y);
    }

    /**
     * Get the PlotScaleX property, used to determine how many pixels correspond
     * to a step of size 1 on the X axis.
     *
     * @return the PlotScaleX property, used to determine how many pixels
     * correspond to a step of size 1 on the X axis.
     */
    double getPlotScaleX() {
	FloatProperty fp = (FloatProperty) this.getPropertyByName("PlotScaleX");
	return fp.getValue();
    }

    /**
     * Get the PlotScaleY property, used to determine how many pixels correspond
     * to a step of size 1 on the Y axis.
     *
     * @return the PlotScaleY property, used to determine how many pixels
     * correspond to a step of size 1 on the Y axis.
     */
    double getPlotScaleY() {
	FloatProperty fp = (FloatProperty) this.getPropertyByName("PlotScaleY");
	return fp.getValue();
    }

    /**
     * Set the PlotScaleX property, used to determine how many pixels correspond
     * to a step of size 1 on the X axis.
     */
    void setPlotScaleX(float x) {
	this.getPropertyByName("PlotScaleX").setValue(x);
    }

    /**
     * Set the PlotScaleY property, used to determine how many pixels correspond
     * to a step of size 1 on the Y axis.
     *
     */
    void setPlotScaleY(float y) {
	this.getPropertyByName("PlotScaleY").setValue(y);
    }

    /**
     * Automatically scale the view along the X axis so that the given lines fit
     * in. Will scale the X axis to correctly position the local minimum and
     * maximum in X axis, located between the provided minY and maxY.
     *
     * @param lines - the collection of lines
     * @param minY - the minimum Y of the interval, in which a local X minimum
     * should be found.
     * @param maxY - the minimum Y of the interval, in which a local X maximum
     * should be found.
     */
    //TODO: some refactoring would come in handy, as code duplication is always bad
  private  void autoScaleXForRange(Collection<PlotLine> lines, double minY, double maxY) {
	double maxX = Double.NEGATIVE_INFINITY;
	double minX = Double.POSITIVE_INFINITY;

	if (this.histogramView) {
	    //minX = 0;

	    for (PlotLine pl : lines) {
		minX = pl.getHistogramMin();
		maxX = pl.getHistogramMax();
	    }
	} else {
	    int totalPointsCount = 0;
	    for (PlotLine pl : lines) {
		for (FloatPoint fp : pl.getPoints().values()) {
		    totalPointsCount++;
		    if (fp.y > minY && fp.y < maxY) {
			if (fp.x > maxX) {
			    maxX = fp.x;
			}
			if (fp.x < minX) {
			    minX = fp.x;
			}
		    }
		}
	    }
	    if (totalPointsCount < 2) {
		return;
	    }
	}
	if (maxX == Double.NEGATIVE_INFINITY || minX == Double.POSITIVE_INFINITY)//this can happen when the chart is outside the view and user wants to autoscale
	{
	    return;
	}

	if (Math.abs(maxX - minX) < 0.000001) {
	    return;
	}
	//so now we found the local max and min in Y between minX and maxX

	double currentPixelMinMaxSpan = (maxX - minX) * this.getPlotScaleX();//the distance in pixels between the min and max X found between minY and maxY
	double targetPixelMinMaxSpan = this.getWidth() * autoScalePaddingCoeff;
	this.setPlotScaleX((float) (this.getPlotScaleX() * (targetPixelMinMaxSpan / currentPixelMinMaxSpan)));
	double maxXOnChart = this.getPlotX() + (maxX * this.getPlotScaleX());//the x position of the local maximum, in pixels, relative to the chart coordinate system
	if (Float.isNaN((float) (this.getPlotX() + ((this.getWidth() * ((1 - autoScalePaddingCoeff) / 2) - maxXOnChart))))) {
	    System.out.println("kvak ptak");
	}
	this.setPlotX((float) (this.getPlotX() - (maxXOnChart - this.getWidth()) - this.getWidth() * ((1 - autoScalePaddingCoeff)) / 2));

	//double maxY=Double.NEGATIVE_INFINITY;
    }

    /**
     * Automatically scale the view along the Y axis so that the given lines fit
     * in. Will scale the Y axis to correctly position the local minimum and
     * maximum in Y axis, located between the provided minX and maxX.
     *
     * @param lines - the collection of lines
     * @param minX - the minimum X of the interval, in which a local Y minimum
     * should be found.
     * @param maxX - the minimum X of the interval, in which a local Y maximum
     * should be found.
     */
    void autoScaleYForRange(Collection<PlotLine> lines, double minX, double maxX) {
	double maxY = Double.NEGATIVE_INFINITY;
	double minY = Double.POSITIVE_INFINITY;

	if (this.histogramView) {
	    minY = 0;

	    for (PlotLine pl : lines) {
		ArrayList<Integer> bins = pl.getHistogramBins();
		for (int val : bins) {
		    if (val > maxY) {
			maxY = val;
		    }
		}
	    }
	} else {
	    int totalPointsCount = 0;
	    for (PlotLine pl : lines) {
		for (FloatPoint fp : pl.getPoints().values()) {
		    totalPointsCount++;
		    if (fp.x > maxX)//pl.getPoints returns an ordered treemap, so no need to iterate further
		    {
			break;
		    }
		    if (fp.x > minX && fp.x < maxX) {//technically, the "x<maxX" is duplicate; but I bet this will be optimized by javac. So it's kept for the sake of readability. 
			if (fp.y > maxY) {
			    maxY = fp.y;
			}
			if (fp.y < minY) {
			    minY = fp.y;
			}
		    }
		}
	    }
	    if (totalPointsCount < 2) {
		return;
	    }
	}
	if (maxY == Double.NEGATIVE_INFINITY || minY == Double.POSITIVE_INFINITY)//this can happen when the chart is outside the view and user wants to autoscale
	{
	    return;
	}
	if (Math.abs(maxY - minY) < 0.000001) {
	    return;
	}
	//so now we found the local max and min in Y between minX and maxX

	double currentPixelMinMaxSpan = (maxY - minY) * this.getPlotScaleY();//the distance in pixels between the min and max Y found between minX and maxX
	double targetPixelMinMaxSpan = this.getHeight() * autoScalePaddingCoeff;
	this.setPlotScaleY((float) (this.getPlotScaleY() * (targetPixelMinMaxSpan / currentPixelMinMaxSpan)));
	double maxYOnChart = this.getPlotY() - (maxY * this.getPlotScaleY());//the y position of the local maximum, in pixels, relative to the chart coordinate system
	if (Float.isNaN((float) (this.getPlotY() + ((this.getHeight() * ((1 - autoScalePaddingCoeff) / 2) - maxYOnChart))))) {
	    System.out.println("kvak ptak");
	}
	this.setPlotY((float) (this.getPlotY() + ((this.getHeight() * ((1 - autoScalePaddingCoeff) / 2) - maxYOnChart))));

	//double maxY=Double.NEGATIVE_INFINITY;
    }

    /**
     * Paint a single line pl on the provided GraphicsContext gc, with a Color
     * c, line width lineWidth, clamped by minX, minY, maxX, maxY
     *
     * @param pl the PlotLine object to draw
     * @param gc the GraphicsContext to draw to
     * @param c the color to draw the line with
     * @param lineWidth the width of the line to draw
     * @param minX - the minimum X to draw to. The drawn line will get clamped
     * by this coordinate.
     * @param minY - the minimum X to draw to. The drawn line will get clamped
     * @param maxX - the minimum X to draw to. The drawn line will get clamped
     * @param maxY - the minimum X to draw to. The drawn line will get clamped
     */
    public void paintSingleLine(PlotLine pl, GraphicsContext gc, Color c, double lineWidth, double minX, double minY, double maxX, double maxY) {
	if (pl == null) {
	    return;
	}

	if (!pl.getPoints().isEmpty()) {
	    FloatPoint fp1 = pl.getPoints().firstEntry().getValue();
	    for (FloatPoint fp : pl.getPoints().values()) {
		if (Double.isNaN(-fp1.y * this.getPlotScaleY() + minY + getPlotY())) {
		    System.out.println("pipka kdaka");
		}
		//gc.setLineWidth(4);
		double lwBackup = gc.getLineWidth();
		gc.setLineWidth(lineWidth);
		gc.setStroke(c);
		strokeBorderedLine(gc, fp1.x * this.getPlotScaleX() + minX + getPlotX(), -fp1.y * this.getPlotScaleY() + minY + getPlotY(), fp.x * this.getPlotScaleX() + minX + getPlotX(), -fp.y * this.getPlotScaleY() + minY + getPlotY(), minX, maxX, minY, maxY);
		paintCursor(gc, c, pl.getCursorX() * this.getPlotScaleX() + getPlotX() + minX, -pl.getCursorY() * this.getPlotScaleY() + getPlotY() + minY);
		fp1 = fp;
		gc.setLineWidth(lwBackup);
	    }
	}
    }

    /**
     * Paint all visible lines, clamping the parts outside the ranges determined
     * by minX, minY, maxX, maxY
     *
     * @param gc - the GraphicsContext to draw to
     * @param minX - the minimum X to draw to. The drawn line will get clamped
     * by this coordinate.
     * @param minY - the minimum X to draw to. The drawn line will get clamped
     * @param maxX - the minimum X to draw to. The drawn line will get clamped
     * @param maxY - the minimum X to draw to. The drawn line will get clamped
     */
    public void paintLines(GraphicsContext gc, double minX, double minY, double maxX, double maxY) {
	for (Map.Entry<Character, PlotLine> entry : this.linesList.entrySet()) {
	    PlotLine pl = entry.getValue();
	    if (pl.isSelected()) {
		paintSingleLine(pl, gc, Color.rgb(255, 255, 0, 0.75), pl.getLineWidth() * 2 + 2, minX, minY, maxX, maxY);
	    }
	    paintSingleLine(pl, gc, pl.getColor(), pl.getLineWidth(), minX, minY, maxX, maxY);
	    //gc.setStroke(pl.getColor());
	}
	PlotLine pl = this.getLine("%", false);
	if (pl != null) {
	    paintSingleLine(pl, gc, Color.rgb(0, 255, 0, 0.75), pl.getLineWidth() * 2 + 2, minX, minY, maxX, maxY);
	    paintSingleLine(pl, gc, pl.getColor(), pl.getLineWidth(), minX, minY, maxX, maxY);
	}
    }

    /**
     * Paint the line cursor, on position defined by x,y, with color c, to the
     * GraphicsContext gc. If the position is outside this chart, ignore (return
     * immediatelly).
     *
     * @param gc
     * @param c
     * @param x
     * @param y
     */
    public void paintCursor(GraphicsContext gc, Color c, double x, double y) {
	if (!isWithinBounds(x, y)) {
	    return;
	}
	//y = -y;
	gc.setLineWidth(2);
	gc.setStroke(c);
	gc.strokeLine(x - 2, y, x - 6, y);
	gc.strokeLine(x + 2, y, x + 6, y);
	gc.strokeLine(x, y - 2, x, y - 6);
	gc.strokeLine(x, y + 2, x, y + 6);
    }

    /**
     * Paint a single histogram, stored in the PlotLine pl, as a series of
     * rectangles, given a fill color and a stroke color and also minimum and
     * maximum positions, beyond which the histogram should never be drawn.
     *
     * @param gc - the GraphicsContext to draw to
     * @param pl - the PlotLine to extract the Histogram from
     * @param fillCol - the color, with which the bins in the histogram should
     * be filled
     * @param strokeCol - the color, with which the outline of the histogram
     * should be stroked
     * @param minX - the minimum X position to draw to. The part of the
     * rectangle left to this X will get cropped (won't display).
     * @param minY - the minimum Y position to draw to. The part of the
     * rectangle down to this Y will get cropped (won't display).
     * @param maxX - the maximum X position to draw to. The part of the
     * rectangle right to this X will get cropped (won't display).
     * @param maxY - the maximum Y position to draw to. The part of the
     * rectangle up to this Y will get cropped (won't display).
     */
    public void paintSingleHistogram(GraphicsContext gc, PlotLine pl, Color fillCol, Color strokeCol, double minX, double minY, double maxX, double maxY) {

	if (!pl.getHistogramBins().isEmpty()) {
	    double binWidth = Math.abs(pl.getHistogramMax() - pl.getHistogramMin()) / pl.getHistogramBinsCount();
	    //FloatPoint fp1 = pl.getPoints().get(0);
	    double currentRectangleStart = pl.getHistogramMin();
	    for (int i : pl.getHistogramBins()) {
		double x1 = minX + getPlotX() + currentRectangleStart * this.getPlotScaleX();
		double x2 = x1 + binWidth * this.getPlotScaleX();
		double y1 = getPlotY() + minY;
		double y2 = y1 - i * getPlotScaleY();
		//Color col = fillCol;//pl.getColor();
		//col = col.deriveColor(0, 1, 1, opacity);
		strokeBorderedRectangle(gc, x1, y1, x2, y2, minX, minY, maxX, maxY, fillCol, strokeCol);

		currentRectangleStart += binWidth;
	    }
	}
    }

    /**
     * Paint histograms for all of the lines, currently visible.
     *
     * @see paintSingleHistogram
     * @param gc
     * @param minX - the minimum X position to draw to. The part of the
     * rectangle left to this X will get cropped (won't display).
     * @param minY - the minimum Y position to draw to. The part of the
     * rectangle down to this Y will get cropped (won't display).
     * @param maxX - the maximum X position to draw to. The part of the
     * rectangle right to this X will get cropped (won't display).
     * @param maxY - the maximum Y position to draw to. The part of the
     * rectangle up to this Y will get cropped (won't display).
     */
    public void paintHistograms(GraphicsContext gc, double minX, double minY, double maxX, double maxY) {

	ArrayList<PlotLine> linesToDrawList = new ArrayList<>();
	for (Map.Entry<Character, PlotLine> entry : this.linesList.entrySet()) {
	    PlotLine pl = entry.getValue();
	    if (pl.isVisible()) {
		linesToDrawList.add(pl);
	    }
	}

	double opacity = (double) 1 / linesToDrawList.size();

	for (PlotLine pl : linesToDrawList) {
	    Color strokeCol = pl.getColor();
	    Color fillCol = strokeCol.deriveColor(0, 1, 1, opacity);
	    if (pl.isSelected()) {
		strokeCol = Color.YELLOW;
	    }
	    paintSingleHistogram(gc, pl, fillCol, strokeCol, minX, minY, maxX, maxY);
	}
	PlotLine currentLine = this.getPlotLineByChar(currentLineChar);
	Color fillCol = Color.TRANSPARENT;
	Color strokeCol = Color.GREENYELLOW;
	paintSingleHistogram(gc, currentLine, fillCol, strokeCol, minX, minY, maxX, maxY);
    }

    /**
     * Draws the letters of the individual lines, with additional info (such as,
     * whether those lines are selected, visible, whether they exist and more).
     * The position on which this legend should be drawn is dictated by the x
     * and y params. The legend then gets drawn to the provided GraphicsContext
     * gc.
     *
     * @param gc - the GraphicsContext to draw to
     * @param x - the x position, at which the upper left point of the legend
     * text should be located
     * @param y - the y position, at which the upper left point of the legend
     * text should be located
     */
    public void drawLegend(GraphicsContext gc, double x, double y) {
	int i = 0;
	for (Map.Entry<Character, PlotLine> entry : this.linesList.entrySet()) {
	    PlotLine pl = entry.getValue();
	    gc.setStroke(pl.getColor());
	    gc.strokeText((pl.getCharacter() == this.currentLineChar ? ">" : "") + pl.getCharacter() + ": " + ((pl.isVisible()) ? "" : "(H)") + (pl.isSelected() ? "(V)" : ""), x, y + i);
	    i += 10;
	}
	if (this.linesList.get(this.currentLineChar) == null)//currently selected is a ghost
	{
	    gc.strokeText(">" + this.currentLineChar + "(G): (ghost)", x, y + i);
	}
    }

    /**
     * Paint the chart axes with the ticks. The density in which the ticks
     * should be painted is calculated automatically.
     *
     * @param gc the GraphicsContext to which the axes should be drawn
     * @param minX the minimum X, determining the bounds, beyond which nothing
     * should be drawn
     * @param minY the minimum Y, determining the bounds, beyond which nothing
     * should be drawn
     * @param maxX the maximum X, determining the bounds, beyond which nothing
     * should be drawn
     * @param maxY the maximum Y, determining the bounds, beyond which nothing
     * should be drawn
     */
    public void paintTicks(GraphicsContext gc, double minX, double minY, double maxX, double maxY) {

	double pixelXTickSize = (this.getPlotScaleX() * currentXTickSize);
	double pixelYTickSize = (this.getPlotScaleY() * currentYTickSize);

	double currentScaleX = this.getPlotScaleX();

	if (histogramView) {
	    PlotLine currentLine = this.getPlotLineByChar('%');
	    ArrayList<PlotLine> linesToDrawList = new ArrayList<>();

	    for (PlotLine pl2 : this.linesList.values()) {
		if (pl2.isSelected() || pl2.equals(currentLine)) {
		    linesToDrawList.add(pl2);
		}
	    }

	    int iterator = 1;

	    for (PlotLine pl : linesToDrawList) {

		gc.setStroke(pl.getColor());
		if (!pl.getHistogramBins().isEmpty()) {
		    double binWidth = Math.abs(pl.getHistogramMax() - pl.getHistogramMin()) / pl.getHistogramBinsCount();
		    while (binWidth * this.getPlotScaleX() < this.minXPixelTickSize) {
			binWidth *= 2;
		    }
		    //FloatPoint fp1 = pl.getPoints().get(0);
		    double currentRectangleStart = pl.getHistogramMin();
		    while (currentRectangleStart <= pl.getHistogramMax()) {
			double x1 = minX + getPlotX() + currentRectangleStart * this.getPlotScaleX();
			//double x2 = x1 + binWidth * this.getPlotScaleX();
			double y1;
			if (iterator % 2 != 0)//1,3,5,7...
			{
			    y1 = minY + iterator * -5 + getHeight();//draw first, third, fifth, seventh... tick line on the down side

			} else {
			    y1 = minY + iterator * 5;//draw the other tick lines on the up side 
			}
			//double y2 = y1 - i * getPlotScaleY();
			//strokeBorderedRectangle(gc, x1, y1, x2, y2, minX, minY, maxX, maxY);

			gc.setStroke(pl.getColor());
			gc.setLineWidth(1);
			//gc.strokeLine(x1, y1, x1, y1 + 5);
			gc.strokeLine(x1, minY, x1, minY + this.getHeight());
			gc.strokeText(Float.toString((float) currentRectangleStart), x1, y1);
			currentRectangleStart += binWidth;
		    }
		}

		iterator++;
	    }

	} else {//not in histogram view mode

	    if (currentScaleX < this.lastScaleXWhenTickSizeChanged)//we scaled it down since last time checked
	    {

		//the ticks may be too close together, so we first sorta approximate the correct order of magnitude, putting them further apart
		while (currentRoundXTickSize * currentScaleX < minXPixelTickSize) {
		    currentRoundXTickSize *= 10;
		    pixelXTickSize = (this.getPlotScaleX() * currentRoundXTickSize);

		}

		currentXTickSize = currentRoundXTickSize;
		//...and then we actually correct for it if necessary!
		while ((currentXTickSize * currentScaleX) / 2 > minXPixelTickSize) {
		    currentXTickSize /= 2;
		    pixelXTickSize = (currentXTickSize * currentScaleX);
		}
		if (currentXTickSize > lastXTickSizeWhenTickSizeChanged)//we actually fixed things and put it further apart
		{
		    lastXTickSizeWhenTickSizeChanged = currentXTickSize;
		} else {
		    currentXTickSize = lastXTickSizeWhenTickSizeChanged;//do nothing if we would make it worse
		}
		lastScaleXWhenTickSizeChanged = currentScaleX;
	    }

	    if (currentScaleX > this.lastScaleXWhenTickSizeChanged)//we scaled it up since last time checked
	    {

		//the ticks may be too far apart, so we first sorta approximate the correct order of magnitude, putting them closer together
		while (currentRoundXTickSize * currentScaleX > maxXPixelTickSize) {
		    currentRoundXTickSize /= 10;
		    pixelXTickSize = (this.getPlotScaleX() * currentRoundXTickSize);

		}

		currentXTickSize = currentRoundXTickSize;
		//...and then we actually correct for it if necessary!
		while ((currentXTickSize * currentScaleX) * 2 < maxXPixelTickSize) {
		    currentXTickSize *= 2;
		    pixelXTickSize = (currentScaleX * currentXTickSize);
		}
		if (currentXTickSize < lastXTickSizeWhenTickSizeChanged)//we actually fixed things and put it closer together
		{
		    lastXTickSizeWhenTickSizeChanged = currentXTickSize;
		} else {
		    currentXTickSize = lastXTickSizeWhenTickSizeChanged;//do nothing if we would make it worse
		}
		lastScaleXWhenTickSizeChanged = currentScaleX;
	    }

	    pixelXTickSize = (this.getPlotScaleX() * currentXTickSize);
	    double minNumberOnAxis = -(getPlotX()) / this.getPlotScaleX();
	    double maxNumberOnAxis = (this.getWidth() - (getPlotX())) / this.getPlotScaleX();
	    double minTickNumberOnAxis = Math.floor(minNumberOnAxis / currentXTickSize) * currentXTickSize;
	    double maxTickNumberOnAxis = Math.ceil(maxNumberOnAxis / currentXTickSize) * currentXTickSize;
	    //System.out.println(maxTickNumberOnAxis);
	    //System.out.println(minNumberOnAxis);
	    //System.out.println(maxNumberOnAxis);
	    //draw max 100 ticks to the right of the origin

	    for (double i = minTickNumberOnAxis; i <= maxTickNumberOnAxis; i += currentXTickSize) {
		{
		    if (minX + getPlotX() + i * getPlotScaleX() > minX && minX + getPlotX() + i * getPlotScaleX() < minX + getWidth()) {
			//col = col.deriveColor(0, 1, 1, opacity);
			//gc.strokeLine(minX + getPlotX() + (i * getPlotScaleX()), minY + getHeight(), minX + getPlotX() + (i * getPlotScaleX()), minY + getHeight() + 5);
			gc.setStroke(this.getColor2());
			gc.strokeLine(minX + getPlotX() + (i * getPlotScaleX()), minY + getHeight(), minX + getPlotX() + (i * getPlotScaleX()), minY + 5);
			gc.strokeText(Float.toString((float) ((i))), minX + getPlotX() + (i * getPlotScaleX()), minY + getHeight());
		    }
		}

	    }
	}
	//pixelXTickSize = (int) (this.getPlotScaleX() * currentXTickSize);

	while (pixelYTickSize < minYPixelTickSize) {
	    currentYTickSize *= 10;
	    pixelYTickSize = (int) (this.getPlotScaleY() * currentYTickSize);
	    for (int i = 0; i < 2; i++) {
		if (pixelYTickSize / 2 > minYPixelTickSize) {
		    pixelYTickSize /= 2;
		    currentYTickSize /= 2;
		}
	    }
	}

	while (pixelYTickSize > maxYPixelTickSize) {
	    currentYTickSize /= 10;
	    pixelYTickSize = (int) (this.getPlotScaleY() * currentYTickSize);
	    for (int i = 0; i < 2; i++) {
		if (pixelYTickSize * 2 < maxYPixelTickSize) {
		    pixelYTickSize *= 2;
		    currentYTickSize *= 2;
		}
	    }
	}

	pixelYTickSize = (this.getPlotScaleY() * currentYTickSize);

	double minNumberOnAxis = -(getPlotY()) / this.getPlotScaleY();
	double maxNumberOnAxis = (this.getHeight() - (getPlotY())) / this.getPlotScaleY();
	double minTickNumberOnAxis = Math.floor(minNumberOnAxis / currentYTickSize) * currentYTickSize;
	double maxTickNumberOnAxis = Math.ceil(maxNumberOnAxis / currentYTickSize) * currentYTickSize;
	System.out.println(maxTickNumberOnAxis);
	for (double i = minTickNumberOnAxis; i <= maxTickNumberOnAxis; i += currentYTickSize) {
	    {
		gc.setStroke(this.getColor2());
		if (minY + getPlotY() + i * getPlotScaleY() > minY && minY + getPlotY() + i * getPlotScaleY() < minY + getHeight()) {
		    //gc.strokeLine(minX, minY + getPlotY() + (i * getPlotScaleY()), minX + 5, minY + getPlotY() + (i) * getPlotScaleY());
		    gc.strokeLine(minX, minY + getPlotY() + (i * getPlotScaleY()), minX + getWidth(), minY + getPlotY() + (i) * getPlotScaleY());
		    gc.strokeText(Float.toString((float) (-i)), minX, minY + getPlotY() + (i * getPlotScaleY()));
		    //gc.strokeLine(minX + getPlotX() + (i * getPlotScaleX()), minY + getHeight(), minX + getPlotX() + (i * getPlotScaleX()), minY + getHeight() + 5);
		    //gc.strokeText(Float.toString((float) ((i))), minX + getPlotX() + (i * getPlotScaleX()), minY + getHeight());
		}

	    }
	}

    }

    /*
     int maxTicksDrawn = 100;
     //draw max 100 ticks, down from the origin
     for (int i = 0; i < maxTicksDrawn; i++) {
     if (y + getPlotY() + (i - maxTicksDrawn / 2) * pixelYTickSize > maxY) {
     break;
     } else if (y + getPlotY() + (i - maxTicksDrawn / 2) * pixelYTickSize > y) {
     gc.strokeLine(x, y + getPlotY() + (i - maxTicksDrawn / 2) * pixelYTickSize, x + 5, y + getPlotY() + (i - maxTicksDrawn / 2) * pixelYTickSize);
     gc.strokeText(Float.toString((float) -((i - maxTicksDrawn / 2) * currentYTickSize)), x + 20, y + getPlotY() + (i - maxTicksDrawn / 2) * pixelYTickSize);
     }

     }
     }*/
    @Override
    public void recompileEvents() {
	super.recompileEvents();

	try {
	    GUICompiler c = this.getGUIPanel().getGUICompiler();
	    c.compile(getSampleEventString());
	    sampleEvent = c.getByteCodeAL();
	    System.out.println("SUCCESS");
	} catch (Exception ex) {
	    //Logger.getLogger(Property.class.getName()).log(Level.SEVERE, null, ex);

	}
    }

    /**
     * If the line with this letter exists, return it; otherwise create it and
     * return it.
     *
     * @param letter
     * @return
     */
    public PlotLine getLine(String letter, boolean createIfNonexistant) {
	char ch;
	if (letter.equals("%")) {
	    ch = this.currentLineChar;
	} else {
	    ch = letter.charAt(0);
	}

	PlotLine pl = this.linesList.get(ch);
	if (pl == null && createIfNonexistant) {
	    pl = new PlotLine(GUIChart.this.currentLineChar, GUIChart.this);
	    GUIChart.this.addLine(pl);
	}

	return pl;
    }

    /**
     * Set the float property, determined by its propertyId, of line, determined
     * by its line letter, to the provided value
     *
     * @param lineLetter - the letter used to determine property of which line
     * should be accessed
     * @param propertyId - the ID, determining which property of the line should
     * be accessed.
     * @param value - the value the property should be set to
     * @see Property.getId
     * @return true if succesful, false otherwise
     */
    public boolean setLineProperty(String lineLetter, int propertyId, float value) {
	return this.getLine(lineLetter, true).setProperty(propertyId, value);
    }

    /**
     * Return the line property object, given the letter of the line, property
     * of which we are accessing, and the ID, determining which property we are
     * accessing.
     *
     * @param lineLetter - the letter used to determine property of which line
     * should be accessed
     * @param propertyId - the ID, determining which property of the line should
     * be accessed.
     *
     * @return the line property object, given the letter of the line, property
     * of which we are accessing, and the ID, determining which property we are
     * accessing.
     */
    public FloatProperty getLineProperty(String lineLetter, int propertyId) {
	return this.getLine(lineLetter, true).getProperty(propertyId);
    }

    /**
     * Get the name for the CLUC callback user function, which can be placed in
     * the user code to automatically get called, whenever some of the lines of
     * this chart are sampled.
     *
     * @return
     */
    public String getSampleEventString() {
	return this.getUniqueName() + "_Sample();\n";
    }

    void sendMousePress(MouseEvent event) {
	if (isWithinBounds(event.getSceneX(), event.getSceneY())) {
	    if (event.getButton().equals(MouseButton.MIDDLE)) {
		FloatPoint fp = this.getLastPositionDrawnTo();
		this.dragStartMouseX = event.getSceneX();
		this.dragStartMouseY = event.getSceneY();
		this.dragStartPlotX = this.getPlotX();
		this.dragStartPlotY = this.getPlotY();
	    }
	}

    }

    void sendMouseDrag(MouseEvent event) {
	double deltaX = event.getSceneX() - dragStartMouseX;
	double deltaY = event.getSceneY() - dragStartMouseY;
	this.setPlotX((float) (this.dragStartPlotX + deltaX));
	this.setPlotY((float) (this.dragStartPlotY + deltaY));
	this.requestRepaint();
    }
}
