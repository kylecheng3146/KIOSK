package com.lafresh.kiosk.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lafresh.kiosk.BuildConfig;
import com.lafresh.kiosk.Config;
import com.lafresh.kiosk.Kiosk;
import com.lafresh.kiosk.R;
import com.lafresh.kiosk.type.FlavorType;
import com.lafresh.kiosk.type.ScanType;
import com.lafresh.kiosk.utils.CommonUtils;
import com.lafresh.kiosk.utils.VerifyUtil;

public class KeyboardDialog extends Dialog {
    private Context context;
    public TextView tvCount;
    public TextView centerText;

    public interface OnEnterListener {
        void onEnter(int count, String text);
    }

    public OnEnterListener onEnterListener;

    public static final String S_TaxId = "TaxId";
    public static final String DONATE_CODE = "Donate_Code";
    public static final String TABLE_NO = "Table_Number";
    public static final String S_VEHICLE = "Vehicle";
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
        tvCount = findViewById(R.id.tv_count);
        centerText = findViewById(R.id.centerText);
        tvCount.setEnabled(false);
        if(BuildConfig.FLAVOR == FlavorType.lanxin.name()) {
            if (Config.scanType == ScanType.DONATE.toString()) {
                centerText.setText(R.string.input_donate_code);
            } else if (Config.scanType == ScanType.VEHICLE.toString()) {
                tvCount.setEnabled(true);
                centerText.setText(R.string.inputVehicle);
            } else if (this.type == KeyboardDialog.S_TaxId) {
                centerText.setText(R.string.add_tax_id);
            }
        }
    }

    private void setActions() {
        View.OnClickListener ocl_count = view -> {
            if (type.equals(S_TaxId)) {
                INPUT_LIMIT = 8;
            }

            if (type.equals(DONATE_CODE)) {
                INPUT_LIMIT = 7;
            }

            if (type.equals(TABLE_NO)) {
                INPUT_LIMIT = 5;
            }

            if (tvCount.getText().toString().length() < INPUT_LIMIT) {
                String showText = tvCount.getText().toString() + view.getTag();
                tvCount.setText(showText);
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
                String number = tvCount.getText().toString();
                if (number.length() >= 1) {
                    number = number.substring(0, number.length() - 1);
                    tvCount.setText(number);
                }
            }
        });

        findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count;
                String inputNumber = tvCount.getText().toString();
                if (inputNumber.length() > 0) {
                    if(BuildConfig.FLAVOR == FlavorType.lanxin.name()){
                        count = inputNumber.length();
                        System.out.println(count);
                    }else{
                        count = Integer.parseInt(inputNumber);
                        System.out.println(count);
                    }
                } else {
                    count = 1;
                }

                if (count == 0) {
                    count = 1;
                }

                if (type.equals(S_VEHICLE)) {
                    boolean isNotGoodVehicle = !VerifyUtil.isPhoneCarrierNumber(inputNumber);
                    if (isNotGoodVehicle) {
                        CommonUtils.showMessage(context, context.getResources().getString(R.string.invalidVehicleNo));
                        return;
                    }
                }

                if (type.equals(S_TaxId)) {
                    boolean isNotGoodTaxId = !VerifyUtil.isUnifiedBusinessNumber(inputNumber);
                    if (isNotGoodTaxId) {
                        CommonUtils.showMessage(context, context.getResources().getString(R.string.invalidTaxNo));
                        return;
                    }
                }

                onEnterListener.onEnter(count, tvCount.getText().toString());
                dismiss();
            }
        });

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        findViewById(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvCount.setText("");
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        Kiosk.idleCount = Config.idleCount;
        return super.dispatchTouchEvent(ev);
    }
}
