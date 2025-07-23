package com.invas.enhanced.fc.bert.service.config;

import com.invas.enhanced.fc.bert.model.config.PhysicalStatus;
import com.invas.enhanced.fc.bert.model.config.PortStatus;
import com.invas.enhanced.fc.bert.model.config.ToolStatus;

public interface ConfigService {


    PhysicalStatus getPhysicalStatus();

    String laserControl(boolean toggle);

    PortStatus getPortStatus();

    ToolStatus getToolStatus();

    String getPSPLinkStatus();

    boolean testControl(boolean toggle);

    String testReset();

    String testTime();

    String togglePSPLink(boolean toggle);

    String getPSPLink();
}
