package com.lafresh.kiosk.printer.wpprinter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.lafresh.kiosk.printer.KioskPrinter;
import com.lafresh.kiosk.utils.CommonUtils;


/**
 * 處理出單機(wp-k837)連線
 */

public class WpPrinterReceiver extends BroadcastReceiver {
    Wp837Printer wp837Printer;

    public WpPrinterReceiver(Wp837Printer wp837Printer) {
        this.wp837Printer = wp837Printer;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

        synchronized (this) {
            //----------------------------------------------------------------------------------
            // 獲取usb設備權限
            if ((device.getVendorId() + "-" + device.getProductId()).equals(action)) {
                // 取得USB同意權限,配合requestPermission,不同意return
                if (!intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    CommonUtils.showMessage(context,"無出單機權限");
                    return;
                }

                if (wp837Printer.connectUsb(device)) {
                    CommonUtils.showMessage(context,"出單機連線成功");
                } else {
                    CommonUtils.showMessage(context,"出單機連線失敗");
                }
            }

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                if (Wp837Printer.VENDOR_ID == device.getVendorId() && Wp837Printer.PRODUCT_ID == device.getProductId()) {
                    KioskPrinter.addLog("Usb連線");
                    wp837Printer.requestPrinterPermission(context);
                }
            }

            //----------------------------------------------------------------------------------
            // usb斷線
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                if (Wp837Printer.VENDOR_ID == device.getVendorId() && Wp837Printer.PRODUCT_ID == device.getProductId()) {
                    KioskPrinter.addLog("Usb斷線");
                    wp837Printer.disconnect();
                }
            }
        }
    }
}
