package com.invas.enhanced.fc.bert.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.invas.enhanced.fc.bert.model.event.HourlyEvent;
import com.invas.enhanced.fc.bert.utils.FileExporter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

import com.invas.enhanced.fc.bert.model.event.EventDisruptions;

import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
@Configuration
public class EventAggregatorConfig {

    ArrayList<EventDisruptions> eventDisruptionsList = new ArrayList<>();
    ConcurrentHashMap<Integer, HourlyEvent> hourlyEventMap = new ConcurrentHashMap<>();
    String hourlyFileName = null;
    String fullEventFileName = null;
    private final TestTimerService testTimerService;

    public EventAggregatorConfig(TestTimerService testTimerService) {
        this.testTimerService = testTimerService;
    }

    public EventDisruptions getLatestEventDisruption() {
        if (eventDisruptionsList.isEmpty()) {
            log.warn("No event disruptions available.");
            return null;
        }
        return eventDisruptionsList.get(eventDisruptionsList.size() - 1);
    }

    public ArrayList<HourlyEvent> getHourlyEventList() {
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

    public ConcurrentHashMap<Integer, HourlyEvent> updateHourlyEventDisruptions() {
        HourlyEvent hourlyEvent = new HourlyEvent(
                0,
                null,
                getLatestEventDisruption().getTraffic()[1].getCurrentUtilization().toPlainString(),
                getLatestEventDisruption().getTraffic()[1].getMeasuredThroughput().toPlainString(),
                getLatestEventDisruption().getFrameLoss()[1].getFrameLossRate().toPlainString(),
                getLatestEventDisruption().getLatency().getLast().toPlainString()
        );
        if (hourlyEventMap.isEmpty()) {
            log.info("Creating new hourly event disruptions map.");
            hourlyEvent.setNo(1);
            hourlyEvent.setTimestamp(testTimerService.getStartTime());
            hourlyEventMap.put(1, hourlyEvent);
        } else {
            log.info("Updating existing hourly event disruptions map.");
            hourlyEvent.setNo(hourlyEventMap.size() + 1);
            hourlyEvent.setTimestamp(testTimerService.getCurrentTime());
            hourlyEventMap.put(hourlyEventMap.size() + 1, hourlyEvent);
        }
        return hourlyEventMap;
    }

    //End Hourly Event Disruptions
    public ConcurrentHashMap<Integer, HourlyEvent> endHourlyEventDisruptions() {
        HourlyEvent hourlyEvent = new HourlyEvent(
                hourlyEventMap.size() + 1,
                testTimerService.getEndTime(),
                getLatestEventDisruption().getTraffic()[1].getCurrentUtilization().toPlainString(),
                getLatestEventDisruption().getTraffic()[1].getMeasuredThroughput().toPlainString(),
                getLatestEventDisruption().getFrameLoss()[1].getFrameLossRate().toPlainString(),
                getLatestEventDisruption().getLatency().getLast().toPlainString()
        );
        hourlyEventMap.put(hourlyEventMap.size() + 1, hourlyEvent);
        return hourlyEventMap;
    }

    public void generateExportFile() {
        log.info("Generating export file for event disruptions...");
        // Implementation for file generation goes here
        fullEventFileName = FileExporter.exportEventDisruptionsToCsv(eventDisruptionsList, testTimerService.getStartTime());
        hourlyFileName = FileExporter.exportHourlyEventsToCsv(getHourlyEventList(), testTimerService.getStartTime());
        //Clear lists after exporting
        eventDisruptionsList.clear();
        hourlyEventMap.clear();
    }
}
