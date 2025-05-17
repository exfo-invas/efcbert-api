package com.invas.enhanced.fc.bert.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScpiCommandConstants {


    private static final String LINS_SOURCE = "LINS1:SOUR:DATA:TEL:";
    private static final String LINS_SENS = "LINS1:SENS:DATA:TEL:";
    private static final String LINS_FETCH = "LINS1:FETC:DATA:TEL:";
    private static final String LASER_CONTROL_PREFIX = "LINS1:OUTP:TEL:LAS";
    private static final String OPT_PORT_FREQ = "OPT:PORT:FREQ";
    private static final String SDT = "SDT:";
    private static final String TEST = "TEST";
    private static final String OPT_PORT = "OPT:PORT:";
    private static final String ETH_PORT_TRAN = "ETH:PORT:TRAN";
    private static final String FIBER_PORT = "FIB:PORT:";

    public ScpiCommandConstants() {
    }

    public static String laserCntrl(String type) {
        String command = switch (type.toUpperCase()) {
            case "ON" -> LASER_CONTROL_PREFIX + " ON";
            case "OFF" -> LASER_CONTROL_PREFIX + " OFF";
            case "STAT" -> LASER_CONTROL_PREFIX + "?";
            default -> "";
        };
        log.info("LASER CONTROL: {}", command);
        return command;
    }

    public static String frequenceInfo(String type) {
        String command = switch (type.toUpperCase()) {
            case "WHICH" -> LINS_SENS + OPT_PORT_FREQ + "?";
            case "NEGATIVE" -> LINS_SENS + OPT_PORT_FREQ + ":NEG?";
            case "POSITIVE" -> LINS_SENS + OPT_PORT_FREQ + ":POS?";
            case "OFFSET" -> LINS_SENS + OPT_PORT_FREQ + ":OFFS:VAL?";
            default -> "";
        };
        log.info("Frequence Information : {}", command);
        return command;
    }

    public static String serviceDesc(String type) {
        String command = switch (type.toUpperCase()) {
            case "LONG" -> LINS_FETCH + SDT + "LONG?";
            case "LAST" -> LINS_FETCH + SDT + "LAST?";
            case "SHORT" -> LINS_FETCH + SDT + "SHOR?";
            case "AVERAGE" -> LINS_FETCH + SDT + "AVER?";
            case "TOTAL" -> LINS_FETCH + SDT + "TOT?";
            case "COUNT" -> LINS_FETCH + SDT + "COUN?";
            default -> "";
        };
        log.info("Service Description: {}", command);
        return command;
    }

    public static String controller(String type) {
        String command = switch (type.toUpperCase()) {
            //case "OPEN" -> LINS_SOURCE + TEST + " TYPE FCBERT";
            case "CLOSE" -> LINS_SOURCE + TEST + " CLOSE";
            case "START" -> LINS_SOURCE + TEST + " ON";
            case "STOP" -> LINS_SOURCE + TEST + " OFF";
            case "RESET" -> LINS_SOURCE + "RES";
            default -> "";
        };
        log.info("Controller Command: {}", command);
        return command;
    }

    public static String consoleOuput(String type) {
        String command = switch (type.toUpperCase()) {
            case "TX" -> LINS_SENS + OPT_PORT + "TX:POW?";
            case "RX" -> LINS_SENS + OPT_PORT + "RX:POW?";
            case "RX-MIN" -> LINS_SENS + OPT_PORT + "RX:POW:MIN?";
            case "RX-MAX" -> LINS_SENS + OPT_PORT + "RX:POW:MAX?";
            case "RX-RANGE" -> LINS_SENS + OPT_PORT + "RX:POW:RANG?";
            case "WAVE-LENGTH" -> LINS_SENS + OPT_PORT + "WAV?";
            case "PATTERN" -> LINS_SENS + OPT_PORT + "PATT?";
            case "STREAM-RATE" -> LINS_SENS + "FIB:STR:RATE?";
            default -> "";
        };
        log.info("Console commands: {}", command);
        return command;
    }

    public static String topology(String type) {
        String command = switch (type.toUpperCase()) {
            case "COUPLED" -> LINS_SOURCE + "TOP COUPLED";
            case "STATUS" -> LINS_SOURCE + "TOP?";
            default -> "";
        };
        log.info("Topology Command: {}", command);
        return command;
    }

    public static String toolStatus(String type) {
        String command = switch (type.toUpperCase()) {
            case "SOURCE" -> LINS_SOURCE + FIBER_PORT + " WSO"; //Add ? or SOURCE input
            case "DESTINATION" -> LINS_SOURCE + FIBER_PORT + " WDES";//Add ? or DESTINATION input
            case "FLOW-CONTROL" -> LINS_SOURCE + FIBER_PORT + "FCON:ENAB";//Add ? or ON or OFF
            case "CREDIT" -> LINS_SOURCE + FIBER_PORT + "AVA:BBCR";//Add ? or value
            case "LOGGING" -> LINS_SOURCE + FIBER_PORT + "LOG:STAT";//Add ? or ON or OFF
            case "TOPOLOGY" -> LINS_SOURCE + FIBER_PORT + "DTOP?";
            case "FABRIC-STATUS" -> LINS_SOURCE + FIBER_PORT + "FLOG:STAT?";
            case "PORT-STATUS" -> LINS_SOURCE + FIBER_PORT + "PLOG:STAT?";
            default -> "";
        };
        log.info("Physical command: {}", command);
        return command;
    }

    public static String interfaceType(String type) {
        String command = switch (type.toUpperCase()) {
            case "SET" -> LINS_SOURCE + "ITYP FC8X";
            case "VALUE" -> LINS_SOURCE + "ITYP?";
            default -> "";
        };
        log.info("Interface Type commands: {}", command);
        return command;
    }

    public static String physicalPort(String type) {
        String command = switch (type.toUpperCase()) {
            case "SET" -> LINS_SOURCE + ETH_PORT_TRAN + " SFP28";
            case "STATUS" -> LINS_SOURCE + ETH_PORT_TRAN + "?";
            default -> "";
        };
        log.info("Physical Port commands: {}", command);
        return command;
    }

    public static String fcbertConfiguration(String type) {
        String command = switch (type.toUpperCase()) {
            case "COUPLED" -> LINS_SENS + "COUP"; //Add ? or ON or OFF
            case "PATTERN" -> LINS_SOURCE + "PATT:TYPE?";
            case "FRAME-SIZE" -> LINS_SOURCE + "FIB:STR:SIZE";//Add ? or value
            case "STREAM-RATE" -> LINS_SOURCE + "FIB:STR:RATE";//Add ? or value
            default -> "";
        };
        log.info("FCBERT Configuration commands: {}", command);
        return command;
    }

    public static String logging(String type) {
        String command = switch (type.toUpperCase()) {
            case "TIME" -> LINS_FETCH + TEST + ":TIME?";
            case "LOG-LIST" -> LINS_FETCH + "LOGG:LIST?";
            case "LINK-STATUS" -> LINS_FETCH + "FIB:PORT:PLOG:STAT?";
            case "TEST-TIME" -> LINS_FETCH + TEST + ":STAR:TIME?";
            case "STATUS" -> LINS_FETCH + TEST + ":STAT?";
            case "VERDICT" -> LINS_FETCH + "STAT:VERD?";
            default -> "";
        };
        log.info("Logging commands: {}", command);
        return command;
    }

    public static String pspLink(String type) {
        String FIBER = "FIB:";
        String command = switch (type.toUpperCase()) {
            case "LINK" -> LINS_FETCH + FIBER + ":LINK?";
            case "ENABLE" -> LINS_SOURCE + FIBER + "PSP ON";
            case "DISABLE" -> LINS_SOURCE + FIBER + "PSP OFF";
            case "STATUS" -> LINS_SOURCE + FIBER + "PSP?";
            default -> "";
        };
        log.info("PSP Link commands: {}", command);
        return command;
    }
}