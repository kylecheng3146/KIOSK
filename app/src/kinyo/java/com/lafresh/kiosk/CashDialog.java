package com.lafresh.kiosk;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.lafresh.kiosk.activity.CheckOutActivity;
import com.lafresh.kiosk.dialog.AlertDialogFragment;
import com.lafresh.kiosk.dialog.PrinterErrorDialog;
import com.lafresh.kiosk.httprequest.model.GetOrderInfoRes;
import com.lafresh.kiosk.model.AddOrderBean;
import com.lafresh.kiosk.printer.KioskPrinter;
import com.lafresh.kiosk.utils.ClickUtil;
import com.lafresh.kiosk.utils.GZipUtils;
import com.lafresh.kiosk.utils.Json;

import pl.droidsonroids.gif.GifImageView;

public class CashDialog extends Dialog {
    private Button btn_back;
    private TextView tv_title1, tv_title2;
    private ProgressBar pgb;
    private ImageView iv_centerLeft;
    private GifImageView payForCash;
//    private TextView tv_fail;

//    private Button btnPlay;
//    private WebView wvGame;
//    private RelativeLayout rlCenter;

    private CountDownTimer afterPrintTimer;
    private RobotActionCallback robotCallback;


    public CashDialog(final Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Kiosk.hidePopupBars(this);
        setContentView(R.layout.d_cash);
        ShopActivity.AL_DialogToClose.add(this);

        robotCallback = (CheckOutActivity) context;
        findViews();
        setAction();

        Bill.fromServer.submit(context, Bill.S_PAY_MENT_CASH);
        Bill.fromServer.lsn = new Bill.LSN() {
            @Override
            public void onSubmitOk(String response) {
                pgb.setVisibility(View.GONE);
                iv_centerLeft.setVisibility(View.GONE);
                //只取code, message 用任何一個有這倆屬性的class映射即可
                GetOrderInfoRes res = Json.fromJson(response, GetOrderInfoRes.class);
                if (res.getCode() == 0) {
                    payForCash.setVisibility(View.VISIBLE);
//                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
//                    params.setMargins(350,10,0,0);
//                    tv_title1.setLayoutParams(params);
//                    tv_title2.setLayoutParams(params);
                    tv_title1.setText(context.getString(R.string.billOk));
                    tv_title2.setText("請從下方取出付款單 至櫃台結帳");
//                    tv_title2.setText(context.getString(R.string.takeBill));

                    Bill.fromServer.print(context);
                    if (!Config.disablePrinterModule) {
                        KioskPrinter.getPrinter().laterPrinterCheckFlow(context, getPrinterCheckListener(context), getAfterFlowListener(), 5);
                    } else {
                        btn_back.setVisibility(View.VISIBLE);
                    }

                    robotCallback.doEndingRobotAction();
//                        setExhibitionAction(cdt);
                } else {
                    String codeMsg = context.getString(R.string.paidErrorCode);
                    codeMsg = String.format(codeMsg, res.getCode());
                    String msg = res.getMessage();
                    String orderIdInfo = context.getString(R.string.orderIdIs);
                    orderIdInfo = String.format(orderIdInfo, Bill.fromServer.worder_id);
                    if (msg == null || "".equals(msg)) {
                        msg = context.getString(R.string.errorOccur) + "\n" + context.getString(R.string.connectionError);
                    }
                    msg = codeMsg + msg;
                    msg += "\n" + orderIdInfo;
                    tv_title1.setText("送單失敗");
                    tv_title2.setText(msg);

                    AddOrderBean addOrderBean = Bill.fromServer.constructAddOrderData();
                    String beanStr = Json.toJson(addOrderBean);
                    String encodeStr = GZipUtils.compress(beanStr);

                    String dialogMsg = context.getString(R.string.orderFailMsg) + "\n" + context.getString(R.string.qrCodeHandleMsg) + "\n" + orderIdInfo;
                    final AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
                    alertDialogFragment.setTitle(codeMsg)
                            .setMessage(dialogMsg)
                            .setConfirmButton(R.string.retry, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!ClickUtil.isFastDoubleClick()) {
                                        alertDialogFragment.dismiss();
                                        Bill.fromServer.retrySubmit(context);
                                        KioskPrinter.addLog("櫃檯結帳重試");
                                    }
                                }
                            })
                            .setCancelButton(R.string.returnHomeButton, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!ClickUtil.isFastDoubleClick()) {
                                        KioskPrinter.addLog("櫃檯結帳失敗返回");
                                        alertDialogFragment.dismiss();
                                        ShopActivity.now.Finish();
                                        HomeActivity.now.closeAllActivities();
                                    }
                                }
                            })
                            .setQrCode(encodeStr)
                            .setUnCancelAble()
                            .show(((Activity) context).getFragmentManager(), AlertDialogFragment.MESSAGE_DIALOG);
                }
            }
        };
    }

    private void findViews() {
        btn_back = findViewById(R.id.btn_back);
        btn_back.setVisibility(View.GONE);
        tv_title1 = findViewById(R.id.tv_title1);
        payForCash = findViewById(R.id.pay_for_cash);
        tv_title2 = findViewById(R.id.tv_title2);
        iv_centerLeft = findViewById(R.id.iv_centerLeft);
        pgb = findViewById(R.id.pgb);
        pgb.setVisibility(View.GONE);
        payForCash.setVisibility(View.GONE);
        payForCash.setImageResource(R.drawable.receipt);

//        btnPlay = findViewById(R.id.btnPlay);
//        wvGame = findViewById(R.id.wvGame);

        ImageView ivLogo = findViewById(R.id.ivLogo);
        Kiosk.checkAndChangeUi(getContext(), Config.titleLogoPath, ivLogo);

        ImageView ivAd = findViewById(R.id.ivAd);
        Kiosk.checkAndChangeUi(getContext(), Config.bannerImg, ivAd);
    }

    private void setAction() {
        HomeActivity.now.stopIdleProof();
        afterPrintTimer = new CountDownTimer(Config.idleCount_BillOK * 1000, 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                btn_back.performClick();
            }
        };
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClickUtil.isFastDoubleClick()) {
                    return;
                }
                if (afterPrintTimer != null) {
                    afterPrintTimer.cancel();
                    afterPrintTimer = null;
                }
                KioskPrinter.addLog("櫃檯結帳正常返回");
                dismiss();
                ShopActivity.now.Finish();
                HomeActivity.now.closeAllActivities();
