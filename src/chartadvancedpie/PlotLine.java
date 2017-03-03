/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.ArrayList;
import javafx.scene.paint.Color;

/**
 *
 * @author thegoodhen
 */
public class PlotLine {

    ArrayList<FloatPoint> pointList = new ArrayList<>();
    Color lineColor = Color.CHARTREUSE;//such a pun, because I reused it in chart! :3
    FloatPoint cursor = new FloatPoint(0, 0);
    private boolean recorded = false;
    private boolean selected = false;
    private boolean visible = true;
    private boolean showPoints = false;
    private boolean showLine = true;
    private char lineChar;

    public PlotLine(char ch)//TODO: this is just a test constructor
    {
	this.lineChar = ch;
	/*
	 for (double i = 0; i < 20 * 3.14159265358979323846; i += 0.1) {
	 double x = (i * 100);
	 double y = (Math.sin(i) * 80);
	 FloatPoint fp = new FloatPoint(x / 10, y + 40);
	 this.addPoint(fp);
	 }
	 */
    }

    public void addPoint(FloatPoint fp) {
	this.pointList.add(fp);
    }

    public ArrayList<FloatPoint> getPoints() {
	return this.pointList;
    }

    public Color getColor() {
	return this.lineColor;
    }

    public double getCursorX() {
	return this.cursor.x;
    }

    public double getCursorY() {
	return this.cursor.y;
    }

    public void setCursorX(double x) {
	this.cursor.x = x;
    }

    public void setCursorY(double y) {
	this.cursor.y = y;
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
	this.addPoint(new FloatPoint(this.cursor));
	//TODO: Following is just for debug purposes
	cursor.x += 10;
	cursor.y = Math.random() * 200 - 100;
    }
}
