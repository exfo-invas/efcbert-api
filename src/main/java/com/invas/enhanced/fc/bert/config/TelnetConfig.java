package com.invas.enhanced.fc.bert.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.telnet.TelnetClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
@Configuration
public class TelnetConfig {

    private TelnetClient telnetClient = new TelnetClient();

    @Bean
    public TelnetClient telnetClient() {
        return telnetClient;
    }

    public String getConnection(String localIpaddress, int port) {
        try {
            // Set the system property to prefer IPv6 addresses if needed
            if (isIPv6Address(localIpaddress)) {
                System.setProperty("java.net.preferIPv6Addresses", "true");
            } else {
                System.setProperty("java.net.preferIPv6Addresses", "false");
            }

            telnetClient = new TelnetClient();
            telnetClient.connect(localIpaddress, port);
            return "Connected to the server: " + localIpaddress;
        } catch (IOException e) {
            return "Failed to connect to the server: " + localIpaddress;
        }
    }

    private boolean isIPv6Address(String ipAddress) {
        return ipAddress.contains(":");
    }

    public boolean getStatus() {
        log.info("Getting connection status");
        return telnetClient.isConnected();
    }

    public InputStream getInputStream() {
        log.info("Getting input stream");
        return telnetClient.getInputStream();
    }

    public OutputStream getOutputStream() {
        log.info("Getting output stream");
        return telnetClient.getOutputStream();
    }

    public void disconnect() {
        try{
            telnetClient.disconnect();
            log.info("Disconnected from the server");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
