package com.pyshankov.microservices.circuit.breaker;

/**
 * Created by pyshankov on 4/3/17.
 */
public class CircuitBreaker {

    private boolean enabled;

    private long timeStamp;

    private long timePeriodEnabled;

    public CircuitBreaker(long timePeriodEnabled) {
        enabled = false;
        timeStamp = System.currentTimeMillis();
        this.timePeriodEnabled = timePeriodEnabled;
    }

    public boolean isEnabled() {
        if (System.currentTimeMillis() - timeStamp > timePeriodEnabled) {
            enabled = false;
        }
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        timeStamp = System.currentTimeMillis();
        this.enabled = enabled;
    }
}
