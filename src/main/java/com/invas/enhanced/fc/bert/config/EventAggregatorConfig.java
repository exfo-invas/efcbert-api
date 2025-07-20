package com.invas.enhanced.fc.bert.config;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Configuration;

import com.invas.enhanced.fc.bert.model.event.disruptions.EventDisruptions;
import com.invas.enhanced.fc.bert.model.event.disruptions.FrameLoss;
import com.invas.enhanced.fc.bert.model.event.disruptions.TrafficResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class EventAggregatorConfig {

    ArrayList<EventDisruptions> eventDisruptionsList = new ArrayList<>();

    public ArrayList<EventDisruptions> getEventDisruptionsList() {
        return eventDisruptionsList;
    }

    public ConcurrentHashMap<Integer, EventDisruptions> getHourlyEventMap() {
        return hourlyEventMap;
    }

    ConcurrentHashMap<Integer, EventDisruptions> hourlyEventMap = new ConcurrentHashMap<>();

    public ArrayList<EventDisruptions> updateEventDisruptionsList(EventDisruptions newEventDisruption) {
        eventDisruptionsList.add(newEventDisruption);
        log.info("Updated event disruptions list: {}", eventDisruptionsList);
        return eventDisruptionsList;
    }

    public ConcurrentHashMap<Integer, EventDisruptions> updateHourlyEventDisruptions() {
        if (eventDisruptionsList.isEmpty()) {
            log.warn("No event disruptions available.");
            return null;
        }
        FrameLoss frameLoss = aggregateFrameLoss(eventDisruptionsList);
        TrafficResponse[] trafficResponses = aggregateTrafficResponse(eventDisruptionsList);
        if (hourlyEventMap.isEmpty()) {
            log.info("Creating new hourly event disruptions map.");
            hourlyEventMap.put(1, new EventDisruptions(trafficResponses, frameLoss));
        } else {
            log.info("Updating existing hourly event disruptions map.");
            hourlyEventMap.put(hourlyEventMap.size(), new EventDisruptions(trafficResponses, frameLoss));
        }
        return hourlyEventMap; // Placeholder for actual implementation
    }

    private FrameLoss aggregateFrameLoss(ArrayList<EventDisruptions> eventDisruptionsList) {
        int totalTxCount = 0;
        int totalRxCount = 0;
        int totalLostFrames = 0;
        double totalFrameLossRate = 0;
        String fcRate = "";
        if (!eventDisruptionsList.isEmpty()) {
            fcRate = eventDisruptionsList.get(0).getFrameLoss().getFcRate();
        }

        for (EventDisruptions event : eventDisruptionsList) {
            FrameLoss frameLoss = event.getFrameLoss();
            if (frameLoss != null) {
                totalTxCount += frameLoss.getTxCount();
                totalRxCount += frameLoss.getRxCount();
                totalLostFrames += frameLoss.getLostFrames();
                totalFrameLossRate += frameLoss.getFrameLossRate();
            }
        }
        // Calculate the average each totalTxCount, totalRxCount, and totalLostFrames, frameLossRate
        totalTxCount = totalTxCount / eventDisruptionsList.size();
        totalRxCount = totalRxCount / eventDisruptionsList.size();
        totalLostFrames = totalLostFrames / eventDisruptionsList.size();
        totalFrameLossRate = totalFrameLossRate / eventDisruptionsList.size();
        return new FrameLoss(fcRate, totalTxCount, totalRxCount, totalLostFrames, totalFrameLossRate);
    }

    private TrafficResponse[] aggregateTrafficResponse(ArrayList<EventDisruptions> eventDisruptionsList) {
        TrafficResponse txTrafficResponses = new TrafficResponse();
        TrafficResponse rxTrafficResponses = new TrafficResponse();

        for (EventDisruptions event : eventDisruptionsList) {
            txTrafficResponses = getTxRXTrafficResponse(txTrafficResponses, event.getTraffic()[0]);
            rxTrafficResponses = getTxRXTrafficResponse(rxTrafficResponses, event.getTraffic()[1]);
        }

        return getAggregatedTrafficResponse(txTrafficResponses, rxTrafficResponses, eventDisruptionsList.size());

    }

    private TrafficResponse[] getAggregatedTrafficResponse(TrafficResponse txTrafficResponses, TrafficResponse rxTrafficResponses, int size) {
        if (txTrafficResponses == null || rxTrafficResponses == null) {
            log.warn("No traffic responses available for aggregation.");
            return new TrafficResponse[0];
        }

        return new TrafficResponse[]{
                new TrafficResponse(
                        txTrafficResponses.getType(),
                        txTrafficResponses.getFcRate(),
                        txTrafficResponses.getActualThroughput() / size,
                        txTrafficResponses.getActualTransferSpeed() / size,
                        txTrafficResponses.getLineSpeed() / size,
                        txTrafficResponses.getCurrentUtilization() / size,
                        txTrafficResponses.getMeasuredThroughput() / size,
                        txTrafficResponses.getTransferSpeed() / size,
                        txTrafficResponses.getMeasuredLineSpeed() / size),
                new TrafficResponse(
                        rxTrafficResponses.getType(),
                        rxTrafficResponses.getFcRate(),
                        rxTrafficResponses.getActualThroughput() / size,
                        rxTrafficResponses.getActualTransferSpeed() / size,
                        rxTrafficResponses.getLineSpeed() / size,
                        rxTrafficResponses.getCurrentUtilization() / size,
                        rxTrafficResponses.getMeasuredThroughput() / size,
                        rxTrafficResponses.getTransferSpeed() / size,
                        rxTrafficResponses.getMeasuredLineSpeed() / size
                )
        };
    }

    private TrafficResponse getTxRXTrafficResponse(TrafficResponse trafficResponse, TrafficResponse eventTrafficResponse) {
        return new TrafficResponse(
                eventTrafficResponse.getType(),
                eventTrafficResponse.getFcRate(),
                trafficResponse.getActualThroughput() + eventTrafficResponse.getActualThroughput(),
                trafficResponse.getActualTransferSpeed() + eventTrafficResponse.getActualTransferSpeed(),
                trafficResponse.getLineSpeed() + eventTrafficResponse.getLineSpeed(),
                trafficResponse.getCurrentUtilization() + eventTrafficResponse.getCurrentUtilization(),
                trafficResponse.getMeasuredThroughput() + eventTrafficResponse.getMeasuredThroughput(),
                trafficResponse.getTransferSpeed() + eventTrafficResponse.getTransferSpeed(),
                trafficResponse.getMeasuredLineSpeed() + eventTrafficResponse.getMeasuredLineSpeed()
        );
    }
}
