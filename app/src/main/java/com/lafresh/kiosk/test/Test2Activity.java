package com.lafresh.kiosk.test;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lafresh.kiosk.R;

/**
 * Created by Kyle on 2021/3/11.
 */

public class Test2Activity extends AppCompatActivity {
    EditText edtMessage;
    TextView tvContent;
    Button btnSend;
    Button btnTest2;
    Button btnTest3;
    Button btnTest4;
    Button btnTest5;
    MyHandler myHandler = new MyHandler();

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String buffer = bundle.getString("newcontent");
            tvContent.append('\n' + buffer);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        btnSend = (Button)findViewById(R.id.btn_test);
        btnTest2 = (Button)findViewById(R.id.btn_test2);
        btnTest3 = (Button)findViewById(R.id.btn_test3);
        btnTest4 = (Button)findViewById(R.id.btn_test4);
        btnTest5 = (Button)findViewById(R.id.btn_test5);
        tvContent = (TextView) findViewById(R.id.tv_test);

//        new Thread(()->{
//            MyServer myServer = new MyServer();
//            myServer.initMyserver(cashModuleListener);
//        }).start();

//        btnSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                JSONObject obj = registerCallBack();
//                new ClientThread(myHandler,obj).start();
//            }
//        });
//
//        btnTest2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                JSONObject obj = transaction();
//                new ClientThread(myHandler,obj).start();
//            }
//        });
//
//        btnTest3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                JSONObject obj = outAllMoney(DevicePort.TEN_DOLLAR.getPort());
//                new ClientThread(myHandler,obj).start();
//            }
//        });
//
//        btnTest4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                JSONObject obj = addMoney();
//                new ClientThread(myHandler,obj).start();
//            }
//        });
//
//        btnTest5.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                JSONObject obj = portSetting();
//                new ClientThread(myHandler,obj).start();
//            }
//        });
//        btnSend.performClick();
    }
}
