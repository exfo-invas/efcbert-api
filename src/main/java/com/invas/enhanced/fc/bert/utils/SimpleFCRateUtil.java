package com.invas.enhanced.fc.bert.utils;

import com.invas.enhanced.fc.bert.model.FcCalculationValues;
import java.util.Map;

public class SimpleFCRateUtil {

    private static final Map<String, String> fcRates = Map.of(
        "1x", "850,100,1.0625",
        "2x", "1700,200,2.125",
        "4x", "3400,400,4.25",
        "8x", "6800,800,8.5",
        "10x", "10200,1200,12.75",
        "16x", "13600,1600,14.025",
        "32x", "27200,3200,28.05",
        "64x", "54400,6400,0"
    );

    public static FcCalculationValues getLineUtilizationCommand(String fcRate) {
        String fcRateValues = fcRates.get(fcRate);
        if (fcRateValues != null) {
            String[] valuesStr = fcRateValues.split(",");
            double[] values = new double[valuesStr.length];
            for (int i = 0; i < valuesStr.length; i++) {
                values[i] = Double.parseDouble(valuesStr[i]);
            }
            return new FcCalculationValues(values[0], values[1], values[2]);
        }
        return null;
    }
}