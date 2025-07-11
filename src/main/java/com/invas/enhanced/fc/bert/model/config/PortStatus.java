package com.invas.enhanced.fc.bert.model.config;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PortStatus {

    private String flowControl;
    private String bufferCredit;
    private String loging;
}
