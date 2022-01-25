package com.lafresh.kiosk.httprequest;


import com.lafresh.kiosk.Config;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;

public class GetBatchNumberApiRequest extends ApiRequest {
    private String token;
    private String shopId;
    private String kioskId;

    public GetBatchNumberApiRequest(String token, String shopId, String kioskId) {
        this.token = token;
        this.shopId = shopId;
        this.kioskId = kioskId;
    }


    @Override
    public HttpUrl getUrl() {
        return HttpUrl.parse(Config.cacheUrl + "/v1/getbatchnumber").newBuilder()
                .addQueryParameter("store_id", shopId)
                .addQueryParameter("pos_id", kioskId)
                .build();
    }

    @Override
    public RequestBody getRequestBody() {
        return null;
    }

    @Override
    protected HttpMethod method() {
        return HttpMethod.GET;
    }

    @Override
    protected void addHeader(Request.Builder builder) {
        builder.addHeader("Authorization", token);
    }
}
