package com.invas.enhanced.fc.bert.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DecimalHandlerUtil {

    public static BigDecimal ifNullReturnZero(String value) {
        if (value == null) {
            return BigDecimal.ZERO;
        } else {
            return roundToTwoDecimalPlaces(new BigDecimal(value),2);
        }
    }

    public static BigDecimal defaultIfNullReturnZero(String value) {
        if (value == null) {
            return BigDecimal.ZERO;
        } else {
            return roundToTwoDecimalPlaces(new BigDecimal(value), 4);
        }
    }

    private static BigDecimal roundToTwoDecimalPlaces(BigDecimal value, int scale) {
        return value.setScale(scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal valuePercentage(BigDecimal value, BigDecimal percentage) {
        return value.multiply(percentage).divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
    }

    public static BigDecimal percentageOfBigDecimal(BigDecimal part, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        // new calculation: part * (total / 100)
        return part
                .divide(total, 10, RoundingMode.HALF_UP)   // part / total
                .multiply(BigDecimal.valueOf(100))         // * 100
                .setScale(2, RoundingMode.HALF_UP);        // final formatting to 2 decimal places
    }
}
