package com.invas.enhanced.fc.bert.model.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HourlyCounter {
    private String hoursElapsed;
    private boolean isReady;

    public HourlyCounter(String hoursElapsed, boolean isReady) {
        this.hoursElapsed = hoursElapsed;
        this.isReady = isReady;
    }

}
