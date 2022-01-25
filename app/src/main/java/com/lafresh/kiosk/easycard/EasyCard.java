package com.lafresh.kiosk.easycard;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.lafresh.kiosk.Bill;
import com.lafresh.kiosk.Config;
import com.lafresh.kiosk.easycard.model.EasyCardData;
import com.lafresh.kiosk.easycard.model.EcCheckoutData;
import com.lafresh.kiosk.easycard.model.EcPayData;
import com.lafresh.kiosk.easycard.model.EcSettle3;
import com.lafresh.kiosk.easycard.model.EcSettle3Parameter;
import com.lafresh.kiosk.easycard.model.EcStmc3Parameter;
import com.lafresh.kiosk.manager.OrderManager;
import com.lafresh.kiosk.timertask.EcDataUploadTimerTask;
import com.lafresh.kiosk.utils.TimeUtil;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;

import static com.lafresh.kiosk.manager.FileManager.EC_DATA_PATH;

public class EasyCard {
    private static final String EASY_CARD_FILE_NAME = "easy_card";
    private static final String BLACK_LIST_DATE = "blackListDate";//黑名單日期
    private static final String BLCNAME = "BLCName";//黑名單檔名
    private static EasyCardData easyCardData = new EasyCardData();
    private static Timer timer;

    public static void startUploadTimer() {
        if (timer == null) {
            long repeatTime = 60 * 1000;
            timer = new Timer(true);
            timer.schedule(new EcDataUploadTimerTask(), repeatTime, repeatTime);
        }
    }

    public static void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public static EasyCardData getEasyCardData() {
        return easyCardData;
    }

    public static boolean hasUnUploadEcData() {
        File ecDataFile = new File(EC_DATA_PATH);
        File[] ecDataFiles = ecDataFile.listFiles();
        if (ecDataFiles != null) {
            return ecDataFiles.length > 0;
        }
        return false;
    }

    public static boolean hasOldData(EasyCardData easyCardData) {
        return easyCardData.checkoutDate != null;
    }

    public static boolean isCheckoutToday(EasyCardData easyCardData) {
        if (easyCardData.checkoutDate == null) {
            easyCardData.checkoutDate = "";
        }
        return easyCardData.checkoutDate.equals(TimeUtil.getToday());
    }

    public static void updateCheckoutDate(EasyCardData easyCardData) {
        easyCardData.checkoutDate = TimeUtil.getToday();
        easyCardData.batchNo = getNextBatchNo(easyCardData);
        easyCardData.tradeNo = getNextTradeNo(easyCardData);
    }

    public static String getBatchNo(EasyCardData easyCardData) {
        if (easyCardData.batchNo == null) {
            easyCardData.batchNo = TimeUtil.getYYYYMMdd().substring(2) + "01";
        }
        return easyCardData.batchNo;
    }

    public static String getTradeNo(EasyCardData easyCardData) {
        if (easyCardData.tradeNo == null) {
            easyCardData.tradeNo = "000001";
        }
        return easyCardData.tradeNo;
    }

    public static void updateTradeNo(EasyCardData easyCardData) {
        easyCardData.tradeNo = getNextTradeNo(easyCardData);
    }

    private static String getNextBatchNo(EasyCardData easyCardData) {
        String today = TimeUtil.getYYYYMMdd().substring(2);
        String signOnDate = getBatchNo(easyCardData);
        if (today.equals(signOnDate.substring(0, 6))) {
            String no = signOnDate.substring(6);
            int tempNo = Integer.parseInt(no);
            tempNo += 1;
            String newNo = String.valueOf(tempNo);
            newNo = addPrefixZero(newNo, 2);
            return today + newNo;
        } else {
            return today + "01";
        }
    }

    private static String getNextTradeNo(EasyCardData easyCardData) {
        String originNo = easyCardData.tradeNo;
        int tempNo = Integer.parseInt(originNo);
        tempNo += 1;
        if (tempNo > 999999) {
            tempNo = 1;
        }
        String nextNo = String.valueOf(tempNo);
        nextNo = addPrefixZero(nextNo, 6);
        return nextNo;
    }

