package com.lafresh.kiosk.httprequest;

import com.lafresh.kiosk.Config;
import com.lafresh.kiosk.easycard.model.EcCheckoutData;
import com.lafresh.kiosk.utils.Json;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;

public class SetEcSettle3ApiRequest extends ApiRequest {
    EcCheckoutData ecCheckoutData;

    public SetEcSettle3ApiRequest(EcCheckoutData ecCheckoutData) {
        this.ecCheckoutData = ecCheckoutData;
    }

    @Override
    public HttpUrl getUrl() {
        return HttpUrl.parse(ORDER_SERVER_URL);
    }

    @Override
    public RequestBody getRequestBody() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("op", "set_EC_SETTLE3")
                .add("authkey", Config.authKey)
                .add("EC_SETTLE3", Json.toJson(ecCheckoutData));
        RequestBody requestBody = builder.build();
        return requestBody;
    }
}
