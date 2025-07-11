package com.invas.enhanced.fc.bert.service.telnet;

import com.invas.enhanced.fc.bert.config.FetchIPAddress;
import com.invas.enhanced.fc.bert.model.telnet.ConnectionResponse;
import com.invas.enhanced.fc.bert.model.telnet.IPAddress;
import com.invas.enhanced.fc.bert.utils.ScpiTelnetHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelnetServiceImpl implements TelnetService {

    private final ScpiTelnetHandler scpiTelnetHandler;
    private final FetchIPAddress fetchIPAddress;

    @Override
    public ConnectionResponse getConnection(String localIpaddress, int port) {
        boolean connected = scpiTelnetHandler.getConnection(localIpaddress, port);

        return new ConnectionResponse(
                connected,
                localIpaddress,
                connected ? "Connection established successfully" : "Connection failed"
        );
    }

    public IPAddress getIPAddress() {
        return fetchIPAddress.getIP();
    }

    public String disconnect() {
        scpiTelnetHandler.disconnect();
        return !scpiTelnetHandler.getStatus() ? "true" : "false";
    }

    public String status() {
        log.info("Get Status {}", scpiTelnetHandler.getStatus());
        return scpiTelnetHandler.getStatus() ? "true" : "false";
    }

}
