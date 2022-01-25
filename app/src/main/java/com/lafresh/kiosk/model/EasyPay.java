package com.lafresh.kiosk.model;

public class EasyPay {
    private String authkey;
    private String order_id;
    private String shop_id;
    private int amount;
    private String qr_code;
    private int retry_time;

    public String getAuthkey() {
        return authkey;
    }

    public void setAuthkey(String authkey) {
        this.authkey = authkey;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getQr_code() {
        return qr_code;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }

    public int getRetryTime() {
        return retry_time;
    }

    public void setRetryTime(int retry_time) {
        this.retry_time = retry_time;
    }
}
