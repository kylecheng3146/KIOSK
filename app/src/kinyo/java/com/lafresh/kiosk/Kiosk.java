package com.lafresh.kiosk;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.lafresh.kiosk.dialog.MessageDialog;
import com.lafresh.kiosk.dialog.MessageDialogFragment;

public class Kiosk {
    public static int idleCount = 30;

    public static void navBar_prevent(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void hidePopupBars(Context ct) {
        ((Activity) ct).getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public static void hidePopupBars(Dialog dialog) {
        dialog.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    public static void showMessageDialog(Context context, String message) {
        showMessageDialog(context, message, null);
    }

    public static void showMessageDialog(Context context, String message, MessageDialog.Listener listener) {
        showMessageDialog(context, message, listener, 0);
    }

    public static void showMessageDialog(Context context, String message, MessageDialog.Listener listener, int second) {
        MessageDialog messageDialog = new MessageDialog(context);
        messageDialog.setMessage(message);
        messageDialog.setListener(listener);
        if (second > 0) {
            messageDialog.setDelayButton(second);
        }
        messageDialog.show();
    }

    public static void showMessageDialogFragment(Context context, String message, MessageDialogFragment.MessageDialogFragmentListener listener) {
        Activity activity = (Activity) context;
        MessageDialogFragment messageDialogFragment = new MessageDialogFragment();
        messageDialogFragment.setMessage(message);
        messageDialogFragment.setMessageDialogFragmentListener(listener);
        messageDialogFragment.show(activity.getFragmentManager(), "DIALOG");
    }

    public static void checkAndChangeUi(Context context, String imgPath, ImageView ivTarget) {
        if (imgPath != null) {
            CommonUtils.loadImage(context,ivTarget,imgPath)
        }
    }
}
