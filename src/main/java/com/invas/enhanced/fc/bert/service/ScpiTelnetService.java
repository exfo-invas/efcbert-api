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
        if (response == null) {
            return null;
        }

        response = response.trim();

        if (response.contains("Connected") || response.contains("This operation may take few minutes to complete. Please wait for a while")) {
            return "true"; // Return as is for these specific cases
        }

        final String prefix = "READY>";
        if (!response.startsWith(prefix)) {
            return null;
        }

        // Remove "READY>" and trim remaining
        String cleaned = response.substring(prefix.length()).trim();

        // Check for "error" case-insensitively
        if (cleaned.toLowerCase().contains("error")) {
            return null; // indicates failure, ignore
        }
        log.info("Sanitized response: {}", cleaned);

        return cleaned;
    }

    public void disconnect() {
        telnetConfig.disconnect();
    }
}
