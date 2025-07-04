package com.invas.enhanced.fc.bert.utils;

import com.invas.enhanced.fc.bert.config.TelnetConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScpiTelnetHandler {

    private final TelnetConfig telnetConfig;

    /**
     * Sends a command to the telnet server and returns the response.
     *
     * @param command The command to send.
     * @return The response from the server.
     */
    public String sendCommand(String command) {
        if (!telnetConfig.getStatus()) {
            return "Connection is not established";
        }
        try (
                PrintWriter writer = new PrintWriter(telnetConfig.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(telnetConfig.getInputStream()))
        ) {
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
            return response.toString();
        } catch (Exception e) {
            log.error("Error during command execution: {}", e.getMessage(), e);
            return "Failed to send command";
        }
    }
}
