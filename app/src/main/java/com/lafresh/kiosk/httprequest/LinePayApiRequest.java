package com.lafresh.kiosk.httprequest;

import android.content.Context;

import com.lafresh.kiosk.Bill;
import com.lafresh.kiosk.Config;
import com.lafresh.kiosk.R;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;

public class LinePayApiRequest extends ApiRequest {
    private Context context;
    private String oneTimeKey;

    public LinePayApiRequest(Context context, String oneTimeKey) {
        this.context = context;
        this.oneTimeKey = oneTimeKey;
    }

    @Override
    public HttpUrl getUrl() {
        return HttpUrl.parse(Config.linePayUrl);
    }

    @Override
    public RequestBody getRequestBody() {
        String amount = String.valueOf(Bill.fromServer.total);
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("amount", amount)
                .add("orderId", Bill.fromServer.worder_id)
                .add("currency", "TWD")
                .add("oneTimeKey", oneTimeKey)
                .add("authkey", Config.authKey);
        RequestBody requestBody = builder.build();
        return requestBody;
    }
}
