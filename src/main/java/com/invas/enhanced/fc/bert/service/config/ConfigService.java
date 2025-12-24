package com.invas.enhanced.fc.bert.service.config;

import com.invas.enhanced.fc.bert.model.config.FileDetails;
import com.invas.enhanced.fc.bert.model.config.FullConfigStatus;

public interface ConfigService {

    String laserControl(boolean toggle);

    boolean testControl(boolean toggle);

    String testReset();

    String testTime();

    String togglePSPLink(boolean toggle);

    String getPSPLink();

    FullConfigStatus getFullConfigStatus();

    FileDetails getFileRecords();
}