    private static String addPrefixZero(String no, int digits) {
        int len = no.length();
        if (len == digits) {
            return no;
        }

        int addDigitNo = digits - len;
        StringBuilder noBuilder = new StringBuilder(no);
        for (int i = 0; i < addDigitNo; i++) {
            noBuilder.insert(0, "0");
        }
        no = noBuilder.toString();
        return no;
    }

    public static boolean isTodayBlackList(Context context) {
        SharedPreferences sp = context.getSharedPreferences(EASY_CARD_FILE_NAME, Context.MODE_PRIVATE);
        String blackListDate = sp.getString(BLACK_LIST_DATE, "");
        return blackListDate.equals(TimeUtil.getToday());
    }

    public static void updateBlackListDate(Context context) {
        SharedPreferences sp = context.getSharedPreferences(EASY_CARD_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(BLACK_LIST_DATE, TimeUtil.getToday());
        editor.apply();
    }

    public static EcCheckoutData parseCheckoutData(String res, EasyCardPay e, EcCheckoutData checkoutData) {
        checkoutData.setShop_id(Config.shop_id);
        checkoutData.setPos_id(Config.kiosk_id);
        checkoutData.setInput_date(TimeUtil.getNowTime());
        checkoutData.setTXN_Serial_number(e.findTag(res, EcResTag.T1100));
        String date = e.findTag(res, EcResTag.T1300);
        String time = e.findTag(res, EcResTag.T1200);
        date = date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6);
        time = time.substring(0, 2) + ":" + time.substring(2, 4) + ":" + time.substring(4);
        checkoutData.setTXN_DateTime(date + " " + time);
        checkoutData.setBatch_number(e.findTag(res, EcResTag.T5501));
        checkoutData.setNew_Deviceid(e.findTag(res, EcResTag.T4110));
        checkoutData.setIsSettle("1");
        checkoutData.setTransfer_status("0");
        checkoutData.setLast_update(TimeUtil.getNowTime());
        checkoutData.setSettlement_Status(e.findTag(res, EcResTag.T3912));
        return checkoutData;
    }

    public static EcCheckoutData parseCheckoutData(EcSettle3 ecSettle3) {
        EcCheckoutData checkoutData = new EcCheckoutData();
        checkoutData.setDeduct_count(ecSettle3.getDeduct_count());
        checkoutData.setRefund_count(ecSettle3.getRefund_count());
        checkoutData.setAddValue_count(ecSettle3.getAddValue_count());
        checkoutData.setTotal_Txn_count(ecSettle3.getTotal_Txn_count());
        checkoutData.setDeduct_amt(ecSettle3.getDeduct_amt() + "");
        checkoutData.setRefund_amt(ecSettle3.getRefund_amt() + "");
        checkoutData.setTotal_AddValue_amt(ecSettle3.getTotal_AddValue_amt() + "");
        checkoutData.setTotal_Txn_amt(ecSettle3.getTotal_Txn_amt() + "");
        checkoutData.setTotal_Txn_pure_amt(ecSettle3.getTotal_Txn_pure_amt() + "");
        return checkoutData;
    }

    public static EcSettle3Parameter parseEcSettle3Parameter(EcCheckoutData checkoutData) {
        EcSettle3Parameter parameter = new EcSettle3Parameter();
        parameter.setStore_id(checkoutData.getShop_id());
        parameter.setPos_id(checkoutData.getPos_id());
        parameter.setSale_date(checkoutData.getInput_date());
        parameter.setTXN_Serial_number(checkoutData.getTXN_Serial_number());
        parameter.setTXN_DateTime(checkoutData.getTXN_DateTime());
        parameter.setBatch_number(checkoutData.getBatch_number());
        parameter.setNew_Deviceid(checkoutData.getNew_Deviceid());
        parameter.setDeduct_count(checkoutData.getDeduct_count());
        parameter.setRefund_count(checkoutData.getRefund_count());
        parameter.setAddValue_count(checkoutData.getAddValue_count());
        parameter.setDeduct_amt(Integer.parseInt(checkoutData.getDeduct_amt()));
        parameter.setRefund_amt(Integer.parseInt(checkoutData.getRefund_amt()));
        parameter.setTotal_AddValue_amt(Integer.parseInt(checkoutData.getTotal_AddValue_amt()));
        parameter.setTotal_Txn_count(checkoutData.getTotal_Txn_count());
        parameter.setTotal_Txn_amt(Integer.parseInt(checkoutData.getTotal_Txn_amt()));
        parameter.setTotal_Txn_pure_amt(Integer.parseInt(checkoutData.getTotal_Txn_pure_amt()));
        parameter.setSettlement_Status(checkoutData.getSettlement_Status());
        return parameter;
    }

