package com.invas.enhanced.fc.bert.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class HourlyEvent {
    private int no;
    private String utilization;
    private String throughput;
    private String frameLoss;
    private String latency;
}
