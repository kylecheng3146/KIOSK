package com.lafresh.kiosk.lanxin;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.lafresh.kiosk.lanxin.model.EzAIOData;

public class EzAIOPay {

    private EzAIOData ezAIOData;
    private Activity activity;
    private ComponentName componentName;
    private Intent intent;

    public EzAIOPay(Activity activity){
        this.activity = activity;
        init();
    }

    private void init(){
        intent = new Intent();
        componentName = new ComponentName("com.ezpay.ezaiopayment", "com.ezpay.ezaiopayment.custom.mainpage.view.MainPageActivity");

        intent.setComponent(componentName);

        ezAIOData = new EzAIOData();
    }

    public EzAIOData getEzAIOData() {
        return ezAIOData;
    }

    public void setEzAIOData(EzAIOData ezAIOData) {
        this.ezAIOData = ezAIOData;
    }

    public void runEzAIOPay(){
        Bundle bundle = new Bundle();
        bundle.putString("tradeInfo", new Gson().toJson(ezAIOData));
        intent.putExtra("EZAIO", bundle);

        this.activity.startActivity(intent);
    }
}
