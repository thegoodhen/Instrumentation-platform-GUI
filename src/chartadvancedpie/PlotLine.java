/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.scene.paint.Color;

/**
 *
 * @author thegoodhen
 */
public class PlotLine {

    ConcurrentSkipListMap<Double, FloatPoint> pointList = new ConcurrentSkipListMap();
    Color lineColor = Color.CHARTREUSE;//such a pun, because I reused it in chart! :3
    //FloatPoint cursor = new FloatPoint(0, 0);
    private boolean recorded = false;
    private boolean selected = false;
    private boolean visible = true;
    private boolean showPoints = false;
    private boolean showLine = true;
    private char lineChar;
    private double histogramMin = 0;
    private double histogramMax = 10;
    private double dataMin = 0;
    private double dataMax = 10;
    private int histogramBinsCount = 10;
    private GUIChart theChart;

    private HashMap<String, Integer> name2IdMap = new HashMap<>();
    private HashMap<Integer, String> id2NameMap = new HashMap<>();
    private HashMap<Integer, FloatProperty> id2PropertyMap = new HashMap<>();
    private HashMap<FloatProperty, Integer> property2idMap = new HashMap<>();

    public int getHistogramBinsCount() {
	return histogramBinsCount;
    }

    public void setHistogramBinsCount(int histogramBinsCount) {
	this.histogramBinsCount = histogramBinsCount;
    }
    private ArrayList<Integer> histogramBins;

    public double getHistogramMin() {
	return histogramMin;
    }

    public double getHistogramMax() {
	return histogramMax;
    }

    public ArrayList<Integer> getHistogramBins() {
	return histogramBins;
    }

    public double getLineWidth() {
	return this.getPropertyByName("LineWidth").getValue();
    }

    public void reset() {
	this.pointList = new ConcurrentSkipListMap();
	histogramMin = 0;
	histogramMax = 10;
	dataMin = 0;
	dataMax = 10;
	histogramBinsCount = 10;
    }

    private ArrayList<Double> parseDoublesFromString(String s) {
	Matcher m1 = Pattern.compile("^([^0-9.,-])*(.*?)(([^0-9e.,-]))*$").matcher(s);
	String nonNumericCharsRegex = "[^0-9.,e-\\s]";

	String prefix = "";
	String body = "";
	String postfix = "";

	ArrayList<Double> returnList = new ArrayList<>();
	if (m1.find()) {
	    prefix = m1.group(1); //characters before the numbers themselves
	    body = m1.group(2); //the numbers
	    postfix = m1.group(3); //characters after the numbers
	}

	boolean containsDot = body.contains(".");
	boolean containsComma = body.contains(",");

	Matcher m = Pattern.compile(",\\s+").matcher(body);
	Matcher m2 = Pattern.compile("[^\\s,.e0-9-]").matcher(body);

	boolean containsCommaSpace = m.find();
	boolean containsOtherChars = m2.find();

	if (containsDot) { //1.2;2.3;3.4 or 1.2, 2.3, 3.4, 2e03, 150e3, 12, 28 or 1.2 something 2.3 something else - numbers with an optional decimal dot; separated by whatever
	    Matcher m3 = Pattern.compile("[0-9-]*\\.?[0-9]+e?[0-9]*").matcher(body);
	    while (m3.find()) {
		try {

		    returnList.add(Double.parseDouble(m3.group(0)));
		} catch (Exception e) {

		}
	    }
	} else {
	    if (containsComma) {
		if (containsCommaSpace) {
		    if (containsOtherChars) {// 1, 000; 2, 000; 
			body = body.replaceAll(",\\s*", "");
			Matcher m3 = Pattern.compile("[0-9-]*\\.?[0-9]+e?[0-9]*").matcher(body);
			while (m3.find()) {

			    try {

				returnList.add(Double.parseDouble(m3.group(0)));
			    } catch (Exception e) {

			    }
			}

		    } else { //1, 2, 3 or 1e02, 1e05, 2e03 - no decimal dots in the input; just a bunch of numbers separated by commas, followed by blank space, OR!! for fools: some guy actually gave us list of numbers using floating comma, separated by comma. Wow, what a dork.
			body = body.replaceAll(",\\s+", "xxx");
			body = body.replaceAll("\\s+", "");
			body = body.replaceAll(",", ".");
			Matcher m3 = Pattern.compile("-?[0-9]*\\.?[0-9]+e?[0-9]*").matcher(body);
			while (m3.find()) {
			    try {

				returnList.add(Double.parseDouble(m3.group(0)));
			    } catch (Exception e) {

			    }
			}
		    }
		} else //contains comma, but it's not followed by a space; doesn't contain any dots.
		{
		    if (containsOtherChars) {
			body = body.replaceAll(",", "");//remove all commas TODO: FIX BUG HERE
			Matcher m3 = Pattern.compile("-?[0-9]*e?[0-9]*").matcher(body);
			while (m3.find()) {
			    try {

				returnList.add(Double.parseDouble(m3.group(0)));
			    } catch (Exception e) {

			    }
			}
		    } else {

			Matcher m3 = Pattern.compile("\\s+").matcher(body);
			if (m3.find())//contains spaces, so it's like 1,2 3,4 5,6
			{

			    body = body.replaceAll(",", ".");//for the parser

			    Matcher m4 = Pattern.compile("-?[0-9]*\\.?[0-9]+e?[0-9]*").matcher(body);
			    while (m4.find()) {
				try {

				    returnList.add(Double.parseDouble(m4.group(0)));
				} catch (Exception e) {

				}
			    }
			} else//no spaces, no dots, no other characters, just numbers and commas, so it's like: 1,2,3,4,5,1e05,2e03
			{
			    Matcher m4 = Pattern.compile("[0-9-]+e?[0-9]*").matcher(body);
			    while (m4.find()) {
				try {

				    returnList.add(Double.parseDouble(m4.group(0)));
				} catch (Exception e) {

				}
			    }
			}
		    }
		}
	    }
	}
	return returnList;
    }

