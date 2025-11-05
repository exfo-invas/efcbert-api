package com.invas.enhanced.fc.bert.model.event;

public class HourlyCounter {
    private int hoursElapsed;
    private boolean isReady;

    public HourlyCounter(int hoursElapsed, boolean isReady) {
        this.hoursElapsed = hoursElapsed;
        this.isReady = isReady;
    }

    public int getHoursElapsed() {
        return hoursElapsed;
    }

    public void setHoursElapsed(int hoursElapsed) {
        this.hoursElapsed = hoursElapsed;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }
}
