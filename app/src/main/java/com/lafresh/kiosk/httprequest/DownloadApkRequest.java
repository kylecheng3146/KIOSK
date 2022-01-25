package com.lafresh.kiosk.httprequest;

import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.lafresh.kiosk.utils.FileUtil;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.lafresh.kiosk.httprequest.ApiRequest.BASIC_SERVER_URL;

public class DownloadApkRequest extends AsyncTask {
    private String filePath;
    private String fileName;
    private int fileSize;
    private Listener listener;
    private ProgressBar pgb;

    public DownloadApkRequest(String filePath, String fileName, int fileSize,
                              Listener listener, ProgressBar pgb) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.listener = listener;
        this.pgb = pgb;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient client = builder.build();
        Request request = new Request.Builder().get().url(getUrl()).build();
        InputStream inputStream;
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            } else {
                inputStream = response.body().byteStream();
                FileUtil.UpdateCallback updateCallback = new FileUtil.UpdateCallback() {
                    @Override
                    public void onUpdate() {
                        publishProgress();
                    }
                };
                FileUtil.storeFile(inputStream, filePath, fileName, fileSize, updateCallback);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            listener.onFinish(fileName);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        int nowProgress = pgb.getProgress();
        pgb.setProgress(nowProgress + 1);
    }

    @Override
    protected void onPostExecute(Object o) {
        pgb.setProgress(pgb.getMax());
        pgb = null;
    }

    private HttpUrl getUrl() {
        return HttpUrl.parse(BASIC_SERVER_URL + "/downloadfile.php?file=" + fileName);
    }

    public interface Listener {
        void onFinish(String fileName);
    }
}
