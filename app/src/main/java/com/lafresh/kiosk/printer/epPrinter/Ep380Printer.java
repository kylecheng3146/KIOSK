package com.lafresh.kiosk.printer.epPrinter;

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

import androidx.annotation.RequiresApi;

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
import com.lafresh.kiosk.printer.model.Ticket;
import com.lafresh.kiosk.type.FlavorType;
import com.lafresh.kiosk.utils.ComputationUtils;
import com.lafresh.kiosk.utils.FormatUtil;
import com.lvrenyang.io.Pos;
import com.lvrenyang.io.USBPrinting;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiresApi(api = Build.VERSION_CODES.M)
public class Ep380Printer extends KioskPrinter {
    public static final int VENDOR_ID = 4070;
    public static final int PRODUCT_ID = 33054;

    private Context context;
    private EpPrinterReceiver partnerReceiver;
    private ExecutorService es;
    private Pos pos;
    private static boolean isPaperOut = false;
    public static USBPrinting mUsb;

    public Ep380Printer(Context ct) {
        this.context = ct;
        es = Executors.newScheduledThreadPool(30);
        pos = new Pos();
        mUsb = new USBPrinting();
        pos.Set(mUsb);
        setReceiver(ct);
    }

    public void setReceiver(Context context) {
        partnerReceiver = new EpPrinterReceiver(this);
        String filterTag = VENDOR_ID + "-" + PRODUCT_ID;
        IntentFilter intentFilter = new IntentFilter(filterTag);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        intentFilter.addAction(UsbManager.EXTRA_PERMISSION_GRANTED);
        context.registerReceiver(partnerReceiver, intentFilter);
    }

    @Override
    public void releaseAll(Context context) {
        context.unregisterReceiver(partnerReceiver);
        if (isConnect()) {
            disconnect();
        }
        partnerReceiver = null;
        pos = null;
        releasePrinter();
    }

    @Override
    public void requestPrinterPermission(Context context) {
        requestUsbPermission(context, VENDOR_ID, PRODUCT_ID);
    }

    @Override
    public boolean disconnect() {
        es.submit(new Runnable() {
            @Override
            public void run() {
                mUsb.Close();
            }
        });
        return true;
    }

    @Override
    public boolean isConnect() {
        return pos.GetIO().IsOpened();
    }

    @Override
    public boolean connectUsb(final UsbDevice usbDevice) {
        final UsbManager mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        es.submit(new Runnable() {
            @Override
            public void run() {
                mUsb.Open(mUsbManager, usbDevice, context);
            }
        });
        return true;
    }

