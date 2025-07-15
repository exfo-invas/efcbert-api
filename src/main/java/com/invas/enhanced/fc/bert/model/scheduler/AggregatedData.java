package com.invas.enhanced.fc.bert.model.scheduler;

public class AggregatedData {
    
    private String timestamp;
    private int totalEvents;
    private int successfulEvents;
    private int failedEvents;

    public AggregatedData(String timestamp, int totalEvents, int successfulEvents, int failedEvents) {
        this.timestamp = timestamp;
        this.totalEvents = totalEvents;
        this.successfulEvents = successfulEvents;
        this.failedEvents = failedEvents;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getTotalEvents() {
        return totalEvents;
    }

    public int getSuccessfulEvents() {
        return successfulEvents;
    }

    public int getFailedEvents() {
        return failedEvents;
    }
}
