/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author thegoodhen
 */
public class GUIStatsDisplay extends GUIelement {

    LinkedList<TimeStampedSample> delayList;//buffer for delaying the samples before they are passed to processing
    LinkedList<TimeStampedSample> workingList;//buffer for calculating the operations on
    LinkedList<Double> samplesSortedByValueList;
    double sum = 0;
    double squaresSum = 0;
    int currentStatgenIndex;
    boolean timeInSamples = false;//whether the times for delay and windows size are actually in number of samples, as opposed to seconds.

    private ArrayList<statGenerator> statGenList;
    private double lastValue;

    private abstract class statGenerator {

	private String statName = "";

	public double getStat() {
	    return 0;
	}

	public statGenerator(String s) {
	    this.statName = s;
	}
    }

    public final void addProperthies() {
	this.addFloatProperty(200, "Delay", 2);
	this.addFloatProperty(201, "WindowWidth", 10);
	this.getPropertyByName("Value").setSetterPropertyCallback(
		new PropertyCallback<Float>() {
		    @Override
		    public void run(Property<Float> p) {
			GUIStatsDisplay.this.addSample(p.getValueSilent());
		    }
		}
	);
	this.getPropertyByName("Value").setGetterPropertyCallback(
		new PropertyCallback<Float>() {
		    @Override
		    public void run(Property<Float> p) {
			p.setValueSilent((float) GUIStatsDisplay.this.getCurrentStat());
		    }
		}
	);
    }

    public GUIStatsDisplay() {
	super();
	this.addProperthies();

    }

    public GUIStatsDisplay(GUITab gut) {
	super(gut);
	this.setGUIPanel(gut.getGUIPanel());
	delayList = new LinkedList<>();
	workingList = new LinkedList<>();
	samplesSortedByValueList = new LinkedList<>();
	statGenList = new ArrayList<>();

	this.addProperthies();
	//super(r);

	statGenerator rawValueStatGen = new statGenerator("Raw value") {
	    @Override
	    public double getStat() {
		return lastValue;
	    }
	};
	statGenerator sumStatGen = new statGenerator("Sum") {
	    @Override
	    public double getStat() {
		return sum;
	    }
	};
	statGenerator squaredSumStatGen = new statGenerator("Sum of squares") {
	    @Override
	    public double getStat() {
		return squaresSum;
	    }
	};

	statGenerator avgStatGen = new statGenerator("Average") {
	    @Override
	    public double getStat() {
		return sum / workingList.size();
	    }
	};

	statGenerator squaresAvgStatGen = new statGenerator("Average of squares") {
	    @Override
	    public double getStat() {
		return squaresSum / workingList.size();
	    }
	};

	statGenerator divergenceStatGen = new statGenerator("Divergence") {
	    @Override
	    public double getStat() {
		double avg = sum / workingList.size();
		return (squaresSum / workingList.size()) - avg * avg;//E(x^2)-E(x)^2
	    }
	};

	statGenerator stdevStatGen = new statGenerator("Standard deviation") {
	    @Override
	    public double getStat() {
		return Math.sqrt(divergenceStatGen.getStat());
	    }
	};

	statGenerator minStatGen = new statGenerator("Minimum") {
	    @Override
	    public double getStat() {
		return samplesSortedByValueList.getFirst();
	    }
	};

	statGenerator maxStatGen = new statGenerator("Maximum") {
	    @Override
	    public double getStat() {
		return samplesSortedByValueList.getLast();
	    }
	};
	statGenList.add(rawValueStatGen);
	statGenList.add(avgStatGen);
	statGenList.add(sumStatGen);
	statGenList.add(minStatGen);
	statGenList.add(maxStatGen);
	statGenList.add(divergenceStatGen);
	statGenList.add(stdevStatGen);
	statGenList.add(squaresAvgStatGen);
	statGenList.add(squaredSumStatGen);
    }

    private void switchStats(int number) {
	currentStatgenIndex += number;
	if (currentStatgenIndex >= statGenList.size()) {
	    currentStatgenIndex = currentStatgenIndex - statGenList.size();//wrap on overflow
	}
	if (currentStatgenIndex < 0) {
	    currentStatgenIndex = currentStatgenIndex + statGenList.size();//wrap on overflow
	}
    }

    //private int value=50;
    public void setValue(int value)//TODO: change this to like IntegerProperty or something and make it generic! :3
    {
	super.setValue(value);
	super.update();
    }

    @Override
    public String shortDesc() {
	return "GUI element - statistical display";
    }

