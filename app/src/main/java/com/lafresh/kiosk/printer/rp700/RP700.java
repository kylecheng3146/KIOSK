package com.lafresh.kiosk.printer.rp700;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.lafresh.kiosk.Bill;
import com.lafresh.kiosk.BuildConfig;
import com.lafresh.kiosk.Config;
import com.lafresh.kiosk.R;
import com.lafresh.kiosk.Tool;
import com.lafresh.kiosk.creditcardlib.nccc.NCCCCode;
import com.lafresh.kiosk.creditcardlib.nccc.NCCCTransDataBean;
import com.lafresh.kiosk.easycard.model.EcCheckoutData;
import com.lafresh.kiosk.easycard.model.EcPayData;
import com.lafresh.kiosk.httprequest.model.Invoice;
import com.lafresh.kiosk.httprequest.model.OrderResponse;
import com.lafresh.kiosk.httprequest.model.WebOrder01;
import com.lafresh.kiosk.httprequest.model.WebOrder02;
import com.lafresh.kiosk.printer.KioskPrinter;
import com.lafresh.kiosk.printer.model.EReceipt;
import com.lafresh.kiosk.printer.model.Payment;
import com.lafresh.kiosk.printer.model.Product;
import com.lafresh.kiosk.printer.model.ReceiptDetail;
import com.lafresh.kiosk.printer.model.Ticket;
import com.lafresh.kiosk.utils.ComputationUtils;
import com.lafresh.kiosk.utils.FormatUtil;

import java.io.UnsupportedEncodingException;
import java.util.List;

import wpprinter.printer.WpPrinter;

import static com.lafresh.kiosk.utils.FormatUtil.addLeftSpaceTillLength;


public class RP700 extends KioskPrinter {
    public static final int VENDOR_ID = 1900;
    public static final int PRODUCT_ID = 770;

    private Context context;
    private RP700Receiver receiver;
    private HardwareConnectionHelper helper;
    private PrinterCommand command;
    private boolean connected;

    private static final String SEPARATE_LINE = "------------------------------";

    public RP700(Context context) {
        this.context = context;
        this.helper = new HardwareConnectionHelper();
        command = new PrinterCommandESC(new PrinterReceiverRawUsb(helper));
        setReceiver();
    }

    private void setReceiver() {
        receiver = new RP700Receiver(this);
        String filterTag = VENDOR_ID + "-" + PRODUCT_ID;
        IntentFilter intentFilter = new IntentFilter(filterTag);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        intentFilter.addAction(UsbManager.EXTRA_PERMISSION_GRANTED);
        context.registerReceiver(receiver, intentFilter);
    }

    @Override
    public void releaseAll(Context context) {
        if (receiver != null) {
            context.unregisterReceiver(receiver);
            receiver = null;
        }
        if (isConnect()) {
            disconnect();
        }
        helper = null;
        command = null;
        releasePrinter();
    }

    @Override
    public void requestPrinterPermission(Context context) {
        requestUsbPermission(context, VENDOR_ID, PRODUCT_ID);
    }

    @Override
    public boolean disconnect() {
        return helper.closeUsb();
    }

    @Override
    public boolean isConnect() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    @Override
    public void requestUsbPermission(Context context, int vendorId, int productId) {
        super.requestUsbPermission(context, vendorId, productId);
    }

    @Override
    public boolean connectUsb(UsbDevice usbDevice) {
        return helper.openUsb(context, usbDevice);
    }

    @Override
    public int printEInvoice(Context context) {
        Invoice invoice = Bill.fromServer.getOrderResponse().getInvoice();
        String testGuiNo = context.getString(R.string.testGuiNo);
        String guiNo = invoice.getGUI_number();

        command.initReceiptTitleFormat();
        command.printFeedLine(80);

        //??????????????????
        if (testGuiNo.equals(guiNo)) {
            String testString = context.getString(R.string.testReceipt);
            command.printString(testString);
            command.printFeedLine(20);
        }

        //1.logo or title
        String invoiceTitle = invoice.getInvoice_title();
        if (invoiceTitle == null) {
            command.printLogo();
        } else {
            command.printString(invoiceTitle);
        }
        command.printFeedLine(20);

        //2.?????????????????????
        command.printString(context.getString(R.string.eInvPrintTitle));
        command.printFeedLine(20);

        //3.????????????
        String twYear = invoice.getTwYearString();
        String beginMonth = invoice.getStartMonthString();
        String endMonth = invoice.getEndMonthString();
        String invoiceBatch = twYear + context.getString(R.string.year) + beginMonth + "-" + endMonth + context.getString(R.string.month);
        command.printString(invoiceBatch);
        command.printFeedLine(20);

        //4.????????????
        String invNo = invoice.getInv_no_b();
        String invoiceNo = invNo.substring(0, 2) + "-" + invNo.substring(2, 10);
        command.printString(invoiceNo);
        command.printFeedLine(20);

        command.initReceiptDetailsFormat();

        //5.????????????
        command.printString(getTime());
        command.printFeedLine(20);

        //?????????????????????????????????
        if (!Bill.fromServer.getCustCode().equals("") && !Bill.fromServer.getCustCode().equals("00000000")) {
            command.printString("  ?????? 25");
        }
        command.printFeedLine(20);

        //6a.???????????????
        command.printString("????????? " + invoice.getRandom_no());
        //6.b??????
        command.printString("    ??????  " + Bill.fromServer.getInvoiceAmount());
        command.printFeedLine(20);

        //7a.????????????code -------------------
        command.printString(context.getString(R.string.seller) + " " + guiNo);
        //7b.??????code
        String custCode = Bill.fromServer.getCustCode();
        if (custCode.length() > 0) {
            command.printString("   ");
            command.printString(context.getString(R.string.buyer) + " " + custCode);
        }
        command.printFeedLine(20);

        //8.??????
        String codeData = invoice.getBarcodeData();
        command.alignCenter();
        command.printBarCode39(codeData);

        // X 9.??????QRCode
        String qrCode77 = Tool.getQRCode77();
        String qrCodeData = Tool.getQRCodeData(Bill.fromServer.getOrderResponse().getWeborder01(), qrCode77);
        command.printDoubleQRCode(qrCodeData, 110);

        //10.?????? ??? ???
        String serial = Bill.fromServer.worder_id.substring(Bill.fromServer.worder_id.length() - 10);
        String shopDataStr = context.getString(R.string.shopId) + Config.shop_id + "  ??? " + Config.kiosk_id + " ???" + serial;
        command.printString(shopDataStr);
        command.printFeedLine(0);

        //11.??????
        command.printString(context.getString(R.string.eInvBottomMsg));
        command.printFeedLine(0);

        //??????????????????????????????
        if (Bill.fromServer.getCustCode().length() > 0) {
            command.printString("------------------------------");
            command.printFeedLine(20);
            printPaymentDetail(context);
        } else {
            command.cutPaperEnd();
        }
        return super.printEInvoice(context);
    }


