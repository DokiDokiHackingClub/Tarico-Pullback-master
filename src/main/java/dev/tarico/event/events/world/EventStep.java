package dev.tarico.event.events.world;

import dev.tarico.event.Event;

@SuppressWarnings("unused")
public class EventStep extends Event {
    private final boolean pre;
    private double stepHeight;
    private double realHeight;

    public EventStep(boolean state, double stepHeight, double realHeight) {
        this.pre = state;
        this.stepHeight = stepHeight;
        this.realHeight = realHeight;
    }

    public EventStep(boolean state, double stepHeight) {
        this.pre = state;
        this.stepHeight = stepHeight;
    }

    public boolean isPre() {
        return this.pre;
    }

    public double getStepHeight() {
        return this.stepHeight;
    }

    public void setStepHeight(double stepHeight) {
        this.stepHeight = stepHeight;
    }

    public double getRealHeight() {
        return this.realHeight;
    }

    public void setRealHeight(double realHeight) {
        this.realHeight = realHeight;
    }
}
