package com.invas.enhanced.fc.bert.service.telnet;

import com.invas.enhanced.fc.bert.config.FetchIPAddress;
import com.invas.enhanced.fc.bert.model.telnet.ConnectionResponse;
import com.invas.enhanced.fc.bert.model.telnet.IPAddress;
import com.invas.enhanced.fc.bert.utils.TelnetConfigUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelnetServiceImpl implements TelnetService {

    private final TelnetConfigUtil telnetConfigUtil;
    private final FetchIPAddress fetchIPAddress;

    @Override
    public ConnectionResponse getConnection(String localIpaddress, int port) {
        telnetConfigUtil.getConnection(localIpaddress, port);

        return new ConnectionResponse(
                telnetConfigUtil.getStatus(),
                localIpaddress,
                telnetConfigUtil.getStatus() ? "Connection established successfully" : "Connection failed"
        );
    }

    public IPAddress getIPAddress() {
        return fetchIPAddress.getIP();
    }

    public String disconnect() {
        telnetConfigUtil.disconnect();
        return !telnetConfigUtil.getStatus() ? "true" : "false";
    }

    public String status() {
        log.info(telnetConfigUtil.getAddress());
        return telnetConfigUtil.getStatus() ? "true" : "false";
    }

}
