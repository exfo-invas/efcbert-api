package com.invas.enhanced.fc.bert.controller.telnet;

import com.invas.enhanced.fc.bert.model.telnet.ConnectionResponse;
import com.invas.enhanced.fc.bert.model.telnet.IPAddress;
import com.invas.enhanced.fc.bert.service.telnet.TelnetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/telnet")
@RequiredArgsConstructor
public class TelnetController {

    private final TelnetService telnetService;

    @GetMapping("/connect/{ip}/{port}")
    public ResponseEntity<ConnectionResponse> openConnection(@PathVariable String ip, @PathVariable int port) {
        ConnectionResponse telnet = telnetService.getConnection(ip, port);
        System.out.println(telnet);
        return ResponseEntity.ok(telnet);
    }

    @GetMapping("/ip")
    public IPAddress getIpAddress() {
        return telnetService.getIPAddress();
    }

    @GetMapping("/status")
    public String status() {
        return telnetService.status();
    }

    @GetMapping("/disconnect")
    public String closeConnection() {
        return telnetService.disconnect();
    }
}
