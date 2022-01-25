package com.lafresh.kiosk.printer.model;

public class Product {
    private String name;
    private double unitPrice;
    private double totalPrice;
    private int quantity;
    private String itemDiscount;
    private String memo;
    private String ticketNo;
    private boolean isUsePoint;
    private boolean isOnSale;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean getIsUsePoint() {
        return isUsePoint;
    }

    public void setIsUsePoint(boolean isUsePoint) {
        this.isUsePoint = isUsePoint;
    }


    public String getItemDiscount() {
        return itemDiscount;
    }

    public void setItemDiscount(String itemDiscount) {
        this.itemDiscount = itemDiscount;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getTicketNo() {
        return ticketNo;
    }

    public void setTicketNo(String ticketNo) {
        this.ticketNo = ticketNo;
    }

    public void setIsOnSale(boolean isOnSale) {
        this.isOnSale = isOnSale;
    }

    public boolean getIsOnSale() {
        return this.isOnSale;
    }
}
