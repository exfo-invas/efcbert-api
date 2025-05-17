package com.invas.enhanced.fc.bert.service.config;

import com.invas.enhanced.fc.bert.model.config.PortStatus;
import com.invas.enhanced.fc.bert.model.config.PhysicalStatus;
import com.invas.enhanced.fc.bert.model.config.ToolStatus;
import com.invas.enhanced.fc.bert.utils.ScpiCommandConstants;
import com.invas.enhanced.fc.bert.utils.TelnetConfigUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final TelnetConfigUtil telnetConfigUtil;

    @Override
    public PhysicalStatus getPhysicalStatus() {

        String command = "BEGIN" + "\n" +
                ScpiCommandConstants.laserCntrl("STAT") + "\n" +
                ScpiCommandConstants.interfaceType("VALUE") + "\n" +
                ScpiCommandConstants.physicalPort("STATUS") + "\n" +
                ScpiCommandConstants.logging("LINK-STATUS") + "\n" +
                ScpiCommandConstants.consoleOuput("TX") + "\n" +
                ScpiCommandConstants.consoleOuput("RX") + "\n" +
                "END";

        String[] response = telnetConfigUtil.sendCommand(command).split("\n");

        //Execute command to get status
        return new PhysicalStatus(
                response[1],
                response[2],
                response[3],
                response[4],
                response[5],
                response[6]
        );
    }

    public String laserControl(boolean type) {
        return telnetConfigUtil.sendCommand(ScpiCommandConstants.laserCntrl(type ? "ON" : "OFF"));
    }

    public PortStatus getPortStatus() {
        String command = "BEGIN" + "\n" +
                ScpiCommandConstants.toolStatus("SOURCE") + "?" + "\n" +
                ScpiCommandConstants.toolStatus("DESTINATION") + "?" + "\n" +
                ScpiCommandConstants.toolStatus("FLOW-CONTROL") + "?" + "\n" +
                ScpiCommandConstants.toolStatus("CREDIT") + "?" + "\n" +
                ScpiCommandConstants.toolStatus("LOGGING") + "?" + "\n" +
                ScpiCommandConstants.toolStatus("TOPOLOGY") + "\n" +
                ScpiCommandConstants.toolStatus("FABRIC-STATUS") + "\n" +
                ScpiCommandConstants.toolStatus("PORT-STATUS") + "\n" +
                "END";

        String[] response = telnetConfigUtil.sendCommand(command).split("\n");

        //Execute command to get status
        return new PortStatus(
                response[1],
                response[2],
                response[3],
                response[4],
                response[5],
                response[6],
                response[7],
                response[8]
        );
    }

    public ToolStatus getToolStatus() {
        String command = "BEGIN" + "\n" +
                ScpiCommandConstants.fcbertConfiguration("COUPLED") + "?" + "\n" +
                ScpiCommandConstants.fcbertConfiguration("PATTERN") + "\n" +
                ScpiCommandConstants.fcbertConfiguration("FRAME-RATE") + "?" + "\n" +
                ScpiCommandConstants.fcbertConfiguration("STREAM-RATE") + "?" + "\n" +
                "END";

        String[] response = telnetConfigUtil.sendCommand(command).split("\n");

        return new ToolStatus(
                response[1],
                response[2],
                response[3],
                response[4]
        );
    }

    public String getPSPLinkStatus() {
        return telnetConfigUtil.sendCommand(ScpiCommandConstants.pspLink("LINK"));
    }
}
