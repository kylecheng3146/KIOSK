package com.lafresh.kiosk.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.lafresh.kiosk.Bill;
import com.lafresh.kiosk.CashDialog;
import com.lafresh.kiosk.Config;
import com.lafresh.kiosk.EasyCardActivity;
import com.lafresh.kiosk.HomeActivity;
import com.lafresh.kiosk.Kiosk;
import com.lafresh.kiosk.R;
import com.lafresh.kiosk.RobotActionCallback;
import com.lafresh.kiosk.ScanActivity;
import com.lafresh.kiosk.dialog.KeyboardDialog;
import com.lafresh.kiosk.manager.EasyCardManager;
import com.lafresh.kiosk.manager.OrderManager;
import com.lafresh.kiosk.utils.ClickUtil;
import com.lafresh.kiosk.utils.NcccUtils;
import com.nuwarobotics.service.IClientId;
import com.nuwarobotics.service.agent.NuwaRobotAPI;


import java.util.ArrayList;
import java.util.List;


public class CheckOutActivity extends BaseActivity implements View.OnClickListener, RobotActionCallback {
    public static final int P_EASY_CARD = 17;
    public static final int P_LINE_PAY = 93;
    public static final int P_NCCC = 8787;
    public static final int P_COUNTER = 66;
    public static final int P_PI_PAY = 487;
    private List<Integer> payments = new ArrayList<>();

    Button btn_vehicle, btn_donate;
    Button btnCash, btnEasyCard, btnLinePay;
    Button btnNccc;
    Button btnPiPay;
    Button btnBack;
    Button btnAddOne;
    Button btnAddTwo;
    LinearLayout llAddOne;
    LinearLayout llAddTwo;
    LinearLayout llPi;
    RelativeLayout rlMid,rlReceiptModule;
    EditText et_vehicle, et_donate, et_taxId;
    IntentIntegrator scanIntegrator;
    private NuwaRobotAPI mRobotAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRobotAPI = NuwaRobotAPI.getInst();

        if(mRobotAPI == null){
            mRobotAPI = new NuwaRobotAPI(this, new IClientId(getPackageName()));
        }

        countPayment();
        int layout;
        if (payments.size() > 1) {
            layout = R.layout.activity_check_out;
        } else {
            layout = R.layout.d_checkout;
        }
        setContentView(layout);

        HomeActivity.now.activities.add(this);
        Kiosk.hidePopupBars(this);
        setUI();
        setActions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        OrderManager manager = OrderManager.getInstance();
        Integer easyCardRetryTime = EasyCardManager.easyCardTryTime.get(manager.getOrderId());
        if (easyCardRetryTime == null) {
            EasyCardManager.easyCardTryTime.put(manager.getOrderId(), 0);
        } else if (easyCardRetryTime > 2) {
            disableEasyCardButton();
        }
        if (!EasyCardManager.getInstance().isEasyCardCheckout() || !Config.useEasyCardByKiosk) {
            disableEasyCardButton();
        }

