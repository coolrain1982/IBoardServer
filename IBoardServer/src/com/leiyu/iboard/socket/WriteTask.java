package com.leiyu.iboard.socket;

import com.leiyu.iboard.IBoardServer;
import com.leiyu.iboard.transmission.InterCmdQueue;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;

/**
 * Created by leiyu on 2016/10/25.
 */

public class WriteTask extends Thread {

    private Socket socket;
    private InterCmdQueue interCmdQueue;
    private PrintWriter bw;
    private boolean isFinish = false;

    protected WriteTask(Socket socket, InterCmdQueue icq) throws IOException{
        this.socket = socket;
        this.interCmdQueue = icq;
        bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
    }

    protected void finish() {
        isFinish = true;
    }

    protected void startListener() {
        start();
    }

    public void run() {
        while(!isFinish) {
            //从消息队列中取出一条消息，发送出去
            String msg = interCmdQueue.getCmdOut();
            if (msg != null) {
                try {
                    bw.print(msg);
                    bw.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    isFinish = true;
                }
                
                if (IBoardServer.isDebug > 0) {
	                Calendar calendar = Calendar.getInstance();
	                System.out.println(String.format("%s.%s %s:%s:%s---send to client[%s %s] : %s", 
	                		calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
	                		calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
	                		calendar.get(Calendar.SECOND), socket.getLocalSocketAddress().toString(),
	                		socket.getRemoteSocketAddress().toString(), msg));
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
