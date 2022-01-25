package com.lafresh.kiosk.printer.wpprinter;

import static com.lafresh.kiosk.utils.FormatUtil.addLeftSpaceTillLength;
import static com.lafresh.kiosk.utils.FormatUtil.removeDot;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.lafresh.kiosk.Bill;
import com.lafresh.kiosk.BuildConfig;
import com.lafresh.kiosk.Config;
import com.lafresh.kiosk.R;
import com.lafresh.kiosk.creditcardlib.nccc.NCCCCode;
import com.lafresh.kiosk.creditcardlib.nccc.NCCCTransDataBean;
import com.lafresh.kiosk.dialog.PrinterErrorDialog;
import com.lafresh.kiosk.easycard.model.EcCheckoutData;
import com.lafresh.kiosk.easycard.model.EcPayData;
import com.lafresh.kiosk.httprequest.model.Invoice;
import com.lafresh.kiosk.httprequest.model.OrderResponse;
import com.lafresh.kiosk.httprequest.model.WebOrder01;
import com.lafresh.kiosk.httprequest.model.WebOrder02;
import com.lafresh.kiosk.manager.BasicSettingsManager;
import com.lafresh.kiosk.manager.OrderManager;
import com.lafresh.kiosk.model.BasicSettings;
import com.lafresh.kiosk.printer.KioskPrinter;
import com.lafresh.kiosk.printer.model.EReceipt;
import com.lafresh.kiosk.printer.model.Payment;
import com.lafresh.kiosk.printer.model.Product;
import com.lafresh.kiosk.printer.model.ReceiptDetail;
import com.lafresh.kiosk.type.FlavorType;
import com.lafresh.kiosk.utils.CommonUtils;
import com.lafresh.kiosk.utils.ComputationUtils;
import com.lafresh.kiosk.utils.FormatUtil;
import com.lafresh.kiosk.utils.TimeUtil;
import com.lvrenyang.io.ImageProcessing;

import java.util.List;

import wpprinter.printer.WpPrinter;

@RequiresApi(api = Build.VERSION_CODES.M)
public class Wp837Printer extends KioskPrinter {
    public static final int VENDOR_ID = 8864;
    public static final int PRODUCT_ID = 837;
    private final Context mContext;
    private WpPrinter mWpPrinter;
    private WpPrinterReceiver wpPrinterReceiver;

    private WpPrinterHandler wpHandler;
    private static boolean isPaperOut = false;
    private static String paperOutCheckHistory = "";

    public Wp837Printer(Context context) {
        wpHandler = new WpPrinterHandler();
        Handler handler = new Handler(wpHandler);
        mWpPrinter = new WpPrinter(context, handler, null);
        setReceiver(context);
        this.mContext = context;
    }

    private void setReceiver(Context context) {
        wpPrinterReceiver = new WpPrinterReceiver(this);
        String filterTag = VENDOR_ID + "-" + PRODUCT_ID;
        IntentFilter intentFilter = new IntentFilter(filterTag);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        intentFilter.addAction(UsbManager.EXTRA_PERMISSION_GRANTED);
        context.registerReceiver(wpPrinterReceiver, intentFilter);
    }

    @Override
    public void releaseAll(Context context) {
        if (wpPrinterReceiver != null) {
            context.getApplicationContext().unregisterReceiver(wpPrinterReceiver);
            wpPrinterReceiver = null;
        }
        if (isConnect()) {
            mWpPrinter.shutDown();
        }
        releasePrinter();
    }

    @Override
    public void requestPrinterPermission(Context context) {
        requestUsbPermission(context, VENDOR_ID, PRODUCT_ID);
    }

    @Override
    public boolean disconnect() {
        mWpPrinter.disconnect();
        return true;
    }

    @Override
    public boolean isConnect() {
        return WpPrinter.is_connected();
    }

    @Override
    public boolean connectUsb(UsbDevice usbDevice) {
        mWpPrinter.connect(usbDevice);
        return true;
    }

    @Override
    public int printEInvoice(Context context) {
        int alignment = WpPrinter.ALIGNMENT_CENTER;
        int attribute = WpPrinter.TEXT_ATTRIBUTE_FONT_A;
        int size = WpPrinter.TEXT_SIZE_HORIZONTAL2 | WpPrinter.TEXT_SIZE_VERTICAL2;
        byte NDOT_BARCODE_SPACE = 3;
        String data;

        //--- Set Page Mode ----------------------
        mWpPrinter.SP_SetPageMode();  // 設定 page mode = ON

        Invoice invoice = Bill.fromServer.getOrderResponse().getInvoice();
        String testGuiNo = context.getString(R.string.testGuiNo);
        String guiNo = invoice.getGUI_number();

        //此為測試發票
        if (testGuiNo.equals(guiNo)) {
            String testString = context.getString(R.string.testReceipt);
            mWpPrinter.SP_printBig5(testString, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);
            mWpPrinter.SP_PrintFeedPaperDot((byte) 15);
        }

        //1.logo
        String invoiceTitle = invoice.getInvoice_title();
        if (invoiceTitle == null) {
            invoiceTitle = "";
        }
        mWpPrinter.SP_printBig5(invoiceTitle, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);
        mWpPrinter.SP_PrintFeedPaperDot((byte) 15);

        //2.電子發票證明聯
        data = context.getString(R.string.eInvPrintTitle);
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //3.發票期別
        String twYear = invoice.getTwYearString();
        String beginMonth = invoice.getStartMonthString();
        String endMonth = invoice.getEndMonthString();
        data = twYear + context.getString(R.string.year) + beginMonth + "-" + endMonth + context.getString(R.string.month);
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10); // 列印空白點數

        //4.發票號碼
        String invNo = invoice.getInv_no_b();
        data = invNo.substring(0, 2) + "-" + invNo.substring(2, 10);
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.SP_PrintFeedPaperDot((byte) 25); // 列印空白點數

        //5.列印時間
        alignment = WpPrinter.ALIGNMENT_LEFT;
        size = WpPrinter.TEXT_SIZE_HORIZONTAL1 | WpPrinter.TEXT_SIZE_VERTICAL1;
        mWpPrinter.SP_SetDotposition(30); //設定與起始點空白距離
        data = getTime() + (Bill.fromServer.getCustCode().equals("") ? "" : "  格式25");
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //6a.列印隨機碼
        data = "隨機碼 " + invoice.getRandom_no();
        mWpPrinter.SP_SetDotposition(30); //設定與起始點空白距離
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串

        //6.b總計
        mWpPrinter.SP_SetDotposition((int) (230)); //設定與起始點空白距離
        mWpPrinter.SP_printBig5("總計  " + Bill.fromServer.getInvoiceAmount(), alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false); //跳行

        //7a.列印買賣方code -------------------
        data = context.getString(R.string.seller) + guiNo;
        mWpPrinter.SP_SetDotposition(30);
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串

        //7b.買方code
        mWpPrinter.SP_SetDotposition(230); //設定與起始點空白距離
        String custCode = Bill.fromServer.getCustCode();
        if (custCode.length() > 0) {
            mWpPrinter.SP_printBig5(context.getString(R.string.buyer) + custCode, alignment, attribute, size, false);  // 列印Big5字串
        }
        mWpPrinter.lineFeed(1, false); //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數 //Snail

        //8.條碼
        int barCodeSystem = WpPrinter.BAR_CODE_CODE39;
        int height = 48;
        int width = 1;
        int characterPosition = WpPrinter.HRI_CHARACTER_NOT_PRINTED;
        alignment = WpPrinter.ALIGNMENT_CENTER;
        String barcodeData = invoice.getBarcodeData();
        mWpPrinter.print1dBarcode(barcodeData, barCodeSystem, alignment, width, height, characterPosition, false);
        mWpPrinter.SP_PrintFeedPaperDot(NDOT_BARCODE_SPACE); // 列印空白點數
        mWpPrinter.SP_PrintFeedPaperDot((byte) 15);  // 列印空白點數 //Snail

        // X 9.左右QRCode
        //--- 列印左邊 QRcode -------------------------
        int model = WpPrinter.QR_CODE_MODEL1;
        int QrCdoeWidth = 3;
        final byte QRCodeVersion = 7;
        final int errorCorrectionLevel = '0';
        alignment = WpPrinter.ALIGNMENT_LEFT;
        mWpPrinter.SP_SelectCorrectionLevel((byte) errorCorrectionLevel); // select QRcode correction Level: '0'~ '3'
        mWpPrinter.SP_SetQRcodeVersion((byte) QRCodeVersion); //設定 QRcode Version
        mWpPrinter.SP_SetDotposition(45);   //設定與起始點空白距離
        String[] QR = Bill.fromServer.getQR();
        data = QR[0];
        mWpPrinter.printQrCode(data, alignment, model, QrCdoeWidth, true); //列印QRcode

        //---列印右邊 QRcode ------------------------------------------
        mWpPrinter.SP_SelectCorrectionLevel((byte) errorCorrectionLevel); // select QRcode correction Level: '0'~ '3'
        mWpPrinter.SP_SetQRcodeVersion(QRCodeVersion); //設定 QRcode Version
        mWpPrinter.SP_SetPaperBackFed((byte) (10 + 45 * QrCdoeWidth)); //設定倒退dot高度 5
        mWpPrinter.SP_SetDotposition(230); //設定與起始點空白距離
        mWpPrinter.printQrCode(QR[1], alignment, model, QrCdoeWidth, true); //列印QRcode
        mWpPrinter.SP_PrintFeedPaperDot(NDOT_BARCODE_SPACE); // 列印空白點數
        mWpPrinter.SP_PrintFeedPaperDot((byte) 15);  // 列印空白點數 //Snail

