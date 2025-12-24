package com.invas.enhanced.fc.bert.service;

import com.invas.enhanced.fc.bert.config.TelnetConfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Objects;

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
            return sanitizeResponse(response.toString(), command);
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
    private String sanitizeResponse(String response, String command) {
        if (response == null || response.isBlank()) {
            return null;
        }

        if (response.contains("Connected")) {
            log.info("response contains connected {}", response);

            if (response.lines().count() == 1) {
                return "true";
            }
        }

        if (response.contains("Connection is not established")) {
            log.info("response contains connection is not established {}", response);
            return null;
        }

        if (Objects.equals(command, "LINS1:SOUR:DATA:TEL:TEST OFF")
                || Objects.equals(command, "LINS1:SOUR:DATA:TEL:TEST ON")) {
            log.info("response for {}: {}", command, response);
            if (response.contains("This operation may take few minutes to complete.")
                    || response.contains("Please wait for a while.")) {
                return "true";
            }
        }

        // ✅ ALWAYS extract READY> value first (multi-line safe)
        if (response.contains("READY>")) {
            String value = response.lines()
                    .filter(line -> line.startsWith("READY>"))
                    .map(line -> line.substring("READY>".length()).trim())
                    .findFirst()
                    .orElse(null);

            if (value != null && !value.isBlank()) {
                log.info("Extracted READY> value: {}", value);
                if (value.contains("error") || value.contains("ERROR")) {
                    log.info("Response contains error, returning null");
                    return null;
                }
                return value;
            }
        }

        if (response.contains("Undefined header")) {
            log.info("Undefined header found in response {}", response);
            return null;
        }

        return null;
    }

    private String retrySendCommand(String command) {
        int attempt = 0;
        int maxRetries = 2;
        while (attempt < maxRetries) {
            String response = sendCommand(command);
            if (response != null) {
                return response;
            }
            attempt++;
            log.info("Retrying command: {} (Attempt {}/{})", command, attempt, maxRetries);
            try {
                Thread.sleep((long) 500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Retry interrupted: {}", e.getMessage(), e);
                break;
            }
        }
        log.error("Max retries reached for command: {}", command);
        return null;
    }

    private String cleanUpMultiLineResponse(String response) {

        StringBuilder cleanedResponse = new StringBuilder();
        String prefix = "READY>";
        for (String line : response.split("\n")) {
            if (line.startsWith(prefix)) {
                String cleanedLine = line.substring(prefix.length()).trim();
                cleanedResponse.append(cleanedLine).append("\n");
            } else {
                cleanedResponse.append(line).append("\n");
            }
        }
        return cleanedResponse.toString().trim();
    }

    public void resetConnection() {
        telnetConfig.disconnect();
    }

    public void disconnect() {
        telnetConfig.disconnect();
    }
}
