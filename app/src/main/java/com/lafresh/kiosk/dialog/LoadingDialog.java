package com.lafresh.kiosk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.Window;

import com.lafresh.kiosk.Config;
import com.lafresh.kiosk.Kiosk;
import com.lafresh.kiosk.R;


public class LoadingDialog extends Dialog {

    public LoadingDialog(final Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.d_loading);
        Kiosk.hidePopupBars(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Kiosk.idleCount = Config.idleCount;
        return super.dispatchTouchEvent(ev);
    }
}
