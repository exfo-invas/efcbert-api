package com.invas.enhanced.fc.bert.controller.config;

import com.invas.enhanced.fc.bert.model.config.FullConfigStatus;
import com.invas.enhanced.fc.bert.service.config.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;

    @GetMapping("/status/full")
    public FullConfigStatus getStatus() {
        return configService.getFullConfigStatus();
    }

    @GetMapping("/laser/{toggle}")
    public String getLaser(@PathVariable boolean toggle) {
        return configService.laserControl(toggle);
    }

    @GetMapping("/test/{toggle}")
    public boolean toogleTest(@PathVariable boolean toggle) {
         /*return configService.testControl(toggle);*/
        return true;
    }

    @GetMapping("/test/reset")
    public String testReset() {
        return configService.testReset();
    }

    @GetMapping("/test/time")
    public String testTime() {
        return configService.testTime();
    }

    @GetMapping("/psp/{toggle}")
    public String getPSPLinkStatus(@PathVariable boolean toggle) {
        return configService.togglePSPLink(toggle);
    }

    @GetMapping("/psp/link")
    public String getPSPLink() {
        return configService.getPSPLink();
    }
}
