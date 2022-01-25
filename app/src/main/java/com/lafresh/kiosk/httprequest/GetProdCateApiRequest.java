package com.lafresh.kiosk.httprequest;

import com.lafresh.kiosk.Config;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;

public class GetProdCateApiRequest extends ApiRequest {

    @Override
    public HttpUrl getUrl() {
        return HttpUrl.parse(PRODUCT_SERVICE_URL);
    }

    @Override
    public RequestBody getRequestBody() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("op", "get_prodcate00")
                .add("authkey", Config.authKey)
                .add("shop_id", Config.shop_id);
        RequestBody requestBody = builder.build();
        return requestBody;
    }
}
