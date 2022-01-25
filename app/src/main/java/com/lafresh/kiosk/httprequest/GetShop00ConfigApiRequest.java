package com.lafresh.kiosk.httprequest;

import com.lafresh.kiosk.Config;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;

public class GetShop00ConfigApiRequest extends ApiRequest {
    private String shopId;

    public GetShop00ConfigApiRequest(String shopId) {
        this.shopId = shopId;
    }

    @Override
    public HttpUrl getUrl() {
        if (shopId != null) {
            return HttpUrl.parse(ORDER_SERVER_URL);
        }

        return HttpUrl.parse(BASIC_SERVER_URL);
    }

    @Override
    public RequestBody getRequestBody() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("op", "get_shop00_config")
                .add("authkey", Config.authKey);

        if (shopId != null) {
            builder.add("shop_id", Config.shop_id);
        }

        RequestBody requestBody = builder.build();
        return requestBody;
    }
}
