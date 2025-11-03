package com.invas.enhanced.fc.bert.service.telnet;

import com.invas.enhanced.fc.bert.model.telnet.ConnectionResponse;
import com.invas.enhanced.fc.bert.model.telnet.IPAddress;

public interface TelnetService {

    ConnectionResponse getConnection(String localIpaddress);

    String disconnect();

    String status();

    IPAddress getIPAddress();
}
