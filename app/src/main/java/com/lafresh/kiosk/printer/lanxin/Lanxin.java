package com.lafresh.kiosk.printer.lanxin;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.text.format.Time;
import android.util.Log;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;

import com.lafresh.kiosk.Bill;
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
import com.lafresh.kiosk.model.GetAccountingInformation;
import com.lafresh.kiosk.model.RevenueItem;
import com.lafresh.kiosk.printer.KioskPrinter;
import com.lafresh.kiosk.printer.model.EReceipt;
import com.lafresh.kiosk.printer.model.Payment;
import com.lafresh.kiosk.printer.model.Product;
import com.lafresh.kiosk.printer.model.ReceiptDetail;
import com.lafresh.kiosk.printer.model.Revenue;
import com.lafresh.kiosk.printer.model.Ticket;
import com.lafresh.kiosk.type.FlavorType;
import com.lafresh.kiosk.type.PaymentsType;
import com.lafresh.kiosk.utils.CommonUtils;
import com.lafresh.kiosk.utils.ComputationUtils;
import com.lafresh.kiosk.utils.FormatUtil;
import com.lvrenyang.io.Pos;
import com.lvrenyang.io.USBPrinting;
import com.redoakps.printer.RedOakPrinter;
import com.redoakps.printer.RedOakInternalPrinter;

import java.util.List;

import print.Print;

public class Lanxin extends KioskPrinter {

    private Context context;
    private boolean connected = true;

    Spinner mSpinner;
    RedOakPrinter mPrinter;

    public Lanxin(Context context) {
        this.context = context;
    }

    @Override
    public int printReceipt(final Context context) {
        final OrderResponse orderResponse = Bill.fromServer.getOrderResponse();

        RedOakPrinter mPrinter = new RedOakInternalPrinter();
        mPrinter.open(context);

        //1.收據
        mPrinter.printLine(getTime(), "", "", RedOakPrinter.STYLE_NONE);
        mPrinter.printLine(orderResponse.getShop_id() + "-" + orderResponse.getWorder_id(), "", "", RedOakPrinter.STYLE_NONE);
        mPrinter.printLine(orderResponse.getMethod_name(), "", "", RedOakPrinter.STYLE_LARGE_FONT);
        mPrinter.printLine(context.getString(R.string.takeMealNo) + orderResponse.getTakeno(), "", "", RedOakPrinter.STYLE_LARGE_FONT);

        mPrinter.printLine("", "", "", RedOakPrinter.STYLE_NONE);   //跳行
        mPrinter.printLine("", "", "", RedOakPrinter.STYLE_NONE);   //跳行

        mPrinter.printLine(context.getString(R.string.receipt), "", "", RedOakPrinter.STYLE_LARGE_FONT);
        mPrinter.printLine(context.getString(R.string.takeMealNo) + orderResponse.getTakeno(), "", "", RedOakPrinter.STYLE_LARGE_FONT);

        //9.商品 數量 金額
        mPrinter.printLine("-------------------------------------------------------------------", "", "", RedOakPrinter.STYLE_NONE);
        mPrinter.printLine(context.getString(R.string.product), context.getString(R.string.quantity), context.getString(R.string.price), RedOakPrinter.STYLE_NONE);
        mPrinter.printLine("-------------------------------------------------------------------", "", "", RedOakPrinter.STYLE_NONE);

        //12~14.商品列表
        mPrinter.printLine("", "", "", RedOakPrinter.STYLE_NONE);   //跳行
        for (WebOrder01 webOrder01 : orderResponse.getWeborder01()) {
            double salePrice = ComputationUtils.parseDouble(webOrder01.getSale_price());
            int qty = (int) ComputationUtils.parseDouble(webOrder01.getQty());

            mPrinter.printLine(webOrder01.getProd_name1(), "" + qty, "" + (qty * salePrice), RedOakPrinter.STYLE_NONE);
        }
        mPrinter.printLine("", "", "", RedOakPrinter.STYLE_NONE);   //跳行

        //15.合計 折讓 金額
        mPrinter.printLine("-------------------------------------------------------------------", "", "", RedOakPrinter.STYLE_NONE);
        mPrinter.printLine(context.getString(R.string.totalAmount), "", "" + orderResponse.getTotal(), RedOakPrinter.STYLE_NONE);
        mPrinter.printLine(context.getString(R.string.discount), "", "" + orderResponse.getDiscount(), RedOakPrinter.STYLE_NONE);
        mPrinter.printLine(context.getString(R.string.price), "", "" + (orderResponse.getTotal() - orderResponse.getDiscount()), RedOakPrinter.STYLE_NONE);

        mPrinter.printLine("", "", "", RedOakPrinter.STYLE_NONE);   //跳行

        //20.實收
        mPrinter.printLine(context.getString(R.string.receive), "", "" + orderResponse.getTot_sales(), RedOakPrinter.STYLE_NONE);

        //21.支付
        mPrinter.printLine(context.getString(R.string.pay), "", "" + orderResponse.getTot_sales(), RedOakPrinter.STYLE_NONE);

        //22~23.付款方式
        mPrinter.printLine("", "", "", RedOakPrinter.STYLE_NONE);
        for (WebOrder02 webOrder02 : orderResponse.getWeborder02()) {
            String payName = CommonUtils.getPayName(context, webOrder02.getPay_id());
            String payAmount = CommonUtils.spaceIt(CommonUtils.removeDot(webOrder02.getAmount()), 8);

            mPrinter.printLine(payName + ":", "", payAmount, RedOakPrinter.STYLE_NONE);
        }

        //24.發票號碼
        if (orderResponse.getInvoice() != null && !Config.disableReceiptModule) {
            String invNo = orderResponse.getInvoice().getInv_no_b();

            mPrinter.printLine(context.getString(R.string.invNo) + invNo, "", "", RedOakPrinter.STYLE_NONE);
        }

        //25.---
        mPrinter.printLine("-------------------------------------------------------------------", "", "", RedOakPrinter.STYLE_NONE);

        //26.此單據內容僅供參考，不作為交易憑證
        mPrinter.printLine(context.getString(R.string.receiptBottomMsg), "", "", RedOakPrinter.STYLE_NONE);

        mPrinter.printLine(getTime(), "", "", RedOakPrinter.STYLE_NONE);    //跳行

        mPrinter.feed();

        final RedOakPrinter.PrintResult printResult = mPrinter.waitForResult((Activity) context);

        //end
        mPrinter.close();

        return super.printReceipt(context);
    }

