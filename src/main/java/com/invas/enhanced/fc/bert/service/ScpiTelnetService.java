package com.invas.enhanced.fc.bert.service;

import com.invas.enhanced.fc.bert.config.TelnetConfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.io.*;

@Slf4j
@Service
@Configuration
public class ScpiTelnetService {

    private final TelnetConfig telnetConfig;

    private PrintWriter writer;
    /*private Scanner reader;*/
    private BufferedReader reader;

    public ScpiTelnetService(TelnetConfig telnetConfig) {
        this.telnetConfig = telnetConfig;
    }

    public boolean getConnection(String localIpaddress, int port) {
        telnetConfig.getConnection(localIpaddress, port);

        if (telnetConfig.getStatus()) {
            writer = new PrintWriter(telnetConfig.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(telnetConfig.getInputStream()));
            log.info("TelnetConfigUtil Connected to {}:{}", localIpaddress, port);
            log.info("TelnetConfigUtil writer: {} \n reader: {}", writer.checkError(), reader);
            return true;
        }
        return false;
    }

    public boolean getStatus() {
        return telnetConfig.getStatus();
    }

    public String getAddress() {
        return telnetConfig.getAddress();
    }

    public synchronized String sendCommand(String command) {
        if (!telnetConfig.getStatus()) {
            return "Connection is not established";
        }
        try {
            writer.println(command);
            writer.flush();
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                response.append(line).append("\n");
                log.info("in line {}", response);
                if (line.toLowerCase().contains("ready") || line.toLowerCase().contains("module not present on slot 1")) {
                    break;
                }
            }
            log.info("Command sent: {}, Response: {}", command, response);
            return sanitizeResponse(response.toString());
        } catch (Exception e) {
            log.error("Error during command execution: {}", e.getMessage(), e);
            return "Failed to send command";
        }
    }

    /**
     * Cleans SCPI response:
     * - Removes "READY>" and trailing newlines/spaces
     * - Ignores response if it contains "error" (case-insensitive)
     * - Returns cleaned data if valid, otherwise null
     */
    private String sanitizeResponse(String response) {
        if (response == null || response.isBlank()) {
            return null;
        }

        if (response.contains("Connected")) {
            log.info("response contains connected {}", response);
            return "true";
        }

        if (response.contains("Connection is not established")) {
            log.info("response contains connection is not established {}", response);
            return null;
        }

        if (response.contains("Undefined header")) {
            log.info("Undefined header found in response {}", response);
            return null;
        }
        final String prefix = "READY>";
        if (response.startsWith(prefix)) {
            log.info("Sanitizing response: {}", response);
            String cleaned = response.substring(prefix.length()).trim();
            if (!cleaned.toLowerCase().contains("error")) {
                log.info("Sanitized response: {}", cleaned);
                return cleaned;
            }
        }
        return null;
    }

    public void disconnect() {
        telnetConfig.disconnect();
    }
}
