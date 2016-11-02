package com.leiyu.iboard.socket;

import com.leiyu.iboard.IBoardServer;
import com.leiyu.iboard.transmission.InterCmdQueue;

import java.io.IOException;
import java.net.Socket;
import java.util.Calendar;

/**
 * Created by leiyu on 2016/10/25.
 */

public class SocketHandler implements SocketStatusListener {

    private Socket socket = null;
    private ReadTask reader;
    private WriteTask writer;
    private boolean isShutDown = false;

    public SocketHandler(Socket socket, InterCmdQueue icq) throws IOException {
        this.socket = socket;
//        this.socket.setTcpNoDelay(true);
        reader = new ReadTask(socket, icq);
        writer = new WriteTask(socket, icq);
        onSocketStatusChanged(socket, STATUS_OPEN, null);
    }

    public void listen() {
        reader.startListener(this);
        writer.startListener();
    }

    public void shutDown() {
        try {
            writer.finish();
        } catch (Exception e) {
        	
        }

        try {
            reader.finish();
        } catch (Exception e) {

        }

        try {
            socket.close();
        } catch (Exception e) {
        	
        }
        
        if (IBoardServer.isDebug > 0) {
        	Calendar calendar = Calendar.getInstance();
            System.out.println(String.format("%s.%s %s:%s:%s---client[%s %s] closed!", 
            		calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
            		calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
            		calendar.get(Calendar.SECOND), socket.getLocalSocketAddress().toString(),
            		socket.getRemoteSocketAddress().toString()));
        }
        
        reader = null;
        writer = null;
        
        isShutDown = true;
    }

    @Override
    public void onSocketStatusChanged(Socket socket, int status, IOException e) {
        switch (status) {
            case SocketStatusListener.STATUS_CLOSE:
            case SocketStatusListener.STATUS_RESET:
            case SocketStatusListener.STATUS_PIP_BROKEN:
                shutDown();
                break;
            default:
                break;
        }
    }
    
    public boolean isShutDown() {
    	return isShutDown;
    }
}
