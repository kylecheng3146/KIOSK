package com.lafresh.kiosk.httprequest;

import android.util.Log;

import com.lafresh.kiosk.Config;
import com.lafresh.kiosk.model.EasyPay;
import com.lafresh.kiosk.utils.Json;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class EasyPayApiRequest extends ApiRequest {
    private EasyPay easyPay;

    public EasyPayApiRequest(EasyPay easyPay) {
        this.easyPay = easyPay;
    }

    @Override
    public HttpUrl getUrl() {
        return HttpUrl.parse(String.format("%s/payment/easy/pay", Config.cacheUrl));
    }

    @Override
    public RequestBody getRequestBody() {
        String jsonString = Json.toJson(easyPay);
        Log.i("@@@", jsonString);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        return RequestBody.create(JSON, jsonString);
    }

    @Override
    public HttpMethod method() {
        return HttpMethod.POST;
    }
}
