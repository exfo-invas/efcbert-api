package com.invas.enhanced.fc.bert.model.event.disruptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrafficResponse {
    private String type;
    private String fcRate;
    private String protocol;
    private String encoding;
    private String actualThroughput;
    private String actualTransferSpeed;
    private String lineSpeed;
    private String currentUtilization;
    private String measuredThroughput;
    private String transferSpeed;
    private String measuredLineSpeed;
}
