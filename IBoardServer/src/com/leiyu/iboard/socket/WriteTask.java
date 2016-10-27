package com.leiyu.iboard.socket;

import com.leiyu.iboard.transmission.InterCmdQueue;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by leiyu on 2016/10/25.
 */

public class WriteTask extends Thread {

    private Socket socket;
    private InterCmdQueue interCmdQueue;
    private BufferedWriter bw;
    private boolean isFinish = false;

    protected WriteTask(Socket socket, InterCmdQueue icq) throws IOException{
        this.socket = socket;
        this.interCmdQueue = icq;
        bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    protected void finish() {
        isFinish = true;
    }

    protected void startListener() {
        start();
    }

    public synchronized void run() {
        while(!isFinish) {
            //从消息队列中取出一条消息，发送出去
            String msg = interCmdQueue.getCmdOut();
            if (msg != null) {
                try {
                    bw.write(msg);
                    bw.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    isFinish = true;
                }
            } else {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {e.printStackTrace();}
            }
        }

        try {
            socket.shutdownOutput();
        } catch (Exception e) {}

        try {
            bw.close();
        } catch (Exception e) {}
    }
}
