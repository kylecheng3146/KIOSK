package com.lafresh.kiosk.lanxin.model;

public class EzAIOData {
    /**
     * packageName: com.lafresh.kiost
     * packagePage: com.lafresh.kiost.activity.HomeActivity
     * transType: 01
     * hostID: 01
     * transAmount: 100
     * installmentPeriod: 0
     * tradeNo: ""
     * posID: 0001
     * orderID: A202101010001
     * walletBarcode: ""
     */

    private String packageName; //回傳的PackageName
    private String packagePage; //回傳的PackagePage
    private String transType;   //交易別(參考API文件)
    private String hostID;      //銀行別(參考API文件)
    private String transAmount; //交易金額
    private String installmentPeriod;   //分期期數
    private String tradeNo;     //簡單付序號(結帳時可為空，退款時需帶入序號)
    private String posID = "0001";      //收銀機編號(可為固定值)
    private String orderID;     //商店訂單編號
    private String walletBarcode;   //電子錢包交易碼

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackagePage() {
        return packagePage;
    }

    public void setPackagePage(String packagePage) {
        this.packagePage = packagePage;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getHostID() {
        return hostID;
    }

    public void setHostID(String hostID) {
        this.hostID = hostID;
    }

    public String getTransAmount() {
        return transAmount;
    }

    public void setTransAmount(String transAmount){
        this.transAmount = transAmount;
    }

    public String getInstallmentPeriod() {
        return installmentPeriod;
    }

    public void setInstallmentPeriod(String installmentPeriod) {
        this.installmentPeriod = installmentPeriod;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getPosID() {
        return posID;
    }

    public void setPosID(String posID) {
        this.posID = posID;
    }

    public String getOrderID() {
        return this.orderID;
    }

    public void setOrderID(String orderID){
        this.orderID = orderID;
    }

    public String getWalletBarcode() {
        return walletBarcode;
    }

    public void setWalletBarcode(String walletBarcode) {
        this.walletBarcode = walletBarcode;
    }
}
