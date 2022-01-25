package com.lafresh.kiosk.creditcardlib.nccc;

public class NCCCPacketHandler {
    // Special characters for NCCC
    public static final byte STX = 0x02;
    public static final byte ETX = 0x03;
    public static final byte ACK = 0x06;
    public static final byte NAK = 0x15;
    public static final int PACKET_LENGTH = 400;
    public static final int PACKET_OVERHEAD = 3;

    public static byte[] generateToNcccData(byte[] originalData) {
        if (PACKET_LENGTH != originalData.length)
            return null;
        byte[] retData = new byte[PACKET_LENGTH + PACKET_OVERHEAD];
        int index = 0;
        retData[index++] = STX;
        System.arraycopy(originalData, 0, retData, index, PACKET_LENGTH);
        index += PACKET_LENGTH;
        retData[index++] = ETX;
        retData[index] = calculateLRC(retData);
        return retData;

    }

    public static boolean validatePacket(byte[] packetData) {
        //Check packet data structur
        if (STX == packetData[0]) {
            if (ETX != packetData[PACKET_LENGTH + 1])
                return false;
        } else {
            if (ETX != packetData[PACKET_LENGTH])
                return false;
        }
        byte LRC = 0;
        LRC = calculateLRC(packetData);
        if (STX == packetData[0]) {
            return LRC == packetData[PACKET_LENGTH + 2];
        } else {
            return LRC == packetData[PACKET_LENGTH + 1];
        }
    }

    private static byte calculateLRC(byte[] data) {
        int index = 0;
        int length = PACKET_LENGTH + 1; // Add ETX
        byte LRC = 0;
        if (STX == data[0]) {
            index++;
        }
        while (length > 0) {
            LRC ^= data[index];
            index++;
            length--;
        }
        return LRC;
    }

}
