package com.lafresh.kiosk.httprequest;

import okhttp3.HttpUrl;
import okhttp3.RequestBody;

public class AskOrderApiRequest extends ApiRequest {
    private String reqString;

    public AskOrderApiRequest(String reqString) {
        this.reqString = reqString;
    }

    @Override
    public HttpUrl getUrl() {
        return HttpUrl.parse(PI_PAY_URL + "ask.php");
    }

    @Override
    public RequestBody getRequestBody() {
        return RequestBody.create(JSON_CONTENT_TYPE, reqString);
    }
}
