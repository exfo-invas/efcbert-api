package com.invas.enhanced.fc.bert.service.config;

import com.invas.enhanced.fc.bert.model.config.PortStatus;
import com.invas.enhanced.fc.bert.model.config.PhysicalStatus;
import com.invas.enhanced.fc.bert.model.config.ToolStatus;
import com.invas.enhanced.fc.bert.utils.ConfigScpiConst;
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
        return telnetConfigUtil.sendCommand(ConfigScpiConst.controller(toggle? "START" : "STOP"));
    }

    @Override
    public String testReset() {
        return telnetConfigUtil.sendCommand(ConfigScpiConst.controller("RESET"));
    }

    @Override
    public String testTime() {
        return telnetConfigUtil.sendCommand(ConfigScpiConst.controller("RESET"));
    }

    @Override
    public String togglePSPLink(boolean toggle) {
        return telnetConfigUtil.sendCommand(ConfigScpiConst.pspLink(toggle ? "ENABLE" : "DISABLE"));
    }

    @Override
    public String getPSPLink() {
        return telnetConfigUtil.sendCommand(ConfigScpiConst.pspLink("LINK"));
    }

    @Override
    public PhysicalStatus getPhysicalStatus() {

        //Execute command to get status
        return new PhysicalStatus(
                telnetConfigUtil.sendCommand(ConfigScpiConst.laserCntrl("STAT")),
                telnetConfigUtil.sendCommand(ConfigScpiConst.interfaceType("VALUE")),
                telnetConfigUtil.sendCommand(ConfigScpiConst.physicalPort("STATUS")),
                telnetConfigUtil.sendCommand(ConfigScpiConst.logging("LINK-STATUS")),
                telnetConfigUtil.sendCommand(ConfigScpiConst.consoleOuput("TX")),
                telnetConfigUtil.sendCommand(ConfigScpiConst.consoleOuput("RX"))
        );
    }

    public String laserControl(boolean type) {
        return telnetConfigUtil.sendCommand(ConfigScpiConst.laserCntrl(type ? "ON" : "OFF"));
    }

    public PortStatus getPortStatus() {
        //Execute command to get status
        return new PortStatus(
                telnetConfigUtil.sendCommand(ConfigScpiConst.toolStatus("SOURCE")),
                telnetConfigUtil.sendCommand(ConfigScpiConst.toolStatus("DESTINATION")),
                telnetConfigUtil.sendCommand(ConfigScpiConst.toolStatus("FLOW-CONTROL")),
                telnetConfigUtil.sendCommand(ConfigScpiConst.toolStatus("CREDIT")),
                telnetConfigUtil.sendCommand(ConfigScpiConst.toolStatus("LOGGING")),
                telnetConfigUtil.sendCommand(ConfigScpiConst.toolStatus("TOPOLOGY")),
                telnetConfigUtil.sendCommand(ConfigScpiConst.toolStatus("FABRIC-STATUS")),
                telnetConfigUtil.sendCommand(ConfigScpiConst.toolStatus("PORT-STATUS"))
        );
    }

    public ToolStatus getToolStatus() {
        return new ToolStatus(
                telnetConfigUtil.sendCommand(ConfigScpiConst.fcbertConfiguration("COUPLED")),
                telnetConfigUtil.sendCommand(ConfigScpiConst.fcbertConfiguration("PATTERN")),
                telnetConfigUtil.sendCommand(ConfigScpiConst.fcbertConfiguration("FRAME-SIZE")),
                telnetConfigUtil.sendCommand(ConfigScpiConst.fcbertConfiguration("STREAM-RATE"))
        );
    }

    public String getPSPLinkStatus() {
        return telnetConfigUtil.sendCommand(ConfigScpiConst.pspLink("LINK"));
    }
}
