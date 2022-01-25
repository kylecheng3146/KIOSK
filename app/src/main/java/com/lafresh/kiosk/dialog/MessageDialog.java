package com.lafresh.kiosk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lafresh.kiosk.Config;
import com.lafresh.kiosk.Kiosk;
import com.lafresh.kiosk.R;
import com.lafresh.kiosk.utils.ClickUtil;

public class MessageDialog extends Dialog {
    private TextView tvMessage;
    private Listener listener;

    public MessageDialog(@NonNull Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Kiosk.hidePopupBars(this);
        setContentView(R.layout.dialog_message);

        Button btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.isFastDoubleClick()) {
                    return;
                }
                dismiss();
                if (listener != null) {
                    listener.onFinish();
                }
            }
        });

        tvMessage = findViewById(R.id.tvMessage);
    }

    public void setMessage(String message) {
        //字太長縮小
        if (message.length() > 10) {
            tvMessage.setTextSize(40);
        }
        tvMessage.setText(message);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setDelayButton(int second) {
        //延後按鈕出現時間，避免使用者立刻按掉
        final Button btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setVisibility(View.GONE);
        CountDownTimer countDownTimer = new CountDownTimer(second * 1000, second * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                btnConfirm.setVisibility(View.VISIBLE);
            }
        };
        countDownTimer.start();
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

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception ignore) {

        }
    }
}
