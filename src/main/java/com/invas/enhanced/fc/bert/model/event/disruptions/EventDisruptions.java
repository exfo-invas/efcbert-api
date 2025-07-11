package com.invas.enhanced.fc.bert.model.event.disruptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventDisruptions {
    private ServiceDisruptions[] service;
    private TrafficResponse[] traffic;
}
