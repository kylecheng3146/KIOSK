package com.lafresh.kiosk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lafresh.kiosk.Bill;
import com.lafresh.kiosk.Config;
import com.lafresh.kiosk.activity.HomeActivity;
import com.lafresh.kiosk.Kiosk;
import com.lafresh.kiosk.R;
import com.lafresh.kiosk.activity.ShopActivity;
import com.lafresh.kiosk.httprequest.model.OrderResponse;
import com.lafresh.kiosk.printer.KioskPrinter;
import com.lafresh.kiosk.utils.Json;

public class PrintReceiptDialog extends Dialog {
    private Context context;
    private Button btnBack;
    private TextView tvTitle;
    private TextView tvMsg;
    private TextView tvFail;

    private CountDownTimer afterPrintTimer;

    public PrintReceiptDialog(@NonNull Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Kiosk.hidePopupBars(this);
        setContentView(R.layout.dialog_print_receipt);
        ShopActivity.AL_DialogToClose.add(this);
        this.context = context;

        findViews();
        setAction();

        submitOrder();
    }

    private void findViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        tvMsg = findViewById(R.id.tvMsg);
        tvFail = findViewById(R.id.tvFail);

        ImageView ivLogo = findViewById(R.id.ivLogo);
        Kiosk.checkAndChangeUi(getContext(), Config.titleLogoPath, ivLogo);

        ImageView ivAd = findViewById(R.id.ivAd);
        Kiosk.checkAndChangeUi(getContext(), Config.bannerImg, ivAd);
    }

    private void setAction() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        afterPrintTimer = new CountDownTimer(Config.idleCount_BillOK * 1000, 1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                btnBack.performClick();
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

    private void submitOrder() {
        Bill.fromServer.setListener(new Bill.LSN() {
            @Override
            public void onSubmitOk(String response) {
                HomeActivity.now.stopIdleProof();

                OrderResponse orderResponse = Json.fromJson(response, OrderResponse.class);
                if (orderResponse.getCode() == 0) {
                    tvTitle.setText(context.getString(R.string.billOk));
                    tvMsg.setText(context.getString(R.string.takeReceipt));

                    setOnDismissListener(new OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (afterPrintTimer != null) {
                                afterPrintTimer.cancel();
                            }
                            ShopActivity.now.Finish();
                            HomeActivity.now.closeAllActivities();
                        }
                    });

                    Bill.fromServer.printZeroReceipt(context);
                    KioskPrinter.getPrinter().laterPrinterCheckFlow(context, getPrinterCheckListener(), getAfterFlowListener(), 5);

                } else {
                    String errorMsg = orderResponse.getMessage();
                    if (errorMsg == null || errorMsg.equals("")) {
                        errorMsg = context.getString(R.string.errorOccur) + "\n" + context.getString(R.string.connectionError);
                    }

                    tvFail.setVisibility(View.VISIBLE);
                    tvFail.setText(errorMsg);
                    btnBack.setVisibility(View.VISIBLE);
                }
            }
        });
        Bill.fromServer.submit(context, Bill.S_PAYMENT_ZERO);
    }

    private PrinterErrorDialog.Listener getPrinterCheckListener() {
        return new PrinterErrorDialog.Listener() {
            @Override
            public void onFinish() {
                KioskPrinter.getPrinter().beforePrintCheckFlow(context, getPrinterCheckListener(), getAfterCheckListener(context));
            }
        };
    }


    private KioskPrinter.PrinterCheckFlowListener getAfterCheckListener(final Context context) {
        return new KioskPrinter.PrinterCheckFlowListener() {
            @Override
            public void onFinish() {
                Bill.fromServer.printZeroReceipt(context);
                KioskPrinter.getPrinter().laterPrinterCheckFlow(context, getPrinterCheckListener(), getAfterFlowListener(), 5);
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
}