    @Override
    public int printReceipt(Context context, EReceipt receipt, ReceiptDetail detail) {
        RedOakPrinter mPrinter = new RedOakInternalPrinter();
        mPrinter.open(context);

        //1.收據
        mPrinter.printLine(getTime(), "", "", RedOakPrinter.STYLE_NONE);
        mPrinter.printLine(Config.shop_id + "-" + detail.getOrderId(), "", "", RedOakPrinter.STYLE_NONE);
        mPrinter.printLine(detail.getSaleType(), "", "", RedOakPrinter.STYLE_LARGE_FONT);
        mPrinter.printLine(context.getString(R.string.takeMealNo) + detail.getTakeMealNumber(), "", "", RedOakPrinter.STYLE_LARGE_FONT);

        mPrinter.printLine("", "", "", RedOakPrinter.STYLE_NONE);   //跳行
        mPrinter.printLine("", "", "", RedOakPrinter.STYLE_NONE);   //跳行

        mPrinter.printLine(context.getString(R.string.receipt), "", "", RedOakPrinter.STYLE_LARGE_FONT);
        mPrinter.printLine(context.getString(R.string.takeMealNo) + detail.getTakeMealNumber(), "", "", RedOakPrinter.STYLE_LARGE_FONT);

        //9.商品 數量 金額
        mPrinter.printLine("-------------------------------------------------------------------", "", "", RedOakPrinter.STYLE_NONE);
        mPrinter.printLine(context.getString(R.string.product), context.getString(R.string.quantity), context.getString(R.string.price), RedOakPrinter.STYLE_NONE);
        mPrinter.printLine("-------------------------------------------------------------------", "", "", RedOakPrinter.STYLE_NONE);

        //12~14.商品列表
        List<Product> productList = detail.getProductList();
        mPrinter.printLine("", "", "", RedOakPrinter.STYLE_NONE);   //跳行
        for (Product product : productList) {
            double salePrice = product.getTotalPrice();
            int qty = product.getQuantity();

            mPrinter.printLine(product.getName(), "" + qty, "" + salePrice, RedOakPrinter.STYLE_NONE);
        }
        mPrinter.printLine("", "", "", RedOakPrinter.STYLE_NONE);   //跳行

        //15.合計 折讓 金額
        mPrinter.printLine("-------------------------------------------------------------------", "", "", RedOakPrinter.STYLE_NONE);
        mPrinter.printLine(context.getString(R.string.totalAmount), "", "" + detail.getTotal(), RedOakPrinter.STYLE_NONE);
        mPrinter.printLine(context.getString(R.string.discount), "", "" + detail.getDiscount(), RedOakPrinter.STYLE_NONE);
        mPrinter.printLine(context.getString(R.string.price), "", "" + (detail.getTotal() - detail.getDiscount()), RedOakPrinter.STYLE_NONE);

        mPrinter.printLine("", "", "", RedOakPrinter.STYLE_NONE);   //跳行

        //20.實收
        mPrinter.printLine(context.getString(R.string.receive), "", "" + detail.getTotalFee(), RedOakPrinter.STYLE_NONE);

        //21.支付
        mPrinter.printLine(context.getString(R.string.pay), "", "" + detail.getTotalFee(), RedOakPrinter.STYLE_NONE);

        //22~23.付款方式
        List<Payment> paymentList = detail.getPaymentList();
        mPrinter.printLine("", "", "", RedOakPrinter.STYLE_NONE);
        for (Payment payment : paymentList) {
            mPrinter.printLine(payment.getName() + ":", "", "" + payment.getAmount(), RedOakPrinter.STYLE_NONE);
        }

        //24.發票號碼
        if (receipt != null && !Config.disableReceiptModule) {
            String invNo = receipt.getEReceiptNumber();

            mPrinter.printLine(context.getString(R.string.invNo) + invNo, "", "", RedOakPrinter.STYLE_NONE);
        }

        //25.---
        mPrinter.printLine("-------------------------------------------------------------------", "", "", RedOakPrinter.STYLE_NONE);

        //26.此單據內容僅供參考，不作為交易憑證
        mPrinter.printLine(context.getString(R.string.receiptBottomMsg), "", "", RedOakPrinter.STYLE_NONE);

        mPrinter.printLine(getTime(), "", "", RedOakPrinter.STYLE_NONE);    //跳行

        mPrinter.feed();

        final RedOakPrinter.PrintResult printResult = mPrinter.waitForResult((Activity) context);

        //end
        mPrinter.close();

        return super.printReceipt(context, receipt, detail);
    }

