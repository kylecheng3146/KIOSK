package com.lafresh.kiosk.fragment;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.lafresh.kiosk.Kiosk;
import com.lafresh.kiosk.R;
import com.lafresh.kiosk.utils.ClickUtil;

public class MessageDialogFragment extends DialogFragment {
    private MessageDialogFragmentListener listener;
    private String msg;
    private boolean isConfirm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogFragmentStyle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_dialog, container, false);
        TextView tvMessage = view.findViewById(R.id.tvMessage);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);
        if (getDialog() != null) {
            Kiosk.hidePopupBars(getDialog());
        }
        if (msg.length() > 10) {
            tvMessage.setTextSize(40);
        }
        tvMessage.setText(msg);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClickUtil.isFastDoubleClick()) {
                    return;
                }
                isConfirm = true;
                dismiss();
                if (listener != null) {
                    listener.onFinish();
                }
            }
        });
        return view;
    }


    public interface MessageDialogFragmentListener {
        void onFinish();

        void onDismiss();
    }

    public void setMessage(String message) {
        msg = message;
    }

    public void setMessageDialogFragmentListener(MessageDialogFragmentListener listener) {
        this.listener = listener;
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (listener != null && !isConfirm) {
            listener.onDismiss();
        }
        isConfirm = false;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);
        } catch (Exception ignore) {

        }
    }
}
