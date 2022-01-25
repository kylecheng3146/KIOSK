package com.lafresh.kiosk.httprequest;

import okhttp3.HttpUrl;
import okhttp3.RequestBody;

public class PayOrderApiRequest extends ApiRequest {
    private String reqJson;

    public PayOrderApiRequest(String reqJson) {
        this.reqJson = reqJson;
    }

    @Override
    public HttpUrl getUrl() {
        return HttpUrl.parse(PI_PAY_URL + "pay_kiosk.php");
    }

    @Override
    public RequestBody getRequestBody() {
        return RequestBody.create(JSON_CONTENT_TYPE, reqJson);
    }
}
