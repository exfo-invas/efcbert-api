package com.invas.enhanced.fc.bert.service.config;

import com.invas.enhanced.fc.bert.config.EventAggregatorConfig;
import com.invas.enhanced.fc.bert.config.StandardConfig;
import com.invas.enhanced.fc.bert.contants.EventScpiConst;
import com.invas.enhanced.fc.bert.model.config.FullConfigStatus;
import com.invas.enhanced.fc.bert.model.config.PortStatus;
import com.invas.enhanced.fc.bert.model.config.PhysicalStatus;
import com.invas.enhanced.fc.bert.model.config.ToolStatus;
import com.invas.enhanced.fc.bert.contants.ConfigScpiConst;
import com.invas.enhanced.fc.bert.model.event.EventDisruptions;
import com.invas.enhanced.fc.bert.model.event.FrameLoss;
import com.invas.enhanced.fc.bert.model.event.StandardTestResponse;
import com.invas.enhanced.fc.bert.model.event.TrafficResponse;
import com.invas.enhanced.fc.bert.service.ScpiTelnetService;
import com.invas.enhanced.fc.bert.service.event.EventService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final ScpiTelnetService scpiTelnetService;
    private final EventService eventService;
    private final StandardConfig standardConfig;
    private final EventAggregatorConfig eventAggregatorConfig;

    @Override
    public boolean testControl(boolean toggle) {

        if (scpiTelnetService.sendCommand(ConfigScpiConst.controller(toggle ? "START" : "STOP")).equalsIgnoreCase("true")) {
            eventService.startScheduledEvent(toggle);
            log.info("Test control command executed successfully: {}", toggle ? "START" : "STOP");
            if (!toggle) {
                getloggingList();
            }
            return true;
        } else {
            log.error("Failed to execute test control command: {}", toggle ? "START" : "STOP");
            return false;
        }
    }

    @Override
    public String testReset() {
        return scpiTelnetService.sendCommand(ConfigScpiConst.controller("RESET"));
    }

    @Override
    public String testTime() {
        return scpiTelnetService.sendCommand(ConfigScpiConst.controller("RESET"));
    }

    @Override
    public String togglePSPLink(boolean toggle) {
        return scpiTelnetService.sendCommand(ConfigScpiConst.pspLink(toggle ? "ENABLE" : "DISABLE"));
    }

    @Override
    public String getPSPLink() {
        return scpiTelnetService.sendCommand(ConfigScpiConst.pspLink("LINK"));
    }

    @Override
    public FullConfigStatus getFullConfigStatus() {
        //Execute command to get status
        return new FullConfigStatus(
                getPhysicalStatus(),
                getPortStatus(),
                getToolStatus(),
                getPSPLinkStatus()
        );
    }

    public PhysicalStatus getPhysicalStatus() {
        String fcRate = this.scpiTelnetService.sendCommand(ConfigScpiConst.interfaceType("VALUE"));
        String frameSize = this.scpiTelnetService.sendCommand(EventScpiConst.frameSize());
        if (fcRate == null || frameSize == null) {
            log.error("Failed to retrieve FC Rate or Frame Size.");
            return null;
        } else {
            this.standardConfig.setStandardTestResponse(
                    new StandardTestResponse(
                            fcRate,
                            frameSize
                    )
            );
        }
        return new PhysicalStatus(
                this.scpiTelnetService.sendCommand(ConfigScpiConst.laserCntrl("STAT")),
                fcRate,
                this.scpiTelnetService.sendCommand(ConfigScpiConst.physicalPort("STATUS")),
                this.scpiTelnetService.sendCommand(ConfigScpiConst.consoleOuput("TX")),
                this.scpiTelnetService.sendCommand(ConfigScpiConst.consoleOuput("RX")));
    }

    public String laserControl(boolean type) {
        return scpiTelnetService.sendCommand(ConfigScpiConst.laserCntrl(type ? "ON" : "OFF"));
    }

    public PortStatus getPortStatus() {
        //Execute command to get status
        return new PortStatus(
                scpiTelnetService.sendCommand(ConfigScpiConst.toolStatus("FLOW-CONTROL")),
                scpiTelnetService.sendCommand(ConfigScpiConst.toolStatus("CREDIT-STAT")),
                scpiTelnetService.sendCommand(ConfigScpiConst.toolStatus("LOGGING-STAT"))
        );
    }

    public ToolStatus getToolStatus() {
        return new ToolStatus(
                scpiTelnetService.sendCommand(ConfigScpiConst.fcbertConfiguration("COUPLED-STAT")),
                scpiTelnetService.sendCommand(ConfigScpiConst.fcbertConfiguration("PATTERN")),
                scpiTelnetService.sendCommand(ConfigScpiConst.fcbertConfiguration("PATTERN")),
                scpiTelnetService.sendCommand(ConfigScpiConst.fcbertConfiguration("FRAME-SIZE-STAT")),
                scpiTelnetService.sendCommand(ConfigScpiConst.fcbertConfiguration("STREAM-RATE-STAT"))
        );
    }

    public String getPSPLinkStatus() {
        return scpiTelnetService.sendCommand(ConfigScpiConst.pspLink("LINK"));
    }

    private void getloggingList() {
    String logg = this.scpiTelnetService.sendCommand(EventScpiConst.loggingList());
    eventAggregatorConfig.generateExportFile();
    log.info("Logging List: {}", logg);
  }
}
