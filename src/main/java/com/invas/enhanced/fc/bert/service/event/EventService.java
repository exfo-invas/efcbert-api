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

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class EventService {

    private final ScpiTelnetService scpiTelnetService;
    private final EventAggregatorConfig eventAggregatorConfig;
    private final StandardConfig standardConfig;

    private boolean scheduledEventEnabled = false;
    // thread-safe counter incremented by the secondly scheduled task
    private final AtomicInteger secondsCounter = new AtomicInteger(0);

    private final AtomicLong lastHourlyRunNano = new AtomicLong(System.nanoTime());

    private boolean readyForHourly = false;

    private static final long ONE_HOUR_NANO = 3_600_000_000_000L;

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
        BigDecimal txCount = ifNullReturnZero(txFrameStr);
        BigDecimal rxCount = ifNullReturnZero(rxFrameStr);
        BigDecimal lostFrames = txCount.subtract(rxCount);
        BigDecimal frameLossRate = percentageOfBigDecimal(lostFrames, txCount);
        frameLossRate = (frameLossRate.compareTo(BigDecimal.valueOf(2.0D)) < 0) ? BigDecimal.ZERO : frameLossRate.setScale(2, RoundingMode.HALF_UP);
        FrameLoss[] frameLoss = {
                new FrameLoss("Tx",
                        roundToTwoDecimalPlaces(ifNullReturnZero(txByteStr)),
                        roundToTwoDecimalPlaces(ifNullReturnZero(txFrameRateStr)),
                        roundToTwoDecimalPlaces(txCount),
                        frameLossRate),
                new FrameLoss("Rx",
                        roundToTwoDecimalPlaces(ifNullReturnZero(rxByteStr)),
                        roundToTwoDecimalPlaces(ifNullReturnZero(rxFrameRateStr)),
                        roundToTwoDecimalPlaces(rxCount),
                        frameLossRate)
        };
        eventDisruptions.setFrameLoss(frameLoss);
        log.info("****Updated frameLossRate: {} in eventDisruptions***", frameLossRate);
        BigDecimal currentUtilTX = ifNullReturnZero(txUtilStr);
        BigDecimal currentUtilRX = ifNullReturnZero(rxUtilStr);
        FcCalculationValues fcValues = SimpleFCRateUtil.getLineUtilizationCommand(standardTestResponse.getFcRate());
        if (fcValues == null) {
            log.error("Invalid fcRate: {}", standardTestResponse.getFcRate());
            return null;
        }
        BigDecimal actualThroughput = BigDecimal.valueOf(fcValues.getActualThroughput());
        BigDecimal actualTransferRate = BigDecimal.valueOf(fcValues.getActualTransferRate());
        BigDecimal actualLineSpeed = BigDecimal.valueOf(fcValues.getLineSpeed());
        BigDecimal measuredThroughputTX = percentageOfBigDecimal(actualThroughput, currentUtilTX);
        BigDecimal measuredThroughputRX = percentageOfBigDecimal(actualThroughput, currentUtilRX);
        BigDecimal transferSpeedTX = percentageOfBigDecimal(actualTransferRate, currentUtilTX);
        BigDecimal transferSpeedRX = percentageOfBigDecimal(actualTransferRate, currentUtilRX);
        BigDecimal lineSpeedTX = percentageOfBigDecimal(actualLineSpeed, currentUtilTX);
        BigDecimal lineSpeedRX = percentageOfBigDecimal(actualLineSpeed, currentUtilRX);
        TrafficResponse[] trafficResponses = {
                new TrafficResponse("Tx",
                        roundToTwoDecimalPlaces(currentUtilTX),
                        roundToTwoDecimalPlaces(measuredThroughputTX),
                        roundToTwoDecimalPlaces(transferSpeedTX),
                        roundToTwoDecimalPlaces(lineSpeedTX)),
                new TrafficResponse("Rx", roundToTwoDecimalPlaces(currentUtilRX),
                        roundToTwoDecimalPlaces(measuredThroughputRX),
                        roundToTwoDecimalPlaces(transferSpeedRX),
                        roundToTwoDecimalPlaces(lineSpeedRX))};
        eventDisruptions.setTraffic(trafficResponses);
        log.info("****Updated trafficResponses: {} in eventDisruptions****", (Object) trafficResponses);
        eventDisruptions.setLatency(getLatency());
        log.info("****Updated latency in eventDisruptions***");
        log.info("EventDisruptions: {}", eventDisruptions);

        eventDisruptions.setHourlyStatus(
                new HourlyCounter(secondsCounter.get(), readyForHourly)
        );
        return eventDisruptions;
    }

    private BigDecimal percentageOfBigDecimal(BigDecimal part, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        // new calculation: part * (total / 100)
        return part.multiply(total).divide(BigDecimal.valueOf(100L), 10, RoundingMode.HALF_UP);
    }

    private BigDecimal roundToTwoDecimalPlaces(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private LatencyResponse getLatency() {
        String last = this.scpiTelnetService.sendCommand(EventScpiConst.latency("LAST"));
        String min = this.scpiTelnetService.sendCommand(EventScpiConst.latency("MIN"));
        String max = this.scpiTelnetService.sendCommand(EventScpiConst.latency("MAX"));
        log.info("last {}, min {}, max {}: Latency", last, min, max);
        return new LatencyResponse(ifNullReturnZero(last), ifNullReturnZero(min), ifNullReturnZero(max));
    }

    private BigDecimal ifNullReturnZero(String value) {
        return (value == null) ? BigDecimal.ZERO : new BigDecimal(value);
    }

    public void startScheduledEvent(boolean enabled) {
        this.scheduledEventEnabled = enabled;
        this.eventAggregatorConfig.generateExportFile();
    }

    @Scheduled(fixedRate = 1000L) // every second
    private void secondlyEventDisruption() {
        if (!this.scheduledEventEnabled) {
            log.info("EventDisruptions not enabled");
            return;
        }
        log.info("Scheduling event disruptions execution...");
        // increment the seconds counter; when it reaches 60 trigger hourly aggregation
        int seconds = this.secondsCounter.incrementAndGet();

        long now = System.nanoTime();
        long last = lastHourlyRunNano.get();

        if (now - last >= ONE_HOUR_NANO) {
            log.info("One hour has passed since last hourly run. Triggering hourlyEventDisruptions...");
            readyForHourly = true;
            // atomically acquire the hourly tick
            if (lastHourlyRunNano.compareAndSet(last, now)) {
                log.info("Executing hourlyEventDisruptions due to one hour elapsed...");
                try {
                    hourlyEventDisruptions();
                } catch (Exception e) {
                    log.error("Error in hourlyEventDisruptions", e);
                }
            }
        }

//        if (seconds >= 3600) { // every hour
//            // reset and call hourly aggregation
//            this.secondsCounter.set(0);
//            try {
//                hourlyEventDisruptions();
//            } catch (Exception e) {
//                log.error("Error while executing hourlyEventDisruptions from secondlyEventDisruption", e);
//            }
//        }

        EventDisruptions eventDisruptions = executeEventDisruptions();
        log.info("EventDisruptions: {} to update EventList", eventDisruptions);
        if (eventDisruptions != null) this.eventAggregatorConfig.updateEventDisruptionsList(eventDisruptions);
    }

    private void hourlyEventDisruptions() {
        if (!this.scheduledEventEnabled) return;
        log.info("Hourly event disruptions execution...");
        this.eventAggregatorConfig.updateHourlyEventDisruptions();
    }
}
