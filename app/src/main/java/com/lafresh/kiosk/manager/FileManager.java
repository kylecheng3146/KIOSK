package com.lafresh.kiosk.manager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.lafresh.kiosk.easycard.model.EasyCardData;
import com.lafresh.kiosk.utils.CommonUtils;
import com.lafresh.kiosk.utils.Json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileManager {
    public static final String EXTERNAL_STORAGE_PATH = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/KIOSK/";
    public static final String LOG_PATH = EXTERNAL_STORAGE_PATH + "LOG/";
    public static final String EC_DATA_PATH = EXTERNAL_STORAGE_PATH + "EasyCard/";

    //參數設定
    private final static String CONFIG_SP_KEY = "config";
    public final static String CONFIG_FILE_NAME = "config.txt";

    //悠遊卡
    public static final String EASY_CARD_SP_KEY = "easy_card";
    public static final String EASY_CARD_FILE_NAME = "easy_card.txt";
    public static final String CHECKOUT_DATE_KEY = "checkoutDate";
    public static final String BATCH_NO_KEY = "Batch_number";//批次號碼
    public static final String TRADE_NO_KEY = "Host_Serial_number";//交易序號 T1101
    public static final String CHECKOUT_DATA = "checkoutDataJsonString";//結帳資料

    //每日任務
    public static final String DAILY_MISSION_SP_KEY = "daily_mission";
    public static final String DAILY_MISSION_FILE_NAME = "daily_mission";
    public static final String KEY_NCCC_CHECKOUT_DATE = "nccc_checkout_date";

    public static void init(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermission((Activity) context)) {
                requestPermission((Activity) context);
            }
        }
        createFolder(EXTERNAL_STORAGE_PATH);
        createFolder(LOG_PATH);
        createFolder(EC_DATA_PATH);
        checkAndCopyEasyCardData(context.getSharedPreferences(EASY_CARD_SP_KEY, Context.MODE_PRIVATE));
    }

    private static void createFolder(String path) {
        File tDataPath = new File(path);
        //識別指定的目錄是否存在，false則建立；
        if (!tDataPath.exists()) {
            tDataPath.mkdir();
        }
    }

    //Write external permission
    private static boolean checkPermission(Activity activity) {
        int result = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private static void requestPermission(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            CommonUtils.showMessage(activity,"Write External Storage permission allows us to do store images. Please allow this permission in App Settings.");
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        }
    }

    private static void checkAndCopyEasyCardData(SharedPreferences sp) {
        if (sp.contains(BATCH_NO_KEY)) {
            String batchNo = sp.getString(BATCH_NO_KEY, "");
            String tradeNo = sp.getString(TRADE_NO_KEY, "");
            String checkoutDate = sp.getString(CHECKOUT_DATE_KEY, "");
            String checkoutData = sp.getString(CHECKOUT_DATA, "");
            EasyCardData easyCardData = new EasyCardData();
            easyCardData.batchNo = batchNo;
            easyCardData.tradeNo = tradeNo;
            easyCardData.checkoutDate = checkoutDate;
            easyCardData.checkoutData = checkoutData;
            writeFileData(EASY_CARD_FILE_NAME, Json.toJson(easyCardData));
            sp.edit().clear().apply();
        }
    }

    public static boolean isSharedPreferenceHasData(SharedPreferences sp) {
        return sp.getAll().size() > 0;
    }

    public static void clearSpData(SharedPreferences sp) {
        sp.edit().clear().apply();
    }

    public static void writeFileData(String fileName, String data) {
        File file = new File(EXTERNAL_STORAGE_PATH, fileName);
        writeDataIntoFile(file, data);
    }

    public static void deleteFileData(String fileName) {
        File file = new File(EXTERNAL_STORAGE_PATH, fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    public static void writeLogFile(String fileName, String data) {
        File file = new File(LOG_PATH, fileName);
        writeDataIntoFile(file, data);
    }

    public static void writeEcData(String fileName, String data) {
        File file = new File(EC_DATA_PATH, fileName);
        writeDataIntoFile(file, data);
    }

    private static void writeDataIntoFile(File file, String data) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String readFileData(String fileName) {
        File file = new File(fileName);
        StringBuilder data = new StringBuilder();
        if (file.exists()) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String s;
                while ((s = br.readLine()) != null) {
                    data.append(s).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return data.toString();
        }
        return data.toString();
    }
}
