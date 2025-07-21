package com.invas.enhanced.fc.bert.controller.event;

import com.invas.enhanced.fc.bert.config.EventAggregatorConfig;
import com.invas.enhanced.fc.bert.model.event.disruptions.EventDisruptions;
import com.invas.enhanced.fc.bert.model.event.disruptions.*;

import java.util.ArrayList;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
public class EventController {

    private final EventAggregatorConfig eventAggregatorConfig;

    public EventController(EventAggregatorConfig eventAggregatorConfig) {
        this.eventAggregatorConfig = eventAggregatorConfig;
    }

    @GetMapping("/details")
    public EventDisruptions getEventDetails() {
        TrafficResponse[] traffic = {
            new TrafficResponse("tx", "1x", 950, 50, 5.0, 900, 45, 4.5, 4.0),
            new TrafficResponse("rx", "1x", 950, 50, 5.0, 900, 45, 4.5, 4.0)
        };
        FrameLoss frameLoss = new FrameLoss("10G", 1000, 950, 50, 5.0);
        
        return new EventDisruptions(
            traffic,
            frameLoss
        );
        //return eventAggregatorConfig.getLatestEventDisruption();
    }

    @GetMapping("/details/hourly")
    public ArrayList<EventDisruptions> getHourlyDisruptionsList() {
        return eventAggregatorConfig.getHourlyEventList();
    }

}
