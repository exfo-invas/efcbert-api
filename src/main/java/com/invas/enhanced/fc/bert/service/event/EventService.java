package com.invas.enhanced.fc.bert.service.event;

import com.invas.enhanced.fc.bert.model.FcCalculationValues;
import com.invas.enhanced.fc.bert.model.event.disruptions.EventDisruptions;
import com.invas.enhanced.fc.bert.model.event.disruptions.FrameLoss;
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

    public EventService(ScpiTelnetService scpiTelnetService, EventAggregatorConfig eventAggregatorConfig) {
        this.scpiTelnetService = scpiTelnetService;
        this.eventAggregatorConfig = eventAggregatorConfig;
    }


    public EventDisruptions executeEventDisruptions() {
        EventDisruptions eventDisruptions = new EventDisruptions();

        String txStr = scpiTelnetService.sendCommand(EventScpiConst.byteCount("TX"));
        String rxStr = scpiTelnetService.sendCommand(EventScpiConst.byteCount("RX"));
        String txRateStr = scpiTelnetService.sendCommand(EventScpiConst.lineUtilization("TX"));
        String rxRateStr = scpiTelnetService.sendCommand(EventScpiConst.lineUtilization("RX"));
        if (txStr == null && rxStr == null && txRateStr == null && rxRateStr == null) {
            log.error("Failed to retrieve TX or RX byte counts.");
            return null;
        }

        String fcRate = scpiTelnetService.sendCommand(ConfigScpiConst.interfaceType("VALUE"));
        int txCount = Integer.parseInt(txStr);
        int rxCount = Integer.parseInt(rxStr);
        int lostFrames = txCount - rxCount;
        double frameLossRate = (double) lostFrames / txCount * 100;

        FrameLoss frameLoss = new FrameLoss(fcRate, txCount, rxCount, lostFrames, frameLossRate);
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
                fcRate,
                fcCalculationValues.getActualThroughput(),
                fcCalculationValues.getActualTransferRate(),
                fcCalculationValues.getLineSpeed(),
                currentUtilizationTX,
                MessuredThroughputTx,
                transferSpeedTx,
                lineSpeedTx
        );
        trafficResponses[1] = new TrafficResponse(
                "RX",
                fcRate,
                fcCalculationValues.getActualThroughput(),
                fcCalculationValues.getActualTransferRate(),
                fcCalculationValues.getLineSpeed(),
                currentUtilizationRX,
                MessuredThroughputRx,
                transferSpeedRx,
                lineSpeedRx
        );
        eventDisruptions.setTraffic(trafficResponses);

        log.info("EventDisruptions: {}", eventDisruptions);
        return eventDisruptions;
    }

    @Scheduled(fixedRate = 1000) // runs every second
    private void getLatestEventDisruption() {
        log.info("Scheduling event disruptions execution...");
        eventAggregatorConfig.updateEventDisruptionsList(executeEventDisruptions());
    }

    @Scheduled(fixedRate = 3600000) // runs every hour
    public void hourlyEventDisruptions() {
        log.info("Hourly event disruptions execution...");
        eventAggregatorConfig.updateHourlyEventDisruptions();
    }
}
