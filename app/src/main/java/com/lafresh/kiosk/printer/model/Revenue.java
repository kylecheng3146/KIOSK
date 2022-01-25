package com.lafresh.kiosk.printer.model;

import com.lafresh.kiosk.model.GetAccountingInformation;

public class Revenue {
    private String startDate;
    private String endDate;
    private GetAccountingInformation accountingInformation;

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setAccountingInformation(GetAccountingInformation accountingInformation) {
        this.accountingInformation = accountingInformation;
    }

    public GetAccountingInformation getAccountingInformation() {
        return accountingInformation;
    }
}
