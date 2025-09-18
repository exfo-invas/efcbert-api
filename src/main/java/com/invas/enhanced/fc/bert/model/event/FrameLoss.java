package com.invas.enhanced.fc.bert.model.event;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FrameLoss {

    private String type;
    private BigDecimal byteCount;
    private BigDecimal frameRate;
    private BigDecimal frameCount;
    private BigDecimal frameLossRate;

}
