package com.lafresh.kiosk.creditcardlib.nccc;

import android.serialport.SerialPort;
import android.util.Log;

import com.lafresh.kiosk.creditcardlib.Session;
import com.lafresh.kiosk.creditcardlib.model.Code;
import com.lafresh.kiosk.creditcardlib.model.Request;
import com.lafresh.kiosk.creditcardlib.model.Response;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;


public class NCCCSession implements Session {
    private static final ReentrantLock reLock = new ReentrantLock();
    private static final int ACK_TIMEOUT = 5 * 1000;
    private int EDC_TIMEOUT = 60 * 1000;
    private static long nextAvailableTime = 0;

    private SerialPort serialPort;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;
    private byte[] toNcccData;
    private byte[] fromNcccData;
    private String devPath;
    private int devBaudRate;
    private long startTime;
    private int dataStartIndex;
    private int dataEndIndex;
    private int dataIndex;
    private byte[] dataBuffer;

    private boolean init(Request request) {
        boolean isValidData = setToNcccData(request.getData().getBytes());
        devPath = request.getPath();
        devBaudRate = request.getBaudRate();
        serialPort = null;
        mFileInputStream = null;
        mFileOutputStream = null;
        dataStartIndex = 0;
        dataEndIndex = -1;
        dataIndex = -1;
        dataBuffer = new byte[4096];
        fromNcccData = new byte[NCCCPacketHandler.PACKET_LENGTH];
        startTime = System.currentTimeMillis();
        return isValidData;
    }

    private boolean setToNcccData(byte[] Data) {
        if (NCCCPacketHandler.PACKET_LENGTH != Data.length) {
            return false;
        }
        toNcccData = NCCCPacketHandler.generateToNcccData(Data);
        return true;
    }

    public Response execute(Request request) {
        boolean isInvalidData = !init(request);//CHECK DATA RESULT
        if (isInvalidData) {
            return Response.errResponse(Code.INVALID_REQ);
        }

        if (!reLock.tryLock()) {
            return Response.errResponse(Code.LOCKED);
        }

        return proceedDeviceIo(request);
    }

    private Response proceedDeviceIo(Request request) {
        try {
            if (request.isNeedSessionWait()) {
                waitSessionIntervalFromLastTime();
            }
            open();
            clearBuffer();
            sendDataToDevice();
            if (hasNakResponse()) {
                return Response.errResponse(Code.NAK_RES);
            }
            int code = readResponse();
            echoResponseToDevice(code);

            return Response.wrapResponse(code, new String(fromNcccData));
        } catch (IOException e) {
            e.printStackTrace();
            return Response.errResponse(Code.IO_ERROR);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return Response.errResponse(Code.INTERRUPTED);
        } finally {
            close();
            reLock.unlock();
            setNextAvailableTime(request.getSessionInterval());
        }
    }

    private void setNextAvailableTime(int sessionIntervalMillis) {
        nextAvailableTime = System.currentTimeMillis() + sessionIntervalMillis;
    }

    private void waitSessionIntervalFromLastTime() throws InterruptedException {
        long waitTime = System.currentTimeMillis() - nextAvailableTime;
        if (waitTime < 0) {
            Thread.sleep(waitTime * (-1));
        }
    }

    private void open() throws IOException {
        serialPort = new SerialPort(devPath, devBaudRate);
        mFileInputStream = (FileInputStream) serialPort.getInputStream();
        mFileOutputStream = (FileOutputStream) serialPort.getOutputStream();
    }

