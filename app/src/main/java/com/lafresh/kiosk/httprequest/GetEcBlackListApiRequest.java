package com.lafresh.kiosk.httprequest;

import android.content.Context;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.lafresh.kiosk.Config;
import com.lafresh.kiosk.R;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class GetEcBlackListApiRequest extends ApiRequest {
    private ApiRequest.ApiListener listener;
    private String filePath;
    private String blcFilePath;
    private String blcZipName;

    public GetEcBlackListApiRequest(Context context, ApiListener listener) {
        this.listener = listener;
        filePath = context.getFilesDir().toString();
        blcFilePath = context.getResources().getString(R.string.ecBlackListFilePath);
        blcZipName = context.getResources().getString(R.string.ecBlackListZipFileName);
    }

    @Override
    public HttpUrl getUrl() {
        return HttpUrl.parse(ORDER_SERVER_URL);
    }

    @Override
    public RequestBody getRequestBody() {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("op", "GetEcBlackList")
                .add("authkey", Config.authKey);
        RequestBody requestBody = builder.build();
        return requestBody;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(15, TimeUnit.SECONDS);
        builder.readTimeout(60, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();

        Request request = new Request.Builder()
                .url(getUrl())
                .post(getRequestBody())
                .build();
        Log.i(getClass().getSimpleName(), request.toString());
        long useTime = 0;
        long start = System.currentTimeMillis();
        try {
            Response response = client.newCall(request).execute();
                if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            } else {
                File file = new File(filePath + blcFilePath + blcZipName);

                BufferedSource source = response.body().source();
                try (BufferedSink sink = Okio.buffer(Okio.sink(file))) {
                    sink.writeAll(source);
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                }

                Log.i(getClass().getSimpleName(), "BlackListFilePath = " + filePath + blcFilePath + blcZipName);
                listener.onSuccess(this, "");
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (request.body() instanceof FormBody) {
                FormBody formBody = (FormBody) request.body();
                FirebaseCrashlytics.getInstance().log(getClass().getSimpleName()+":"+ "Request =>" + getRequestContent(formBody));
            }
            long finish = System.currentTimeMillis();
            useTime = finish - start;
            FirebaseCrashlytics.getInstance().log(getClass().getSimpleName()+":"+ "耗時 =>" + useTime + "毫秒");
            FirebaseCrashlytics.getInstance().recordException(e);
            listener.onFail();
        }
        return null;
    }
}
