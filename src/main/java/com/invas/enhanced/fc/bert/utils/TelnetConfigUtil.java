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

        String condition1 = "this operation may take few minutes";
        String condition2 = "Trying to connect";

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
            String line;
            log.info("************COMMAND BEGIN**************");
            do {
                line = reader.nextLine();
                log.info("Reader : {}", line);
                response.append(line).append("\n");
                log.info("TelnetConfigUtil response next line: {}", reader.hasNextLine());
                if (line.contains("Ready")) {
                    log.info("TelnetConfigUtil ready final line: {}", line);
                    break;
                }
            } while (line.toLowerCase().contains(condition1.toLowerCase()) || line.toLowerCase().contains(condition2.toLowerCase()));

            log.info("************COMMAND END**************");
        } catch (Exception e) {
            log.error("TelnetConfigUtil {}", e.getMessage(), e);
            return "Failed to send command";
        }
        log.info("TelnetConfigUtil sendCommand response: {}", response);
        return response.toString();
    }

    public void disconnect() {
        telnetConfig.disconnect();
    }
}
