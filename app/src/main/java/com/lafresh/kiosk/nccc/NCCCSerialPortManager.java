package com.lafresh.kiosk.nccc;

import android.serialport.SerialPort;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.lafresh.kiosk.utils.LogUtil;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * 這支主要在啟用serial port 功能與信用卡機進行溝通
 *
 * Serial port 控制碼代號
 * NUL 00
 * SOH 01
 * STX 02 Start of text
 * ETX 03 End of text
 * EOT 04
 * ENQ 05
 * ACK 06 Acknowledge
 * BEL 07
 * BS  08
 * HT  09
 * LF  0A
 * VT  0B
 * FE  0C
 * CR  0D
 * SO  0E
 * SI  0F
 * DLE 10
 * DC1 11
 * DC2 12
 * DC3 13
 * DC4 14
 * NAK 15 Negative acknowledge
 * SYN 16
 * ETB 17
 * CAN 18
 * EM  19
 * SUB 1A
 * ESC 1B
 * FS  1C
 * GS  1D
 * RS  1E
 * US  1F
 * SP  20
 *
 * */
public class NCCCSerialPortManager {
    private SerialPort serialPort;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;


    private byte LRC = 0; //Exclusive-Or All Bytes Of Data & ETX (STX Not Include).
    private byte[] EcrSendData = new byte[1024];

    private byte[] AckOrNak = new byte[1024];
    private byte[] EcrReceiveData = new byte[1024];
    private byte[] EcrInDataBuffer = new byte[1024];//建立一個大小8Byte的緩衝區
    private byte[] EcrOutDataBuffer = new byte[1024];//建立一個大小8Byte的緩衝區
    private int bufSize;//用來計算讀入的資料大小
    private int i = 0;
    private int EcrEndFlag = 0;
    private static boolean connectOk = false;
    public static final int EDC_TIMEOUT = 60;

    public static boolean isConnectOk() {
        return connectOk;
    }

    public void open(String path, int baudRate) throws IOException {
        serialPort = new SerialPort(path, baudRate);
        mFileInputStream = (FileInputStream) serialPort.getInputStream();
        mFileOutputStream = (FileOutputStream) serialPort.getOutputStream();
    }

    public void close() {
        if (mFileInputStream != null) {
            try {
                mFileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (mFileOutputStream != null) {
            try {
                mFileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (serialPort != null) {
            serialPort.close();
            serialPort = null;
        }
    }

    public void executeIo(byte[] input, Listener listener) throws IOException {
        int totalBufsize = input.length;
        System.arraycopy(input, 0, EcrInDataBuffer, 0, totalBufsize);

        EcrInDataBuffer[totalBufsize] = 0x03;

        //xor input value to LRC
        for (int i = 0; i < totalBufsize + 1; i++) {
            LRC ^= EcrInDataBuffer[i];
        }

        // STX  02 作為文檔開頭
        EcrSendData[0] = 0x02;

        //複製重做一個byte array, stx + byte data + etx +LRC,+1是要填補 etx 的位置,因為一個已經被stx佔用
        System.arraycopy(EcrInDataBuffer, 0, EcrSendData, 1, totalBufsize + 1);

        //最後填補LRC位置 ， 所以最終 byte array 等於 stx + byte data + etx +LRC
        EcrSendData[totalBufsize + 2] = LRC;

        // 20190614 Embert.Tsai
        // Clear buffer before sending command.
        try {
            while (mFileInputStream.available() > 0) {
                bufSize = mFileInputStream.read(EcrOutDataBuffer);//讀取資料到buf內
                Thread.sleep(1);
            }
        } catch (Exception e) {
            Log.i("Seriport", "Can not clear buffer");
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().log(LogUtil.parseExceptionToString(e));
        }

        try {
            mFileOutputStream.write(EcrSendData, 0, totalBufsize + 3);
            Log.i("Seriport", "發送成功" + (totalBufsize + 3));
        } catch (Exception e) {
            Log.i("Seriport", "發送失敗");
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().log(LogUtil.parseExceptionToString(e));
        }

        try {
            while (i++ < 300) {
                mFileInputStream.read(AckOrNak, i, 1);
                if (AckOrNak[i] == 0x06) {
                    Log.i("Seriport", "接收成功:ACK");
                    break;
                } else if (AckOrNak[i] == 0x15) {
                    Log.i("Seriport", "接收成功:NAK");
                    break;
                }

                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().log(LogUtil.parseExceptionToString(e));
        }
        connectOk = true;
        i = 0;
        int j = 0;
        try {
            // 20190614 Embert.Tsai
            // New method.
            while (true) {
                if (mFileInputStream.available() > 0) {
                    bufSize = mFileInputStream.read(EcrOutDataBuffer);//讀取資料到buf內
                    for (i = 0; i < bufSize; i++) {
                        if (EcrOutDataBuffer[i] == 0x00) {
                            continue;
                        }
                        if (EcrOutDataBuffer[i] == 0x02) {
                            Log.i("Seriport", "接收成功:STX");
                            break;
                        }
                    }
                }
                if (EcrOutDataBuffer[i] == 0x02) {
                    break;
                }
                Thread.sleep(1);
            }
            if (i < (bufSize - 1)) {
                System.arraycopy(EcrOutDataBuffer, i + 1, EcrReceiveData, j, bufSize - i - 1);
                j += (bufSize - i - 1);
            }

            //這裏收到response stx+ byte array + etx + LRC
            while (true) {
                if (mFileInputStream.available() > 0) {
                    bufSize = mFileInputStream.read(EcrOutDataBuffer);//讀取資料到buf內
                    for (i = 0; i < bufSize; i++) {
                        EcrReceiveData[j] = EcrOutDataBuffer[i];
                        j++;
                        if (EcrOutDataBuffer[i] == 0x03) {
                            if (i == (bufSize - 1)) {
                                bufSize = mFileInputStream.read(EcrOutDataBuffer);//讀取資料到buf內
                                EcrReceiveData[j] = EcrOutDataBuffer[0];
                            } else {
                                EcrReceiveData[j] = EcrOutDataBuffer[i];
                            }
                            EcrEndFlag = 1;
                            Log.i("Seriport", "接收成功:ETX");
                            break;
                        }
                    }
                }
                if (EcrEndFlag == 1) {
                    Log.i("Seriport", "Send to NCCC Success");
                    FirebaseCrashlytics.getInstance().log("Send to NCCC Success");

                    break;
                }
                Thread.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().log(LogUtil.parseExceptionToString(e));
        }


        if (EcrEndFlag == 1) {
            //這裡兩行表示回傳給對方兩個ack，表示有收到卡機回傳資料（不要刪掉其中一行）.
            mFileOutputStream.write(new byte[]{0x06}, 0, 1);
            mFileOutputStream.write(new byte[]{0x06}, 0, 1);
            FirebaseCrashlytics.getInstance().log("Send ACK and received NCCC response");
            Log.i("Seriport", "Send ACK  to talk to received NCCC response");
        }

        int receiveLength = j - 1;

        close();

        if (receiveLength > 0) {
            String resString = new String(EcrReceiveData, 0, receiveLength);
            Log.wtf(getClass().getSimpleName(), "ReceiveLength = " + receiveLength);
            Log.wtf(getClass().getSimpleName(), "ReceiveString = " + resString);
            listener.onFinish(resString);
        } else {
            FirebaseCrashlytics.getInstance().log("NCCC receiveLength = -1");
        }
    }

    public static byte[] strToByteArray(String str) {
        if (str == null) {
            return null;
        }
        return str.getBytes();
    }

    public interface Listener {
        void onFinish(String res);
    }
}
