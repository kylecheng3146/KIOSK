package com.lafresh.kiosk.printer.tp805;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.lafresh.kiosk.R;
import com.lafresh.kiosk.utils.AlertUtils;

/**
 * Created by Kevin on 2020/7/3.
 */

class TP805BlueToothReceiver extends BroadcastReceiver {
    TP805Printer tp805Printer;

    public TP805BlueToothReceiver(TP805Printer tp805Printer) {
        this.tp805Printer = tp805Printer;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//           ... //Device found
        }
        else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            tp805Printer.setConnected(true);
//           ... //Device is now connected
        }
        else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//           ... //Done searching
        }
        else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
//           ... //Device is about to disconnect
        }
        else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
//           ... //Device has disconnected
            tp805Printer.setConnected(false);
            AlertUtils.showMsgWithConfirmBlueTooth((AppCompatActivity) context, R.string.activity_main_scan_error);
        }
    }
}
