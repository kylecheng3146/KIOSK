package com.lafresh.kiosk.printer.rp700;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.lafresh.kiosk.utils.CommonUtils;

public class RP700Receiver extends BroadcastReceiver {
    RP700 rp700;

    public RP700Receiver(RP700 rp700) {
        this.rp700 = rp700;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

        synchronized (this) {
            //----------------------------------------------------------------------------------
            // 獲取usb設備權限
            assert device != null;
            if ((device.getVendorId() + "-" + device.getProductId()).equals(action)) {
                // 取得USB同意權限,配合requestPermission,不同意return
                if (!intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    CommonUtils.showMessage(context,"無出單機權限");
                    return;
                }

                if (rp700.connectUsb(device)) {
                    rp700.setConnected(true);
                    CommonUtils.showMessage(context,"出單機連線成功");
                } else {
                    rp700.setConnected(false);
                    CommonUtils.showMessage(context,"出單機連線失敗");
                }
            }

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                if (RP700.VENDOR_ID == device.getVendorId() && RP700.PRODUCT_ID == device.getProductId()) {
//                    KioskPrinter.addLog("Usb連線");
                    rp700.requestPrinterPermission(context);
                }
            }

            //----------------------------------------------------------------------------------
            // usb斷線
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                if (RP700.VENDOR_ID == device.getVendorId() && RP700.PRODUCT_ID == device.getProductId()) {
//                    KioskPrinter.addLog("Usb斷線");
                    rp700.disconnect();
                    rp700.setConnected(false);
                }
            }
        }
    }
}