    @Override
    public int printEInvoice(Context context, EReceipt receipt, ReceiptDetail detail) {
        String testGuiNo = context.getString(R.string.testGuiNo);
        String guiNo = receipt.getSellerGuiNumber();
        String data = "";

        command.initReceiptTitleFormat();
        command.printFeedLine(80);

        //??????????????????
        if (testGuiNo.equals(guiNo)) {
            String testString = context.getString(R.string.testReceipt);
            command.printString(testString);
            command.printFeedLine(20);
        }

        //1.logo or title
        String receiptTitle = receipt.getReceiptTitle();
        if (receiptTitle == null) {
            receiptTitle = "";
        }
        command.printString(receiptTitle);
        command.printFeedLine(20);

        //2.?????????????????????
        command.printString(context.getString(R.string.eInvPrintTitle));
        command.printFeedLine(20);

        //3.????????????
        String twYear = receipt.getTwYearString();
        String beginMonth = receipt.getStartMonth();
        String endMonth = receipt.getEndMonth();
        String invoiceBatch = twYear + context.getString(R.string.year) + beginMonth + "-" + endMonth + context.getString(R.string.month);
        command.printString(invoiceBatch);
        command.printFeedLine(20);

        //4.????????????
        String invNo = receipt.getEReceiptNumber();
        String invoiceNo = invNo.substring(0, 2) + "-" + invNo.substring(2, 10);
        command.printString(invoiceNo);
        command.printFeedLine(20);

        command.initReceiptDetailsFormat();

        //5.????????????
        command.printString(getTime());

        //6a.???????????????
        command.printString("????????? " + receipt.getRandomNumber());
        //6.b??????
        command.printString("    ??????  " + receipt.getTotalAmount());
        command.printFeedLine(20);

        //7a.????????????code -------------------
        command.printString(context.getString(R.string.seller) + " " + guiNo);
        //7b.??????code
        String buyerGuiNumber = receipt.getBuyerGuiNumber();
        if (buyerGuiNumber.length() > 0) {
            command.printString("   ");
            command.printString(context.getString(R.string.buyer) + " " + buyerGuiNumber);
        }
        command.printFeedLine(20);

        //8.??????
        String codeData = receipt.getBarCodeData();
        command.alignCenter();
        command.printBarCode39(codeData);

        // X 9.??????QRCode
        String[] qrCodeData = receipt.getQrCodeData();
        command.printDoubleQRCode(qrCodeData[0], 110);

        //10.?????? ??? ???
        String serial = detail.getOrderId().substring(detail.getOrderId().length() - 10);
        data = context.getString(R.string.shopId) + Config.shop_id + "  ??? " + Config.kiosk_id + " ???" + serial;
        command.printString(data);
        command.printFeedLine(0);

        //11.??????
        command.printString(context.getString(R.string.eInvBottomMsg));
        command.printFeedLine(0);

        //??????????????????????????????
        //------------------------------------------
        if (receipt.getBuyerGuiNumber().length() > 0) {
            command.printString("------------------------------");
            command.printFeedLine(20);
            printPaymentDetail(context, receipt, detail);
        } else {
            command.cutPaperEnd();
        }
        return super.printEInvoice(context, receipt, detail);
    }

