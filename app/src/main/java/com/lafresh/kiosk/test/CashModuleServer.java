package com.lafresh.kiosk.test;

import com.lafresh.kiosk.utils.CashModuleServerListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Kyle on 2021/3/11.
 */

public class CashModuleServer {
    private static final int PORT = 8300; //spy on the port 5000
    private Thread thread = null;
    private Thread connectThread = null;
    private Runnable runnable = null;
    private Socket socket;
    private ServerSocket serverSocket;
    private boolean shutdown = false;

    public void initCashModuleServer(CashModuleServerListener cashModuleServerListener) {
        try {
            serverSocket = new ServerSocket(PORT);
            connectThread = new Thread(() -> {
                while(!shutdown) {
                    //If there is not connect,it will block.
                    //Listen to the connect from client.
                    //If there is a connect ,it will return a socket.
                    try {
                        assert serverSocket != null;
                        socket = serverSocket.accept();
                        //build a thread to deal with the socket
                        runnable = new ServerThread(socket,cashModuleServerListener);
                        thread = new Thread(runnable);
                        thread.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            connectThread.start();

        } catch (IOException e) {
            // TODO: handle exception
        }
    }

    public void interrupt(){
        try {
            serverSocket.close();
            shutdown = true;
            if(thread != null && !thread.isInterrupted() ) {
                thread.interrupt();
            }
            if(connectThread != null && !connectThread.isInterrupted()){
                connectThread.interrupt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            Thread.currentThread().interrupt();
        }

    }
}
