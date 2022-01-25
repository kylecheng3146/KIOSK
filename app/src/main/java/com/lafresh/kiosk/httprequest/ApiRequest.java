package com.lafresh.kiosk.httprequest;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.JsonObject;
import com.lafresh.kiosk.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;


public abstract class ApiRequest extends AsyncTask<Void, Void, Void> {

    public static String MEMBER_SERVICE_URL = Config.ApiRoot + "/webservice/member/";
    public static String PRODUCT_SERVICE_URL = Config.cacheUrl + "/product/route_product";
    public static String BASIC_SERVER_URL = Config.ApiRoot + "/webservice/basic/";
    public static String ORDER_SERVER_URL = Config.ApiRoot + "/webservice/order/";
    public static String TICKET_SERVER_URL = Config.ApiRoot + "/webservice/ticket/";
    public static String PICTURE_URL = Config.ApiRoot + "/public/product00/";
    public static String CATEGORY_PICTURE_URL = Config.ApiRoot + "/public/prodcate00/";
    public static String TICK_PICTURE_URL = Config.ApiRoot + "/public/activity/";
    public static String ACTIVITY_IMAGE_URL = Config.ApiRoot + "/public/newsinfo/";
    public static String LOGO_IMAGE_URL = Config.ApiRoot + "/public/company/";
    public static String BANNERS_IMAGE_URL = Config.ApiRoot + "/public/banners/";
    public static String PI_PAY_URL = Config.ApiRoot + "/webservice/pi/";

    static final MediaType JSON_CONTENT_TYPE = MediaType.parse("application/json");

    private ApiListener listener;
    private int connectTimeout = 15;
    private int readTimeout = 15;
    private int retryTimes;

    public interface ApiListener {
        void onSuccess(ApiRequest apiRequest, String body);

        void onFail();
    }

    public abstract HttpUrl getUrl();

    public abstract RequestBody getRequestBody();

    public ApiRequest setApiListener(ApiListener l) {
        listener = l;
        return this;
    }

    public ApiListener getApiListener() {
        return listener;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    protected HttpMethod method() {
        return HttpMethod.POST;
    }

    protected void addHeader(Request.Builder builder) {

    }

    @Override
    protected Void doInBackground(Void... voids) {
        OkHttpClient client = buildClient();
        Request request = buildRequest(method());
        httpRequest(client, request);
        return null;
    }

    protected OkHttpClient buildClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
    }

    protected Request buildRequest(HttpMethod method) {
        Request.Builder builder = new Request.Builder();
        builder.url(getUrl());
        addHeader(builder);
        switch (method) {
            case GET:
                builder.get();
                break;
            case POST:
                builder.post(getRequestBody());
                break;
            case DELETE:
                builder.delete(getRequestBody());
                break;
            case PATCH:
                builder.patch(getRequestBody());
                break;
            default:
                break;
        }
        return builder.build();
    }

