package com.lafresh.kiosk;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.lafresh.kiosk.dialog.PrinterErrorDialog;
import com.lafresh.kiosk.manager.OrderManager;
import com.lafresh.kiosk.model.Order;
import com.lafresh.kiosk.printer.KioskPrinter;

public class PaidDialog extends Dialog {
    private Button btnPlay;
    private Button btnBack;
    private RelativeLayout rlCenter;
    private WebView wvGame;
    private CountDownTimer afterPrintTimer;
    private RobotActionCallback robotCallback;

    private Order order;

    public PaidDialog(final Context ct, Order order) {
        this(ct);
        this.order = order;
    }

    public PaidDialog(final Context ct) {
        super(ct, android.R.style.Theme_Translucent_NoTitleBar);
        ShopActivity.AL_DialogToClose.add(this);
        setContentView(R.layout.d_paid);

        robotCallback = (ScanActivity) ct;

        findViews();
        setAction();
        if (!Config.disablePrinterModule) {
            KioskPrinter.getPrinter().laterPrinterCheckFlow(ct, getPrinterCheckListener(), getAfterFlowListener(), 5);
        } else {
            btnBack.setVisibility(View.VISIBLE);
        }

    }

    private void findViews() {
        ImageView ivLogo = findViewById(R.id.ivLogo);
        Kiosk.checkAndChangeUi(getContext(), Config.titleLogoPath, ivLogo);

//        ImageView ivAd = findViewById(R.id.ivAd);
//        Kiosk.checkAndChangeUi(getContext(), Config.bannerImg, ivAd);

        btnBack = findViewById(R.id.btn_back);
        btnBack.setVisibility(View.INVISIBLE);
        btnPlay = findViewById(R.id.btnPlay);
        rlCenter = findViewById(R.id.rl_center);
        wvGame = findViewById(R.id.wvGame);
    }

    private void setAction() {
        HomeActivity.now.stopIdleProof();
        afterPrintTimer = new CountDownTimer(Config.idleCount_BillOK * 1000, 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                btnBack.performClick();
            }
        };

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KioskPrinter.addLog("付款完成後返回");
                if (afterPrintTimer != null) {
                    afterPrintTimer.cancel();
                }
                dismiss();
                ShopActivity.now.Finish();
                HomeActivity.now.closeAllActivities();
                triggerRobotFace();  //Trigger Robot face
            }
        });

//        btnPlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rlCenter.setVisibility(View.GONE);
//                btnPlay.setVisibility(View.GONE);
//                btnBack.setVisibility(View.VISIBLE);
//                wvGame.setVisibility(View.VISIBLE);
//                wvGame.getSettings().setJavaScriptEnabled(true);
//                wvGame.setWebViewClient(new WebViewClient());
//
//                String url = getContext().getResources().getString(R.string.gameUrl);
//                wvGame.loadUrl(url);
//            }
//        });
    }

    private PrinterErrorDialog.Listener getPrinterCheckListener() {
        return new PrinterErrorDialog.Listener() {
            @Override
            public void onFinish() {
                KioskPrinter.getPrinter().beforePrintCheckFlow(getContext(), getPrinterCheckListener(), getAfterCheckListener(getContext()));
            }
        };
    }


    private KioskPrinter.PrinterCheckFlowListener getAfterCheckListener(final Context context) {
        return new KioskPrinter.PrinterCheckFlowListener() {
            @Override
            public void onFinish() {
                //改為不印發票，只印收據
                KioskPrinter printer = KioskPrinter.getPrinter();
                if (Config.isNewOrderApi) {
                    OrderManager.getInstance().printOnlyReceipt(context, order);
                } else {
                    printer.printReceipt(context);
                }
                printer.laterPrinterCheckFlow(context, getPrinterCheckListener(), getAfterFlowListener(), 5);
            }
        };
    }

    private KioskPrinter.PrinterCheckFlowListener getAfterFlowListener() {
        return new KioskPrinter.PrinterCheckFlowListener() {
            @Override
            public void onFinish() {
                KioskPrinter.addLog("印單完成");
                btnBack.setVisibility(View.VISIBLE);
                afterPrintTimer.start();
            }
        };
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Kiosk.idleCount = Config.idleCount;
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(listener);
        ShopActivity.AL_DialogToClose.remove(this);
    }

    public void triggerRobotFace() {
        Intent goFaceIntent = new Intent();
        goFaceIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);
        goFaceIntent.setClassName("com.nuwarobotics.app.facepresenter",
                "com.nuwarobotics.app.facepresenter.FaceActivity");
        getContext().startActivity(goFaceIntent);
    }
}
