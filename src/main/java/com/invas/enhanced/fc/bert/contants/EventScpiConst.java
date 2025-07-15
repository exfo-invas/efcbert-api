package com.invas.enhanced.fc.bert.contants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventScpiConst {

    private static final String LINS_SOURCE = "LINS1:SOUR:DATA:TEL:";
    private static final String LINS_FETCH = "LINS1:FETC:DATA:TEL:";
    private static final String FIBER_STRING = "FIB:STR:";

    private static final String LOGGING_LIST = "LINS1:FETC:DATA:TEL:LOGG:LIST?"; //returns CSV file with all events

    public static String lineUtilization(String type) {
        String line = "LINE:UTIL? ";
        String command = switch (type.toUpperCase()) {
            case "TX" -> LINS_FETCH + FIBER_STRING + line + " TX";
            case "RX" -> LINS_FETCH + FIBER_STRING + line + " RX";
            default -> "";
        };
        log.info("Line Utilization CONTROL: {}", command);
        return command;
    }

    public static String frameRate(String type) {
        String line = "FRAM:RATE? ";
        String command = switch (type.toUpperCase()) {
            case "TX" -> LINS_FETCH + FIBER_STRING + line + " TX";
            case "RX" -> LINS_FETCH + FIBER_STRING + line + " RX";
            default -> "";
        };
        log.info("Frame Rate CONTROL: {}", command);
        return command;
    }

    public static String byteCount(String type) {
        String line = "BYTE:COUN? ";
        String command = switch (type.toUpperCase()) {
            case "TX" -> LINS_FETCH + FIBER_STRING + line + " TX";
            case "RX" -> LINS_FETCH + FIBER_STRING + line + " RX";
            default -> "";
        };
        log.info("Byte Count CONTROL: {}", command);
        return command;
    }

    public static String frameCount(String type) {
        String line = "FRAM:COUN? ";
        String command = switch (type.toUpperCase()) {
            case "TX" -> LINS_FETCH + FIBER_STRING + line + " TX";
            case "RX" -> LINS_FETCH + FIBER_STRING + line + " RX";
            default -> "";
        };
        log.info("Frame Count CONTROL: {}", command);
        return command;
    }

    public static String frameSize() {
        String command = LINS_FETCH + FIBER_STRING + "SIZE?";
        log.info("Frame Size CONTROL: {}", command);
        return command;
    }

    public static String fcRate() {
        String command = LINS_SOURCE + "ITYP?";
        log.info("Controller Command: {}", command);
        return command;
    }

}


