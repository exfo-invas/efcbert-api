package com.invas.enhanced.fc.bert.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class LatencyResponse {
    private BigDecimal current;
    private BigDecimal last;
    private BigDecimal min;
    private BigDecimal max;
}
