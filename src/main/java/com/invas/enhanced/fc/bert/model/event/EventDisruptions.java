package com.invas.enhanced.fc.bert.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDisruptions {
    private TrafficResponse[] traffic;
    private FrameLoss[] frameLoss;
    private StandardTestResponse standard;
    private LatencyResponse latency;
}
