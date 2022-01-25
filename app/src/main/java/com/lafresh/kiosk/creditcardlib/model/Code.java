package com.lafresh.kiosk.creditcardlib.model;

public class Code {
    public static final int SUCCESS = 0x00; // 0x00: SUCCESS
    public static final int LOCKED = 0x01; // 0x01: Can not acquire the lock
    public static final int NAK_RES = 0x02; // 0x02: NAK response
    public static final int INVALID_REQ = 0x03; // 0x03: Invalid request
    public static final int IO_ERROR = 0x10; // 0x10: IOError
    public static final int INTERRUPTED = 0x11; // 0x11: Interrupted
    public static final int TIMEOUT = 0x21; // 0x21: Response timeout
    public static final int INVALID_LENGTH = 0x22; // 0x22: Invalid packet length
    public static final int NO_LRC = 0x23; // 0x23: Not LRC found
    public static final int WRONG_DATA = 0x24; // 0x23: invalid packet
}
