package com.lafresh.kiosk.printer.epPrinter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.lafresh.kiosk.utils.CommonUtils;

import static com.lafresh.kiosk.printer.epPrinter.Ep380Printer.mUsb;

public class EpPrinterReceiver extends BroadcastReceiver {
    Ep380Printer partnerPrinter;

    public EpPrinterReceiver(Ep380Printer partnerPrinter) {
        this.partnerPrinter = partnerPrinter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        UsbManager mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        if ((device.getVendorId() + "-" + device.getProductId()).equals(action)) {
            if (!intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                CommonUtils.showMessage(context,"無出單機權限");
                return;
            }

            if (mUsb.Open(mUsbManager, device, context)) {
                CommonUtils.showMessage(context,"出單機連線成功");
            } else {
                CommonUtils.showMessage(context,"出單機連線失敗");
            }
        }

        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            if (Ep380Printer.VENDOR_ID == device.getVendorId() && Ep380Printer.PRODUCT_ID == device.getProductId()) {
                partnerPrinter.requestPrinterPermission(context);
            }
        }

        //----------------------------------------------------------------------------------
        // usb斷線
        if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            if (Ep380Printer.VENDOR_ID == device.getVendorId() && Ep380Printer.PRODUCT_ID == device.getProductId()) {
                partnerPrinter.disconnect();
            }
        }
    }
}
