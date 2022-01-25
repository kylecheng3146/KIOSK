package com.lafresh.kiosk.easycard.model;

import com.google.gson.annotations.SerializedName;

public class BatchNumber {

    @SerializedName("batch_number")
    private String batchNumber;

    @SerializedName("is_new_batch_number")
    private boolean isNewBatchNumber;

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public boolean isNewBatchNumber() {
        return isNewBatchNumber;
    }

    public void setNewBatchNumber(boolean newBatchNumber) {
        isNewBatchNumber = newBatchNumber;
    }
}
