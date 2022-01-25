package com.lafresh.kiosk.httprequest;

import android.util.Log;

import com.lafresh.kiosk.Config;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;

public class GetSaleMethodApiRequest extends ApiRequest {
    @Override
    public HttpUrl getUrl() {
        return HttpUrl.parse(ORDER_SERVER_URL);
    }

    @Override
    public RequestBody getRequestBody() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("op", "get_sale_method")
                .add("authkey", Config.authKey)
                .add("shop_id", Config.shop_id)
                .add("storefrom", "KIOSK");
        RequestBody requestBody = builder.build();
        Log.i(getClass().getName(), getRequestContent((FormBody) requestBody));
        return requestBody;
    }
}
