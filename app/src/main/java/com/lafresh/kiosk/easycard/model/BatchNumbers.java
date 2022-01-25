package com.lafresh.kiosk.easycard.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BatchNumbers {
    @SerializedName("batch_numbers")
    private List<BatchNumber> batchNumberList;

    public List<BatchNumber> getBatchNumberList() {
        return batchNumberList;
    }

    public void setBatchNumberList(List<BatchNumber> batchNumberList) {
        this.batchNumberList = batchNumberList;
    }
}
