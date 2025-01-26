package com.invas.enhanced.fc.bert.controller;

import com.invas.enhanced.fc.bert.model.ConnectionResponse;
import com.invas.enhanced.fc.bert.service.TelnetService;
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

    @GetMapping("/status")
    public String status() {
        return telnetService.status();
    }

    @PostMapping("/send")
    public String sendCommand(@RequestBody String command) {
        return telnetService.sendCommand(command);
    }

    @GetMapping("/disconnect")
    public String closeConnection() {
        return telnetService.disconnect();
    }
}
