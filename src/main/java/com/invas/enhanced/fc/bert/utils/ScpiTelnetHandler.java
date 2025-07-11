package com.invas.enhanced.fc.bert.utils;

import com.invas.enhanced.fc.bert.config.TelnetConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.io.*;

@Slf4j
@Service
@Configuration
@RequiredArgsConstructor
public class ScpiTelnetHandler {

    private final TelnetConfig telnetConfig;

    private PrintWriter writer;
    /*private Scanner reader;*/
    private BufferedReader reader;

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

    public void disconnect() {
        telnetConfig.disconnect();
    }
}
