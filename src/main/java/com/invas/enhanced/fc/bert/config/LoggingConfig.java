package com.invas.enhanced.fc.bert.config;

import com.invas.enhanced.fc.bert.model.config.LogTestRecords;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Slf4j
@Configuration
public class LoggingConfig {

    ArrayList<LogTestRecords> logTestRecords = new ArrayList<>();
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public ArrayList<LogTestRecords> updateLog(int testId, String event) {
        LogTestRecords logRecord = new LogTestRecords();
        logRecord.setId(logTestRecords.size() - 1 ); // testId is the index of the log record
        logRecord.setStartTime(startTime.toString());
        logRecord.setEvent(event);
        logRecord.setDuration(getElapsedFormatted());
        logTestRecords.add(logRecord);
        return logTestRecords;
    }

    public ArrayList<LogTestRecords> endLog() {
        LogTestRecords logRecord = new LogTestRecords();
        logRecord.setId(logTestRecords.size() - 1 ); // testId is the index of the log record
        logRecord.setStartTime(startTime.toString());
        logRecord.setEvent("test completed");
        logRecord.setDuration(getElapsedFormatted());
        logTestRecords.add(logRecord);
        return logTestRecords;
    }

    public ArrayList<LogTestRecords> getLogTestRecords() {
        log.info("Retrieving log records, total count: {}", logTestRecords.size());
        return logTestRecords;
    }

    public void start() {
        startTime = LocalDateTime.now();
    }

    public void stop() {
        endTime = LocalDateTime.now();
    }

    public long getElapsedSeconds() {
        if (startTime == null || endTime == null) {
            return -1;
        }
        return Duration.between(startTime, endTime).getSeconds();
    }

    public String getElapsedFormatted() {
        long seconds = getElapsedSeconds();
        if (seconds < 0) return "N/A";
        long mins = seconds / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }
}
