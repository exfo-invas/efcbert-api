package com.invas.enhanced.fc.bert.model.event.disruptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrafficResponse {
    private String type;
    private String fcRate;
    private double actualThroughput;
    private double actualTransferSpeed;
    private double lineSpeed;
    private double currentUtilization;
    private double measuredThroughput;
    private double transferSpeed;
    private double measuredLineSpeed;
}
