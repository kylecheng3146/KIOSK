package com.lafresh.kiosk.printer;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.lafresh.kiosk.model.Item;
import com.lafresh.kiosk.model.Order;
import com.lafresh.kiosk.model.ReceiptData;
import com.lafresh.kiosk.model.SaleType;
import com.lafresh.kiosk.printer.model.EReceipt;
import com.lafresh.kiosk.printer.model.Payment;
import com.lafresh.kiosk.printer.model.Product;
import com.lafresh.kiosk.printer.model.ReceiptDetail;
import com.lafresh.kiosk.type.PaymentsType;
import com.lafresh.kiosk.utils.CommonUtils;
import com.lafresh.kiosk.utils.EReceiptUtil;
import com.lafresh.kiosk.utils.FormatUtil;
import com.lafresh.kiosk.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.M)
public class DataTransformer {

    public static EReceipt generateEReceipt(ReceiptData data, List<Product> productList) {
        EReceipt eReceipt = new EReceipt();
        eReceipt.setReceiptTitle(data.getTitle());
        eReceipt.setLoveCode(data.getNpoban());
        eReceipt.setCarrierNumber(data.getCarrier());
        eReceipt.setMemberCarrier(data.isUseMemberCarrier());
        eReceipt.setEReceiptNumber(data.getReceipt_number());
        eReceipt.setRandomNumber(data.getRandom_number());
        eReceipt.setBuyerGuiNumber(data.getTax_ID_number());
        eReceipt.setSellerGuiNumber(data.getGui_number());
        eReceipt.setYear(data.getBegin_ym().substring(0, 4));
        eReceipt.setStartMonth(data.getBegin_ym().substring(4));
        eReceipt.setEndMonth(data.getEnd_ym().substring(4));
        String receiptDate = FormatUtil.parseRocYear(eReceipt.getYear()) +
                TimeUtil.getNowTime(TimeUtil.MONTH_DATE_PATTERN);
        eReceipt.setEReceiptDate(receiptDate);
        int totalAmount = data.getTotalAmount();
        int unTaxAmount = (int) Math.round(totalAmount / 1.05);
        eReceipt.setTotalAmount(totalAmount + "");
        eReceipt.setUnTaxAmount(unTaxAmount + "");
        eReceipt.setTaxAmount((totalAmount - unTaxAmount) + "");
        String qrCode77 = EReceiptUtil.getQRCode77(
                data.getAESKey(), eReceipt.getEReceiptNumber(), receiptDate,
                eReceipt.getRandomNumber(), eReceipt.getUnTaxAmount(), eReceipt.getTotalAmount(),
                eReceipt.getBuyerGuiNumber(), eReceipt.getSellerGuiNumber());
        String qrCodeData = EReceiptUtil.getQrCodeData(qrCode77, productList);
        eReceipt.setQrCodeData(EReceiptUtil.splitQRCodeData(qrCodeData));
        return eReceipt;
    }

    public static ReceiptDetail generateReceiptDetail(Order order, List<Product> productList) {
        ReceiptDetail detail = new ReceiptDetail();
        detail.setOrderId(order.getId());
        detail.setPickupMethod(order.getPickupMethod());
        detail.setTotal(order.getCharges().getTotal().getAmount());
        detail.setTotalFee((int) order.getCharges().getTotal_fee().getAmount());
        detail.setDiscount((int) order.getCharges().getDiscount().getAmount());
        detail.setTotalDischarge(order.getCharges().getTotal_Discharge().getAmount());
        detail.setSaleType(getSaleTypeName(order.getType()));
        detail.setTakeMealNumber(order.getDisplay_id());
        detail.setTableNumber(order.getTable_number());
        detail.setProductList(generateProductList(order));
        List<Payment> paymentList = new ArrayList<>();
        for (com.lafresh.kiosk.model.Payment p : order.getPayments()) {
            if (PaymentsType.CASH.name().equals(p.getType())) {
                continue;//櫃台結帳不印出
            }
            Payment payment = generatePayment(p);
            paymentList.add(payment);
        }
        detail.setPaymentList(paymentList);
        return detail;
    }

    public static List<Product> generateProductList(Order order) {
        List<Product> productList = new ArrayList<>();
        for (Item item : order.getCart().getItems()) {
            Product product = generateProduct(item);
            productList.add(product);
        }
        return productList;
    }

    private static Product generateProduct(Item item) {
        Product product = new Product();
        validateProductIsClone(item, product);
        product.setUnitPrice(item.getPrice().getUnit_price().getAmount());
        product.setTotalPrice(item.getPrice().getTotal_price().getAmount());
        product.setIsOnSale(item.getPrice().getUnit_price().getAmount() > (item.getPrice().getTotal_price().getAmount() / item.getQuantity()));
        product.setQuantity(item.getQuantity());
        product.setMemo(generateProductMemo(item));
        return product;
    }

    /**
     * 弘爺的資料格式，如果為虛擬商品則隱藏此項，但若有加值則需顯示.
     * */
    private static void validateProductIsClone(Item item, Product product) {
        if(!item.getIsHidden()) {
            product.setName(CommonUtils.INSTANCE.switchProductNameType(item));
            product.setIsUsePoint(item.getPrice().getSpendPoints() > 0);
            product.setTicketNo(item.getTicket_no());
            product.setIsUsePoint(item.getRedeemPoint() > 0);
        }else{
            product.setName("");
            product.setIsUsePoint(false);
            product.setTicketNo("");
            product.setIsUsePoint(false);
        }
    }

    private static String generateProductMemo(Item item) {
        StringBuilder builder = new StringBuilder();
        for (Item.SelectedGroup group : item.getSelected_modifier_groups()) {
            for (Item taste : group.getSelected_items()) {
                if (builder.length() > 0) {
                    builder.append(";");
                }
                builder.append(taste.getTitle());
                int addAmount = (int) taste.getPrice().getUnit_price().getAmount();
                if (addAmount > 0) {
                    builder.append("{+").append(addAmount).append("}");
                }
            }
        }
        return builder.toString();
    }

    private static Payment generatePayment(com.lafresh.kiosk.model.Payment p) {
        Payment payment = new Payment();
        payment.setAmount(p.getPayment_amount());
        payment.setType(p.getType());
        payment.setName(getPayName(p.getType()));
        return payment;
    }

    private static String getSaleTypeName(String type) {
        if (SaleType.DINE_IN.name().equals(type)) {
            return "內用";
        }
        if (SaleType.PICK_UP.name().equals(type)) {
            return "外帶";
        }
        if (SaleType.DELIVERY.name().equals(type)) {
            return "外送";
        }
        return "";
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
        return "";
    }

}
