package com.lafresh.kiosk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lafresh.kiosk.Bill;
import com.lafresh.kiosk.BuildConfig;
import com.lafresh.kiosk.Config;
import com.lafresh.kiosk.activity.HomeActivity;
import com.lafresh.kiosk.Kiosk;
import com.lafresh.kiosk.R;
import com.lafresh.kiosk.activity.ShopActivity;
import com.lafresh.kiosk.printer.KioskPrinter;
import com.lafresh.kiosk.utils.ClickUtil;

import static com.lafresh.kiosk.utils.LogUtil.writeLogToLocalFile;

public class PrinterErrorDialog extends Dialog implements View.OnClickListener {
    private Listener listener;

    public PrinterErrorDialog(@NonNull Context context, Listener listener, String errorMessage) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Kiosk.hidePopupBars(this);
        setContentView(R.layout.dialog_printer_error);

        if (Bill.fromServer != null) {
            KioskPrinter.addLog("wOrderId=" + Bill.fromServer.worder_id);
            writeLogToLocalFile(KioskPrinter.printerStatus);
            KioskPrinter.printerStatus = null;
        }

        findViewById(R.id.btnBackHome).setOnClickListener(this);
        Button btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(this);


        String errorMsg;
        if (errorMessage != null) {
            errorMsg = errorMessage;
            if (errorMessage.equals(context.getString(R.string.printerPaperPresentAlert))) {
                if (!"mwd".equals(BuildConfig.FLAVOR)) {
                    btnConfirm.setText(context.getString(R.string.confirm));
                }
            }
        } else {
            errorMsg = context.getString(R.string.printerError);
        }

        if (listener != null && Bill.fromServer != null) {
            errorMsg += context.getString(R.string.orderIdIs);
            errorMsg = String.format(errorMsg, Bill.fromServer.worder_id);
        }

        TextView tvMessage = findViewById(R.id.tvMessage);
        tvMessage.setText(errorMsg);

        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.btnConfirm:
                if (listener != null) {
                    KioskPrinter.addLog("再次印單");
                    listener.onFinish();
                }
                dismiss();
                break;

            case R.id.btnBackHome:
                dismiss();
                if (ShopActivity.now != null) {
                    ShopActivity.now.Finish();
                }
                HomeActivity.now.closeAllActivities();
                break;

            default:
                break;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Kiosk.idleCount = Config.idleCount;
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        super.setOnDismissListener(listener);
    }

    public interface Listener {
        void onFinish();
    }
}
