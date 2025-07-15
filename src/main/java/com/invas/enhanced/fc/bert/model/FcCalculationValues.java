package com.invas.enhanced.fc.bert.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FcCalculationValues {

    private double actualThroughput;
    private double ActualTransferRate;
    private double lineSpeed;
}
