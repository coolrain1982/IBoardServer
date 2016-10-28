package com.leiyu.iboard;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class IBoardServer {
	
	private ServerSocket server = null;
	private int port = 0;
	public static List<IBoardMeeting> iboardMeetingList = new ArrayList<>();
	private static final Map<String,ClientConnection> userToSockets = new HashMap<>();
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public void startServer() throws IOException {
		server = new ServerSocket(port);
	}
	
	public void run() {
		while(true) {
			Socket socket = null;
			try {
				socket = server.accept();
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
			
			//
			ClientConnection clientConnection = new ClientConnection(socket);
			clientConnection.start();
		}
		
		Iterator<Entry<String, ClientConnection>> allSockets = userToSockets.entrySet().iterator();
		while(allSockets.hasNext()) {
			try {
				allSockets.next().getValue().getSocket().close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//关闭server
		try {
			server.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("The number of parameters must be 2!");
			return;
		}
		//第一个参数为端口
		int port = 0;
		try {
			port = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			System.out.println("The second parameter must be integer!");
		}
		
		IBoardServer iBoardServer = new IBoardServer();
		//开始启动服务
		iBoardServer.setPort(port);
		try {
			iBoardServer.startServer();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		iBoardServer.run();
		
		System.exit(0);
	}
}
