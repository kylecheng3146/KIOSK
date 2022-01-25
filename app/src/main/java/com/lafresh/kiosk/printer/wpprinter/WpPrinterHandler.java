package com.lafresh.kiosk.printer.wpprinter;

import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.lafresh.kiosk.printer.KioskPrinter;

import java.util.HashMap;

import wpprinter.printer.WpPrinter;


public class WpPrinterHandler implements Handler.Callback {

    private ConnectListener connectListener;
    private SensorListener sensorListener;
    private StatusListener statusListener;

    public void setConnectListener(ConnectListener connectListener) {
        this.connectListener = connectListener;
    }

    public void setSensorListener(SensorListener sensorListener) {
        this.sensorListener = sensorListener;
    }

    public void setStatusListener(StatusListener statusListener) {
        this.statusListener = statusListener;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            //findUsbPrintersBySerial Return
            case WpPrinter.MESSAGE_USB_SERIAL_SET:
                if (msg.obj == null) {
                    if (connectListener != null) {
                        connectListener.onResult(false);
                        connectListener = null;
                    }
                } else {
                    final HashMap<String, UsbDevice> usbDeviceMap = (HashMap<String, UsbDevice>) msg.obj;
                    final String[] items = usbDeviceMap.keySet().toArray(new String[usbDeviceMap.size()]);
                    //直接取第一台來連線
                    UsbDevice usbDevice = usbDeviceMap.get(items[0]);
                    KioskPrinter.getPrinter().connectUsb(usbDevice);
                }
                return true;

            //connectUsb Return
            case WpPrinter.MESSAGE_STATE_CHANGE: {
                switch (msg.arg1) {
                    case WpPrinter.STATE_CONNECTED:
                        if (connectListener != null) {
                            connectListener.onResult(true);
                            connectListener = null;
                        }
                        break;
                }
                return true;
            }

            //directIO Return
            case WpPrinter.MESSAGE_READ:
                if (msg.arg1 == WpPrinter.PROCESS_EXECUTE_DIRECT_IO) {
                    Bundle data = msg.getData();
                    byte[] response = data.getByteArray(WpPrinter.KEY_STRING_DIRECT_IO);
                    int d = response[0];

                    if (statusListener != null) {
                        if ((d & 0x40) == 0x40) {
                            //PrinterError();
                            statusListener.statusResult(true, d);
                        } else if ((d & 0x04) == 0x04) {
                            //PrinterCoverOpen();
                            statusListener.statusResult(true, d);
                        } else if ((d & 0x20) == 0x20) {
                            //PaperEndError();
                            statusListener.statusResult(true, d);
                        } else {
                            statusListener.statusResult(false, d);
                        }

                        statusListener = null;
                        return true;
                    }

                    if (sensorListener != null) {
                        if ((d & 0x08) == 0x08) {
                            //paperPresent 0x3A=58
                            sensorListener.paperResult(true, d);
                        } else {
                            //paperNotPresent 50d=0x32
                            sensorListener.paperResult(false, d);
                        }
                        sensorListener = null;
                        return true;
                    }
                }
                return true;

            default:
                return true;
        }
    }

    public interface ThreadFlagController {
        boolean isResponded();

        void setResponded();
    }

    public interface ConnectListener {
        void onResult(boolean isConnected);
    }

    public interface SensorListener extends ThreadFlagController {
        void paperResult(boolean present, int resCode);
    }

    public interface StatusListener extends ThreadFlagController {
        void statusResult(boolean isError, int resCode);
    }
}
