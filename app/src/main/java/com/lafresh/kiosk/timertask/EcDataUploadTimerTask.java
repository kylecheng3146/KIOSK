package com.lafresh.kiosk.timertask;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.lafresh.kiosk.Config;
import com.lafresh.kiosk.easycard.EasyCard;
import com.lafresh.kiosk.easycard.model.EcStmc3Parameter;
import com.lafresh.kiosk.httprequest.restfulapi.ApiServices;
import com.lafresh.kiosk.httprequest.restfulapi.RetrofitManager;
import com.lafresh.kiosk.utils.Json;

import java.io.File;
import java.io.IOException;
import java.util.TimerTask;

import retrofit2.Response;

import static com.lafresh.kiosk.manager.FileManager.EC_DATA_PATH;
import static com.lafresh.kiosk.manager.FileManager.readFileData;

@RequiresApi(api = Build.VERSION_CODES.M)
public class EcDataUploadTimerTask extends TimerTask {

    @Override
    public void run() {
        File ecDataFile = new File(EC_DATA_PATH);
        File[] ecDataFiles = ecDataFile.listFiles();
        if (ecDataFiles.length > 0) {
            reSendDataToServer(ecDataFiles);
        } else {
            EasyCard.cancelTimer();
        }
    }

    private void reSendDataToServer(File[] ecDataFiles) {
        for (File file : ecDataFiles) {
            String data = readFileData(file.getAbsolutePath());
            boolean isSendSuccess = sendEcData(data);
            if (isSendSuccess) {
                file.delete();
            }
        }
    }

    private boolean sendEcData(String data) {
        ApiServices apiServices = RetrofitManager.getInstance().getApiServices(Config.cacheUrl);
        EcStmc3Parameter parameter = Json.fromJson(data, EcStmc3Parameter.class);
        Response<Response<Void>> response;
        try {
            response = apiServices.setEcStmc3(Config.token, parameter).execute();
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
