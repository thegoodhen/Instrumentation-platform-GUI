/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

/**
 *
 * @author thegoodhen
 */
public class TimeStampedSample {

    private final double value;
    private final long sampleTime;

    public TimeStampedSample(double value) {
	this.value = value;
	this.sampleTime = System.currentTimeMillis();
    }
    public long getSampleTime()
    {
	return this.sampleTime;
    }

    public double getValue()
    {
	return this.value;
    }

    /**
     * Whether the sample has been taken more than n millis ago.
     * @param millis
     * @return 
     */
    public boolean isOlderThan(long millis) {
	return sampleTime < System.currentTimeMillis() - millis;
    }

}
