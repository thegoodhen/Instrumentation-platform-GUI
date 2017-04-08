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
public class GUITimer extends GUIelement {

    double output = 0;
    double lastValue = 0;
    double currentValue = 0;
    double sum = 0;
    ArrayList<Token> sampleEvent = null;
    private boolean isSampling = false;
    private Timer sampleTimer;
    private Timer refreshTimer;

    private long lastSamplingStartTime;

    public final void addProperthies() {
	this.addFloatProperty(215, "Period", 1);//TODO: hope 215 is ok :X

	FloatProperty runTime = new FloatProperty(150, "RunTime", -1.0F, this);
	runTime.setGetterPropertyCallback(new PropertyCallback<Float>() {
	    @Override
	    public void run(Property<Float> p) {
		System.out.println("slepice v optice kdaka velmi velice");
		if (!isSampling) {
		    p.setValueSilent((float) -1);
		} else {
		    p.setValueSilent((float) (System.currentTimeMillis() - lastSamplingStartTime) / 1000);
		}
	    }
	}
	);
	this.addProperty(runTime);
	IntegerProperty enabled = new IntegerProperty(216, "Enabled", 0, this);
	enabled.setSetterPropertyCallback(new PropertyCallback<Integer>() {
	    @Override
	    public void run(Property<Integer> p) {
		if (p.getValueSilent() == 0) {
		    if (isSampling) {
			GUITimer.this.startStopSampling();
		    }
		} else {

		    if (!isSampling) {
			GUITimer.this.startStopSampling();
		    }
		}
	    }
	}
	);
	this.addProperty(enabled);
    }

    public GUITimer() {
	super();
	this.addProperthies();

    }

    public GUITimer(GUITab gut) {
	super(gut);
	this.addProperthies();
	this.getPropertyByName("Width").setValueSilent(300);
	this.setGUIPanel(gut.getGUIPanel());

	refreshTimer = new Timer();
	refreshTimer.schedule(new TimerTask() {
	    @Override
	    public void run() {
		//System.out.println("kokonptak");
		Platform.runLater(() -> {//TODO: only surround the necessary stuff in runLater!
		    GUITimer.this.requestRepaint();
		});
		//GUIPID.this.sampleAllRelevant();
	    }
	}, 0, 200);
	this.setName("timer");
    }

    //private int value=50;
    public void setValue(int value)//TODO: change this to like IntegerProperty or something and make it generic! :3
    {
	super.setValue(value);
	super.update();
    }

    @Override
    public String shortDesc() {
	return "GUI element - Timer";
    }

    @Override
    public void setGUITab(GUITab gut) {
	super.setGUITab(gut);

	RegisterAction yankAction = new RegisterAction("yank (copy)") {

	    @Override
	    public void doAction(String register) {
		if ((register.charAt(0) >= 'a' && register.charAt(0) <= 'z') || (register.charAt(0) >= 'A' && register.charAt(0) <= 'Z')) {
		    GUITimer.this.getGUIPanel().setRegisterContent(register, Float.toString(GUITimer.this.getValue()));
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

		GUITimer.this.startStopSampling();
	    }

	};

	NamedGUIAction unnamedRegisterYankAction = new NamedGUIAction("yank (copy)") {
	    @Override
	    public void doAction() {

		GUITimer.this.getGUIPanel().setRegisterContent("%", Float.toString(GUITimer.this.getValue()));
	    }

	    @Override
	    public void doAction(IRepetitionCounter irc) {
		GUITimer.this.getGUIPanel().setRegisterContent("%", Float.toString(GUITimer.this.getValue()));
	    }

	};

	NamedGUIAction unnamedRegisterPasteAction = new NamedGUIAction("paste") {
	    @Override
	    public void doAction() {

		String content = GUITimer.this.getGUIPanel().getRegisterContent("%");
		try {
		    GUITimer.this.setValue((int) Float.parseFloat(content));
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
	Menu selectRegMenu = new RegisterSelectionMenu(GUITimer.this.getGUIPanel(), "access register", ram);

	this.setMenu(new Menu(gut.getGUIPanel(), "timer menu", true));
	//this.getMenu().addAction("y", unnamedRegisterYankAction);
	//this.getMenu().addAction("p", unnamedRegisterPasteAction);
	this.getMenu().addAction("s", startSamplingAction);
	//this.getMenu().addSubMenu("\"", selectRegMenu);
	ram.setSuperMenu(this.getMenu());
    }

    @Override
    public String getGUIelementName() {
	return "TIMER";
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

	lastSamplingStartTime = System.currentTimeMillis();
	sampleTimer = new Timer();
	sampleTimer.schedule(new TimerTask() {
	    @Override
	    public void run() {
		//System.out.println("kokodak");
		if (sampleEvent != null) {
		    Platform.runLater(() -> {//TODO: only surround the necessary stuff in runLater!
			GUITimer.this.getGUIPanel().handleCallBack(sampleEvent);//call the user event
		    });
		    //GUIPID.this.sampleAllRelevant();
		}
	    }
	}, 0, (long) ((float) this.getPropertyByName("Period").getValue() * 1000));
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
	return this.getUniqueName() + "_Tick();\n";
    }

    public void paint(GraphicsContext gc, double x, double y) {
	super.paint(gc, x, y);
	/*
	 if (getRegister() != null) {
	 gc.strokeText(getRegister().getName().toString(), x, y + 20);
	 }
	 */
	gc.setStroke(Color.WHITESMOKE);
	String runningStoppedString;
	if (this.isSampling) {
	    runningStoppedString = "running for " + this.getPropertyByName("RunTime").getValue(true, false) + "  seconds ";
	} else {
	    runningStoppedString = "stopped ";
	}
	String text = "Timer, " + runningStoppedString + "tick period" + this.getPropertyByName("Period").getValue(true, false);
	gc.strokeText(text, x, y + 10);
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
