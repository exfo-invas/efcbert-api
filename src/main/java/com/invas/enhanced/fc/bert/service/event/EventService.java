package com.invas.enhanced.fc.bert.service.event;

import com.invas.enhanced.fc.bert.config.StandardConfig;
import com.invas.enhanced.fc.bert.contants.ConfigScpiConst;
import com.invas.enhanced.fc.bert.model.FcCalculationValues;
import com.invas.enhanced.fc.bert.model.event.*;
import com.invas.enhanced.fc.bert.config.EventAggregatorConfig;
import com.invas.enhanced.fc.bert.contants.EventScpiConst;
import com.invas.enhanced.fc.bert.service.ScpiTelnetService;
import com.invas.enhanced.fc.bert.utils.SimpleFCRateUtil;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
public class EventService {

    private final ScpiTelnetService scpiTelnetService;
    private final EventAggregatorConfig eventAggregatorConfig;
    private final StandardConfig standardConfig;

    private boolean scheduledEventEnabled = false;

    public EventService(ScpiTelnetService scpiTelnetService, EventAggregatorConfig eventAggregatorConfig, StandardConfig standardConfig) {
        this.scpiTelnetService = scpiTelnetService;
        this.eventAggregatorConfig = eventAggregatorConfig;
        this.standardConfig = standardConfig;
    }

    public EventDisruptions executeEventDisruptions() {
        EventDisruptions eventDisruptions = new EventDisruptions();

        // 1️⃣ Get FC rate and frame size from standardConfig or SCPI
        String fcRate = standardConfig.getFcRate();
        double frameSize = standardConfig.getFrameSize();

        if (fcRate == null || frameSize == 0) {
            fcRate = scpiTelnetService.sendCommand(ConfigScpiConst.interfaceType("VALUE"));
            frameSize = Double.parseDouble(scpiTelnetService.sendCommand(EventScpiConst.frameSize()));
            standardConfig.setFcRate(fcRate);
            standardConfig.setFrameSize(frameSize);
        }

        eventDisruptions.setStandard(new StandardTestResponse(
                fcRate,
                BigDecimal.valueOf(frameSize).toPlainString()
        ));

        // 2️⃣ Retrieve TX/RX counts and rates
        String txByteStr = scpiTelnetService.sendCommand(EventScpiConst.byteCount("TX"));
        String rxByteStr = scpiTelnetService.sendCommand(EventScpiConst.byteCount("RX"));
        String txFrameStr = scpiTelnetService.sendCommand(EventScpiConst.frameCount("TX"));
        String rxFrameStr = scpiTelnetService.sendCommand(EventScpiConst.frameCount("RX"));
        String txFrameRateStr = scpiTelnetService.sendCommand(EventScpiConst.frameRate("TX"));
        String rxFrameRateStr = scpiTelnetService.sendCommand(EventScpiConst.frameRate("RX"));
        String txUtilStr = scpiTelnetService.sendCommand(EventScpiConst.lineUtilization("TX"));
        String rxUtilStr = scpiTelnetService.sendCommand(EventScpiConst.lineUtilization("RX"));

        if (txByteStr == null || rxByteStr == null || txUtilStr == null || rxUtilStr == null) {
            log.error("Failed to retrieve TX or RX data.");
            return null;
        }

        // 3️⃣ Convert counts and rates to BigDecimal
        BigDecimal txCount = new BigDecimal(txFrameStr);
        BigDecimal rxCount = new BigDecimal(rxFrameStr);
        BigDecimal lostFrames = txCount.subtract(rxCount);
        BigDecimal frameLossRate = percentageOfBigDecimal(lostFrames, txCount);

        // Apply minimum threshold and round to 2 decimals
        frameLossRate = frameLossRate.compareTo(BigDecimal.valueOf(2.0)) < 0
                ? BigDecimal.ZERO
                : frameLossRate.setScale(2, RoundingMode.HALF_UP);

        FrameLoss[] frameLoss = {
                new FrameLoss("TX", new BigDecimal(txByteStr), new BigDecimal(txFrameRateStr), txCount, frameLossRate),
                new FrameLoss("RX", new BigDecimal(rxByteStr), new BigDecimal(rxFrameRateStr), rxCount, frameLossRate)
        };
        eventDisruptions.setFrameLoss(frameLoss);

        // 4️⃣ Calculate utilization & throughput
        BigDecimal currentUtilTX = new BigDecimal(txUtilStr);
        BigDecimal currentUtilRX = new BigDecimal(rxUtilStr);

        FcCalculationValues fcValues = SimpleFCRateUtil.getLineUtilizationCommand(standardConfig.getFcRate());
        if (fcValues == null) {
            log.error("Invalid fcRate: {}", standardConfig.getFcRate());
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
                new TrafficResponse("TX", currentUtilTX, measuredThroughputTX, transferSpeedTX, lineSpeedTX),
                new TrafficResponse("RX", currentUtilRX, measuredThroughputRX, transferSpeedRX, lineSpeedRX)
        };
        eventDisruptions.setTraffic(trafficResponses);

        // 5️⃣ Set latency
        eventDisruptions.setLatency(getLatency());

        log.info("EventDisruptions: {}", eventDisruptions);
        return eventDisruptions;
    }


    private BigDecimal percentageOfBigDecimal(BigDecimal part, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return part.multiply(BigDecimal.valueOf(100)).divide(total, 10, RoundingMode.HALF_UP);
    }

    private LatencyResponse getLatency() {
        String current = scpiTelnetService.sendCommand(EventScpiConst.latency("CURR"));
        String last = scpiTelnetService.sendCommand(EventScpiConst.latency("LAST"));
        String min = scpiTelnetService.sendCommand(EventScpiConst.latency("MIN"));
        String max = scpiTelnetService.sendCommand(EventScpiConst.latency("MAX"));
        if (current == null && last == null && min == null && max == null) {
            log.error("Failed to retrieve latency data.");
            return null;
        }
        assert current != null;
        return new LatencyResponse(
                new BigDecimal(current),
                new BigDecimal(last),
                new BigDecimal(min),
                new BigDecimal(max)
        );
    }

    public void startScheduledEvent(boolean enabled) {
        scheduledEventEnabled = enabled;
    }

    @Scheduled(fixedRate = 1000) // runs every second
    private void secondlyEventDisruption() {
        if (!scheduledEventEnabled) {
            log.info("EventDisruptions not enabled");
            return;
        }
        log.info("Scheduling event disruptions execution...");
        EventDisruptions eventDisruptions = executeEventDisruptions();
        log.info("EventDisruptions: {} to update EventList", eventDisruptions);
        if (eventDisruptions != null) {
            eventAggregatorConfig.updateEventDisruptionsList(eventDisruptions);
        }
    }

    @Scheduled(fixedRate = 1000) //TODO: 3600000 runs every hour
    private void hourlyEventDisruptions() {
        if (!scheduledEventEnabled) {
            return;
        }
        log.info("Hourly event disruptions execution...");
        eventAggregatorConfig.updateHourlyEventDisruptions();
    }


}
