package com.lafresh.kiosk.pipay;

import com.google.gson.annotations.SerializedName;

public class PiRes {
    /**
     * status : success
     * bill_id : 12345678987654321
     * psp_id : pi
     * wallet_transaction_id : TX1942620753651392
     * trans_status : accepted
     * amt : 100
     * sale_amt : 100
     * bill_amt : 100
     * proxy_amt : 100
     * carrier_id_2 : /VXXP-O2
     * channel_id : LAFRESH
     * store_no : 222222
     * reg_id : 2
     * seq_no : 0000000001
     * items : 統一豆漿*2 $40,統一米漿*3 $60#$#$0000000000
     * create_time : 20190627174422
     * member_card_id :
     * member_card_type :
     * payment_type : 08
     * balance_amount : 0
     * 2nd_funding_amount : 0
     * 2nd_funding_type :
     * 2nd_funding_source_code :
     * merchant_discount_code :
     * merchant_discount_amount : 0
     * psp_discount_code :
     * psp_discount_amount : 0
     * psp_bonuspoint : 100
     * psp_bonuspoint_amount : 100
     * clearing_by : piwallet
     * barcode : PI89928FECDD9D6949
     * coupon_id :
     */

    private String status;
    private String error_code;
    private String error_desc;
    private String bill_id;
    private String psp_id;
    private String wallet_transaction_id;
    private String trans_status;
    private int amt;
    private int sale_amt;
    private int bill_amt;
    private int proxy_amt;
    private String carrier_id_2;
    private String channel_id;
    private String store_no;
    private String reg_id;
    private String seq_no;
    private String items;
    private String create_time;
    private String member_card_id;
    private String member_card_type;
    private String payment_type;
    private int balance_amount;
    @SerializedName("2nd_funding_amount")
    private int _$2nd_funding_amount;
    @SerializedName("2nd_funding_type")
    private String _$2nd_funding_type;
    @SerializedName("2nd_funding_source_code")
    private String _$2nd_funding_source_code;
    private String merchant_discount_code;
    private int merchant_discount_amount;
    private String psp_discount_code;
    private int psp_discount_amount;
    private int psp_bonuspoint;
    private int psp_bonuspoint_amount;
    private String clearing_by;
    private String barcode;
    private String coupon_id;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getError_desc() {
        return error_desc;
    }

    public void setError_desc(String error_desc) {
        this.error_desc = error_desc;
    }

    public String getBill_id() {
        return bill_id;
    }

    public void setBill_id(String bill_id) {
        this.bill_id = bill_id;
    }

    public String getPsp_id() {
        return psp_id;
    }

    public void setPsp_id(String psp_id) {
        this.psp_id = psp_id;
    }

    public String getWallet_transaction_id() {
        return wallet_transaction_id;
    }

    public void setWallet_transaction_id(String wallet_transaction_id) {
        this.wallet_transaction_id = wallet_transaction_id;
    }

    public String getTrans_status() {
        return trans_status;
    }

    public void setTrans_status(String trans_status) {
        this.trans_status = trans_status;
    }

    public int getAmt() {
        return amt;
    }

    public void setAmt(int amt) {
        this.amt = amt;
    }

    public int getSale_amt() {
        return sale_amt;
    }

    public void setSale_amt(int sale_amt) {
        this.sale_amt = sale_amt;
    }

    public int getBill_amt() {
        return bill_amt;
    }

    public void setBill_amt(int bill_amt) {
        this.bill_amt = bill_amt;
    }

    public int getProxy_amt() {
        return proxy_amt;
    }

    public void setProxy_amt(int proxy_amt) {
        this.proxy_amt = proxy_amt;
    }

    public String getCarrier_id_2() {
        return carrier_id_2;
    }

    public void setCarrier_id_2(String carrier_id_2) {
        this.carrier_id_2 = carrier_id_2;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getStore_no() {
        return store_no;
    }

    public void setStore_no(String store_no) {
        this.store_no = store_no;
    }

    public String getReg_id() {
        return reg_id;
    }

    public void setReg_id(String reg_id) {
        this.reg_id = reg_id;
    }

    public String getSeq_no() {
        return seq_no;
    }

    public void setSeq_no(String seq_no) {
        this.seq_no = seq_no;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getMember_card_id() {
        return member_card_id;
    }

    public void setMember_card_id(String member_card_id) {
        this.member_card_id = member_card_id;
    }

    public String getMember_card_type() {
        return member_card_type;
    }

    public void setMember_card_type(String member_card_type) {
        this.member_card_type = member_card_type;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public int getBalance_amount() {
        return balance_amount;
    }

    public void setBalance_amount(int balance_amount) {
        this.balance_amount = balance_amount;
    }

    public int get_$2nd_funding_amount() {
        return _$2nd_funding_amount;
    }

    public void set_$2nd_funding_amount(int _$2nd_funding_amount) {
        this._$2nd_funding_amount = _$2nd_funding_amount;
    }

    public String get_$2nd_funding_type() {
        return _$2nd_funding_type;
    }

    public void set_$2nd_funding_type(String _$2nd_funding_type) {
        this._$2nd_funding_type = _$2nd_funding_type;
    }

    public String get_$2nd_funding_source_code() {
        return _$2nd_funding_source_code;
    }

    public void set_$2nd_funding_source_code(String _$2nd_funding_source_code) {
        this._$2nd_funding_source_code = _$2nd_funding_source_code;
    }

    public String getMerchant_discount_code() {
        return merchant_discount_code;
    }

    public void setMerchant_discount_code(String merchant_discount_code) {
        this.merchant_discount_code = merchant_discount_code;
    }

    public int getMerchant_discount_amount() {
        return merchant_discount_amount;
    }

    public void setMerchant_discount_amount(int merchant_discount_amount) {
        this.merchant_discount_amount = merchant_discount_amount;
    }

    public String getPsp_discount_code() {
        return psp_discount_code;
    }

    public void setPsp_discount_code(String psp_discount_code) {
        this.psp_discount_code = psp_discount_code;
    }

    public int getPsp_discount_amount() {
        return psp_discount_amount;
    }

    public void setPsp_discount_amount(int psp_discount_amount) {
        this.psp_discount_amount = psp_discount_amount;
    }

    public int getPsp_bonuspoint() {
        return psp_bonuspoint;
    }

    public void setPsp_bonuspoint(int psp_bonuspoint) {
        this.psp_bonuspoint = psp_bonuspoint;
    }

    public int getPsp_bonuspoint_amount() {
        return psp_bonuspoint_amount;
    }

    public void setPsp_bonuspoint_amount(int psp_bonuspoint_amount) {
        this.psp_bonuspoint_amount = psp_bonuspoint_amount;
    }

    public String getClearing_by() {
        return clearing_by;
    }

    public void setClearing_by(String clearing_by) {
        this.clearing_by = clearing_by;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(String coupon_id) {
        this.coupon_id = coupon_id;
    }
}
