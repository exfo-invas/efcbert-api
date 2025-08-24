package com.invas.enhanced.fc.bert.service.event;

import com.invas.enhanced.fc.bert.model.FcCalculationValues;
import com.invas.enhanced.fc.bert.model.event.disruptions.EventDisruptions;
import com.invas.enhanced.fc.bert.model.event.disruptions.FrameLoss;
import com.invas.enhanced.fc.bert.model.event.disruptions.StandardTestResponse;
import com.invas.enhanced.fc.bert.model.event.disruptions.TrafficResponse;
import com.invas.enhanced.fc.bert.config.EventAggregatorConfig;
import com.invas.enhanced.fc.bert.contants.ConfigScpiConst;
import com.invas.enhanced.fc.bert.contants.EventScpiConst;
import com.invas.enhanced.fc.bert.service.ScpiTelnetService;
import com.invas.enhanced.fc.bert.utils.SimpleFCRateUtil;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventService {

    private final ScpiTelnetService scpiTelnetService;
    private final EventAggregatorConfig eventAggregatorConfig;

    String fcRate = "";
    double frameSize = 0;

    private boolean scheduledEventEnabled = false;

    public EventService(ScpiTelnetService scpiTelnetService, EventAggregatorConfig eventAggregatorConfig) {
        this.scpiTelnetService = scpiTelnetService;
        this.eventAggregatorConfig = eventAggregatorConfig;
    }

    /**
     * Executes the event disruptions and returns the EventDisruptions object.
     * This method retrieves the latest event disruptions data from the SCPI commands.
     *
     * @return EventDisruptions object containing the latest event disruptions data.
     */
    public EventDisruptions executeEventDisruptions() {
        EventDisruptions eventDisruptions = new EventDisruptions();

        if (fcRate.isEmpty() || frameSize == 0) {
            fcRate = scpiTelnetService.sendCommand(ConfigScpiConst.interfaceType("VALUE"));
            frameSize = Double.parseDouble(scpiTelnetService.sendCommand(EventScpiConst.frameSize()));
        }
        log.info("fcRate: {}, frameSize: {}", fcRate, frameSize);

        String txByteCount = scpiTelnetService.sendCommand(EventScpiConst.byteCount("TX"));
        String rxByteCount = scpiTelnetService.sendCommand(EventScpiConst.byteCount("RX"));
        String txFrameCount = scpiTelnetService.sendCommand(EventScpiConst.frameCount("TX"));
        String rxFrameCount = scpiTelnetService.sendCommand(EventScpiConst.frameCount("RX"));
        String txFrameRate = scpiTelnetService.sendCommand(EventScpiConst.frameRate("TX"));
        String rxFrameRate = scpiTelnetService.sendCommand(EventScpiConst.frameRate("RX"));
        String txRateStr = scpiTelnetService.sendCommand(EventScpiConst.lineUtilization("TX"));
        String rxRateStr = scpiTelnetService.sendCommand(EventScpiConst.lineUtilization("RX"));
        if (txByteCount == null && rxByteCount == null && txRateStr == null && rxRateStr == null) {
            log.error("Failed to retrieve TX or RX byte counts.");
            return null;
        }
        assert txByteCount != null;
        double txCount = Double.parseDouble(txFrameCount);
        double rxCount = Double.parseDouble(rxFrameCount);
        double txByteCountDouble = Double.parseDouble(txByteCount);
        double rxByteCountDouble = Double.parseDouble(rxByteCount);
        double txFrameRateDouble = Double.parseDouble(txFrameRate);
        double rxFrameRateDouble = Double.parseDouble(rxFrameRate);
        double lostFrames = txCount - rxCount;
        double frameLossRate = (lostFrames / txCount * 100) < 2.0 ? 0 : lostFrames / txCount * 100;

        FrameLoss[] frameLoss = new FrameLoss[2];
        frameLoss[0] = new FrameLoss(
                "TX",
                txByteCountDouble,
                txFrameRateDouble,
                txCount,
                frameLossRate);
        frameLoss[1] = new FrameLoss(
                "RX",
                rxByteCountDouble,
                rxFrameRateDouble,
                rxCount,
                frameLossRate
        );
        eventDisruptions.setFrameLoss(frameLoss);


        // Calculate current utilization based on the frame size and byte count
        double currentUtilizationTX = Double.parseDouble(txRateStr);
        double currentUtilizationRX = Double.parseDouble(rxRateStr);
        log.info("getting FcCalculationValues for fcRate: {}", fcRate);
        FcCalculationValues fcCalculationValues = SimpleFCRateUtil.getLineUtilizationCommand(fcRate);
        if (fcCalculationValues == null) {
            log.error("Invalid fcRate: {}", fcRate);
            return null;
        }
        log.info("FcCalculationValues: {}", fcCalculationValues);
        double MessuredThroughputTx = fcCalculationValues.getActualThroughput() * (currentUtilizationTX / 100);
        double MessuredThroughputRx = fcCalculationValues.getActualThroughput() * (currentUtilizationRX / 100);
        log.info("Measured Throughput TX: {}, RX: {}", MessuredThroughputTx, MessuredThroughputRx);
        double transferSpeedTx = fcCalculationValues.getActualTransferRate() * (currentUtilizationTX / 100);
        double transferSpeedRx = fcCalculationValues.getActualTransferRate() * (currentUtilizationRX / 100);
        log.info("Transfer Speed TX: {}, RX: {}", transferSpeedTx, transferSpeedRx);
        double lineSpeedTx = fcCalculationValues.getLineSpeed() * (currentUtilizationTX / 100);
        double lineSpeedRx = fcCalculationValues.getLineSpeed() * (currentUtilizationRX / 100);
        log.info("Line Speed TX: {}, RX: {}", lineSpeedTx, lineSpeedRx);
        TrafficResponse[] trafficResponses = new TrafficResponse[2];
        trafficResponses[0] = new TrafficResponse(
                "TX",
                currentUtilizationTX,
                MessuredThroughputTx,
                transferSpeedTx,
                lineSpeedTx
        );
        trafficResponses[1] = new TrafficResponse(
                "RX",
                currentUtilizationRX,
                MessuredThroughputRx,
                transferSpeedRx,
                lineSpeedRx
        );
        eventDisruptions.setTraffic(trafficResponses);
        eventDisruptions.setStandard(new StandardTestResponse(fcRate, frameSize));
        log.info("EventDisruptions: {}", eventDisruptions);
        return eventDisruptions;
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

    @Scheduled(fixedRate = 3600000) // runs every hour
    private void hourlyEventDisruptions() {
        if (!scheduledEventEnabled) {
            return;
        }
        log.info("Hourly event disruptions execution...");
        eventAggregatorConfig.updateHourlyEventDisruptions();
    }
}
