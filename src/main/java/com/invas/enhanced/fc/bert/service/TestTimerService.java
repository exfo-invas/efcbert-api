// language: java
package com.invas.enhanced.fc.bert.service;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class TestTimerService {

    private final AtomicLong accumulatedMillis = new AtomicLong(0);
    private final AtomicReference<Instant> startInstant = new AtomicReference<>(null);

    private Duration StartedDuration;
    private Duration StoppedDuration;

    public Duration getStartedDuration() {
        return StartedDuration;
    }

    public void setStartedDuration(Duration startedDuration) {
        StartedDuration = startedDuration;
    }

    public Duration getStoppedDuration() {
        return StoppedDuration;
    }

    public void setStoppedDuration(Duration stoppedDuration) {
        StoppedDuration = stoppedDuration;
    }

// java
// Add these fields and methods inside TestTimerService

    private final AtomicReference<Instant> lastStartInstant = new AtomicReference<>(null);
    private final AtomicReference<Instant> lastStopInstant = new AtomicReference<>(null);

    public void start() {
        Instant now = Instant.now();
        if (startInstant.compareAndSet(null, now)) {
            lastStartInstant.set(now);
        }
    }

    public Duration stop() {
        Instant start = startInstant.getAndSet(null);
        if (start == null) return Duration.ZERO;
        Instant now = Instant.now();
        long elapsed = Duration.between(start, now).toMillis();
        accumulatedMillis.addAndGet(elapsed);
        lastStopInstant.set(now);
        return Duration.ofMillis(elapsed);
    }

    /* Returns the start time as HH:mm:ss. If the timer is currently running returns the running start,
       otherwise returns the most recent start instant (may be null). */
    public String getStartTime() {
        Instant running = startInstant.get();
        Instant instant = running != null ? running : lastStartInstant.get();
        return formatInstant(instant);
    }

    /* Returns the most recent stop time as HH:mm:ss (null if never stopped). */
    public String getEndTime() {
        return formatInstant(lastStopInstant.get());
    }

    /* Returns the current instant (now) formatted as HH:mm:ss. */
    public String getCurrentTime() {
        return formatInstant(Instant.now());
    }

    private String formatInstant(Instant instant) {
        if (instant == null) return null;
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter
            .ofPattern("HH:mm:ss")
            .withZone(java.time.ZoneId.systemDefault());
        return fmt.format(instant);
    }

    /**
     * Returns total elapsed time (accumulated + current running segment) formatted as HH:mm.
     */
    public String getElapsedHourMinute() {
        long totalMillis = accumulatedMillis.get();
        Instant start = startInstant.get();
        if (start != null) totalMillis += Duration.between(start, Instant.now()).toMillis();

        long totalMinutes = totalMillis / 60_000;
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        return String.format("%02d:%02d", hours, minutes);
    }

    /**
     * Get total elapsed duration (accumulated + current running segment).
     */
    public Duration getElapsedDuration() {
        long totalMillis = accumulatedMillis.get();
        Instant start = startInstant.get();
        if (start != null) totalMillis += Duration.between(start, Instant.now()).toMillis();
        return Duration.ofMillis(totalMillis);
    }

    /**
     * Reset timer (clears accumulated time and running state).
     */
    public void reset() {
        accumulatedMillis.set(0);
        startInstant.set(null);
    }

    /**
     * Returns whether the timer is currently running.
     */
    public boolean isRunning() {
        return startInstant.get() != null;
    }
}
