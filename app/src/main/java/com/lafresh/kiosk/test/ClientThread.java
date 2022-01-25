package com.lafresh.kiosk.test;

import android.os.Bundle;

import com.lafresh.kiosk.utils.CashModuleClientListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by Kyle on 2021/3/11.
 */

public class  ClientThread extends Thread {
    private String HOST = "127.0.0.1";//computer IP
    private int PORT = 7500; //server PORT,the same to server
    private Socket socket;
    private String buffer;
    PrintStream output;
    BufferedReader in;
    CashModuleClientListener cashModuleClientListener;
    private JSONObject obj;

    public ClientThread(CashModuleClientListener cashModuleClientListener, JSONObject obj) {
        this.cashModuleClientListener = cashModuleClientListener;
        this.obj = obj;
    }

    @Override
    public void run() {
        Bundle bundle = new Bundle();
        bundle.clear();
        //build socket conncetion.
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(HOST,PORT),5000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintStream(socket.getOutputStream());
            output.println(obj.toString());
            output.flush();
            String response;
            while ((response = in.readLine()) != null) {
                cashModuleClientListener.onReceived(response);
                socket.shutdownInput();
            }
            try {
                output.close();
                in.close();
                socket.close();
            }
            catch (IOException e) {
                bundle.putString("newcontent","There is IOEX close");
            }
        }
        catch (SocketTimeoutException aa) {
            bundle.putString("newcontent","There is Timeout");
        }
        catch (IOException e) {
            bundle.putString("newcontent","There is IOEX");
        }
    }

    public void shutdown() {
        try {
            socket.shutdownInput();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
