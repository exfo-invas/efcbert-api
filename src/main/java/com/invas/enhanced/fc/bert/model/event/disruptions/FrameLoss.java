package com.invas.enhanced.fc.bert.model.event.disruptions;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FrameLoss {

    private String type;
    private double byteCount;
    private double frameRate;
    private double frameCount;
    private double frameLossRate;

}
