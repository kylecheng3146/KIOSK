package com.lafresh.kiosk;

import com.google.firebase.database.annotations.NotNull;
import com.google.gson.JsonObject;
import com.lafresh.kiosk.httprequest.ApiRequest;
import com.lafresh.kiosk.model.SaleMethod;
import com.lafresh.kiosk.type.EasyCardTransactionType;
import com.lafresh.kiosk.type.FlavorType;

import java.util.List;

public class Config {
    public static String ApiRoot = BuildConfig.DEBUG ? "https://kiosk.lafresh.com.tw/kiosk/apptest" : "https://kiosk.lafresh.com.tw/kiosk/app";
    public static String cacheUrl = BuildConfig.DEBUG ? "http://omni1.lafresh.com.tw:8090" : "https://omni.lafresh.com.tw";
    public static String authKey = "vbuf8b1blyrm7k7fbnrfpu59twhkuywg";
    public static String shop_id = "000030";
    public static String shopName = "";
    public static String memberName = "";
    public static String acckeyDefault = "67pcbag9awwfdxe19gdj1rgfb2xvny9c";
    public static String acckey = acckeyDefault;
    public static String kiosk_id = "k1";
    public static String linePayUrl = ApiRoot + "/webservice/line/pay.php";
    public static int idleCount = BuildConfig.FLAVOR.equals(FlavorType.liangpin.name()) ? 40 : 60;
    public static int idleCount_BillOK = 10;
    public static int idleWarnTime = 30;
    public static String invoice_aes_key = "AES_Key_laFresh";
    public static int time_adChange = 5;

    public static String creditCardComportPath = "/dev/ttyS3";
    public static int creditCardBaudRate = 9600;

    public static int orderPriceLimit = 100000;//單筆訂單金額限制
    public static int tableNoConfig = 0;
    public static EasyCardTransactionType easyCardTransactionType;//悠遊卡交易類別
    public static String mealDate;//悠遊卡交易類別

    public static String defaultToken;
    public static String token;
    public static String titleLogoPath;
    public static String homeTitleLogoPath;
    public static String productImgPath;
    public static String bannerImg;

    public static boolean useEasyCardByShop = false;//預留日後擴充
    public static boolean useEasyCardByKiosk = false;//預留日後擴充
    public static boolean useNcccByShop = false;
    public static boolean useNcccByKiosk = false;
    public static boolean disableReceiptModule = false;
    public static boolean disablePrinterModule = false;
    public static boolean disablePrinter = false;
    public static boolean usePiPay = false;
    public static boolean enableCounter = false;//櫃檯結帳
    public static boolean isOnlyCounterPay = false;//只使用櫃台結帳
    public static boolean isLogin = true;//外帶開關
    public static boolean isNewOrderApi = true;//新版訂單API流程
    //若使用模擬器則開啟此判斷，就不用每次一直註解掉硬體判斷邏輯的Code
    public static boolean isUseEmulator = BuildConfig.DEBUG;
    //如果使用模擬器則可以直接在這邊寫入LinePay的付款碼
    public static String linePayPaymentCode = "";
    //選擇的付款方式
    public static String scanType = "";
    public static boolean isAlreadyRecommended = false;
    //伽利略地址暫存
    public static String addressDefault = "";
    public static String address = addressDefault;
    public static String phoneNumber = "";
    //進入集點頁面的位置
    public static String intentFrom = "";
    public static String saleType = "";
    public static boolean isTriggerFirebaseDatabase = false;
    public static boolean isEnabledCashModule = true;
    public static boolean isCashInventoryEmpty = false;
    //暫存會員ID
    public static String group_id = "";

    public static boolean useMultiBrand=false;
    @NotNull
    public static List<SaleMethod> saleMethods;
    public static boolean isRestaurant = true;
    public static boolean isShopping = false;
    public static boolean isEnabledCashPayment = false;

    public static String getAllConfig() {
        JsonObject jo = new JsonObject();
        jo.addProperty("ApiRoot", ApiRoot);
        jo.addProperty("cacheUrl", cacheUrl);
        jo.addProperty("linePayUrl", linePayUrl);
        jo.addProperty("authKey", authKey);
        jo.addProperty("accKey", acckey);
        jo.addProperty("shop_id", shop_id);
        jo.addProperty("acckeyDefault", acckeyDefault);
        jo.addProperty("kiosk_id", kiosk_id);
        jo.addProperty("creditCardComportPath", creditCardComportPath);
        jo.addProperty("creditCardBaudRate", creditCardBaudRate);
        jo.addProperty("useEasyCardByShop", useEasyCardByShop);
        jo.addProperty("useEasyCardByKiosk", useEasyCardByKiosk);
        jo.addProperty("useNcccByShop", useNcccByShop);
        jo.addProperty("useNcccByKiosk", useNcccByKiosk);
        jo.addProperty("usePiPay", usePiPay);
        jo.addProperty("useMultiBrand",useMultiBrand);
        jo.addProperty("isRestaurant",isRestaurant);
        jo.addProperty("isShopping",isShopping);
        jo.addProperty("enableCounter", enableCounter);
        jo.addProperty("disableReceiptModule", disableReceiptModule);
        jo.addProperty("disablePrinterModule", disablePrinterModule);
        jo.addProperty("disablePrinter", disablePrinter);
        jo.addProperty("isOnlyCounterPay", isOnlyCounterPay);
        jo.addProperty("isNewOrderApi", isNewOrderApi);
        jo.addProperty("address", address);
        jo.addProperty("isEnabledCashPayment", isEnabledCashPayment);
        jo.addProperty("addressDefault", addressDefault);
        jo.addProperty("ApiRequest Url", ApiRequest.getAllUrl());
        return jo.toString();
    }
}
