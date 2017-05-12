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
 * A class for storing data points and their respective histograms.
 * Used by {@link GUIChart}.
 * Each {@code PlotLine} can have its width, color, set of data points and a histogram associated to it.
 * It can also become selected.
 * Lines are referred to by the {@link GUIChart} by their letter. This letter is also stored in the line.
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

    /**
     * Get the amount of bins for the histogram display.
     * @return the amount of bins for the histogram display.
     */
    public int getHistogramBinsCount() {
	return histogramBinsCount;
    }

    /**
     * Set the amount of bins for the histogram display.
     */
    public void setHistogramBinsCount(int histogramBinsCount) {
	this.histogramBinsCount = histogramBinsCount;
    }
    private ArrayList<Integer> histogramBins;

    /**
     * @return The x-minimum of the leftmost bin of the histogram.
     */
    public double getHistogramMin() {
	return histogramMin;
    }

    /**
     * @return The x-maximum of the rightmost bin of the histogram.
     */
    public double getHistogramMax() {
	return histogramMax;
    }

    /**
     * 
     * @return an {@link ArrayList} of {@link Integer}, each element representing the
     * number of samples in a single bin.
     */
    public ArrayList<Integer> getHistogramBins() {
	return histogramBins;
    }

    /**
     * 
     * @return the displayed width of this line (2 by default)
     */
    public double getLineWidth() {
	return this.getPropertyByName("LineWidth").getValue();
    }

    /**
     * Revert this {@code PlotLine} to the default state, discarding the data.
     * 
     */
    public void reset() {
	this.pointList = new ConcurrentSkipListMap();
	histogramMin = 0;
	histogramMax = 10;
	dataMin = 0;
	dataMax = 10;
	histogramBinsCount = 10;
    }


    public PlotLine(String s, char ch, GUIChart gc) {
	this(ch, gc);
	this.reset();
	s=s.replace("(^|\\r?\\n)[^0-9]\\r?\\n", "");//throw away lines without numbahs
	String lines[] = s.split("\\r?\\n");
	for(int i=0;i<lines.length;i++)
	{
	    String currentLine=lines[i];
	    ArrayList<Double> numbersOnCurrentLine=SimpleDoubleParser.parseDoublesFromString(currentLine);
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

    /**
     * Add a data point to this {@code PlotLine}. 
     * Will automatically update the histogram, the {@code LineSamples} {@link Property} and request the repainting of the
     * enclosing {@link GUIChart}
     * @param fp 
     */
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

    /**
     * 
     * @return the {@link ConcurrentSkipListMap}, which stores all the {@link FloatPoint}s of this
     * {@code PlotLine}
     */
    public synchronized ConcurrentSkipListMap<Double, FloatPoint> getPoints() {
	return this.pointList;
    }

    /**
     * @return the {@link Color} of this {@code PlotLine}
     */
    public Color getColor() {
	return this.lineColor;
    }

    /**
     * @return the {@code LineX} {@link Property}. This is used by the {@link GUIChart}
     * to determine where next {@link FloatPoint} should be recorded when recording a
     * {@code PlotLine}.
     * @see #sample() 
     * @see GUIChart#sampleAllRelevant() 
     */
    public double getCursorX() {
	return this.getPropertyByName("LineX").getValue();
    }


    /**
     * @return the {@code LineY} {@link Property}. This is used by the {@link GUIChart}
     * to determine where next {@link FloatPoint} should be recorded when recording a
     * {@code PlotLine}.
     * @see #sample() 
     * @see GUIChart#sampleAllRelevant() 
     */
    public double getCursorY() {
	return this.getPropertyByName("LineY").getValue();
    }

    /**
     * Set the {@code LineX} {@link Property}.
     * @see #getCursorX() 
     * @param x the new value of the {@code LineX} {@link Property}.
     * 
     */
    public void setCursorX(double x) {
	this.setProperty(this.name2IdMap.get("LineX"), (float) x);
    }

    /**
     * Set the {@code LineY} {@link Property}.
     * @see #getCursorY() 
     * @param y the new value of the {@code LineY} {@link Property}.
     */
    public void setCursorY(double y) {
	this.setProperty(this.name2IdMap.get("LineY"), (float) y);
    }

    /**
     * Set whether or not should this line be recorded by the {@link GUIChart}.
     * @see GUIChart#startRecording() 
     * @param rec whether or not should this line be recorded by the {@link GUIChart}.
     */
    public synchronized void setRecorded(boolean rec) {
	this.recorded = rec;
    }

    /**
     * @return whether or not should this line be recorded by the {@link GUIChart}.
     */
    public synchronized boolean isBeingRecorded() {
	return this.recorded;
    }


    /**
     * Set whether or not this {@code PlotLine} is selected, when on the {@link GUIChart}
     * @param selected whether or not this {@code PlotLine} is selected, when on the {@link GUIChart}
     */
    public void setSelected(boolean selected) {
	this.selected = selected;
    }

    /**
     * Whether or not this {@code PlotLine} is selected, when on the {@link GUIChart}.
     * This determines, whether it will be recorded when the next recording is initialized and more.
     * @see GUIChart#sampleAllRelevant() 
     */
    public boolean isSelected() {
	return this.selected;
    }

    /**
     * 
     * @return the character, representing this {@code PlotLine}
     */
    public char getCharacter() {
	return this.lineChar;
    }

    /**
     * 
     * @return whether or not should this line be drawn
     */
    boolean isVisible() {
	return this.visible;
    }

    /**
     * 
     * @return the number of {@link FloatPoint}s it his {@code PlotLine}.
     * Calling this method is semantically equivalent to calling {@code getPoints().size()}
     */
    public synchronized int getPointCount() {
	return this.pointList.size();
    }

    @Override
    /**
     * @return The textual representation of the data points in this line, compatible
     * with spreadsheet editors, such as Excel, and with the format from which
     * the line can be imported into a {@link GUIChart}
     * @see GUIChart#pasteOverwriteAction
     */
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

    /**
     * Add a new {@link FloatPoint} to the list.
     * The position is determined by the {@code LineX} and 
     * {@code LineY}  {@link Property} objects.
     * @see #getCursorX() 
     * @see #getCursorY() 
     * @see #getPoints() 
     */
    public void sample() {
	this.addPoint(new FloatPoint(this.getPropertyByName("LineX").getValue(), this.getPropertyByName("LineY").getValue()));
	//TODO: Following is just for debug purposes
	//cursor.x += 10;
	//cursor.y = Math.random() * 200 - 100;
    }

    /**
     * Update the range of the histogram and the data, using the new {@link FloatPoint}. If necessarry (the newly added point is outside
     * the current histogram x-range), recalculate the whole histogram for the adjusted range.
     * @param fp the newly added {@link FloatPoint}
     */
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

    /**
     * Return a {@link Property}, given its unique name.
     * @param name the name of the {@link Property} to return.
     * @return {@link Property}, given its unique name.
     * @see Property#getName() 
     */
    public FloatProperty getPropertyByName(String name) {
	Integer id = this.name2IdMap.get(name);
	if (id != null) {
	    FloatProperty fp = this.id2PropertyMap.get(id);
	    return fp;
	}
	return null;
    }

    /**
     * Set the {@link Property}, identified by its unique ID, to the value provided.
     * (Remark: for simplicity of implementation, all properties of a line are of type
     * {@link FloatProperty}).
     * 
     * This method will safely fail (return false) if no such {@link Property} exists.
     * 
     * @param propertyId the unique ID of the {@link Property} to adjust
     * @param value the new value of this property
     * @return whether the act of setting was succesful (true) or not (false)
     */
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




    /**
     * Register a new {@link Property} for this {@code PlotLine}. Once this
     * method is called, it is possible to set the value of this property and
     * retrieve it using the setLine[PropertyName] or getLine[PropertyName] CLUC
     * functions.
     *
     * The registration of this property is handled by correctly adjusting the
     * {@code name2IdMap}, {@code id2NameMap}, {@code property2idMap} and
     * {@code property2idMap} {@link HashMap}s.
     *
     * @param p the {@link Property} to add
     */
    public void addProperty(FloatProperty p) {
	name2IdMap.put(p.getName(), p.getId());
	id2NameMap.put(p.getId(), p.getName());
	id2PropertyMap.put(p.getId(), p);
	property2idMap.put(p, p.getId());

    }

    /**
     * Get the {@link FloatProperty}, based on the given unique ID
     * @param propertyId
     * @return {@link FloatProperty}, based on the given unique ID
     * @see Property#getId() 
     */
    FloatProperty getProperty(int propertyId) {
	return id2PropertyMap.get(propertyId);
    }
}
