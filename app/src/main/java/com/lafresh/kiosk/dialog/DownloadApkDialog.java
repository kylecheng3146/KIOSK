package com.lafresh.kiosk.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.lafresh.kiosk.Kiosk;
import com.lafresh.kiosk.R;
import com.lafresh.kiosk.manager.VersionCheckManager;

import org.jetbrains.annotations.NotNull;

public class DownloadApkDialog extends Dialog {
    private Activity activity;
    private ProgressBar pgb;


    public DownloadApkDialog(@NonNull Context context, String fileName) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_downloading);
        Kiosk.hidePopupBars(this);

        pgb = findViewById(R.id.pgb);


        this.activity = (Activity) context;

        downloadApk(fileName);
    }

    private void downloadApk(final String fileName) {
        final VersionCheckManager manager = VersionCheckManager.getManager();
        VersionCheckManager.Listener listener = new VersionCheckManager.Listener() {
            @Override
            public void onUpdate(@NotNull int percent) {
                pgb.setProgress(percent);
            }

            @Override
            public void onFinish(String result) {
                manager.installApk(fileName, activity);
                dismiss();
            }

            @Override
            public void isNewestVersion(boolean isNewestVersion) {
            }
        };
        manager.setListener(listener);
        manager.downloadApk(fileName);

    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception ignore) {

        }
    }
}
