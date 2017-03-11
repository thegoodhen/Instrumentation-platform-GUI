/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javafx.application.Platform;
import javafx.scene.paint.Color;

/**
 *
 * @author thegoodhen
 */
public class PlotLine {

    ArrayList<FloatPoint> pointList = new ArrayList<>();
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

    public PlotLine(char ch, GUIChart gc)//TODO: this is just a test constructor
    {
	this.theChart = gc;
	this.addProperty(new FloatProperty(104, "LineX", 0.0F, gc));
	this.addProperty(new FloatProperty(105, "LineY", 0.0F, gc));
	this.lineChar = ch;
	histogramBins = new ArrayList<Integer>();

	for (double i = 0; i < 2 * 3.14159265358979323846; i += 0.5) {
	    double x = (i * 100);
	    double y = (Math.sin(i) * 80);
	    FloatPoint fp = new FloatPoint(x / 10, y + 40);
	    this.addPoint(fp);
	}

	System.out.println("gek gek");
    }

    public void addPoint(FloatPoint fp) {
	this.pointList.add(fp);
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

    public ArrayList<FloatPoint> getPoints() {
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

    public void setRecorded(boolean rec) {
	this.recorded = rec;
    }

    public boolean isBeingRecorded() {
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

    public int getPointCount() {
	return this.pointList.size();
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();
	for (FloatPoint fp : this.pointList) {
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
	    for (FloatPoint fp2 : pointList) {
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
}
