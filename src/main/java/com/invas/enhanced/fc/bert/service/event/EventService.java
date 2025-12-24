package com.invas.enhanced.fc.bert.service.event;

import com.invas.enhanced.fc.bert.config.StandardConfig;
import com.invas.enhanced.fc.bert.contants.ConfigScpiConst;
import com.invas.enhanced.fc.bert.model.FcCalculationValues;
import com.invas.enhanced.fc.bert.model.event.*;
import com.invas.enhanced.fc.bert.config.EventAggregatorConfig;
import com.invas.enhanced.fc.bert.contants.EventScpiConst;
import com.invas.enhanced.fc.bert.service.ScpiTelnetService;
import com.invas.enhanced.fc.bert.utils.DecimalHandlerUtil;
import com.invas.enhanced.fc.bert.utils.SimpleFCRateUtil;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Setter
@Service
public class EventService {

    private final ScpiTelnetService scpiTelnetService;
    private final EventAggregatorConfig eventAggregatorConfig;
    private final StandardConfig standardConfig;

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService executorHourly = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> secondTask;
    private ScheduledFuture<?> hourlyTask;
    private int hourlyCounter = 0;

    private boolean readyForHourly = false;

    public EventService(ScpiTelnetService scpiTelnetService, EventAggregatorConfig eventAggregatorConfig, StandardConfig standardConfig) {
        this.scpiTelnetService = scpiTelnetService;
        this.eventAggregatorConfig = eventAggregatorConfig;
        this.standardConfig = standardConfig;
    }

    public EventDisruptions executeEventDisruptions() {
        EventDisruptions eventDisruptions = new EventDisruptions();
        log.info("********************START EventDisruptions...************************");
        StandardTestResponse standardTestResponse = this.standardConfig.getStandardTestResponse();
        if (standardTestResponse == null) {
            standardTestResponse = new StandardTestResponse(
                    this.scpiTelnetService.sendCommand(ConfigScpiConst.interfaceType("VALUE")),
                    this.scpiTelnetService.sendCommand(EventScpiConst.frameSize())
            );
            this.standardConfig.setStandardTestResponse(standardTestResponse);
        } else {
            String frameSize = this.scpiTelnetService.sendCommand(EventScpiConst.frameSize());
            if (!standardTestResponse.getFrameSize().equals(frameSize == null ? "" : frameSize)) {
                standardTestResponse.setFrameSize(frameSize);
            }
        }
        eventDisruptions.setStandard(standardTestResponse);
        log.info("****Updated standardConfig: {} in eventDisruptions****", this.standardConfig);
        String txByteStr = this.scpiTelnetService.sendCommand(EventScpiConst.byteCount("TX"));
        String rxByteStr = this.scpiTelnetService.sendCommand(EventScpiConst.byteCount("RX"));
        String txFrameStr = this.scpiTelnetService.sendCommand(EventScpiConst.frameCount("TX"));
        String rxFrameStr = this.scpiTelnetService.sendCommand(EventScpiConst.frameCount("RX"));
        String txFrameRateStr = this.scpiTelnetService.sendCommand(EventScpiConst.frameRate("TX"));
        String rxFrameRateStr = this.scpiTelnetService.sendCommand(EventScpiConst.frameRate("RX"));
        String txUtilStr = this.scpiTelnetService.sendCommand(EventScpiConst.lineUtilization("TX"));
        String rxUtilStr = this.scpiTelnetService.sendCommand(EventScpiConst.lineUtilization("RX"));
        if (txByteStr == null || rxByteStr == null || txUtilStr == null || rxUtilStr == null) {
            log.error("Failed to retrieve TX or RX data.");
            return null;
        }
        BigDecimal txCount = DecimalHandlerUtil.ifNullReturnZero(txFrameStr);
        BigDecimal rxCount = DecimalHandlerUtil.ifNullReturnZero(rxFrameStr);
        BigDecimal lostFrames = txCount.subtract(rxCount);
        BigDecimal frameLossRate = DecimalHandlerUtil.percentageOfBigDecimal(lostFrames, txCount);
        frameLossRate = (frameLossRate.compareTo(BigDecimal.valueOf(2.0D)) < 0) ? BigDecimal.ZERO : frameLossRate;
        FrameLoss[] frameLoss = {
                new FrameLoss("Tx",
                        DecimalHandlerUtil.ifNullReturnZero(txByteStr),
                        DecimalHandlerUtil.ifNullReturnZero(txFrameRateStr),
                        txCount,
                        frameLossRate),
                new FrameLoss("Rx",
                        DecimalHandlerUtil.ifNullReturnZero(rxByteStr),
                        DecimalHandlerUtil.ifNullReturnZero(rxFrameRateStr),
                        rxCount,
                        frameLossRate)
        };
        eventDisruptions.setFrameLoss(frameLoss);
        log.info("****Updated frameLossRate: {} in eventDisruptions***", frameLossRate);
        BigDecimal currentUtilTX = DecimalHandlerUtil.ifNullReturnZero(txUtilStr);
        BigDecimal currentUtilRX = DecimalHandlerUtil.ifNullReturnZero(rxUtilStr);
        FcCalculationValues fcValues = SimpleFCRateUtil.getLineUtilizationCommand(standardTestResponse.getFcRate());
        if (fcValues == null) {
            log.error("Invalid fcRate: {}", standardTestResponse.getFcRate());
            return null;
        }
        BigDecimal actualThroughput = BigDecimal.valueOf(fcValues.getActualThroughput());
        BigDecimal actualTransferRate = BigDecimal.valueOf(fcValues.getActualTransferRate());
        BigDecimal actualLineSpeed = BigDecimal.valueOf(fcValues.getLineSpeed());
        BigDecimal measuredThroughputTX = DecimalHandlerUtil.valuePercentage(actualThroughput, currentUtilTX);
        BigDecimal measuredThroughputRX = DecimalHandlerUtil.valuePercentage(actualThroughput, currentUtilRX);
        BigDecimal transferSpeedTX = DecimalHandlerUtil.valuePercentage(actualTransferRate, currentUtilTX);
        BigDecimal transferSpeedRX = DecimalHandlerUtil.valuePercentage(actualTransferRate, currentUtilRX);
        BigDecimal lineSpeedTX = actualLineSpeed;
        BigDecimal lineSpeedRX = actualLineSpeed;
        TrafficResponse[] trafficResponses = {
                new TrafficResponse("Tx",
                        currentUtilTX,
                        measuredThroughputTX,
                        transferSpeedTX,
                        lineSpeedTX),
                new TrafficResponse("Rx", currentUtilRX,
                        measuredThroughputRX,
                        transferSpeedRX,
                        lineSpeedRX)
        };
        eventDisruptions.setTraffic(trafficResponses);
        log.info("****Updated trafficResponses: {} in eventDisruptions****", (Object) trafficResponses);
        eventDisruptions.setLatency(getLatency());
        log.info("****Updated latency in eventDisruptions***");
        log.info("EventDisruptions: {}", eventDisruptions);

        eventDisruptions.setHourlyStatus(
                new HourlyCounter(getNextRunTime(), readyForHourly)
        );
        return eventDisruptions;
    }