    @Override
    public int printEInvoice(Context context) {
        String data;
        pos.POS_Reset();  //打印機歸位

        Invoice invoice = Bill.fromServer.getOrderResponse().getInvoice();
        String testGuiNo = context.getString(R.string.testGuiNo);
        String guiNo = invoice.getGUI_number();

        pos.POS_S_Align(1);  //置中

        //此為測試發票
        if (testGuiNo.equals(guiNo)) {
            String testString = context.getString(R.string.testReceipt);
            pos.POS_TextOut(testString, 3, 0, 1, 1, 0, 0);
            pos.POS_FeedLine();  //跳行
        }

        //1.logo
        String invoiceTitle = invoice.getInvoice_title();
        if (invoiceTitle == null) {
            invoiceTitle = "";
        }
        pos.POS_TextOut(invoiceTitle, 3, 0, 1, 1, 0, 0);
        pos.POS_FeedLine();  //跳行

        //2.電子發票證明聯
        pos.POS_TextOut(context.getString(R.string.eInvPrintTitle), 3, 0, 1, 1, 0, 0);
        pos.POS_FeedLine();  //跳行

        //3.發票期別
        String twYear = invoice.getTwYearString();
        String beginMonth = invoice.getStartMonthString();
        String endMonth = invoice.getEndMonthString();
        data = twYear + context.getString(R.string.year) + beginMonth + "-" + endMonth + context.getString(R.string.month);
        pos.POS_TextOut(data, 3, 0, 1, 1, 0, 0);
        pos.POS_FeedLine();  //跳行

        //4.發票號碼
        String invNo = invoice.getInv_no_b();
        data = invNo.substring(0, 2) + "-" + invNo.substring(2, 10);
        pos.POS_TextOut(data, 3, 0, 1, 1, 0, 0);
        pos.POS_FeedLine();  //跳行

        //5.列印時間
        pos.POS_S_Align(0);  //置左
        data = getTime() + (Bill.fromServer.getCustCode().equals("") ? "" : "  格式25");
        pos.POS_TextOut(data, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //6a.列印隨機碼
        data = "隨機碼 " + invoice.getRandom_no();
        pos.POS_TextOut(data, 3, 0, 0, 0, 0, 0);

        //6.b總計
        data = "總計  " + Bill.fromServer.getInvoiceAmount();
        pos.POS_TextOut(data, 3, 200, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //7a.列印買賣方code -------------------
        data = context.getString(R.string.seller) + guiNo;
        pos.POS_TextOut(data, 3, 0, 0, 0, 0, 0);

        //7b.買方code
        String custCode = Bill.fromServer.getCustCode();
        if (custCode.length() > 0) {
            pos.POS_TextOut(context.getString(R.string.buyer) + custCode, 3, 200, 0, 0, 0, 0);
        }
        pos.POS_FeedLine();  //跳行

        //8.條碼
        int height = 48;
        int width = 1;
        int barCodeType = 69;  //BAR_CODE_CODE39
        String barcodeData = invoice.getBarcodeData();
        pos.POS_S_Align(1);  //置中
        pos.POS_S_SetBarcode(barcodeData, 0, barCodeType, width, height, 0, 0);

        //9.左右QRCode
        int QrCodeWidth = 3;
        final byte QRCodeVersion = 7;
        final int errorCorrectionLevel = 1;
        String[] QR = Bill.fromServer.getQR();
        pos.POS_DoubleQRCode(QR[0], 20, errorCorrectionLevel, QRCodeVersion, QR[1], 230, errorCorrectionLevel, QRCodeVersion, QrCodeWidth);

        //10.店號 機 序
        //-----列印店名 -------------------
        pos.POS_S_Align(0);  //置左
        String serial = Bill.fromServer.worder_id.substring(Bill.fromServer.worder_id.length() - 10);
        data = context.getString(R.string.shopId) + Config.shop_id + "  機 " + Config.kiosk_id + " 序" + serial;
        pos.POS_TextOut(data, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //11.退貨
        pos.POS_TextOut(context.getString(R.string.eInvBottomMsg), 3, 0, 0, 0, 0, 0);
        //------------------------------------------
        if (Bill.fromServer.getCustCode().length() > 0) {
            printPaymentDetail(context);
        } else {
            pos.POS_FeedLine();  //跳行
            pos.POS_FeedLine();  //跳行
            pos.POS_FeedLine();  //跳行
            pos.POS_FeedLine();  //跳行
            pos.POS_HalfCutPaper();
        }
        return super.printEInvoice(context);
    }

    @Override
    public int printEInvoice(Context context, EReceipt receipt, ReceiptDetail detail) {
        String data;
        pos.POS_Reset();  //打印機歸位

        String testGuiNo = context.getString(R.string.testGuiNo);
        String guiNo = receipt.getSellerGuiNumber();

        pos.POS_S_Align(1);  //置中

        //此為測試發票
        if (testGuiNo.equals(guiNo)) {
            String testString = context.getString(R.string.testReceipt);
            pos.POS_TextOut(testString, 3, 0, 1, 1, 0, 0);
            pos.POS_FeedLine();  //跳行
        }

        //1.logo
        String receiptTitle = receipt.getReceiptTitle();
        if (receiptTitle == null) {
            receiptTitle = "";
        }
        pos.POS_TextOut(receiptTitle, 3, 0, 1, 1, 0, 0);
        pos.POS_FeedLine();  //跳行

        //2.電子發票證明聯
        pos.POS_TextOut(context.getString(R.string.eInvPrintTitle), 3, 0, 1, 1, 0, 0);
        pos.POS_FeedLine();  //跳行

        //3.發票期別
        String twYear = receipt.getTwYearString();
        String beginMonth = receipt.getStartMonth();
        String endMonth = receipt.getEndMonth();
        data = twYear + context.getString(R.string.year) + beginMonth + "-" + endMonth + context.getString(R.string.month);
        pos.POS_TextOut(data, 3, 0, 1, 1, 0, 0);
        pos.POS_FeedLine();  //跳行

        //4.發票號碼
        String invNo = receipt.getEReceiptNumber();
        data = invNo.substring(0, 2) + "-" + invNo.substring(2, 10);
        pos.POS_TextOut(data, 3, 0, 1, 1, 0, 0);
        pos.POS_FeedLine();  //跳行

        //5.列印時間
        pos.POS_S_Align(0);  //置左
        data = getTime() + (receipt.getBuyerGuiNumber().equals("") ? "" : "  格式25");
        pos.POS_TextOut(data, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //6a.列印隨機碼
        data = "隨機碼 " + receipt.getRandomNumber();
        pos.POS_TextOut(data, 3, 0, 0, 0, 0, 0);

        //6.b總計
        data = "總計  " + receipt.getTotalAmount();
        pos.POS_TextOut(data, 3, 200, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //7a.列印買賣方code -------------------
        data = context.getString(R.string.seller) + guiNo;
        pos.POS_TextOut(data, 3, 0, 0, 0, 0, 0);

        //7b.買方code
        String buyerGuiNumber = receipt.getBuyerGuiNumber();
        if (buyerGuiNumber.length() > 0) {
            pos.POS_TextOut(context.getString(R.string.buyer) + buyerGuiNumber, 3, 200, 0, 0, 0, 0);
        }
        pos.POS_FeedLine();  //跳行

        //8.條碼
        int height = 48;
        int width = 1;
        int barCodeType = 69;  //BAR_CODE_CODE39
        String barcodeData = receipt.getBarCodeData();
        pos.POS_S_Align(1);  //置中
        pos.POS_S_SetBarcode(barcodeData, 0, barCodeType, width, height, 0, 0);

        //9.左右QRCode
        int QrCodeWidth = 3;
        final byte QRCodeVersion = 7;
        final int errorCorrectionLevel = 1;
        String[] qrCodeData = receipt.getQrCodeData();
        qrCodeData[1] = "**                                                       " +
                "                                                                              ";
        pos.POS_DoubleQRCode(qrCodeData[0], 20, errorCorrectionLevel, QRCodeVersion,
                qrCodeData[1], 230, errorCorrectionLevel, QRCodeVersion, QrCodeWidth);

        //10.店號 機 序
        //-----列印店名 -------------------
        pos.POS_S_Align(0);  //置左
        String serial = detail.getOrderId().substring(detail.getOrderId().length() - 10);
        data = context.getString(R.string.shopId) + Config.shop_id + "  機 " + Config.kiosk_id + " 序" + serial;
        pos.POS_TextOut(data, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //11.退貨
        pos.POS_TextOut(context.getString(R.string.eInvBottomMsg), 3, 0, 0, 0, 0, 0);
        //------------------------------------------
        if (receipt.getBuyerGuiNumber().length() > 0) {
            printPaymentDetail(context, receipt, detail);
        } else {
            pos.POS_FeedLine();  //跳行
            pos.POS_FeedLine();  //跳行
            pos.POS_FeedLine();  //跳行
            pos.POS_FeedLine();  //跳行
            pos.POS_HalfCutPaper();
        }
        return super.printEInvoice(context, receipt, detail);
    }

    private String getStrokeLine() {
        StringBuilder strokeLine = new StringBuilder();
        for (int i = 1; i <= 32; i++) {
            strokeLine.append("-");
        }
        return strokeLine.toString();
    }

    @Override
    public int printPaymentDetail(Context context) {
        String data;
        String strokeLine = getStrokeLine();

        //0.---
        pos.POS_FeedLine();  //跳行
        pos.POS_TextOut(strokeLine, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //1.交易明細
        pos.POS_SetLineHeight(35);  //設置行高
        pos.POS_S_Align(1);  //置中
        pos.POS_TextOut(context.getString(R.string.invDetail), 3, 0, 1, 1, 0, 0);
        pos.POS_FeedLine();  //跳行

        // 5. 商品列表
        pos.POS_S_Align(0);  //置左
        List<WebOrder01> webOrder01List = Bill.fromServer.getOrderResponse().getWeborder01();
        for (WebOrder01 webOrder01 : webOrder01List) {
            double salePrice = ComputationUtils.parseDouble(webOrder01.getSale_price());
            int qty = (int) ComputationUtils.parseDouble(webOrder01.getQty());
            pos.POS_TextOut(webOrder01.getProd_name1(), 3, 0, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
            data = addLeftSpaceTillLength(String.valueOf(qty), 4);
            pos.POS_TextOut(data, 3, 50, 0, 0, 0, 0);
            data = addLeftSpaceTillLength(salePrice + "", 8);
            pos.POS_TextOut(data, 3, 130, 0, 0, 0, 0);
            String total = qty * salePrice + "TX";
            data = addLeftSpaceTillLength(total, 8);
            pos.POS_TextOut(data, 3, 280, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
        }
        pos.POS_FeedLine();  //跳行

        //6.小計
        pos.POS_TextOut(context.getString(R.string.subtotalWithColon), 3, 0, 0, 0, 0, 0);
        data = addLeftSpaceTillLength(Bill.fromServer.getInvoiceAmount() + "TX", 8);
        pos.POS_TextOut(data, 3, 280, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //7.小計上---
        pos.POS_TextOut(strokeLine, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //8.應稅銷售
        pos.POS_TextOut(context.getString(R.string.taxAmount), 3, 0, 0, 0, 0, 0);
        data = addLeftSpaceTillLength(Bill.fromServer.get_unTax() + "", 8);
        pos.POS_TextOut(data, 3, 280, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //8.免稅
        pos.POS_TextOut(context.getString(R.string.taxFreeAmt), 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut(addLeftSpaceTillLength("0", 8), 3, 280, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //9.稅額
        pos.POS_TextOut("稅      額", 3, 0, 0, 0, 0, 0);
        data = addLeftSpaceTillLength(Bill.fromServer.getTax() + "", 8);
        pos.POS_TextOut(data, 3, 280, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //10.總計
        pos.POS_TextOut("總      計", 3, 0, 0, 0, 0, 0);
        data = addLeftSpaceTillLength(Bill.fromServer.getInvoiceAmount() + "", 8);
        pos.POS_TextOut(data, 3, 280, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //.11小計下---
        pos.POS_TextOut(strokeLine, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //.12付款方式
        List<WebOrder02> webOrder02List = Bill.fromServer.getOrderResponse().getWeborder02();
        for (WebOrder02 webOrder02 : webOrder02List) {
            String payName = getPayName(context, webOrder02.getPay_id());
            pos.POS_TextOut(payName + ":", 3, 0, 0, 0, 0, 0);
            String payAmount = addLeftSpaceTillLength(removeDot(webOrder02.getAmount()), 8);
            pos.POS_TextOut(payAmount, 3, 280, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
        }
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行

        pos.POS_HalfCutPaper();
        return super.printPaymentDetail(context);
    }

    @Override
    public int printPaymentDetail(Context context, EReceipt receipt, ReceiptDetail detail) {
        String data;
        String strokeLine = getStrokeLine();

        //0.---
        pos.POS_FeedLine();  //跳行
        pos.POS_TextOut(strokeLine, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //1.交易明細
        pos.POS_SetLineHeight(35);  //設置行高
        pos.POS_S_Align(1);  //置中
        pos.POS_TextOut(context.getString(R.string.invDetail), 3, 0, 1, 1, 0, 0);
        pos.POS_FeedLine();  //跳行

        // 5. 商品列表
        pos.POS_S_Align(0);  //置左
        List<Product> productList = detail.getProductList();
        for (Product product : productList) {
            int qty = product.getQuantity();
            String usePointMsg = product.getIsUsePoint() ? "(點數兌換)": "";
            String useTicketMsg = product.getTicketNo() != null ? "(商品券兌換)": "";
            String onSale = product.getIsOnSale() ? "(特價)": "";

            if(!useTicketMsg.isEmpty())
                pos.POS_TextOut(product.getName()+useTicketMsg, 3, 0, 0, 0, 0, 0);
            else if(!usePointMsg.isEmpty())
                pos.POS_TextOut(product.getName()+usePointMsg, 3, 0, 0, 0, 0, 0);
            else if (!onSale.isEmpty())
                pos.POS_TextOut(product.getName()+onSale, 3, 0, 0, 0, 0, 0);
            else
                pos.POS_TextOut(product.getName(), 3, 0, 0, 0, 0, 0);

            pos.POS_FeedLine();  //跳行
            pos.POS_TextOut(addLeftSpaceTillLength(qty + "", 4), 3, 50, 0, 0, 0, 0);
            if(BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name())) {
                String unitPrice = FormatUtil.removeDot(product.getUnitPrice()) + "";
                pos.POS_TextOut(addLeftSpaceTillLength(unitPrice, 8), 3, 130, 0, 0, 0, 0);
            }
            String totalPrice = FormatUtil.removeDot(product.getTotalPrice()) + "TX";
            pos.POS_TextOut(addLeftSpaceTillLength(totalPrice, 8), 3, 280, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
        }
        pos.POS_FeedLine();  //跳行

        //6.小計
        pos.POS_TextOut(context.getString(R.string.subtotalWithColon), 3, 0, 0, 0, 0, 0);
        data = addLeftSpaceTillLength(receipt.getTotalAmount() + "TX", 8);
        pos.POS_TextOut(data, 3, 280, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //7.小計上---
        pos.POS_TextOut(strokeLine, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //8.應稅銷售
        pos.POS_TextOut(context.getString(R.string.taxAmount), 3, 0, 0, 0, 0, 0);
        data = addLeftSpaceTillLength(receipt.getUnTaxAmount(), 8);
        pos.POS_TextOut(data, 3, 280, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //8.免稅
        pos.POS_TextOut(context.getString(R.string.taxFreeAmt), 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut(addLeftSpaceTillLength("0", 8), 3, 280, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //9.稅額
        pos.POS_TextOut("稅      額", 3, 0, 0, 0, 0, 0);
        data = addLeftSpaceTillLength(receipt.getTaxAmount(), 8);
        pos.POS_TextOut(data, 3, 280, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //10.總計
        pos.POS_TextOut("總      計", 3, 0, 0, 0, 0, 0);
        data = addLeftSpaceTillLength(detail.getTotal() + "", 8);
        pos.POS_TextOut(data, 3, 280, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //.11小計下---
        pos.POS_TextOut(strokeLine, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //.12付款方式
        printPaymentData(detail.getPaymentList());
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行

        pos.POS_HalfCutPaper();
        return super.printPaymentDetail(context, receipt, detail);
    }

    @Override
    public int printReceipt(Context context) {
        OrderResponse orderResponse = Bill.fromServer.getOrderResponse();

        String data;
        String strokeLine = getStrokeLine();

        pos.POS_Reset();  //打印機歸位

        if (!BuildConfig.FLAVOR.equals("TFG")) {
            printTakeOutNumber(context, orderResponse);
            pos.POS_FeedLine();  //跳行
            pos.POS_FeedLine();  //跳行
            pos.POS_HalfCutPaper();
        }

        //1.收據
        pos.POS_TextOut(context.getString(R.string.receipt), 3, 0, 1, 1, 0, 0);
        pos.POS_FeedLine();  //跳行
        printTakeOutNumber(context, orderResponse);
        pos.POS_FeedLine();  //跳行
        //6.商品 數量 原價 小計
        pos.POS_TextOut(strokeLine, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        pos.POS_TextOut(context.getString(R.string.product), 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut(context.getString(R.string.quantity), 3, 110, 0, 0, 0, 0);
        pos.POS_TextOut(context.getString(R.string.originPrice), 3, 220, 0, 0, 0, 0);
        pos.POS_TextOut(context.getString(R.string.subtoal), 3, 330, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        pos.POS_TextOut(strokeLine, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        // 9~11. 商品列表
        List<WebOrder01> webOrder01List = Bill.fromServer.getOrderResponse().getWeborder01();
        for (WebOrder01 webOrder01 : webOrder01List) {
            double salePrice = ComputationUtils.parseDouble(webOrder01.getSale_price());
            int qty = (int) ComputationUtils.parseDouble(webOrder01.getQty());
            int itemDisc = (int) ComputationUtils.parseDouble(webOrder01.getItem_disc());
            pos.POS_TextOut(webOrder01.getProd_name1(), 3, 0, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
            if (webOrder01.getTaste_memo() != null && webOrder01.getTaste_memo().length() > 0) {
                pos.POS_TextOut("  **" + webOrder01.getTaste_memo(), 3, 0, 0, 0, 0, 0);
                pos.POS_FeedLine();  //跳行
            }
            data = addLeftSpaceTillLength(qty + "", 4);
            pos.POS_TextOut(data, 3, 110, 0, 0, 0, 0);
            data = addLeftSpaceTillLength(salePrice * qty + "", 8);
            pos.POS_TextOut(data, 3, 170, 0, 0, 0, 0);
            data = addLeftSpaceTillLength(salePrice * qty + itemDisc + "", 8);
            pos.POS_TextOut(data, 3, 280, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
        }
        pos.POS_FeedLine();  //跳行

        pos.POS_TextOut(strokeLine, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //12~13.總金額
        pos.POS_TextOut(context.getString(R.string.totalAmount) + ":", 3, 0, 0, 0, 0, 0);
        data = addLeftSpaceTillLength(orderResponse.getTot_sales() + "", 8);
        pos.POS_TextOut(data, 3, 280, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //14.尾數折讓
        if (orderResponse.getDiscount() != 0) {
            pos.POS_TextOut(context.getString(R.string.tailDiscount) + ":", 3, 0, 0, 0, 0, 0);
            String tailDiscount = addLeftSpaceTillLength(orderResponse.getDiscount() * -1 + "", 8);
            pos.POS_TextOut(tailDiscount, 3, 280, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
        }

        //15.實收
        pos.POS_TextOut(context.getString(R.string.receive), 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut(data, 3, 280, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //16.支付
        pos.POS_TextOut(context.getString(R.string.pay), 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut(data, 3, 280, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        pos.POS_FeedLine();  //跳行

        //18~19.付款方式
        List<WebOrder02> webOrder02List = orderResponse.getWeborder02();
        for (WebOrder02 webOrder02 : webOrder02List) {
            String payName = getPayName(context, webOrder02.getPay_id());
            pos.POS_TextOut(payName + ":", 3, 0, 0, 0, 0, 0);
            String payAmount = addLeftSpaceTillLength(removeDot(webOrder02.getAmount()), 8);
            pos.POS_TextOut(payAmount, 3, 280, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
        }
        pos.POS_FeedLine();  //跳行

        //21.發票號碼
        if (Bill.fromServer.getOrderResponse().getInvoice() != null && !Config.disableReceiptModule) {
            String invNo = Bill.fromServer.getOrderResponse().getInvoice().getInv_no_b();
            data = context.getString(R.string.invNo) + invNo;
            pos.POS_TextOut(data, 3, 0, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
        }

        //23.捐贈碼
        if (Bill.fromServer.getNpoBan().trim().length() > 0) {
            data = context.getString(R.string.donateNo) + Bill.fromServer.getNpoBan();
            pos.POS_TextOut(data, 3, 0, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
        }
        //24.載具
        if (Bill.fromServer.getBuyerNumber().trim().length() > 0) {
            data = context.getString(R.string.invVehicle) + Bill.fromServer.getBuyerNumber();
            pos.POS_TextOut(data, 3, 0, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
        }

        //悠遊卡交易
        if (Bill.fromServer.getEcPayData() != null) {
            int space = 150;
            EcPayData ecPayData = Bill.fromServer.getEcPayData();
            //------------
            pos.POS_TextOut("-----悠遊卡交易明細-----", 3, 0, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行

            //交易時間
            String date = ecPayData.getTxn_Date();
            String time = ecPayData.getTxn_Time();
            String printDate = date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6);
            String printTime = time.substring(0, 2) + ":" + time.substring(2, 4) + ":" + time.substring(4);
            pos.POS_TextOut("交易時間:" + printDate + " " + printTime, 3, 0, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行

            //悠遊卡卡號
            pos.POS_TextOut("悠遊卡卡號", 3, 0, 0, 0, 0, 0);
            pos.POS_TextOut(":" + ecPayData.getReceipt_card_number(), 3, space, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行

            //交易類別
            pos.POS_TextOut("交易類別", 3, 0, 0, 0, 0, 0);
            pos.POS_TextOut(":悠遊卡扣款", 3, space, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行

            //二代設備編號
            pos.POS_TextOut("二代設備編號", 3, 0, 0, 0, 0, 0);
            pos.POS_TextOut(":" + ecPayData.getNew_deviceID(), 3, space, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行

            //批次號碼
            pos.POS_TextOut("批次號碼", 3, 0, 0, 0, 0, 0);
            pos.POS_TextOut(":" + ecPayData.getBatch_number(), 3, space, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行

            //RRN
            pos.POS_TextOut("RRN", 3, 0, 0, 0, 0, 0);
            pos.POS_TextOut(":" + ecPayData.getRRN(), 3, space, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行

            //交易前餘額
            pos.POS_TextOut("交易前餘額", 3, 0, 0, 0, 0, 0);
            pos.POS_TextOut(":" + ecPayData.getN_CPU_EV_Before_TXN(), 3, space, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行

            //自動加值金額
            pos.POS_TextOut("自動加值金額", 3, 0, 0, 0, 0, 0);
            pos.POS_TextOut(":" + ecPayData.getN_AutoLoad_Amount(), 3, space, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行

            //交易金額
            pos.POS_TextOut("交易金額", 3, 0, 0, 0, 0, 0);
            pos.POS_TextOut(":" + ecPayData.getN_CPU_Txn_AMT(), 3, space, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行

            //交易後餘額
            pos.POS_TextOut("交易後餘額", 3, 0, 0, 0, 0, 0);
            pos.POS_TextOut(":" + ecPayData.getN_CPU_EV(), 3, space, 0, 0, 0, 0);
            pos.POS_FeedLine(); //跳行
        }

        //信用卡交易
        if (Bill.fromServer.getNcccTransDataBean() != null) {
            printNCCCTransData(Bill.fromServer.getNcccTransDataBean());
        }

        //26.----
        pos.POS_TextOut(strokeLine, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //27.此單據內容僅供參考，不作為交易憑證
        pos.POS_TextOut(context.getString(R.string.receiptBottomMsg), 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  // 列印空白點數

        //28.time
        pos.POS_TextOut(getTime(), 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //29.barCode
        int height = 60;
        int width = 2;
        int barCodeType = 73;  //BAR_CODE_CODE128
        pos.POS_S_Align(1);  //置中
        data = Bill.fromServer.worder_id;
        pos.POS_S_SetBarcode(data, 0, barCodeType, width, height, 0, 0);

        //30.QRCode
        int QrCodeWidth = 4;
        final byte QRCodeVersion = 7;
        final int errorCorrectionLevel = 1;
        pos.POS_S_SetQRcode(data, QrCodeWidth, QRCodeVersion, errorCorrectionLevel);
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行

        //end
        pos.POS_HalfCutPaper();
        return super.printReceipt(context);
    }

    private void printProductDetail(List<Product> productList) {
        for (Product product : productList) {
            int qty = product.getQuantity();
            String usePointMsg = product.getIsUsePoint() ? "(點數兌換)": "";
            String useTicketMsg = product.getTicketNo() != null ? "(商品券兌換)": "";
            String onSale = product.getUnitPrice() > product.getTotalPrice()? "(特價)": "";
            if(!useTicketMsg.isEmpty())
                pos.POS_TextOut(product.getName()+useTicketMsg, 3, 0, 0, 0, 0, 0);
            else if(!usePointMsg.isEmpty())
                pos.POS_TextOut(product.getName()+usePointMsg, 3, 0, 0, 0, 0, 0);
            else if (!onSale.isEmpty())
                pos.POS_TextOut(product.getName()+onSale, 3, 0, 0, 0, 0, 0);
            else
                pos.POS_TextOut(product.getName(), 3, 0, 0, 0, 0, 0);

            pos.POS_FeedLine();  //跳行
            if (product.getMemo() != null && product.getMemo().length() > 0) {
                pos.POS_TextOut("  **" + product.getMemo(), 3, 0, 0, 0, 0, 0);
                pos.POS_FeedLine();  //跳行
            }
            pos.POS_TextOut(addLeftSpaceTillLength(qty + "", 4), 3, 165, 0, 0, 0, 0);
            if(BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name())) {
            String unitPrice = FormatUtil.removeDot(product.getUnitPrice());
            pos.POS_TextOut(addLeftSpaceTillLength(unitPrice, 8), 3, 170, 0, 0, 0, 0);
            }
            String totalPrice = FormatUtil.removeDot(product.getTotalPrice());
            pos.POS_TextOut(addLeftSpaceTillLength(totalPrice, 8), 3, 280, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
        }
        pos.POS_FeedLine();  //跳行
    }

    private void printPaymentData(List<Payment> paymentList) {
        for (Payment payment : paymentList) {
            pos.POS_TextOut(payment.getName() + ":", 3, 0, 0, 0, 0, 0);
            String payAmount = addLeftSpaceTillLength(payment.getAmount() + "", 8);
            pos.POS_TextOut(payAmount, 3, 280, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
        }
        pos.POS_FeedLine();  //跳行
    }

    private void printTakeOutNumber(Context context, OrderResponse orderResponse) {
        String data;//4.內用外帶桌號
        //2.時間
        pos.POS_TextOut(getTime(), 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //3.店號 機 序
        data = Config.shop_id + "-" + Config.kiosk_id + "-" + Bill.fromServer.worder_id;
        pos.POS_TextOut(data, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        if (BuildConfig.FLAVOR.equals("TFG")) {
            data = Config.shopName;
            pos.POS_TextOut("店名：" + data, 3, 0, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
        }

        data = Bill.fromServer.getInOut_String(context);
        if (orderResponse.getTable_no() != null && orderResponse.getTable_no().length() > 0) {
            data += "/桌號-" + orderResponse.getTable_no();
        }
        pos.POS_TextOut(data, 3, 0, 1, 1, 0, 0);
        pos.POS_FeedLine();  //跳行

        //5.取餐號碼
        data = context.getString(R.string.takeMealNo) + Bill.fromServer.getOrderResponse().getTakeno();
        pos.POS_TextOut(data, 3, 0, 1, 1, 0, 0);
        pos.POS_FeedLine();  //跳行
    }

    @Override
    public int printReceipt(Context context, EReceipt receipt, ReceiptDetail detail) {
        String data;
        String strokeLine = getStrokeLine();

        pos.POS_Reset();  //打印機歸位

        printTakeOutNumber(context, detail);
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_HalfCutPaper();
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
        pos.POS_TextOut(data, 3, 0, 1, 1, 0, 0);
        pos.POS_FeedLine();  //跳行
        printTakeOutNumber(context, detail);
        pos.POS_FeedLine();  //跳行
        //6.商品 數量 原價 小計
        pos.POS_TextOut(strokeLine, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        pos.POS_TextOut(context.getString(R.string.product), 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut(context.getString(R.string.quantity), 3, 165, 0, 0, 0, 0);
        if(BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name())) {
            pos.POS_TextOut(context.getString(R.string.originPrice), 3, 220, 0, 0, 0, 0);
        }
        pos.POS_TextOut(context.getString(R.string.price), 3, 330, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        pos.POS_TextOut(strokeLine, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        // 9~11. 商品列表
        printProductDetail(detail.getProductList());

        pos.POS_TextOut(strokeLine, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //8.合計
        //12~13.總金額
        pos.POS_TextOut(context.getString(R.string.totalAmount) + ":", 3, 0, 0, 0, 0, 0);
        data = addLeftSpaceTillLength(FormatUtil.removeDot(detail.getTotal()) + "", 8);
        pos.POS_TextOut(data, 3, 280, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        double cashTicketAmount = 0.0;
        for (Payment payment : detail.getPaymentList()) {
            if(payment.getType().equals("CASH_TICKET")){
                cashTicketAmount = payment.getAmount() * 1.0;
            }
        }
        //8.折讓
        double discount = detail.getTotalFee() - detail.getTotalDischarge() + cashTicketAmount - detail.getTotal();
        if(discount > 0.0) {
            pos.POS_TextOut(context.getString(R.string.discount) + ":", 3, 0, 0, 0, 0, 0);
            data = addLeftSpaceTillLength("-" + FormatUtil.removeDot(discount), 8);
            pos.POS_TextOut(data, 3, 280, 0, 0, 0, 0);
            pos.POS_FeedLine();
        }//跳行

        //8.金額
        pos.POS_TextOut(context.getString(R.string.price) + ":", 3, 0, 0, 0, 0, 0);
        data = addLeftSpaceTillLength(FormatUtil.removeDot(detail.getTotal() - detail.getDiscount()) + "", 8);
        pos.POS_TextOut(data, 3, 280, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        pos.POS_FeedLine();  //跳行

        //14.尾數折讓
        if (detail.getTotalDischarge() != 0.0) {
            pos.POS_TextOut(context.getString(R.string.tailDiscount) + ":", 3, 0, 0, 0, 0, 0);
            String tailDiscount = addLeftSpaceTillLength(detail.getTotalDischarge() * -1 + "", 8);
            pos.POS_TextOut(tailDiscount, 3, 280, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
        }

        data = addLeftSpaceTillLength(FormatUtil.removeDot(detail.getTotalFee() + cashTicketAmount) + "", 8);
        //15.實收
        pos.POS_TextOut(context.getString(R.string.receive), 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut(data, 3, 280, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //16.支付
        pos.POS_TextOut(context.getString(R.string.pay), 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut(data, 3, 280, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        pos.POS_FeedLine();  //跳行

        //18~19.付款方式
        printPaymentData(detail.getPaymentList());

        //21.發票號碼
        if (receipt != null && !Config.disableReceiptModule) {
            String invNo = receipt.getEReceiptNumber();
            data = context.getString(R.string.invNo) + invNo;
            pos.POS_TextOut(data, 3, 0, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
        }

        //23.捐贈碼
        if (receipt != null && receipt.getLoveCode() != null && receipt.getLoveCode().length() > 0) {
            data = context.getString(R.string.donateNo) + receipt.getLoveCode();
            pos.POS_TextOut(data, 3, 0, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
        }

        //24.載具
        if (receipt != null && receipt.getCarrierNumber() != null && receipt.getCarrierNumber().length() > 0) {
            String carrierNumber = receipt.getCarrierNumber();
            pos.POS_TextOut(context.getString(R.string.invVehicle) + carrierNumber, 3, 0, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
        }

        //24.統一編號
        if (receipt != null && receipt.getBuyerGuiNumber() != null && receipt.getBuyerGuiNumber().length() > 0) {
            String buyerNumber = receipt.getBuyerGuiNumber();
            pos.POS_TextOut(context.getString(R.string.buyerNumber) + buyerNumber, 3, 0, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
        }

        //悠遊卡交易
        if (detail.getEcPayData() != null) {
            printEcSaleData(detail.getEcPayData());
        }


        if(OrderManager.getInstance().isLogin() || OrderManager.getInstance().isGuestLogin()){
            pos.POS_TextOut("----- 紅利點數收據明細 -----", 3, 0, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
            int space = 150;
            pos.POS_TextOut("本次新增點數:", 3, 0, 0, 0, 0, 0); // 列印Big5字串
            data = addLeftSpaceTillLength(OrderManager.getInstance().getPoints().getAdd() + "", 17);
            pos.POS_TextOut(data, 3, 0, 0, 0, 0, 0); // 列印Big5字串
            pos.POS_FeedLine();  //跳行

            pos.POS_TextOut("過去累積點數:", 3, 0, 0, 0, 0, 0); // 列印Big5字串
            data = addLeftSpaceTillLength(OrderManager.getInstance().getPoints().getBefore_transaction()+ "", 17);
            pos.POS_TextOut(data, 3, 0, 0, 0, 0, 0); // 列印Big5字串
            pos.POS_FeedLine();  //跳行

            pos.POS_TextOut("本次使用點數:", 3, 0, 0, 0, 0, 0); // 列印Big5字串
            data = addLeftSpaceTillLength(String.valueOf(OrderManager.getInstance().getPoints().getUse()), 17);
            pos.POS_TextOut(data, 3, 0, 0, 0, 0, 0); // 列印Big5字串
            pos.POS_FeedLine();  //跳行

            pos.POS_TextOut("本次使用點數:", 3, 0, 0, 0, 0, 0); // 列印Big5字串
            data = addLeftSpaceTillLength(""+OrderManager.getInstance().getPoints().getAfter_transaction(), 17);
            pos.POS_TextOut(data, 3, 0, 0, 0, 0, 0); // 列印Big5字串
            pos.POS_FeedLine();  //跳行
        }

        //信用卡交易
        if (detail.getNcccTransDataBean() != null) {
            printNCCCTransData(detail.getNcccTransDataBean());
        }
        //佐登妮絲提示訊息
        if (BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name())) {
            pos.POS_TextOut(strokeLine, 3, 0, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
            if (Config.isShopping) {
                data = context.getString(R.string.unpickedUp) + ", " + context.getString(R.string.saveReceipt);
            } else if (Config.isRestaurant) {
                data = context.getString(R.string.untakenMeal) + ", " + context.getString(R.string.saveReceipt);
            } else {
                data = context.getString(R.string.saveReceipt);
            }
            pos.POS_TextOut(data, 3, 0, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
            pos.POS_S_Align(1);  //置中
            pos.POS_TextOut(context.getString(R.string.goToCounter), 3, 0, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
            pos.POS_S_Align(0);  //置左
        }
        //26.----
        pos.POS_TextOut(strokeLine, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //27.此單據內容僅供參考，不作為交易憑證
        pos.POS_TextOut(context.getString(R.string.receiptBottomMsg), 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  // 列印空白點數

        //28.time
        pos.POS_TextOut(getTime(), 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        pos.POS_S_Align(1);  //置中
//        if(!OrderManager.getInstance().isLogin() || OrderManager.getInstance().isGuestLogin()) {
//            //29.barCode
//            int height = 60;
//            int width = 2;
//            int barCodeType = 73;  //BAR_CODE_CODE128
//            data = detail.getOrderId();
//            pos.POS_S_SetBarcode(data, 0, barCodeType, width, height, 0, 0);
//        }

        if((!BasicSettingsManager.Companion.getInstance().getBasicSetting().getRegister_url().isEmpty())) {
            pos.POS_FeedLine();
            pos.POS_TextOut("立即加入會員", 3, 0, 1, 1, 0, 0);
        }
        //30.QRCode
        int QrCodeWidth = 4;
        final byte QRCodeVersion = 7;
        final int errorCorrectionLevel = 1;
        pos.POS_FeedLine();
        if ((!BasicSettingsManager.Companion.getInstance().getBasicSetting().getRegister_url().isEmpty())) {
            pos.POS_S_SetQRcode(BasicSettingsManager.Companion.getInstance().getBasicSetting().getRegister_url(), QrCodeWidth, QRCodeVersion, errorCorrectionLevel);
            pos.POS_FeedLine();
        }
        BasicSettings basicSettings = BasicSettingsManager.getInstance().getBasicSetting();
        if (basicSettings != null && basicSettings.getPrint_order_qrcode()) {
            pos.POS_TextOut("訂單QR Code", 3, 0, 1, 1, 0, 0);
            pos.POS_FeedLine();
            pos.POS_S_SetQRcode(detail.getOrderId(), QrCodeWidth, QRCodeVersion, errorCorrectionLevel);
            pos.POS_FeedLine();
        }

        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行

        //end
        pos.POS_HalfCutPaper();
        return super.printReceipt(context, receipt, detail);
    }

    private void printTakeOutNumber(Context context, ReceiptDetail detail) {
        String data;//4.內用外帶桌號
        //2.時間
        pos.POS_TextOut(getTime(), 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //3.店號 機 序
        data = Config.shop_id + "-" + Config.kiosk_id + "-" + detail.getOrderId();
        pos.POS_TextOut(data, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        if (BuildConfig.FLAVOR.equals("TFG")) {
            data = Config.shopName;
            pos.POS_TextOut("店名：" + data, 3, 0, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
        }

        data = detail.getSaleType();
        if (BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name()) && Config.isShopping) {
            data = "取貨方式:" + detail.getPickupMethod().getName();
        }
        if (detail.getTableNumber() != null && detail.getTableNumber().length() > 0) {
            data += "/桌號-" + detail.getTableNumber();
        }
        pos.POS_TextOut(data, 3, 0, 1, 1, 0, 0);
        pos.POS_FeedLine();  //跳行
        if (!BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name())){
            //5.取餐號碼
            data = context.getString(R.string.takeMealNo) + detail.getTakeMealNumber();
            pos.POS_TextOut(data, 3, 0, 1, 1, 0, 0);
            pos.POS_FeedLine();  //跳行
        }
    }

    @Override
    public int printActivityMessage(Context context, String activityMessage) {
        //2.時間
        pos.POS_S_Align(1);  //置中
        pos.POS_TextOut("好 康 兌 換",3, 0, 1, 1, 0, 0);
        pos.POS_FeedLine();  //跳行
        pos.POS_TextOut(getTime(), 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        pos.POS_S_Align(0);  //置左
        String data;
        data = activityMessage;
        pos.POS_TextOut(data,3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        //end
        pos.POS_HalfCutPaper();
        return super.printActivityMessage(context, activityMessage);
    }

    private void printEcSaleData(EcPayData ecPayData) {
        int space = 150;
        //------------
        pos.POS_TextOut("-----悠遊卡交易明細-----", 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //交易時間
        String date = ecPayData.getTxn_Date();
        String time = ecPayData.getTxn_Time();
        String printDate = date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6);
        String printTime = time.substring(0, 2) + ":" + time.substring(2, 4) + ":" + time.substring(4);
        pos.POS_TextOut("交易時間:" + printDate + " " + printTime, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //悠遊卡卡號
        pos.POS_TextOut("悠遊卡卡號", 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut(":" + ecPayData.getReceipt_card_number(), 3, space, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //交易類別
        pos.POS_TextOut("交易類別", 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut(":悠遊卡扣款", 3, space, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //二代設備編號
        pos.POS_TextOut("二代設備編號", 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut(":" + ecPayData.getNew_deviceID(), 3, space, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //批次號碼
        pos.POS_TextOut("批次號碼", 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut(":" + ecPayData.getBatch_number(), 3, space, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //RRN
        pos.POS_TextOut("RRN", 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut(":" + ecPayData.getRRN(), 3, space, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //交易前餘額
        pos.POS_TextOut("交易前餘額", 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut(":" + ecPayData.getN_CPU_EV_Before_TXN(), 3, space, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //自動加值金額
        pos.POS_TextOut("自動加值金額", 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut(":" + ecPayData.getN_AutoLoad_Amount(), 3, space, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //交易金額
        pos.POS_TextOut("交易金額", 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut(":" + ecPayData.getN_CPU_Txn_AMT(), 3, space, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //交易後餘額
        pos.POS_TextOut("交易後餘額", 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut(":" + ecPayData.getN_CPU_EV(), 3, space, 0, 0, 0, 0);
        pos.POS_FeedLine(); //跳行
    }

    private void printNCCCTransData(NCCCTransDataBean dataBean) {
        String title;
        title = "E".equals(dataBean.getEsvcIndicator()) ? "-----悠遊卡交易明細-----" : "-----信用卡交易明細-----";
        pos.POS_TextOut(title, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        pos.POS_TextOut("聯合信用卡處理中心", 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        pos.POS_TextOut("交易日期: " + dataBean.getTransDate() + dataBean.getTransTime(), 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        String terminalId = dataBean.getTerminalId();
        pos.POS_TextOut("端末機: " + terminalId, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        String receiptNo = dataBean.getReceiptNo();
        String approvalNo = dataBean.getApprovalNo().trim();
        pos.POS_TextOut("調閱編號: " + receiptNo + "  授權碼: " + approvalNo, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        String cardName = NCCCCode.getCardTypeName(dataBean.getCardType());
        pos.POS_TextOut("卡別: " + cardName, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        String cardNo = dataBean.getCardNo();
        pos.POS_TextOut("卡號: " + cardNo, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        String transAmount = dataBean.getTransAmount();
        String payAmount = "＄" + NCCCTransDataBean.parseAmount(transAmount);
        pos.POS_TextOut("交易金額: " + payAmount, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
    }

    @Override
    public int printBill(Context context) {
        OrderResponse orderResponse = Bill.fromServer.getOrderResponse();

        String data;
        String strokeLine = getStrokeLine();

        pos.POS_Reset();  //打印機歸位

        //1.付款單
        pos.POS_S_Align(1);  //置中
        pos.POS_TextOut(context.getString(R.string.bill), 3, 0, 1, 1, 0, 0);
        pos.POS_FeedLine();  //跳行

        //2.時間
        pos.POS_S_Align(0);  //置左
        pos.POS_TextOut(getTime(), 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //3.店號 機 序
        data = Config.shop_id + "-" + Config.kiosk_id + "-" + Bill.fromServer.worder_id;
        pos.POS_TextOut(data, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        //若為智葳版本跳過內用外帶的字眼
        if (!"brogent".equals(BuildConfig.FLAVOR)) {
            //4.內用外帶桌號
            data = Bill.fromServer.getInOut_String(context);
            if (orderResponse.getTable_no() != null && orderResponse.getTable_no().length() > 0) {
                data += "/桌號-" + orderResponse.getTable_no();
            }
            pos.POS_TextOut(data, 3, 0, 1, 1, 0, 0);
            pos.POS_FeedLine();  //跳行
        }

        //6.商品 數量 原價 小計
        pos.POS_TextOut(strokeLine, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        pos.POS_TextOut(context.getString(R.string.product), 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut(context.getString(R.string.quantity), 3, 110, 0, 0, 0, 0);
        pos.POS_TextOut(context.getString(R.string.originPrice), 3, 220, 0, 0, 0, 0);
        pos.POS_TextOut(context.getString(R.string.subtoal), 3, 330, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        pos.POS_TextOut(strokeLine, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        // 7. 商品列表
        List<WebOrder01> webOrder01List = Bill.fromServer.getOrderResponse().getWeborder01();
        for (WebOrder01 webOrder01 : webOrder01List) {
            double salePrice = ComputationUtils.parseDouble(webOrder01.getSale_price());
            int qty = (int) ComputationUtils.parseDouble(webOrder01.getQty());
            int itemDisc = (int) ComputationUtils.parseDouble(webOrder01.getItem_disc());
            pos.POS_TextOut(webOrder01.getProd_name1(), 3, 0, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
            if (webOrder01.getTaste_memo() != null && webOrder01.getTaste_memo().length() > 0) {
                pos.POS_TextOut("  **" + webOrder01.getTaste_memo(), 3, 0, 0, 0, 0, 0);
                pos.POS_FeedLine();  //跳行
            }
            data = qty + "";
            pos.POS_TextOut(addLeftSpaceTillLength(data, 4), 3, 110, 0, 0, 0, 0);
            data = salePrice * qty + "";
            pos.POS_TextOut(addLeftSpaceTillLength(data, 8), 3, 170, 0, 0, 0, 0);
            data = salePrice * qty + itemDisc + "";
            pos.POS_TextOut(addLeftSpaceTillLength(data, 8), 3, 280, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
        }
        pos.POS_FeedLine();  //跳行
        pos.POS_TextOut(strokeLine, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //8.總金額
        pos.POS_TextOut(context.getString(R.string.totalAmount), 3, 0, 0, 0, 0, 0);
        try {
            String total = Bill.fromServer.getOrderResponse().getTotal() + "";
            pos.POS_TextOut(addLeftSpaceTillLength(total, 8), 3, 280, 0, 0, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        pos.POS_FeedLine();  //跳行

        //14.尾數折讓
        if (orderResponse.getDiscount() != 0) {
            pos.POS_TextOut(context.getString(R.string.tailDiscount) + ":", 3, 0, 0, 0, 0, 0);
            String discount = addLeftSpaceTillLength("-"+orderResponse.getDiscount() + "", 8);
            pos.POS_TextOut(discount, 3, 280, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
        }

        //付款方式
        List<WebOrder02> webOrder02List = orderResponse.getWeborder02();
        if (webOrder02List != null) {
            for (WebOrder02 webOrder02 : webOrder02List) {
                String payName = getPayName(context, webOrder02.getPay_id());
                pos.POS_TextOut(payName + ":", 3, 0, 0, 0, 0, 0);
                String payAmount = addLeftSpaceTillLength(removeDot(webOrder02.getAmount()), 8);
                pos.POS_TextOut(payAmount, 3, 280, 0, 0, 0, 0);
                pos.POS_FeedLine();  //跳行
            }
        }
        pos.POS_FeedLine();  //跳行

        //9.請至櫃檯結帳，謝謝您
        pos.POS_TextOut(context.getString(R.string.plsPayAtCounter), 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //11.barCode
        int height = 60;
        int width = 2;
        int barCodeType = 73;  //BAR_CODE_CODE128
        pos.POS_S_Align(1);  //置中
        data = Bill.fromServer.worder_id;
        pos.POS_S_SetBarcode(data, 0, barCodeType, width, height, 0, 0);

        //12.QRCode
        int QrCodeWidth = 3;
        final byte QRCodeVersion = 7;
        final int errorCorrectionLevel = 1;
        pos.POS_S_SetQRcode(data, QrCodeWidth, QRCodeVersion, errorCorrectionLevel);
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行

        //end
        pos.POS_HalfCutPaper();
        return super.printBill(context);
    }

    @Override
    public int printBill(Context context, ReceiptDetail detail) {
        String data;
        String strokeLine = getStrokeLine();

        pos.POS_Reset();  //打印機歸位

        //1.付款單
        pos.POS_S_Align(1);  //置中
        pos.POS_TextOut(context.getString(R.string.bill), 3, 0, 1, 1, 0, 0);
        pos.POS_FeedLine();  //跳行

        //2.時間
        pos.POS_S_Align(0);  //置左
        pos.POS_TextOut(getTime(), 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //3.店號 機 序
        data = Config.shop_id + "-" + Config.kiosk_id + "-" + detail.getOrderId();
        pos.POS_TextOut(data, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        //若為智葳版本跳過內用外帶的字眼
        if (!"brogent".equals(BuildConfig.FLAVOR)) {
            //4.內用外帶桌號
            data = detail.getSaleType();
            if (detail.getTableNumber() != null && detail.getTableNumber().length() > 0) {
                data += "/桌號-" + detail.getTableNumber();
            }

            pos.POS_TextOut(data, 3, 0, 1, 1, 0, 0);
            pos.POS_FeedLine();  //跳行

        }
        //6.商品 數量 原價 小計
        pos.POS_TextOut(strokeLine, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        pos.POS_TextOut(context.getString(R.string.product), 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut(context.getString(R.string.quantity), 3, 180, 0, 0, 0, 0);
        if(BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name())){
            pos.POS_TextOut(context.getString(R.string.originPrice), 3, 220, 0, 0, 0, 0);
        }
        pos.POS_TextOut(context.getString(R.string.price), 3, 330, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        pos.POS_TextOut(strokeLine, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        // 7. 商品列表
        List<Product> productList = detail.getProductList();
        printProductDetail(productList);

        pos.POS_FeedLine();  //跳行
        pos.POS_TextOut(strokeLine, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //8.合計
        //12~13.總金額
        pos.POS_TextOut(context.getString(R.string.totalAmount) + ":", 3, 0, 0, 0, 0, 0);
        data = addLeftSpaceTillLength(FormatUtil.removeDot(detail.getTotal()) + "", 8);
        pos.POS_TextOut(data, 3, 280, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //8.折讓
        double cashTicketAmount = 0.0;
        for (Payment payment : detail.getPaymentList()) {
            if(payment.getType().equals("CASH_TICKET")){
                cashTicketAmount = payment.getAmount() * 1.0;
            }
        }
        double discount = detail.getTotalFee() - detail.getTotalDischarge() + cashTicketAmount - detail.getTotal();
        if(discount > 0.0){
            pos.POS_TextOut(context.getString(R.string.discount) + ":", 3, 0, 0, 0, 0, 0);
            data = addLeftSpaceTillLength("-"+FormatUtil.removeDot(discount), 8);
            pos.POS_TextOut(data, 3, 280, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
        }

        //8.金額
        pos.POS_TextOut(context.getString(R.string.price) + ":", 3, 0, 0, 0, 0, 0);
        data = addLeftSpaceTillLength(FormatUtil.removeDot(detail.getTotal() - detail.getDiscount()) + "", 8);
        pos.POS_TextOut(data, 3, 280, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        pos.POS_FeedLine();  //跳行

        //14.尾數折讓
        if (detail.getTotalDischarge() != 0.0) {
            pos.POS_TextOut(context.getString(R.string.tailDiscount) + ":", 3, 0, 0, 0, 0, 0);
            String tailDiscount = addLeftSpaceTillLength(detail.getTotalDischarge() * -1 + "", 8);
            pos.POS_TextOut(tailDiscount, 3, 280, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
        }

        //付款方式
        List<Payment> paymentList = detail.getPaymentList();
        printPaymentData(paymentList);

        //8.尚餘
        pos.POS_TextOut(context.getString(R.string.balance) + ":", 3, 0, 0, 0, 0, 0);
        data = addLeftSpaceTillLength(FormatUtil.removeDot(detail.getTotalFee()) + "", 8);
        pos.POS_TextOut(data, 3, 280, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        pos.POS_TextOut(strokeLine, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        //9.請至櫃檯結帳，謝謝您
        pos.POS_TextOut(context.getString(R.string.plsPayAtCounter), 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //11.barCode
        int height = 60;
        int width = 2;
        int barCodeType = 73;  //BAR_CODE_CODE128
        pos.POS_S_Align(1);  //置中
        data = detail.getOrderId();
        pos.POS_S_SetBarcode(data, 0, barCodeType, width, height, 0, 0);

        //12.QRCode
        int QrCodeWidth = 3;
        final byte QRCodeVersion = 7;
        final int errorCorrectionLevel = 1;
        pos.POS_S_SetQRcode(data, QrCodeWidth, QRCodeVersion, errorCorrectionLevel);
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行

        //end
        pos.POS_HalfCutPaper();
        return super.printBill(context, detail);
    }

    @Override
    public int printGamePoint(Context context) {
        OrderResponse orderResponse = Bill.fromServer.getOrderResponse();
        String data;
        String strokeLine = getStrokeLine();
        data = Bill.fromServer.worder_id;

        pos.POS_Reset();  //打印機歸位
        //logo
        pos.POS_S_Align(1);  //置中

        printLogo();
        //29.barCode
        int height = 60;
        int width = 2;
        int barCodeType = 73;  //BAR_CODE_CODE128
        pos.POS_S_Align(1);  //置中

        //30.QRCode
        int QrCodeWidth = 4;
        final byte QRCodeVersion = 7;
        final int errorCorrectionLevel = 1;
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_S_SetQRcode(data, QrCodeWidth, QRCodeVersion, errorCorrectionLevel);
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行

        //1.收據
        pos.POS_TextOut("5G體感 電競基地", 3, 0, 1, 1, 0, 0);

        pos.POS_FeedLine();  //跳行
        pos.POS_TextOut(" ", 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        pos.POS_S_Align(1);  //置中
        pos.POS_TextOut("票券編號:" + Bill.fromServer.worder_id, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_TextOut(strokeLine, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_TextOut("購 買 明 細", 3, 0, 1, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        printHorizontalLine();
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_S_Align(0);  //靠左
        pos.POS_TextOut("購買時間" + ":", 3, 0, 0, 0, 0, 0);
        data = addLeftSpaceTillLength(Bill.fromServer.getOrderResponse().getInput_date(), 32);
        pos.POS_TextOut(data, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        pos.POS_TextOut("商品明細" + ":", 3, 0, 0, 0, 0, 0);
        // 9~11. 商品列表
        List<WebOrder01> webOrder01List = Bill.fromServer.getOrderResponse().getWeborder01();
        for (WebOrder01 webOrder01 : webOrder01List) {
            double salePrice = ComputationUtils.parseDouble(webOrder01.getSale_price());
            int qty = (int) ComputationUtils.parseDouble(webOrder01.getQty());
            data = addLeftSpaceTillLength(webOrder01.getProd_name1() + "*" + qty + "", 20);
            pos.POS_TextOut(data, 3, 0, 0, 0, 0, 0);
            data = addLeftSpaceTillLength(salePrice * qty + "", 17);
            pos.POS_TextOut(data, 3, 170, 0, 0, 0, 0);
            pos.POS_FeedLine();  //跳行
        }
        //12~13.總金額
        pos.POS_TextOut(context.getString(R.string.totalAmount) + ":", 3, 0, 0, 0, 0, 0);
        data = addLeftSpaceTillLength(orderResponse.getTot_sales() + "", 8);
        pos.POS_TextOut(data, 3, 280, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_TextOut("附註:", 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        pos.POS_TextOut("1.請憑QR code點數票券入場遊玩。", 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        pos.POS_TextOut("2.請妥善保存此點數票券及購買明細，以免影響遊玩權益。如發生遺失、破損、燒毀或無法辨識等狀況，恕不補發。票券有效狀態依主辦單位之系統驗證結果為準，主辦單位有最終解釋權。", 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        pos.POS_TextOut("3.請憑點數票券與購買明細進行商品退換貨。", 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        //28.time
        pos.POS_S_Align(1);  //置中
        pos.POS_TextOut("列印時間:" + getTime(), 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        //end
        pos.POS_HalfCutPaper();
        return super.printGamePoint(context);
    }

    @Override
    public int printTicket(Context context, Ticket ticket) {
        pos.POS_Reset();  //打印機歸位
        pos.POS_S_Align(1);  //置中

        printLogo();

        //QRCode
        int QrCodeWidth = 4;
        final byte QRCodeVersion = 7;
        final int errorCorrectionLevel = 1;
        pos.POS_S_SetQRcode(ticket.getQrCodeData(), QrCodeWidth, QRCodeVersion, errorCorrectionLevel);
        pos.POS_FeedLine();  //跳行

        pos.POS_TextOut(ticket.getSubject(), 3, 0, 1, 1, 0, 0);
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行

        pos.POS_S_Align(0);  //置左
        pos.POS_TextOut("參觀日期:" + ticket.getDate(), 3, 30, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        pos.POS_TextOut("參觀時間:" + ticket.getTime(), 3, 30, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        pos.POS_TextOut("票種:" + ticket.getKind(), 3, 30, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        pos.POS_TextOut("票價:" + ticket.getPrice(), 3, 30, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        pos.POS_TextOut("可抵用金額:" + ticket.getDiscount(), 3, 30, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        pos.POS_TextOut("票券編號:" + ticket.getTicketNumber(), 3, 30, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行

        pos.POS_TextOut("列印時間:" + getTime(), 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_HalfCutPaper();
        return super.printTicket(context, ticket);
    }

    @Override
    public int printEasyCardCheckOutData(EcCheckoutData ecCheckoutData) {
        String data;
        int space = 200;

        pos.POS_Reset();  //打印機歸位

        //logo
        pos.POS_S_Align(1);  //置中

        //悠遊卡結帳憑證
        pos.POS_TextOut("悠遊卡結帳憑證", 3, 0, 1, 1, 0, 0);
        pos.POS_FeedLine();  //跳行

        //門市
        pos.POS_S_Align(0);  //置左
        pos.POS_TextOut("門市", 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut(":" + Config.shop_id, 3, space, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //交易序號
        pos.POS_TextOut("交易序號", 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut(":" + ecCheckoutData.getTXN_Serial_number(), 3, space, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //------------------------
        pos.POS_TextOut("-----------------------------", 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //交易時間
        data = "交易時間:" + ecCheckoutData.getTXN_DateTime().replace("-", "/");
        pos.POS_TextOut(data, 3, 0, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //平帳
        pos.POS_TextOut("平帳", 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut(":" + (ecCheckoutData.getSettlement_Status().equals("0") ? "平帳" : "不平帳"), 3, space, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //二代設備編號
        pos.POS_TextOut("二代設備編號", 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut(":" + ecCheckoutData.getNew_Deviceid(), 3, space, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //批次號碼
        pos.POS_TextOut("批次號碼", 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut(":" + ecCheckoutData.getBatch_number(), 3, space, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        pos.POS_FeedLine();  //跳行

        //購貨筆數
        pos.POS_TextOut("購貨筆數    :" + ecCheckoutData.getDeduct_count(), 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut("總額  :" + ecCheckoutData.getDeduct_amt(), 3, space, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //退貨筆數
        pos.POS_TextOut("退貨筆數    :" + ecCheckoutData.getRefund_count(), 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut("總額  :" + ecCheckoutData.getRefund_amt(), 3, space, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //自動加值筆數
        pos.POS_TextOut("自動加值筆數:" + ecCheckoutData.getAddValue_count(), 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut("總額  :" + ecCheckoutData.getTotal_AddValue_amt(), 3, space, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //購貨類總筆數
        pos.POS_TextOut("購貨類總筆數:" + ecCheckoutData.getTotal_Txn_count(), 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut("總金額:" + ecCheckoutData.getTotal_Txn_amt(), 3, space, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        pos.POS_TextOut("總淨額:" + ecCheckoutData.getTotal_Txn_pure_amt(), 3, space, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行

        //交易總筆數
        pos.POS_TextOut("交易總筆數 ", 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut(":＄" + ecCheckoutData.getTotal_Txn_count(), 3, space, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行

        //交易總額
        pos.POS_TextOut("交易總額 ", 3, 0, 0, 0, 0, 0);
        pos.POS_TextOut(":＄" + ecCheckoutData.getTotal_Txn_amt(), 3, space, 0, 0, 0, 0);
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行

        //end
        pos.POS_HalfCutPaper();
        return super.printEasyCardCheckOutData(ecCheckoutData);
    }

    @Override
    public int printCreditCardReceipt(Context context) {
        pos.POS_Reset();  //打印機歸位
        NCCCTransDataBean dataBean = Bill.fromServer.getNcccTransDataBean();
        printNCCCTransData(dataBean);
        pos.POS_FeedLine();  //跳行
        pos.POS_TextOut(context.getString(R.string.creditCardReceiptBottom), 3, 0, 0, 0, 0, 0);
        cutPaper();
        return super.printCreditCardReceipt(context);
    }

    @Override
    public void cutPaper() {
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        pos.POS_FeedLine();  //跳行
        //end
        pos.POS_HalfCutPaper();
        super.cutPaper();
    }

    @Override
    public void showPrinterErrorDialog(Context context, PrinterErrorDialog.Listener listener, String message) {
        new PrinterErrorDialog(context, listener, message).show();
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

    public void printerStatusCheck(final Context context, final PrinterErrorDialog.Listener dialogListener, final PrinterCheckFlowListener printerCheckFlowListener, final boolean beforePrint) {
        byte[] status = new byte[1];
        String msg = null;

        if (pos.POS_RTQueryStatus(status, 2, 1000, 2)) {
            if ((status[0] & 0x04) == 0x04) {
                msg = "出單機蓋為開啟";
                addLog(msg + ", code：" + status[0]);
            } else {
                addLog("檢查出單機蓋 OK");
            }

            if ((status[0] & 0x20) == 0x20) {
                msg = context.getString(R.string.printerPaperEnd);
                addLog(msg + ", code：" + status[0]);
            } else {
                addLog("檢查缺紙 OK");
            }
        }

        if (beforePrint) {
            if (pos.POS_RTQueryStatus(status, 3, 1000, 2)) {
                if ((status[0] & 0x08) == 0x08) {
                    msg = String.format(context.getString(R.string.printerNotOkReason), "請開啟重新放紙");
                    addLog(msg + ", code：" + status[0]);
                } else {
                    addLog("檢查切刀異常 OK");
                }

                if ((status[0] & 0x40) == 0x40) {
                    msg = String.format(context.getString(R.string.printerNotOkReason), "出單機過熱");
                    addLog(msg + ", code：" + status[0]);
                } else {
                    addLog("檢查出單機過熱 OK");
                }
            }
        }

        if (msg != null) {
            showPrinterErrorDialog(context, dialogListener, msg);
            crashlyticsLog(status[0], isPaperOut, beforePrint);
        } else {
            if (beforePrint) {
                paperOutCheck(context, dialogListener, printerCheckFlowListener, beforePrint);
            } else {
                if (isPaperOut) {
                    printerCheckFlowListener.onFinish();
                    isPaperOut = false;
                } else {
                    showPrinterErrorDialog(context, dialogListener, null);
                    crashlyticsLog(status[0], isPaperOut, beforePrint);
                }
            }
        }
    }

    public void paperOutCheck(final Context context, final PrinterErrorDialog.Listener dialogListener,
                              final PrinterCheckFlowListener printerCheckFlowListener, final boolean beforePrint) {
        byte[] status = new byte[1];
        String msg = null;

        if (pos.POS_RTQueryStatus(status, 1, 1000, 2)) {
            if ((status[0] & 0x80) == 0x80) {
                msg = context.getString(R.string.printerPaperPresentAlert);
                isPaperOut = true;
            }
        }

        addLog("isPaperOut：" + isPaperOut);

        if (beforePrint) {
            if (isPaperOut) {
                showPrinterErrorDialog(context, dialogListener, msg);
                isPaperOut = false;
            } else {
                printerCheckFlowListener.onFinish();
            }
        }
    }

    private void printTicketInfo() {
        Drawable drawable = context.getResources().getDrawable(R.drawable.ticket_info);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        pos.POS_PrintPicture(bitmap, 400, 0, 0);
    }

    private void printHorizontalLine() {
        Drawable drawable = context.getResources().getDrawable(R.drawable.horizontal_line);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        pos.POS_PrintPicture(bitmap, 400, 0, 0);
    }

    private void printLogo() {
        byte[] printLogoCommand = {0x1C, 0x70, 0x01, 0x30};
        pos.GetIO().Write(printLogoCommand, 0, 4);
    }
}
