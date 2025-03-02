package com.invas.enhanced.fc.bert.controller.physical;

import com.invas.enhanced.fc.bert.model.physical.PhysicalStatus;
import com.invas.enhanced.fc.bert.service.physical.PhysicalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/physical")
@RequiredArgsConstructor
public class PhysicalController {

    private final PhysicalService physicalService;

    @GetMapping("/status")
    public PhysicalStatus getStatus() {
        return physicalService.getStatus();
    }


}
