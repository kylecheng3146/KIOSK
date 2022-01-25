package com.lafresh.kiosk.model;

//還原json用，不分大小寫
public class ConfigFileData {

    /**
     * ApiRoot : http://kiosk.lafresh.com.tw:8080/kiosk/app
     * cacheUrl : http://13.70.19.141:80
     * linePayUrl : http://kiosk.lafresh.com.tw:8080/kiosk/app/webservice/line/pay.php
     * authKey : v32lgfy6hdq7y69pr95a62new6zsz3bc
     * accKey : 67pcbag9awwfdxe19gdj1rgfb2xvny9c
     * shop_id : 000030
     * acckeyDefault : 67pcbag9awwfdxe19gdj1rgfb2xvny9c
     * kiosk_id : k1
     * creditCardComportPath : /dev/ttyS3
     * creditCardBaudRate : 9600
     * useNcccByShop : false
     * useNcccByKiosk : true
     * usePiPay : false
     * enableCounter : false
     */

    private String ApiRoot;
    private String cacheUrl;
    private String linePayUrl;
    private String authKey;
    private String accKey;
    private String shop_id;
    private String acckeyDefault;
    private String kiosk_id;
    private String creditCardComportPath;
    private String addressDefault;
    private int creditCardBaudRate;
    private boolean useEasyCardByKiosk = true;
    private boolean useNcccByShop;
    private boolean useNcccByKiosk;
    private boolean disableReceiptModule;
    private boolean disablePrinter;
    private boolean usePiPay;
    private boolean enableCounter;
    private boolean isOnlyCounterPay;
    private boolean isNewOrderApi;
    private boolean isRestaurant;
    private boolean isShopping;
    private boolean isEnabledCashPayment;

    public boolean isRestaurant() {
        return isRestaurant;
    }

    public void setRestaurant(boolean restaurant) {
        isRestaurant = restaurant;
    }

    public boolean isShopping() {
        return isShopping;
    }

    public void setShopping(boolean shopping) {
        isShopping = shopping;
    }


    public boolean isEnabledCashPayment() {
        return isEnabledCashPayment;
    }

    public void setEnabledCash(boolean isEnabledCashPayment) {
        isEnabledCashPayment = isEnabledCashPayment;
    }

    public boolean isUseMultiBrand() {
        return useMultiBrand;
    }

    public void setUseMultiBrand(boolean useMultiBrand) {
        this.useMultiBrand = useMultiBrand;
    }

    private boolean useMultiBrand;

    public String getApiRoot() {
        return ApiRoot;
    }

    public void setApiRoot(String ApiRoot) {
        this.ApiRoot = ApiRoot;
    }

    public String getCacheUrl() {
        return cacheUrl;
    }

    public void setCacheUrl(String cacheUrl) {
        this.cacheUrl = cacheUrl;
    }

    public String getLinePayUrl() {
        return linePayUrl;
    }

    public void setLinePayUrl(String LinePayUrl) {
        this.linePayUrl = LinePayUrl;
    }

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

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getAcckeyDefault() {
        return acckeyDefault;
    }

    public void setAccKeyDefault(String acckeyDefault) {
        this.acckeyDefault = acckeyDefault;
    }

    public String getKiosk_id() {
        return kiosk_id;
    }

    public void setKiosk_id(String kiosk_id) {
        this.kiosk_id = kiosk_id;
    }

    public String getCreditCardComportPath() {
        return creditCardComportPath;
    }

    public String getAddressDefault() {
        return addressDefault;
    }

    public void setAddressDefault(String addressDefault) {
        this.addressDefault = addressDefault;
    }


    public void setCreditCardComportPath(String creditCardComportPath) {
        this.creditCardComportPath = creditCardComportPath;
    }

    public int getCreditCardBaudRate() {
        return creditCardBaudRate;
    }

    public void setCreditCardBaudRate(int creditCardBaudRate) {
        this.creditCardBaudRate = creditCardBaudRate;
    }

    public boolean isUseNcccByShop() {
        return useNcccByShop;
    }

    public void setUseNcccByShop(boolean useNcccByShop) {
        this.useNcccByShop = useNcccByShop;
    }

    public boolean isUseNcccByKiosk() {
        return useNcccByKiosk;
    }

    public void setUseNcccByKiosk(boolean useNcccByKiosk) {
        this.useNcccByKiosk = useNcccByKiosk;
    }

    public boolean getDisableReceiptModule() {
        return disableReceiptModule;
    }

    public void setDisableReceiptModule(boolean disableReceiptModule) {
        this.disableReceiptModule = disableReceiptModule;
    }

    public boolean isUsePiPay() {
        return usePiPay;
    }

    public void setUsePiPay(boolean usePiPay) {
        this.usePiPay = usePiPay;
    }

    public boolean isEnableCounter() {
        return enableCounter;
    }

    public void setEnableCounter(boolean enableCounter) {
        this.enableCounter = enableCounter;
    }

    public boolean isOnlyCounterPay() {
        return isOnlyCounterPay;
    }

    public void setOnlyCounterPay(boolean onlyCounterPay) {
        isOnlyCounterPay = onlyCounterPay;
    }

    public boolean isUseEasyCardByKiosk() {
        return useEasyCardByKiosk;
    }

    public void setUseEasyCardByKiosk(boolean useEasyCardByKiosk) {
        this.useEasyCardByKiosk = useEasyCardByKiosk;
    }

    public boolean isDisablePrinter() {
        return disablePrinter;
    }

    public void setDisablePrinter(boolean disablePrinter) {
        this.disablePrinter = disablePrinter;
    }

    public boolean isNewOrderApi() {
        return isNewOrderApi;
    }

    public void setNewOrderApi(boolean newOrderApi) {
        isNewOrderApi = newOrderApi;
    }
}
