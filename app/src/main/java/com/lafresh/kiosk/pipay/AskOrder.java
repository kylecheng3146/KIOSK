package com.lafresh.kiosk.pipay;

public class AskOrder {

    /**
     * bill_id : 12345678987654321
     * wallet_transaction_id : T1428558400000100
     * channel_id : t00001
     */

    private String authkey;
    private String shop_id;

    private String bill_id;
    private String wallet_transaction_id;
    private String channel_id;

    public String getAuthkey() {
        return authkey;
    }

    public void setAuthkey(String authkey) {
        this.authkey = authkey;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getBill_id() {
        return bill_id;
    }

    public void setBill_id(String bill_id) {
        this.bill_id = bill_id;
    }

    public String getWallet_transaction_id() {
        return wallet_transaction_id;
    }

    public void setWallet_transaction_id(String wallet_transaction_id) {
        this.wallet_transaction_id = wallet_transaction_id;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }
}