    @Override
    public int printPaymentDetail(Context context) {
        command.initReceiptTitleFormat();
        command.printFeedLine(80);

        //1.????????????
        command.printString(context.getString(R.string.invDetail));
        command.printFeedLine(60);

        command.initReceiptProductFormat();

        // 5. ????????????
        List<WebOrder01> webOrder01List = Bill.fromServer.getOrderResponse().getWeborder01();
        printProductDetail(context, webOrder01List);

        //6.??????
        String[] subject = paddingRight(context.getString(R.string.subtotalWithColon), 12);
        String[] total = paddingLeft(Bill.fromServer.getInvoiceAmount() + "TX", 18);
        command.printString(subject[0] + total[0]);
        command.printFeedLine(60);

        command.printString(SEPARATE_LINE);
        command.printFeedLine(60);

        //7.????????????
        subject = paddingRight(context.getString(R.string.taxAmount), 12);
        String[] unTax = paddingLeft(Bill.fromServer.get_unTax() + "", 18);
        command.printString(subject[0] + unTax[0]);
        command.printFeedLine(60);

        //8.??????
        subject = paddingRight(context.getString(R.string.taxFreeAmt), 12);
        String[] noTax = paddingLeft("0", 18);
        command.printString(subject[0] + noTax[0]);
        command.printFeedLine(60);

        //9.??????
        subject = paddingRight("???      ???", 12);
        String[] taxAmount = paddingLeft(Bill.fromServer.getTax() + "", 18);
        command.printString(subject[0] + taxAmount[0]);
        command.printFeedLine(60);

        //10.??????
        subject = paddingRight("???      ???", 12);
        total = paddingLeft(Bill.fromServer.getInvoiceAmount() + "", 18);
        command.printString(subject[0] + total[0]);
        command.printFeedLine(60);

        command.printString(SEPARATE_LINE);
        command.printFeedLine(60);

        //.12????????????
        List<WebOrder02> webOrder02List = Bill.fromServer.getOrderResponse().getWeborder02();
        printPaymentData(context, webOrder02List);

        command.cutPaperEnd();
        return super.printPaymentDetail(context);
    }

    @Override
    public int printPaymentDetail(Context context, EReceipt receipt, ReceiptDetail detail) {
        command.initReceiptTitleFormat();
        command.printFeedLine(80);

        //1.????????????
        command.printString(context.getString(R.string.invDetail));
        command.printFeedLine(60);

        command.initReceiptProductFormat();

        // 5. ????????????
        List<Product> productList = detail.getProductList();
        printProductDetail(productList);

        //6.??????
        String[] subject = paddingRight(context.getString(R.string.subtotalWithColon), 12);
        String[] total = paddingLeft(receipt.getTotalAmount() + "TX", 18);
        command.printString(subject[0] + total[0]);
        command.printFeedLine(60);

        command.printString(SEPARATE_LINE);
        command.printFeedLine(60);

        //7.????????????
        subject = paddingRight(context.getString(R.string.taxAmount), 12);
        String[] unTax = paddingLeft(receipt.getUnTaxAmount() + "", 18);
        command.printString(subject[0] + unTax[0]);
        command.printFeedLine(60);

        //8.??????
        subject = paddingRight(context.getString(R.string.taxFreeAmt), 12);
        String[] noTax = paddingLeft("0", 18);
        command.printString(subject[0] + noTax[0]);
        command.printFeedLine(60);

        //9.??????
        subject = paddingRight("???      ???", 12);
        String[] taxAmount = paddingLeft(receipt.getTaxAmount() + "", 18);
        command.printString(subject[0] + taxAmount[0]);
        command.printFeedLine(60);

        //10.??????
        subject = paddingRight("???      ???", 12);
        total = paddingLeft(detail.getTotal() + "", 18);
        command.printString(subject[0] + total[0]);
        command.printFeedLine(60);

        command.printString(SEPARATE_LINE);
        command.printFeedLine(60);

        //.12????????????
        List<Payment> paymentList = detail.getPaymentList();
        printPaymentData(paymentList);

        command.cutPaperEnd();
        return super.printPaymentDetail(context, receipt, detail);
    }

    @Override
    public int printReceipt(Context context, EReceipt receipt, ReceiptDetail detail) {
        printTakeNumber(context, detail);//????????????????????????
        command.cutPaperEnd();
        command.initReceiptLargeFormat();

        command.printString(context.getString(R.string.receipt));
        command.printFeedLine(60);

        printTakeNumber(context, detail);//????????????????????????

        command.initReceiptProductFormat();

        //6.?????? ?????? ?????? ??????
        // 9~11. ????????????
        List<Product> productList = detail.getProductList();
        printProductDetail(productList);

        //12~13.?????????
        String[] subject = paddingRight(context.getString(R.string.totalAmount), 12);
        String[] tot_sales = paddingLeft(detail.getTotal() + "", 18);
        command.printString(subject[0] + ":" + tot_sales[0]);
        command.printFeedLine(80);

        //14.????????????
        if (detail.getTotalDischarge() != 0) {
            subject = paddingRight(context.getString(R.string.tailDiscount), 12);
            String[] totDischarge = paddingLeft(detail.getTotalDischarge() + "", 18);
            command.printString(subject[0] + ":" + totDischarge[0]);
            command.printFeedLine(60);
        }

        //15.??????
        String s = context.getString(R.string.receive);
        subject = paddingRight(s.substring(0, s.length() - 1), 12);
        command.printString(subject[0] + ":" + tot_sales[0]);
        command.printFeedLine(60);

        //16.??????
        s = context.getString(R.string.pay);
        subject = paddingRight(s.substring(0, s.length() - 1), 12);
        command.printString(subject[0] + ":" + tot_sales[0]);
        command.printFeedLine(60);

        //????????????
        List<Payment> paymentList = detail.getPaymentList();
        printPaymentData(paymentList);

        //21.????????????
        if (receipt != null && !Config.disableReceiptModule) {
            String invNo = receipt.getEReceiptNumber();
            command.printString(context.getString(R.string.invNo) + invNo);
            command.printFeedLine(60);
        }

        //23.?????????
        if (receipt != null && receipt.getLoveCode() != null && receipt.getLoveCode().length() > 0) {
            String loveCode = receipt.getLoveCode();
            command.printString(context.getString(R.string.donateNo) + loveCode);
            command.printFeedLine(60);
        }

        //24.??????
        if (receipt != null && receipt.getCarrierNumber() != null && receipt.getCarrierNumber().length() > 0) {
            String carrierNumber = receipt.getCarrierNumber();
            command.printString(context.getString(R.string.invVehicle) + carrierNumber);
            command.printFeedLine(60);
        }

        //24.????????????
        if (receipt != null && receipt.getBuyerGuiNumber() != null && receipt.getBuyerGuiNumber().length() > 0) {
            String buyerNumber = receipt.getBuyerGuiNumber();
            command.printString(context.getString(R.string.buyerNumber) + buyerNumber);
            command.printFeedLine(60);
        }

        //???????????????
        if (detail.getEcPayData() != null) {
            printEcSaleData(detail.getEcPayData());
        }

        if (detail.getNcccTransDataBean() != null) {
            printNCCCTransData(detail.getNcccTransDataBean());
        }

        command.printString(SEPARATE_LINE);
        command.printFeedLine(60);

        //27.???????????????????????????????????????????????????
        command.printString(context.getString(R.string.receiptBottomMsg));
        command.printFeedLine(60);

        //28.time
        command.printString(getTime());
        command.printFeedLine(60);

        //29.barCode
        command.alignCenter();
        command.printBarCode39(detail.getOrderId());

        //30.QRCode
        QrCodeSetting setting = QrCodeSetting.getDefaultSetting();
        command.printQRCode(detail.getOrderId(), setting);

        command.cutPaperEnd();

        return super.printReceipt(context, receipt, detail);
    }

