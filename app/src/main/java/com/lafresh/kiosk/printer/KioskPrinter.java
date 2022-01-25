package com.lafresh.kiosk.printer;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.lafresh.kiosk.BuildConfig;
import com.lafresh.kiosk.Config;
import com.lafresh.kiosk.R;
import com.lafresh.kiosk.dialog.PrinterErrorDialog;
import com.lafresh.kiosk.easycard.model.EcCheckoutData;
import com.lafresh.kiosk.printer.epPrinter.Ep380Printer;
import com.lafresh.kiosk.printer.lanxin.Lanxin;
import com.lafresh.kiosk.printer.model.EReceipt;
import com.lafresh.kiosk.printer.model.ReceiptDetail;
import com.lafresh.kiosk.printer.model.Revenue;
import com.lafresh.kiosk.printer.model.Ticket;
import com.lafresh.kiosk.printer.rp700.RP700;
import com.lafresh.kiosk.printer.tp805.TP805Printer;
import com.lafresh.kiosk.printer.wpprinter.Wp837Printer;
import com.lafresh.kiosk.utils.TimeUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public abstract class KioskPrinter {
    public static final int PRINT_SUCCESS = 1001;
    public static final int PRINT_FAILED = -999;
    public static String printerStatus;
    private static KioskPrinter printer;

    public static KioskPrinter initKioskPrinter(Context context) {
        if (printer == null) {
            if (Config.disablePrinter) {
                printer = new DummyPrinter();//不使用出單機，回傳假出單機。不影響流程
                return printer;
            }

            if(BuildConfig.FLAVOR.equals("kinyo")){
                printer = new TP805Printer(context);
            } else if(BuildConfig.FLAVOR.equals("lanxin")){
                printer = new Lanxin(context);
            }else{
                UsbManager mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
                HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
                Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
                while (deviceIterator.hasNext()) {
                    UsbDevice myDevice = deviceIterator.next();
                    if (Wp837Printer.VENDOR_ID == myDevice.getVendorId() && Wp837Printer.PRODUCT_ID == myDevice.getProductId()) {
                        printer = new Wp837Printer(context);
                    }
                    if (Ep380Printer.VENDOR_ID == myDevice.getVendorId() && Ep380Printer.PRODUCT_ID == myDevice.getProductId()) {
                        printer = new Ep380Printer(context);
                    }
                    if (RP700.VENDOR_ID == myDevice.getVendorId() && RP700.PRODUCT_ID == myDevice.getProductId()) {
                        printer = new RP700(context);
                    }
                    //往下增加出單機
                }
            }
        }
        return printer;
    }

    public static KioskPrinter getPrinter() {
        return printer;
    }

    public static void releasePrinter() {
        printer = null;
    }

    public abstract void releaseAll(Context context);

    public abstract void requestPrinterPermission(Context context);

    public void requestUsbPermission(Context context, int vendorId, int productId) {
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(vendorId + "-" + productId), PendingIntent.FLAG_ONE_SHOT);

        UsbManager mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice usbDevice = deviceIterator.next();
            if (vendorId == usbDevice.getVendorId() && productId == usbDevice.getProductId()) {
                mUsbManager.requestPermission(usbDevice, mPermissionIntent);
            }
        }
    }

    public boolean connectUsb(UsbDevice usbDevice) {
        return false;
    }

    public abstract boolean disconnect();

    public abstract boolean isConnect();

    //電子發票
    public int printEInvoice(Context context) {
        return PRINT_SUCCESS;
    }

    //電子發票
    public int printEInvoice(Context context, EReceipt receipt, ReceiptDetail detail) {
        return PRINT_SUCCESS;
    }

    //活動訊息
    public int printActivityMessage(Context context, String activityMessage) {
        return PRINT_SUCCESS;
    }

    //交易明細
    public int printPaymentDetail(Context context) {
        return PRINT_SUCCESS;
    }

    //交易明細
    public int printPaymentDetail(Context context, EReceipt receipt, ReceiptDetail detail) {
        return PRINT_SUCCESS;
    }

    //收據
    public int printReceipt(Context context) {
        return PRINT_SUCCESS;
    }

    //收據
    public int printReceipt(Context context, EReceipt receipt, ReceiptDetail detail) {
        return PRINT_SUCCESS;
    }

    //遊戲點數（智葳）
    public int printGamePoint(Context context) {
        return PRINT_SUCCESS;
    }

    //付款單
    public int printBill(Context context) {
        return PRINT_SUCCESS;
    }

    //付款單
    public int printBill(Context context, ReceiptDetail detail) {
        return PRINT_SUCCESS;
    }

    //信用卡收執聯
    public int printCreditCardReceipt(Context context) {
        return PRINT_SUCCESS;
    }

    public void cutPaper() {
    }

    public int printTest(Context context, String data) {
        return PRINT_SUCCESS;
    }

    //悠遊卡結帳條
    public int printEasyCardCheckOutData(EcCheckoutData ecCheckoutData) {
        return PRINT_SUCCESS;
    }

    public int printTicket(Context context, Ticket ticket) {
        return PRINT_SUCCESS;
    }

    //每日帳務報表
    public int printDateRevenue(Context context, Revenue revenue) {
        return PRINT_SUCCESS;
    }

    public void showPrinterErrorDialog(Context context, PrinterErrorDialog.Listener listener, String message) {
    }

    public void beforePrintCheckFlow(Context context, PrinterErrorDialog.Listener dialogListener, PrinterCheckFlowListener printerCheckFlowListener) {
        //兼容無檢查功能出單機
        printerCheckFlowListener.onFinish();
    }

    public void laterPrinterCheckFlow(Context context, PrinterErrorDialog.Listener dialogListener, PrinterCheckFlowListener printerCheckFlowListener, int second) {
        //兼容無檢查功能出單機
        printerCheckFlowListener.onFinish();
    }

    protected static String getPayName(Context ct, String payId) {
        switch (payId) {
            case "K":
                return ct.getString(R.string.easyCard);
            case "Q004":
                return ct.getString(R.string.coupon);
            case "Q006":
                return ct.getString(R.string.linePay);
            case "Q011":
                return ct.getString(R.string.creditCard);
            case "Q012":
                return ct.getString(R.string.piPay);
            default:
                return "";
        }
    }

    public static String getTime() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        String M = month <= 9 ? "0" + month : month + "";
        String D = day <= 9 ? "0" + day : day + "";
        String hh = hour <= 9 ? "0" + hour : hour + "";
        String mm = minute <= 9 ? "0" + minute : minute + "";
        String ss = second <= 9 ? "0" + second : second + "";
        String YMD = year + "-" + M + "-" + D;
        String hhmmss = hh + ":" + mm + ":" + ss;

        return YMD + " " + hhmmss;
    }

    public static void addLog(String msg) {
        String date = TimeUtil.getNowTime() + " ";

        if (printerStatus != null) {
            printerStatus += date;
        } else {
            printerStatus = date;
        }
        printerStatus += msg;
        printerStatus += "\n";
    }

    protected static void crashlyticsLog(byte status, boolean isPaperOut, boolean beforePrint) {
        FirebaseCrashlytics.getInstance().log("Printer Check Error");
        FirebaseCrashlytics.getInstance().log("Printer:"+"isError ResCode = " + status);
        FirebaseCrashlytics.getInstance().log("Printer:"+"isPaperOut = " + isPaperOut);
        FirebaseCrashlytics.getInstance().log("Printer:"+"isBeforePrint = " + beforePrint);
    }

    public interface PrinterCheckFlowListener {
        void onFinish();
    }
}
