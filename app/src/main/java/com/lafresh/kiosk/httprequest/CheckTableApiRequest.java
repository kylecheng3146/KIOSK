package com.lafresh.kiosk.httprequest;

import com.lafresh.kiosk.Config;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;

public class CheckTableApiRequest extends ApiRequest {
    private String table_no;

    public CheckTableApiRequest(String table_no) {
        this.table_no = table_no;
    }

    @Override
    public HttpUrl getUrl() {
        return HttpUrl.parse(BASIC_SERVER_URL);
    }

    @Override
    public RequestBody getRequestBody() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("op", "check_table")
                .add("authkey", Config.authKey)
                .add("shop_id", Config.shop_id)
                .add("table_no", table_no);
        RequestBody requestBody = builder.build();
        return requestBody;
    }
}
