package com.lafresh.kiosk.model;

public class StupidPrice {
    /**
     * 意義是三小，林北只知道幹譙!
     * amount : 1100
     * currency_code : TWD
     * formatted_amount : 1100
     */

    private double amount;
    private String currency_code;
    private String formatted_amount;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public String getFormatted_amount() {
        return formatted_amount;
    }

    public void setFormatted_amount(String formatted_amount) {
        this.formatted_amount = formatted_amount;
    }
}
