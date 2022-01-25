package com.lafresh.kiosk.httprequest;

import com.lafresh.kiosk.easycard.model.EcPayData;
import com.lafresh.kiosk.utils.Json;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;

public class AddEzCardPayApiRequest extends ApiRequest {
    private String authkey;
    private String acckey;
    private EcPayData ecPayData;


    public AddEzCardPayApiRequest(String authkey, String acckey, EcPayData ecPayData) {
        this.authkey = authkey;
        this.acckey = acckey;
        this.ecPayData = ecPayData;
    }

    @Override
    public HttpUrl getUrl() {
        return HttpUrl.parse(ORDER_SERVER_URL);
    }

    @Override
    public RequestBody getRequestBody() {
        String jsonString = Json.toJson(ecPayData);
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("op", "set_EC_STMC3")
                .add("authkey", authkey)
                .add("acckey", acckey)
                .add("EC_STMC3", "[" + jsonString + "]");
        RequestBody requestBody = builder.build();
        return requestBody;
    }
}