    public static EcPayData parsePayData(String res, EasyCardPay e) {
        EcPayData ecPayData = new EcPayData();
        ecPayData.setShop_id(Config.shop_id);
        if (Config.isNewOrderApi) {
            ecPayData.setSale_id(OrderManager.getInstance().getOrderId());
        }else{
            ecPayData.setSale_id(Bill.fromServer.worder_id);
        }
        ecPayData.setPos_id(Config.kiosk_id);
        ecPayData.setInput_date(TimeUtil.getNowTime());
        ecPayData.setUser_id(Config.acckey);
        ecPayData.setJob_type("0");
        ecPayData.setError_code(e.findTag(res, EcResTag.T3901));
        String t0409 = e.findTag(res, EcResTag.T0409);
        String saleType = "1";
        if (t0409 != null) {
            if ("0".equals(ecPayData.getError_code())) {
                saleType = "3";
            } else {
                saleType = "4";
            }
        }
        ecPayData.setSale_type(saleType);
        ecPayData.setMessage_type(e.findTag(res, EcResTag.T0100));
        ecPayData.setProcessing_code(e.findTag(res, EcResTag.T0300));
        ecPayData.setTXN_number(e.findTag(res, EcResTag.T1100));
        ecPayData.setHost_Serial_number(e.findTag(res, EcResTag.T1101));
        ecPayData.setTxn_Time(e.findTag(res, EcResTag.T1200));
        ecPayData.setTxn_Date(e.findTag(res, EcResTag.T1300));
        ecPayData.setBatch_number(e.findTag(res, EcResTag.T5501));
        ecPayData.setMifare_ID(e.findTag(res, EcResTag.T0200));
        ecPayData.setPurse_ID(e.findTag(res, EcResTag.T0211));
        ecPayData.setCard_type(e.findTag(res, EcResTag.T0213));
        ecPayData.setPersonal_profile(e.findTag(res, EcResTag.T0214));
        ecPayData.setReceipt_card_number(e.findTag(res, EcResTag.T0215));
        ecPayData.setCard_exp(e.findTag(res, EcResTag.T1402));
        ecPayData.setRRN(e.findTag(res, EcResTag.T3700));
        ecPayData.setEDC_ID(e.findTag(res, EcResTag.T4100));
        ecPayData.setDevice_ID(e.findTag(res, EcResTag.T4109));
        ecPayData.setNew_deviceID(e.findTag(res, EcResTag.T4110));
        ecPayData.setShop_code(e.findTag(res, EcResTag.T4200));
        ecPayData.setCPU_purse_version_number(e.findTag(res, EcResTag.T4800));
        ecPayData.setBank_code(e.findTag(res, EcResTag.T4803));
        ecPayData.setArea_code(e.findTag(res, EcResTag.T4804));
        ecPayData.setCPU_sub_area_code(e.findTag(res, EcResTag.T4805));
        ecPayData.setTXN_proof_code(e.findTag(res, EcResTag.T6400));
        ecPayData.setN_CPU_EV_Before_TXN(e.findTag(res, EcResTag.T0415));
        ecPayData.setN_CPU_Txn_AMT(e.findTag(res, EcResTag.T0400));
        ecPayData.setN_CPU_EV(e.findTag(res, EcResTag.T0410));
        ecPayData.setN_AutoLoad_Amount(e.findTag(res, EcResTag.T0409));
        ecPayData.setIsUpload("0");
        ecPayData.setTransfer_status("0");
        ecPayData.setLast_update(TimeUtil.getNowTime());

        return ecPayData;
    }

