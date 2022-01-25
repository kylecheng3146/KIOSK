package com.lafresh.kiosk.creditcardlib.model;

import java.util.HashMap;
import java.util.Map;

public class Message {
    private static final String SUCCESS = "SUCCESS";
    private static final String LOCKED = "Can not acquire the lock";
    private static final String NAK_RES = "NAK response";
    private static final String INVALID_REQ = "Invalid request";
    private static final String IO_ERROR = "IOError";
    private static final String INTERRUPTED = "Interrupted";
    private static final String TIMEOUT = "Response timeout";
    private static final String INVALID_LENGTH = "Invalid packet length";
    private static final String NO_LRC = "Not LRC found";
    private static final String WRONG_DATA = "invalid packet";

    private static Map<Integer, String> messageMap;

    static {
        messageMap = new HashMap<>();
        messageMap.put(Code.SUCCESS, SUCCESS);
        messageMap.put(Code.LOCKED, LOCKED);
        messageMap.put(Code.NAK_RES, NAK_RES);
        messageMap.put(Code.INVALID_REQ, INVALID_REQ);
        messageMap.put(Code.IO_ERROR, IO_ERROR);
        messageMap.put(Code.INTERRUPTED, INTERRUPTED);
        messageMap.put(Code.TIMEOUT, TIMEOUT);
        messageMap.put(Code.INVALID_LENGTH, INVALID_LENGTH);
        messageMap.put(Code.NO_LRC, NO_LRC);
        messageMap.put(Code.WRONG_DATA, WRONG_DATA);
    }

    public static Map<Integer, String> getMessageMap() {
        return messageMap;
    }

    public static String getMessage(int code) {
        return messageMap.get(code);
    }
}