    private void close() {
        if (mFileInputStream != null) {
            try {
                mFileInputStream.close();
                mFileInputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (mFileOutputStream != null) {
            try {
                mFileOutputStream.flush();
                mFileOutputStream.close();
                mFileOutputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (serialPort != null) {
            serialPort.close();
            serialPort = null;
        }

    }

    private void clearBuffer() throws IOException, InterruptedException {
        byte[] dataBuffer = new byte[1024];
        Thread.sleep(100);
        while (mFileInputStream.available() > 0) {
            int l = mFileInputStream.read(dataBuffer);
            Log.i(getClass().getSimpleName(), "clearBuffer/" + l);
            Thread.sleep(10);
        }
    }

    private void sendDataToDevice() throws IOException {
        mFileOutputStream.write(toNcccData);
    }

    private boolean hasNakResponse() throws IOException, InterruptedException {
        byte[] buffer = new byte[1];
        while (System.currentTimeMillis() < (startTime + ACK_TIMEOUT)) {
            Thread.sleep(10);
            if (mFileInputStream.available() == 0) {
                continue;
            }
            if (mFileInputStream.read(buffer, 0, 1) == 0) {
                continue;
            }
            if (buffer[0] == NCCCPacketHandler.ACK) {
                return false;
            } else if (buffer[0] == NCCCPacketHandler.NAK) {
                return true;
            }
        }
        return false;
    }

    private int readResponse() throws IOException, InterruptedException {
        final int READ_SIZE = 256;
        byte controlByte = NCCCPacketHandler.STX;

        while (System.currentTimeMillis() < (startTime + EDC_TIMEOUT)) {
            if ((dataEndIndex + READ_SIZE) >= 4096) {// Buffer overflow
                return Code.INVALID_LENGTH;
            }
            if ((dataIndex - dataStartIndex) > NCCCPacketHandler.PACKET_LENGTH) {
                return Code.INVALID_LENGTH;
            }
            if (mFileInputStream.available() == 0) {
                Thread.sleep(100);
                continue;
            }
            int readLen = mFileInputStream.read(dataBuffer, dataIndex + 1, READ_SIZE);
            dataIndex += readLen;
            boolean isFoundControlByte = findByteAndPoint(controlByte);
            if (isFoundControlByte) {
                if (controlByte == NCCCPacketHandler.STX) {
                    pointDataStartIndex();
                    controlByte = NCCCPacketHandler.ETX;
                } else {//found ETX
                    return handleAndCheckReceivedData();
                }
            }
        }
        return Code.TIMEOUT;
    }

    private boolean findByteAndPoint(byte controlByte) {
        do {
            dataEndIndex++;
            if (dataBuffer[dataEndIndex] == controlByte) {
                return true;
            }
        } while (dataIndex != dataEndIndex);
        return false;
    }

    private void pointDataStartIndex() {
        dataStartIndex = dataEndIndex + 1;
    }

    private boolean isLrcNotFound() throws IOException, InterruptedException {
        int tryTime = 0;
        while (tryTime < 3) {
            tryTime++;
            if (mFileInputStream.available() != 0) {
                mFileInputStream.read(dataBuffer, dataEndIndex + 1, 1);
                return false;
            }
            Thread.sleep(1000);
        }
        return true;
    }

    private int handleAndCheckReceivedData() throws IOException, InterruptedException {
        if (dataIndex == dataEndIndex) {
            if (isLrcNotFound()) {
                return Code.NO_LRC;
            }
        }
        if ((dataEndIndex - dataStartIndex) != NCCCPacketHandler.PACKET_LENGTH) {
            return Code.INVALID_LENGTH;
        }
        byte[] verifiedBuf = new byte[NCCCPacketHandler.PACKET_LENGTH + 2];
        System.arraycopy(dataBuffer, dataStartIndex, verifiedBuf, 0, NCCCPacketHandler.PACKET_LENGTH + 2);
        boolean isValidData = NCCCPacketHandler.validatePacket(verifiedBuf);
        if (isValidData) {
            System.arraycopy(dataBuffer, dataStartIndex, fromNcccData, 0, NCCCPacketHandler.PACKET_LENGTH);
            return Code.SUCCESS;
        }
        return Code.WRONG_DATA;
    }

    private void echoResponseToDevice(int code) throws IOException {
        if (code == Code.TIMEOUT) {
            //套件判斷的TIMEOUT，通常為卡機沒收到REQUEST。回傳NAK會造成白畫面數秒且無意義，故不回傳。
            return;
        }
        byte echo = NCCCPacketHandler.ACK;
        if (code > 0) {
            echo = NCCCPacketHandler.NAK;
        }
        byte[] outBuf = new byte[2];
        outBuf[0] = echo;
        outBuf[1] = echo;
        mFileOutputStream.write(outBuf);
    }
}
