package com.lafresh.kiosk.utils;

import android.Manifest;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.lafresh.kiosk.printer.tp805.DeviceListActivity;
import com.lafresh.kiosk.printer.tp805.WifiActivity;
import com.tbruyelle.rxpermissions.RxPermissions;

import print.Print;
import rx.functions.Action1;

/**
 * Created by Kevin on 2020/7/3.
 */

public class ConnectUtils {
    public static void connectionBluetooth(final AppCompatActivity activity) {
        //獲取藍芽權限
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.ACCESS_FINE_LOCATION).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
//                if (aBoolean) {
                    Intent serverIntent = new Intent(activity, DeviceListActivity.class);
                    activity.startActivityForResult(serverIntent, Print.ACTIVITY_CONNECT_BT);
//                }else{
//                    Toast.makeText(activity,"請開啟藍芽權限",Toast.LENGTH_LONG).show();
//                }
            }
        });
    }

    public static void connectionWifi(final AppCompatActivity activity) {
        //獲取藍芽權限
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
                Manifest.permission.CHANGE_WIFI_STATE).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (aBoolean) {
                Intent serverIntent = new Intent(activity, WifiActivity.class);
                activity.startActivityForResult(serverIntent, Print.ACTIVITY_CONNECT_WIFI);
                }else{
                    CommonUtils.showMessage(activity,"請開啟網路權限");
                }
            }
        });
    }
}
