package com.invas.enhanced.fc.bert.controller.telnet;

import com.invas.enhanced.fc.bert.model.telnet.ConnectionResponse;
import com.invas.enhanced.fc.bert.model.telnet.IPAddress;
import com.invas.enhanced.fc.bert.service.telnet.TelnetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/telnet")
@RequiredArgsConstructor
public class TelnetController {

    private final TelnetService telnetService;

    @GetMapping("/ishealthy")
    public ResponseEntity<Map<String, String>> getHealthy() {
        log.info("Is Healthy");
        return ResponseEntity.ok().body(Map.of("status", "UP"));
    }

    @GetMapping("/connect/{ip}")
    public ConnectionResponse openConnection(@PathVariable String ip) {
        ConnectionResponse telnet = telnetService.getConnection(ip);
        log.info("TelnetController get connection {}", telnet);
        return telnet;
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
