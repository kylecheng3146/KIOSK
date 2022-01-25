package com.lafresh.kiosk.httprequest.model;

public class WebOrder02 {
    /**
     * company_id : LAJOIN2
     * shop_id : 11
     * worder_id : 18122500003
     * pay_id : Q006
     * amount : 30.0000
     * old_amount : .0000
     * relate_id : 18122500003|2018122560354678410
     * isinv : 1
     * approveCode : 2018122560354678410
     * Request_status : 0
     * Refund_status : 0
     */

    private String company_id;
    private String shop_id;
    private String worder_id;
    private String pay_id;
    private String amount;
    private String old_amount;
    private String relate_id;
    private String isinv;
    private String approveCode;
    private String Request_status;
    private String Refund_status;

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getWorder_id() {
        return worder_id;
    }

    public void setWorder_id(String worder_id) {
        this.worder_id = worder_id;
    }

    public String getPay_id() {
        return pay_id;
    }

    public void setPay_id(String pay_id) {
        this.pay_id = pay_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOld_amount() {
        return old_amount;
    }

    public void setOld_amount(String old_amount) {
        this.old_amount = old_amount;
    }

    public String getRelate_id() {
        return relate_id;
    }

    public void setRelate_id(String relate_id) {
        this.relate_id = relate_id;
    }

    public String getIsinv() {
        return isinv;
    }

    public void setIsinv(String isinv) {
        this.isinv = isinv;
    }

    public String getApproveCode() {
        return approveCode;
    }

    public void setApproveCode(String approveCode) {
        this.approveCode = approveCode;
    }

    public String getRequest_status() {
        return Request_status;
    }

    public void setRequest_status(String Request_status) {
        this.Request_status = Request_status;
    }

    public String getRefund_status() {
        return Refund_status;
    }

    public void setRefund_status(String Refund_status) {
        this.Refund_status = Refund_status;
    }
}
