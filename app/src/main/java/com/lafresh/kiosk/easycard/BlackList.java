package com.lafresh.kiosk.easycard;

import android.content.Context;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.lafresh.kiosk.R;
import com.lafresh.kiosk.httprequest.ApiRequest;
import com.lafresh.kiosk.httprequest.GetEcBlackListApiRequest;
import com.lafresh.kiosk.utils.ZipUtil;

import java.io.File;

/**
 * 悠遊卡需一天更新一次黑名單
 */

public class BlackList {
    private static GetEcBlackListApiRequest getEcBlcRequest;

    public static void checkEcBlackList(Context context) {
        if (!EasyCard.isTodayBlackList(context)) {
            getEcBlcRequest = new GetEcBlackListApiRequest(context, getEcBlcDataListener(context));
            getEcBlcRequest.go();
        }
    }

    private static ApiRequest.ApiListener getEcBlcDataListener(final Context context) {
        return new ApiRequest.ApiListener() {
            @Override
            public void onSuccess(ApiRequest apiRequest, String body) {
                String fileName = unzipEcBlcFile(context);
                if (isLegalBlcFileName(fileName)) {
                    EasyCard.updateBlackListDate(context);
                    EasyCard.updateBlcNameParameter(context, fileName);
                }
            }

            @Override
            public void onFail() {

            }
        };
    }

    private static String unzipEcBlcFile(Context context) {
        String filePath = context.getFilesDir().toString();
        String blcFilePath = context.getResources().getString(R.string.ecBlackListFilePath);
        String blcZipName = context.getResources().getString(R.string.ecBlackListZipFileName);
        File[] blcFiles = new File(filePath + blcFilePath).listFiles();
        File zipFile = new File(filePath + blcFilePath + blcZipName);
        String fileName = "";

        if (!ZipUtil.isGoodZipFile(zipFile)) {
            return fileName;
        }

        for (File file : blcFiles) {
            if (file.getName().endsWith(".BIG")) {
                boolean isDelete = file.delete();
            }
        }

        try {
            ZipUtil.unzip(zipFile);
            throw new RuntimeException("Unzip BlackListFile Finished");
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        blcFiles = new File(filePath + blcFilePath).listFiles();
        for (File file : blcFiles) {
            if (file.getName().endsWith(".BIG")) {
                fileName = file.getName();
            }
        }

        return fileName;
    }

    private static boolean isLegalBlcFileName(String fileName) {
        if (fileName == null || fileName.length() == 0) {
            return false;
        }

        return fileName.startsWith("BLC") && fileName.endsWith(".BIG");
    }

    public static void cancelGetEcBlackListApiRequest() {
        if(getEcBlcRequest!=null){
            getEcBlcRequest.cancel(true);
        }
    }
}
