package com.lafresh.kiosk.httprequest.model;

import com.google.gson.annotations.SerializedName;

public class AuthParameter {
    @SerializedName("authkey")
    private String authKey;

    @SerializedName("acckey")
    private String accKey;

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getAccKey() {
        return accKey;
    }

    public void setAccKey(String accKey) {
        this.accKey = accKey;
    }
}
