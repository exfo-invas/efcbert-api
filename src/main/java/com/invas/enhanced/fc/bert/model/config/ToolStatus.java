package com.invas.enhanced.fc.bert.model.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ToolStatus {
    private String coupledStatus;
    private String patternStatus;
    private String frameStatus;
    private String streamStatus;
}
