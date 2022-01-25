package com.lafresh.kiosk.httprequest;

import com.lafresh.kiosk.Config;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;

public class GetKioskImgApiRequest extends ApiRequest {

    @Override
    public HttpUrl getUrl() {
        return HttpUrl.parse(BASIC_SERVER_URL);
    }

    @Override
    public RequestBody getRequestBody() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("op", "get_kiosk_img")
                .add("authkey", Config.authKey);
        RequestBody requestBody = builder.build();
        return requestBody;
    }
}
