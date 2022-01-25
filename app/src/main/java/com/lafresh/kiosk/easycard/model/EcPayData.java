package com.lafresh.kiosk.easycard.model;

/**
 * 雙卡合一結帳交易 資料檔
 * 金額會自動加 00 故自行刪除
 */

public class EcPayData {
    private String shop_id;
    private String sale_id;
    private String pos_id;
    private String input_date;
    private String user_id;
    private String job_type;//0 銷售單，1 客戶訂貨單
    private String sale_type;//1 扣款，2 退款，3 扣款加值
    private String Error_code;//T3901
    private String Message_type;//T0100
    private String Processing_code;//T0300 交易類別
    private String TXN_number;//T1100
    private String Host_Serial_number;//T1101
    private String Txn_Time;//T1200 交易時間
    private String Txn_Date;//T1300 交易日期
    private String Batch_number;//T5501 批次號碼
    private String Mifare_ID;//T0200
    private String purse_ID;//T0211
    private String Card_type;//T0213
    private String Personal_profile;//T0214
    private String receipt_card_number;//T0215 收據列印卡號
    private String card_exp;//T1402
    private String RRN;//T3700 RRN
    private String EDC_ID;//T4100
    private String Device_ID;//T4109
    private String New_deviceID;//T4110 二代設備編號
    private String shop_code;//T4200
    private String CPU_purse_version_number;//T4800
    private String Bank_code;//T4803
    private String Area_code;//T4804
    private String CPU_sub_area_code;//T4805
    private String TXN_proof_code;//T6400
    private String n_CPU_EV_Before_TXN;//T0415 交易前餘額
    private String n_CPU_Txn_AMT;//T0400 交易金額
    private String n_CPU_EV;//T0410 交易後餘額
    private String n_AutoLoad_Amount = "0";//T0409 自動加值金額 避免印出null 給預設值為0
    private String IsUpload;
    private String transfer_status;
    private String last_update;

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getSale_id() {
        return sale_id;
    }

    public void setSale_id(String sale_id) {
        this.sale_id = sale_id;
    }

    public String getPos_id() {
        return pos_id;
    }

    public void setPos_id(String pos_id) {
        this.pos_id = pos_id;
    }

    public String getInput_date() {
        return input_date;
    }