    public void httpRequest(OkHttpClient client, Request request) {
        long useTime;
        long start = System.currentTimeMillis();
        String name = getClass().getSimpleName();
        String returnBody = "";
        if(request.body() instanceof  FormBody){
            Log.wtf(name, "Request =>" + getRequestContent((FormBody) request.body()));
        }else{
            Log.wtf(name, "Request =>" +request.body().toString());
        }

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response.code());
            } else {
                String body = response.body().string();

                useTime = calculateUseTime(start);
                Log.wtf(getClass().getSimpleName(), name + "耗時 =>" + useTime + "毫秒");
                Log.wtf(getClass().getSimpleName(), body);

                returnBody = body;
                JSONObject json = new JSONObject(body);

                if (needRetry(json)) {
                    retry();
                    return;
                }

                if (listener != null && !isCancelled()) {
                    Log.wtf(name, "Response=>" + body);
                    listener.onSuccess(ApiRequest.this, body);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (request.body() instanceof FormBody) {
                FormBody formBody = (FormBody) request.body();
                FirebaseCrashlytics.getInstance().log(getClass().getSimpleName()+":"+"Request =>" + getRequestContent(formBody));
            }

            useTime = calculateUseTime(start);
            Log.wtf(getClass().getSimpleName(), name + "耗時 =>" + useTime + "毫秒");
            FirebaseCrashlytics.getInstance().log(getClass().getSimpleName()+":"+ name + "耗時 =>" + useTime + "毫秒");
            FirebaseCrashlytics.getInstance().log(getClass().getSimpleName()+":"+ "Response =>" + returnBody);
            FirebaseCrashlytics.getInstance().recordException(e);

            if (needRetry()) {
                retry();
                return;
            }

            if (listener != null && !isCancelled()) {
                listener.onFail();
            }
        }
    }

    public String getRequestContent(FormBody formBody) {
        if (formBody == null) {
            return "";
        }

        String content = "{";
        int size = formBody.size();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                content += ",";
            }

            String key = formBody.encodedName(i);
            String value = formBody.encodedValue(i);
            content = content + "\"" + key + "\":\"" + value + "\"";
        }

        return content + "}";
    }

    //預設執行方式為依序執行，改為並行執行。
    //單核可同時執行五條執行緒
    public void go() {
        this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public ApiRequest setRetry(int retryTimes) {
        this.retryTimes = retryTimes;
        return this;
    }

    boolean needRetry() {
        return retryTimes > 0 && !isCancelled();
    }

    protected boolean needRetry(JSONObject jsonObject) throws JSONException {
        boolean isSuccess = jsonObject.has("code") && jsonObject.getInt("code") == 0;
        return !isSuccess && needRetry();
    }

    public void retry() {
        retryTimes--;
        OkHttpClient client = buildClient();
        Request request = buildRequest(method());
        httpRequest(client, request);
    }

    //動態改環境需要更新URL
    public static void renewUrl() {
        if (Config.ApiRoot.endsWith("_gray")) {
            //灰度測試版本，圖片位置需與正式環境相同
            int len = Config.ApiRoot.length();
            String formalApiRoot = Config.ApiRoot.substring(0, len - 5);
            PICTURE_URL = formalApiRoot + "/public/product00/";
            CATEGORY_PICTURE_URL = formalApiRoot + "/public/prodcate00/";
            TICK_PICTURE_URL = formalApiRoot + "/public/activity/";
            ACTIVITY_IMAGE_URL = formalApiRoot + "/public/newsinfo/";
            LOGO_IMAGE_URL = formalApiRoot + "/public/company/";
            BANNERS_IMAGE_URL = formalApiRoot + "/public/banners/";
        } else {
            PICTURE_URL = Config.ApiRoot + "/public/product00/";
            CATEGORY_PICTURE_URL = Config.ApiRoot + "/public/prodcate00/";
            TICK_PICTURE_URL = Config.ApiRoot + "/public/activity/";
            ACTIVITY_IMAGE_URL = Config.ApiRoot + "/public/newsinfo/";
            LOGO_IMAGE_URL = Config.ApiRoot + "/public/company/";
            BANNERS_IMAGE_URL = Config.ApiRoot + "/public/banners/";
        }
        PRODUCT_SERVICE_URL = Config.cacheUrl + "/product/route_product";
        MEMBER_SERVICE_URL = Config.ApiRoot + "/webservice/member/";
        BASIC_SERVER_URL = Config.ApiRoot + "/webservice/basic/";
        ORDER_SERVER_URL = Config.ApiRoot + "/webservice/order/";
        TICKET_SERVER_URL = Config.ApiRoot + "/webservice/ticket/";
        PI_PAY_URL = Config.ApiRoot + "/webservice/pi/";
    }

    public static String getAllUrl() {
        JsonObject jo = new JsonObject();
        jo.addProperty("MEMBER_SERVICE_URL", MEMBER_SERVICE_URL);
        jo.addProperty("PRODUCT_SERVICE_URL", PRODUCT_SERVICE_URL);
        jo.addProperty("BASIC_SERVER_URL", BASIC_SERVER_URL);
        jo.addProperty("ORDER_SERVER_URL", ORDER_SERVER_URL);
        jo.addProperty("TICKET_SERVER_URL", TICKET_SERVER_URL);
        jo.addProperty("PICTURE_URL", PICTURE_URL);
        jo.addProperty("CATEGORY_PICTURE_URL", CATEGORY_PICTURE_URL);
        jo.addProperty("TICK_PICTURE_URL", TICK_PICTURE_URL);
        jo.addProperty("ACTIVITY_IMAGE_URL", ACTIVITY_IMAGE_URL);
        jo.addProperty("LOGO_IMAGE_URL", LOGO_IMAGE_URL);
        jo.addProperty("BANNERS_IMAGE_URL", BANNERS_IMAGE_URL);
        jo.addProperty("PI_PAY_URL", PI_PAY_URL);
        return jo.toString();
    }

    private long calculateUseTime(long start) {
        long finish = System.currentTimeMillis();
        return finish - start;
    }
}
