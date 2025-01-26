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

    public String getConnection(String ipAddress, int port) {
        try {
            if (isIPv6Address(ipAddress)) {
                return connectIPv6(ipAddress, port);
            } else {
                return connectIPv4(ipAddress, port);
            }
        } catch (IOException e) {
            return "Failed to connect to the server: " + ipAddress;
        }
    }

    private String connectIPv6(String ipAddress, int port) throws IOException {
        System.setProperty("java.net.preferIPv6Addresses", "true");
        telnetClient = new TelnetClient();
        telnetClient.connect(ipAddress, port);
        return "Connected to the server: " + ipAddress;
    }

    private String connectIPv4(String ipAddress, int port) throws IOException {
        System.setProperty("java.net.preferIPv6Addresses", "false");
        telnetClient = new TelnetClient();
        telnetClient.connect(ipAddress, port);
        return "Connected to the server: " + ipAddress;
    }

    private boolean isIPv6Address(String ipAddress) {
        return ipAddress.contains(":");
    }

    public boolean getStatus() {
        log.info("Getting connection status");
        return telnetClient.isConnected();
    }

    public String getAddress() {
        log.info("Getting connection address");
        return telnetClient.getRemoteAddress().getHostName() + ":" + telnetClient.getRemotePort();
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
        try {
            telnetClient.disconnect();
            log.info("Disconnected from the server");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
