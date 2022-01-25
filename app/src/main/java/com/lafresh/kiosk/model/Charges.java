package com.lafresh.kiosk.model;

public class Charges {


    /**
     * total : {"amount":1100,"currency_code":"TWD","formatted_amount":"1100"}
     * sub_total : {"amount":1100,"currency_code":"TWD","formatted_amount":"1100"}
     * discount : {"amount":100,"currency_code":"TWD","formatted_amount":"100"}
     * total_fee : {"amount":1000,"currency_code":"TWD","formatted_amount":"1000"}
     */

    private StupidPrice total;
    private StupidPrice sub_total;
    private StupidPrice discount;
    private StupidPrice total_fee;
    private StupidPrice total_discharge;
    private Points points;

    public StupidPrice getTotal() {
        return total;
    }

    public void setTotal(StupidPrice total) {
        this.total = total;
    }

    public StupidPrice getSub_total() {
        return sub_total;
    }

    public void setSub_total(StupidPrice sub_total) {
        this.sub_total = sub_total;
    }

    public StupidPrice getDiscount() {
        return discount;
    }

    public void setDiscount(StupidPrice discount) {
        this.discount = discount;
    }

    public StupidPrice getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(StupidPrice total_fee) {
        this.total_fee = total_fee;
    }

    public StupidPrice getTotal_Discharge() {
        return total_discharge;
    }

    public void setTotal_Discharge(StupidPrice total_discharge) {
        this.total_discharge = total_discharge;
    }

    public void setPoints(Points points) {
        this.points = points;
    }

    public Points getPoints() {
        return points;
    }
}
