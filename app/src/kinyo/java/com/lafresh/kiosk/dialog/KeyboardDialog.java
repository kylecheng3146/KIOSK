package com.lafresh.kiosk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.lafresh.kiosk.Config;
import com.lafresh.kiosk.Kiosk;
import com.lafresh.kiosk.R;
import com.lafresh.kiosk.utils.VerifyUtil;

public class KeyboardDialog extends Dialog {
    private Context context;
    public TextView tv_count;

    public interface Listener {
        void onEnter(int count, String text);
    }

    public Listener listener;

    public static final String S_TaxId = "TaxId";
    public static final String DONATE_CODE = "Donate_Code";
    public static final String TABLE_NO = "Table_Number";
    public String type = "";
    private int INPUT_LIMIT = 3;


    public KeyboardDialog(final Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Kiosk.hidePopupBars(this);
        setContentView(R.layout.d_keyboard);
        this.context = context;

        setUI();
        setActions();
    }

    private void setUI() {
        tv_count = findViewById(R.id.tv_count);
    }

    private void setActions() {
        View.OnClickListener ocl_count = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals(S_TaxId)) {
                    INPUT_LIMIT = 8;
                }

                if (type.equals(DONATE_CODE)) {
                    INPUT_LIMIT = 7;
                }

                if (type.equals(TABLE_NO)) {
                    INPUT_LIMIT = 5;
                }

                if (tv_count.getText().toString().length() < INPUT_LIMIT) {
                    String showText = tv_count.getText().toString() + view.getTag();
                    tv_count.setText(showText);
                }
            }
        };

        findViewById(R.id.btn00).setOnClickListener(ocl_count);
        findViewById(R.id.btn_0).setOnClickListener(ocl_count);
        findViewById(R.id.btn_1).setOnClickListener(ocl_count);
        findViewById(R.id.btn_2).setOnClickListener(ocl_count);
        findViewById(R.id.btn_3).setOnClickListener(ocl_count);
        findViewById(R.id.btn_4).setOnClickListener(ocl_count);
        findViewById(R.id.btn_5).setOnClickListener(ocl_count);
        findViewById(R.id.btn_6).setOnClickListener(ocl_count);
        findViewById(R.id.btn_7).setOnClickListener(ocl_count);
        findViewById(R.id.btn_8).setOnClickListener(ocl_count);
        findViewById(R.id.btn_9).setOnClickListener(ocl_count);
        findViewById(R.id.btn_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = tv_count.getText().toString();
                if (number.length() >= 1) {
                    number = number.substring(0, number.length() - 1);
                    tv_count.setText(number);
                }
            }
        });

        findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputNumber = tv_count.getText().toString();
                if(inputNumber.isEmpty() || "0".equals(inputNumber)){
                    dismiss();
                    return;
                }

                int count;


                if (inputNumber.length() > 0) {
                    count = Integer.parseInt(inputNumber);
                } else {
                    count = 1;
                }

                if (count == 0) {
                    count = 1;
                }

                if (type.equals(S_TaxId)) {
                    boolean isNotGoodTaxId = !VerifyUtil.isUnifiedBusinessNumber(inputNumber);
                    if (isNotGoodTaxId) {
                        String msg = context.getResources().getString(R.string.invalidTaxNo);
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                listener.onEnter(count, tv_count.getText().toString());
                dismiss();
            }
        });

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        Kiosk.idleCount = Config.idleCount;
        return super.dispatchTouchEvent(ev);
    }
}