    public static EcStmc3Parameter parseEcStmc3Parameter(EcPayData e) {
        EcStmc3Parameter parameter = new EcStmc3Parameter();
        parameter.setStore_id(e.getShop_id());
        parameter.setPos_id(e.getPos_id());
        parameter.setOrder_id(e.getSale_id());
        parameter.setSale_date(e.getInput_date());
        parameter.setSale_type(Integer.parseInt(e.getSale_type()));
        parameter.setError_code(e.getError_code());
        parameter.setMessage_type(e.getMessage_type());
        parameter.setProcessing_code(e.getProcessing_code());
        parameter.setTXN_number(e.getTXN_number());
        parameter.setHost_Serial_number(e.getHost_Serial_number());
        parameter.setTxn_Time(e.getTxn_Time());
        parameter.setTxn_Date(e.getTxn_Date());
        parameter.setBatch_number(e.getBatch_number());
        parameter.setMifare_ID(e.getMifare_ID());
        parameter.setPurse_ID(e.getPurse_ID());
        parameter.setCard_type(e.getCard_type());
        parameter.setPersonal_profile(e.getPersonal_profile());
        parameter.setReceipt_card_number(e.getReceipt_card_number());
        parameter.setCard_exp(e.getCard_exp());
        parameter.setRRN(e.getRRN());
        parameter.setEDC_ID(e.getEDC_ID());
        parameter.setDevice_ID(e.getDevice_ID());
        parameter.setNew_deviceID(e.getNew_deviceID());
        parameter.setShop_code(e.getShop_code());
        parameter.setCPU_purse_version_number(e.getCPU_purse_version_number());
        parameter.setBank_code(e.getBank_code());
        parameter.setArea_code(e.getArea_code());
        parameter.setCPU_sub_area_code(e.getCPU_sub_area_code());
        parameter.setTXN_proof_code(e.getTXN_proof_code());
        parameter.setN_CPU_EV_Before_TXN(Integer.parseInt(e.getN_CPU_EV_Before_TXN()));
        parameter.setN_CPU_Txn_AMT(Integer.parseInt(e.getN_CPU_Txn_AMT()));
        parameter.setN_CPU_EV(Integer.parseInt(e.getN_CPU_EV()));
        parameter.setN_AutoLoad_Amount(Integer.parseInt(e.getN_AutoLoad_Amount()));
        return parameter;
    }

    public static void updateBlcNameParameter(Context context, String fileName) {
        SharedPreferences sp = context.getSharedPreferences(EASY_CARD_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(BLCNAME, fileName).apply();

        String ICERINI_Path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/ICERINI/";
        String mApplicationPath = context.getFilesDir().toString();
        String iniFileName = "ICERINI.xml";
        File local = new File(mApplicationPath, iniFileName);
        SAXReader reader = new SAXReader();
        try {
            Document localDoc = reader.read(local);
            Element blcElement = localDoc.getRootElement().element("TRANS").element(BLCNAME);
            blcElement.setText(fileName);

            String xmlString = localDoc.asXML();
            writeFile(ICERINI_Path + iniFileName, xmlString);
            writeFile(mApplicationPath + "/" + iniFileName, xmlString);
        } catch (DocumentException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    public static String getICERINIParameter(Context context) {
        String mApplicationPath = context.getFilesDir().toString();
        String iniFileName = "ICERINI.xml";
        File local = new File(mApplicationPath, iniFileName);
        SAXReader reader = new SAXReader();
        String parameter = "";
        try {
            Document localDoc = reader.read(local);
            parameter = localDoc.asXML();
        } catch (DocumentException e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        return parameter;
    }

    private static void writeFile(String file_name, String data) {
        File file = new File(file_name);
        try {
            FileWriter fw = new FileWriter(file);
            fw.append(data);
            fw.flush();
            fw.close();

            Log.d("EasyCard", "fileName = " + file_name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
