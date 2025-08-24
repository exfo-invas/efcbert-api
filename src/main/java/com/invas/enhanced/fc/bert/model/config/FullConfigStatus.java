package com.invas.enhanced.fc.bert.model.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FullConfigStatus {

    private PhysicalStatus physicalStatus;
    private PortStatus portStatus;
    private ToolStatus toolStatus;
    private String pspLinkStatus;

}