    public void setInput_date(String input_date) {
        this.input_date = input_date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getJob_type() {
        return job_type;
    }

    public void setJob_type(String job_type) {
        this.job_type = job_type;
    }

    public String getSale_type() {
        return sale_type;
    }

    public void setSale_type(String sale_type) {
        this.sale_type = sale_type;
    }

    public String getError_code() {
        return Error_code;
    }

    public void setError_code(String error_code) {
        Error_code = error_code;
    }

    public String getMessage_type() {
        return Message_type;
    }

    public void setMessage_type(String message_type) {
        Message_type = message_type;
    }

    public String getProcessing_code() {
        return Processing_code;
    }

    public void setProcessing_code(String processing_code) {
        Processing_code = processing_code;
    }

    public String getTXN_number() {
        return TXN_number;
    }

    public void setTXN_number(String TXN_number) {
        this.TXN_number = TXN_number;
    }

    public String getHost_Serial_number() {
        return Host_Serial_number;
    }

    public void setHost_Serial_number(String host_Serial_number) {
        Host_Serial_number = host_Serial_number;
    }

    public String getTxn_Time() {
        return Txn_Time;
    }

    public void setTxn_Time(String txn_Time) {
        Txn_Time = txn_Time;
    }

    public String getTxn_Date() {
        return Txn_Date;
    }

    public void setTxn_Date(String txn_Date) {
        Txn_Date = txn_Date;
    }

    public String getBatch_number() {
        return Batch_number;
    }

    public void setBatch_number(String batch_number) {
        Batch_number = batch_number;
    }

    public String getMifare_ID() {
        return Mifare_ID;
    }

    public void setMifare_ID(String mifare_ID) {
        Mifare_ID = mifare_ID;
    }

    public String getPurse_ID() {
        return purse_ID;
    }

    public void setPurse_ID(String purse_ID) {
        this.purse_ID = purse_ID;
    }

    public String getCard_type() {
        return Card_type;
    }

    public void setCard_type(String card_type) {
        Card_type = card_type;
    }

    public String getPersonal_profile() {
        return Personal_profile;
    }

    public void setPersonal_profile(String personal_profile) {
        Personal_profile = personal_profile;
    }

    public String getReceipt_card_number() {
        return receipt_card_number;
    }

    public void setReceipt_card_number(String receipt_card_number) {
        this.receipt_card_number = receipt_card_number;
    }

    public String getCard_exp() {
        return card_exp;
    }

    public void setCard_exp(String card_exp) {
        this.card_exp = card_exp;
    }

    public String getRRN() {
        return RRN;
    }

    public void setRRN(String RRN) {
        this.RRN = RRN;
    }

    public String getEDC_ID() {
        return EDC_ID;
    }

    public void setEDC_ID(String EDC_ID) {
        this.EDC_ID = EDC_ID;
    }

    public String getDevice_ID() {
        return Device_ID;
    }

    public void setDevice_ID(String device_ID) {
        Device_ID = device_ID;
    }

    public String getNew_deviceID() {
        return New_deviceID;
    }

    public void setNew_deviceID(String new_deviceID) {
        New_deviceID = new_deviceID;
    }

    public String getShop_code() {
        return shop_code;
    }

    public void setShop_code(String shop_code) {
        this.shop_code = shop_code;
    }

    public String getCPU_purse_version_number() {
        return CPU_purse_version_number;
    }

    public void setCPU_purse_version_number(String CPU_purse_version_number) {
        this.CPU_purse_version_number = CPU_purse_version_number;
    }

    public String getBank_code() {
        return Bank_code;
    }

    public void setBank_code(String bank_code) {
        Bank_code = bank_code;
    }

    public String getArea_code() {
        return Area_code;
    }

    public void setArea_code(String area_code) {
        Area_code = area_code;
    }

    public String getCPU_sub_area_code() {
        return CPU_sub_area_code;
    }

    public void setCPU_sub_area_code(String CPU_sub_area_code) {
        this.CPU_sub_area_code = CPU_sub_area_code;
    }

    public String getTXN_proof_code() {
        return TXN_proof_code;
    }

    public void setTXN_proof_code(String TXN_proof_code) {
        this.TXN_proof_code = TXN_proof_code;
    }

    public String getN_CPU_EV_Before_TXN() {
        return n_CPU_EV_Before_TXN;
    }

    public void setN_CPU_EV_Before_TXN(String n_CPU_EV_Before_TXN) {
        int len = n_CPU_EV_Before_TXN.length();
        this.n_CPU_EV_Before_TXN = n_CPU_EV_Before_TXN.substring(0, len - 2);
    }

    public String getN_CPU_Txn_AMT() {
        return n_CPU_Txn_AMT;
    }

    public void setN_CPU_Txn_AMT(String n_CPU_Txn_AMT) {
        int len = n_CPU_Txn_AMT.length();
        this.n_CPU_Txn_AMT = n_CPU_Txn_AMT.substring(0, len - 2);
    }

    public String getN_CPU_EV() {
        return n_CPU_EV;
    }

    public void setN_CPU_EV(String n_CPU_EV) {
        int len = n_CPU_EV.length();
        this.n_CPU_EV = n_CPU_EV.substring(0, len - 2);
    }

    public String getN_AutoLoad_Amount() {
        return n_AutoLoad_Amount;
    }

    public void setN_AutoLoad_Amount(String n_AutoLoad_Amount) {
        if (n_AutoLoad_Amount != null) {
            int len = n_AutoLoad_Amount.length();
            if (len > 2) {
                this.n_AutoLoad_Amount = n_AutoLoad_Amount.substring(0, len - 2);
            } else {
                this.n_AutoLoad_Amount = n_AutoLoad_Amount;
            }
        } else {
            this.n_AutoLoad_Amount = "0";
        }
    }

    public String getIsUpload() {
        return IsUpload;
    }

    public void setIsUpload(String isUpload) {
        IsUpload = isUpload;
    }

    public String getTransfer_status() {
        return transfer_status;
    }

    public void setTransfer_status(String transfer_status) {
        this.transfer_status = transfer_status;
    }

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }
}
