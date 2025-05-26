package com.invas.enhanced.fc.bert.service.config;

import com.invas.enhanced.fc.bert.model.config.PortStatus;
import com.invas.enhanced.fc.bert.model.config.PhysicalStatus;
import com.invas.enhanced.fc.bert.model.config.ToolStatus;
import com.invas.enhanced.fc.bert.utils.ScpiCommandConstants;
import com.invas.enhanced.fc.bert.utils.TelnetConfigUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final TelnetConfigUtil telnetConfigUtil;

    @Override
    public String testControl(boolean toggle) {
        return telnetConfigUtil.sendCommand(ScpiCommandConstants.controller(toggle? "START" : "STOP"));
    }

    @Override
    public String testReset() {
        return telnetConfigUtil.sendCommand(ScpiCommandConstants.controller("RESET"));
    }

    @Override
    public String testTime() {
        return telnetConfigUtil.sendCommand(ScpiCommandConstants.controller("RESET"));
    }

    @Override
    public String togglePSPLink(boolean toggle) {
        return telnetConfigUtil.sendCommand(ScpiCommandConstants.pspLink(toggle ? "ENABLE" : "DISABLE"));
    }

    @Override
    public String getPSPLink() {
        return telnetConfigUtil.sendCommand(ScpiCommandConstants.pspLink("LINK"));
    }

    @Override
    public PhysicalStatus getPhysicalStatus() {

        //Execute command to get status
        return new PhysicalStatus(
                telnetConfigUtil.sendCommand(ScpiCommandConstants.laserCntrl("STAT")),
                telnetConfigUtil.sendCommand(ScpiCommandConstants.interfaceType("VALUE")),
                telnetConfigUtil.sendCommand(ScpiCommandConstants.physicalPort("STATUS")),
                telnetConfigUtil.sendCommand(ScpiCommandConstants.logging("LINK-STATUS")),
                telnetConfigUtil.sendCommand(ScpiCommandConstants.consoleOuput("TX")),
                telnetConfigUtil.sendCommand(ScpiCommandConstants.consoleOuput("RX"))
        );
    }

    public String laserControl(boolean type) {
        return telnetConfigUtil.sendCommand(ScpiCommandConstants.laserCntrl(type ? "ON" : "OFF"));
    }

    public PortStatus getPortStatus() {
        //Execute command to get status
        return new PortStatus(
                telnetConfigUtil.sendCommand(ScpiCommandConstants.toolStatus("SOURCE")),
                telnetConfigUtil.sendCommand(ScpiCommandConstants.toolStatus("DESTINATION")),
                telnetConfigUtil.sendCommand(ScpiCommandConstants.toolStatus("FLOW-CONTROL")),
                telnetConfigUtil.sendCommand(ScpiCommandConstants.toolStatus("CREDIT")),
                telnetConfigUtil.sendCommand(ScpiCommandConstants.toolStatus("LOGGING")),
                telnetConfigUtil.sendCommand(ScpiCommandConstants.toolStatus("TOPOLOGY")),
                telnetConfigUtil.sendCommand(ScpiCommandConstants.toolStatus("FABRIC-STATUS")),
                telnetConfigUtil.sendCommand(ScpiCommandConstants.toolStatus("PORT-STATUS"))
        );
    }

    public ToolStatus getToolStatus() {
        return new ToolStatus(
                telnetConfigUtil.sendCommand(ScpiCommandConstants.fcbertConfiguration("COUPLED")),
                telnetConfigUtil.sendCommand(ScpiCommandConstants.fcbertConfiguration("PATTERN")),
                telnetConfigUtil.sendCommand(ScpiCommandConstants.fcbertConfiguration("FRAME-SIZE")),
                telnetConfigUtil.sendCommand(ScpiCommandConstants.fcbertConfiguration("STREAM-RATE"))
        );
    }

    public String getPSPLinkStatus() {
        return telnetConfigUtil.sendCommand(ScpiCommandConstants.pspLink("LINK"));
    }
}
