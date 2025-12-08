package com.invas.enhanced.fc.bert.model.event;

public class HourlyCounter {
    private String hoursElapsed;
    private boolean isReady;

    public HourlyCounter(String hoursElapsed, boolean isReady) {
        this.hoursElapsed = hoursElapsed;
        this.isReady = isReady;
    }

    public String getHoursElapsed() {
        return hoursElapsed;
    }

    public void setHoursElapsed(String hoursElapsed) {
        this.hoursElapsed = hoursElapsed;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }
}
