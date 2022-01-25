package com.lafresh.kiosk.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kyle on 2021/1/29.
 */

@IgnoreExtraProperties
public class LostOrderInfo {

    public String orderId;
    public List<Payment> payments;

    public LostOrderInfo(String orderId, List<Payment> payments) {
        this.orderId = orderId;
        this.payments = payments;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("payments", payments);
        return result;
    }
}
