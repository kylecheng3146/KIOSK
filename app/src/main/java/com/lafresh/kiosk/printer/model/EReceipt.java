package com.lafresh.kiosk.printer.model;

public class EReceipt {
    private String receiptTitle;
    private String eReceiptNumber;//發票字軌號碼 (10)：記錄發票完整 10 碼字軌號碼。
    private String eReceiptDate;//發票開立日期 (7)：記錄發票 3 碼民國年份 2 碼月份 2 碼日期共 7 碼。
    private String randomNumber;//隨機碼 (4)：記錄發票 4 碼隨機碼。
    private String saleAmount;
    private String totalAmount;
    private String unTaxAmount;
    private String taxAmount;
    private String year;
    private String startMonth;
    private String endMonth;
    private String buyerGuiNumber = "";//買方統一編號 (8)：記錄發票買受人統一編號， 若買受人為一般消費者則以 00000000 記載。
    private String sellerGuiNumber;//賣方統一編號 (8)：記錄發票賣方統一編號。
    private String[] qrCodeData;

    private String carrierNumber;//載具
    private String loveCode;//捐贈碼
    private boolean useMemberCarrier;

    public String getReceiptTitle() {
        return receiptTitle;
    }

    public void setReceiptTitle(String receiptTitle) {
        this.receiptTitle = receiptTitle;
    }

    public String getEReceiptNumber() {
        return eReceiptNumber;
    }

    public void setEReceiptNumber(String eReceiptNumber) {
        this.eReceiptNumber = eReceiptNumber;
    }

    public String getEReceiptDate() {
        return eReceiptDate;
    }

    public void setEReceiptDate(String eReceiptDate) {
        this.eReceiptDate = eReceiptDate;
    }

    public String getRandomNumber() {
        return randomNumber;
    }

    public void setRandomNumber(String randomNumber) {
        this.randomNumber = randomNumber;
    }

    public String getSaleAmount() {
        return saleAmount;
    }

    public void setSaleAmount(String saleAmount) {
        this.saleAmount = saleAmount;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(String startMonth) {
        this.startMonth = startMonth;
    }

    public String getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(String endMonth) {
        this.endMonth = endMonth;
    }

    public String getBuyerGuiNumber() {
        return buyerGuiNumber;
    }

    public void setBuyerGuiNumber(String buyerGuiNumber) {
        this.buyerGuiNumber = buyerGuiNumber;
    }

    public String getSellerGuiNumber() {
        return sellerGuiNumber;
    }

    public void setSellerGuiNumber(String sellerGuiNumber) {
        this.sellerGuiNumber = sellerGuiNumber;
    }

    public String getBarCodeData() {
        return getPeriodData() + eReceiptNumber + randomNumber;
    }

    public String[] getQrCodeData() {
        return qrCodeData;
    }

    public void setQrCodeData(String[] qrCodeData) {
        this.qrCodeData = qrCodeData;
    }

    public String getCarrierNumber() {
        return carrierNumber;
    }

    public void setCarrierNumber(String carrierNumber) {
        this.carrierNumber = carrierNumber;
    }

    public String getLoveCode() {
        return loveCode;
    }

    public void setLoveCode(String loveCode) {
        this.loveCode = loveCode;
    }

    public String getUnTaxAmount() {
        return unTaxAmount;
    }

    public void setUnTaxAmount(String unTaxAmount) {
        this.unTaxAmount = unTaxAmount;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    //期別 取雙數月 ex:10906
    private String getPeriodData() {
        return getTwYearString() + endMonth;
    }

    public String getTwYearString() {
        int i = Integer.parseInt(year);
        return String.valueOf(i - 1911);
    }

    public void setMemberCarrier(boolean useMemberCarrier) {
        this.useMemberCarrier = useMemberCarrier;
    }

    public boolean getMemberCarrier() {
        return useMemberCarrier;
    }
}
