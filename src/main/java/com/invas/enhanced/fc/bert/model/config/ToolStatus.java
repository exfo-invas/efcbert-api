package com.invas.enhanced.fc.bert.model.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ToolStatus {
    private String coupled;
    private String txPattern;
    private String rxPattern;
    private String fcFrameSize;
    private String trafficShaping;
}
