package com.invas.enhanced.fc.bert.service.event;

import com.invas.enhanced.fc.bert.model.FcCalculationValues;
import com.invas.enhanced.fc.bert.model.event.disruptions.EventDisruptions;
import com.invas.enhanced.fc.bert.model.event.disruptions.FrameLoss;
import com.invas.enhanced.fc.bert.model.event.disruptions.TrafficResponse;
import com.invas.enhanced.fc.bert.contants.ConfigScpiConst;
import com.invas.enhanced.fc.bert.contants.EventScpiConst;
import com.invas.enhanced.fc.bert.service.ScpiTelnetService;
import com.invas.enhanced.fc.bert.utils.SimpleFCRateUtil;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventService {

    private final ScpiTelnetService scpiTelnetService;

    public EventService(ScpiTelnetService scpiTelnetService) {
        this.scpiTelnetService = scpiTelnetService;
    }


    public EventDisruptions executeEventDisruptions() {
        EventDisruptions eventDisruptions = new EventDisruptions();

        String fcRate = scpiTelnetService.sendCommand(ConfigScpiConst.interfaceType("VALUE"));
        int txCount = Integer.parseInt(scpiTelnetService.sendCommand(EventScpiConst.byteCount("TX")));
        int rxCount = Integer.parseInt(scpiTelnetService.sendCommand(EventScpiConst.byteCount("RX")));
        int lostFrames = txCount - rxCount;
        double frameLossRate = (double) lostFrames / txCount * 100;

        FrameLoss frameLoss = new FrameLoss(fcRate, txCount, rxCount, lostFrames, frameLossRate);
        eventDisruptions.setFrameLoss(frameLoss);


        // Calculate current utilization based on the frame size and byte count
        double currentUtilizationTX = Double.parseDouble(scpiTelnetService.sendCommand(EventScpiConst.lineUtilization("TX")));
        double currentUtilizationRX = Double.parseDouble(scpiTelnetService.sendCommand(EventScpiConst.lineUtilization("RX")));
        log.info("getting FcCalculationValues for fcRate: {}", fcRate);
        FcCalculationValues fcCalculationValues = SimpleFCRateUtil.getLineUtilizationCommand(fcRate);
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
}
