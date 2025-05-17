package com.invas.enhanced.fc.bert.model.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PhysicalStatus {

    private String laserStatus;
    private String fcRate;
    private String sfpnPort;
    private String linkStatus;
    private String txPower;
    private String rxPower;

}