    @Override
    public int printDateRevenue(final Context context, final Revenue revenue){

        GetAccountingInformation accountingInformation = revenue.getAccountingInformation();

        RedOakPrinter mPrinter = new RedOakInternalPrinter();
        mPrinter.open(context);

        //1.標題
        mPrinter.printLine("", "商店名稱", "", RedOakPrinter.STYLE_BOLD);
        mPrinter.printLine("藍新機01 - " + context.getString(R.string.machine_record), "", "", RedOakPrinter.STYLE_BOLD);

        //3.帳務紀錄時間
        mPrinter.printLine(context.getString(R.string.start_time) + " " + revenue.getStartDate(), "", "", RedOakPrinter.STYLE_NONE);
        mPrinter.printLine(context.getString(R.string.end_time) + " " + revenue.getEndDate(), "", "", RedOakPrinter.STYLE_NONE);
        mPrinter.printLine("", "", "", RedOakPrinter.STYLE_NONE);   //跳行

        //6~9.營業額
        mPrinter.printLine(context.getString(R.string.revenue), "", accountingInformation.getRevenue().getTotal_revenue().getFormatted_amount(), RedOakPrinter.STYLE_BOLD);
        mPrinter.printLine("-------------------------------------------------------------------", "", "", RedOakPrinter.STYLE_NONE);
        List<RevenueItem> revenueItemList = accountingInformation.getRevenue().getRevenue_items();
        for(RevenueItem revenueItem : revenueItemList){
            String type = revenueItem.getType();
            mPrinter.printLine(getPayName(type), "", revenueItem.getPayment_amount().getFormatted_amount(), RedOakPrinter.STYLE_NONE);
        }
        mPrinter.printLine("", "", "", RedOakPrinter.STYLE_NONE);   //跳行

        //10.總折扣讓
        mPrinter.printLine(context.getString(R.string.total_discount), "", accountingInformation.getDiscount().getFormatted_amount(), RedOakPrinter.STYLE_BOLD);
        mPrinter.printLine("-------------------------------------------------------------------", "", "", RedOakPrinter.STYLE_NONE);
        mPrinter.printLine("", "", "", RedOakPrinter.STYLE_NONE);   //跳行

        //15.銷售總額
        mPrinter.printLine(context.getString(R.string.sale_amount), "", accountingInformation.getTotal_sale().getFormatted_amount(), RedOakPrinter.STYLE_BOLD);
        mPrinter.printLine("-------------------------------------------------------------------", "", "", RedOakPrinter.STYLE_NONE);
        mPrinter.printLine("+" + context.getString(R.string.revenue), "", accountingInformation.getRevenue().getTotal_revenue().getFormatted_amount(), RedOakPrinter.STYLE_NONE);
        mPrinter.printLine("+" + context.getString(R.string.total_discount), "", accountingInformation.getDiscount().getFormatted_amount(), RedOakPrinter.STYLE_NONE);
        mPrinter.printLine("", "", "", RedOakPrinter.STYLE_NONE);   //跳行

        //20.發票
        mPrinter.printLine(context.getString(R.string.invoice), "", "", RedOakPrinter.STYLE_BOLD);
        mPrinter.printLine("-------------------------------------------------------------------", "", "", RedOakPrinter.STYLE_NONE);
        mPrinter.printLine(context.getString(R.string.invoice_sheet), "", "" + accountingInformation.getReceipt().getCount(), RedOakPrinter.STYLE_NONE);
        mPrinter.printLine(context.getString(R.string.invoice_amount), "", accountingInformation.getReceipt().getReceipt_amount().getFormatted_amount(), RedOakPrinter.STYLE_NONE);
        mPrinter.printLine(context.getString(R.string.invalid_invoice_sheet), "", "" + accountingInformation.getReceipt().getVoid_receipt_count(), RedOakPrinter.STYLE_NONE);
        mPrinter.printLine(context.getString(R.string.invalid_invoice_amount), "", accountingInformation.getReceipt().getVoid_receipt_amount().getFormatted_amount(), RedOakPrinter.STYLE_NONE);
        mPrinter.printLine("", "", "", RedOakPrinter.STYLE_NONE);   //跳行

        //27.其他
        mPrinter.printLine(context.getString(R.string.others), "", "", RedOakPrinter.STYLE_BOLD);
        mPrinter.printLine("-------------------------------------------------------------------", "", "", RedOakPrinter.STYLE_NONE);
        mPrinter.printLine(context.getString(R.string.customer_amount), "", "" + accountingInformation.getOther().getCustomer_count(), RedOakPrinter.STYLE_NONE);
        mPrinter.printLine(context.getString(R.string.sale_quantity), "", "" + accountingInformation.getOther().getSale_count(), RedOakPrinter.STYLE_NONE);
        mPrinter.printLine(context.getString(R.string.average_customer_price), "", "" + accountingInformation.getOther().getAverage_price(), RedOakPrinter.STYLE_NONE);
        mPrinter.printLine("", "", "", RedOakPrinter.STYLE_NONE);    //跳行

        mPrinter.feed();

        final RedOakPrinter.PrintResult printResult = mPrinter.waitForResult((Activity) context);

        //end
        mPrinter.close();

        return super.printDateRevenue(context, revenue);
    }

