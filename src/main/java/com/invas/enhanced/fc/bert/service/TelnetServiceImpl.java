package com.invas.enhanced.fc.bert.service;

import com.invas.enhanced.fc.bert.model.ConnectionResponse;
import com.invas.enhanced.fc.bert.utils.TelnetConfigUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.constant.Constable;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelnetServiceImpl implements TelnetService {

    private final TelnetConfigUtils telnetConfigUtils;

    @Override
    public ConnectionResponse getConnection(String localIpaddress, int port) {
        telnetConfigUtils.getConnection(localIpaddress, 23);

        return new ConnectionResponse(
                telnetConfigUtils.getStatus(),
                localIpaddress,
                telnetConfigUtils.getStatus() ? "Connection established successfully" : "Connection failed"
        );
    }

    public String disconnect() {
        telnetConfigUtils.disconnect();
        return !telnetConfigUtils.getStatus() ? "true" : "false";
    }

    public String status() {
        log.info(telnetConfigUtils.getAddress());
        return telnetConfigUtils.getStatus() ? "true" : "false";
    }


    public String sendCommand(String command) {
        try {
            return telnetConfigUtils.readAndWriteData(command);
        } catch (Exception e) {
            return "Failed to send command";
        }
    }

}
