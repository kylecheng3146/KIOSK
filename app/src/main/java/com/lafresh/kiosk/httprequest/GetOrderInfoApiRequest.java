package com.lafresh.kiosk.httprequest;

import com.lafresh.kiosk.Config;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;

public class GetOrderInfoApiRequest extends ApiRequest {
    private String worder_id;

    public GetOrderInfoApiRequest(String worder_id) {
        this.worder_id = worder_id;
    }

    @Override
    public HttpUrl getUrl() {
        return HttpUrl.parse(ORDER_SERVER_URL);
    }

    @Override
    public RequestBody getRequestBody() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("op", "get_order_info")
                .add("authkey", Config.authKey)
                .add("acckey", Config.acckey)
                .add("worder_id", worder_id);
        RequestBody requestBody = builder.build();
        return requestBody;
    }
}
