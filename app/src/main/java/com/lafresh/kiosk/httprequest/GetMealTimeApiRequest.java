package com.lafresh.kiosk.httprequest;

import com.lafresh.kiosk.Config;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;

/**
 * Created by Kevin on 2020/8/14.
 */

public class GetMealTimeApiRequest extends ApiRequest {
    private String date;

    public GetMealTimeApiRequest(String date) {
        this.date = date;
    }

    @Override
    public HttpUrl getUrl() {
        return HttpUrl.parse(ORDER_SERVER_URL);
    }

    @Override
    public RequestBody getRequestBody() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("op", "get_meal_time")
                .add("authkey", Config.authKey)
                .add("shop_id", Config.shop_id)
                .add("date", this.date)
                .add("v","1");
        return builder.build();
    }
}
