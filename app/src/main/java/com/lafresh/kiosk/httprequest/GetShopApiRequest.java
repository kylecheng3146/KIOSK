package com.lafresh.kiosk.httprequest;

import com.lafresh.kiosk.Config;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;

/**
 * Created by Kevin on 2020/8/20.
 */

public class GetShopApiRequest extends ApiRequest {
    private String shopId;

    public GetShopApiRequest(String shopId) {
        this.shopId = shopId;
    }

    @Override
    public HttpUrl getUrl() {
        return HttpUrl.parse(BASIC_SERVER_URL);
    }

    @Override
    public RequestBody getRequestBody() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("op", "get_shop")
                .add("authkey", Config.authKey);

        if (shopId != null) {
            builder.add("shop_id", Config.shop_id);
        }

        RequestBody requestBody = builder.build();
        return requestBody;
    }
}
