package com.invas.enhanced.fc.bert.config;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.invas.enhanced.fc.bert.model.config.FullConfigStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

import com.invas.enhanced.fc.bert.model.event.disruptions.EventDisruptions;
import com.invas.enhanced.fc.bert.model.event.disruptions.FrameLoss;
import com.invas.enhanced.fc.bert.model.event.disruptions.TrafficResponse;

import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
@Configuration
public class EventAggregatorConfig {

    FullConfigStatus fullConfigStatus = new FullConfigStatus();

    ArrayList<EventDisruptions> eventDisruptionsList = new ArrayList<>();
    ConcurrentHashMap<Integer, EventDisruptions> hourlyEventMap = new ConcurrentHashMap<>();

    public EventDisruptions getLatestEventDisruption() {
        if (eventDisruptionsList.isEmpty()) {
            log.warn("No event disruptions available.");
            return null;
        }
        return eventDisruptionsList.get(eventDisruptionsList.size() - 1);
    }

    public ArrayList<EventDisruptions> getHourlyEventList() {
        if (hourlyEventMap.isEmpty()) {
            log.warn("No event disruptions available. Skipping hourly event disruptions retrieval.");
            return new ArrayList<>();
        }
        return new ArrayList<>(hourlyEventMap.values());
    }

    public ArrayList<EventDisruptions> updateEventDisruptionsList(EventDisruptions newEventDisruption) {
        eventDisruptionsList.add(newEventDisruption);
        log.info("Added new event disruption: {}", newEventDisruption);
        log.info("Updated event disruptions list: {}", eventDisruptionsList);
        return eventDisruptionsList;
    }

    public ConcurrentHashMap<Integer, EventDisruptions> updateHourlyEventDisruptions() {
        if (eventDisruptionsList.isEmpty()) {
            log.warn("No event disruptions available. Skipping hourly event disruptions update.");
            return null;
        }
        EventDisruptions eventDisruptions = aggregateResponses(eventDisruptionsList);
        if (hourlyEventMap.isEmpty()) {
            log.info("Creating new hourly event disruptions map.");
            hourlyEventMap.put(1, eventDisruptions);
        } else {
            log.info("Updating existing hourly event disruptions map.");
            hourlyEventMap.put(hourlyEventMap.size() + 1, eventDisruptions);
        }
        return hourlyEventMap; // Placeholder for actual implementation
    }

    private EventDisruptions aggregateResponses(ArrayList<EventDisruptions> eventDisruptionsList) {
        TrafficResponse txTraffic = new TrafficResponse();
        TrafficResponse rxTraffic = new TrafficResponse();
        FrameLoss txFrameLoss = new FrameLoss();
        FrameLoss rxFrameLoss = new FrameLoss();

        for (EventDisruptions event : eventDisruptionsList) {
            txTraffic = combineTraffic(txTraffic, event.getTraffic()[0]);
            rxTraffic = combineTraffic(rxTraffic, event.getTraffic()[1]);
            txFrameLoss = combineFrameLoss(txFrameLoss, event.getFrameLoss()[0]);
            rxFrameLoss = combineFrameLoss(rxFrameLoss, event.getFrameLoss()[1]);
        }

        EventDisruptions aggregatedEvent = new EventDisruptions();
        aggregatedEvent.setTraffic(averageTraffic(txTraffic, rxTraffic, eventDisruptionsList.size()));
        aggregatedEvent.setFrameLoss(averageFrameLoss(txFrameLoss, rxFrameLoss, eventDisruptionsList.size()));
        aggregatedEvent.setStandard(eventDisruptionsList.get(0).getStandard());
        log.info("Aggregated EventDisruptions: {}", aggregatedEvent);

        return aggregatedEvent;
    }

    private FrameLoss combineFrameLoss(FrameLoss base, FrameLoss addition) {
        return new FrameLoss(
            addition.getType(),
            base.getByteCount() + addition.getByteCount(),
            base.getFrameRate() + addition.getFrameRate(),
            base.getFrameCount() + addition.getFrameCount(),
            base.getFrameLossRate() + addition.getFrameLossRate()
        );
    }

    private FrameLoss[] averageFrameLoss(FrameLoss txLoss, FrameLoss rxLoss, int size) {
        return new FrameLoss[] {
            averageSingleFrameLoss(txLoss, size),
            averageSingleFrameLoss(rxLoss, size)
        };
    }

    private FrameLoss averageSingleFrameLoss(FrameLoss loss, int size) {
        return new FrameLoss(
            loss.getType(),
            loss.getByteCount() / size,
            loss.getFrameRate() / size,
            loss.getFrameCount() / size,
            loss.getFrameLossRate() / size
        );
    }

    private TrafficResponse[] averageTraffic(TrafficResponse txTraffic, TrafficResponse rxTraffic, int size) {
        return new TrafficResponse[] {
            averageSingleTraffic(txTraffic, size),
            averageSingleTraffic(rxTraffic, size)
        };
    }

    private TrafficResponse averageSingleTraffic(TrafficResponse traffic, int size) {
        return new TrafficResponse(
            traffic.getType(),
            traffic.getCurrentUtilization() / size,
            traffic.getMeasuredThroughput() / size,
            traffic.getTransferSpeed() / size,
            traffic.getMeasuredLineSpeed() / size
        );
    }

    private TrafficResponse combineTraffic(TrafficResponse base, TrafficResponse addition) {
        return new TrafficResponse(
            addition.getType(),
            base.getCurrentUtilization() + addition.getCurrentUtilization(),
            base.getMeasuredThroughput() + addition.getMeasuredThroughput(),
            base.getTransferSpeed() + addition.getTransferSpeed(),
            base.getMeasuredLineSpeed() + addition.getMeasuredLineSpeed()
        );
    }
}
