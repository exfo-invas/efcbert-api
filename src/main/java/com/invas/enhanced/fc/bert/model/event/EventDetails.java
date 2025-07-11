package com.invas.enhanced.fc.bert.model.event;

import com.invas.enhanced.fc.bert.model.event.disruptions.EventDisruptions;
import com.invas.enhanced.fc.bert.model.event.throughput.EventThroughput;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventDetails {
    private EventDisruptions eventDisruptions;
    private EventThroughput eventThroughput;
}
