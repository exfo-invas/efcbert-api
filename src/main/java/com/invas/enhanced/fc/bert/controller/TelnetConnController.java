package com.invas.enhanced.fc.bert.controller;

import com.invas.enhanced.fc.bert.service.TelnetConnService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/telnet")
@RequiredArgsConstructor
public class TelnetConnController {

    private final TelnetConnService telnetConnService;

    @GetMapping("/connect/{ip}")
    public String openConnection(@PathVariable String ip) {
        String telnet = telnetConnService.getConnection(ip);
        System.out.println(telnet);
        return "Connected to the server: " + ip;
    }

}