    @Override
    public int printReceipt(Context context) {
        OrderResponse orderResponse = Bill.fromServer.getOrderResponse();

        command.initReceiptLargeFormat();

        command.printString(context.getString(R.string.receipt));
        command.printFeedLine(60);

        printTakeNumber(context, orderResponse);

        command.initReceiptProductFormat();

        //6.?????? ?????? ?????? ??????
        List<WebOrder01> webOrder01List = orderResponse.getWeborder01();
        printProductDetail(context, webOrder01List);

        //12~13.?????????
        String[] subject = paddingRight(context.getString(R.string.totalAmount), 12);
        String[] tot_sales = paddingLeft(orderResponse.getTot_sales() + "", 18);
        command.printString(subject[0] + ":" + tot_sales[0]);
        command.printFeedLine(80);

        //14.????????????
        if (orderResponse.getDiscount() != 0) {
            subject = paddingRight(context.getString(R.string.tailDiscount), 12);
            String[] totDischarge = paddingLeft(orderResponse.getDiscount() + "", 18);
            command.printString(subject[0] + ":" + totDischarge[0]);
            command.printFeedLine(60);
        }

        //15.??????
        String s = context.getString(R.string.receive);
        subject = paddingRight(s.substring(0, s.length() - 1), 12);
        command.printString(subject[0] + ":" + tot_sales[0]);
        command.printFeedLine(60);

        //16.??????
        s = context.getString(R.string.pay);
        subject = paddingRight(s.substring(0, s.length() - 1), 12);
        command.printString(subject[0] + ":" + tot_sales[0]);
        command.printFeedLine(60);

        //????????????
        List<WebOrder02> webOrder02List = orderResponse.getWeborder02();
        printPaymentData(context, webOrder02List);

        //21.????????????
        if (Bill.fromServer.getOrderResponse().getInvoice() != null) {
            String invNo = Bill.fromServer.getOrderResponse().getInvoice().getInv_no_b();
            command.printString(context.getString(R.string.invNo) + invNo);
            command.printFeedLine(60);
        }

        //23.?????????
        String npoban = Bill.fromServer.getNpoBan();
        if (npoban.trim().length() > 0) {
            command.printString(context.getString(R.string.donateNo) + npoban);
            command.printFeedLine(60);
        }
        //24.??????
        String buyerNumber = Bill.fromServer.getBuyerNumber();
        if (buyerNumber.trim().length() > 0) {
            command.printString(context.getString(R.string.invVehicle) + buyerNumber);
            command.printFeedLine(60);
        }

        //???????????????
        if (Bill.fromServer.getEcPayData() != null) {
            EcPayData ecPayData = Bill.fromServer.getEcPayData();
            printEcSaleData(ecPayData);
        }

        //???????????????
        if (Bill.fromServer.getNcccTransDataBean() != null) {
            printNCCCTransData(Bill.fromServer.getNcccTransDataBean());
        }

        command.printString(SEPARATE_LINE);
        command.printFeedLine(60);

        //27.???????????????????????????????????????????????????
        command.printString(context.getString(R.string.receiptBottomMsg));
        command.printFeedLine(60);

        //28.time
        command.printString(getTime());
        command.printFeedLine(60);

        //29.barCode
        command.alignCenter();
        command.printBarCode39(Bill.fromServer.worder_id);

        //30.QRCode
        QrCodeSetting setting = QrCodeSetting.getDefaultSetting();
        command.printQRCode(Bill.fromServer.worder_id, setting);

        command.cutPaperEnd();

        return super.printReceipt(context);
    }

