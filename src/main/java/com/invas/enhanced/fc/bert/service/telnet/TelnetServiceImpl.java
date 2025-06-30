package com.invas.enhanced.fc.bert.service.telnet;

import com.invas.enhanced.fc.bert.config.FetchIPAddress;
import com.invas.enhanced.fc.bert.config.TelnetConfig;
import com.invas.enhanced.fc.bert.model.telnet.ConnectionResponse;
import com.invas.enhanced.fc.bert.model.telnet.IPAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelnetServiceImpl implements TelnetService {

    private final TelnetConfig telnetConfig;
    private final FetchIPAddress fetchIPAddress;

    @Override
    public ConnectionResponse getConnection(String localIpaddress, int port) {
        telnetConfig.getConnection(localIpaddress, port);

        return new ConnectionResponse(
                telnetConfig.getStatus(),
                localIpaddress,
                telnetConfig.getStatus() ? "Connection established successfully" : "Connection failed"
        );
    }

    public IPAddress getIPAddress() {
        return fetchIPAddress.getIP();
    }

    public String disconnect() {
        telnetConfig.disconnect();
        return !telnetConfig.getStatus() ? "true" : "false";
    }

    public String status() {
        log.info(telnetConfig.getAddress());
        return telnetConfig.getStatus() ? "true" : "false";
    }

}
