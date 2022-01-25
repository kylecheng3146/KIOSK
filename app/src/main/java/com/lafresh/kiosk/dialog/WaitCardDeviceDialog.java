package com.lafresh.kiosk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.Window;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.lafresh.kiosk.Kiosk;
import com.lafresh.kiosk.R;

public class WaitCardDeviceDialog extends Dialog {
    private ProgressBar pgb;
    private Listener listener;

    public WaitCardDeviceDialog(@NonNull Context context, int waitSecond) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_wait_card_device);
        Kiosk.hidePopupBars(this);

        pgb = findViewById(R.id.pgb);
        pgb.setMax(waitSecond);

        startWait(waitSecond);
    }

    private void startWait(int waitSecond) {
        new CountDownTimer(waitSecond * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                pgb.setProgress(pgb.getProgress() + 1);
            }

            @Override
            public void onFinish() {
                pgb.setProgress(pgb.getMax());
                listener.onFinish(WaitCardDeviceDialog.this);
            }
        }.start();
    }


    public WaitCardDeviceDialog setListener(Listener listener) {
        this.listener = listener;
        return this;
    }

    public interface Listener {
        void onFinish(WaitCardDeviceDialog dialog);
    }
}