    @Override
    public int printBill(Context context, ReceiptDetail detail) {
        command.initReceiptLargeFormat();
        //1.?????????
        command.printString(context.getString(R.string.bill));
        command.printFeedLine(120);

        command.initReceiptDetailsFormat();

        //2.??????
        command.printString(getTime());
        command.printFeedLine(60);

        //3 ??????????????????
        String data = Config.shop_id + "-" + Config.kiosk_id + "-" + detail.getOrderId();
        command.printString(data);
        command.printFeedLine(60);

        command.initReceiptLargeFormat();

        //4.????????????
        String inOutData = detail.getSaleType();
        if (detail.getTableNumber() != null && detail.getTableNumber().length() > 0) {
            inOutData += "/??????-" + detail.getTableNumber();
        }

        command.printString(inOutData);
        command.printFeedLine(120);

        command.initReceiptProductFormat();

        //6.?????? ?????? ?????? ??????
        List<Product> productList = detail.getProductList();
        printProductDetail(productList);

        //?????????
        String[] subject = paddingRight(context.getString(R.string.totalAmount), 12);
        String[] tot_sales = paddingLeft(detail.getTotal() + "", 18);
        command.printString(subject[0] + ":" + tot_sales[0]);
        command.printFeedLine(80);

        //8.??????
        double cashTicketAmount = 0.0;
        //8.??????
        for (Payment payment : detail.getPaymentList()) {
            if(payment.getType().equals("CASH_TICKET")){
                cashTicketAmount = payment.getAmount() * 1.0;
            }
        }
        double discount = detail.getTotalFee() - detail.getTotalDischarge() + cashTicketAmount - detail.getTotal();
        if (discount > 0.0) {
            subject = paddingRight(context.getString(R.string.tailDiscount), 12);
            String[] totDischarge = paddingLeft(discount + "", 18);
            command.printString(subject[0] + ":" + totDischarge[0]);
            command.printFeedLine(60);
        }

        //????????????
        List<Payment> paymentList = detail.getPaymentList();
        printPaymentData(paymentList);
        //9.??????????????????????????????
        command.printString(context.getString(R.string.plsPayAtCounter));
        command.printFeedLine(80);

        //barCode
        command.alignCenter();
        command.printBarCode39(detail.getOrderId());

        //QRCode
        QrCodeSetting setting = QrCodeSetting.getDefaultSetting();
        command.printQRCode(detail.getOrderId(), setting);

        command.cutPaperEnd();

        return super.printBill(context, detail);
    }

    private void printPaymentData(List<Payment> paymentList) {
        for (Payment payment : paymentList) {
            String payName = getPayName(context, payment.getName());
            String[] name = paddingRight(payName, 12);
            String payAmount = FormatUtil.removeDot(payment.getAmount());
            String[] amount = paddingLeft(payAmount, 18);
            command.printString(name[0] + ":" + amount[0]);
            if (name[1].length() > 0) {
                command.printFeedLine(60);
                command.printString(name[1]);
            }
            command.printFeedLine(60);
        }
    }

    @Override
    public int printBill(Context context) {
        OrderResponse orderResponse = Bill.fromServer.getOrderResponse();

        command.initReceiptTitleFormat();

        //1.?????????
        command.printString(context.getString(R.string.bill));
        command.printFeedLine(120);

        command.initReceiptDetailsFormat();

        //2.??????
        command.printString(getTime());
        command.printFeedLine(60);

        //3 ??????????????????
        command.printString(Config.shop_id + "-" + Config.kiosk_id + "-" + Bill.fromServer.worder_id);
        command.printFeedLine(60);

        command.initReceiptLargeFormat();

        //4.????????????
        String inOutData = Bill.fromServer.getInOut_String(context);
        if (orderResponse.getTable_no() != null && orderResponse.getTable_no().length() > 0) {
            inOutData += "/??????-" + orderResponse.getTable_no();
        }
        command.printString(inOutData);
        command.printFeedLine(120);

        command.initReceiptProductFormat();

        //6.?????? ?????? ?????? ??????
        List<WebOrder01> webOrder01List = orderResponse.getWeborder01();
        printProductDetail(context, webOrder01List);

        //?????????
        String[] subject = paddingRight(context.getString(R.string.totalAmount), 12);
        String[] tot_sales = paddingLeft(orderResponse.getTot_sales() + "", 18);
        command.printString(subject[0] + ":" + tot_sales[0]);
        command.printFeedLine(80);

        //????????????
        if (orderResponse.getDiscount() != 0) {
            subject = paddingRight(context.getString(R.string.tailDiscount), 12);
            String[] totDischarge = paddingLeft(orderResponse.getDiscount() + "", 18);
            command.printString(subject[0] + ":" + totDischarge[0]);
            command.printFeedLine(60);
        }

        //????????????
        List<WebOrder02> webOrder02List = orderResponse.getWeborder02();
        printPaymentData(context, webOrder02List);

        //9.??????????????????????????????
        command.printString(context.getString(R.string.plsPayAtCounter));
        command.printFeedLine(80);

        //barCode
        command.alignCenter();
        command.printBarCode39(Bill.fromServer.worder_id);

        //QRCode
        QrCodeSetting setting = QrCodeSetting.getDefaultSetting();
        command.printQRCode(Bill.fromServer.worder_id, setting);

        command.cutPaperEnd();

        return super.printBill(context);
    }