    public PlotLine(String s, char ch, GUIChart gc) {
	this(ch, gc);
	this.reset();
	s=s.replace("(^|\\r?\\n)[^0-9]\\r?\\n", "");//throw away lines without numbahs
	String lines[] = s.split("\\r?\\n");
	for(int i=0;i<lines.length;i++)
	{
	    String currentLine=lines[i];
	    ArrayList<Double> numbersOnCurrentLine=parseDoublesFromString(currentLine);
	    if(numbersOnCurrentLine!=null)
	    {
		if(numbersOnCurrentLine.size()==2)
		{
		    double theX=numbersOnCurrentLine.get(0);
		    double theY=numbersOnCurrentLine.get(1);
		    this.pointList.put(theX,new FloatPoint(theX,theY));
		}
	    }
	}
    }

    public PlotLine(char ch, GUIChart gc)//TODO: this is just a test constructor
    {
	this.theChart = gc;
	this.addProperty(new FloatProperty(104, "LineX", 0.0F, gc));
	this.addProperty(new FloatProperty(105, "LineY", 0.0F, gc));
	this.addProperty(new FloatProperty(106, "LineWidth", 2.0F, gc));
	this.addProperty(new FloatProperty(107, "LineSamples", 0.0F, gc));
	FloatProperty lineCol = new FloatProperty(108, "LineColor", 2.0F, gc);
	lineCol.setSetterPropertyCallback(new PropertyCallback<Float>() {
	    @Override
	    public void run(Property<Float> p) {
		PlotLine.this.lineColor = ColorManager.get().colorFromFloat(p.getValueSilent());
	    }

	});

	lineCol.setGetterPropertyCallback(new PropertyCallback<Float>() {
	    @Override
	    public void run(Property<Float> p) {
		p.setValueSilent(ColorManager.get().floatFromColor(lineColor));
	    }

	});
	this.addProperty(lineCol);

	this.lineChar = ch;
	histogramBins = new ArrayList<Integer>();

	for (double i = 0; i < 20 * 3.14159265358979323846; i += 0.5) {
	    double x = (i * 100);
	    double y = (Math.sin(i) * 80);
	    FloatPoint fp = new FloatPoint(x / 10, y + 40);
	    this.addPoint(fp);
	}

	System.out.println("gek gek");
    }

    /**
     * Returns the FloatPoint, x value of which is the closest to the value
     * provided, but smaller.
     *
     * @param value
     * @return
     */
    public FloatPoint getPointLeftTo(double value) {
	return this.pointList.floorEntry(value).getValue();
    }

    /**
     * Returns the FloatPoint, x value of which is the closest to the value
     * provided, but greater.
     *
     * @param value
     * @return
     */
    public FloatPoint getPointRightTo(double value) {
	return this.pointList.ceilingEntry(value).getValue();
    }

    /**
     * Use linear interpolation to guess the value between 2 points; returns 0
     * if no such value exists.
     *
     * @param x
     * @return
     */
    public double evaluateYatX(double x) {
	FloatPoint thePoint = this.pointList.get(x);
	if (thePoint != null) {
	    return thePoint.getY();
	}
	FloatPoint leftPoint = getPointLeftTo(x);
	FloatPoint rightPoint = getPointRightTo(x);
	if (leftPoint != null && rightPoint != null) {
	    return ((x - leftPoint.x) / (rightPoint.x - leftPoint.x)) * (rightPoint.y - leftPoint.y) + leftPoint.y;
	}
	return 0;
    }