        setNumbers();
        mRobotAPI.startTTS(getResources().getString(R.string.robot_sentence_pay_notice));
        mRobotAPI.motionPlay(getResources().getString(R.string.robot_pay_way_motion), false);
        mRobotAPI.startTTS(getResources().getString(R.string.robot_pay_kinds));

    }

    void setUI() {
        btn_vehicle = findViewById(R.id.btn_vehicle);
        btn_donate = findViewById(R.id.btn_donate);
        btnCash = findViewById(R.id.Btn_Cash);
        btnEasyCard = findViewById(R.id.Btn_Card);
        btnLinePay = findViewById(R.id.Btn_QRCode);
        btnNccc = findViewById(R.id.btnNccc);
        btnPiPay = findViewById(R.id.btnPiPay);
        llAddOne = findViewById(R.id.llAddOne);
        btnAddOne = findViewById(R.id.btnAddOne);
        if (payments.size() > 1) {
            btnAddTwo = findViewById(R.id.btnAddTwo);
            llAddTwo = findViewById(R.id.llAddTwo);
        }
        llPi = findViewById(R.id.llPi);
        if (Config.usePiPay) {
            llPi.setVisibility(View.VISIBLE);
        }
        btnBack = findViewById(R.id.btn_back);
        btnAddOne = findViewById(R.id.btnAddOne);

        et_donate = findViewById(R.id.et_donate);
        et_vehicle = findViewById(R.id.et_vehicle);
        et_donate = findViewById(R.id.et_donate);
        et_taxId = findViewById(R.id.et_taxId);
        rlMid = findViewById(R.id.rl_mid);
//        rlReceiptModule = findViewById(R.id.rl_receipt_module);

//        if(Config.disableReceiptModule){
//            rlMid.setVisibility(View.GONE);
//            rlReceiptModule.setVisibility(View.GONE);
//        }

        ImageView ivLogo = findViewById(R.id.ivLogo);
        Kiosk.checkAndChangeUi(this, Config.titleLogoPath, ivLogo);

        ImageView ivAd = findViewById(R.id.ivAd);
        Kiosk.checkAndChangeUi(this, Config.bannerImg, ivAd);
    }

    void setActions() {
        btn_donate.setOnClickListener(this);
        btn_vehicle.setOnClickListener(this);
        btnEasyCard.setOnClickListener(this);
        btnLinePay.setOnClickListener(this);
        btnPiPay.setOnClickListener(this);
        btnCash.setOnClickListener(this);

        findViewById(R.id.btn_taxId).setOnClickListener(this);
        findViewById(R.id.et_taxId).setOnClickListener(this);
        btnBack.setOnClickListener(this);

        if (payments.size() == 2) {
            setNcccButton(btnAddOne, llAddOne);
            setCounterPayButton(btnAddTwo, llAddTwo);
        } else if (payments.size() == 1) {
            if (payments.get(0) == P_COUNTER) {
                setCounterPayButton(btnAddOne, llAddOne);
            } else {
                setNcccButton(btnAddOne, llAddOne);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_vehicle:
                //掃描載具
                openScanner();
                break;
            case R.id.btn_donate:
                //掃描捐贈碼
                ScanActivity.scanType = ScanActivity.TYPE_DONATE;
                startActivity(new Intent(CheckOutActivity.this, ScanActivity.class));
                break;
            case R.id.et_taxId:
            case R.id.btn_taxId:
                //統一編號
                KeyboardDialog keyboardDialog = new KeyboardDialog(CheckOutActivity.this);
                keyboardDialog.type = KeyboardDialog.S_TaxId;
                keyboardDialog.tv_count.setText(et_taxId.getText().toString());
                keyboardDialog.show();
                keyboardDialog.listener = new KeyboardDialog.Listener() {
                    @Override
                    public void onEnter(int count, String text) {
                        et_donate.setText("");
                        et_taxId.setText(text);
                        Bill.fromServer.setNpoBan("");
                        Bill.fromServer.setCustCode(text);
                    }
                };
                break;
            case R.id.Btn_Card:
                startActivity(new Intent(CheckOutActivity.this, EasyCardActivity.class));
                break;
            case R.id.Btn_QRCode:
                ScanActivity.scanType = ScanActivity.TYPE_LINE_PAY;
                startActivity(new Intent(getApplicationContext(), ScanActivity.class));
                break;
            case R.id.btnPiPay:
                ScanActivity.scanType = ScanActivity.TYPE_PI_PAY;
                startActivity(new Intent(getApplicationContext(), ScanActivity.class));
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.Btn_Cash:
                new CashDialog(this).show();
                break;
            default:
                break;
        }
    }

    private void setNcccButton(final Button button, LinearLayout linearLayout) {
        linearLayout.setVisibility(View.VISIBLE);
        button.setText(getString(R.string.creditCard));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.isFastDoubleClick()) {
                    return;
                }
                startActivity(new Intent(getApplicationContext(), NCCCActivity.class));
            }
        });
    }

    private void setCounterPayButton(final Button button, LinearLayout linearLayout) {
        linearLayout.setVisibility(View.VISIBLE);
        button.setText(getString(R.string.counterButton));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.isFastDoubleClick()) {
                    return;
                }
                new CashDialog(CheckOutActivity.this).show();
            }
        });
    }

    private void countPayment() {
        if (NcccUtils.isUseNccc()) {
            payments.add(P_NCCC);
        }

        if (Config.enableCounter) {
            payments.add(P_COUNTER);
        }
    }

    private void setNumbers() {
        String buyerNo = Bill.fromServer.getBuyerNumber();
        String npoBan = Bill.fromServer.getNpoBan();
        String custCode = Bill.fromServer.getCustCode();
        et_donate.setText(npoBan);
        et_vehicle.setText(buyerNo);
        et_taxId.setText(custCode);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Kiosk.idleCount = Config.idleCount;
        return super.dispatchTouchEvent(ev);
    }

    public void onDestroy() {
//        Log.e("Act_CheckOut", "onDestroy");
        HomeActivity.now.activities.remove(this);
        super.onDestroy();
    }

    private void disableEasyCardButton() {
        btnEasyCard.setOnClickListener(null);
        btnEasyCard.setBackgroundResource(R.drawable.checkout_easycard);
    }

    private void disableNcccButton() {
        btnAddOne.setOnClickListener(null);
        btnAddOne.setBackgroundResource(R.drawable.btn_no_text_press);
    }

    private void openScanner(){
        scanIntegrator = new IntentIntegrator(CheckOutActivity.this);
        scanIntegrator.setPrompt("請掃描載具");
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            et_vehicle = findViewById(R.id.et_vehicle);
                            et_vehicle.setText(scanContent);
                        }
                    });
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
