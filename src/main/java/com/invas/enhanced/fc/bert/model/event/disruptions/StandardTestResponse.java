package com.invas.enhanced.fc.bert.model.event.disruptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StandardTestResponse {
    private String fcRate;
    private double frameSize;
}
