package com.lafresh.kiosk.httprequest;

import com.lafresh.kiosk.Bill;
import com.lafresh.kiosk.BuildConfig;
import com.lafresh.kiosk.Config;
import com.lafresh.kiosk.model.AddOrderBean;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;

public class AddOrderApiRequest extends ApiRequest {
    private String worder_id;
    private String payment_term;
    private String status;
    private String meal_date;
    private String weborder01;
    private String weborder011;
    private String weborder02;
    private String weborder021;
    private String ticket;

    private String cust_code;
    private String buyer_number;
    private String npoban;
    private String table_no;

    public AddOrderApiRequest(String worder_id, String payment_term, String status,
                              String meal_date, String weborder01, String weborder011,
                              String weborder02, String weborder021, String ticket) {
        this.worder_id = worder_id;
        this.payment_term = payment_term;
        this.status = status;
        this.meal_date = meal_date;
        this.weborder01 = weborder01;
        this.weborder011 = weborder011;
        this.weborder02 = weborder02;
        this.weborder021 = weborder021;
        this.ticket = ticket;
    }

    public AddOrderApiRequest(AddOrderBean bean,String mealDate) {
        this.worder_id = bean.getwOrderId();
        this.payment_term = bean.getPaymentTerm();
        this.status = bean.getStatus();
        this.meal_date = BuildConfig.FLAVOR.equals("TFG") ? mealDate : bean.getMealDate();
        this.weborder01 = bean.getWebOrder01();
        this.weborder011 = bean.getWebOrder011();
        this.weborder02 = bean.getWebOrder02();
        this.weborder021 = bean.getWebOrder021();
        this.ticket = bean.getTicket();
    }

    @Override
    public HttpUrl getUrl() {
        return HttpUrl.parse(ORDER_SERVER_URL);
    }

    @Override
    public RequestBody getRequestBody() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("op", "add_order")
                .add("authkey", Config.authKey)
                .add("acckey", Config.acckey)
                .add("shop_id", Config.shop_id)
                .add("kiosk_id", Config.kiosk_id)
                .add("order_source", "1")
                .add("source_type", "2")
                .add("sale_method", Config.saleType)
                .add("storefrom", BuildConfig.FLAVOR.equals("TFG") ? "APP" : "KIOSK")
                .add("meal_date", meal_date)
                .add("payment_terms", payment_term)
                .add("status", status)
                .add("worder_id", worder_id)
                .add("weborder01", weborder01)
                .add("weborder011", weborder011)
                .add("weborder02", weborder02)
                .add("weborder021", weborder021)
                .add("ticket", ticket);

        if (cust_code != null) {
            builder.add("cust_code", cust_code);
        }

        if (buyer_number != null) {
            builder.add("buyer_type", "3J0002")
                    .add("buyer_number", buyer_number);
        }

        if (npoban != null) {
            builder.add("npoban", npoban);
        }

        if (table_no != null) {
            builder.add("table_no", table_no);
        }

        RequestBody requestBody = builder.build();
        return requestBody;
    }

    public void setCust_code(String cust_code) {
        this.cust_code = cust_code;
    }

    public void setBuyer_number(String buyer_number) {
        this.buyer_number = buyer_number;
    }

    public void setNpoban(String npoban) {
        this.npoban = npoban;
    }

    public void setTable_no(String table_no) {
        this.table_no = table_no;
    }

    @Override
    protected boolean needRetry(JSONObject jsonObject) throws JSONException {
        //項目不符合錯誤，重試三次
        boolean isRetryCode = jsonObject.has("code") && jsonObject.getInt("code") == 30;
        return isRetryCode && needRetry();
    }
}
