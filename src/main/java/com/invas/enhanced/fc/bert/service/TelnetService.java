package com.invas.enhanced.fc.bert.service;

import com.invas.enhanced.fc.bert.model.ConnectionResponse;

public interface TelnetService {

    ConnectionResponse getConnection(String localIpaddress, int port);

    String disconnect();

    String status();

    String sendCommand(String command);
}
