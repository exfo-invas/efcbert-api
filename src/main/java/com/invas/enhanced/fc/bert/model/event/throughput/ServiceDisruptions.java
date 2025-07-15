package com.invas.enhanced.fc.bert.model.event.throughput;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceDisruptions {
    private String type;
    private String speed;
    private String frameSize;
    private String lineDataRate;
    private String txUtilization;
    private String throughput;
}
