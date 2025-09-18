package com.invas.enhanced.fc.bert.controller.event;

import com.invas.enhanced.fc.bert.config.EventAggregatorConfig;
import com.invas.enhanced.fc.bert.model.event.EventDisruptions;

import java.util.ArrayList;

import com.invas.enhanced.fc.bert.model.event.HourlyEvent;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<EventDisruptions> getEventDetails() {
        EventDisruptions eventDisruptions = eventAggregatorConfig.getLatestEventDisruption();
        if (eventDisruptions == null) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(eventDisruptions);
    }

    @GetMapping("/details/hourly")
    public ArrayList<HourlyEvent> getHourlyDisruptionsList() {
        return eventAggregatorConfig.getHourlyEventList();
    }

}
