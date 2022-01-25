package com.lafresh.kiosk.httprequest;

import com.lafresh.kiosk.Config;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;

public class GetProductApiRequest extends ApiRequest {
    private String depId;

    public GetProductApiRequest(String serno) {
        this.depId = serno;
    }

    @Override
    public HttpUrl getUrl() {
        return HttpUrl.parse(PRODUCT_SERVICE_URL);
    }

    @Override
    public RequestBody getRequestBody() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("op", "get_product")
                .add("authkey", Config.authKey)
                .add("acckey", Config.acckey)
                .add("shop_id", Config.shop_id)
                .add("sale_method", Config.saleType)
                .add("dep_id", depId);
        RequestBody requestBody = builder.build();
        return requestBody;
    }
}