    private void printProductDetail(List<Product> productList) {
        command.printString(SEPARATE_LINE);
        command.printFeedLine(60);

        int product_padding = 12;
        int qty_padding = 6;
        int originPricePadding = 6;
        int subtotalPadding = 6;

        String[] title_product = paddingRight(context.getString(R.string.product), product_padding);
        String[] title_qty = paddingLeft(context.getString(R.string.quantity), qty_padding);
        String[] titleOriginPrice = paddingLeft(context.getString(R.string.originPrice), originPricePadding);
        String[] titleSubtotal = paddingLeft(context.getString(R.string.subtoal), subtotalPadding);
        command.printString(title_product[0] + title_qty[0] + titleOriginPrice[0] + titleSubtotal[0]);
        command.printFeedLine(60);

        command.printString(SEPARATE_LINE);
        command.printFeedLine(60);

        // 9~11. ????????????
        for (Product product : productList) {
            if(!product.getName().equals("") || !product.getMemo().equals("")){
                double salePrice = ComputationUtils.parseDouble(String.valueOf(product.getTotalPrice()));
                int quantity = (int) ComputationUtils.parseDouble(String.valueOf(product.getQuantity()));
                int itemDisc = (int) ComputationUtils.parseDouble(product.getItemDiscount());
                String[] products = paddingRight(product.getName(), product_padding);
                String[] qty = paddingLeft(quantity + "", qty_padding);
                String[] originPrice = paddingLeft(salePrice * quantity + "", originPricePadding);
                String[] subtotal = paddingLeft(salePrice * quantity + itemDisc + "", subtotalPadding);
                command.printString(products[0] + qty[0] + originPrice[0] + subtotal[0]);
                if (products[1].length() > 0) {
                    command.printFeedLine(60);
                    command.printString(products[1]);
                }
                command.printFeedLine(60);
                if (product.getMemo() != null && product.getMemo().length() > 0) {
                    command.printString("  **" + product.getMemo());
                    command.printFeedLine(60);
                }
            }
        }
        command.printString(SEPARATE_LINE);
        command.printFeedLine(60);
        command.printFeedLine(20);
    }

    private void printProductDetail(Context context, List<WebOrder01> webOrder01List) {
        command.printString(SEPARATE_LINE);
        command.printFeedLine(60);

        int product_padding = 12;
        int qty_padding = 6;
        int originPricePadding = 6;
        int subtotalPadding = 6;

        String[] title_product = paddingRight(context.getString(R.string.product), product_padding);
        String[] title_qty = paddingLeft(context.getString(R.string.quantity), qty_padding);
        String[] titleOriginPrice = paddingLeft(context.getString(R.string.originPrice), originPricePadding);
        String[] titleSubtotal = paddingLeft(context.getString(R.string.subtoal), subtotalPadding);
        command.printString(title_product[0] + title_qty[0] + titleOriginPrice[0] + titleSubtotal[0]);
        command.printFeedLine(60);

        command.printString(SEPARATE_LINE);
        command.printFeedLine(60);

        // 9~11. ????????????
        for (WebOrder01 webOrder01 : webOrder01List) {
            double salePrice = ComputationUtils.parseDouble(webOrder01.getSale_price());
            int quantity = (int) ComputationUtils.parseDouble(webOrder01.getQty());
            int itemDisc = (int) ComputationUtils.parseDouble(webOrder01.getItem_disc());

            String[] product = paddingRight(webOrder01.getProd_name1(), product_padding);
            String[] qty = paddingLeft(quantity + "", qty_padding);
            String[] originPrice = paddingLeft(salePrice * quantity + "", originPricePadding);
            String[] subtotal = paddingLeft(salePrice * quantity + itemDisc + "", subtotalPadding);
            command.printString(product[0] + qty[0] + originPrice[0] + subtotal[0]);
            if (product[1].length() > 0) {
                command.printFeedLine(60);
                command.printString(product[1]);
            }
            command.printFeedLine(60);
            if (webOrder01.getTaste_memo() != null && webOrder01.getTaste_memo().length() > 0) {
                command.printString("  **" + webOrder01.getTaste_memo());
                command.printFeedLine(60);
            }
        }

        command.printString(SEPARATE_LINE);
        command.printFeedLine(60);
        command.printFeedLine(20);
    }


    private void printPaymentData(Context context, List<WebOrder02> webOrder02List) {
        for (WebOrder02 webOrder02 : webOrder02List) {
            String payName = getPayName(context, webOrder02.getPay_id());
            String[] name = paddingRight(payName, 12);
            String payAmount = FormatUtil.removeDot(webOrder02.getAmount());
            String[] amount = paddingLeft(payAmount, 18);
            command.printString(name[0] + ":" + amount[0]);
            if (name[1].length() > 0) {
                command.printFeedLine(60);
                command.printString(name[1]);
            }
            command.printFeedLine(60);
        }

        command.printFeedLine(60);
    }

