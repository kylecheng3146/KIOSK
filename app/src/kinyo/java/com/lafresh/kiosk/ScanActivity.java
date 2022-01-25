package com.lafresh.kiosk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.lafresh.kiosk.activity.BaseActivity;
import com.lafresh.kiosk.dialog.AlertDialogFragment;
import com.lafresh.kiosk.dialog.KeyboardDialog;
import com.lafresh.kiosk.httprequest.ApiRequest;
import com.lafresh.kiosk.httprequest.AskOrderApiRequest;
import com.lafresh.kiosk.httprequest.CheckTableApiRequest;
import com.lafresh.kiosk.httprequest.PayOrderApiRequest;
import com.lafresh.kiosk.httprequest.model.OrderResponse;
import com.lafresh.kiosk.httprequest.model.WebOrder01;
import com.lafresh.kiosk.model.AddOrderBean;
import com.lafresh.kiosk.pipay.AskOrder;
import com.lafresh.kiosk.pipay.PayOrder;
import com.lafresh.kiosk.pipay.PiRes;
import com.lafresh.kiosk.printer.KioskPrinter;
import com.lafresh.kiosk.utils.Arith;
import com.lafresh.kiosk.utils.ClickUtil;
import com.lafresh.kiosk.utils.ComputationUtils;
import com.lafresh.kiosk.utils.GZipUtils;
import com.lafresh.kiosk.utils.Json;
import com.lafresh.kiosk.utils.LogUtil;
import com.lafresh.kiosk.utils.TimeUtil;
import com.lafresh.kiosk.utils.VerifyUtil;
import com.nuwarobotics.service.IClientId;
import com.nuwarobotics.service.agent.NuwaRobotAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ScanActivity extends BaseActivity implements View.OnClickListener, RobotActionCallback {
    public static final int TYPE_LOGIN = 5;
    public static final int TYPE_DONATE = 66;
    public static final int TYPE_VEHICLE = 77;
    public static final int TYPE_LINE_PAY = 33;
    public static final int TYPE_TABLE_NO = 87;
    public static final int TYPE_PI_PAY = 9487;
    public static int scanType = TYPE_LOGIN;

    Button btn_back, btn_paste , btnOpenScanner;
    Button btnInputCode;
    EditText et_code;
    CountDownTimer countDownTimer;
    TextView tv_title1, tv_title2, tv_fail, tv_code;
    RelativeLayout rl_pasteCover;
    ProgressBar pgb;
    private D_Loading loading;
    long scanTime;
    String title1;
    String title2;
    IntentIntegrator scanIntegrator;

    LSN lsn;
    private NuwaRobotAPI mRobotAPI;

    interface LSN {
        void onScan(String code);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HomeActivity.now.activities.add(this);
        if (scanType == TYPE_LINE_PAY) {
            setContentView(R.layout.activity_line_pay);
        } else {
            setContentView(R.layout.act_scan);
        }
        setType();
        setUI();
        setActions();

        //Nuwa SDK init
        mRobotAPI = NuwaRobotAPI.getInst();

        if(mRobotAPI == null){
            mRobotAPI = new NuwaRobotAPI(this, new IClientId(getPackageName()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Kiosk.hidePopupBars(this);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    void setType() {
        if (scanType == TYPE_LOGIN) {
            scanTime = 50 * 20;
            title1 = getString(R.string.memberLogin);
            title2 = getString(R.string.loginMsg);

            lsn = new LSN() {
                @Override
                public void onScan(String code) {
                    loading.show();
                    login(code);
                }
            };
        }

        if (scanType == TYPE_VEHICLE) {
            scanTime = 10 * 20;
            title1 = getString(R.string.scanVehicle);
            title2 = getString(R.string.scanVehicleMsg);

            lsn = new LSN() {
                @Override
                public void onScan(String code) {
                    if (VerifyUtil.isPhoneCarrierNumber(code)) {
                        Bill.fromServer.setBuyerNumber(code);
                        finish();
                    }
                }
            };
        }

        if (scanType == TYPE_DONATE) {
            scanTime = 10 * 20;
            title1 = getString(R.string.scanDonateNo);
            title2 = getString(R.string.scanDonateNoMsg);
            lsn = new LSN() {
                @Override
                public void onScan(String code) {
                    if (VerifyUtil.isLoveCode(code)) {
                        Bill.fromServer.setNpoBan(code);
                        Bill.fromServer.setCustCode("");
                        finish();
                    }
                }
            };
        }

        if (scanType == TYPE_TABLE_NO) {
            scanTime = 125 * 20;
            title1 = getString(R.string.scanTableNoCode);
            title2 = getString(R.string.scanTableNoMsg);
            ImageView ivIcon = findViewById(R.id.iv_cellPhone);
            ivIcon.setImageDrawable(getDrawable(R.drawable.icon_qrcode));
            lsn = new LSN() {
                @Override
                public void onScan(String code) {
                    enableBtnBack(false);
                    tv_fail.setText("");
                    checkTableNoCode(code);
                }
            };
        }

        if (scanType == TYPE_LINE_PAY) {
            scanTime = 20 * 20;
            title1 = getString(R.string.scanLinePay);
            title2 = getString(R.string.scanLinePayMsg);
            lsn = new LSN() {
                @Override
                public void onScan(String code) {
                    enableBtnBack(false);
                    loading.show();
                    if (code.length() > 18) {
                        code = code.substring(0, 18);
                    }
                    linepay(code);
                }
            };
        }

        if (scanType == TYPE_PI_PAY) {
            scanTime = 20 * 20;
            title1 = getString(R.string.scanPiPay);
            title2 = getString(R.string.scanPiPayMsg);
            lsn = new LSN() {
                @Override
                public void onScan(String code) {
                    enableBtnBack(false);
                    loading.show();
                    if (code.length() > 18) {
                        code = code.substring(0, 18);
                    }
                    piPay(code);
                }
            };
        }
    }

    void setUI() {
        btnInputCode = findViewById(R.id.btnInputCode);
        btn_back = findViewById(R.id.btn_back);
        btn_paste = findViewById(R.id.btn_paste);
        et_code = findViewById(R.id.et_code);
        tv_title1 = findViewById(R.id.tv_title1);
        tv_title2 = findViewById(R.id.tv_title2);
        tv_code = findViewById(R.id.tv_code);
        tv_fail = findViewById(R.id.tv_fail);
        btnOpenScanner = findViewById(R.id.btn_open_scanner);
        rl_pasteCover = findViewById(R.id.rl_pasteCover);

        pgb = findViewById(R.id.pgb);
        tv_fail.setVisibility(View.INVISIBLE);
        pgb.setVisibility(View.INVISIBLE);
        tv_title1.setText(title1);
        tv_title2.setText(title2);
        btnOpenScanner.setOnClickListener(this);

        loading = new D_Loading(ScanActivity.this);

        ImageView ivLogo = findViewById(R.id.ivLogo);
        Kiosk.checkAndChangeUi(this, Config.titleLogoPath, ivLogo);

        ImageView ivAd = findViewById(R.id.ivAd);
        Kiosk.checkAndChangeUi(this, Config.bannerImg, ivAd);
    }

    @SuppressLint("ClickableViewAccessibility")
    void setActions() {
        if (scanType == TYPE_DONATE) {
            btnInputCode.setVisibility(View.VISIBLE);
            btnInputCode.setOnClickListener(this);
        }

        enableBtnBack(true);

        et_code.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) et_code.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null && et_code.hasSelection()) {
                    imm.hideSoftInputFromWindow(et_code.getWindowToken(), 0);
                }
                return true;
            }
        });
        et_code.setTextSize(1);

        et_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, final int start, int count, int after) {
                if (start == 0 && after == 1) {
                    countDownTimer = new CountDownTimer(scanTime, scanTime) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            String code = et_code.getText().toString().trim();
                            et_code.setText("");
                            if (lsn != null) {
                                lsn.onScan(code);
                            }
                        }
                    }.start();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.btn_open_scanner:
                openScanner();
                break;
            case R.id.btnInputCode:
                KeyboardDialog keyboard = new KeyboardDialog(ScanActivity.this);
                keyboard.type = KeyboardDialog.DONATE_CODE;
                keyboard.listener = new KeyboardDialog.Listener() {
                    @Override
                    public void onEnter(int count, String text) {
                        lsn.onScan(text);
                    }
                };
                keyboard.show();
                break;
            case R.id.btn_back:
                lsn = null;
                finish();
                break;
            default:
                break;
        }
    }

    void login(final String code) {
        Post post = new Post(ScanActivity.this, Config.ApiRoot + "/webservice/member/index.php");
        post.AddField("authkey", Config.authKey);
        post.AddField("op", "DecVirtualCard");
        post.AddField("card_no", code);

        post.SetPostListener(new Post.PostListener() {
            @Override
            public void OnFinish(String Response) {
                try {
                    JSONObject jo = new JSONObject(Response);
                    if (jo.getInt("code") == 0 && !jo.getString("acckey").equals("null")) {
                        Config.acckey = jo.getString("acckey");
                        Bill.Now.isMember = true;
                        finish();
                    } else {
                        tv_fail.setText(getString(R.string.loginFail));
                        tv_fail.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (loading != null && loading.isShowing()) {
                        loading.cancel();
                    }
                }
            }
        });
        post.GO();
    }

    void checkTableNoCode(String code) {
        String failMsg = getString(R.string.scanFail) + "\n";
        boolean isGoodCode = code.contains("authkey") && code.contains("table_no") && code.contains("shop_id");
        if (isGoodCode) {
            String authKey = getDataFromUrl("authkey", code);
            String shopId = getDataFromUrl("shop_id", code);
            final String tableNo = getDataFromUrl("table_no", code);
            if (tableNo.length() > 5) {
                failMsg += getString(R.string.reScanMsg);
                showFailMessage(failMsg);
                return;
            }

            if (Config.authKey.equals(authKey) && Config.shop_id.equals(shopId)) {
                ApiRequest.ApiListener listener = new ApiRequest.ApiListener() {
                    @Override
                    public void onSuccess(ApiRequest apiRequest, String body) {
                        try {
                            JSONObject jsonObject = new JSONObject(body);
                            if (jsonObject.getInt("code") == 0) {
                                Bill.Now.setTableNo(tableNo);
                                Intent intent = new Intent(ScanActivity.this, ShopActivity.class);
                                startActivity(intent);
                            } else {
                                onFail();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            FirebaseCrashlytics.getInstance().recordException(e);
                        }
                    }

                    @Override
                    public void onFail() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showFailMessage(getString(R.string.tableNoError));
                            }
                        });
                    }
                };
                new CheckTableApiRequest(tableNo).setApiListener(listener).go();
            } else {
                failMsg += getString(R.string.tableNoCodeError);
                showFailMessage(failMsg);
            }
        } else {
            failMsg += getString(R.string.reScanMsg);
            showFailMessage(failMsg);
        }
        enableBtnBack(true);
    }

    String getDataFromUrl(String key, String url) {
        if (url.contains(key)) {
            int begin = url.indexOf(key);
            if (begin > 0) {
                url = url.substring(begin + key.length() + 1);
            } else {
                return "";
            }
            int end = url.indexOf("&");
            if (end > 0) {
                return url.substring(0, end);
            } else {
                return url;
            }
        } else {
            return "";
        }
    }

    void showFailMessage(String msg) {
        tv_fail.setText(msg);
        tv_fail.setVisibility(View.VISIBLE);
    }

    void linepay(final String oneTimeKey) {
        HttpRequest httpRequest = new HttpRequest(Config.linePayUrl);

        try {
            String currencySymbol = getString(R.string.currencySymbol);
            httpRequest.jo.put("amount", Bill.fromServer.total);
            httpRequest.jo.put("orderId", Bill.fromServer.worder_id);
            httpRequest.jo.put("currency", currencySymbol);
            httpRequest.jo.put("oneTimeKey", oneTimeKey);
            httpRequest.jo.put("authkey", Config.authKey);
            httpRequest.jo.put("MerchantDeviceProfileId", Config.shop_id + "-" + Config.kiosk_id);
        } catch (Exception e) {
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        httpRequest.lsn = new HttpRequest.LSN() {
            @Override
            public void onFinish(String response) {
                try {
                    JSONObject jo = new JSONObject(response);
                    if (jo.getString("returnCode").equals("0000")) {
                        JSONObject jo_info = jo.getJSONObject("info");
                        String transactionId = jo_info.getString("transactionId");
                        Bill.fromServer.weborder02_setLinePay(transactionId);
                        submitOrder(Bill.S_PAY_MENT_LINE_PAY);
                    } else {
                        String scanError = getString(R.string.scanError);
                        tv_fail.setVisibility(View.VISIBLE);
                        tv_fail.setText(scanError);
                        enableBtnBack(true);
                        FirebaseCrashlytics.getInstance().setUserId(Bill.fromServer.worder_id);
                        FirebaseCrashlytics.getInstance().log("ScanActivity-LINE Response = " + response);
                        FirebaseCrashlytics.getInstance().log("ScanActivity-OrderId = " + Bill.fromServer.worder_id);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    FirebaseCrashlytics.getInstance().recordException(e);
                    enableBtnBack(true);
                } finally {
                    if (loading != null && loading.isShowing()) {
                        loading.cancel();
                    }
                }
            }
        };
        httpRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void piPay(String barcode) {
        PayOrder payOrder = new PayOrder();
        payOrder.setAuthkey(Config.authKey);
        payOrder.setShop_id(Config.shop_id);
        payOrder.setReg_id(Config.kiosk_id);
        payOrder.setBill_id(Bill.fromServer.worder_id);
        payOrder.setSeq_no(Bill.fromServer.worder_id);
        payOrder.setBarcode(barcode);
        payOrder.setAmt(Bill.fromServer.total);
        payOrder.setItems(getPiPayItems());
        payOrder.setCreate_time(TimeUtil.getNowTime("yyyyMMddHHmmss"));
        ApiRequest.ApiListener listener = new ApiRequest.ApiListener() {
            @Override
            public void onSuccess(ApiRequest apiRequest, String body) {
                LogUtil.writeLogToLocalFile("訂單編號 = " + Bill.fromServer.worder_id + "\nPi 拍錢包 回傳 = " + body);
                PiRes piRes = Json.fromJson(body, PiRes.class);
                if (piRes.getWallet_transaction_id() != null) {
                    //直接查訂單
                    piAskOrder(piRes.getWallet_transaction_id());
                } else {
                    piErrorHandle(piRes);
                }
            }

            @Override
            public void onFail() {
                piErrorHandle(null);
            }
        };
        new PayOrderApiRequest(Json.toJson(payOrder)).setApiListener(listener).go();
    }

    private void piAskOrder(String transactionId) {
        AskOrder askOrder = new AskOrder();
        askOrder.setAuthkey(Config.authKey);
        askOrder.setShop_id(Config.shop_id);
        askOrder.setBill_id(Bill.fromServer.worder_id);
        askOrder.setWallet_transaction_id(transactionId);
        ApiRequest.ApiListener listener = new ApiRequest.ApiListener() {
            @Override
            public void onSuccess(ApiRequest apiRequest, String body) {
                LogUtil.writeLogToLocalFile("訂單編號 = " + Bill.fromServer.worder_id + "\nPi 拍錢包 回傳 = " + body);
                PiRes piRes = Json.fromJson(body, PiRes.class);
                if ("success".equals(piRes.getStatus()) && "accepted".equals(piRes.getTrans_status())) {
                    Bill.fromServer.setPiWebOrder02(piRes);
                    submitOrder(Bill.S_PAYMENT_PI);
                } else {
                    piErrorHandle(piRes);
                }
            }

            @Override
            public void onFail() {
                piErrorHandle(null);
            }
        };
        new AskOrderApiRequest(Json.toJson(askOrder)).setApiListener(listener).go();
    }

    private void submitOrder(final String type) {
        if (loading.isShowing()) {
            loading.cancel();
        }
        runOnUiThread(new Runnable() {//處理執行緒問題
            @Override
            public void run() {
                Bill.fromServer.lsn = new Bill.LSN() {
                    @Override
                    public void onSubmitOk(String response) {
                        try {
                            OrderResponse orderResponse = Json.fromJson(response, OrderResponse.class);
                            if (orderResponse.getCode() == 0) {
                                tv_fail.setVisibility(View.INVISIBLE);
                                tv_title1.setText(getString(R.string.paid));
                                tv_title2.setText(getString(R.string.takeReceipts));

                                Bill.fromServer.print(ScanActivity.this);

//                                if (orderResponse.getInvoice() == null && !Config.disableReceiptModule) {
//                                    new NoInvoiceDialog(Act_Scan.this).show();
//                                } else {
                                    new PaidDialog(ScanActivity.this).show();
//                                }
                                  doEndingRobotAction();
                            } else {
                                String codeMsg = getString(R.string.paidErrorCode);
                                codeMsg = String.format(codeMsg, orderResponse.getCode());
                                String message = orderResponse.getMessage();
                                if (message == null) {
                                    message = getString(R.string.scanPayError);
                                }

                                tv_fail.setVisibility(View.VISIBLE);
                                tv_fail.setText(message);

//                                String errMsg = codeMsg + getString(R.string.paidError)
//                                        + Bill.fromServer.worder_id;

                                AddOrderBean addOrderBean = Bill.fromServer.constructAddOrderData();
                                String beanStr = Json.toJson(addOrderBean);
                                String encodeStr = GZipUtils.compress(beanStr);

                                String orderIdInfo = getString(R.string.orderIdIs);
                                orderIdInfo = String.format(orderIdInfo, Bill.fromServer.worder_id);
                                String dialogMsg = getString(R.string.orderFailMsg) + "\n" + getString(R.string.qrCodeHandleMsg) + "\n" + orderIdInfo;
                                final AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
                                alertDialogFragment.setTitle(codeMsg)
                                        .setMessage(dialogMsg)
                                        .setConfirmButton(R.string.retry, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (!ClickUtil.isFastDoubleClick()) {
                                                    alertDialogFragment.dismiss();
                                                    Bill.fromServer.retrySubmit(ScanActivity.this);
                                                    KioskPrinter.addLog("掃碼付款，失敗重試");
                                                }
                                            }
                                        })
                                        .setCancelButton(R.string.returnHomeButton, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (!ClickUtil.isFastDoubleClick()) {
                                                    KioskPrinter.addLog("掃碼付款，失敗返回");
                                                    alertDialogFragment.dismiss();
                                                    ShopActivity.now.Finish();
                                                    HomeActivity.now.closeAllActivities();
                                                }
                                            }
                                        })
                                        .setQrCode(encodeStr)
                                        .setUnCancelAble()
                                        .show(getFragmentManager(), AlertDialogFragment.MESSAGE_DIALOG);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            FirebaseCrashlytics.getInstance().recordException(e);
                            enableBtnBack(true);
                        }
                    }
                };
                HomeActivity.now.stopIdleProof();
                Bill.fromServer.submit(ScanActivity.this, type);
            }
        });
    }


    private String getPiPayItems() {
        String items = "";
        List<WebOrder01> weborder01List = Bill.fromServer.getOrderResponse().getWeborder01();
        for (WebOrder01 webOrder01 : weborder01List) {
            String productName = webOrder01.getProd_name1();
            String qty = webOrder01.getQty();
            String salePrice = webOrder01.getSale_price();
            double q = ComputationUtils.parseDouble(qty);
            double p = ComputationUtils.parseDouble(salePrice);
            String price = String.valueOf(Arith.mul(q, p));
            String item = productName + "*" + q + " $" + price;
            if (items.length() > 0) {
                items += ",";
            }
            items += item;
        }
        return items;
    }

    private void piErrorHandle(final PiRes piRes) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enableBtnBack(true);
                tv_fail.setVisibility(View.VISIBLE);
                String errMsg = getString(R.string.scanError);
                if (piRes != null) {
                    errMsg = piRes.getError_desc();

                    FirebaseCrashlytics.getInstance().log("Pi 拍錢包 回傳 = " + Json.toJson(piRes));
                }
                FirebaseCrashlytics.getInstance().setUserId(Bill.fromServer.worder_id);
                FirebaseCrashlytics.getInstance().log("PI 拍錢包 Error");
                FirebaseCrashlytics.getInstance().log("訂單編號 = " + Bill.fromServer.worder_id);
                String logMsg = "訂單編號 = " + Bill.fromServer.worder_id + "\nPi 拍錢包 回傳 = " + Json.toJson(piRes);
                LogUtil.writeLogToLocalFile(logMsg);
                tv_fail.setText(errMsg);
                if (loading.isShowing()) {
                    loading.cancel();
                }
            }
        });
    }

    public void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        HomeActivity.now.activities.remove(this);
        super.onDestroy();
    }

    private void enableBtnBack(boolean b) {
        if (b) {
            btn_back.setOnClickListener(this);
            btn_back.setVisibility(View.VISIBLE);
        } else {
            btn_back.setOnClickListener(null);
            btn_back.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserInteraction() {
        Kiosk.idleCount = Config.idleCount;
    }

    private void openScanner(){
        scanIntegrator = new IntentIntegrator(ScanActivity.this);
        scanIntegrator.setPrompt("請掃描LINE PAY");
        scanIntegrator.setTimeout(10000);
        scanIntegrator.setOrientationLocked(false);
        scanIntegrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            if(scanningResult.getContents() != null) {
                final String scanContent = scanningResult.getContents();
                if (!scanContent.equals("")) {
                    linepay(scanContent);
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, intent);
            Toast.makeText(getApplicationContext(),"發生錯誤",Toast.LENGTH_LONG).show();
        }
    }

    public void doEndingRobotAction() {
        mRobotAPI.startTTS(getResources().getString(R.string.robot_sentence_ending));
        mRobotAPI.motionPlay(getResources().getString(R.string.robot_ending_motion), false);
    }
}
