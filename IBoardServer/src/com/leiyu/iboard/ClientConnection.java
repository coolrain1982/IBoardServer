package com.leiyu.iboard;

import java.io.IOException;
import java.net.Socket;

import com.leiyu.iboard.socket.SocketHandler;
import com.leiyu.iboard.transmission.Command;
import com.leiyu.iboard.transmission.InterCmdQueue;

public class ClientConnection extends Thread {
	private Socket socket;
	private SocketHandler socketHandler;
	private InterCmdQueue interCmdQueue;
	
	public ClientConnection(Socket s) {
		socket = s;
		interCmdQueue = new InterCmdQueue();
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public void sendMsg(String msg) {
		interCmdQueue.addCmdOut(msg);
	}
	
	@Override
	public void run() {
		try {
			socketHandler = new SocketHandler(socket, interCmdQueue) ;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		socketHandler.listen();
		
		while (!socketHandler.isShutDown()) {
			
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Object o = interCmdQueue.getCmdIn();
			if (o == null) {
				continue;
			}
			
			if (!Command.class.isInstance(o)) {
				continue;
			}
			
			Command command = (Command)o;
			
			
		}
		
		interCmdQueue.clear();
	}
}
