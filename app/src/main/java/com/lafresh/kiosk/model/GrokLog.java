package com.lafresh.kiosk.model;

public class GrokLog {
    private String logDatetime;
    private String ip = "000.000.000.000";
    private String logLevel = "info";//info、warning、error
    private int errorLevel = 0;//Error 1~5種等級
    private String deviceId;
    private String serviceVersion;//各系統或服務的版本號
    private String type = "API";  /*訂單（Order）
                                    商品
                                    票卷
                                    */
    private String companyId;//平台使用的公司編號或公司資訊
    private String shopId;
    private String resourceId = "";/*訂單： worder_id
                                     商品：商品 ID
                                     票卷：票卷號碼
                                     */

    private String msg = "";//供非RD追蹤問題之訊息
    private String msgData = "";//供FAE追蹤問題之訊息
    private String msgDetail = "";//供RD追蹤問題之訊息

    public void setLogDatetime(String logDatetime) {
        this.logDatetime = logDatetime;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public void setErrorLevel(int errorLevel) {
        this.errorLevel = errorLevel;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setMsgData(String msgData) {
        this.msgData = msgData;
    }

    public void setMsgDetail(String msgDetail) {
        this.msgDetail = msgDetail;
    }

    /**
     * 2020-08-24 18:16:40.416|--|219.84.34.82|--|info|--|error_level|--|
     * service_id|--|device_id|--|service_version|--|type|--|company_id|--|
     * shop_id|--|resourceId|--|msg|--|msgData|--|msgDetail
     */
    @Override
    public String toString() {
        String SEPARATOR = "|--|";
        return logDatetime +
                SEPARATOR + ip +
                SEPARATOR + logLevel +
                SEPARATOR + errorLevel +
                SEPARATOR + "KIOSK" +
                SEPARATOR + deviceId +
                SEPARATOR + serviceVersion +
                SEPARATOR + type +
                SEPARATOR + companyId +
                SEPARATOR + shopId +
                SEPARATOR + resourceId +
                SEPARATOR + msg +
                SEPARATOR + msgData +
                SEPARATOR + msgDetail;
    }
}