    @Override
    public int printEasyCardCheckOutData(EcCheckoutData ecCheckoutData) {
        command.initReceiptTitleFormat();
        command.printString("?????????????????????");
        command.printFeedLine(120);

        command.initReceiptDetailsFormat();

        command.printString("??????:" + Config.shop_id);
        command.printFeedLine(60);

        command.alignLeft();

        command.printString("????????????:" + ecCheckoutData.getTXN_Serial_number());
        command.printFeedLine(60);

        command.printString(SEPARATE_LINE);
        command.printFeedLine(60);

        command.printString("????????????:" + ecCheckoutData.getTXN_DateTime().replace("-", "/"));
        command.printFeedLine(60);

        command.printString("??????");
        command.printString(":" + (ecCheckoutData.getSettlement_Status().equals("0") ? "??????" : "?????????"));
        command.printFeedLine(60);

        command.printString("??????????????????:" + ecCheckoutData.getNew_Deviceid());
        command.printFeedLine(60);

        command.printString("????????????:" + ecCheckoutData.getBatch_number());
        command.printFeedLine(120);

        command.printString("????????????:" + ecCheckoutData.getDeduct_count());
        command.printFeedLine(60);
        command.printString("??????  :" + ecCheckoutData.getDeduct_amt());
        command.printFeedLine(100);

        command.printString("????????????:" + ecCheckoutData.getRefund_count());
        command.printFeedLine(60);
        command.printString("??????  :" + ecCheckoutData.getRefund_amt());
        command.printFeedLine(100);

        command.printString("??????????????????:" + ecCheckoutData.getAddValue_count());
        command.printFeedLine(60);
        command.printString("??????  :" + ecCheckoutData.getTotal_AddValue_amt());
        command.printFeedLine(100);

        command.printString("??????????????????:" + ecCheckoutData.getTotal_Txn_count());
        command.printFeedLine(60);
        command.printString("?????????:" + ecCheckoutData.getTotal_Txn_amt());
        command.printFeedLine(60);
        command.printString("?????????:" + ecCheckoutData.getTotal_Txn_pure_amt());
        command.printFeedLine(100);

        command.printString("??????????????? :???" + ecCheckoutData.getTotal_Txn_count());
        command.printFeedLine(60);

        command.printString("???????????? :???" + ecCheckoutData.getTotal_Txn_amt());
        command.printFeedLine(30);

        command.cutPaperEnd();

        return super.printEasyCardCheckOutData(ecCheckoutData);
    }

    @Override
    public int printTest(Context context, String data) {
        command.printString("TEST");
        command.printFeedLine(20);
        command.cutPaper(true);
        return super.printTest(context, data);
    }

    private void printTakeNumber(Context context, ReceiptDetail detail) {
        command.initReceiptDetailsFormat();
        String data;//4.????????????
        //2.??????
        command.printString(getTime());
        command.printFeedLine(60);
        //3 ??????????????????
        data = Config.shop_id + "-" + Config.kiosk_id + "-" + detail.getOrderId();
        command.printString(data);
        command.printFeedLine(60);

        command.initReceiptLargeFormat();
        data = detail.getSaleType();
        if (detail.getTableNumber() != null && detail.getTableNumber().length() > 0) {
            data += "/??????-" + detail.getTableNumber();
        }
        command.printString(data);
        command.printFeedLine(60);
        //5.????????????
        data = context.getString(R.string.takeMealNo) + detail.getTakeMealNumber();
        command.printString(data);
        command.printFeedLine(60);
    }

    private void printTakeNumber(Context context, OrderResponse orderResponse) {
        command.initReceiptDetailsFormat();

        //2.??????
        command.printString(getTime());
        command.printFeedLine(60);

        //3 ??????????????????
        command.printString(Config.shop_id + "-" + Config.kiosk_id + "-" + Bill.fromServer.worder_id);
        command.printFeedLine(60);

        command.initReceiptLargeFormat();

        String inOutData = Bill.fromServer.getInOut_String(context);
        if (orderResponse.getTable_no() != null && orderResponse.getTable_no().length() > 0) {
            inOutData += "/??????-" + orderResponse.getTable_no();
        }
        command.printString(inOutData);
        command.printFeedLine(120);

        //5.????????????
        String takeNoData = context.getString(R.string.takeMealNo) + Bill.fromServer.getOrderResponse().getTakeno();
        command.printString(takeNoData);
        command.printFeedLine(120);
    }

    private void printEcSaleData(EcPayData ecPayData) {
        command.printString("-----?????????????????????-----");
        command.printFeedLine(60);
        //????????????
        command.alignLeft();
        String date = ecPayData.getTxn_Date();
        String time = ecPayData.getTxn_Time();
        String printDate = date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6);
        String printTime = time.substring(0, 2) + ":" + time.substring(2, 4) + ":" + time.substring(4);
        command.printString("????????????:" + printDate + " " + printTime);
        command.printFeedLine(60);