    private static String getPayName(String type) {
        if (PaymentsType.CREDIT_CARD.name().equals(type)) {
            return "信用卡";
        }
        if (PaymentsType.EASY_CARD.name().equals(type)) {
            return "悠遊卡";
        }
        if (PaymentsType.LINE_PAY.name().equals(type)) {
            return "LINE Pay";
        }
        if (PaymentsType.PI_PAY.name().equals(type)) {
            return "Pi 拍錢包";
        }

        if (PaymentsType.CASH_TICKET.name().equals(type)) {
            return "現金券";
        }

        if (PaymentsType.CASH_MODULE.name().equals(type)) {
            return "現金";
        }
        if (PaymentsType.TAIWAN_PAY.name().equals(type)) {
            return "台灣 Pay";
        }
        if(PaymentsType.CASH.name().equals(type)){
            return "臨櫃結帳";
        }
        if(PaymentsType.APPLE_PAY.name().equals(type)){
            return "Apple Pay";
        }
        if(PaymentsType.Easy_Wallet.name().equals(type)){
            return "悠遊付";
        }
        return type;
    }

    @Override
    public void releaseAll(Context context) {

    }

    @Override
    public void requestPrinterPermission(Context context) {

    }

    @Override
    public boolean disconnect() {
        return true;
    }

    @Override
    public boolean isConnect() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
