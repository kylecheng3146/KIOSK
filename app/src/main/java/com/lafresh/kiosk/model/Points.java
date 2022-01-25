package com.lafresh.kiosk.model;

/**
 * Created by Kyle on 2020/11/19.
 */

public class Points {

    /**
     * before_transaction : 120
     * exchange_amount : 10
     * after_transaction : 21
     * add : 1
     */

    private int before_transaction;
    private int exchange_amount;
    private int after_transaction;
    private int add;
    private int use;

    public int getBefore_transaction() {
        return before_transaction;
    }

    public void setBefore_transaction(int before_transaction) {
        this.before_transaction = before_transaction;
    }

    public int getExchange_amount() {
        return exchange_amount;
    }

    public void setExchange_amount(int exchange_amount) {
        this.exchange_amount = exchange_amount;
    }

    public int getAfter_transaction() {
        return after_transaction;
    }

    public void setAfter_transaction(int after_transaction) {
        this.after_transaction = after_transaction;
    }

    public int getAdd() {
        return add;
    }

    public void setAdd(int add) {
        this.add = add;
    }

    public int getUse() {
        return use;
    }

    public void setUse(int use) {
        this.use = use;
    }
}