        //10.店號 機 序
        //-----列印店名 -------------------
        mWpPrinter.SP_SetDotposition(30);
        String serial = Bill.fromServer.worder_id.substring(Bill.fromServer.worder_id.length() - 10);
        data = context.getString(R.string.shopId) + Config.shop_id + "  機 " + Config.kiosk_id + " 序" + serial;
        alignment = WpPrinter.ALIGNMENT_CENTER;
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false); //跳行

        //11.退貨
        alignment = WpPrinter.ALIGNMENT_LEFT; //置左排列
        mWpPrinter.SP_SetDotposition((int) 30);
        data = context.getString(R.string.eInvBottomMsg);
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串
        //------------------------------------------
        if (Bill.fromServer.getCustCode().length() > 0) {
            printPaymentDetail(context);
        } else {
            mWpPrinter.cutPaper(6, true);
        }
        return super.printEInvoice(context);
    }

    @Override
    public int printActivityMessage(Context context, String activityMessage) {
        int alignment = WpPrinter.ALIGNMENT_CENTER;
        int attribute = WpPrinter.TEXT_ATTRIBUTE_FONT_A;
        int size;
        String data;//4.內用外帶
        //2.時間
        alignment = WpPrinter.ALIGNMENT_CENTER;
        attribute = WpPrinter.TEXT_ATTRIBUTE_FONT_A;
        size = WpPrinter.TEXT_SIZE_HORIZONTAL2 | WpPrinter.TEXT_SIZE_VERTICAL2;
        mWpPrinter.SP_printBig5("好 康 兌 換", alignment, attribute, size, false); // 列印Big5字串
        size = WpPrinter.TEXT_SIZE_HORIZONTAL1 | WpPrinter.TEXT_SIZE_VERTICAL1;
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_printBig5(getTime(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        alignment = WpPrinter.ALIGNMENT_LEFT;
        data = activityMessage;
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數
        mWpPrinter.cutPaper(6, true);
        return super.printActivityMessage(context, activityMessage);
    }

    @Override
    public int printEInvoice(Context context, EReceipt receipt, ReceiptDetail detail) {
        int alignment = WpPrinter.ALIGNMENT_CENTER;
        int attribute = WpPrinter.TEXT_ATTRIBUTE_FONT_A;
        int size = WpPrinter.TEXT_SIZE_HORIZONTAL2 | WpPrinter.TEXT_SIZE_VERTICAL2;
        byte NDOT_BARCODE_SPACE = 3;
        String data;

        //--- Set Page Mode ----------------------
        mWpPrinter.SP_SetPageMode();  // 設定 page mode = ON

        String testGuiNo = context.getString(R.string.testGuiNo);
        String guiNo = receipt.getSellerGuiNumber();

        //此為測試發票
        if (testGuiNo.equals(guiNo)) {
            String testString = context.getString(R.string.testReceipt);
            mWpPrinter.SP_printBig5(testString, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);
            mWpPrinter.SP_PrintFeedPaperDot((byte) 15);
        }

        //1.logo
        String receiptTitle = receipt.getReceiptTitle();
        if (receiptTitle == null) {
            receiptTitle = "";
        }
        mWpPrinter.SP_printBig5(receiptTitle, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);
        mWpPrinter.SP_PrintFeedPaperDot((byte) 15);

        //2.電子發票證明聯
        data = context.getString(R.string.eInvPrintTitle);
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //3.發票期別
        String twYear = receipt.getTwYearString();
        String beginMonth = receipt.getStartMonth();
        String endMonth = receipt.getEndMonth();
        data = twYear + context.getString(R.string.year) + beginMonth + "-" + endMonth + context.getString(R.string.month);
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10); // 列印空白點數

        //4.發票號碼
        String invNo = receipt.getEReceiptNumber();
        data = invNo.substring(0, 2) + "-" + invNo.substring(2, 10);
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.SP_PrintFeedPaperDot((byte) 25); // 列印空白點數

        //5.列印時間
        alignment = WpPrinter.ALIGNMENT_LEFT;
        size = WpPrinter.TEXT_SIZE_HORIZONTAL1 | WpPrinter.TEXT_SIZE_VERTICAL1;
        mWpPrinter.SP_SetDotposition(30); //設定與起始點空白距離
        data = getTime() + (receipt.getBuyerGuiNumber().equals("") ? "" : "  格式25");
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //6a.列印隨機碼
        data = "隨機碼 " + receipt.getRandomNumber();
        mWpPrinter.SP_SetDotposition(30); //設定與起始點空白距離
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串

        //6.b總計
        mWpPrinter.SP_SetDotposition((int) (230)); //設定與起始點空白距離
        mWpPrinter.SP_printBig5("總計  " + receipt.getTotalAmount(), alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false); //跳行

        //7a.列印買賣方code -------------------
        data = context.getString(R.string.seller) + guiNo;
        mWpPrinter.SP_SetDotposition(30);
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串

        //7b.買方code
        mWpPrinter.SP_SetDotposition(230); //設定與起始點空白距離
        String buyerGuiNumber = receipt.getBuyerGuiNumber();
        if (buyerGuiNumber.length() > 0) {
            mWpPrinter.SP_printBig5(context.getString(R.string.buyer) + buyerGuiNumber, alignment, attribute, size, false);  // 列印Big5字串
        }
        mWpPrinter.lineFeed(1, false); //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數 //Snail

        //8.條碼
        int barCodeSystem = WpPrinter.BAR_CODE_CODE39;
        int height = 48;
        int width = 1;
        int characterPosition = WpPrinter.HRI_CHARACTER_NOT_PRINTED;
        alignment = WpPrinter.ALIGNMENT_CENTER;
        String barcodeData = receipt.getBarCodeData();
        mWpPrinter.print1dBarcode(barcodeData, barCodeSystem, alignment, width, height, characterPosition, false);
        mWpPrinter.SP_PrintFeedPaperDot(NDOT_BARCODE_SPACE); // 列印空白點數
        mWpPrinter.SP_PrintFeedPaperDot((byte) 15);  // 列印空白點數 //Snail

        // X 9.左右QRCode
        //--- 列印左邊 QrCode -------------------------
        int model = WpPrinter.QR_CODE_MODEL1;
        int QrCodeWidth = 3;
        final byte QRCodeVersion = 7;
        final int errorCorrectionLevel = '0';
        alignment = WpPrinter.ALIGNMENT_LEFT;
        mWpPrinter.SP_SelectCorrectionLevel((byte) errorCorrectionLevel); // select QRcode correction Level: '0'~ '3'
        mWpPrinter.SP_SetQRcodeVersion((byte) QRCodeVersion); //設定 QRcode Version
        mWpPrinter.SP_SetDotposition(45);   //設定與起始點空白距離
        String[] qrCodeData = receipt.getQrCodeData();
        data = qrCodeData[0];
        mWpPrinter.printQrCode(data, alignment, model, QrCodeWidth, true); //列印QRcode

        //---列印右邊 QRcode ------------------------------------------
        mWpPrinter.SP_SelectCorrectionLevel((byte) errorCorrectionLevel); // select QRcode correction Level: '0'~ '3'
        mWpPrinter.SP_SetQRcodeVersion(QRCodeVersion); //設定 QRcode Version
        mWpPrinter.SP_SetPaperBackFed((byte) (10 + 45 * QrCodeWidth)); //設定倒退dot高度 5
        mWpPrinter.SP_SetDotposition(230); //設定與起始點空白距離
        mWpPrinter.printQrCode(qrCodeData[1], alignment, model, QrCodeWidth, true); //列印QRcode
        mWpPrinter.SP_PrintFeedPaperDot(NDOT_BARCODE_SPACE); // 列印空白點數
        mWpPrinter.SP_PrintFeedPaperDot((byte) 15);  // 列印空白點數 //Snail

        //10.店號 機 序
        //-----列印店名 -------------------
        mWpPrinter.SP_SetDotposition(30);
        String serial = detail.getOrderId().substring(detail.getOrderId().length() - 10);
        data = context.getString(R.string.shopId) + Config.shop_id + "  機 " + Config.kiosk_id + " 序" + serial;
        alignment = WpPrinter.ALIGNMENT_CENTER;
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false); //跳行

        //11.退貨
        alignment = WpPrinter.ALIGNMENT_LEFT; //置左排列
        mWpPrinter.SP_SetDotposition((int) 30);
        data = context.getString(R.string.eInvBottomMsg);
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串
        //------------------------------------------
        if (receipt.getBuyerGuiNumber().length() > 0) {
            printPaymentDetail(context, receipt, detail);
        } else {
            mWpPrinter.cutPaper(6, true);
        }
        return super.printEInvoice(context, receipt, detail);
    }

    @Override
    public int printPaymentDetail(Context context) {
        int X1 = 50;
        int X2 = 150;
        int X3 = 250;
        int attribute = 0;
        int size = 0;
        String data = "";
        int alignment = WpPrinter.ALIGNMENT_CENTER;
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //0.---
        data = "-----------------------------";
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //1.交易明細
        size = WpPrinter.TEXT_SIZE_HORIZONTAL2 | WpPrinter.TEXT_SIZE_VERTICAL2;
        data = context.getString(R.string.invDetail);
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        // 5. 商品列表
        size = WpPrinter.TEXT_SIZE_HORIZONTAL1 | WpPrinter.TEXT_SIZE_VERTICAL1;
        alignment = WpPrinter.ALIGNMENT_LEFT;
        List<WebOrder01> webOrder01List = Bill.fromServer.getOrderResponse().getWeborder01();
        for (WebOrder01 webOrder01 : webOrder01List) {
            double salePrice = ComputationUtils.parseDouble(webOrder01.getSale_price());
            int qty = (int) ComputationUtils.parseDouble(webOrder01.getQty());
            mWpPrinter.SP_printBig5(webOrder01.getProd_name1(), alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
            mWpPrinter.SP_SetDotposition(X1); //設定與起始點空白距離
            mWpPrinter.SP_printBig5(qty + "", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(X2 - 50); //設定與起始點空白距離
//            data = addLeftSpaceTillLength(webOrder01.getSale_price(), 8);
//            mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(X3); //設定與起始點空白距離
            String total = qty * salePrice + "TX";
            data = addLeftSpaceTillLength(total, 8);
            mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
        }
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //6.小計
        mWpPrinter.SP_printBig5(context.getString(R.string.subtotalWithColon), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(X3); //設定與起始點空白距離
        data = addLeftSpaceTillLength(Bill.fromServer.getInvoiceAmount() + "TX", 8);
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //7.小計上---
        data = "-----------------------------";
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //8.應稅銷售
        mWpPrinter.SP_printBig5(context.getString(R.string.taxAmount), alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.SP_SetDotposition(X3); //設定與起始點空白距離
        data = addLeftSpaceTillLength(Bill.fromServer.get_unTax() + "", 8);
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //8.免稅
        mWpPrinter.SP_printBig5(context.getString(R.string.taxFreeAmt), alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.SP_SetDotposition((int) (X3)); //設定與起始點空白距離
        mWpPrinter.SP_printBig5(addLeftSpaceTillLength("0", 8), alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //9.稅額
        mWpPrinter.SP_printBig5("稅      額", alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.SP_SetDotposition(X3); //設定與起始點空白距離
        data = addLeftSpaceTillLength(Bill.fromServer.getTax() + "", 8);
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數
        //10.總計
        mWpPrinter.SP_printBig5("總      計", alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.SP_SetDotposition(X3); //設定與起始點空白距離
        data = addLeftSpaceTillLength(Bill.fromServer.getInvoiceAmount() + "", 8);
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //.11小計下---
        data = "-----------------------------";
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //.12付款方式
        List<WebOrder02> webOrder02List = Bill.fromServer.getOrderResponse().getWeborder02();
        for (WebOrder02 webOrder02 : webOrder02List) {
            String payName = getPayName(context, webOrder02.getPay_id());
            mWpPrinter.SP_printBig5(payName + ":", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(X3); //設定與起始點空白距離
            String payAmount = addLeftSpaceTillLength(removeDot(webOrder02.getAmount()), 8);
            mWpPrinter.SP_printBig5(payAmount, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
        }

        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數
        mWpPrinter.cutPaper(6, true);
        return super.printPaymentDetail(context);
    }

    @Override
    public int printPaymentDetail(Context context, EReceipt receipt, ReceiptDetail detail) {
        int X1 = 50;
        int X2 = 150;
        int X3 = 250;
        int attribute = 0;
        int size = 0;
        String data;
        int alignment = WpPrinter.ALIGNMENT_CENTER;
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //0.---
        data = "-----------------------------";
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //1.交易明細
        size = WpPrinter.TEXT_SIZE_HORIZONTAL2 | WpPrinter.TEXT_SIZE_VERTICAL2;
        data = context.getString(R.string.invDetail);
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        // 5. 商品列表
        size = WpPrinter.TEXT_SIZE_HORIZONTAL1 | WpPrinter.TEXT_SIZE_VERTICAL1;
        alignment = WpPrinter.ALIGNMENT_LEFT;
        List<Product> productList = detail.getProductList();
        for (Product product : productList) {
            int qty = product.getQuantity();
            mWpPrinter.SP_printBig5(product.getName(), alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
            mWpPrinter.SP_SetDotposition(X1); //設定與起始點空白距離
            mWpPrinter.SP_printBig5(qty + "", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(X2 - 50); //設定與起始點空白距離
//            data = addLeftSpaceTillLength(product.getUnitPrice() + "", 8);
//            mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(X3); //設定與起始點空白距離
            String total = product.getTotalPrice() + "TX";
            data = addLeftSpaceTillLength(total, 8);
            mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
        }
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //6.小計
        mWpPrinter.SP_printBig5(context.getString(R.string.subtotalWithColon), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(X3); //設定與起始點空白距離
        data = addLeftSpaceTillLength(receipt.getTotalAmount() + "TX", 8);
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //7.小計上---
        data = "-----------------------------";
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //8.應稅銷售
        mWpPrinter.SP_printBig5(context.getString(R.string.taxAmount), alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.SP_SetDotposition(X3); //設定與起始點空白距離
        data = addLeftSpaceTillLength(receipt.getUnTaxAmount(), 8);
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //8.免稅
        mWpPrinter.SP_printBig5(context.getString(R.string.taxFreeAmt), alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.SP_SetDotposition((int) (X3)); //設定與起始點空白距離
        mWpPrinter.SP_printBig5(addLeftSpaceTillLength("0", 8), alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //9.稅額
        mWpPrinter.SP_printBig5("稅      額", alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.SP_SetDotposition(X3); //設定與起始點空白距離
        data = addLeftSpaceTillLength(receipt.getTaxAmount(), 8);
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //10.總計
        mWpPrinter.SP_printBig5("總      計", alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.SP_SetDotposition(X3); //設定與起始點空白距離
        data = addLeftSpaceTillLength(detail.getTotal() + "", 8);
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //.11小計下---
        data = "-----------------------------";
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //.12付款方式
        List<Payment> paymentList = detail.getPaymentList();
        printPaymentData(paymentList, X3);

        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數
        mWpPrinter.cutPaper(6, true);
        return super.printPaymentDetail(context, receipt, detail);
    }

    @Override
    public int printReceipt(Context context) {
        OrderResponse orderResponse = Bill.fromServer.getOrderResponse();

        String strokeLine = "";
        for (int i = 1; i <= 33; i++) {
            strokeLine = strokeLine + "-";
        }

        int X2 = 120;
        int X3 = 180;
        int X4 = 280;

        mWpPrinter.SP_SetPageMode();  // 設定 page mode = ON
        int attribute = 0;
        int size = 0;
        int alignment = WpPrinter.ALIGNMENT_LEFT;
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        String data = context.getString(R.string.receipt);

        if (!BuildConfig.FLAVOR.equals("TFG")) {
            printTakeOutNumber(context, orderResponse, attribute, alignment);
            //cut
            mWpPrinter.cutPaper(6, true);
        }
        //1.收據
        data = context.getString(R.string.receipt);
        size = WpPrinter.TEXT_SIZE_HORIZONTAL2 | WpPrinter.TEXT_SIZE_VERTICAL2;
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數
        printTakeOutNumber(context, orderResponse, attribute, alignment); //列印內用外帶單號
        mWpPrinter.lineFeed(1, false);  //跳行
        //6.商品 數量 原價 小計
        size = WpPrinter.TEXT_SIZE_HORIZONTAL1 | WpPrinter.TEXT_SIZE_VERTICAL1;
        mWpPrinter.SP_printBig5(strokeLine, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_printBig5(context.getString(R.string.product), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(X3); //設定與起始點空白距離
        mWpPrinter.SP_printBig5(context.getString(R.string.quantity), alignment, attribute, size, false); // 列印Big5字串
        if(BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name())){
            mWpPrinter.SP_SetDotposition(X3); //設定與起始點空白距離
            String originPrice = context.getString(R.string.originPrice);
            mWpPrinter.SP_printBig5(addLeftSpaceTillLength(originPrice, 6), alignment, attribute, size, false); // 列印Big5字串
        }
        String price = context.getString(R.string.price);
        mWpPrinter.SP_SetDotposition(X4); //設定與起始點空白距離
        mWpPrinter.SP_printBig5(addLeftSpaceTillLength(price, 6), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        mWpPrinter.SP_printBig5(strokeLine, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        // 9~11. 商品列表
        List<WebOrder01> webOrder01List = orderResponse.getWeborder01();
        for (WebOrder01 webOrder01 : webOrder01List) {
            double salePrice = ComputationUtils.parseDouble(webOrder01.getSale_price());
            int qty = (int) ComputationUtils.parseDouble(webOrder01.getQty());
            int itemDisc = (int) ComputationUtils.parseDouble(webOrder01.getItem_disc());
            mWpPrinter.SP_printBig5(webOrder01.getProd_name1(), alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
            if (webOrder01.getTaste_memo() != null && webOrder01.getTaste_memo().length() > 0) {
                mWpPrinter.SP_printBig5("  **" + webOrder01.getTaste_memo(), alignment, attribute, size, false); // 列印Big5字串
                mWpPrinter.lineFeed(1, false);  //跳行
            }
            mWpPrinter.SP_SetDotposition(X2); //設定與起始點空白距離
            data = addLeftSpaceTillLength(qty + "", 4);
            mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(X3); //設定與起始點空白距離
//            data = addLeftSpaceTillLength(salePrice * qty + "", 8);
//            mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(X4); //設定與起始點空白距離
            data = addLeftSpaceTillLength(salePrice * qty + itemDisc + "", 8);
            mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
        }
        mWpPrinter.lineFeed(1, false);  //跳行

        mWpPrinter.SP_printBig5(strokeLine, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //12~13.總金額
        mWpPrinter.SP_printBig5(context.getString(R.string.totalAmount) + ":", alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(X4); //設定與起始點空白距離
        data = addLeftSpaceTillLength(orderResponse.getTot_sales() + "", 8);
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 30);  // 列印空白點數

        //15.實收
        mWpPrinter.SP_printBig5(context.getString(R.string.receive), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(X4); //設定與起始點空白距離
        mWpPrinter.SP_printBig5(addLeftSpaceTillLength(data + "", 8), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //16.支付
        mWpPrinter.SP_printBig5(context.getString(R.string.pay), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(X4); //設定與起始點空白距離
        mWpPrinter.SP_printBig5(addLeftSpaceTillLength(data + "", 8), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        mWpPrinter.lineFeed(1, false);

        //18~19.付款方式
        List<WebOrder02> webOrder02List = orderResponse.getWeborder02();
        for (WebOrder02 webOrder02 : webOrder02List) {
            String payName = getPayName(context, webOrder02.getPay_id());
            mWpPrinter.SP_printBig5(payName + ":", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(X4); //設定與起始點空白距離
            String payAmount = addLeftSpaceTillLength(removeDot(webOrder02.getAmount()), 8);
            mWpPrinter.SP_printBig5(payAmount, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
        }
        mWpPrinter.lineFeed(1, false);  //跳行

        //21.發票號碼
        if (Bill.fromServer.getOrderResponse().getInvoice() != null && !Config.disableReceiptModule) {
            String invNo = Bill.fromServer.getOrderResponse().getInvoice().getInv_no_b();
            mWpPrinter.SP_printBig5(context.getString(R.string.invNo) + invNo, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
        }

        //23.捐贈碼
        String npoban = Bill.fromServer.getNpoBan();
        if (npoban.trim().length() > 0) {
            mWpPrinter.SP_printBig5(context.getString(R.string.donateNo) + npoban, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
        }
        //24.載具
        String buyerNumber = Bill.fromServer.getBuyerNumber();
        if (buyerNumber.trim().length() > 0) {
            mWpPrinter.SP_printBig5(context.getString(R.string.invVehicle) + buyerNumber, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
        }

        //悠遊卡交易
        if (Bill.fromServer.getEcPayData() != null) {
            int space = 150;
            EcPayData ecPayData = Bill.fromServer.getEcPayData();
            //------------
            mWpPrinter.SP_printBig5("-----悠遊卡交易明細-----", alignment, attribute, size, false);  // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行

            //交易時間
            String date = ecPayData.getTxn_Date();
            String time = ecPayData.getTxn_Time();
            String printDate = date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6);
            String printTime = time.substring(0, 2) + ":" + time.substring(2, 4) + ":" + time.substring(4);
            mWpPrinter.SP_printBig5("交易時間", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
            mWpPrinter.SP_printBig5(":" + printDate + " " + printTime, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行

            //悠遊卡卡號
            mWpPrinter.SP_printBig5("悠遊卡卡號", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
            mWpPrinter.SP_printBig5(":" + ecPayData.getReceipt_card_number(), alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行

            //交易類別
            mWpPrinter.SP_printBig5("交易類別", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
            mWpPrinter.SP_printBig5(":悠遊卡扣款", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行

            //二代設備編號
            mWpPrinter.SP_printBig5("二代設備編號", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
            mWpPrinter.SP_printBig5(":" + ecPayData.getNew_deviceID(), alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行

            //批次號碼
            mWpPrinter.SP_printBig5("批次號碼", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
            mWpPrinter.SP_printBig5(":" + ecPayData.getBatch_number(), alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行

            //RRN
            mWpPrinter.SP_printBig5("RRN", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
            mWpPrinter.SP_printBig5(":" + ecPayData.getRRN(), alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行

            //交易前餘額
            mWpPrinter.SP_printBig5("交易前餘額", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
            mWpPrinter.SP_printBig5(":" + ecPayData.getN_CPU_EV_Before_TXN(), alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行

            //自動加值金額
            mWpPrinter.SP_printBig5("自動加值金額", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
            mWpPrinter.SP_printBig5(":" + ecPayData.getN_AutoLoad_Amount(), alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行

            //交易金額
            mWpPrinter.SP_printBig5("交易金額", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
            mWpPrinter.SP_printBig5(":" + ecPayData.getN_CPU_Txn_AMT(), alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行

            //交易後餘額
            mWpPrinter.SP_printBig5("交易後餘額", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
            mWpPrinter.SP_printBig5(":" + ecPayData.getN_CPU_EV(), alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
        }

        if (Bill.fromServer.getNcccTransDataBean() != null) {
            printNCCCTransData(Bill.fromServer.getNcccTransDataBean());
        }

        if(OrderManager.getInstance().isLogin() || OrderManager.getInstance().isGuestLogin()){
            int space = 150;
            mWpPrinter.SP_printBig5("本次新增點數", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
            mWpPrinter.SP_printBig5(":" + OrderManager.getInstance().getPoints().getAdd(), alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行

            mWpPrinter.SP_printBig5("過去累積點數", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
            mWpPrinter.SP_printBig5(":" + OrderManager.getInstance().getPoints().getBefore_transaction(), alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行

            mWpPrinter.SP_printBig5("本次使用點數", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
            mWpPrinter.SP_printBig5(":" + String.valueOf(OrderManager.getInstance().getPoints().getUse()), alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行

            mWpPrinter.SP_printBig5("交易剩餘點數", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
            mWpPrinter.SP_printBig5(":" + OrderManager.getInstance().getPoints().getAfter_transaction(), alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
        }

        //26.----
        mWpPrinter.SP_printBig5(strokeLine, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //27.此單據內容僅供參考，不作為交易憑證
        mWpPrinter.SP_printBig5(context.getString(R.string.receiptBottomMsg), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //28.time
        mWpPrinter.SP_printBig5(getTime(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //29.barCode
        int barCodeSystem = WpPrinter.BAR_CODE_CODE128;
        int height = 60;
        int width = 1;
        int characterPosition = WpPrinter.HRI_CHARACTER_NOT_PRINTED;
        alignment = WpPrinter.ALIGNMENT_CENTER;
        data = Bill.fromServer.worder_id;
        mWpPrinter.print1dBarcode(data, barCodeSystem, alignment, width, height, characterPosition, false);
        mWpPrinter.SP_PrintFeedPaperDot((byte) 4); // 列印空白點數

        //30.QRCode
        int model = WpPrinter.QR_CODE_MODEL1;
        int QrCdoeWidth = 4;

        final byte QRCodeVersion = 7;
        final int errorCorrectionLevel = '0';
        alignment = WpPrinter.ALIGNMENT_CENTER;
        mWpPrinter.SP_SelectCorrectionLevel((byte) errorCorrectionLevel); // select QRcode correction Level: '0'~ '3'
        mWpPrinter.SP_SetQRcodeVersion((byte) QRCodeVersion); //設定 QRcode Version
        mWpPrinter.printQrCode(Bill.fromServer.worder_id, alignment, model, QrCdoeWidth, true); //列印QRcode
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //end
        mWpPrinter.cutPaper(6, true);
        return super.printReceipt(context);
    }

    @Override
    public int printReceipt(Context context, EReceipt receipt, ReceiptDetail detail) {
        printTakeOutNumber(context, detail);//列印內用外帶單號
        //cut
        mWpPrinter.cutPaper(6, true);

        StringBuilder strokeLineBuilder = new StringBuilder();
        for (int i = 1; i <= 33; i++) {
            strokeLineBuilder.append("-");
        }
        String strokeLine = strokeLineBuilder.toString();
        String data;
        int X2 = 120;
        int X3 = 140;
        int X4 = 280;

        mWpPrinter.SP_SetPageMode();  // 設定 page mode = ON
        int attribute = 0;
        int size;
        int alignment = WpPrinter.ALIGNMENT_LEFT;
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //1.收據
        if (BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name())) {
            if (Config.isShopping) {
                data = "佐美館 " + context.getString(R.string.shopping) + context.getString(R.string.receipt);
            } else if (Config.isRestaurant) {
                data = "佐美館 " + context.getString(R.string.restaurant) + context.getString(R.string.receipt);
            } else {
                data = "佐美館 " + context.getString(R.string.receipt);
            }
        } else {
            data = context.getString(R.string.receipt);
        }
        size = WpPrinter.TEXT_SIZE_HORIZONTAL2 | WpPrinter.TEXT_SIZE_VERTICAL2;
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //5.取餐號碼
        if(BuildConfig.FLAVOR.equals("TFG")) {
            data = context.getString(R.string.takeMealNo) + "P"+Bill.fromServer.worder_id.substring(Bill.fromServer.worder_id.length()-4);
        }else if (BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name())) {
            if (Config.isShopping) {
                data = "取貨方式:" + detail.getPickupMethod().getName();
            } else {
                data = "外帶";
            }
        }
        else {
            if(Config.isNewOrderApi){
                data = context.getString(R.string.takeMealNo) + detail.getTakeMealNumber();
            }else{
                data = context.getString(R.string.takeMealNo) + Bill.fromServer.getOrderResponse().getTakeno();
            }
        }

        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //6.商品 數量 原價 小計
        size = WpPrinter.TEXT_SIZE_HORIZONTAL1 | WpPrinter.TEXT_SIZE_VERTICAL1;
        mWpPrinter.SP_printBig5(strokeLine, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_printBig5(context.getString(R.string.product), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(X3); //設定與起始點空白距離
        mWpPrinter.SP_printBig5(context.getString(R.string.quantity), alignment, attribute, size, false); // 列印Big5字串
        if(BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name())) {
            mWpPrinter.SP_SetDotposition(X3); //設定與起始點空白距離
            String originPrice = context.getString(R.string.originPrice);
            mWpPrinter.SP_printBig5(addLeftSpaceTillLength(originPrice, 6), alignment, attribute, size, false); // 列印Big5字串
        }
        String price = context.getString(R.string.price);
        mWpPrinter.SP_SetDotposition(X4); //設定與起始點空白距離
        mWpPrinter.SP_printBig5(addLeftSpaceTillLength(price, 6), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        mWpPrinter.SP_printBig5(strokeLine, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        // 9~11. 商品列表
        List<Product> productList = detail.getProductList();
        printProductDetail(productList);

        mWpPrinter.SP_printBig5(strokeLine, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //8.合計
        mWpPrinter.SP_printBig5(context.getString(R.string.totalAmount), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition((int) (X4)); //設定與起始點空白距離
        String total = FormatUtil.removeDot(detail.getTotal()) + "";
        mWpPrinter.SP_printBig5(addLeftSpaceTillLength(total, 8), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        double cashTicketAmount = 0.0;
        //8.折讓
        for (Payment payment : detail.getPaymentList()) {
            if(payment.getType().equals("CASH_TICKET")){
                cashTicketAmount = payment.getAmount() * 1.0;
            }
        }
        mWpPrinter.SP_printBig5(context.getString(R.string.discount), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition((int) (X4)); //設定與起始點空白距離
        String discount = FormatUtil.removeDot(detail.getTotalFee() - detail.getTotalDischarge() + cashTicketAmount - detail.getTotal()) + "";
        mWpPrinter.SP_printBig5(addLeftSpaceTillLength(discount, 8), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //8.金額
        mWpPrinter.SP_printBig5(context.getString(R.string.price), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition((int) (X4)); //設定與起始點空白距離
        mWpPrinter.SP_printBig5(addLeftSpaceTillLength(FormatUtil.removeDot(detail.getTotal() - detail.getDiscount()), 8), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        mWpPrinter.lineFeed(1, false);  //跳行

        //14.尾數折讓
        if (detail.getTotalDischarge() != 0) {
            mWpPrinter.SP_printBig5(context.getString(R.string.tailDiscount) + ":", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(X4); //設定與起始點空白距離
            String TotalDischarge = addLeftSpaceTillLength(detail.getTotalDischarge() + "", 8);
            mWpPrinter.SP_printBig5(TotalDischarge, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
            mWpPrinter.SP_PrintFeedPaperDot((byte) 30);  // 列印空白點數
        }

        data = addLeftSpaceTillLength(FormatUtil.removeDot(detail.getTotalFee() + cashTicketAmount) + "", 8);
        //15.實收
        mWpPrinter.SP_printBig5(context.getString(R.string.receive), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(X4); //設定與起始點空白距離
        mWpPrinter.SP_printBig5(addLeftSpaceTillLength(data + "", 8), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //16.支付
        mWpPrinter.SP_printBig5(context.getString(R.string.pay), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(X4); //設定與起始點空白距離
        mWpPrinter.SP_printBig5(addLeftSpaceTillLength(data + "", 8), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        mWpPrinter.lineFeed(1, false);

        //18~19.付款方式
        List<Payment> paymentList = detail.getPaymentList();
        printPaymentData(paymentList, X4);

        //21.發票號碼
        if (receipt != null && !Config.disableReceiptModule) {
            String invNo = receipt.getEReceiptNumber();
            mWpPrinter.SP_printBig5(context.getString(R.string.invNo) + invNo, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
        }

        //23.捐贈碼
        if (receipt != null && receipt.getLoveCode() != null && receipt.getLoveCode().length() > 0) {
            String loveCode = receipt.getLoveCode();
            mWpPrinter.SP_printBig5(context.getString(R.string.donateNo) + loveCode, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
        }
        //24.載具
        if (receipt != null && receipt.getCarrierNumber() != null && receipt.getCarrierNumber().length() > 0) {
            String carrierNumber = receipt.getCarrierNumber();
            mWpPrinter.SP_printBig5(context.getString(R.string.invVehicle) + carrierNumber, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
        }

        //24.統一編號
        if (receipt != null && receipt.getBuyerGuiNumber() != null && receipt.getBuyerGuiNumber().length() > 0) {
            String buyerNumber = receipt.getBuyerGuiNumber();
            mWpPrinter.SP_printBig5(context.getString(R.string.buyerNumber) + buyerNumber, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
        }

        //悠遊卡交易
        if (detail.getEcPayData() != null) {
            printEcSaleData(detail.getEcPayData());
        }

        if (detail.getNcccTransDataBean() != null) {
            printNCCCTransData(detail.getNcccTransDataBean());
        }


        if(OrderManager.getInstance().isLogin() || OrderManager.getInstance().isGuestLogin()){
            mWpPrinter.SP_printBig5("----- 紅利點數收據明細 -----", alignment, attribute, size, false);  // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
            int space = 150;
            mWpPrinter.SP_printBig5("本次新增點數", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
            mWpPrinter.SP_printBig5(":" + OrderManager.getInstance().getPoints().getAdd(), alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行

            mWpPrinter.SP_printBig5("過去累積點數", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
            mWpPrinter.SP_printBig5(":" + OrderManager.getInstance().getPoints().getBefore_transaction(), alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行

            mWpPrinter.SP_printBig5("本次使用點數", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
            mWpPrinter.SP_printBig5(":" + String.valueOf(OrderManager.getInstance().getPoints().getUse()), alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行

            mWpPrinter.SP_printBig5("交易剩餘點數", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
            mWpPrinter.SP_printBig5(":" + OrderManager.getInstance().getPoints().getAfter_transaction(), alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
        }
        //佐登妮絲提示訊息
        if (BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name())) {
            mWpPrinter.SP_printBig5(strokeLine, alignment, attribute, size, false);  // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
            if (Config.isShopping) {
                data = context.getString(R.string.unpickedUp) + ", " + context.getString(R.string.saveReceipt);
            } else if (Config.isRestaurant) {
                data = context.getString(R.string.untakenMeal) + ", " + context.getString(R.string.saveReceipt);
            } else {
                data = context.getString(R.string.saveReceipt);
            }
            mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
            mWpPrinter.SP_printBig5(context.getString(R.string.goToCounter), WpPrinter.ALIGNMENT_CENTER, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
        }
        //26.----
        mWpPrinter.SP_printBig5(strokeLine, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //27.此單據內容僅供參考，不作為交易憑證
        mWpPrinter.SP_printBig5(context.getString(R.string.receiptBottomMsg), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //28.time
        mWpPrinter.SP_printBig5(getTime(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //29.barCode
        int barCodeSystem = WpPrinter.BAR_CODE_CODE128;
        int height = 60;
        int width = 1;
        int characterPosition = WpPrinter.HRI_CHARACTER_NOT_PRINTED;
        alignment = WpPrinter.ALIGNMENT_CENTER;
        data = detail.getOrderId();
//        if(!OrderManager.getInstance().isLogin() || OrderManager.getInstance().isGuestLogin()) {
//            mWpPrinter.print1dBarcode(data, barCodeSystem, alignment, width, height, characterPosition, false);
//            mWpPrinter.SP_PrintFeedPaperDot((byte) 4); // 列印空白點數
//        }

        size = WpPrinter.TEXT_SIZE_HORIZONTAL2 | WpPrinter.TEXT_SIZE_VERTICAL2;
        //28.time
        if((!BasicSettingsManager.Companion.getInstance().getBasicSetting().getRegister_url().isEmpty())) {
            mWpPrinter.SP_printBig5("立即加入會員", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
            mWpPrinter.lineFeed(1, false);  //跳行
        }

        //30.QRCode
        int model = WpPrinter.QR_CODE_MODEL1;
        int QrCdoeWidth = 4;

        final byte QRCodeVersion = 7;
        final int errorCorrectionLevel = '0';
        mWpPrinter.SP_SelectCorrectionLevel((byte) errorCorrectionLevel); // select QRcode correction Level: '0'~ '3'
        mWpPrinter.SP_SetQRcodeVersion((byte) QRCodeVersion); //設定 QRcode Version
        if ((!BasicSettingsManager.Companion.getInstance().getBasicSetting().getRegister_url().isEmpty())) {
            mWpPrinter.printQrCode(BasicSettingsManager.Companion.getInstance().getBasicSetting().getRegister_url(), alignment, model, QrCdoeWidth, true); //列印QRcode
            mWpPrinter.lineFeed(1, false);  //跳行
        }
        BasicSettings basicSettings = BasicSettingsManager.getInstance().getBasicSetting();
        if (basicSettings != null && basicSettings.getPrint_order_qrcode()) {
            mWpPrinter.SP_printBig5("訂單QR Code", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
            mWpPrinter.lineFeed(1, false);  //跳行
            mWpPrinter.printQrCode(detail.getOrderId(), alignment, model, QrCdoeWidth, true); //列印QRcode
            mWpPrinter.lineFeed(1, false);  //跳行
        }

        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //end
        mWpPrinter.cutPaper(6, true);
        return super.printReceipt(context, receipt, detail);
    }

    private void printProductDetail(List<Product> productList) {
        int X2 = 200;
        int X3 = 300;
        int X4 = 280;
        int attribute = 0;
        int size = WpPrinter.TEXT_SIZE_HORIZONTAL1 | WpPrinter.TEXT_SIZE_VERTICAL1;
        int alignment = WpPrinter.ALIGNMENT_LEFT;
        for (Product product : productList) {
            int qty = product.getQuantity();
            String usePointMsg = product.getIsUsePoint() ? "(點數兌換)": "";
            String useTicketMsg = product.getTicketNo() != null ? "(商品券兌換)": "";
            String onSale = product.getIsOnSale()? "(特價)": "";
            mWpPrinter.lineFeed(1, false);  //跳行
            if(!useTicketMsg.isEmpty())
                mWpPrinter.SP_printBig5(product.getName() + useTicketMsg, alignment, attribute, size, false); /* 列印Big5字串*/
            else if(!usePointMsg.isEmpty())
                mWpPrinter.SP_printBig5(product.getName()+usePointMsg, alignment, attribute, size, false); // 列印Big5字串
            else if (!onSale.isEmpty())
                mWpPrinter.SP_printBig5(product.getName()+onSale, alignment, attribute, size, false); // 列印Big5字串
            else
                mWpPrinter.SP_printBig5(product.getName(), alignment, attribute, size, false); // 列印Big5字串

            mWpPrinter.lineFeed(1, false);  //跳行
            if (product.getMemo() != null && product.getMemo().length() > 0) {
                mWpPrinter.SP_printBig5("  **" + product.getMemo(), alignment, attribute, size, false); // 列印Big5字串
                mWpPrinter.lineFeed(1, false);  //跳行
            }
            mWpPrinter.SP_SetDotposition(X2); //設定與起始點空白距離
            mWpPrinter.SP_printBig5(qty + "", alignment, attribute, size, false); // 列印Big5字串
            if(BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name())) {
                mWpPrinter.SP_SetDotposition(X3); //設定與起始點空白距離
                String unitPrice = FormatUtil.removeDot(product.getUnitPrice()) + "";
                mWpPrinter.SP_printBig5(addLeftSpaceTillLength(unitPrice, 8), alignment, attribute, size, false); // 列印Big5字串
            }
            String totalPrice = FormatUtil.removeDot(product.getTotalPrice()) + "";
            mWpPrinter.SP_SetDotposition(X3); //設定與起始點空白距離
            mWpPrinter.SP_printBig5(addLeftSpaceTillLength(totalPrice, 8), alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
        }
        mWpPrinter.lineFeed(1, false);  //跳行
    }

    private void printPaymentData(List<Payment> paymentList, int space) {
        int attribute = 0;
        int size = WpPrinter.TEXT_SIZE_HORIZONTAL1 | WpPrinter.TEXT_SIZE_VERTICAL1;
        int alignment = WpPrinter.ALIGNMENT_LEFT;
        for (Payment payment : paymentList) {
            mWpPrinter.SP_printBig5(payment.getName() + ":", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(space); //設定與起始點空白距離
            String payAmount = addLeftSpaceTillLength(payment.getAmount() + "", 8);
            mWpPrinter.SP_printBig5(payAmount, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
        }
    }

    private void printTakeOutNumber(Context context, OrderResponse orderResponse, int attribute, int alignment) {
        int size;
        String data;//4.內用外帶
        //2.時間
        alignment = WpPrinter.ALIGNMENT_LEFT;
        attribute = WpPrinter.TEXT_ATTRIBUTE_FONT_A;
        size = WpPrinter.TEXT_SIZE_HORIZONTAL1 | WpPrinter.TEXT_SIZE_VERTICAL1;
        mWpPrinter.SP_printBig5(getTime(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        //X 3??
        data = Config.shop_id + "-" + Config.kiosk_id + "-" + Bill.fromServer.worder_id;
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        if (BuildConfig.FLAVOR.equals("TFG")) {
            data = Config.shopName;
            mWpPrinter.SP_printBig5("店名：" + data, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
            mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數
        }

        size = WpPrinter.TEXT_SIZE_HORIZONTAL2 | WpPrinter.TEXT_SIZE_VERTICAL2;
        data = Bill.fromServer.getInOut_String(context);

        if (orderResponse.getTable_no() != null && orderResponse.getTable_no().length() > 0) {
            data += "/桌號-" + orderResponse.getTable_no();
        }

        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //5.取餐號碼
        if (BuildConfig.FLAVOR.equals("TFG")) {
            data = context.getString(R.string.takeMealNo) + "P" +Bill.fromServer.worder_id.substring(Bill.fromServer.worder_id.length() - 4);
        } else {
            data = context.getString(R.string.takeMealNo) + Bill.fromServer.getOrderResponse().getTakeno();
        }

        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
    }

    private void printTakeOutNumber(Context context, ReceiptDetail detail) {
        int size;
        String data;//4.內用外帶
        //2.時間
        int alignment = WpPrinter.ALIGNMENT_LEFT;
        int attribute = WpPrinter.TEXT_ATTRIBUTE_FONT_A;
        size = WpPrinter.TEXT_SIZE_HORIZONTAL1 | WpPrinter.TEXT_SIZE_VERTICAL1;
        mWpPrinter.SP_printBig5(getTime(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        //X 3??
        data = Config.shop_id + "-" + Config.kiosk_id + "-" + detail.getOrderId();
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        if (BuildConfig.FLAVOR.equals("TFG")) {
            data = Config.shopName;
            mWpPrinter.SP_printBig5("店名："+data, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
            mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數
        }

        size = WpPrinter.TEXT_SIZE_HORIZONTAL2 | WpPrinter.TEXT_SIZE_VERTICAL2;
        data = detail.getSaleType();
        if (BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name()) && Config.isShopping) {
            data = "取貨方式:" + detail.getPickupMethod().getName();
        }
        if (detail.getTableNumber() != null && detail.getTableNumber().length() > 0) {
            data += "/桌號-" + detail.getTableNumber();
        }

        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數
        if (!BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name())) {
            //5.取餐號碼
            data = context.getString(R.string.takeMealNo) + detail.getTakeMealNumber();
            mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
        }
    }

    private void printEcSaleData(EcPayData ecPayData) {
        int attribute = 0;
        int alignment = WpPrinter.ALIGNMENT_LEFT;
        int size = WpPrinter.TEXT_SIZE_HORIZONTAL1 | WpPrinter.TEXT_SIZE_VERTICAL1;
        int space = 150;
        //------------
        mWpPrinter.SP_printBig5("-----悠遊卡交易明細-----", alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //交易時間
        String date = ecPayData.getTxn_Date();
        String time = ecPayData.getTxn_Time();
        String printDate = date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6);
        String printTime = time.substring(0, 2) + ":" + time.substring(2, 4) + ":" + time.substring(4);
        mWpPrinter.SP_printBig5("交易時間", alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
        mWpPrinter.SP_printBig5(":" + printDate + " " + printTime, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //悠遊卡卡號
        mWpPrinter.SP_printBig5("悠遊卡卡號", alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
        mWpPrinter.SP_printBig5(":" + ecPayData.getReceipt_card_number(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //交易類別
        mWpPrinter.SP_printBig5("交易類別", alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
        mWpPrinter.SP_printBig5(":悠遊卡扣款", alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //二代設備編號
        mWpPrinter.SP_printBig5("二代設備編號", alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
        mWpPrinter.SP_printBig5(":" + ecPayData.getNew_deviceID(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //批次號碼
        mWpPrinter.SP_printBig5("批次號碼", alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
        mWpPrinter.SP_printBig5(":" + ecPayData.getBatch_number(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //RRN
        mWpPrinter.SP_printBig5("RRN", alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
        mWpPrinter.SP_printBig5(":" + ecPayData.getRRN(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //交易前餘額
        mWpPrinter.SP_printBig5("交易前餘額", alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
        mWpPrinter.SP_printBig5(":" + ecPayData.getN_CPU_EV_Before_TXN(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //自動加值金額
        mWpPrinter.SP_printBig5("自動加值金額", alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
        mWpPrinter.SP_printBig5(":" + ecPayData.getN_AutoLoad_Amount(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //交易金額
        mWpPrinter.SP_printBig5("交易金額", alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
        mWpPrinter.SP_printBig5(":" + ecPayData.getN_CPU_Txn_AMT(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //交易後餘額
        mWpPrinter.SP_printBig5("交易後餘額", alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
        mWpPrinter.SP_printBig5(":" + ecPayData.getN_CPU_EV(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
    }

    private void printNCCCTransData(NCCCTransDataBean dataBean) {
        int attribute = 0;
        int size = 0;
        int alignment = WpPrinter.ALIGNMENT_LEFT;

        String title;
        //------------
        title = "E".equals(dataBean.getEsvcIndicator()) ? "-----悠遊卡交易明細-----" : "-----信用卡交易明細-----";
        mWpPrinter.SP_printBig5(title, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        mWpPrinter.SP_printBig5("聯合信用卡處理中心", alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        String transactionTime = "交易日期: " + dataBean.getTransDate() + dataBean.getTransTime();
        mWpPrinter.SP_printBig5(transactionTime, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        String terminalId = dataBean.getTerminalId();
        mWpPrinter.SP_printBig5("端末機: " + terminalId, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        String receiptNo = dataBean.getReceiptNo();
        String approvalNo = dataBean.getApprovalNo().trim();
        mWpPrinter.SP_printBig5("調閱編號: " + receiptNo + "  授權碼: " + approvalNo, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        String cardName = NCCCCode.getCardTypeName(dataBean.getCardType());
        mWpPrinter.SP_printBig5("卡別: " + cardName, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        String cardNo = dataBean.getCardNo();
        mWpPrinter.SP_printBig5("卡號: " + cardNo, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        String transAmount = dataBean.getTransAmount();
        String payAmount = "$" + NCCCTransDataBean.parseAmount(transAmount);
        mWpPrinter.SP_printBig5("交易金額: " + payAmount, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
    }

    @Override
    public int printBill(Context context) {
        int X2 = 120;
        int X3 = 180;
        int X4 = 280;
        mWpPrinter.SP_SetPageMode();  // 設定 page mode = ON
        int attribute = 0;
        int size = 0;
        int alignment = WpPrinter.ALIGNMENT_LEFT;
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        OrderResponse orderResponse = Bill.fromServer.getOrderResponse();

        //1.付款單
        alignment = WpPrinter.ALIGNMENT_CENTER;
        size = WpPrinter.TEXT_SIZE_HORIZONTAL2 | WpPrinter.TEXT_SIZE_VERTICAL2;
        String data = context.getString(R.string.bill);
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //2.時間
        alignment = WpPrinter.ALIGNMENT_LEFT;
        attribute = WpPrinter.TEXT_ATTRIBUTE_FONT_A;
        size = WpPrinter.TEXT_SIZE_HORIZONTAL1 | WpPrinter.TEXT_SIZE_VERTICAL1;
        mWpPrinter.SP_printBig5(getTime(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行


        //X 3??
        data = Config.shop_id + "-" + Config.kiosk_id + "-" + Bill.fromServer.worder_id;
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //4.內用外帶
        size = WpPrinter.TEXT_SIZE_HORIZONTAL2 | WpPrinter.TEXT_SIZE_VERTICAL2;
        data = Bill.fromServer.getInOut_String(context);
        if (orderResponse.getTable_no() != null && orderResponse.getTable_no().length() > 0) {
            data += "/桌號-" + orderResponse.getTable_no();
        }

        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //6.商品 數量 原價 小計
        size = WpPrinter.TEXT_SIZE_HORIZONTAL1 | WpPrinter.TEXT_SIZE_VERTICAL1;
        data = "------------------------------";
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_printBig5(context.getString(R.string.product), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(X3); //設定與起始點空白距離
        mWpPrinter.SP_printBig5(context.getString(R.string.quantity), alignment, attribute, size, false); // 列印Big5字串
//        mWpPrinter.SP_SetDotposition(X3); //設定與起始點空白距離
//        mWpPrinter.SP_printBig5(addLeftSpaceTillLength(context.getString(R.string.originPrice), 6), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(X4); //設定與起始點空白距離
        mWpPrinter.SP_printBig5(addLeftSpaceTillLength(context.getString(R.string.price), 6), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        data = "------------------------------";
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        // 7. 商品列表
        List<WebOrder01> webOrder01List = orderResponse.getWeborder01();
        for (WebOrder01 webOrder01 : webOrder01List) {
            double salePrice = ComputationUtils.parseDouble(webOrder01.getSale_price());
            int qty = (int) ComputationUtils.parseDouble(webOrder01.getQty());
            int itemDisc = (int) ComputationUtils.parseDouble(webOrder01.getItem_disc());
            mWpPrinter.SP_printBig5(webOrder01.getProd_name1(), alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
            if (webOrder01.getTaste_memo() != null && webOrder01.getTaste_memo().length() > 0) {
                mWpPrinter.SP_printBig5("  **" + webOrder01.getTaste_memo(), alignment, attribute, size, false); // 列印Big5字串
                mWpPrinter.lineFeed(1, false);  //跳行
            }
            mWpPrinter.SP_SetDotposition(X2); //設定與起始點空白距離
            mWpPrinter.SP_printBig5(qty + "", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(X3); //設定與起始點空白距離
            String price = salePrice * qty + "";
            mWpPrinter.SP_printBig5(addLeftSpaceTillLength(price, 8), alignment, attribute, size, false); // 列印Big5字串
            price = salePrice * qty + itemDisc + "";
            mWpPrinter.SP_SetDotposition(X4); //設定與起始點空白距離
            mWpPrinter.SP_printBig5(addLeftSpaceTillLength(price, 8), alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
        }
        mWpPrinter.lineFeed(1, false);  //跳行
        data = "------------------------------";
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //8.總金額
        mWpPrinter.SP_printBig5(context.getString(R.string.totalAmount), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition((int) (X4)); //設定與起始點空白距離
        try {
            String total = Bill.fromServer.getOrderResponse().getTotal() + "";
            mWpPrinter.SP_printBig5(addLeftSpaceTillLength(total, 8), alignment, attribute, size, false); // 列印Big5字串
        } catch (Exception e) {
            e.printStackTrace();
        }
        mWpPrinter.lineFeed(1, false);  //跳行

        //14.尾數折讓
        if (orderResponse.getDiscount() != 0) {
            mWpPrinter.SP_printBig5(context.getString(R.string.tailDiscount) + ":", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(X4); //設定與起始點空白距離
            String discount = addLeftSpaceTillLength(orderResponse.getDiscount() + "", 8);
            mWpPrinter.SP_printBig5(discount, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
            mWpPrinter.SP_PrintFeedPaperDot((byte) 30);  // 列印空白點數
        }

        //付款方式
        List<WebOrder02> webOrder02List = orderResponse.getWeborder02();
        if (webOrder02List != null) {
            for (WebOrder02 webOrder02 : webOrder02List) {
                String payName = getPayName(context, webOrder02.getPay_id());
                mWpPrinter.SP_printBig5(payName + ":", alignment, attribute, size, false); // 列印Big5字串
                mWpPrinter.SP_SetDotposition(X4); //設定與起始點空白距離
                String payAmount = addLeftSpaceTillLength(removeDot(webOrder02.getAmount()), 8);
                mWpPrinter.SP_printBig5(payAmount, alignment, attribute, size, false); // 列印Big5字串
                mWpPrinter.lineFeed(1, false);  //跳行
            }
        }

        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //9.請至櫃檯結帳，謝謝您
        mWpPrinter.SP_printBig5(context.getString(R.string.plsPayAtCounter), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //11.barCode
        int barCodeSystem = WpPrinter.BAR_CODE_CODE128;

        int height = 60;
        int width = 1;
        int characterPosition = WpPrinter.HRI_CHARACTER_NOT_PRINTED;
        alignment = WpPrinter.ALIGNMENT_CENTER;
        data = Bill.fromServer.worder_id;
        mWpPrinter.print1dBarcode(data, barCodeSystem, alignment, width, height, characterPosition, false);
        mWpPrinter.SP_PrintFeedPaperDot((byte) 4); // 列印空白點數
        mWpPrinter.lineFeed(1, false);  //跳行

        //12.QRCode
        int model = WpPrinter.QR_CODE_MODEL1;
        int QrCdoeWidth = 4;

        final byte QRCodeVersion = 7;
        final int errorCorrectionLevel = '0';
        alignment = WpPrinter.ALIGNMENT_CENTER;
        mWpPrinter.SP_SelectCorrectionLevel((byte) errorCorrectionLevel); // select QRcode correction Level: '0'~ '3'
        mWpPrinter.SP_SetQRcodeVersion((byte) QRCodeVersion); //設定 QRcode Version
        mWpPrinter.printQrCode(Bill.fromServer.worder_id, alignment, model, QrCdoeWidth, true); //列印QRcode
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //end
        mWpPrinter.cutPaper(6, true);

        return super.printBill(context);
    }

    @Override
    public int printBill(Context context, ReceiptDetail detail) {
        int X2 = 120;
        int X3 = 180;
        int X4 = 300;
        mWpPrinter.SP_SetPageMode();  // 設定 page mode = ON
        int attribute = 0;
        int size;
        int alignment;
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //1.付款單
        alignment = WpPrinter.ALIGNMENT_CENTER;
        size = WpPrinter.TEXT_SIZE_HORIZONTAL2 | WpPrinter.TEXT_SIZE_VERTICAL2;
        String data = context.getString(R.string.bill);
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //2.時間
        alignment = WpPrinter.ALIGNMENT_LEFT;
        attribute = WpPrinter.TEXT_ATTRIBUTE_FONT_A;
        size = WpPrinter.TEXT_SIZE_HORIZONTAL1 | WpPrinter.TEXT_SIZE_VERTICAL1;
        mWpPrinter.SP_printBig5(getTime(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //X 3??
        data = Config.shop_id + "-" + Config.kiosk_id + "-" + detail.getOrderId();
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //4.內用外帶
        size = WpPrinter.TEXT_SIZE_HORIZONTAL2 | WpPrinter.TEXT_SIZE_VERTICAL2;
        data = detail.getSaleType();
        if (detail.getTableNumber() != null && detail.getTableNumber().length() > 0) {
            data += "/桌號-" + detail.getTableNumber();
        }

        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //6.商品 數量 原價 小計
        size = WpPrinter.TEXT_SIZE_HORIZONTAL1 | WpPrinter.TEXT_SIZE_VERTICAL1;
        data = "------------------------------";
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_printBig5(context.getString(R.string.product), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(X3); //設定與起始點空白距離
        mWpPrinter.SP_printBig5(context.getString(R.string.quantity), alignment, attribute, size, false); // 列印Big5字串
//        mWpPrinter.SP_SetDotposition(X3); //設定與起始點空白距離
//        mWpPrinter.SP_printBig5(addLeftSpaceTillLength(context.getString(R.string.originPrice), 6), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(X4); //設定與起始點空白距離
        mWpPrinter.SP_printBig5(addLeftSpaceTillLength(context.getString(R.string.price), 6), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        data = "------------------------------";
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        // 7. 商品列表
        List<Product> productList = detail.getProductList();
        printProductDetail(productList);

        data = "------------------------------";
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //8.合計
        mWpPrinter.SP_printBig5(context.getString(R.string.totalAmount), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition((int) (X4)); //設定與起始點空白距離
        String total = FormatUtil.removeDot(detail.getTotal()) + "";
        mWpPrinter.SP_printBig5(addLeftSpaceTillLength(total, 8), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //8.折讓
        double cashTicketAmount = 0.0;
        //8.折讓
        for (Payment payment : detail.getPaymentList()) {
            if(payment.getType().equals("CASH_TICKET")){
                cashTicketAmount = payment.getAmount() * 1.0;
            }
        }
        double discount = detail.getTotalFee() - detail.getTotalDischarge() + cashTicketAmount - detail.getTotal();
        if(discount > 0.0) {
            mWpPrinter.SP_printBig5(context.getString(R.string.discount), alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition((int) (X4)); //設定與起始點空白距離
            mWpPrinter.SP_printBig5(addLeftSpaceTillLength(FormatUtil.removeDot(discount), 8), alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
        }

        //8.金額
        mWpPrinter.SP_printBig5(context.getString(R.string.price), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition((int) (X4)); //設定與起始點空白距離
        String totalFee = FormatUtil.removeDot(detail.getTotalFee());
        mWpPrinter.SP_printBig5(addLeftSpaceTillLength(FormatUtil.removeDot(detail.getTotal() - detail.getDiscount()), 8), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        mWpPrinter.lineFeed(1, false);  //跳行

        //14.尾數折讓
        if (detail.getTotalDischarge() != 0) {
            mWpPrinter.SP_printBig5(context.getString(R.string.tailDiscount) + ":", alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.SP_SetDotposition(X4); //設定與起始點空白距離
            String TotalDischarge = addLeftSpaceTillLength(detail.getTotalDischarge() + "", 8);
            mWpPrinter.SP_printBig5(TotalDischarge, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);  //跳行
            mWpPrinter.SP_PrintFeedPaperDot((byte) 30);  // 列印空白點數
        }

        String strokeLine = "";
        for (int i = 1; i <= 33; i++) {
            strokeLine = strokeLine + "-";
        }

        //付款方式
        List<Payment> paymentList = detail.getPaymentList();
        printPaymentData(paymentList, X4);
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數
        //8.尚餘
        mWpPrinter.SP_printBig5(context.getString(R.string.balance), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition((int) (X4)); //設定與起始點空白距離
        mWpPrinter.SP_printBig5(addLeftSpaceTillLength(totalFee, 8), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_printBig5(strokeLine, alignment, attribute, size, false);  // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //9.請至櫃檯結帳，謝謝您
        mWpPrinter.SP_printBig5(context.getString(R.string.plsPayAtCounter), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //11.barCode
        int barCodeSystem = WpPrinter.BAR_CODE_CODE128;

        int height = 60;
        int width = 1;
        int characterPosition = WpPrinter.HRI_CHARACTER_NOT_PRINTED;
        alignment = WpPrinter.ALIGNMENT_CENTER;
        data = detail.getOrderId();
        mWpPrinter.print1dBarcode(data, barCodeSystem, alignment, width, height, characterPosition, false);
        mWpPrinter.SP_PrintFeedPaperDot((byte) 4); // 列印空白點數
        mWpPrinter.lineFeed(1, false);  //跳行

        //12.QRCode
        int model = WpPrinter.QR_CODE_MODEL1;
        int QrCdoeWidth = 4;

        final byte QRCodeVersion = 7;
        final int errorCorrectionLevel = '0';
        alignment = WpPrinter.ALIGNMENT_CENTER;
        mWpPrinter.SP_SelectCorrectionLevel((byte) errorCorrectionLevel); // select QRcode correction Level: '0'~ '3'
        mWpPrinter.SP_SetQRcodeVersion((byte) QRCodeVersion); //設定 QRcode Version
        mWpPrinter.printQrCode(data, alignment, model, QrCdoeWidth, true); //列印QRcode
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //end
        mWpPrinter.cutPaper(6, true);
        return super.printBill(context, detail);
    }

    @Override
    public int printEasyCardCheckOutData(EcCheckoutData ecCheckoutData) {
        int alignment;
        int attribute;
        int size;
        String data;
        int space = 200;

        //--- Set Page Mode ----------------------
        mWpPrinter.SP_SetPageMode();  // 設定 page mode = ON

        //logo
        alignment = WpPrinter.ALIGNMENT_CENTER;
        attribute = WpPrinter.TEXT_ATTRIBUTE_FONT_A;
        size = WpPrinter.TEXT_SIZE_HORIZONTAL2 | WpPrinter.TEXT_SIZE_VERTICAL2;

        //悠遊卡結帳憑證
        data = "悠遊卡結帳憑證";
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        alignment = WpPrinter.ALIGNMENT_LEFT;
        attribute = WpPrinter.TEXT_ATTRIBUTE_FONT_A;
        size = WpPrinter.TEXT_SIZE_HORIZONTAL1 | WpPrinter.TEXT_SIZE_VERTICAL1;

        //門市
        mWpPrinter.SP_printBig5("門市", alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
        mWpPrinter.SP_printBig5(":" + Config.shop_id, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //交易序號
        mWpPrinter.SP_printBig5("交易序號", alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
        mWpPrinter.SP_printBig5(":" + ecCheckoutData.getTXN_Serial_number(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //------------------------
        mWpPrinter.SP_printBig5("-----------------------------", alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //交易時間
        mWpPrinter.SP_printBig5("交易時間:" + ecCheckoutData.getTXN_DateTime().replace("-", "/"), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //平帳
        mWpPrinter.SP_printBig5("平帳", alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
        mWpPrinter.SP_printBig5(":" + (ecCheckoutData.getSettlement_Status().equals("0") ? "平帳" : "不平帳"), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //二代設備編號
        mWpPrinter.SP_printBig5("二代設備編號", alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
        mWpPrinter.SP_printBig5(":" + ecCheckoutData.getNew_Deviceid(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //批次號碼
        mWpPrinter.SP_printBig5("批次號碼", alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
        mWpPrinter.SP_printBig5(":" + ecCheckoutData.getBatch_number(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        mWpPrinter.lineFeed(1, false);  //跳行

        //購貨筆數
        mWpPrinter.SP_printBig5("購貨筆數    :" + ecCheckoutData.getDeduct_count(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
        mWpPrinter.SP_printBig5("總額  :" + ecCheckoutData.getDeduct_amt(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //退貨筆數
        mWpPrinter.SP_printBig5("退貨筆數    :" + ecCheckoutData.getRefund_count(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
        mWpPrinter.SP_printBig5("總額  :" + ecCheckoutData.getRefund_amt(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //自動加值筆數
        mWpPrinter.SP_printBig5("自動加值筆數:" + ecCheckoutData.getAddValue_count(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
        mWpPrinter.SP_printBig5("總額  :" + ecCheckoutData.getTotal_AddValue_amt(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //購貨類總筆數
        mWpPrinter.SP_printBig5("購貨類總筆數:" + ecCheckoutData.getTotal_Txn_count(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
        mWpPrinter.SP_printBig5("總金額:" + ecCheckoutData.getTotal_Txn_amt(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
        mWpPrinter.SP_printBig5("總淨額:" + ecCheckoutData.getTotal_Txn_pure_amt(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.lineFeed(1, false);  //跳行

        //交易總筆數
        mWpPrinter.SP_printBig5("交易總筆數 ", alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
        mWpPrinter.SP_printBig5(":＄" + ecCheckoutData.getTotal_Txn_count(), alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);  //跳行

        //交易總額
        mWpPrinter.SP_printBig5("交易總額 ", alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.SP_SetDotposition(space);//設定與起始點空白距離
        mWpPrinter.SP_printBig5(":＄" + ecCheckoutData.getTotal_Txn_amt(), alignment, attribute, size, false); // 列印Big5字串

        //end
        mWpPrinter.cutPaper(6, true);
        return super.printEasyCardCheckOutData(ecCheckoutData);
    }

    @Override
    public void showPrinterErrorDialog(Context context, PrinterErrorDialog.Listener listener, String message) {
        new PrinterErrorDialog(context, listener, message).show();
    }

    @Override
    public int printGamePoint(Context context) {
        OrderResponse orderResponse = Bill.fromServer.getOrderResponse();
        String strokeLine = "";
        for (int i = 1; i <= 33; i++) {
            strokeLine = strokeLine + "-";
        }

        int X2 = 120;
        int X3 = 180;
        int X4 = 280;

        mWpPrinter.SP_SetPageMode();  // 設定 page mode = ON
        int attribute = 0;
        int size = 0;
        int alignment = WpPrinter.ALIGNMENT_LEFT;
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        String data = context.getString(R.string.receipt);
        //1.點數券
        alignment = WpPrinter.ALIGNMENT_CENTER;
        data = context.getString(R.string.gamePointTicket);
        size = WpPrinter.TEXT_SIZE_HORIZONTAL2 | WpPrinter.TEXT_SIZE_VERTICAL2;
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);
        mWpPrinter.lineFeed(1, false);

        Drawable drawable = mContext.getResources().getDrawable(R.drawable.brogent_logo);
        printBitmap(drawable,500);
        mWpPrinter.lineFeed(1, false);
        mWpPrinter.lineFeed(1, false);
        //30.QRCode
        int model = WpPrinter.QR_CODE_MODEL1;
        int QrCdoeWidth = 4;

        final byte QRCodeVersion = 7;
        final int errorCorrectionLevel = '0';
        mWpPrinter.SP_SelectCorrectionLevel((byte) errorCorrectionLevel); // select QRcode correction Level: '0'~ '3'
        mWpPrinter.SP_SetQRcodeVersion((byte) QRCodeVersion); //設定 QRcode Version
        mWpPrinter.printQrCode(Bill.fromServer.worder_id, alignment, model, QrCdoeWidth, true); //列印QRcode
        mWpPrinter.lineFeed(1, false);  //跳行
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        data = context.getString(R.string.brogent_title);
        size = WpPrinter.TEXT_SIZE_HORIZONTAL2 | WpPrinter.TEXT_SIZE_VERTICAL2;
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數

        //1.點數券
        data = context.getString(R.string.gamePointNo) + Bill.fromServer.worder_id;
        size = WpPrinter.TEXT_SIZE_HORIZONTAL1 | WpPrinter.TEXT_SIZE_VERTICAL1;
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);
        mWpPrinter.SP_PrintFeedPaperDot((byte) 10);  // 列印空白點數
        mWpPrinter.cutPaper(6, true);

        //1.購買明細
        alignment = WpPrinter.ALIGNMENT_CENTER;
        data = context.getString(R.string.gamePointDetail);
        size = WpPrinter.TEXT_SIZE_HORIZONTAL1 | WpPrinter.TEXT_SIZE_VERTICAL1;
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);
        drawable = mContext.getResources().getDrawable(R.drawable.brogent_line);
        printBitmap(drawable,390);
        alignment = WpPrinter.ALIGNMENT_LEFT;
        data = "購買時間:   ";
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        data = addLeftSpaceTillLength(Bill.fromServer.getOrderResponse().getInput_date(), 20);
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);
        data = "購買明細:   ";
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        // 9~11. 商品列表
        int index = 0;
        List<WebOrder01> webOrder01List = Bill.fromServer.getOrderResponse().getWeborder01();
        for (WebOrder01 webOrder01 : webOrder01List) {
            double salePrice = ComputationUtils.parseDouble(webOrder01.getSale_price());
            int qty = (int) ComputationUtils.parseDouble(webOrder01.getQty());
            data = addLeftSpaceTillLength(webOrder01.getProd_name1() + "*" + qty + "", index==0?10:22);
            mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
            data = addLeftSpaceTillLength(salePrice * qty + "", 8);
            mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
            mWpPrinter.lineFeed(1, false);
            index++;
        }
        //12~13.總金額
        data = context.getString(R.string.totalAmount)+":";
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        data = addLeftSpaceTillLength(orderResponse.getTot_sales() + "", 25);
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);
        mWpPrinter.lineFeed(1, false);
        drawable = mContext.getResources().getDrawable(R.drawable.brogent_ticket_info);
        printBitmap(drawable,420);
        mWpPrinter.lineFeed(1, false);
        alignment = WpPrinter.ALIGNMENT_CENTER;
        data = "列印時間:"+ TimeUtil.getNowTime();
        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);
        mWpPrinter.cutPaper(6, true);
        return super.printGamePoint(context);
    }

    @Override
    public void beforePrintCheckFlow(Context context, PrinterErrorDialog.Listener dialogListener, PrinterCheckFlowListener printerCheckFlowListener) {
        printerStatusCheck(context, dialogListener, printerCheckFlowListener, true);
    }

    @Override
    public void laterPrinterCheckFlow(final Context context, final PrinterErrorDialog.Listener dialogListener, final PrinterCheckFlowListener printerCheckFlowListener, int second) {
        new CountDownTimer(second * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                paperOutCheck(context, dialogListener, printerCheckFlowListener, false);
            }

            @Override
            public void onFinish() {
                printerStatusCheck(context, dialogListener, printerCheckFlowListener, false);
            }
        }.start();
    }

    //檢查出紙口seconsor有無紙張
    public void checkPaperSensor(final Context context, final PrinterErrorDialog.Listener dialogListener, WpPrinterHandler.SensorListener sensorListener, boolean beforePrint) {
        wpHandler.setSensorListener(sensorListener);
        byte[] command = new byte[]{0x10, 0x04, 0x05};
        mWpPrinter.executeDirectIo(command, true);

        //印單後的情況會於狀態檢查執行下一步
        if (beforePrint) {
            threadCheck(context, dialogListener, sensorListener);
        }
    }

    public void checkPrinterStatus(final Context context, final PrinterErrorDialog.Listener dialogListener, final WpPrinterHandler.StatusListener statusListener) {
        wpHandler.setStatusListener(statusListener);
        byte[] command = new byte[]{0x10, 0x04, 0x02};
        mWpPrinter.executeDirectIo(command, true);

        threadCheck(context, dialogListener, statusListener);
    }
    public byte[] hexStringToByteArray(String hex) {

        String[] ary=hex.split(" ");
        int len = ary.length;
        byte[] data = new byte[len];
        int h;
        int l;
        for( int i=0; i<len;i++) {
            int k= ary[i].length();
            if( k == 1) {
                //data[i] = (byte) Character.digit(ary[i].charAt(0), 16);
                h =     (ary[i].charAt(0));
                if( h==-1  )
                    CommonUtils.showMessage(mContext, "input error:"+ary[i]);
                else
                    data[i] = (byte)(h);

            }
            else {
                h = hexToBin(ary[i].charAt(0 ));
                l = hexToBin(ary[i].charAt(1));
                if( h==-1 || l == -1 )
                    CommonUtils.showMessage(mContext, "input error:"+ary[i]);
                else
                    data[i] = (byte)(h*16+l);
            }
        }
        return data;
    }

    private static int hexToBin( char ch ) {
        if( '0'<=ch && ch<='9' )    return ch-'0';
        if( 'A'<=ch && ch<='F' )    return ch-'A'+10;
        if( 'a'<=ch && ch<='f' )    return ch-'a'+10;
        return -1;
    }

    public void threadCheck(final Context context, final PrinterErrorDialog.Listener dialogListener, final WpPrinterHandler.ThreadFlagController controller) {
        new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (controller.isResponded()) {
                    cancel();
                }
            }

            @Override
            public void onFinish() {
                synchronized (controller) {
                    if (controller.isResponded()) {
                        return;
                    }
                    controller.setResponded();
                }

                addLog("出單機執行緒檢查，超過兩秒沒有回應");
                String msg = context.getString(R.string.printerNotOk);
                showPrinterErrorDialog(context, dialogListener, msg);

                //避免斷線後無連線情況，再連線一次
                requestPrinterPermission(context);
            }
        }.start();
    }

    public void printerStatusCheck(final Context context, final PrinterErrorDialog.Listener dialogListener, final PrinterCheckFlowListener printerCheckFlowListener, final boolean beforePrint) {
        checkPrinterStatus(context, dialogListener, new WpPrinterHandler.StatusListener() {
            boolean isResponded = false;

            @Override
            public void statusResult(boolean isError, int resCode) {
                synchronized (this) {
                    if (isResponded) {
                        return;
                    }
                    isResponded = true;
                }

                if (isError) {
                    String msg;
                    if (isPaperEnd(resCode)) {
                        msg = context.getString(R.string.printerPaperEnd) + "\n" + context.getString(R.string.printerErrMoreMsg) + "\n";
                    } else if (isCoverOpen(resCode)) {
                        msg = context.getString(R.string.printerCoverOpen) + "\n" + context.getString(R.string.printerErrMoreMsg) + "\n";
                    } else {
                        msg = context.getString(R.string.printerNotOk) + "\n" + context.getString(R.string.printerErrMoreMsg) + "\n";
                    }
                    showPrinterErrorDialog(context, dialogListener, msg);
                    addLog(msg + "Printer Check Error isError ResCode = " + resCode);
                    FirebaseCrashlytics.getInstance().log("Printer Check Error");
                    FirebaseCrashlytics.getInstance().log("Printer:"+ "isError ResCode = " + resCode);

                } else {
                    if (beforePrint) {
                        paperOutCheck(context, dialogListener, printerCheckFlowListener, beforePrint);
                    } else {
                        if (isPaperOut) {
                            printerCheckFlowListener.onFinish();
                            isPaperOut = false;
                        } else {
                            String errMsg = context.getString(R.string.printFail) + context.getString(R.string.printerErrMoreMsg) + "\n";
                            showPrinterErrorDialog(context, dialogListener, errMsg);

                            FirebaseCrashlytics.getInstance().log("Printer Check Error");
                            FirebaseCrashlytics.getInstance().log("Printer:"+ "isError ResCode = " + resCode);
                            FirebaseCrashlytics.getInstance().log("Printer:"+ "isPaperOut = " + isPaperOut);
                            FirebaseCrashlytics.getInstance().log("Printer:"+ "paperOutCheckHistory = " + paperOutCheckHistory);
                            FirebaseCrashlytics.getInstance().log("Printer:"+ "isBeforePrint = " + false);
                        }
                        paperOutCheckHistory = "";
                    }
                }
            }


            @Override
            public boolean isResponded() {
                return isResponded;
            }

            @Override
            public void setResponded() {
                isResponded = true;
            }
        });
    }

    public void paperOutCheck(final Context context, final PrinterErrorDialog.Listener dialogListener,
                              final PrinterCheckFlowListener printerCheckFlowListener, final boolean beforePrint) {
        checkPaperSensor(context, dialogListener, new WpPrinterHandler.SensorListener() {
            boolean isResponded = false;

            @Override
            public void paperResult(boolean present, int resCode) {
                if (beforePrint) {
                    synchronized (this) {
                        if (isResponded) {
                            return;
                        }
                        isResponded = true;
                    }
                }

                addLog("isPaperOut=" + present);
                addLog("responseCode=" + resCode);
                isPaperOut = isPaperOut || present;
                if (beforePrint) {
                    if (isPaperOut) {
                        String msg = context.getString(R.string.printerPaperPresentAlert);
                        showPrinterErrorDialog(context, dialogListener, msg);
                        isPaperOut = false;
                    } else {
                        printerCheckFlowListener.onFinish();
                    }
                } else {
                    paperOutCheckHistory += "isPaperOut = " + isPaperOut + " \n";
                }
            }

            @Override
            public boolean isResponded() {
                return isResponded;
            }

            @Override
            public void setResponded() {
                isResponded = true;
            }
        }, beforePrint);
    }

    private void printBitmap(Drawable drawable,int width) {
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        try {
            int dstw = (width + 7) / 8 * 8;
            int dsth = bitmap.getHeight() * dstw / bitmap.getWidth();
            int[] dst = new int[dstw * dsth];
            bitmap = ImageProcessing.resizeImage(bitmap, dstw, dsth);
            bitmap.getPixels(dst, 0, dstw, 0, 0, dstw, dsth);
            byte[] gray = ImageProcessing.GrayImage(dst);
            boolean[] dithered = new boolean[dstw * dsth];
            ImageProcessing.format_K_dither16x16(dstw, dsth, gray, dithered);

            byte[] data;
            data = ImageProcessing.eachLinePixToCmd(dithered, dstw, 0);
            mWpPrinter.printBitmap(data,1,width,1,true);
        } catch (Exception var15) {
            Log.i("@@@@@@", "zz____"+var15.toString());
        } finally {
            Log.i("@@@@@@","1231231");
        }
    }


    private boolean isPaperEnd(int code) {
        return (code & 0x20) == 0x20;
    }

    private boolean isCoverOpen(int code) {
        return (code & 0x04) == 0x04;
    }

    @Override
    public int printTest(Context context, String data) {
        int alignment = WpPrinter.ALIGNMENT_LEFT;
        int attribute = WpPrinter.TEXT_ATTRIBUTE_FONT_A;
        int size = WpPrinter.TEXT_SIZE_HORIZONTAL1 | WpPrinter.TEXT_SIZE_VERTICAL1;

        //--- Set Page Mode ----------------------
        mWpPrinter.SP_SetPageMode();  // 設定 page mode = ON

        mWpPrinter.SP_printBig5(data, alignment, attribute, size, false); // 列印Big5字串
        mWpPrinter.lineFeed(1, false);
        mWpPrinter.SP_PrintFeedPaperDot((byte) 15);

        //end
        mWpPrinter.cutPaper(6, true);
        return super.printTest(context, data);
    }
}
