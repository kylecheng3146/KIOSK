package com.lafresh.kiosk.easycard.model;

public class EcSettle3Parameter {

    /**
     * store_id : 001
     * pos_id : k1
     * sale_date : 2020-01-01T15:16:54.723Z
     * TXN_Serial_number : 152845
     * TXN_DateTime : 2020-01-01T15:16:54.723Z
     * Batch_number : 19010312
     * new_Deviceid : 0001088101304099
     * Deduct_count : 1
     * Refund_count : 0
     * AddValue_count : 0
     * Deduct_amt : 5
     * Refund_amt : 0
     * Total_AddValue_amt : 0
     * Total_Txn_count : 1
     * Total_Txn_amt : 5
     * Total_Txn_pure_amt : 5
     */

    private String store_id;
    private String pos_id;
    private String sale_date;
    private String TXN_Serial_number;
    private String TXN_DateTime;
    private String Batch_number;
    private String new_Deviceid;
    private String Settlement_Status;
    private int Deduct_count;
    private int Refund_count;
    private int AddValue_count;
    private int Deduct_amt;
    private int Refund_amt;
    private int Total_AddValue_amt;
    private int Total_Txn_count;
    private int Total_Txn_amt;
    private int Total_Txn_pure_amt;

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getPos_id() {
        return pos_id;
    }

    public void setPos_id(String pos_id) {
        this.pos_id = pos_id;
    }

    public String getSale_date() {
        return sale_date;
    }

    public void setSale_date(String sale_date) {
        this.sale_date = sale_date;
    }

    public String getTXN_Serial_number() {
        return TXN_Serial_number;
    }

    public void setTXN_Serial_number(String TXN_Serial_number) {
        this.TXN_Serial_number = TXN_Serial_number;
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

    public void setBatch_number(String Batch_number) {
        this.Batch_number = Batch_number;
    }

    public String getNew_Deviceid() {
        return new_Deviceid;
    }

    public String getSettlement_Status() {
        return Settlement_Status;
    }

    public void setSettlement_Status(String settlement_Status) {
        Settlement_Status = settlement_Status;
    }

    public void setNew_Deviceid(String new_Deviceid) {
        this.new_Deviceid = new_Deviceid;
    }

    public int getDeduct_count() {
        return Deduct_count;
    }

    public void setDeduct_count(int Deduct_count) {
        this.Deduct_count = Deduct_count;
    }

    public int getRefund_count() {
        return Refund_count;
    }

    public void setRefund_count(int Refund_count) {
        this.Refund_count = Refund_count;
    }

    public int getAddValue_count() {
        return AddValue_count;
    }

    public void setAddValue_count(int AddValue_count) {
        this.AddValue_count = AddValue_count;
    }

    public int getDeduct_amt() {
        return Deduct_amt;
    }

    public void setDeduct_amt(int Deduct_amt) {
        this.Deduct_amt = Deduct_amt;
    }

    public int getRefund_amt() {
        return Refund_amt;
    }

    public void setRefund_amt(int Refund_amt) {
        this.Refund_amt = Refund_amt;
    }

    public int getTotal_AddValue_amt() {
        return Total_AddValue_amt;
    }

    public void setTotal_AddValue_amt(int Total_AddValue_amt) {
        this.Total_AddValue_amt = Total_AddValue_amt;
    }

    public int getTotal_Txn_count() {
        return Total_Txn_count;
    }

    public void setTotal_Txn_count(int Total_Txn_count) {
        this.Total_Txn_count = Total_Txn_count;
    }

    public int getTotal_Txn_amt() {
        return Total_Txn_amt;
    }

    public void setTotal_Txn_amt(int Total_Txn_amt) {
        this.Total_Txn_amt = Total_Txn_amt;
    }

    public int getTotal_Txn_pure_amt() {
        return Total_Txn_pure_amt;
    }

    public void setTotal_Txn_pure_amt(int Total_Txn_pure_amt) {
        this.Total_Txn_pure_amt = Total_Txn_pure_amt;
    }
}
