package com.invas.enhanced.fc.bert.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import com.invas.enhanced.fc.bert.model.scheduler.AggregatedData;
import com.invas.enhanced.fc.bert.model.scheduler.EventData;

public class SchedulerTelnetService {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledTask;
    private final List<EventData> eventDataList = new ArrayList<>();
    private final List<AggregatedData> hourlyAggregates = new ArrayList<>();
    private boolean testRunning = false;
    private LocalDateTime testStartTime;
    private LocalDateTime lastAggregateTime;



    public ResponseEntity<String> handleTest(@PathVariable boolean start) {
        if (start && !testRunning) {
            testRunning = true;
            testStartTime = LocalDateTime.now();
            lastAggregateTime = testStartTime;
            scheduledTask = scheduler.scheduleAtFixedRate(this::executeEventDisruptions, 0, 30, TimeUnit.SECONDS);
            scheduler.scheduleAtFixedRate(this::aggregateHourly, 1, 1, TimeUnit.HOURS);
            return ResponseEntity.ok("Test started");
        } else if (!start && testRunning) {
            testRunning = false;
            if (scheduledTask != null) scheduledTask.cancel(true);
            saveDataToCsv();
            return ResponseEntity.ok("Test stopped and data saved");
        }
        return ResponseEntity.badRequest().body("Invalid operation");
    }

    private void executeEventDisruptions() {
        if (!testRunning) return;
        // ...collect data...
        // EventData data = new EventData(/* ... */);
        // synchronized (eventDataList) {
        //     eventDataList.add(data);
        // }
    }

    private void aggregateHourly() {
        // if (!testRunning) return;
        // LocalDateTime now = LocalDateTime.now();
        // List<EventData> toAggregate;
        // synchronized (eventDataList) {
        //     toAggregate = eventDataList.stream()
        //         .filter(d -> d.getTimestamp().isAfter(lastAggregateTime) && d.getTimestamp().isBefore(now))
        //         .collect(Collectors.toList());
        // }
        // // Aggregate double and int values
        // AggregatedData agg = AggregatedData.aggregate(toAggregate);
        // hourlyAggregates.add(agg);
        // lastAggregateTime = now;
    }

    private void saveDataToCsv() {
        // Use OpenCSV or similar to write eventDataList and hourlyAggregates to CSV
    }
    
}
