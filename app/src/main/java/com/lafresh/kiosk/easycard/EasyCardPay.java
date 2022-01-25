package com.lafresh.kiosk.easycard;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.lafresh.kiosk.R;
import com.lafresh.kiosk.easycard.model.EasyCardData;
import com.lafresh.kiosk.manager.FileManager;
import com.lafresh.kiosk.utils.Json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import icerapi.icerapi.ICERAPI;

public class EasyCardPay {
    private final static String TAG = "EasyCardPay";
    private EasyCardData easyCardData;

    // /data/user/0/{AppId}/files
    private String mApplicationPath;
    private String mOutputString = "";
    private String mFinalOutputString = "";

    private String mMoney;
    //default 交易明細
    private String mTransactionType = "216000";
    //thread
    private Handler mThreadHandler;
    private HandlerThread mThread;
    private String[] sICERParName = {"LogFlag", "DLLVersion", "TCPIPTimeOut", "LogCnt", "ICERINI_Name", "ComPort", "ComPort2", "ECC_IP", "ECC_Port", "ICER_IP", "ICER_Port", "CMAS_IP", "CMAS_Port", "TMLocationID", "TMID", "TMAgentNumber", "LocationID", "NewLocationID", "SPID", "NewSPID", "Slot", "BaudRate", "OpenCom", "MustSettleDate", "ReaderMode", "BatchFlag", "OnlineFlag", "ICERDataFlag", "MessageHeader", "DLLMode", "AutoLoadMode", "MaxALAmt", "Dev_Info", "TCPIP_SSL", "CMASAdviceVerify", "AutoSignOnPercnet", "AutoLoadFunction", "VerificationCode", "ReSendReaderAVR", "XMLHeaderFlag", "FolderCreatFlag", "BLCName", "CMASMode", "POS_ID", "PacketLenFlag", "CRT_FileName", "Key_FileName", "ICERFlowDebug", "RS232Parameter"};
    private String[] sICERParValue = {"1", "2", "30", "30", "ICERINI.xml", "1", "/dev/ttyXRM1", "172.16.11.20", "8902", "172.25.17.95", "8303", "211.78.134.165", "7000", "0000000001", "01", "0001", "0", "0", "0", "0", "11", "115200", "4", "10", "4", "1", "1", "1", "99909020", "0", "1", "1000", "1122334455", "1", "0", "0", "0", "0", "0", "1", "1", "BLC03331A_161221.BIG", "0", "0", "0", "", "", "0", "8N1"};

    private String[] sICERReqValue = {"", "", "0", "0", "00000001", "", "", "1"};

    //1300 交易日期, 3700 RRN, 4100 端末設備編號
    private String t3700, t1300, t4100;

    // /storage/emulated/0/ICERINI/
    private final String ICERINI_Path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/ICERINI/";

    private Activity activity;
    private Listener listener;

    public EasyCardPay(Activity activity) {
        this.activity = activity;
        init();
    }

    private void init() {
        //初始化
        mFinalOutputString = "";
        mOutputString = "";
        mMoney = "0";
        mApplicationPath = activity.getFilesDir().toString();
        createFolder(ICERINI_Path);
        //create /data/data/[packageName]/files/BlcFile for 黑名單
        createFolder(mApplicationPath + "/BlcFile");
        //create data/data/[packageName]/files/ICERData
        createFolder(mApplicationPath + "/ICERData");
        createFolder(mApplicationPath + "/ICERData/BlcFile");
        mThread = new HandlerThread("name");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        String ecDataJson = FileManager.readFileData(FileManager.EXTERNAL_STORAGE_PATH + FileManager.EASY_CARD_FILE_NAME);
        easyCardData = Json.fromJson(ecDataJson, EasyCardData.class);
        if (easyCardData == null) {
            easyCardData = EasyCard.getEasyCardData();
        }
    }

    public EasyCardData getEasyCardData() {
        return easyCardData;
    }

    public void setEasyCardData(EasyCardData easyCardData) {
        this.easyCardData = easyCardData;
    }

    //"881999:SignOn"
    public void signOn() {
        this.mTransactionType = "881999";
        doIcerApi();
    }

    //"606100:扣款"
    public void deduct(String money) {
        this.mMoney = money;
        this.mTransactionType = "606100";
        doIcerApi();
        this.mMoney = "";
    }

