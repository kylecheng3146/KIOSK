package com.lafresh.kiosk.test;

import com.lafresh.kiosk.utils.CashModuleServerListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kyle on 2021/3/11.
 */
public class ServerThread implements Runnable {

    Socket socket;
    PrintStream output;
    BufferedReader in;
    CashModuleServerListener listener;


    public ServerThread(Socket socket, CashModuleServerListener listener) throws IOException {
        this.socket = socket;
        this.listener = listener;
    }

    //broadcast the message from one client to every clients
    public void run() {

        String buffer;
        try {

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintStream(socket.getOutputStream());

            buffer = ""; //if init buffer = null, will get a string which like "null..."
            String line = null;

            //Read message From readline.
            //Here , You must be careful, because readline() will block rather than return null
            //if there is no input.Readline() will return  when you close the stream,such as
//            socket.shutdownOutput();

            while ((line = in.readLine()) != null) {
                if(line.endsWith("}")){
                    this.listener.onServerReceived(line);
                    socket.shutdownOutput();
                }
            }

            //You can replace the code of "Read message From readline " by follow,
            //but you can not output to client after you shutdown output.
//			while((line = in.readLine()) != null) {
//				buffer = buffer + line;
//			}
//			output.println(packetMessage(buffer));
//			output.flush();
//		    socket.shutdownOutput();

            //System.out.println(buffer);

        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public String packetMessage(String buffer) {
        String result = null;
        SimpleDateFormat dFormat = new SimpleDateFormat("HH:mm:ss");
        result = "Form server:  "+dFormat.format(new Date())+" "+buffer;
        this.listener.onServerReceived(buffer);
        return result;
    }
}
