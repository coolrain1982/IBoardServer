package com.leiyu.iboard.socket;

import com.leiyu.iboard.transmission.Command;
import com.leiyu.iboard.transmission.CommandProcess;
import com.leiyu.iboard.transmission.InterCmdQueue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by leiyu on 2016/10/25.
 */

public class ReadTask extends Thread {

    private Socket socket;
    private SocketStatusListener socketStatusListener;
    private BufferedReader br;
    private boolean listening;
    private char[] charArr;
    private InterCmdQueue interCmdQueue;
    private StringBuffer sb = new StringBuffer();

    protected ReadTask(Socket socket, InterCmdQueue icq) throws IOException {
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.socket = socket;
        interCmdQueue = icq;
        charArr = new char[1024];
    }

    protected void finish() throws IOException {
        listening = false;
        interrupt();

        try {
            socket.shutdownInput();
        } catch (Exception e) {
        }

        try {
            br.close();
        } catch (Exception e) {
        } finally {
            br = null;
        }

        charArr = null;
    }

    public synchronized void run() {
        while (listening) {
            int charLen = 0;
            try {
                while((charLen = br.read(charArr)) > -1) {
                    sb.append(charArr, 0, charLen);
                }

                CommandProcess cp = new CommandProcess(sb);
                Command cmd = cp.getCommand();
                if (cmd != null) {
                    interCmdQueue.addCmdIn(cmd);
                }

                try {
                    Thread.sleep(100);
                } catch (Exception e) {e.printStackTrace();}

            } catch (IOException e) {
                listening = false;
                if (socketStatusListener != null) {
                    int status = parseSocketStatus(e);
                    socketStatusListener.onSocketStatusChanged(socket, status, e);
                    return;
                }
            }
        }
    }

    private int parseSocketStatus(Exception e) {
        if (SocketException.class.isInstance(e)) {
            String msg = e.getLocalizedMessage().trim();
            if ("connection reset".equalsIgnoreCase(msg)) {
                return SocketStatusListener.STATUS_RESET;
            } else if ("socket is close".equalsIgnoreCase(msg)) {
                return SocketStatusListener.STATUS_CLOSE;
            } else if ("broken pipe".equalsIgnoreCase(msg)) {
                return SocketStatusListener.STATUS_PIP_BROKEN;
            }
        }

        return SocketStatusListener.STATUS_UNKOWN;
    }

    protected void startListener(SocketStatusListener ssl) {
        listening = true;
        this.socketStatusListener = ssl;
        start();
    }
}
