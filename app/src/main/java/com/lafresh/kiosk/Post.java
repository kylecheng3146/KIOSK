package com.lafresh.kiosk;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class Post extends AsyncTask<String, Void, String> {
    public static final String TAG = "Post";
    private String url;
    public String response;
    public JSONObject jsonObject = new JSONObject();
    private String requestBody = "";

    public interface PostListener {
        void OnFinish(String response);
    }

    private PostListener postListener;

    public Post(Context context, String url) {
        this.url = url;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(String... params) {
        httpRequest();
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            postListener.OnFinish(response);
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    public void addField(String key, String value) {
        try {
            String pre = requestBody.equals("") ? "" : "&";
            requestBody = requestBody + pre + key + "=" + value;
            jsonObject.put(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    private void httpRequest() {
        long useTime = 0;
        long start = System.currentTimeMillis();
        try {
            Log.d(TAG, "requestBody=>" + requestBody);
            Log.d(TAG, "Url=>" + url);
            Log.d(TAG, "Request=>" + jsonObject.toString());

            requestBody = requestBody.replace("+", "%2B");

            byte[] postData = requestBody.getBytes(StandardCharsets.UTF_8);
            URL object = new URL(url);

            HttpURLConnection con = (HttpURLConnection) object.openConnection();
            con.setRequestMethod("POST");

            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("charset", "utf-8");
            con.setRequestProperty("Connection", "close");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);

            OutputStream os = con.getOutputStream();
            DataOutputStream writer = new DataOutputStream(os);

            writer.write(postData);
            writer.flush();
            writer.close();
            os.close();

            StringBuilder sb = new StringBuilder();
            int HttpResult = con.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                response = sb.toString();
            } else {
                //若網路連線失敗，要有回傳，以兼容原先做法
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("code", -1)
                        .put("message", "網路連線失敗，Code = " + HttpResult);
                response = jsonObject.toString();
            }

            long finish = System.currentTimeMillis();
            useTime = finish - start;
            Log.d(TAG, "Response=>" + response);
            Log.d(TAG, "耗時=>" + useTime + "毫秒");

        } catch (Exception e) {
            e.printStackTrace();
            long finish = System.currentTimeMillis();
            useTime = finish - start;
            FirebaseCrashlytics.getInstance().log("E/POST :耗時=>" + useTime + "毫秒");
            FirebaseCrashlytics.getInstance().log("E/POST :Request=>" + jsonObject.toString());
            FirebaseCrashlytics.getInstance().log("E/POST :Response=>" + response);
            FirebaseCrashlytics.getInstance().log(e.getMessage());

            //因有 ECONNRESET (Connection reset by peer) 錯誤，故Catch到直接當網路錯誤處理
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("code", -2)
                        .put("message", "網路斷線，請檢查網路");
                response = jsonObject.toString();
            } catch (JSONException e1) {
                e1.printStackTrace();
                FirebaseCrashlytics.getInstance().recordException(e1);
            }
        }
    }

    public void SetPostListener(PostListener postListener) {
        this.postListener = postListener;
    }

    public void GO() {
        this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }
}


