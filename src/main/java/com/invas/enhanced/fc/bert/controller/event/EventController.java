package com.invas.enhanced.fc.bert.controller.event;

import com.invas.enhanced.fc.bert.config.EventAggregatorConfig;
import com.invas.enhanced.fc.bert.model.event.*;

import java.awt.*;
import java.util.ArrayList;

import com.invas.enhanced.fc.bert.service.event.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
public class EventController {

    private final EventAggregatorConfig eventAggregatorConfig;
    private final EventService eventService;

    public EventController(EventAggregatorConfig eventAggregatorConfig, EventService eventService) {
        this.eventAggregatorConfig = eventAggregatorConfig;
        this.eventService = eventService;
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
        ArrayList<HourlyEvent> hourlyEvents = eventAggregatorConfig.getHourlyEventList();
        eventService.setReadyForHourly(false);
        return hourlyEvents;
    }

}
