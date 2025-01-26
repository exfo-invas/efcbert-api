package com.invas.enhanced.fc.bert.utils;

import com.invas.enhanced.fc.bert.config.TelnetConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Service
@RequiredArgsConstructor
public class TelnetConfigUtils {

    private final TelnetConfig telnetConfig;

    public String getConnection(String localIpaddress, int port) {
        return telnetConfig.getConnection(localIpaddress, port);
    }

    public boolean getStatus() {
        return telnetConfig.getStatus();
    }

    public String getAddress() {
        return telnetConfig.getAddress();
    }

    public InputStream getInputStream() {
        return telnetConfig.getInputStream();
    }

    public OutputStream getOutputStream() {
        return telnetConfig.getOutputStream();
    }

    public String readAndWriteData(String command) throws IOException {
        if (getStatus()) {
            InputStream inputStream = getInputStream();
            OutputStream outputStream = getOutputStream();

            // Send command to the output stream
            outputStream.write((command + "\n").getBytes());
            outputStream.flush();

            // Read response from the input stream
            byte[] buffer = new byte[1024];
            int bytesRead;
            StringBuilder response = new StringBuilder();


            if ((bytesRead = inputStream.read(buffer)) != -1) {
                response.append(new String(buffer, 0, bytesRead));
            }

            /*while ((bytesRead = inputStream.read(buffer)) != -1) {
                response.append(new String(buffer, 0, bytesRead));
                if (bytesRead < buffer.length) {
                    break;
                }
            }*/
            return response.toString();
        } else {
            return "Connection is not established";
        }
    }

    public void disconnect() {
        telnetConfig.disconnect();
    }
}