    private LatencyResponse getLatency() {
        String last = this.scpiTelnetService.sendCommand(EventScpiConst.latency("LAST"));
        String min = this.scpiTelnetService.sendCommand(EventScpiConst.latency("MIN"));
        String max = this.scpiTelnetService.sendCommand(EventScpiConst.latency("MAX"));
        log.info("last {}, min {}, max {}: Latency", last, min, max);
        return new LatencyResponse(DecimalHandlerUtil.defaultIfNullReturnZero(last), DecimalHandlerUtil.defaultIfNullReturnZero(min), DecimalHandlerUtil.defaultIfNullReturnZero(max));
    }

    /**
     * ================= START POLLING ==================
     */
    public synchronized void startScheduledEvent(boolean enabled) {

        if (enabled) {
            log.info(" Polling Started");

            // run every second
            secondTask = executor.scheduleAtFixedRate(() -> {
                EventDisruptions event = executeEventDisruptions();
                log.info("Second aggregated update triggered with event: {}", event);
                eventAggregatorConfig.updateEventDisruptionsList(event);
                if (hourlyCounter < 1) {
                    eventAggregatorConfig.updateHourlyEventDisruptions();
                }
                readyForHourly = true;
                //increment hourly counter up to 3
                hourlyCounter = hourlyCounter < 3 ? ++hourlyCounter : hourlyCounter;
            }, 0, 1, TimeUnit.SECONDS);

            // hourly task - run every hour
            // Separate executor to avoid interference with second task
            hourlyTask = executorHourly.scheduleAtFixedRate(() -> {
                log.info(" Hourly aggregated update triggered");
                eventAggregatorConfig.updateHourlyEventDisruptions();
                readyForHourly = true;
            }, 1, 1, TimeUnit.HOURS);
        }

        /* ================= STOP POLLING ================== */
        if (!enabled) {
            log.info(" Polling Stopped");
            hourlyCounter = 0;

            if (secondTask != null) secondTask.cancel(true);
            if (hourlyTask != null) hourlyTask.cancel(true);
        }
    }

    public String getNextRunTime() {

        long millis = hourlyTask.getDelay(TimeUnit.MILLISECONDS);

        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        long hours = (millis / (1000 * 60 * 60));

        return String.format("Next consolidation in %02d hr %02d min %02d sec", hours, minutes, seconds);
    }
}
