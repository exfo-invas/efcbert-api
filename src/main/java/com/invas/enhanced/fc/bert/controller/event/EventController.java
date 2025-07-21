package com.invas.enhanced.fc.bert.controller.event;

import com.invas.enhanced.fc.bert.config.EventAggregatorConfig;
import com.invas.enhanced.fc.bert.model.event.disruptions.EventDisruptions;

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
        return eventAggregatorConfig.getLatestEventDisruption();
    }

    @GetMapping("/details/hourly")
    public ArrayList<EventDisruptions> getHourlyDisruptionsList() {
        return eventAggregatorConfig.getHourlyEventList();
    }

}