    public void addPoint(FloatPoint fp) {
	this.pointList.put(fp.x, fp);
	float currentSamplesCount = this.getPropertyByName("LineSamples").getValue();
	this.getPropertyByName("LineSamples").setValue(currentSamplesCount + 1);
	theChart.update();
	GUIPanel gp = theChart.getGUIPanel();
	if (gp != null) {
	    GUITab gt = gp.getCurrentGUITab();
	    if (gt != null) {

		Platform.runLater(() -> {
		    gt.paintGUIelements();
		}
		);
	    }
	}
	System.out.println("updating chart");
	updateHistogram(fp);
    }

    public synchronized ConcurrentSkipListMap<Double, FloatPoint> getPoints() {
	return this.pointList;
    }

    public Color getColor() {
	return this.lineColor;
    }

    public double getCursorX() {
	return this.getPropertyByName("LineX").getValue();
    }

    public double getCursorY() {
	return this.getPropertyByName("LineY").getValue();
    }

    public void setCursorX(double x) {
	this.setProperty(this.name2IdMap.get("LineX"), (float) x);
    }

    public void setCursorY(double y) {
	this.setProperty(this.name2IdMap.get("LineY"), (float) y);
    }

    public synchronized void setRecorded(boolean rec) {
	this.recorded = rec;
    }

    public synchronized boolean isBeingRecorded() {
	return this.recorded;
    }

    public void setSelected(boolean selected) {
	this.selected = selected;
    }

    public boolean isSelected() {
	return this.selected;
    }

    public char getCharacter() {
	return this.lineChar;
    }

    boolean isVisible() {
	return this.visible;
    }

    public synchronized int getPointCount() {
	return this.pointList.size();
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();
	for (FloatPoint fp : this.pointList.values()) {
	    sb.append(fp.x);
	    sb.append("\t");
	    sb.append(fp.y);
	    sb.append("\n");
	}
	return sb.toString();
    }

    public void sample() {
	this.addPoint(new FloatPoint(this.getPropertyByName("LineX").getValue(), this.getPropertyByName("LineY").getValue()));
	//TODO: Following is just for debug purposes
	//cursor.x += 10;
	//cursor.y = Math.random() * 200 - 100;
    }

    private void updateHistogram(FloatPoint fp) {
	boolean updatedRange = false;
	if (fp.y > dataMax) {
	    updatedRange = true;
	    dataMax = fp.y;
	    histogramMax = fp.y;
	}
	if (fp.y < dataMin) {
	    updatedRange = true;
	    dataMin = fp.y;
	    histogramMin = fp.y;
	}
	if (updatedRange) {
	    if (histogramMax > 138) {
		System.out.println("kdak");
	    }
	    double y = Math.max(Math.floor(Math.log10(Math.abs(dataMax))), Math.floor(Math.log10(Math.abs(dataMin))));//we get the highest order of magnitude present in the data first...
	    histogramMin = (Math.floor(dataMin / Math.pow(10, y))) * Math.pow(10, y);
	    histogramMax = (Math.floor(dataMax / Math.pow(10, y)) + 1) * Math.pow(10, y);
	    histogramBins = new ArrayList<>(Collections.nCopies(histogramBinsCount, 0));
	    for (FloatPoint fp2 : pointList.values()) {
		int binIndex = (int) (Math.floor(((fp2.y - histogramMin) / (histogramMax - histogramMin)) * histogramBinsCount));
		histogramBins.set(binIndex, histogramBins.get(binIndex) + 1);

	    }
	} else {
	    int binIndex = (int) (Math.floor(((fp.y - histogramMin) / (histogramMax - histogramMin)) * histogramBinsCount));
	    histogramBins.set(binIndex, histogramBins.get(binIndex) + 1);
	}
    }

    public FloatProperty getPropertyByName(String name) {
	Integer id = this.name2IdMap.get(name);
	if (id != null) {
	    FloatProperty fp = this.id2PropertyMap.get(id);
	    return fp;
	}
	return null;
    }

    public boolean setProperty(int propertyId, float value) {
	FloatProperty fp = id2PropertyMap.get(propertyId);
	if (fp != null) {
	    System.out.println("setting " + fp.getName() + " to " + value + "!");
	    fp.setValue(value);
	    return true;
	} else {
	    System.err.println("Error: No property with the following ID: " + propertyId);
	    return false;
	}
    }

    public void addProperty(FloatProperty p) {
	name2IdMap.put(p.getName(), p.getId());
	id2NameMap.put(p.getId(), p.getName());
	id2PropertyMap.put(p.getId(), p);
	property2idMap.put(p, p.getId());

    }

    FloatProperty getProperty(int propertyId) {
	return id2PropertyMap.get(propertyId);
    }
}
