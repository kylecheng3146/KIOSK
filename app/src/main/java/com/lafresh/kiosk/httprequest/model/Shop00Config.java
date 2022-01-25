package com.lafresh.kiosk.httprequest.model;


public class Shop00Config {
    public static final int TABLE_NO_DISABLE = 0;
    public static final int TABLE_NO_SCAN = 1;
    public static final int TABLE_NO_INPUT = 2;

    /**
     * code : 0
     * message : 門市/分店購物參數資料
     * meal_days : 7
     * meal_btime : 09:00
     * meal_etime : 21:00
     * meal_gap : 15
     * gap_amt_limit : 0
     * order_amt_limit : 0
     * pretime : 0
     * ship_range : 0
     * ship_amount : null
     * kiosk_amt_limit : null
     */

    private int code = -999;
    private String message;
    private String meal_days;
    private String meal_btime;
    private String meal_etime;
    private String meal_gap;
    private String gap_amt_limit;
    private String pretime;
    private String ship_range;
    private String ship_amount;
    /**
     * datacnt : 1
     * order_amt_limit : 0
     * order_timeout : 0
     * order_online : 0
     * order_qrcode : 0
     * kiosk_amt_limit : 0
     * enable_line_pay : 0
     * enable_nccc_pay : 0
     * enable_counter_pay : 0
     * enable_easy_card_pay : 0
     * enable_table_no : 0
     * merchant_id : null
     * terminal_id : null
     * mac_key : null
     * channel_id : null
     * channel_secret : null
     */

    private int datacnt;
    private String order_amt_limit;//Basic路徑＝公司設定，Order路徑＝單店設定 單筆金額限制
    private String order_timeout;
    private String order_online;
    private String order_qrcode;
    private String kiosk_amt_limit;//KIOSK單筆金額限制
    private String enable_line_pay;
    private String enable_nccc_pay;
    private String enable_counter_pay;
    private String enable_easy_card_pay;
    private String enable_table_no;//0 不啟用, 1 啟用掃描, 2 啟用鍵盤輸入
    private String enable_pi_pay_kiosk;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMeal_days() {
        return meal_days;
    }

    public void setMeal_days(String meal_days) {
        this.meal_days = meal_days;
    }

    public String getMeal_btime() {
        return meal_btime;
    }

    public void setMeal_btime(String meal_btime) {
        this.meal_btime = meal_btime;
    }

    public String getMeal_etime() {
        return meal_etime;
    }

    public void setMeal_etime(String meal_etime) {
        this.meal_etime = meal_etime;
    }

    public String getMeal_gap() {
        return meal_gap;
    }

    public void setMeal_gap(String meal_gap) {
        this.meal_gap = meal_gap;
    }

    public String getGap_amt_limit() {
        return gap_amt_limit;
    }

    public void setGap_amt_limit(String gap_amt_limit) {
        this.gap_amt_limit = gap_amt_limit;
    }

    public String getOrder_amt_limit() {
        return order_amt_limit;
    }

    public void setOrder_amt_limit(String order_amt_limit) {
        this.order_amt_limit = order_amt_limit;
    }

    public String getPretime() {
        return pretime;
    }

    public void setPretime(String pretime) {
        this.pretime = pretime;
    }

    public String getShip_range() {
        return ship_range;
    }

    public void setShip_range(String ship_range) {
        this.ship_range = ship_range;
    }

    public String getShip_amount() {
        return ship_amount;
    }

    public void setShip_amount(String ship_amount) {
        this.ship_amount = ship_amount;
    }

    public String getKiosk_amt_limit() {
        return kiosk_amt_limit;
    }

    public void setKiosk_amt_limit(String kiosk_amt_limit) {
        this.kiosk_amt_limit = kiosk_amt_limit;
    }

    public int getDatacnt() {
        return datacnt;
    }

    public void setDatacnt(int datacnt) {
        this.datacnt = datacnt;
    }


    public String getOrder_timeout() {
        return order_timeout;
    }

    public void setOrder_timeout(String order_timeout) {
        this.order_timeout = order_timeout;
    }

    public String getOrder_online() {
        return order_online;
    }

    public void setOrder_online(String order_online) {
        this.order_online = order_online;
    }

    public String getOrder_qrcode() {
        return order_qrcode;
    }

    public void setOrder_qrcode(String order_qrcode) {
        this.order_qrcode = order_qrcode;
    }

    public String getEnable_line_pay() {
        return enable_line_pay;
    }

    public void setEnable_line_pay(String enable_line_pay) {
        this.enable_line_pay = enable_line_pay;
    }

    public String getEnable_nccc_pay() {
        return enable_nccc_pay;
    }

    public void setEnable_nccc_pay(String enable_nccc_pay) {
        this.enable_nccc_pay = enable_nccc_pay;
    }

    public String getEnable_counter_pay() {
        return enable_counter_pay;
    }

    public void setEnable_counter_pay(String enable_counter_pay) {
        this.enable_counter_pay = enable_counter_pay;
    }

    public String getEnable_easy_card_pay() {
        return enable_easy_card_pay;
    }

    public void setEnable_easy_card_pay(String enable_easy_card_pay) {
        this.enable_easy_card_pay = enable_easy_card_pay;
    }

    public String getEnable_table_no() {
        return enable_table_no;
    }

    public void setEnable_table_no(String enable_table_no) {
        this.enable_table_no = enable_table_no;
    }

    public String getEnable_pi_pay_kiosk() {
        return enable_pi_pay_kiosk;
    }

    public void setEnable_pi_pay_kiosk(String enable_pi_pay_kiosk) {
        this.enable_pi_pay_kiosk = enable_pi_pay_kiosk;
    }
}
