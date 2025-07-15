package com.invas.enhanced.fc.bert.controller.event;

import com.invas.enhanced.fc.bert.model.event.EventDetails;
import com.invas.enhanced.fc.bert.model.event.disruptions.EventDisruptions;
import com.invas.enhanced.fc.bert.service.event.EventService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/details")
    public EventDisruptions getEventDetails() {
        return eventService.executeEventDisruptions();
    }

}
