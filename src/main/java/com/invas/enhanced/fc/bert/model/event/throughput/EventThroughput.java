package com.invas.enhanced.fc.bert.model.event.throughput;

import com.invas.enhanced.fc.bert.model.event.disruptions.FrameLoss;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventThroughput {

    private ThroughputData[] throughputData;
    private ServiceDisruptions[] serviceDisruptions;
}