    //"620061:扣款退貨"
    public void refund(String money) {
        this.mMoney = money;
        this.mTransactionType = "620061";
        doIcerApi();
        this.mMoney = "";
    }

    //"296000:票卡查詢"
    public void checkPayment() {
        this.mTransactionType = "296000";
        doIcerApi();
    }

    //"900099:悠遊卡結帳"
    public void checkout(String totCount, String totAmt) {
        this.mTransactionType = "900099";
        sICERReqValue[5] = totCount;//交易總筆數
        sICERReqValue[6] = totAmt;//交易總金額
        doIcerApi();
    }

    //"900099:悠遊卡結帳"
    public void checkout(String batchNo, String totCount, String totAmt) {
        this.mTransactionType = "900099";
        easyCardData.batchNo = batchNo;
        sICERReqValue[5] = totCount;//交易總筆數
        sICERReqValue[6] = totAmt;//交易總金額
        doIcerApi();
    }

    private void setBatchNo() {
        sICERReqValue[4] = easyCardData.batchNo;
    }

    private void setTradeNo() {
        sICERReqValue[1] = EasyCard.getTradeNo(easyCardData);
    }

    public void doIcerApi() {
        setBatchNo();
        setTradeNo();
        //初始化
        mFinalOutputString = "";
        mOutputString = "";
        Log.d(TAG, "Transaction = " + mTransactionType + "   " + "Money = " + mMoney);

        FileOutputStream out;
        String tempString = TxnPar.createRequestFile(mTransactionType, mMoney, sICERReqValue, t4100, t3700, t1300);
        Log.d(TAG, "REQ_FILE: " + tempString);
        try {

            if (checkICERDataFlag())
                out = new FileOutputStream(mApplicationPath + "/ICERData/ICERAPI.REQ");
            else
                out = new FileOutputStream(mApplicationPath + "/ICERAPI.REQ");
            out.write(tempString.toString().getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //create REQ.ok
        try {
            if (checkICERDataFlag())
                out = new FileOutputStream(mApplicationPath + "/ICERData/ICERAPI.REQ.OK");
            else
                out = new FileOutputStream(mApplicationPath + "/ICERAPI.REQ.OK");
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mThreadHandler.post(ICERAPI_exe);
    }

    private void createFolder(String path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                File tDataPath = new File(path);
                //識別指定的目錄是否存在，false則建立；
                if (!tDataPath.exists()) {
                    tDataPath.mkdir();
                }
            } else {
                requestPermission();
            }
        } else {
            File tDataPath = new File(path);
            //識別指定的目錄是否存在，false則建立；
            if (!tDataPath.exists()) {
                tDataPath.mkdir();
            }
        }
    }


    //run ICERAPI
    private Runnable ICERAPI_exe = new Runnable() {

        public synchronized void run() {
            int inTime;
            //delete ICERAPI.RES.OK   ICERAPI.RES
            deleteExistFile(mApplicationPath + "/ICERData/ICERAPI.RES.OK");
            deleteExistFile(mApplicationPath + "/ICERData/ICERAPI.RES");
            deleteExistFile(mApplicationPath + "/ICERAPI.RES.OK");
            deleteExistFile(mApplicationPath + "/ICERAPI.RES");
            //call ICER_API
            new ICERAPI(mApplicationPath, Environment.getExternalStorageDirectory().getAbsolutePath(), activity);

            //showMessage ICERAPI.RES
            inTime = 0;
            while (true) {
                File file;
                if (checkICERDataFlag()) {
                    file = new File(mApplicationPath + "/ICERData/ICERAPI.RES.OK");
                } else {
                    file = new File(mApplicationPath + "/ICERAPI.RES.OK");
                }

                if (file.exists()) {
                    String[] result = new String[6];
                    if (checkICERDataFlag()) {
                        mOutputString = showFileOnToast(mApplicationPath + "/ICERData/ICERAPI.RES", false);
                    } else {
                        mOutputString = showFileOnToast(mApplicationPath + "/ICERAPI.RES", false);
                    }
                    //get t3700 t1300 t4100
                    if (findTag(mOutputString, "T3700") != null) {
                        t3700 = findTag(mOutputString, "T3700");
                    }
                    if (findTag(mOutputString, "T1300") != null) {
                        t1300 = findTag(mOutputString, "T1300");
                    }
                    if (findTag(mOutputString, "T4100") != null) {
                        t4100 = findTag(mOutputString, "T4100");
                    }
                    //get t3700 t1300 t4100
                    for (int i = 0; i <= 6; i++) {
                        if (mOutputString.contains("T390" + i)) {
                            result[i] = findTag(mOutputString, "T390" + i);
                            mFinalOutputString += "T390" + i + " =  " + result[i] + "\n";
                        }
                    }
                    Log.d(TAG, "output = " + mFinalOutputString);
                    break;
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                inTime += 50;

                int timeout = 10;
                if (inTime >= timeout * 1000) {
                    break;
                }
            }

            //收到T1101要進位
            if (findTag(mOutputString, EcResTag.T1101) != null) {
                EasyCard.updateTradeNo(easyCardData);
            }

            if (listener != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onResult(mOutputString);
                    }
                });
            }
        }
    };

    public void retryApi(String price) {
        FileOutputStream out;
        mMoney = price;
        String tempString = TxnPar.createRequestFile(mTransactionType, mMoney, sICERReqValue, t4100, t3700, t1300);
        mMoney = "";
        Log.d(TAG, "REQ_FILE: " + tempString);
        try {
            if (checkICERDataFlag())
                out = new FileOutputStream(mApplicationPath + "/ICERData/ICERAPI.REQ");
            else
                out = new FileOutputStream(mApplicationPath + "/ICERAPI.REQ");
            out.write(tempString.toString().getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mThreadHandler.postDelayed(ICERAPI_exe, 200);
    }

    public String findTag(String mOutputString, String tag) {
        if (mOutputString.contains(tag)) {
            int start, end;
            start = mOutputString.indexOf(tag);
            end = mOutputString.indexOf("/" + tag);
            String tempString;
            if (tag.equals("LocationID")) {
                tempString = mOutputString.substring(start, end);
                start = tempString.lastIndexOf("LocationID");
                tempString = tempString.substring(start, tempString.length());
            } else {
                tempString = mOutputString.substring(start, end);
            }
            Log.d(TAG, "tempString:" + tempString);
            start = tempString.indexOf(">");
            end = tempString.indexOf("<");
            if (end - start > 1)
                return tempString.substring(start + 1, end);
            else
                return null;
        }
        return null;
    }

    public String showFileOnToast(String bFileName, Boolean showToast) {
        FileInputStream in = null;
        StringBuffer data = new StringBuffer();
        try {
            //開啟 getFilesDir() 目錄底下名稱為 test.txt 檔案
            in = new FileInputStream(bFileName);
            //讀取該檔案的內容
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (showToast)
            Toast.makeText(activity, data.toString(), Toast.LENGTH_SHORT).show();
        return data.toString();
    }

    public void deleteExistFile(String bFileName) {
        File file = new File(bFileName);
        Log.d(TAG, "deleteFile: " + bFileName);
        if (file.exists())
            file.delete();
    }

    public void deleteLocalEasyCardData() {
        FileManager.deleteFileData(FileManager.EASY_CARD_FILE_NAME);
    }

    public void release() {
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }

        listener = null;
        activity = null;
    }

    //Write external permission
    protected boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    protected void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(activity, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        }
    }


    private Boolean checkICERDataFlag() {
        for (int i = 0; i < sICERParName.length; i++) {
            if (sICERParName[i].equals("ICERDataFlag")) {
                return sICERParValue[i].equals("1");
            }
        }
        return false;
    }

    //將參數檔案寫至App路徑資料夾
    public void restoreFile() {
        Runnable getIniFile = new Runnable() {
            @Override
            public void run() {
                InputStream in;
                OutputStream out;
                byte[] buffer = new byte[1024];
                int read;

                File inFile = new File(ICERINI_Path, "ICERINI.xml");
                if (!inFile.exists()) {
                    String paraMsg = activity.getString(R.string.noEzCardPara);
                    Toast.makeText(activity, paraMsg, Toast.LENGTH_LONG).show();
                    if (listener != null) {
                        listener.onNoICERINI();
                    }
                    return;
                }

                File outFile = new File(mApplicationPath, "ICERINI.xml");
                try {
                    in = new FileInputStream(inFile);
                    out = new FileOutputStream(outFile);

                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    in.close();
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    FirebaseCrashlytics.getInstance().recordException(e);
                } finally {
                    if (listener != null) {
                        listener.onResult(null);
                    }
                }
            }
        };
        mThreadHandler.postDelayed(getIniFile, 50);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }


    public interface Listener {
        void onResult(String result);

        void onNoICERINI();
    }
}
