package com.lafresh.kiosk.easycard.model;

/**
 * 雙卡合一結帳檔
 */

public class EcCheckoutData {
    //Api回傳用
    private int code = -999;
    private String message;

    private String shop_id;
    private String pos_id;
    private String input_date;
    private String TXN_Serial_number;//T1100
    private String user_id;
    private String TXN_DateTime;//T1300 + T1200
    private String Batch_number;//T5501
    private String new_Deviceid;//T4110
    private int Deduct_count;//購貨筆數
    private int Refund_count;//退貨筆數
    private int AddValue_count;//自動加值筆數
    private String Deduct_amt;//購貨總額
    private String Refund_amt;//退貨總額
    private String Total_AddValue_amt;//自動加值總額
    private int Total_Txn_count;//交易總筆數
    private String Total_Txn_amt;//交易總金額
    private String Total_Txn_pure_amt;//交易總淨額
    private String isSettle;
    private String transfer_status;
    private String last_update;
    private String Settlement_Status;//0:平帳、1:不平帳。

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getPos_id() {
        return pos_id;
    }

    public void setPos_id(String pos_id) {
        this.pos_id = pos_id;
    }

    public String getInput_date() {
        return input_date;
    }

    public void setInput_date(String input_date) {
        this.input_date = input_date;
    }

    public String getTXN_Serial_number() {
        return TXN_Serial_number;
    }

    public void setTXN_Serial_number(String TXN_Serial_number) {
        this.TXN_Serial_number = TXN_Serial_number;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTXN_DateTime() {
        return TXN_DateTime;
    }

    public void setTXN_DateTime(String TXN_DateTime) {
        this.TXN_DateTime = TXN_DateTime;
    }

    public String getBatch_number() {
        return Batch_number;
    }

    public void setBatch_number(String batch_number) {
        Batch_number = batch_number;
    }

    public String getNew_Deviceid() {
        return new_Deviceid;
    }

    public void setNew_Deviceid(String new_Deviceid) {
        this.new_Deviceid = new_Deviceid;
    }

    public int getDeduct_count() {
        return Deduct_count;
    }

    public void setDeduct_count(int deduct_count) {
        Deduct_count = deduct_count;
    }

    public int getRefund_count() {
        return Refund_count;
    }

    public void setRefund_count(int refund_count) {
        Refund_count = refund_count;
    }

    public int getAddValue_count() {
        return AddValue_count;
    }

    public void setAddValue_count(int addValue_count) {
        AddValue_count = addValue_count;
    }

    public String getDeduct_amt() {
        return Deduct_amt;
    }

    public void setDeduct_amt(String deduct_amt) {
        Deduct_amt = deduct_amt;
    }

    public String getRefund_amt() {
        return Refund_amt;
    }

    public void setRefund_amt(String refund_amt) {
        Refund_amt = refund_amt;
    }

    public String getTotal_AddValue_amt() {
        return Total_AddValue_amt;
    }

    public void setTotal_AddValue_amt(String total_AddValue_amt) {
        Total_AddValue_amt = total_AddValue_amt;
    }

    public int getTotal_Txn_count() {
        return Total_Txn_count;
    }

    public void setTotal_Txn_count(int total_Txn_count) {
        Total_Txn_count = total_Txn_count;
    }

    public String getTotal_Txn_amt() {
        return Total_Txn_amt;
    }

    public void setTotal_Txn_amt(String total_Txn_amt) {
        Total_Txn_amt = total_Txn_amt;
    }

    public String getTotal_Txn_pure_amt() {
        return Total_Txn_pure_amt;
    }

    public void setTotal_Txn_pure_amt(String total_Txn_pure_amt) {
        Total_Txn_pure_amt = total_Txn_pure_amt;
    }

    public String getIsSettle() {
        return isSettle;
    }

    public void setIsSettle(String isSettle) {
        this.isSettle = isSettle;
    }

    public String getTransfer_status() {
        return transfer_status;
    }

    public void setTransfer_status(String transfer_status) {
        this.transfer_status = transfer_status;
    }

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }

    public String getSettlement_Status() {
        return Settlement_Status;
    }

    public void setSettlement_Status(String settlement_Status) {
        Settlement_Status = settlement_Status;
    }
}
