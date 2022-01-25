package com.lafresh.kiosk.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.lafresh.kiosk.BuildConfig;
import com.lafresh.kiosk.R;
import com.lafresh.kiosk.utils.QrcodeGender;


public class AlertDialogFragment extends DialogFragment {
    private int title;
    private String titleStr;
    private int message;
    private String messageStr;
    private String subMessageStr;
    private int confirmMessage;
    private int customMessage;
    private int cancelMessage;
    private String qrCodeData;

    private View.OnClickListener confirmListener;
    private View.OnClickListener cancelListener;
    private View.OnClickListener customListener;

    private boolean hasTitle = false;
    private boolean hasMessage = false;
    private boolean hasSubMessage = false;
    private boolean isConfirmButtonVisible = false;
    private boolean isCancelButtonVisible = false;
    private boolean isCustomButtonVisible = false;
    private boolean isProgressBarVisible = false;
    private boolean isImageVisible = false;

    public static final String MESSAGE_DIALOG = "MESSAGE";
    public static final String PROGRESS_DIALOG = "PROGRESS";
    public static final String DEBUG_DIALOG = "DEBUG";

    public AlertDialogFragment setTitle(@StringRes int title) {
        this.title = title;
        hasTitle = true;
        return this;
    }

    public AlertDialogFragment setTitle(String title) {
        titleStr = title;
        hasTitle = true;
        return this;
    }

    public AlertDialogFragment setMessage(@StringRes int message) {
        this.message = message;
        hasMessage = true;
        return this;
    }

    public AlertDialogFragment setMessage(String message) {
        messageStr = message;
        hasMessage = true;
        return this;
    }

    public AlertDialogFragment setSubMessage(String subMessage) {
        this.subMessageStr = subMessage;
        hasSubMessage = true;
        return this;
    }


    public AlertDialogFragment setProgressBar() {
        isProgressBarVisible = true;
        return this;
    }

    public AlertDialogFragment setConfirmButton(@StringRes int confirmMessage, View.OnClickListener listener) {
        this.confirmListener = listener;
        this.confirmMessage = confirmMessage;
        isConfirmButtonVisible = true;
        return this;
    }

    public AlertDialogFragment setConfirmButton(View.OnClickListener listener) {
        setConfirmButton(R.string.confirm, listener);
        return this;
    }

    public AlertDialogFragment setConfirmButton() {
        setConfirmButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return this;
    }

    public AlertDialogFragment setCustomButton(@StringRes int customMessage, View.OnClickListener listener) {
        this.customListener = listener;
        this.customMessage = customMessage;
        isCustomButtonVisible = true;
        return this;
    }

    public AlertDialogFragment setCancelButton(@StringRes int cancelMessage, View.OnClickListener listener) {
        this.cancelListener = listener;
        this.cancelMessage = cancelMessage;
        isCancelButtonVisible = true;
        return this;
    }

    public AlertDialogFragment setQrCode(String data) {
        this.isImageVisible = true;
        this.qrCodeData = data;
        return this;
    }

    public AlertDialogFragment setCancelButton() {
        setCancelButton(R.string.returnHomeButton, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return this;
    }

    public AlertDialogFragment setUnCancelAble() {
        setCancelable(false);
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //不要標頭
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alert_dialog, container, false);
        TextView vTitle = (TextView) view.findViewById(R.id.title);
        TextView vMessage = (TextView) view.findViewById(R.id.message);
        TextView subMessage = (TextView) view.findViewById(R.id.sub_message);
        Button vSure = (Button) view.findViewById(R.id.sure);
        Button vCancel = (Button) view.findViewById(R.id.cancel);
        Button btnCustom = (Button) view.findViewById(R.id.btn_custom);
        ProgressBar vProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        View vLongDivider = view.findViewById(R.id.long_divider);
        View vDivider = view.findViewById(R.id.divider);
        ImageView ivQrCode = view.findViewById(R.id.ivQrCode);

        if (hasTitle) {
            if (titleStr == null) {
                vTitle.setText(title);
            } else {
                vTitle.setText(titleStr);
            }
            vTitle.setVisibility(View.VISIBLE);
        }

        if (hasMessage) {
            if (messageStr == null) {
                vMessage.setText(message);
            } else {
                vMessage.setText(messageStr);
            }
            vMessage.setVisibility(View.VISIBLE);
        }

        if (hasSubMessage) {
            if (subMessage == null) {
                subMessage.setText(subMessageStr);
            } else {
                subMessage.setText(subMessageStr);
            }
            subMessage.setVisibility(View.VISIBLE);
        }

        if (isConfirmButtonVisible) {
            vSure.setText(confirmMessage);
            vSure.setOnClickListener(confirmListener);
            vSure.setVisibility(View.VISIBLE);
        }

        if (isCancelButtonVisible) {
            vCancel.setText(cancelMessage);
            vCancel.setOnClickListener(cancelListener);
            vCancel.setVisibility(View.VISIBLE);
        }

        if (isCustomButtonVisible) {
            btnCustom.setText(customMessage);
            btnCustom.setOnClickListener(customListener);
            btnCustom.setVisibility(View.VISIBLE);
        }

        if (isConfirmButtonVisible && isCancelButtonVisible) {
            vDivider.setVisibility(View.VISIBLE);
        }

        if (isProgressBarVisible) {
            vProgressBar.setVisibility(View.VISIBLE);
            setCancelable(false);
        }

        if (isImageVisible) {
            ivQrCode.setVisibility(View.VISIBLE);
            QrcodeGender.generateQRCode_general("123123", ivQrCode, 300, 300);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // safety check
        if (getDialog() == null)
            return;

        int dialogWidth = 875; // specify a value here
        int dialogHeight = 1075; // specify a value here

        if (isImageVisible) {
            dialogHeight = 1450;
        }

        if(BuildConfig.FLAVOR.equals("kinyo")){
            dialogWidth = 900; // specify a value here
            dialogHeight = 600; // specify a value here
        }


        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
    }
}
