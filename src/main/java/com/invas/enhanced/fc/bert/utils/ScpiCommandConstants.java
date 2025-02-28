package com.invas.enhanced.fc.bert.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ScpiCommandConstants {

    private static final String LINS_SOURCE = "LINS1:SOUR:DATA:TEL:";

    private static final String LINS_SENS = "LINS1:SENS:DATA:TEL:";

    private static final String LINS_FETCH = "LINS1:FETC:DATA:TEL:";

    public static String laserCntrl(String type) {
        String command = "";
        if (type.equalsIgnoreCase("ON")) {
            command = "LINS1:OUTP:TEL:LAS ON";
        } else if (type.equalsIgnoreCase("OFF")) {
            command = "LINS1:OUTP:TEL:LAS OFF";
        } else if (type.equalsIgnoreCase("STAT")) {
            command = "LINS1:OUTP:TEL:LAS?";
        }
        log.info("LASER CONTROL: {}", command);
        return command;
    }

    public static String frequenceInfo(String type) {
        String command = "";
        if (type.equalsIgnoreCase("WHICH")) {
            command = LINS_SENS + "OPT:PORT:FREQ?";
        } else if(type.equalsIgnoreCase("NEGATIVE")) {
            command = LINS_SENS + "OPT:PORT:FREQ:NEG?";
        } else if(type.equalsIgnoreCase("POSITIVE")) {
            command = LINS_SENS + "OPT:PORT:FREQ:POS?";
        } else if(type.equalsIgnoreCase("OFFSET")) {
            command = LINS_SENS + "OPT:PORT:FREQ:OFFS:VAL?";
        }
        log.info("Frequence Information : {}", command);
        return command;
    }

    public static String serviceDesc(String type) {
        String command = "";
        if (type.equalsIgnoreCase("LONG")) {
            command = LINS_FETCH + "SDT:LONG?";
        } else if(type.equalsIgnoreCase("LAST")) {
            command = LINS_FETCH + "SDT:LAST?";
        } else if(type.equalsIgnoreCase("SHORT")) {
            command = LINS_FETCH + "SDT:SHOR?";
        } else if(type.equalsIgnoreCase("AVERAGE")) {
            command = LINS_FETCH + "SDT:AVER?";
        } else if(type.equalsIgnoreCase("TOTAL")) {
            command = LINS_FETCH + "SDT:TOT?";
        } else if(type.equalsIgnoreCase("COUNT")) {
            command = LINS_FETCH + "SDT:COUN?";
        }
        log.info("Service Description: {}", command);
        return command;
    }

    public static String controller(String type) {
        String command = "";
        if (type.equalsIgnoreCase("OPEN")) {
            command = LINS_SOURCE + "TEST:TYPE FCBERT";
        } else if(type.equalsIgnoreCase("CLOSE")) {
            command = LINS_SOURCE + "TEST CLOSE";
        } else if (type.equalsIgnoreCase("START")) {
            command = LINS_SOURCE + "TEST ON";
        } else if(type.equalsIgnoreCase("STOP")) {
            command = LINS_SOURCE + "TEST OFF";
        } else if(type.equalsIgnoreCase("RESET")) {
            command = LINS_SOURCE + "RES";
        }
        log.info("Controller Command: {}", command);
        return command;
    }

    public static String consoleOuput(String type) {
        String command = "";
        if (type.equalsIgnoreCase("TX")) {
            command = LINS_SENS + "OPT:PORT:TX:POW?";
        } else if(type.equalsIgnoreCase("RX")) {
            command = LINS_SENS + "OPT:PORT:RX:POW?";
        } else if(type.equalsIgnoreCase("RX-MIN")) {
            command = LINS_SENS + "OPT:PORT:RX:POW:MIN?";
        } else if(type.equalsIgnoreCase("RX-MAX")) {
            command = LINS_SENS + "OPT:PORT:RX:POW:MAX?";
        } else if (type.equalsIgnoreCase("RX-RANGE")) {
            command = LINS_SENS + "OPT:PORT:RX:POW:RANG?";
        } else if (type.equalsIgnoreCase("WAVE-LENGTH")) {
            command = LINS_SENS + "OPT:PORT:WAV?";
        } else if (type.equalsIgnoreCase("PATTERN")) {
            command = LINS_SENS + "OPT:PORT:PATT?";
        } else if(type.equalsIgnoreCase("STREAM-RATE")) {
            command = LINS_SENS + "FIB:STR:RATE?";
        }
        log.info("Console commands: {}", command);
        return command;
    }

    public static String topology(String type) {
        String command = "";
        if (type.equalsIgnoreCase("COUPLED")) {
            command = LINS_SOURCE + "TOP COUPLED";
        } else if (type.equalsIgnoreCase("STATUS")) {
            command= LINS_SOURCE + "TOP?";
        }
        log.info("Topology Command: {}", command);
        return command;
    }

    public static String interfaceType(String type) {
        String command = "";
        if (type.equalsIgnoreCase("SET")) {
            command = LINS_SOURCE + "ITYP FC8X";
        } else if (type.equalsIgnoreCase("VALUE")) {
            command = LINS_SOURCE + "ITYP?";
        }
        log.info("Interface Type commands: {}", command);
        return command;
    }

    public static String physicalPort(String type) {
        String command = "";
        if (type.equalsIgnoreCase("SET")) {
            command = LINS_SOURCE + "ETH:PORT:TRAN SFP28";
        } else if(type.equalsIgnoreCase("STATUS")) {
            command = LINS_SOURCE + "ETH:PORT:TRAN?";
        }
        log.info("Physical Port commands: {}", command);
        return command;
    }

    public static String logging(String type) {
        String command = "";
        if (type.equalsIgnoreCase("LOG-LIST")) {
            command = LINS_FETCH + "LOGG:LIST?";
        } else if (type.equalsIgnoreCase("TEST-TIME")) {
            command = LINS_FETCH + "TEST:STAR:TIME?";
        } else if (type.equalsIgnoreCase("STATUS")) {
            command = LINS_FETCH + "TEST:STAT?";
        } else if (type.equalsIgnoreCase("VERDICT")) {
            command = LINS_FETCH + "STAT:VERD?";
        }
        log.info("Logging commands: {}", command);
        return command;
    }
}
