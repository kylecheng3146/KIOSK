package com.lafresh.kiosk.easycard.model;

public class EcSettle3 {

    /**
     * Deduct_count : 1
     * Refund_count : 0
     * AddValue_count : 0
     * Deduct_amt : 5.0
     * Refund_amt : 0.0
     * Total_AddValue_amt : 0.0
     * Total_Txn_count : 1
     * Total_Txn_amt : 5.0
     * Total_Txn_pure_amt : 5.0
     */

    private int Deduct_count;
    private int Refund_count;
    private int AddValue_count;
    private int Deduct_amt;
    private int Refund_amt;
    private int Total_AddValue_amt;
    private int Total_Txn_count;
    private int Total_Txn_amt;
    private int Total_Txn_pure_amt;

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

    public void setDeduct_amt(int deduct_amt) {
        Deduct_amt = deduct_amt;
    }

    public int getRefund_amt() {
        return Refund_amt;
    }

    public void setRefund_amt(int refund_amt) {
        Refund_amt = refund_amt;
    }

    public int getTotal_AddValue_amt() {
        return Total_AddValue_amt;
    }

    public void setTotal_AddValue_amt(int total_AddValue_amt) {
        Total_AddValue_amt = total_AddValue_amt;
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

    public void setTotal_Txn_amt(int total_Txn_amt) {
        Total_Txn_amt = total_Txn_amt;
    }

    public int getTotal_Txn_pure_amt() {
        return Total_Txn_pure_amt;
    }

    public void setTotal_Txn_pure_amt(int total_Txn_pure_amt) {
        Total_Txn_pure_amt = total_Txn_pure_amt;
    }
}
