package com.invas.enhanced.fc.bert.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrafficResponse {
    private String type;
    private BigDecimal currentUtilization;
    private BigDecimal measuredThroughput;
    private BigDecimal transferSpeed;
    private BigDecimal measuredLineSpeed;
}
