package com.lafresh.kiosk.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.lafresh.kiosk.Bill;
import com.lafresh.kiosk.Config;
import com.lafresh.kiosk.R;
import com.lafresh.kiosk.manager.OrderManager;
import com.lafresh.kiosk.type.ScanType;

import com.lafresh.kiosk.dialog.KeyboardDialog;

/**
 * Sample Activity extending from ActionBarActivity to display a Toolbar.
 */
public class ScanZxcingCapture extends AppCompatActivity {
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private ImageButton ibRestart;
    private ImageButton ibInputBarCode;
    private TextView tvTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_scan_zxcing);

        serUI();
        setActivity();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
    }

    private void serUI(){
        tvTitle = findViewById(R.id.tv_title);
        ibRestart = findViewById(R.id.ib_restart);
        ibInputBarCode = findViewById(R.id.ib_input_bar_code);


        if(Config.scanType == ScanType.DONATE.toString()){//捐贈碼
            ibInputBarCode.setVisibility(View.VISIBLE);
            tvTitle.setText(R.string.scan_donate_code);
        }

        if(Config.scanType == ScanType.VEHICLE.toString()){
            ibInputBarCode.setVisibility(View.VISIBLE);
            tvTitle.setText(R.string.scanVehicle);
        }

        if(Config.scanType == ScanType.LINE_PAY.toString()){
            tvTitle.setText(R.string.scanLinePay);
        }

        if(Config.scanType == ScanType.EASY_WALLET.toString()){
            tvTitle.setText(R.string.scan_easy_pay);
        }

        ibInputBarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setActivity(){
        ibRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ibInputBarCode.setOnClickListener(view -> {
            KeyboardDialog keyboard = new KeyboardDialog(ScanZxcingCapture.this);
            if(Config.scanType == ScanType.VEHICLE.toString()){
                keyboard.type = KeyboardDialog.S_VEHICLE;
            }else {
                keyboard.type = KeyboardDialog.DONATE_CODE;
            }
            keyboard.show();
            keyboard.onEnterListener = (count, text) -> {
                if(Config.scanType == ScanType.DONATE.toString()){//捐贈碼
                    Bill.fromServer.setNpoBan(text);
                    Bill.fromServer.setCustCode("");
                }

                if(Config.scanType == ScanType.VEHICLE.toString()){
                    Bill.fromServer.setBuyerNumber(text);
                }
                OrderManager.getInstance().setLoveCode(text);
                finish();
            };
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }
}
