package com.invas.enhanced.fc.bert.model.event.throughput;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FrameLoss {

    private String type;
    private String frameRate;
    private String totalTransmittedBytes;
    private String framesTransmitted;

}
