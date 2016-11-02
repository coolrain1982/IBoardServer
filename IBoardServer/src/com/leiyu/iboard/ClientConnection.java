package com.leiyu.iboard;

import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map.Entry;

import com.leiyu.iboard.socket.SocketHandler;
import com.leiyu.iboard.transmission.Command;
import com.leiyu.iboard.transmission.InterCmdQueue;

public class ClientConnection extends Thread {
	private Socket socket;
	private SocketHandler socketHandler;
	private InterCmdQueue interCmdQueue;
	private int role = 0;
	
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
	
	public void destory() {
		socketHandler.shutDown();
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
		
		main:while (!socketHandler.isShutDown()) {
			
			try {
				Thread.sleep(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			while (true) {
				Object o = interCmdQueue.getCmdIn();
				if (o == null) {
					continue main;
				}
				
				if (!Command.class.isInstance(o)) {
					continue main;
				}
				
				Command command = (Command)o;
				switch (command.getCommandId()) {
				case Command.COMMAND_USERID:
					setUserSocket(command.getCommand().toLowerCase().trim());
					break;
				case Command.COMMAND_DRAWSHAPE_START:
				case Command.COMMAND_DRAWSHAPE_MOVE:
				case Command.COMMAND_DRAWSHAPE_END:
					transAShapeToStudents(command);
					break;
				default:
					break;
				}
			}
		}
		
		interCmdQueue.clear();
		
		//把这个连接对应的用户信息删除掉
		String userID = null;
		synchronized (IBoardServer.lockUserToSockets) {
			Iterator<Entry<String, ClientConnection>> it = IBoardServer.userToSockets.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, ClientConnection> scEntry = it.next();
				if (scEntry.getValue() == this) {
					IBoardServer.userToSockets.remove(scEntry.getKey());
					userID = scEntry.getKey();
				}
			}
		}
		
		if (userID != null) {
			synchronized (IBoardServer.lockiUserRole) {
				if (IBoardServer.UserRole.containsKey(userID)) {
					IBoardServer.UserRole.remove(userID);
				}
			}
		}
	}
	
	private void setUserSocket(String userID) {
		synchronized (IBoardServer.lockUserToSockets) {
			IBoardServer.userToSockets.put(userID, this);
		}
		
		synchronized (IBoardServer.lockiUserRole) {
			role = IBoardServer.UserRole.containsKey(userID)?IBoardServer.UserRole.get(userID):
				0;
		}
		
		System.out.println(String.format("userID = %s, role = %d", userID, role));
	}
	
	private void transAShapeToStudents(Command command) {
		synchronized (IBoardServer.lockUserToSockets) {
			Iterator<Entry<String, ClientConnection>> it = IBoardServer.userToSockets.entrySet().iterator();
			while (it.hasNext()) {
				ClientConnection cc = it.next().getValue();
				if (cc.role == 2) {
					cc.sendMsg(command.getAllCommandStr());
				}
			}
		}
	}
}
