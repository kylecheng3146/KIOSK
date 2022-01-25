package com.lafresh.kiosk.model;

public class ReceiptData {
    /**
     * tax_ID_number : 24436074
     * npoban :
     * carrier_type :
     * carrier :
     * receipt_number :
     * random_number : 3931
     * AESKey :
     * title :
     * begin_ym : 202007
     * end_ym : 202008
     * take_number : true
     */

    private String tax_ID_number;
    private String npoban;
    private String carrier_type;
    private String carrier;
    private String receipt_number;
    private String random_number;
    private String AESKey;
    private String title;
    private String begin_ym;
    private String end_ym;
    private String gui_number;
    private int totalAmount;
    private boolean take_number;
    private boolean use_member_carrier;

    public boolean isUseMemberCarrier() {
        return use_member_carrier;
    }

    public void setUseMemberCarrier(boolean useMemberCarrier) {
        this.use_member_carrier = useMemberCarrier;
    }

    public String getTax_ID_number() {
        return tax_ID_number;
    }

    public void setTax_ID_number(String tax_ID_number) {
        this.tax_ID_number = tax_ID_number;
    }

    public String getNpoban() {
        return npoban;
    }

    public void setNpoban(String npoban) {
        this.npoban = npoban;
    }

    public String getCarrier_type() {
        return carrier_type;
    }

    public void setCarrier_type(String carrier_type) {
        this.carrier_type = carrier_type;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getReceipt_number() {
        return receipt_number;
    }

    public void setReceipt_number(String receipt_number) {
        this.receipt_number = receipt_number;
    }

    public String getRandom_number() {
        return random_number;
    }

    public void setRandom_number(String random_number) {
        this.random_number = random_number;
    }

    public String getAESKey() {
        return AESKey;
    }

    public void setAESKey(String AESKey) {
        this.AESKey = AESKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBegin_ym() {
        return begin_ym;
    }

    public void setBegin_ym(String begin_ym) {
        this.begin_ym = begin_ym;
    }

    public String getEnd_ym() {
        return end_ym;
    }

    public void setEnd_ym(String end_ym) {
        this.end_ym = end_ym;
    }

    public String getGui_number() {
        return gui_number;
    }

    public void setGui_number(String gui_number) {
        this.gui_number = gui_number;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public boolean getTakeNumber() {
        return take_number;
    }

    public void setTakeNumber(boolean take_number) {
        this.take_number = take_number;
    }
}