//                KioskPrinter.getPrinter().releaseAll(getContext());
//                KioskPrinter.releasePrinter();
                triggerRobotFace();  //Trigger Robot face
            }
        });
    }

    private PrinterErrorDialog.Listener getPrinterCheckListener(final Context context) {
        return new PrinterErrorDialog.Listener() {
            @Override
            public void onFinish() {
                KioskPrinter.getPrinter().beforePrintCheckFlow(context, getPrinterCheckListener(context), getAfterCheckListener(context));
            }
        };
    }

    private KioskPrinter.PrinterCheckFlowListener getAfterCheckListener(final Context context) {
        return new KioskPrinter.PrinterCheckFlowListener() {
            @Override
            public void onFinish() {
                Bill.fromServer.print(context);
                KioskPrinter.getPrinter().laterPrinterCheckFlow(context, getPrinterCheckListener(context), getAfterFlowListener(), 5);
            }
        };
    }

    private KioskPrinter.PrinterCheckFlowListener getAfterFlowListener() {
        return new KioskPrinter.PrinterCheckFlowListener() {
            @Override
            public void onFinish() {
                KioskPrinter.addLog("印單完成");
                btn_back.setVisibility(View.VISIBLE);
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

//    public void setExhibitionAction(final CountDownTimer cdt) {
//        btn_back.setVisibility(View.GONE);
//        btnPlay.setVisibility(View.VISIBLE);
//        btnPlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                btnPlay.setVisibility(View.GONE);
//                rlCenter.setVisibility(View.GONE);
//                rlFail.setVisibility(View.GONE);
//                btn_back.setVisibility(View.VISIBLE);
//                btn_back.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (cdt != null) {
//                            cdt.cancel();
//                        }
//                        dismiss();
//                        if (Act_Shop.Now != null) {
//                            Act_Shop.Now.Finish();
//                        }
//                        Act_AD.Now.Act_CloseAll();
//                    }
//                });
//
//                wvGame.setVisibility(View.VISIBLE);
//                wvGame.getSettings().setJavaScriptEnabled(true);
//                wvGame.setWebViewClient(new WebViewClient());
//
//                String url = getContext().getResources().getString(R.string.gameUrl);
//                wvGame.loadUrl(url);
//            }
//        });
//
//    }
}
