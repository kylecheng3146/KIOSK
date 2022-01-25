package com.lafresh.kiosk.utils;


import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.lafresh.kiosk.R;
import com.lafresh.kiosk.fragment.AlertDialogFragment;

public class AlertUtils {

    /**
     * Show msg by AlertDialog
     *
     * @param activity context
     * @param msg      msg
     */
    public static void showMsg(AppCompatActivity activity, String msg) {
        final AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        alertDialogFragment.show(activity.getFragmentManager(), AlertDialogFragment.MESSAGE_DIALOG);
    }

    public static void showMsgWithConfirmBlueTooth(final AppCompatActivity activity, int msg){
        final AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        alertDialogFragment
                .setMessage(msg)
                .setConfirmButton(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //連線藍芽頁面
                        ConnectUtils.connectionBluetooth(activity);
                        alertDialogFragment.dismiss();
                    }
                })
                .setCancelButton(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogFragment.dismiss();
                    }
                })
                .setUnCancelAble()
                .show(activity.getFragmentManager(), AlertDialogFragment.MESSAGE_DIALOG);
    }

    public static void showMsgWithConfirm(final AppCompatActivity activity, String msg){
        final AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        alertDialogFragment
                .setTitle("123")
                .setMessage(msg)
                .setConfirmButton(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialogFragment.dismiss();
                    }
                })
                .setUnCancelAble()
                .show(activity.getFragmentManager(), AlertDialogFragment.MESSAGE_DIALOG);
    }
}
