package com.invas.enhanced.fc.bert.utils;

import com.invas.enhanced.fc.bert.config.TelnetConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

@Service
@RequiredArgsConstructor
public class TelnetConfigUtil {

    private final TelnetConfig telnetConfig;

    private PrintWriter writer;
    private Scanner reader;
    private static final int RESPONSE_TIMEOUT = 10000;

    public boolean getConnection(String localIpaddress, int port) {

        if (telnetConfig.getConnection(localIpaddress, port)) {
            writer = new PrintWriter(telnetConfig.getOutputStream(), true);
            reader = new Scanner(telnetConfig.getInputStream());
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
        if (getStatus()) {
            try {

                writer.println(command);
                writer.flush();
                Thread.sleep(500);

                long startTime = System.currentTimeMillis();
                StringBuilder response = new StringBuilder();
                while (reader.hasNextLine() && (System.currentTimeMillis() - startTime <= RESPONSE_TIMEOUT)) {
                    response.append(reader.nextLine()).append("\n");
                }
                return response.toString();
            } catch (Exception e) {
                return "Failed to send command";
            }
        } else {
            return "Connection is not established";
        }
    }

    public void disconnect() {
        telnetConfig.disconnect();
    }
}
