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
import javafx.scene.paint.Color;
import shuntingyard.Token;

/**
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

    public GUIChart() {

	this.addFloatProperty(104, "LineX", 0);
	this.addFloatProperty(105, "LineY", 0);
    }

    public GUIChart(GUITab gut) {
	super(gut);
		//super(r);

	//actionMap.put("l", testAction);
	//actionMap.put("h", testAction2);
    }

    public void addLine(PlotLine pl) {
	this.linesList.put(pl.getCharacter(), pl);
    }

    public void startRecording() {
	if (isRecording) {
	    return;
	} else {

	    isRecording = true;
	}
	if (sampleTimer == null) {
	    sampleTimer = new Timer();
	}
	sampleTimer.schedule(
		new TimerTask() {
		    @Override
		    public void run() {
			System.out.println("kokodak");
			if (sampleEvent != null) {
			    Platform.runLater(() -> {//TODO: only surround the necessary stuff in runLater!
				GUIChart.this.getGUIPanel().handleCallBack(sampleEvent);//call the user event
			    });
			    GUIChart.this.sampleAllRelevant();
			}
		    }
		}, 0, 100);
    }

    public void setCurrentLineChar(char c) {
	this.currentLineChar = c;
    }

    void selectNextLine(int nth, boolean onlyThoseContainingData) {
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
		selectNextLine(dir, false);
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

    }

    @Override
    public void setGUITab(GUITab gut) {
	super.setGUITab(gut);

	NamedGUIAction scaleXUpAction = new NamedGUIAction("hscale++") {
	    @Override
	    public void doAction() {
		GUIChart.this.scaleAlongOrigin(1 + (0.01 * this.getCount()), 1, 200, 50);
		//GUIChart.this.setPlotScaleX((float) GUIChart.this.getPlotScaleX() * 1.01F);
		System.out.println("kokodak");
		GUIChart.super.update();

	    }
	};

	NamedGUIAction scaleXDownAction = new NamedGUIAction("hscale--") {
	    @Override
	    public void doAction() {
		GUIChart.this.scaleAlongOrigin(1 - (0.01 * this.getCount()), 1, 200, 50);
		//GUIChart.this.setPlotScaleX((float) GUIChart.this.getPlotScaleX() * 0.99F);
		System.out.println("kokodak");
		GUIChart.super.update();

	    }
	};

	NamedGUIAction scaleYUpAction = new NamedGUIAction("vscale++") {
	    @Override
	    public void doAction() {
		GUIChart.this.scaleAlongOrigin(1, 1.01, 200, 50);
		//GUIChart.this.setPlotScaleX((float) GUIChart.this.getPlotScaleX() * 1.01F);
		System.out.println("kokodak");
		GUIChart.super.update();

	    }
	};

	NamedGUIAction scaleYDownAction = new NamedGUIAction("vscale--") {
	    @Override
	    public void doAction() {
		GUIChart.this.scaleAlongOrigin(1, 0.99, 200, 50);
		//GUIChart.this.setPlotScaleX((float) GUIChart.this.getPlotScaleX() * 0.99F);
		System.out.println("kokodak");
		GUIChart.super.update();

	    }
	};

	NamedGUIAction nextExistingLine = new NamedGUIAction("next existing line") {
	    @Override
	    public void doAction() {
		//GUIChart.this.setPlotScaleX((float) GUIChart.this.getPlotScaleX() * 0.99F);
		GUIChart.this.selectNextLine(this.getCount(), true);
		System.out.println(GUIChart.this.currentLineChar);
		GUIChart.super.update();

	    }
	};
	NamedGUIAction prevExistingLine = new NamedGUIAction("previous existing line") {
	    @Override
	    public void doAction() {
		//GUIChart.this.setPlotScaleX((float) GUIChart.this.getPlotScaleX() * 0.99F);
		GUIChart.this.selectNextLine(-this.getCount(), true);
		System.out.println(GUIChart.this.currentLineChar);
		GUIChart.super.update();

	    }
	};
	NamedGUIAction nextLine = new NamedGUIAction("next line") {
	    @Override
	    public void doAction() {
		//GUIChart.this.setPlotScaleX((float) GUIChart.this.getPlotScaleX() * 0.99F);
		GUIChart.this.selectNextLine(this.getCount(), false);
		System.out.println(GUIChart.this.currentLineChar);
		GUIChart.super.update();

	    }
	};

	NamedGUIAction prevLine = new NamedGUIAction("previous line") {
	    @Override
	    public void doAction() {
		//GUIChart.this.setPlotScaleX((float) GUIChart.this.getPlotScaleX() * 0.99F);
		GUIChart.this.selectNextLine(-this.getCount(), false);
		System.out.println(GUIChart.this.currentLineChar);
		GUIChart.super.update();

	    }
	};

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

	NamedGUIAction pasteOverwriteAction = new NamedGUIAction("paste with overwriting") {
	    @Override
	    public void doAction() {
		ArrayList<String> test = GUIChart.this.parseDoublesFromString("1.2,\t 2.3, \t 3.4,  kokon slepice   4.5 10e05, 		50.35, 10.3e10");
		ArrayList<String> test2 = GUIChart.this.parseDoublesFromString("2, 3, 4");
		ArrayList<String> test3 = GUIChart.this.parseDoublesFromString("2,3,4e05");
		ArrayList<String> test4 = GUIChart.this.parseDoublesFromString("1 000, 2 000, 3 000");
		ArrayList<String> test5 = GUIChart.this.parseDoublesFromString("1, 000, 000; 2, 000, 000; 3, 000; 5e04");
		ArrayList<String> test6 = GUIChart.this.parseDoublesFromString("1,2, 2,3, 3,4, 5,6");
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

	NamedGUIAction startRecordingAction = new NamedGUIAction("Start recording") {
	    @Override
	    public void doAction() {

		if (this.getGUIPanel().getVFlag()) { //multiple lines selected, should operate on the selected ones
		    for (Map.Entry<Character, PlotLine> entry : GUIChart.this.linesList.entrySet()) {
			PlotLine pl = entry.getValue();
			if (pl.isSelected()) {
			    pl.setRecorded(true);
			}
		    }
		} else {
		    PlotLine pl = GUIChart.this.getPlotLineByChar(GUIChart.this.getGUIPanel().getCurrentRegisterLetterAndReset().charAt(0));
		    pl.setRecorded(true);
		}
		GUIChart.this.startRecording();
	    }

	};

	NamedGUIAction autoScaleAction = new NamedGUIAction("Autoscale seletion") {
	    @Override
	    public void doAction() {

		double minDrawnX = -GUIChart.this.getPlotX() * GUIChart.this.getPlotScaleX();//minimum value of x displayed in chart at current scale
		double maxDrawnX = (-GUIChart.this.getPlotX() + GUIChart.this.getWidth()) * GUIChart.this.getPlotScaleX();//maximum value of x displayed in chart at current scale
		GUIChart.this.autoScaleYForRange(GUIChart.this.linesList.values(), minDrawnX, maxDrawnX);

		if (this.getGUIPanel().getVFlag()) { //multiple lines selected, should operate on the selected ones
		    for (Map.Entry<Character, PlotLine> entry : GUIChart.this.linesList.entrySet()) {
			PlotLine pl = entry.getValue();
		    }
		} else {
		    PlotLine pl = GUIChart.this.getPlotLineByChar(GUIChart.this.getGUIPanel().getCurrentRegisterLetterAndReset().charAt(0));
		}
	    }

	};
	this.setMenu(new Menu(gut.getGUIPanel(), "slider menu", true));
	this.getMenu().addAction("a", autoScaleAction);
	this.getMenu().addAction("l", scaleXUpAction);
	this.getMenu().addAction("h", scaleXDownAction);
	//this.getMenu().addAction("j", scaleYUpAction);
	//this.getMenu().addAction("k", scaleYDownAction);

	this.getMenu().addAction("j", nextExistingLine);
	this.getMenu().addAction("k", prevExistingLine);
	this.getMenu().addAction("J", nextLine);
	this.getMenu().addAction("K", prevLine);
	this.getMenu().addAction("r", startRecordingAction);
	this.getMenu().addAction("s", sampleAction);
	GUIPanel gp = gut.getGUIPanel();
	Menu yankMenu = new Menu(gp, "yank (copy)", false);
	yankMenu.addAction("y", yankAction);
	this.getMenu().addSubMenu("y", yankMenu);
	Menu selectionMenu = new Menu(gp, "visually select", false);
	selectionMenu.addAction("v", selectOneAction);
	selectionMenu.addAction("a", selectAllAction);
	selectionMenu.addAction("i", invertSelectionAction);
	this.getMenu().addSubMenu("v", selectionMenu);

	Menu pasteMenu = new Menu(gp, "paste", false);
	pasteMenu.addAction("p", pasteOverwriteAction);
	this.getMenu().addSubMenu("p", pasteMenu);

    }

    public void sampleAllRelevant() {
	for (Map.Entry<Character, PlotLine> entry : GUIChart.this.linesList.entrySet()) {
	    PlotLine pl = entry.getValue();
	    if (pl.isBeingRecorded()) {
		pl.sample();
	    }
	}
    }

    private ArrayList<String> parseDoublesFromString(String s) {
	Matcher m1 = Pattern.compile("^([^0-9.,])*(.*?)(([^0-9e.,]))*$").matcher(s);
	String nonNumericCharsRegex = "[^0-9.,e\\s]";

	String prefix = "";
	String body = "";
	String postfix = "";

	ArrayList<String> returnList = new ArrayList<>();
	if (m1.find()) {
	    prefix = m1.group(1); //characters before the numbers themselves
	    body = m1.group(2); //the numbers
	    postfix = m1.group(3); //characters after the numbers
	}

	boolean containsDot = body.contains(".");
	boolean containsComma = body.contains(",");

	Matcher m = Pattern.compile(",\\s+").matcher(body);
	Matcher m2 = Pattern.compile("[^\\s,.e0-9]").matcher(body);

	boolean containsCommaSpace = m.find();
	boolean containsOtherChars = m2.find();

	if (containsDot) { //1.2;2.3;3.4 or 1.2, 2.3, 3.4, 2e03, 150e3, 12, 28 or 1.2 something 2.3 something else - numbers with an optional decimal dot; separated by whatever
	    Matcher m3 = Pattern.compile("[0-9]*\\.?[0-9]+e?[0-9]*").matcher(body);
	    while (m3.find()) {
		returnList.add(m3.group(0));
	    }
	} else {
	    if (containsComma) {
		if (containsCommaSpace) {
		    if (containsOtherChars) {// 1, 000; 2, 000; 
			body = body.replaceAll(",\\s*", "");
			Matcher m3 = Pattern.compile("[0-9]*\\.?[0-9]+e?[0-9]*").matcher(body);
			while (m3.find()) {
			    returnList.add(m3.group(0));
			}

		    } else { //1, 2, 3 or 1e02, 1e05, 2e03 - no decimal dots or commas in the input; just a bunch of numbers separated by commas, followed by blank space
			body = body.replaceAll(",\\s+", "xxx");
			body = body.replaceAll("\\s+", "");
			Matcher m3 = Pattern.compile("[0-9]*,?[0-9]+e?[0-9]*").matcher(body);
			while (m3.find()) {
			    returnList.add(m3.group(0));
			}
		    }
		} else //contains comma, but it's not followed by a space; doesn't contain any dots.
		{
		    if (containsOtherChars) {
			body = body.replaceAll(",", "");//remove all commas
			Matcher m3 = Pattern.compile("[0-9]*e?[0-9]*").matcher(body);
			while (m3.find()) {
			    returnList.add(m3.group(0));
			}
		    } else {

			Matcher m3 = Pattern.compile("\\s+").matcher(body);
			if (m3.find())//contains spaces, so it's like 1,2 3,4 5,6
			{

			    Matcher m4 = Pattern.compile("[0-9]*,?[0-9]+e?[0-9]*").matcher(body);
			    while (m4.find()) {
				returnList.add(m4.group(0));
			    }
			} else//no spaces, no dots, no other characters, just numbers and commas, so it's like: 1,2,3,4,5,1e05,2e03
			{
			    Matcher m4 = Pattern.compile("[0-9]+e?[0-9]*").matcher(body);
			    while (m4.find()) {
				returnList.add(m4.group(0));
			    }
			}
		    }
		}
	    }
	}
	return returnList;
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
    public void scaleAlongOrigin(double amountX, double amountY, int originX, int originY) {
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
	gc.setFill(Color.RED);
	gc.fillRect(x, y, this.getWidth(), this.getHeight());
	//gc.fillRect(x+this.getWidth(), y+this.getHeight()/3,this.getWidth()*(2/3), this.getHeight()*(2/3));
	paintLines(gc, x, y, x + this.getWidth(), y + this.getHeight());//TODO: limit amount of calls to getWidth and getHeight
	//paintHistograms(gc, x, y, x + this.getWidth(), y + this.getHeight());//TODO: limit amount of calls to getWidth and getHeight
	paintTicks(gc, x, y, x + this.getWidth(), y + this.getHeight());
	drawLegend(gc, x + this.getWidth() - 50, y + 50);
    }

    @Override
    public GUIelement makeCopy() {
	GUIChart cb = new GUIChart();
	this.copyPropertiesTo(cb);
	return cb;
    }

    public PlotLine getPlotLineByChar(char c) {
	if (c == '%') {
	    return this.linesList.get(this.currentLineChar);
	} else {
	    return this.linesList.get(c);
	}
    }

    private void strokeBorderedRectangle(GraphicsContext gc, double x1, double y1, double x2, double y2, double minX, double minY, double maxX, double maxY) {
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

	gc.setFill(Color.FUCHSIA);
	gc.fillRect(leftPoint.x, upPoint.y, rightPoint.x - leftPoint.x, downPoint.y - upPoint.y);
	gc.strokeRect(leftPoint.x, upPoint.y, rightPoint.x - leftPoint.x, downPoint.y - upPoint.y);
	//gc.strokeRect(200,100,-200,200);

    }

    @Override
    public int getHeight() {
	int preferredHeight = ((IntegerProperty) this.getPropertyByName("Height")).getValue();
	int preferredWidth = ((IntegerProperty) this.getPropertyByName("Width")).getValue();
	return (int) (((double) getWidth() / preferredWidth) * preferredHeight);
    }

    @Override
    public int getWidth() {
	int preferredWidth = ((IntegerProperty) this.getPropertyByName("Width")).getValue();
	return Math.max(preferredWidth, (int) (this.getGUIPanel().getCanvas().getWidth() * 0.8));
    }

    private void strokeBorderedLine(GraphicsContext gc, double x1, double y1, double x2, double y2, double minX, double maxX, double minY, double maxY) {
	double xSmaller = Math.min(x1, x2);
	double xBigger = Math.max(x1, x2);

	FloatPoint p1 = new FloatPoint(x1, y1);
	FloatPoint p2 = new FloatPoint(x2, y2);

	FloatPoint leftPoint = FloatPoint.getLeftMostPoint(p1, p2);
	FloatPoint rightPoint = FloatPoint.getRightMostPoint(p1, p2);
	FloatPoint upPoint = FloatPoint.getUpMostPoint(p1, p2);
	FloatPoint downPoint = FloatPoint.getDownMostPoint(p1, p2);

	if (leftPoint.x > minX && rightPoint.x < maxX && upPoint.y > minY && downPoint.y < maxY) {
	    gc.strokeLine(leftPoint.x, leftPoint.y, rightPoint.x, rightPoint.y);
	} else {

	    if (leftPoint.x < minX && rightPoint.x > minX)//outside the left border
	    {
		double yIntersect = findVertLineIntersect(x1, y1, x2, y2, minX);
		gc.strokeLine(minX, yIntersect, rightPoint.x, rightPoint.y);
		gc.setFill(Color.AQUA);
		gc.fillOval(minX - 2, yIntersect - 2, 4, 4);
	    } else if (leftPoint.x < maxX && rightPoint.x > maxX) {

		double yIntersect = findVertLineIntersect(x1, y1, x2, y2, maxX);
		gc.strokeLine(maxX, yIntersect, leftPoint.x, leftPoint.y);
		gc.setFill(Color.AQUA);
		gc.fillOval(maxX - 2, yIntersect - 2, 4, 4);
	    } else if (upPoint.y < minY && downPoint.y > minY) {
		double xIntersect = findHorLineIntersect(x1, y1, x2, y2, minY);
		gc.strokeLine(xIntersect, minY, downPoint.x, downPoint.y);
		gc.setFill(Color.AQUA);
		gc.fillOval(xIntersect - 2, minY - 2, 4, 4);
	    } else if (upPoint.y < maxY && downPoint.y > maxY) {
		double xIntersect = findHorLineIntersect(x1, y1, x2, y2, maxY);
		gc.strokeLine(xIntersect, maxY, upPoint.x, upPoint.y);
		gc.setFill(Color.AQUA);
		gc.fillOval(xIntersect - 2, maxY - 2, 4, 4);
	    }

	}
    }

    double findVertLineIntersect(double x1, double y1, double x2, double y2, double lineX) {
	double A = (y1 - y2) / (x1 - x2);
	double B = y1 - A * x1;

	return A * lineX + B;
    }

    double findHorLineIntersect(double x1, double y1, double x2, double y2, double lineY) {
	double A = (y1 - y2) / (x1 - x2);
	double B = y1 - A * x1;

	return (lineY - B) / A;
    }

    double getPlotX() {
	FloatProperty fp = (FloatProperty) this.getPropertyByName("PlotX");
	return fp.getValue();
    }

    double getPlotY() {
	FloatProperty fp = (FloatProperty) this.getPropertyByName("PlotY");
	return fp.getValue();
    }

    void setPlotX(float x) {
	this.getPropertyByName("PlotX").setValue(x);
    }

    void setPlotY(float y) {
	this.getPropertyByName("PlotY").setValue(y);
    }

    double getPlotScaleX() {
	FloatProperty fp = (FloatProperty) this.getPropertyByName("PlotScaleX");
	return fp.getValue();
    }

    double getPlotScaleY() {
	FloatProperty fp = (FloatProperty) this.getPropertyByName("PlotScaleY");
	return fp.getValue();
    }

    void setPlotScaleX(float x) {
	this.getPropertyByName("PlotScaleX").setValue(x);
    }

    void setPlotScaleY(float y) {
	this.getPropertyByName("PlotScaleY").setValue(y);
    }

    void autoScaleYForRange(Collection<PlotLine> lines, double minX, double maxX) {
	double maxY = Double.NEGATIVE_INFINITY;
	double minY = Double.POSITIVE_INFINITY;
	for (PlotLine pl : lines) {
	    for (FloatPoint fp : pl.getPoints().values()) {
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
	    //so now we found the local max and min in Y between minX and maxX

	    double currentPixelMinMaxSpan = (maxY - minY) * this.getPlotScaleY();//the distance in pixels between the min and max Y found between minX and maxX
	    double targetPixelMinMaxSpan = this.getHeight() * autoScalePaddingCoeff;
	    this.setPlotScaleY((float) (this.getPlotScaleY() * (targetPixelMinMaxSpan / currentPixelMinMaxSpan)));
	    double maxYOnChart = this.getPlotY() - (maxY * this.getPlotScaleY());//the y position of the local maximum, in pixels, relative to the chart coordinate system
	    this.setPlotY((float) (this.getPlotY() + ((this.getHeight() * ((1 - autoScalePaddingCoeff) / 2) - maxYOnChart))));
	}

	//double maxY=Double.NEGATIVE_INFINITY;
    }

    public void paintLines(GraphicsContext gc, double x, double y, double maxX, double maxY) {
	for (Map.Entry<Character, PlotLine> entry : this.linesList.entrySet()) {
	    PlotLine pl = entry.getValue();
	    gc.setStroke(pl.getColor());
	    if (!pl.getPoints().isEmpty()) {
		FloatPoint fp1 = pl.getPoints().firstEntry().getValue();
		for (FloatPoint fp : pl.getPoints().values()) {
		    strokeBorderedLine(gc, fp1.x * this.getPlotScaleX() + x + getPlotX(), -fp1.y * this.getPlotScaleY() + y + getPlotY(), fp.x * this.getPlotScaleX() + x + getPlotX(), -fp.y * this.getPlotScaleY() + y + getPlotY(), x, maxX, y, maxY);
		    fp1 = fp;
		}
	    }
	}
    }

    public void paintHistograms(GraphicsContext gc, double x, double y, double maxX, double maxY) {

	for (Map.Entry<Character, PlotLine> entry : this.linesList.entrySet()) {
	    PlotLine pl = entry.getValue();
	    gc.setStroke(pl.getColor());
	    if (!pl.getHistogramBins().isEmpty()) {
		double binWidth = Math.abs(pl.getHistogramMax() - pl.getHistogramMin()) / pl.getHistogramBinsCount();
		//FloatPoint fp1 = pl.getPoints().get(0);
		double currentRectangleStart = pl.getHistogramMin();
		for (int i : pl.getHistogramBins()) {
		    double x1 = x + getPlotX() + currentRectangleStart * this.getPlotScaleX();
		    double x2 = x1 + binWidth * this.getPlotScaleX();
		    double y1 = getPlotY() + y;
		    double y2 = y1 - i * getPlotScaleY();
		    strokeBorderedRectangle(gc, x1, y1, x2, y2, x, y, maxX, maxY);

		    currentRectangleStart += binWidth;
		}
	    }
	}
    }

    public void drawLegend(GraphicsContext gc, double x, double y) {
	int i = 0;
	for (Map.Entry<Character, PlotLine> entry : this.linesList.entrySet()) {
	    PlotLine pl = entry.getValue();
	    gc.strokeText((pl.getCharacter() == this.currentLineChar ? ">" : "") + pl.getCharacter() + ": " + ((pl.isVisible()) ? "" : "(H)") + (pl.isSelected() ? "(V)" : ""), x, y + i);
	    i += 10;
	}
	if (this.linesList.get(this.currentLineChar) == null)//currently selected is a ghost
	{
	    gc.strokeText(">" + this.currentLineChar + "(G): (ghost)", x, y + i);
	}
    }

    public void paintTicks(GraphicsContext gc, double x, double y, double maxX, double maxY) {

	double pixelXTickSize = (this.getPlotScaleX() * currentXTickSize);
	double pixelYTickSize = (this.getPlotScaleY() * currentYTickSize);

	double currentScaleX = this.getPlotScaleX();

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

	pixelXTickSize = (this.getPlotScaleX() * currentXTickSize);
	pixelYTickSize = (this.getPlotScaleY() * currentYTickSize);

	double minNumberOnAxis = -(getPlotX()) / this.getPlotScaleX();
	double maxNumberOnAxis = (this.getWidth() - (getPlotX())) / this.getPlotScaleX();
	double minTickNumberOnAxis = Math.floor(minNumberOnAxis / currentXTickSize) * currentXTickSize;
	double maxTickNumberOnAxis = Math.ceil(maxNumberOnAxis / currentXTickSize) * currentXTickSize;
	System.out.println(maxTickNumberOnAxis);
	//System.out.println(minNumberOnAxis);
	//System.out.println(maxNumberOnAxis);
	//draw max 100 ticks to the right of the origin

	for (double i = minTickNumberOnAxis; i <= maxTickNumberOnAxis; i += currentXTickSize) {
	    {
		if (x + getPlotX() + i * getPlotScaleX() > x && x + getPlotX() + i * getPlotScaleX() < x + getWidth()) {
		    gc.strokeLine(x + getPlotX() + (i * getPlotScaleX()), y + getHeight(), x + getPlotX() + (i * getPlotScaleX()), y + getHeight() + 5);
		    gc.strokeText(Float.toString((float) ((i))), x + getPlotX() + (i * getPlotScaleX()), y + getHeight());
		}
	    }

	}

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
    }

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

    public PlotLine getLine(String letter) {
	char ch;
	if (letter.equals("%")) {
	    ch = this.currentLineChar;
	} else {
	    ch = letter.charAt(0);
	}
	return this.linesList.get(ch);
    }

    public boolean setLineProperty(String lineLetter, int propertyId, float value) {
	return this.getLine(lineLetter).setProperty(propertyId, value);
    }

    public String getSampleEventString() {
	return this.getUniqueName() + "_Sample();\n";
    }

}
