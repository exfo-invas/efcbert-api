package com.invas.enhanced.fc.bert.utils;
import com.invas.enhanced.fc.bert.model.event.EventDisruptions;
import com.invas.enhanced.fc.bert.model.event.HourlyEvent;
import com.invas.enhanced.fc.bert.service.TestTimerService;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.List;

import java.nio.file.*;
import java.util.Date;

@Slf4j
public class FileExporter {

    private final TestTimerService testTimerService;

    public FileExporter(TestTimerService testTimerService) {
        this.testTimerService = testTimerService;
    }


    /**
     * Export event disruptions list to CSV.
     * Fields: traffic[1].currentUtilization, traffic[1].measuredThroughput,
     * frameLoss[1].frameLossRate, latency.last
     *
     * @return
     */
    public static String exportEventDisruptionsToCsv(List<EventDisruptions> eventDisruptionsList, String timestamp) {
        Path filePath;
        try {
            filePath = getDefaultCsvPath("full_event", timestamp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (eventDisruptionsList == null || eventDisruptionsList.isEmpty()) {
            log.warn("No event disruptions to export to CSV: {}", filePath);
            try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
                writer.write("currentUtilization,measuredThroughput,frameLossRate,latencyLast");
                writer.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return filePath.toString();
        }

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write("currentUtilization,measuredThroughput,frameLossRate,latencyLast");
            writer.newLine();

            for (EventDisruptions e : eventDisruptionsList) {
                String currentUtil = safeSafeToString(() -> e.getTraffic()[1].getCurrentUtilization().toPlainString());
                String throughput  = safeSafeToString(() -> e.getTraffic()[1].getMeasuredThroughput().toPlainString());
                String frameLoss   = safeSafeToString(() -> e.getFrameLoss()[1].getFrameLossRate().toPlainString());
                String latency     = safeSafeToString(() -> e.getLatency().getLast().toPlainString());

                writer.write(
                        escapeCsv(currentUtil) + "," +
                                escapeCsv(throughput)  + "," +
                                escapeCsv(frameLoss)   + "," +
                                escapeCsv(latency)
                );
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("Exported {} event disruptions to {}", eventDisruptionsList.size(), filePath);
        return filePath.toString();
    }

    /**
     * Export hourly event list to CSV.
     * Columns: no,utilization,throughput,frameLoss,latency
     *
     * @return
     */
    public static String exportHourlyEventsToCsv(List<HourlyEvent> hourlyEventList, String timestamp) {
        Path filePath;
        try {
            filePath = getDefaultCsvPath("consolidated_events", timestamp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (hourlyEventList == null || hourlyEventList.isEmpty()) {
            log.warn("No hourly events to export to CSV: {}", filePath);
            try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
                writer.write("no,utilization,throughput,frameLoss,latency");
                writer.newLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return filePath.toString();
        }

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            writer.write("no,utilization,throughput,frameLoss,latency");
            writer.newLine();

            for (HourlyEvent h : hourlyEventList) {
                writer.write(
                        escapeCsv(String.valueOf(h.getNo())) + "," +
                                escapeCsv(h.getUtilization()) + "," +
                                escapeCsv(h.getThroughput()) + "," +
                                escapeCsv(h.getFrameLoss()) + "," +
                                escapeCsv(h.getLatency())
                );
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("Exported {} hourly events to {}", hourlyEventList.size(), filePath);
        return filePath.toString();
    }

    // Helper: create timestamped path on Desktop/EnhancedFCBert/
    private static Path getDefaultCsvPath(String prefix, String timestamp) throws IOException {
        String userHome = System.getProperty("user.home");
        String dateFolder = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        Path folder = Paths.get(userHome, "Desktop", "EnhancedFcbert", dateFolder);
        if (!Files.exists(folder)) {
            Files.createDirectories(folder);
        }

        String fileName = prefix + "_records_" + timestamp + ".csv";
        return folder.resolve(fileName);
    }

    // Helper to safely handle nulls/exceptions
    private interface CheckedSupplier<T> { T get() throws Exception; }

    private static <T> String safeSafeToString(CheckedSupplier<T> supplier) {
        try {
            T val = supplier.get();
            return val == null ? "" : val.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    private static String escapeCsv(String s) {
        if (s == null) return "";
        String out = s.replace("\"", "\"\"");
        if (out.contains(",") || out.contains("\n") || out.contains("\"")) {
            return "\"" + out + "\"";
        }
        return out;
    }
}
