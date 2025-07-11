package com.invas.enhanced.fc.bert.service.config;

import com.invas.enhanced.fc.bert.model.config.PortStatus;
import com.invas.enhanced.fc.bert.model.config.PhysicalStatus;
import com.invas.enhanced.fc.bert.model.config.ToolStatus;
import com.invas.enhanced.fc.bert.utils.ConfigScpiConst;
import com.invas.enhanced.fc.bert.utils.ScpiTelnetHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final ScpiTelnetHandler scpiTelnetHandler;

    @Override
    public String testControl(boolean toggle) {
        return scpiTelnetHandler.sendCommand(ConfigScpiConst.controller(toggle? "START" : "STOP"));
    }

    @Override
    public String testReset() {
        return scpiTelnetHandler.sendCommand(ConfigScpiConst.controller("RESET"));
    }

    @Override
    public String testTime() {
        return scpiTelnetHandler.sendCommand(ConfigScpiConst.controller("RESET"));
    }

    @Override
    public String togglePSPLink(boolean toggle) {
        return scpiTelnetHandler.sendCommand(ConfigScpiConst.pspLink(toggle ? "ENABLE" : "DISABLE"));
    }

    @Override
    public String getPSPLink() {
        return scpiTelnetHandler.sendCommand(ConfigScpiConst.pspLink("LINK"));
    }

    @Override
    public PhysicalStatus getPhysicalStatus() {

        //Execute command to get status
        return new PhysicalStatus(
                scpiTelnetHandler.sendCommand(ConfigScpiConst.laserCntrl("STAT")),
                scpiTelnetHandler.sendCommand(ConfigScpiConst.interfaceType("VALUE")),
                scpiTelnetHandler.sendCommand(ConfigScpiConst.physicalPort("STATUS")),
                scpiTelnetHandler.sendCommand(ConfigScpiConst.logging("LINK-STATUS")),
                scpiTelnetHandler.sendCommand(ConfigScpiConst.consoleOuput("TX")),
                scpiTelnetHandler.sendCommand(ConfigScpiConst.consoleOuput("RX"))
        );
    }

    public String laserControl(boolean type) {
        return scpiTelnetHandler.sendCommand(ConfigScpiConst.laserCntrl(type ? "ON" : "OFF"));
    }

    public PortStatus getPortStatus() {
        //Execute command to get status
        return new PortStatus(
                ConfigScpiConst.toolStatus("SOURCE"),
                ConfigScpiConst.toolStatus("DESTINATION"),
                ConfigScpiConst.toolStatus("FLOW-CONTROL"),
                ConfigScpiConst.toolStatus("CREDIT"),
                ConfigScpiConst.toolStatus("LOGGING"),
                ConfigScpiConst.toolStatus("TOPOLOGY"),
                ConfigScpiConst.toolStatus("FABRIC-STATUS"),
                ConfigScpiConst.toolStatus("PORT-STATUS")
        );
    }

    public ToolStatus getToolStatus() {
        return new ToolStatus(
                scpiTelnetHandler.sendCommand(ConfigScpiConst.fcbertConfiguration("COUPLED")),
                scpiTelnetHandler.sendCommand(ConfigScpiConst.fcbertConfiguration("PATTERN")),
                scpiTelnetHandler.sendCommand(ConfigScpiConst.fcbertConfiguration("FRAME-SIZE")),
                scpiTelnetHandler.sendCommand(ConfigScpiConst.fcbertConfiguration("STREAM-RATE"))
        );
    }

    public String getPSPLinkStatus() {
        return scpiTelnetHandler.sendCommand(ConfigScpiConst.pspLink("LINK"));
    }
}
