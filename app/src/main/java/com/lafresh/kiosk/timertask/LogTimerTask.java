package com.lafresh.kiosk.timertask;

import android.util.Log;

import com.lafresh.kiosk.Config;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.TimerTask;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.lafresh.kiosk.httprequest.ApiRequest.BASIC_SERVER_URL;
import static com.lafresh.kiosk.manager.FileManager.LOG_PATH;
import static com.lafresh.kiosk.manager.FileManager.readFileData;

public class LogTimerTask extends TimerTask {

    @Override
    public void run() {
        File logFile = new File(LOG_PATH);
        File[] logs = logFile.listFiles();
        if (logs.length > 0) {
            handleLogFiles(logs);
        }
    }

    private void handleLogFiles(File[] logFiles) {
        for (File logFile : logFiles) {
            String data = readFileData(logFile.getAbsolutePath());
            boolean isSendSuccess = sendLog(data);
            if (isSendSuccess) {
                logFile.delete();
            }
        }
    }

    //因本身為非UI執行緒，故不用AsyncTask
    private boolean sendLog(String logData) {
        OkHttpClient client = buildClient();
        Request request = buildRequest(logData);
        String res = "";
        int code = 1;
        try {
            res = httpRequest(client, request);
            JSONObject jsonObject = new JSONObject(res);
            code = jsonObject.getInt("code");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res.length() > 0 && code == 0;
    }

    private OkHttpClient buildClient() {
        return new OkHttpClient.Builder().build();
    }

    private Request buildRequest(String data) {
        return new Request.Builder()
                .url(HttpUrl.parse(BASIC_SERVER_URL))
                .post(getRequestBody(data))
                .build();
    }

    public RequestBody getRequestBody(String msg) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("op", "add_sitelog")
                .add("authkey", Config.authKey)
                .add("acckey", Config.acckey)
                .add("log_type", "KIOSK")
                .add("log_code", "KIOSK")
                .add("status", "KIOSK")
                .add("msg", msg);
        return builder.build();
    }

    private String httpRequest(OkHttpClient client, Request request) {
        String returnBody = "";
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response.code());
            } else {
                String body = response.body().string();
                Log.d(getClass().getSimpleName(), body);
                returnBody = body;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnBody;
    }
}
