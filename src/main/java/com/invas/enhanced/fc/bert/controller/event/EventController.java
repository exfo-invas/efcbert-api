package com.invas.enhanced.fc.bert.controller.event;

import com.invas.enhanced.fc.bert.config.EventAggregatorConfig;
import com.invas.enhanced.fc.bert.model.event.disruptions.EventDisruptions;

import java.util.ArrayList;

import com.invas.enhanced.fc.bert.model.event.disruptions.FrameLoss;
import com.invas.enhanced.fc.bert.model.event.disruptions.StandardTestResponse;
import com.invas.enhanced.fc.bert.model.event.disruptions.TrafficResponse;
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
        //EventDisruptions eventDisruptions = eventAggregatorConfig.getLatestEventDisruption();
        /*if (eventDisruptions == null) {
            return ResponseEntity.badRequest().body(null);
        }*/

        TrafficResponse[] trafficResponse = new TrafficResponse[2];
        trafficResponse[0] = new TrafficResponse("tx", 3, 1, 2, 2);
        trafficResponse[1] = new TrafficResponse("rx", 3, 1, 2, 2);

        FrameLoss[] frameLossList = new FrameLoss[2];
        frameLossList[0] = new FrameLoss("tx", 1, 1, 1, 1);
        frameLossList[1] = new FrameLoss("rx", 1, 1, 1, 1);

        return ResponseEntity.ok(new EventDisruptions(trafficResponse, frameLossList, new StandardTestResponse("10x", 10200)));
    }

    @GetMapping("/details/hourly")
    public ArrayList<EventDisruptions> getHourlyDisruptionsList() {
        return eventAggregatorConfig.getHourlyEventList();
    }

}
