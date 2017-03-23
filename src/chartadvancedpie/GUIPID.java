/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import shuntingyard.Token;

/**
 *
 * @author thegoodhen
 */
public class GUIPID extends GUIelement {

    double output = 0;
    double lastValue = 0;
    double currentValue = 0;
    double sum = 0;
    ArrayList<Token> sampleEvent = null;
    private boolean isSampling = false;
    private Timer sampleTimer;

    private void calculateOutput() {
	double kp = (float) this.getPropertyByName("P").getValueSilent();
	double ki = (float) this.getPropertyByName("I").getValueSilent();
	double kd = (float) this.getPropertyByName("D").getValueSilent();
	double diff = 0;
	sum += currentValue;
	diff = (currentValue - lastValue);
	output = currentValue * kp + sum * ki + diff * kd;
	lastValue = currentValue;

    }

    public final void addProperthies() {
	this.addFloatProperty(210, "SamplePeriod", 1);
	this.addFloatProperty(211, "P", 0.01F);
	this.addFloatProperty(212, "I", 0F);
	this.addFloatProperty(213, "D", 0);
	this.getPropertyByName("Value").setSetterPropertyCallback(new PropertyCallback<Float>() {
	    @Override
	    public void run(Property<Float> p) {
		currentValue = (p.getValueSilent());
		calculateOutput();
	    }
	}
	);
	this.getPropertyByName("Value").setGetterPropertyCallback(new PropertyCallback<Float>() {
	    @Override
	    public void run(Property<Float> p) {
		p.setValueSilent((float) output);
	    }
	}
	);
    }

    public GUIPID() {
	super();
	this.addProperthies();

    }

    public GUIPID(GUITab gut) {
	super(gut);
	this.addProperthies();
	this.setGUIPanel(gut.getGUIPanel());
    }

    //private int value=50;
    public void setValue(int value)//TODO: change this to like IntegerProperty or something and make it generic! :3
    {
	super.setValue(value);
	super.update();
    }

    @Override
    public String shortDesc() {
	return "GUI element - PID regulator";
    }

    @Override
    public void setGUITab(GUITab gut) {
	super.setGUITab(gut);

	RegisterAction yankAction = new RegisterAction("yank (copy)") {

	    @Override
	    public void doAction(String register) {
		if ((register.charAt(0) >= 'a' && register.charAt(0) <= 'z') || (register.charAt(0) >= 'A' && register.charAt(0) <= 'Z')) {
		    GUIPID.this.getGUIPanel().setRegisterContent(register, Float.toString(GUIPID.this.getValue()));
		}
		//GUIPanel.this.setMark(register);
	    }

	    @Override
	    public void doAction(String register, IRepetitionCounter irc) {
		doAction(register);
	    }
	};

	NamedGUIAction startSamplingAction = new NamedGUIAction("Start/stop sampling") {
	    @Override
	    public void doAction() {

		GUIPID.this.startStopSampling();
	    }

	};

	NamedGUIAction unnamedRegisterYankAction = new NamedGUIAction("yank (copy)") {
	    @Override
	    public void doAction() {

		GUIPID.this.getGUIPanel().setRegisterContent("%", Float.toString(GUIPID.this.getValue()));
	    }

	    @Override
	    public void doAction(IRepetitionCounter irc) {
		GUIPID.this.getGUIPanel().setRegisterContent("%", Float.toString(GUIPID.this.getValue()));
	    }

	};

	NamedGUIAction unnamedRegisterPasteAction = new NamedGUIAction("paste") {
	    @Override
	    public void doAction() {

		String content = GUIPID.this.getGUIPanel().getRegisterContent("%");
		try {
		    GUIPID.this.setValue((int) Float.parseFloat(content));
		} catch (Exception e) {

		}

	    }

	    @Override
	    public void doAction(IRepetitionCounter irc) {
		doAction();
	    }

	};

	RegisterActionMenu ram = new RegisterActionMenu(this.getGUIPanel(), "kokodak");
	ram.addRegisterAction("y", yankAction);
	Menu selectRegMenu = new RegisterSelectionMenu(GUIPID.this.getGUIPanel(), "access register", ram);

	this.setMenu(new Menu(gut.getGUIPanel(), "display menu", true));
	this.getMenu().addAction("y", unnamedRegisterYankAction);
	this.getMenu().addAction("p", unnamedRegisterPasteAction);
	this.getMenu().addAction("s", startSamplingAction);
	this.getMenu().addSubMenu("\"", selectRegMenu);
	ram.setSuperMenu(this.getMenu());
    }

    public String getName() {
	return "pid";
    }

    @Override
    public String getGUIelementName() {
	return "PID_REGULATOR";
    }

    public void startStopSampling() {
	if (isSampling) {
	    isSampling = false;
	    if (sampleTimer != null) {
		sampleTimer.cancel();
	    }
	    return;
	} else {

	    isSampling = true;
	}
	//lastRecordingStartTime = System.currentTimeMillis();
	    sampleTimer = new Timer();
	sampleTimer.schedule(
		new TimerTask() {
		    @Override
		    public void run() {
			System.out.println("kokodak");
			if (sampleEvent != null) {
			    Platform.runLater(() -> {//TODO: only surround the necessary stuff in runLater!
				GUIPID.this.getGUIPanel().handleCallBack(sampleEvent);//call the user event
			    });
			    //GUIPID.this.sampleAllRelevant();
			}
		    }
		}, 0, 100);
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

    public String getSampleEventString() {
	return this.getUniqueName() + "_Sample();\n";
    }

    public void paint(GraphicsContext gc, double x, double y) {
	super.paint(gc, x, y);
	/*
	 if (getRegister() != null) {
	 gc.strokeText(getRegister().getName().toString(), x, y + 20);
	 }
	 */
	gc.setStroke(Color.WHITESMOKE);
	gc.strokeText(Float.toString(this.getValue()), x, y + 10);
    }

    /*
    @Override
    public GUIelement makeCopy() {
	GUIPID cb = new GUIPID();
	this.copyPropertiesTo(cb);
	return cb;
    }
    */

    private void addSample(Float value) {
    }

}
