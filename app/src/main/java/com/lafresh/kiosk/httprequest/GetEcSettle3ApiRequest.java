package com.lafresh.kiosk.httprequest;

import com.lafresh.kiosk.Config;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;

public class GetEcSettle3ApiRequest extends ApiRequest {
    String batchNumber;

    public GetEcSettle3ApiRequest(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    @Override
    public HttpUrl getUrl() {
        return HttpUrl.parse(ORDER_SERVER_URL);
    }

    @Override
    public RequestBody getRequestBody() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("op", "get_EC_SETTLE3")
                .add("authkey", Config.authKey)
                .add("shop_id", Config.shop_id)
                .add("pos_id", Config.kiosk_id)
                .add("Batch_number", batchNumber);
        RequestBody requestBody = builder.build();
        return requestBody;
    }
}
