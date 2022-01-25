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

public class NoInvoiceDialog extends Dialog implements View.OnClickListener {
    private Button btnConfirm;
    private TextView tvMessage;

    public NoInvoiceDialog(@NonNull Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Kiosk.hidePopupBars(this);
        setContentView(R.layout.dialog_printer_error);
        ShopActivity.AL_DialogToClose.add(this);

        if (KioskPrinter.printerStatus != null) {
            KioskPrinter.addLog("wOrderId=" + Bill.fromServer.worder_id);
            writeLogToLocalFile(KioskPrinter.printerStatus);
            KioskPrinter.printerStatus = null;
        }
        findViews();
    }

    private void findViews() {
        btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(this);
        if (!"".equals(BuildConfig.FLAVOR)) {
            btnConfirm.setText(getContext().getString(R.string.informed));
        }
        findViewById(R.id.btnBackHome).setOnClickListener(this);
        tvMessage = findViewById(R.id.tvMessage);
        String msg = getContext().getString(R.string.noInvoiceErrMsg) + Bill.fromServer.worder_id;
        tvMessage.setText(msg);
    }

    @Override
    public void onClick(View v) {
        if (ClickUtil.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.btnConfirm:
            case R.id.btnBackHome:
                KioskPrinter.addLog("?????????");
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
        ShopActivity.AL_DialogToClose.remove(this);
    }
}
