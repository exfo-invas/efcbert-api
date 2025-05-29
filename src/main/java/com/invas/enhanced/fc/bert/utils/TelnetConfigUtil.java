package com.invas.enhanced.fc.bert.utils;

import com.invas.enhanced.fc.bert.config.TelnetConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.util.Scanner;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelnetConfigUtil {

    private final TelnetConfig telnetConfig;

    private PrintWriter writer;
    private Scanner reader;

    public void getConnection(String localIpaddress, int port) {

        if (telnetConfig.getConnection(localIpaddress, port)) {
            writer = new PrintWriter(telnetConfig.getOutputStream(), true);
            reader = new Scanner(telnetConfig.getInputStream());
            log.info("TelnetConfigUtil Connected to {}:{}", localIpaddress, port);
            log.info("TelnetConfigUtil writer: {} \n reader: {}", writer.checkError(), reader);
        }
    }

    public boolean getStatus() {
        return telnetConfig.getStatus();
    }

    public String getAddress() {
        return telnetConfig.getAddress();
    }

    public String sendCommand(String command) {

        String ready = "ready";

        log.info("TelnetConfigUtil sendCommand {}", command);
        if (!getStatus()) {
            return "Connection is not established";
        }

        StringBuilder response = new StringBuilder();
        try {
            writer.println(command);
            writer.flush();
            Thread.sleep(500);
            log.info("Writer flush check error {}", writer.checkError());

            log.info("************COMMAND BEGIN**************");
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                if (line == null || line.isBlank()) {
                    continue;
                }
                log.info("Reader : {}", line);
                response.append(line).append("\n");
                if (line.toLowerCase().contains(ready.toLowerCase())) {
                    log.info("TelnetConfigUtil has ready -> final line: {}", line);
                    break;
                }
            }
            log.info("************COMMAND END**************");
        } catch (Exception e) {
            log.error("TelnetConfigUtil Error{}", e.getMessage(), e.getCause());
            return "Failed to send command";
        }
        log.info("TelnetConfigUtil sendCommand response: {}", response);
        return response.toString();
    }

    public void disconnect() {
        telnetConfig.disconnect();
    }
}
