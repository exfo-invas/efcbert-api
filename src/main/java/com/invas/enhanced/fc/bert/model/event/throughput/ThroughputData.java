package com.invas.enhanced.fc.bert.model.event.throughput;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThroughputData {
    private String type;
    private String fcRate;
    private String frameSize;
    private String fullLineRate;
    private String measureRate;
    private String framesLossRate;
}
