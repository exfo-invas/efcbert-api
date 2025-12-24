package com.invas.enhanced.fc.bert.model.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileDetails {
    private String startTime;
    private String endTime;
    private String eventFileName;
    private String hourlyFileName;
}
