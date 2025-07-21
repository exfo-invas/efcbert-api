package com.invas.enhanced.fc.bert.service.config;

import com.invas.enhanced.fc.bert.model.config.PortStatus;
import com.invas.enhanced.fc.bert.model.config.PhysicalStatus;
import com.invas.enhanced.fc.bert.model.config.ToolStatus;
import com.invas.enhanced.fc.bert.contants.ConfigScpiConst;
import com.invas.enhanced.fc.bert.service.ScpiTelnetService;
import com.invas.enhanced.fc.bert.service.event.EventService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final ScpiTelnetService scpiTelnetService;
    private final EventService eventService;

    @Override
    public String testControl(boolean toggle) {
        eventService.startScheduledEvent(toggle);
        return scpiTelnetService.sendCommand(ConfigScpiConst.controller(toggle? "START" : "STOP"));
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
    public PhysicalStatus getPhysicalStatus() {        

        //Execute command to get status
        return new PhysicalStatus(
                scpiTelnetService.sendCommand(ConfigScpiConst.laserCntrl("STAT")),
                scpiTelnetService.sendCommand(ConfigScpiConst.interfaceType("VALUE")),
                scpiTelnetService.sendCommand(ConfigScpiConst.physicalPort("STATUS")),
                scpiTelnetService.sendCommand(ConfigScpiConst.consoleOuput("TX")),
                scpiTelnetService.sendCommand(ConfigScpiConst.consoleOuput("RX"))
        );
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
}
