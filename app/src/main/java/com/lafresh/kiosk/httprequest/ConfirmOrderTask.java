package com.lafresh.kiosk.httprequest;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.HttpUrl;
import okhttp3.RequestBody;

public class ConfirmOrderTask extends ApiRequest {
    private boolean runningAddOrder = true;
    private AddOrderApiRequest addOrderApiRequest;
    private GetOrderInfoApiRequest getOrderInfoApiRequest;
    private int switchTimes = 2;//避免無限迴圈

    public ConfirmOrderTask(AddOrderApiRequest addOrderApiRequest) {
        this.addOrderApiRequest = addOrderApiRequest;
    }

    @Override
    public HttpUrl getUrl() {
        if (runningAddOrder) {
            return addOrderApiRequest.getUrl();
        } else {
            return getOrderInfoApiRequest.getUrl();
        }
    }

    @Override
    public RequestBody getRequestBody() {
        if (runningAddOrder) {
            setApiListener(addOrderApiRequest.getApiListener());
            return addOrderApiRequest.getRequestBody();
        } else {
            setApiListener(getOrderInfoApiRequest.getApiListener());
            return getOrderInfoApiRequest.getRequestBody();
        }
    }

    @Override
    protected boolean needRetry(JSONObject jsonObject) throws JSONException {
        if (runningAddOrder) {
            return addOrderApiRequest.needRetry(jsonObject);
        } else {
            return super.needRetry(jsonObject);
        }
    }

    public void switchToGetOrderInfo(GetOrderInfoApiRequest apiRequest) {
        if (apiRequest != null) {
            this.getOrderInfoApiRequest = apiRequest;
        }
        switchTimes--;
        runningAddOrder = false;
        Log.wtf(getClass().getSimpleName(), "switch to getOrderInfo");
        httpRequest(buildClient(), buildRequest(method()));
    }

    public void switchToAddOrder() {
        runningAddOrder = true;
        Log.wtf(getClass().getSimpleName(), "switch to add_order");
        httpRequest(buildClient(), buildRequest(method()));
    }

    public int getSwitchTimes() {
        return switchTimes;
    }

    public void retryFromGetOrderInfo(GetOrderInfoApiRequest apiRequest) {
        this.getOrderInfoApiRequest = apiRequest;
        runningAddOrder = false;
        Log.wtf(getClass().getSimpleName(), "Retry From GetOrderInfo");
        go();
    }
}
