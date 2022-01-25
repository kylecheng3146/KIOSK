package com.lafresh.kiosk.printer.model;

import com.lafresh.kiosk.model.TicketOrderInfo;
import com.lafresh.kiosk.utils.CryptoUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class Ticket {
    private String qrCodeData;
    private String subject;
    private String date;
    private String time;
    private String kind;
    private int price;
    private int discount;
    private String ticketNumber;

    public Ticket() {
    }

    public Ticket(TicketOrderInfo.OrderInfosBean orderInfo, TicketOrderInfo.OrderInfosBean.OrderTicketsBean ticketsBean) {
        this.subject = orderInfo.getSaleExhibition().getSaleExhibitionInfos().get(0).getName();
        this.date = orderInfo.getVisitingDate();
        this.time = orderInfo.getVisitingStartTime();
        this.kind = ticketsBean.getTicket().getTicketInfos().get(0).getName();
        this.price = ticketsBean.getTicket().getPrice();
        this.discount = ticketsBean.getTicket().getCouponPrice();
        this.ticketNumber = ticketsBean.getVerificationCode();
        this.qrCodeData = getQrCodeString(ticketNumber, date);
    }

    private String getQrCodeString(String code, String date) {
        String parkingSecret = "GiantParkingSecret^2020^";
        String resSecret = CryptoUtil.getSHA256StrJava(date + parkingSecret);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code", code);
            jsonObject.put("parkingSecret", resSecret);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String getQrCodeData() {
        return qrCodeData;
    }

    public void setQrCodeData(String qrCodeData) {
        this.qrCodeData = qrCodeData;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }
}
