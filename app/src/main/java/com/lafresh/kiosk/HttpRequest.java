package com.lafresh.kiosk;

import android.os.AsyncTask;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.lafresh.kiosk.utils.LogUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpRequest extends AsyncTask<String, Void, String> {

    private String url;
    private String response;
    public JSONObject jo = new JSONObject();

    public HttpRequest(String url) {
        this.url = url;
    }

    public interface LSN {
        void onFinish(String response);
    }

    public LSN lsn;

    @Override
    protected String doInBackground(String... params) {
        httpRequest();
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            lsn.onFinish(response);
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }


    private void httpRequest() {
        try {
            URL object = new URL(url);
            HttpURLConnection con = (HttpURLConnection) object.openConnection();
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);

            OutputStream os = con.getOutputStream();
            DataOutputStream writer = new DataOutputStream(os);

            String reqString = "linePay_Request = " + jo.toString();
            LogUtil.writeLogToLocalFile(reqString);

            writer.writeBytes(jo.toString());
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
                String resString = "linePay_Response = " + response;
                LogUtil.writeLogToLocalFile(resString);
            }

        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
}
