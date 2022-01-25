package com.lafresh.kiosk.printer.rp700;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 當作第三方的Library可以綁定系統的設定檔
 * 使用AndroidPosSetting設定檔資料
 */
public class HardwareConnectionHelper {
    private UsbDeviceConnection connection;
    private UsbInterface usbInterface;
    private UsbEndpoint outEndPoint;
    private UsbEndpoint inEndPoint;

    public boolean openUsb(Context application_context, UsbDevice usbDevice) {
        if (usbDevice == null) {
            return false;
        }

        UsbManager usbManager = (UsbManager) application_context.getSystemService(Context.USB_SERVICE);
        connection = usbManager.openDevice(usbDevice);
        if (connection == null) {
            return false;
        }

        usbInterface = usbDevice.getInterface(0);

        if (!connection.claimInterface(usbInterface, true)) {
            return false;
        }

        int endpoint_count = usbInterface.getEndpointCount();

        for (int i = 0; i < endpoint_count; i++) {
            UsbEndpoint endpoint = usbInterface.getEndpoint(i);
            if (endpoint.getDirection() == UsbConstants.USB_DIR_OUT && endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                outEndPoint = endpoint;
            }

            if (endpoint.getDirection() == UsbConstants.USB_DIR_IN && endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                inEndPoint = endpoint;
            }
        }
        if (outEndPoint == null) {
            return false;
        }

        return true;

    }

    public boolean closeUsb() {
        boolean state = connection.releaseInterface(usbInterface);
        connection.close();
        return state;
    }

    public boolean writeDataInUsb(byte[] bytes, int timeout) {
        int state = connection.bulkTransfer(outEndPoint, bytes, bytes.length, timeout);
        return state != -1;
    }

    public boolean readDataInUsb(byte[] container, int timeout) {
        int state = connection.bulkTransfer(inEndPoint, container, container.length, timeout);
        return state != -1;
    }

    private Socket socket;
    private OutputStream outputStream;

    public boolean openWifi(String ip, int port) {
        synchronized (this) {
            try {
                if (socket != null) {
                    socket.close();
                }

                socket = new Socket();
                socket.setKeepAlive(true);
                socket.connect(new InetSocketAddress(ip, port), 500);

                if (!socket.isConnected()) {
                    return false;
                }
                outputStream = socket.getOutputStream();

                return true;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean closeWifi() {
        try {
            outputStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean printDataInWifi(byte[] bytes) {
        try {
            outputStream.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public UsbDeviceConnection getConnection() {
        return connection;
    }
}
