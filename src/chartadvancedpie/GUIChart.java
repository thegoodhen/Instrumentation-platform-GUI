/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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

    public GUIChart() {

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

    public void setCurrentLineChar(char c) {
	this.currentLineChar = c;
    }

    void selectNextLine(boolean forward, boolean onlyThoseContainingData) {
	int dir = 0;
	if (forward) {
	    dir = 1;
	} else {
	    dir = -1;
	}

	this.currentLineChar += dir;
	if (this.currentLineChar > 122) {
	    this.currentLineChar = 97;
	}
	if (this.currentLineChar < 97) {
	    this.currentLineChar = 122;
	}

	if (onlyThoseContainingData) {
	    if (currentLineChar == 'a') {
		System.out.println("kokoko");
	    }
	    PlotLine pl = this.linesList.get(currentLineChar);
	    while (pl == null || (!this.linesList.get(currentLineChar).isVisible()) || (pl.getPointCount() == 0)) {
		selectNextLine(forward, false);
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
	this.addLine(new PlotLine('a'));
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
		GUIChart.this.scaleAlongOrigin(1.01, 1, 200, 50);
		//GUIChart.this.setPlotScaleX((float) GUIChart.this.getPlotScaleX() * 1.01F);
		System.out.println("kokodak");
		GUIChart.super.update();

	    }
	};

	NamedGUIAction scaleXDownAction = new NamedGUIAction("hscale--") {
	    @Override
	    public void doAction() {
		GUIChart.this.scaleAlongOrigin(0.99, 1, 200, 50);
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
		GUIChart.this.selectNextLine(true, true);
		System.out.println(GUIChart.this.currentLineChar);
		GUIChart.super.update();

	    }
	};
	NamedGUIAction prevExistingLine = new NamedGUIAction("previous existing line") {
	    @Override
	    public void doAction() {
		//GUIChart.this.setPlotScaleX((float) GUIChart.this.getPlotScaleX() * 0.99F);
		GUIChart.this.selectNextLine(false, true);
		System.out.println(GUIChart.this.currentLineChar);
		GUIChart.super.update();

	    }
	};
	NamedGUIAction nextLine = new NamedGUIAction("next line") {
	    @Override
	    public void doAction() {
		//GUIChart.this.setPlotScaleX((float) GUIChart.this.getPlotScaleX() * 0.99F);
		GUIChart.this.selectNextLine(true, false);
		System.out.println(GUIChart.this.currentLineChar);
		GUIChart.super.update();

	    }
	};

	NamedGUIAction prevLine = new NamedGUIAction("previous line") {
	    @Override
	    public void doAction() {
		//GUIChart.this.setPlotScaleX((float) GUIChart.this.getPlotScaleX() * 0.99F);
		GUIChart.this.selectNextLine(false, false);
		System.out.println(GUIChart.this.currentLineChar);
		GUIChart.super.update();

	    }
	};

	NamedGUIAction sampleAction = new NamedGUIAction("sample line(s)") {
	    @Override
	    public void doAction() {
		//GUIChart.this.setPlotScaleX((float) GUIChart.this.getPlotScaleX() * 0.99F);

		PlotLine pl = GUIChart.this.linesList.get(GUIChart.this.currentLineChar);
		if (pl == null) {
		    pl = new PlotLine(GUIChart.this.currentLineChar);
		    GUIChart.this.addLine(pl);
		}

		pl.sample();

		GUIChart.super.update();

	    }
	};

	NamedGUIAction yankAction = new NamedGUIAction("yank (copy) current line") {
	    @Override
	    public void doAction() {
		PlotLine currentLine = GUIChart.this.linesList.get(GUIChart.this.currentLineChar);
		if (currentLine != null) {
		    GUIChart.this.getGUIPanel().setCurrentRegisterContentAndReset(currentLine.toString());
		}
	    }
	};
	this.setMenu(new Menu(gut.getGUIPanel(), "slider menu", true));
	this.getMenu().addAction("l", scaleXUpAction);
	this.getMenu().addAction("h", scaleXDownAction);
	//this.getMenu().addAction("j", scaleYUpAction);
	//this.getMenu().addAction("k", scaleYDownAction);

	this.getMenu().addAction("j", nextExistingLine);
	this.getMenu().addAction("k", prevExistingLine);
	this.getMenu().addAction("J", nextLine);
	this.getMenu().addAction("K", prevLine);
	this.getMenu().addAction("s", sampleAction);
	GUIPanel gp = gut.getGUIPanel();
	Menu yankMenu = new Menu(gp, "yank (copy)", false);
	yankMenu.addAction("y", yankAction);
	this.getMenu().addSubMenu("y", yankMenu);

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
	paintTicks(gc, x, y, x + this.getWidth(), y + this.getHeight());
    }

    @Override
    public GUIelement makeCopy() {
	GUIChart cb = new GUIChart();
	this.copyPropertiesTo(cb);
	return cb;
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

    public void paintLines(GraphicsContext gc, double x, double y, double maxX, double maxY) {
	for (Map.Entry<Character, PlotLine> entry : this.linesList.entrySet()) {
	    PlotLine pl = entry.getValue();
	    gc.setStroke(pl.getColor());
	    if (!pl.getPoints().isEmpty()) {
		FloatPoint fp1 = pl.getPoints().get(0);
		for (FloatPoint fp : pl.getPoints()) {
		    strokeBorderedLine(gc, fp1.x * this.getPlotScaleX() + x - 100 + getPlotX(), fp1.y * this.getPlotScaleY() + y + getPlotY(), fp.x * this.getPlotScaleX() + x - 100 + getPlotX(), fp.y * this.getPlotScaleY() + y + getPlotY(), x, maxX, y, maxY);
		    fp1 = fp;
		}
	    }
	}
    }

    public void paintTicks(GraphicsContext gc, double x, double y, double maxX, double maxY) {

	int pixelXTickSize = (int) (this.getPlotScaleX() * currentXTickSize);
	int pixelYTickSize = (int) (this.getPlotScaleY() * currentYTickSize);

	while (pixelXTickSize < minXPixelTickSize) {
	    currentXTickSize *= 10;
	    pixelXTickSize = (int) (this.getPlotScaleX() * currentXTickSize);
	    for (int i = 0; i < 2; i++) {
		if (pixelXTickSize / 2 > minXPixelTickSize) {
		    pixelXTickSize /= 2;
		    currentXTickSize /= 2;
		}
	    }
	}

	while (pixelXTickSize > maxXPixelTickSize) {
	    currentXTickSize /= 10;
	    pixelXTickSize = (int) (this.getPlotScaleX() * currentXTickSize);
	    for (int i = 0; i < 2; i++) {
		if (pixelXTickSize * 2 < maxXPixelTickSize) {
		    pixelXTickSize *= 2;
		    currentXTickSize *= 2;
		}
	    }
	}

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

	pixelXTickSize = (int) (this.getPlotScaleX() * currentXTickSize);
	pixelYTickSize = (int) (this.getPlotScaleY() * currentYTickSize);

	//draw max 100 ticks to the right of the origin
	for (int i = 0; i < 100; i++) {
	    if (x - 100 + getPlotX() + i * pixelXTickSize > maxX) {
		break;
	    } else {
		gc.strokeLine(x - 100 + getPlotX() + i * pixelXTickSize, y + getHeight(), x - 100 + getPlotX() + i * pixelXTickSize, y + getHeight() + 5);
		gc.strokeText(Float.toString((float) (i * currentXTickSize)), x - 100 + getPlotX() + i * pixelXTickSize, y + getHeight());
	    }

	}

	//draw max 100 ticks, down from the origin
	for (int i = 0; i < 100; i++) {
	    if (y + getPlotY() + i * pixelYTickSize > maxY) {
		break;
	    } else {
		gc.strokeLine(x, y + getPlotY() + i * pixelYTickSize, x + 5, y + getPlotY() + i * pixelYTickSize);
		gc.strokeText(Float.toString((float) (i * currentYTickSize)), x + 20, y + getPlotY() + i * pixelYTickSize);
	    }

	}
    }
}
