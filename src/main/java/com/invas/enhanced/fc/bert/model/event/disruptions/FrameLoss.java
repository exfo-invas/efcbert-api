package com.invas.enhanced.fc.bert.model.event.disruptions;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FrameLoss {

    private String fcRate;
    private int txCount;
    private int rxCount;
    private int lostFrames;
    private double frameLossRate;

}