        //???????????????
        command.printString("???????????????:" + ecPayData.getReceipt_card_number());
        command.printFeedLine(60);
        //????????????
        command.printString("????????????:???????????????");
        command.printFeedLine(60);
        //??????????????????
        command.printString("??????????????????:" + ecPayData.getNew_deviceID());
        command.printFeedLine(60);
        //????????????
        command.printString("????????????:" + ecPayData.getBatch_number());
        command.printFeedLine(60);
        //RRN
        command.printString("RRN:" + ecPayData.getRRN());
        command.printFeedLine(60);
        //???????????????
        command.printString("???????????????:" + ecPayData.getN_CPU_EV_Before_TXN());
        command.printFeedLine(60);
        //??????????????????
        command.printString("??????????????????:" + ecPayData.getN_AutoLoad_Amount());
        command.printFeedLine(60);
        //????????????
        command.printString("????????????:" + ecPayData.getN_CPU_Txn_AMT());
        command.printFeedLine(60);
        //???????????????
        command.printString("???????????????:" + ecPayData.getN_CPU_EV());
        command.printFeedLine(60);
    }

    private void printNCCCTransData(NCCCTransDataBean dataBean) {

        String title;
        //------------
        title = "E".equals(dataBean.getEsvcIndicator()) ? "-----?????????????????????-----" : "-----?????????????????????-----";
        command.printString(title);
        command.printFeedLine(60);

        command.printFeedLine(60);
        command.printString("???????????????????????????");
        command.printFeedLine(60);

        command.alignLeft();
        command.printString("????????????: " + dataBean.getTransDate() + dataBean.getTransTime());
        command.printFeedLine(60);

        String terminalId = dataBean.getTerminalId();
        command.printString("?????????: " + terminalId);
        command.printFeedLine(60);

        String receiptNo = dataBean.getReceiptNo();
        String approvalNo = dataBean.getApprovalNo().trim();
        command.printString("????????????: " + receiptNo + "  ?????????: " + approvalNo);
        command.printFeedLine(60);

        String cardName = NCCCCode.getCardTypeName(dataBean.getCardType());
        command.printString("??????: " + cardName);
        command.printFeedLine(60);

        String cardNo = dataBean.getCardNo();
        command.printString("??????: " + cardNo);
        command.printFeedLine(60);

        String transAmount = dataBean.getTransAmount();
        String payAmount = "$" + NCCCTransDataBean.parseAmount(transAmount);
        command.printString("????????????: " + payAmount);
        command.printFeedLine(60);
    }

    @Override
    public int printCreditCardReceipt(Context context) {
        NCCCTransDataBean dataBean = Bill.fromServer.getNcccTransDataBean();
        command.initReceiptProductFormat();
        printNCCCTransData(dataBean);
        command.printFeedLine(60);
        command.printString(context.getString(R.string.creditCardReceiptBottom));
        cutPaper();
        return super.printCreditCardReceipt(context);
    }

    @Override
    public void cutPaper() {
        command.cutPaperEnd();
        super.cutPaper();
    }

    @Override
    public int printTicket(Context context, Ticket ticket) {
        command.printLogo();
        command.printQRCode(ticket.getQrCodeData(), QrCodeSetting.getDefaultSetting());
        command.initReceiptTitleFormat();

        command.printString(ticket.getSubject());
        command.printFeedLine(100);
        command.printFeedLine(100);

        command.initReceiptDetailsFormat();

        command.printString("????????????:" + ticket.getDate());
        command.printFeedLine(75);

        command.printString("????????????:" + ticket.getTime());
        command.printFeedLine(75);

        command.printString("??????:" + ticket.getKind());
        command.printFeedLine(75);

        command.printString("??????:" + ticket.getPrice());
        command.printFeedLine(75);

        command.printString("???????????????:" + ticket.getDiscount());
        command.printFeedLine(75);

        command.printString("????????????:" + ticket.getTicketNumber());
        command.printFeedLine(75);

        command.printFeedLine(100);
        command.printString("????????????:" + getTime());

        command.cutPaperEnd();
        return super.printTicket(context, ticket);
    }


    private String[] paddingRight(String str, int limit_byte_length) {
        String[] return_data = new String[2];
        return_data[0] = "";
        return_data[1] = "";
        int param_length;

        try {
            // ????????????byte?????????????????????2byte?????????1byte
            param_length = str.getBytes("BIG5").length;

            // ??????????????????==??????????????????
            if (param_length == limit_byte_length) {
                // ??????-??????
                return_data[0] = str;
                return_data[1] = "";
                return return_data;
            }

            // ??????-?????????
            if (param_length < limit_byte_length) {

                return_data[0] = str;
                for (int i = 0; i < (limit_byte_length - param_length); i++) {
                    return_data[0] = return_data[0] + " ";
                }
                return_data[1] = "";
                return return_data;
            }

            int first_line_end_position = 0;
            // ??????-?????????
            if (param_length > limit_byte_length) {
                int str_length = str.length();
                for (int i = 0; i < str_length; i++) {

                    String str_char = str.substring(i, i + 1);

                    int char_length = str_char.getBytes("BIG5").length;

                    int return_first_length = return_data[0].getBytes("BIG5").length;
                    int diff = limit_byte_length - return_first_length;

                    //???????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    if (return_first_length < limit_byte_length && diff >= char_length) {
                        return_data[0] = return_data[0] + str_char;
                    } else {
                        first_line_end_position = i;
                        break;
                    }
                }

                //????????????1???????????????
                if (return_data[0].getBytes("BIG5").length < limit_byte_length) {
                    return_data[0] = return_data[0] + " ";
                }

                return_data[1] = str.substring(first_line_end_position, str_length);

                return return_data;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return return_data;
    }

    private String[] paddingLeft(String str, int limit_byte_length) {
        String[] return_data = new String[2];
        int param_length;

        try {
            // ????????????byte?????????????????????2byte?????????1byte
            param_length = str.getBytes("BIG5").length;

            // ??????????????????==??????????????????
            if (param_length == limit_byte_length) {
                // ??????-??????
                return_data[0] = str;
                return_data[1] = "";
                return return_data;
            }

            if (param_length < limit_byte_length) {
                // ??????-?????????
                return_data[0] = str;
                for (int i = 0; i < (limit_byte_length - param_length); i++) {
                    return_data[0] = " " + return_data[0];
                }

                return_data[1] = "";
                return return_data;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return return_data;
    }

}
