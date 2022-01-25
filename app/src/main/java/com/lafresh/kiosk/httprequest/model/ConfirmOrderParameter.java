package com.lafresh.kiosk.httprequest.model;

import com.lafresh.kiosk.model.Payment;

import java.util.List;

public class ConfirmOrderParameter {

    /**
     * id : STORE2005010001
     * payments : [{"type":"CASH","payment_amount":1000,"transaction_id":"2001795436021"}]
     */

    private String id;
    private List<Payment> payments;
    private boolean receipt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public boolean isReceipt() {
        return receipt;
    }

    public void setReceipt(boolean receipt) {
        this.receipt = receipt;
    }
}
