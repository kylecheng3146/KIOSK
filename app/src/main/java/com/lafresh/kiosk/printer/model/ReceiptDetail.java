package com.lafresh.kiosk.printer.model;

import com.lafresh.kiosk.creditcardlib.nccc.NCCCTransDataBean;
import com.lafresh.kiosk.easycard.model.EcPayData;
import com.lafresh.kiosk.model.PickupMethod;

import java.util.List;

public class ReceiptDetail {
    private String orderId;
    private double total;
    private int discount;
    private String tableNumber;
    private int taxableAmount;//應稅銷售額
    private int taxAmount;//稅額
    private String saleType;//內用外帶
    private String takeMealNumber;//取餐號
    private PickupMethod pickupMethod;

    private List<Product> productList;
    private List<Payment> paymentList;

    private EcPayData ecPayData;
    private NCCCTransDataBean ncccTransDataBean;
    private double totalDischarge;
    private double totalFee;

    public PickupMethod getPickupMethod() {
        return pickupMethod;
    }

    public void setPickupMethod(PickupMethod pickupMethod) {
        this.pickupMethod = pickupMethod;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(int totalFee) {
        this.totalFee = totalFee;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public double getTotalDischarge() {
        return totalDischarge;
    }

    public void setTotalDischarge(double totalDischarge) {
        this.totalDischarge = totalDischarge;
    }


    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getTaxableAmount() {
        return taxableAmount;
    }

    public void setTaxableAmount(int taxableAmount) {
        this.taxableAmount = taxableAmount;
    }

    public int getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(int taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getSaleType() {
        return saleType;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

    public String getTakeMealNumber() {
        return takeMealNumber;
    }

    public void setTakeMealNumber(String takeMealNumber) {
        this.takeMealNumber = takeMealNumber;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public List<Payment> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(List<Payment> paymentList) {
        this.paymentList = paymentList;
    }

    public EcPayData getEcPayData() {
        return ecPayData;
    }

    public void setEcPayData(EcPayData ecPayData) {
        this.ecPayData = ecPayData;
    }

    public NCCCTransDataBean getNcccTransDataBean() {
        return ncccTransDataBean;
    }

    public void setNcccTransDataBean(NCCCTransDataBean ncccTransDataBean) {
        this.ncccTransDataBean = ncccTransDataBean;
    }
}
