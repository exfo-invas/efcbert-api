package com.invas.enhanced.fc.bert.model.scheduler;

public class EventData {

    private String eventType;
    private String timestamp;
    private String details;

    public EventData(String eventType, String timestamp, String details) {
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.details = details;
    }

    public String getEventType() {
        return eventType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getDetails() {
        return details;
    }
}