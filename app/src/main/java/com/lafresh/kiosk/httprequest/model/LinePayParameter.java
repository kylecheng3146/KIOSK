package com.lafresh.kiosk.httprequest.model;

import com.google.gson.annotations.SerializedName;

public class LinePayParameter {
    private int amount;
    private String orderId;
    private String currency = "TWD";
    private String oneTimeKey;

    @SerializedName("authkey")
    private String authKey;

    @SerializedName("MerchantDeviceProfileId")
    private String profileId;
    private int retry_time;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getOneTimeKey() {
        return oneTimeKey;
    }

    public void setOneTimeKey(String oneTimeKey) {
        this.oneTimeKey = oneTimeKey;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public int getRetryTime() {
        return retry_time;
    }

    public void setRetryTime(int retry_time) {
        this.retry_time = retry_time;
    }
}
