/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

/**
 * A utility class for storing value of a sample and a time, when it was taken.
 * @author thegoodhen
 */
public class TimeStampedSample {

    private final double value;
    private final long sampleTime;

    public TimeStampedSample(double value) {
	this.value = value;
	this.sampleTime = System.currentTimeMillis();
    }
    /**
     * Return the time in millis, when this sample was taken.
     * This is consistent with the {@link System#currentTimeMillis()} method.
     * 
     * @return the time in millis, when this sample was taken.
     * 
     */
    public long getSampleTime()
    {
	return this.sampleTime;
    }

    /**
     * Get the value of this sample.
     * @return the value of this sample.
     */
    public double getValue()
    {
	return this.value;
    }

    /**
     * Whether the sample has been taken more than n millis ago.
     * @param millis
     * @return whether the sample has been taken more than n millis ago.
     */
    public boolean isOlderThan(long millis) {
	return sampleTime < System.currentTimeMillis() - millis;
    }

}