    @Override
    public void setGUITab(GUITab gut) {
	super.setGUITab(gut);

	RegisterAction yankAction = new RegisterAction("yank (copy)") {

	    @Override
	    public void doAction(String register) {
		if ((register.charAt(0) >= 'a' && register.charAt(0) <= 'z') || (register.charAt(0) >= 'A' && register.charAt(0) <= 'Z')) {
		    GUIStatsDisplay.this.getGUIPanel().setRegisterContent(register, Float.toString(GUIStatsDisplay.this.getValue()));
		}
		//GUIPanel.this.setMark(register);
	    }

	    @Override
	    public void doAction(String register, IRepetitionCounter irc) {
		doAction(register);
	    }
	};

	NamedGUIAction unnamedRegisterYankAction = new NamedGUIAction("yank (copy)") {
	    @Override
	    public void doAction() {

		GUIStatsDisplay.this.getGUIPanel().setRegisterContent("%", Float.toString(GUIStatsDisplay.this.getValue()));
	    }

	    @Override
	    public void doAction(IRepetitionCounter irc) {
		GUIStatsDisplay.this.getGUIPanel().setRegisterContent("%", Float.toString(GUIStatsDisplay.this.getValue()));
	    }

	};

	NamedGUIAction unnamedRegisterPasteAction = new NamedGUIAction("paste") {
	    @Override
	    public void doAction() {

		String content = GUIStatsDisplay.this.getGUIPanel().getRegisterContent("%");
		try {
		    GUIStatsDisplay.this.setValue((int) Float.parseFloat(content));
		} catch (Exception e) {

		}

	    }

	    @Override
	    public void doAction(IRepetitionCounter irc) {
		doAction();
	    }

	};

	NamedGUIAction nextStatAction = new NamedGUIAction("next statistic") {
	    @Override
	    public void doAction() {
		switchStats(this.getCount());
	    }
	};

	NamedGUIAction prevStatAction = new NamedGUIAction("previous statistic") {
	    @Override
	    public void doAction() {
		switchStats(-this.getCount());
	    }
	};
	NamedGUIAction switchSamplesSecondsAction = new NamedGUIAction("switch between samples and seconds") {
	    @Override
	    public void doAction() {
		timeInSamples = !timeInSamples;
	    }
	};

	RegisterActionMenu ram = new RegisterActionMenu(this.getGUIPanel(), "kokodak");
	ram.addRegisterAction("y", yankAction);
	Menu selectRegMenu = new RegisterSelectionMenu(GUIStatsDisplay.this.getGUIPanel(), "access register", ram);

	this.setMenu(new Menu(gut.getGUIPanel(), "display menu", true));
	this.getMenu().addAction("y", unnamedRegisterYankAction);
	this.getMenu().addAction("p", unnamedRegisterPasteAction);
	this.getMenu().addSubMenu("\"", selectRegMenu);
	this.getMenu().addAction("s", switchSamplesSecondsAction);
	this.getMenu().addAction("k", prevStatAction);
	this.getMenu().addAction("j", nextStatAction);
	ram.setSuperMenu(this.getMenu());
    }

    public String getName() {
	return "stats display";
    }

    @Override
    public String getGUIelementName() {
	return "STATS_DISPLAY";
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
	String unitString = (this.timeInSamples ? "samples" : "seconds");
	double delay = (float) this.getPropertyByName("Delay").getValueSilent();
	String delayString = "";
	if (delay != 0) {
	    delayString = ", delayed by " + (delay) + " " + unitString;
	}
	double winWidth = (float) this.getPropertyByName("WindowWidth").getValue();
	if (timeInSamples) {
	    winWidth = Math.floor(winWidth);
	}
	String infoText = "(" + this.statGenList.get(currentStatgenIndex).statName + ", window width " + winWidth + " " + unitString + delayString + ")";
	gc.strokeText(infoText, x + 40, y + 10);
    }

    @Override
    public GUIelement makeCopy() {
	GUIStatsDisplay cb = new GUIStatsDisplay();
	this.copyPropertiesTo(cb);
	return cb;
    }

    private void addSample(Float value) {
	delayList.addLast(new TimeStampedSample(value));
	this.updateStats();
    }

    private void addSampleToWorkingList(TimeStampedSample sample) {
	workingList.addLast(sample);
	sum += sample.getValue();
	this.lastValue = sample.getValue();
	this.squaresSum += sample.getValue() * sample.getValue();
	this.samplesSortedByValueList.add(sample.getValue());
	this.samplesSortedByValueList.sort(null);
    }

    private void clearUpWorkingList() {
	if (!workingList.isEmpty()) {
	    long delay = (long) ((float) this.getPropertyByName("Delay").getValueSilent()) * 1000;
	    long windowWidth = (long) ((float) this.getPropertyByName("WindowWidth").getValueSilent()) * 1000;
	    long windowWidthInSamples = (long) ((float) this.getPropertyByName("WindowWidth").getValueSilent());
	    while (!workingList.isEmpty() && ((!timeInSamples && workingList.getFirst().isOlderThan(delay + windowWidth)) || (timeInSamples && workingList.size() > windowWidthInSamples))) {
		TimeStampedSample s = workingList.removeFirst();
		System.out.println("removing" + s.getValue());
		this.sum -= s.getValue();
		this.samplesSortedByValueList.remove(s.getValue());
		this.samplesSortedByValueList.sort(null);
		this.squaresSum -= s.getValue() * s.getValue();
	    }
	}
    }

    private void updateStats() {
	if (delayList.isEmpty()) {
	    clearUpWorkingList();
	    return;
	}
	TimeStampedSample oldestSample = delayList.getFirst();
	long delay = (long) ((float) this.getPropertyByName("Delay").getValueSilent()) * 1000;
	long delaySamples = (long) ((float) this.getPropertyByName("Delay").getValueSilent());
	if ((!timeInSamples && oldestSample.isOlderThan(delay)) || (timeInSamples && delayList.size() > delaySamples)) {
	    delayList.removeFirst();
	    addSampleToWorkingList(oldestSample);
	    clearUpWorkingList();
	}
    }

    public double getCurrentStat() {
	System.out.println("returning current stat");
	this.updateStats();
	if (workingList.size() == 0) {
	    return 0;
	}
	//return (this.squaresSum / workingList.size()) -avg*avg;//E(x^2)-E(x)^2
	return this.statGenList.get(currentStatgenIndex).getStat();
    }
}
