package com.invas.enhanced.fc.bert.service.telnet;

import com.invas.enhanced.fc.bert.config.FetchIPAddress;
import com.invas.enhanced.fc.bert.model.telnet.ConnectionResponse;
import com.invas.enhanced.fc.bert.model.telnet.IPAddress;
import com.invas.enhanced.fc.bert.service.ScpiTelnetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelnetServiceImpl implements TelnetService {

    private final ScpiTelnetService scpiTelnetService;
    private final FetchIPAddress fetchIPAddress;

    @Override
    public ConnectionResponse getConnection(String localIpaddress) {
        boolean connected = scpiTelnetService.getConnection(localIpaddress, 5024);

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
        scpiTelnetService.disconnect();
        return !scpiTelnetService.getStatus() ? "true" : "false";
    }

    public String status() {
        log.info("Get Status {}", scpiTelnetService.getStatus());
        return scpiTelnetService.getStatus() ? "true" : "false";
    }

}
