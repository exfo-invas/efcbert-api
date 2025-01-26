package com.invas.enhanced.fc.bert.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConnectionResponse {
    private boolean status;
    private String address;
    private String message;
}

