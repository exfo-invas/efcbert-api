package com.invas.enhanced.fc.bert.model.physical;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhysicalStatus {

    private String laserStatus;
    private String fcRate;
    private String sfpnPort;
    private String linkStatus;
    private String txPower;
    private String rxPower;

}
