/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import shuntingyard.Token;

/**
 *
 * @author thegoodhen
 */
public class GUINumericUpDown extends GUIelement {

    double delta = 10;//the delta, signifying by how much should the value be increased; this is related to where the cursor is.
    int cursorPos = 0;//the position of the cursor (which digit will be incremented) - zero based
    private int dotPos = 1;
    double preciseValue = 0;
    static MappingManager elementTypeMappingManager;

    public final void addProperthies() {

	this.getPropertyByName("Value").setGetterPropertyCallback(new PropertyCallback<Float>() {
	    @Override
	    public void run(Property<Float> p) {
		p.setValueSilent((float) preciseValue);
	    }
	}
	);

	this.getPropertyByName("Value").setSetterPropertyCallback(new PropertyCallback<Float>() {
	    @Override
	    public void run(Property<Float> p) {
		preciseValue = p.getValue();
	    }
	}
	);

	GUINumericUpDown.this.getPropertyByName("Height").setValueSilent(50);
	GUINumericUpDown.this.getPropertyByName("Width").setValueSilent(250);
    }

    public GUINumericUpDown() {
	super();
	this.addProperthies();

    }

    /**
     * Based on te dotPos field, this method takes a series of digits as string
     * and adds the dot there
     *
     * @param dotlessString
     * @return
     */
    private String fillTheDotAndSign(String dotlessString) {
	StringBuilder sb = new StringBuilder();
	//float f=(float)this.getPropertyByName("Value").getValueSilent();
	if (preciseValue < 0) {
	    sb.append("-");
	}
	for (int i = 0; i < dotlessString.length(); i++) {
	    sb.append(dotlessString.charAt(i));
	    if (i == dotPos) {
		sb.append(".");
	    }

	}
	return sb.toString();

    }

    private String getNumberString() {
	return String.format(Locale.US,"%.5f", preciseValue);
    }

    private String getDotLessString() {

	String numAsString=getNumberString();
	numAsString = numAsString.replaceAll("[^0-9]", "");
	return numAsString;
    }

    NamedGUIAction cursorLeftAction = new NamedGUIAction("cursor to the left") {
	@Override
	public void doAction() {
	    /*
	     if (cursorPos == 0) {
	     return;
	     }
	     */
	    cursorPos -= this.getCount();
	    delta *= Math.pow(10, this.getCount());

	}
    };

    NamedGUIAction cursorRightAction = new NamedGUIAction("cursor to the right") {
	@Override
	public void doAction() {
	    /*
	     if (cursorPos == 6) {
	     return;
	     }
	     */
	    cursorPos += this.getCount();
	    delta /= Math.pow(10, this.getCount());
	}
    };

    NamedGUIAction cycleUpAction = new NamedGUIAction("cycle up (add)") {
	@Override
	public void doAction() {
	    preciseValue += delta;
	    updateCursorAndDotPosition();
	}
    };

    NamedGUIAction cycleDownAction = new NamedGUIAction("cycle down (subtract)") {
	@Override
	public void doAction() {
	    // FloatProperty val = (FloatProperty) GUINumericUpDown.this.getPropertyByName("Value");
	    preciseValue -= delta;
	    updateCursorAndDotPosition();
	}
    };

    NamedGUIAction zeroCurrentDigit = new NamedGUIAction("zero current digit") {
	@Override
	public void doAction() {
	    if (cursorPos < 0 || cursorPos > 5) {
		return;
	    }
	    byte[] digits = getDotLessString().getBytes();
	    digits[cursorPos] = '0';
	    String newString = new String(digits);
	    newString = fillTheDotAndSign(newString);
	    preciseValue = Double.parseDouble(newString);
	    GUINumericUpDown.this.updateCursorAndDotPosition();
	    //GUINumericUpDown.this.getPropertyByName("Value").setValueSilent(Float.parseFloat(newString));

	}
    };

    public GUINumericUpDown(GUITab gut) {
	super(gut);
	this.addProperthies();
	this.setName("UPDOWN");
	this.setGUIPanel(gut.getGUIPanel());
	elementTypeMappingManager=new MappingManager(gut.getGUIPanel());
    }

    public static MappingManager getElementTypeMappingManager() {
	return elementTypeMappingManager;
    }

    private int getIndexOfLastDigitBeforeDot() {
	//double val = (double) Math.abs((float) this.getPropertyByName("Value").getValueSilent());
	/*
	if (preciseValue < 10) {
	    return 0;
	} else {
	    return (int) Math.floor(Math.log10(preciseValue));
	}
	*/
	String s=this.getNumberString();
	s=s.replace("-", "");
	return s.indexOf(".")-1;
    }

    private void updateCursorAndDotPosition() {
	int lastDigitBeforeDotIndex = getIndexOfLastDigitBeforeDot();
	//double val = Math.abs((float) this.getPropertyByName("Value").getValueSilent());
	int offset = (int) Math.round(Math.log10(delta));
	this.cursorPos = lastDigitBeforeDotIndex - offset;
	this.dotPos = lastDigitBeforeDotIndex;
	System.out.println("curPos: " + this.cursorPos);
    }

    //private int value=50;
    public void setValue(int value)//TODO: change this to like IntegerProperty or something and make it generic! :3
    {
	super.setValue(value);
	super.update();
    }

    @Override
    public String shortDesc() {
	return "GUI element - Numeric Up/Down";
    }

    @Override
    public void setGUITab(GUITab gut) {
	super.setGUITab(gut);

	this.setMenu(new Menu(gut.getGUIPanel(), "numUpDown menu", true));
	this.getMenu().addAction("h", cursorLeftAction);
	this.getMenu().addAction("l", cursorRightAction);
	this.getMenu().addAction("j", cycleDownAction);
	this.getMenu().addAction("k", cycleUpAction);
	this.getMenu().addAction("r", zeroCurrentDigit);
    }

    public String getName() {
	return "NumUpDown";
    }

    @Override
    public String getGUIelementName() {
	return "UPDOWN";
    }

    public void paint(GraphicsContext gc, double x, double y) {
	super.paint(gc, x, y);
	/*
	 if (getRegister() != null) {
	 gc.strokeText(getRegister().getName().toString(), x, y + 20);
	 }
	 */
	if (cursorPos < 0 || cursorPos > 5) {
	    gc.setStroke(this.getColor3());
	    gc.strokeText(String.format("%6.0e", delta), x, y + 5);
	}
	gc.setStroke(Color.WHITESMOKE);
	Font f = gc.getFont();
	gc.setFont(new Font("default", 50));
	String numAsString = getDotLessString();
	//numAsString = numAsString.replaceAll("[^0-9]", "");
	if (numAsString.length() > 6) {
	    numAsString = numAsString.substring(0, 6);
	}
	if (this.preciseValue < 0) {
	    gc.setFill(this.getColor1());

	    gc.fillText("-", x + 10, y + 40);
	}
	for (int i = 0; i < numAsString.length(); i++) {
	    if (i == cursorPos) {
		gc.setFill(this.getColor2());
	    } else {
		gc.setFill(this.getColor1());
	    }
	    if (i != dotPos) {
		gc.fillText("" + numAsString.charAt(i), x + i * 35 + 30, y + 40);
	    } else {
		gc.fillText("" + numAsString.charAt(i) + ".", x + i * 35 + 30, y + 40);
	    }
	}
	gc.setFont(f);
    }

    /*
     @Override
     public GUIelement makeCopy() {
     GUIPID cb = new GUIPID();
     this.copyPropertiesTo(cb);
     return cb;
     }
     */
}
