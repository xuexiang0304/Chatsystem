package au.edu.unimelb.tcp.server;

import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerHandler {
	private Socket socket;

	private JsonUtilsServer jsonServer = new JsonUtilsServer();
	DataOutputStream out;

	public ServerHandler(Socket socket) {
		this.socket = socket;
		try {
			out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void newIdentity(String former, String identity, Socket socket) {
		try {
			RoomInfoMap.newJoin("", identity, socket);
			RoomInfoMap.createRoom("MainHall", "");
			String newidentity = jsonServer.newIdentity(former, identity)+ "\n";			
			System.out.println(newidentity);

			try {
				out.write(newidentity.getBytes("UTF-8"));
				out.flush();
			} catch (EOFException e) {
				if (socket != null)
					socket.close();
				System.out.println("Client disconnected.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showRoomList() {
		try {
			List<String> roomlist = new ArrayList<String>();
			roomlist = RoomInfoMap.getRoomList();
			String getRoomList = jsonServer.roomList(roomlist)+ "\n";
			System.out.println(getRoomList);
			try {
				out.write(getRoomList.getBytes("UTF-8"));
				out.flush();
			} catch (EOFException e) {
				if (socket != null)
					socket.close();
				System.out.println("Client disconnected.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void joinRoom(String roomid) {
		try {
			String identity = RoomInfoMap.getUserBySocket(socket);
			String former = RoomInfoMap.getRoomBySocket(socket);
			String owner = RoomInfoMap.getOwner(former);
			try { 
				if(RoomInfoMap.getRoomOwnerPair().containsKey(roomid)&&!RoomInfoMap.isKick(roomid, identity)){
					RoomInfoMap.removeUser(former, identity);
					RoomInfoMap.newJoin(roomid, identity, socket);
					// if the owner of the room disconnected and the room is empty
					// this room should be deleted immediately.
					if(owner.equals("")&&!former.equals("")&&!former.equals("MainHall")&&RoomInfoMap.isRoomEmpty(former)){
						RoomInfoMap.getRoomOwnerPair().remove(former);
						RoomInfoMap.getRoomInfo().remove(former);
					}
					String joinMsg = jsonServer.roomChange(identity, former, roomid)+ "\n";
					System.out.println(joinMsg);
					List<Socket> socketNewroom = RoomInfoMap.getSocketsOfRoom(roomid);
					List<Socket> socketFormer = RoomInfoMap.getSocketsOfRoom(former);
					for(int i=0; i<socketNewroom.size();i++){
						DataOutputStream outdata = new DataOutputStream(socketNewroom.get(i).getOutputStream());
						outdata.write(joinMsg.getBytes("UTF-8"));
						outdata.flush();
					}
					for(int i=0; i<socketFormer.size();i++){
						DataOutputStream outdata = new DataOutputStream(socketFormer.get(i).getOutputStream());
						outdata.write(joinMsg.getBytes("UTF-8"));
						outdata.flush();
					}
					if(roomid.equals("MainHall")){
						String roomContent = jsonServer.roomContents(roomid)+ "\n";
						List<String> roomlist = RoomInfoMap.getRoomList();
						String roomList = jsonServer.roomList(roomlist)+ "\n";
						out.write(roomContent.getBytes("UTF-8"));
						out.write(roomList.getBytes("UTF-8"));
						out.flush();
					}
					
				}else if(RoomInfoMap.isKick(roomid, identity)){
					long joketime = RoomInfoMap.getTime(roomid, identity);
					long currenttime = System.currentTimeMillis();
					long timeGap = currenttime-joketime;
					long setTime = RoomInfoMap.getCreateTime(roomid, identity).longValue();
					if(timeGap < setTime){
						String content = identity+" "+", sorry! You have been temporily banned from this room."
								+ " The remaining ban time is"+" "+ timeGap/1000;
						String joinMsg = jsonServer.sendMessage("System", content)+ "\n";
						System.out.println(joinMsg);
						out.write(joinMsg.getBytes("UTF-8"));
						out.flush();
					}else{
						RoomInfoMap.removeKick(roomid, identity);
						RoomInfoMap.removeUser(former, identity);
						RoomInfoMap.newJoin(roomid, identity, socket);
						// if the owner of the room disconnected and the room is empty
						// this room should be deleted immediately.
						if(owner.equals("")&&!former.equals("MainHall")&&RoomInfoMap.isRoomEmpty(former)){
							RoomInfoMap.getRoomOwnerPair().remove(former);
							RoomInfoMap.getRoomInfo().remove(former);
						}
						String joinMsg = jsonServer.roomChange(identity, former, roomid)+ "\n";
						System.out.println(joinMsg);
						List<Socket> socketNewroom = RoomInfoMap.getSocketsOfRoom(roomid);
						List<Socket> socketFormer = RoomInfoMap.getSocketsOfRoom(former);
						for(int i=0; i<socketNewroom.size();i++){
							DataOutputStream outdata = new DataOutputStream(socketNewroom.get(i).getOutputStream());
							outdata.write(joinMsg.getBytes("UTF-8"));
							outdata.flush();
						}
						for(int i=0; i<socketFormer.size();i++){
							DataOutputStream outdata = new DataOutputStream(socketFormer.get(i).getOutputStream());
							outdata.write(joinMsg.getBytes("UTF-8"));
							outdata.flush();
						}
					}
				}else{
					String joinMsg = jsonServer.roomChange(identity, former, former)+ "\n";
					System.out.println(joinMsg);
					out.write(joinMsg.getBytes("UTF-8"));
					out.flush();
				}				
			} catch (EOFException e) {
				if (socket != null)
					socket.close();
				System.out.println("Client disconnected.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showRoomContent(String roomid) {
		try {
			try {
			if(RoomInfoMap.getRoomOwnerPair().containsKey(roomid)){				
				String roomContent = jsonServer.roomContents(roomid)+ "\n";
				System.out.println(roomContent);
				out.write(roomContent.getBytes("UTF-8"));
				out.flush();
			}else{
				String content = "Room name"+" "+ roomid+" "+"is invalid or non exist."; 
				String error = jsonServer.sendMessage("System", content)+ "\n";
				System.out.println(error);
				out.write(error.getBytes("UTF-8"));
				out.flush();
			}				
			} catch (EOFException e) {
				if (socket != null)
					socket.close();
				System.out.println("Client disconnected.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void identityChange(String newidentity) {
		try {
			String former = RoomInfoMap.getUserBySocket(socket);
			String roomid = RoomInfoMap.getRoomBySocket(socket);
			char[] chars = newidentity.toCharArray();
			char firstCharacter = chars[0];
			int charsize = chars.length;
			String jsonnewidentity;
			try {
				
			if (former.equals(newidentity) || charsize < 3 || charsize > 16
					|| !Character.isLetter(firstCharacter) || !isValid(chars)||RoomInfoMap.isExistId(newidentity)) {
				jsonnewidentity = jsonServer.newIdentity(former, former)+ "\n";
				out.write(jsonnewidentity.getBytes("UTF-8"));
				out.flush();
			} else {
				RoomInfoMap.removeUser(roomid, former);
				RoomInfoMap.newJoin(roomid, newidentity, socket);
				if(RoomInfoMap.getRoomOwnerPair().containsValue(former)){
					RoomInfoMap.changeOwner(former, newidentity);
				}
				if(former.matches("^guest[1-9][1-9]*$")){
					char[] former_arr = former.toCharArray();
					String id_no ="";
					for(int i=0;i<former_arr.length;i++){
						if(Character.isDigit(former_arr[i])){
							id_no+=former_arr[i];
						}
					}
					int id = Integer.parseInt(id_no);
					RoomInfoMap.getReuseIdentities().put(id, former);
				}
				jsonnewidentity = jsonServer.newIdentity(former, newidentity)+ "\n";
				List<Socket> sockets = new ArrayList<Socket>();
				sockets = RoomInfoMap.getAllSocket();
				for(int i=0; i< sockets.size();i++){
					DataOutputStream outdata = new DataOutputStream(sockets.get(i).getOutputStream());
					outdata.write(jsonnewidentity.getBytes("UTF-8"));
					outdata.flush();
				}
			}
			System.out.println(jsonnewidentity);
							
			} catch (EOFException e) {
				if (socket != null)
					socket.close();
				System.out.println("Client disconnected.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createRoom(String roomid){
		try {
			String owner = RoomInfoMap.getUserBySocket(socket);
			char[] chars = roomid.toCharArray();
			char firstCharacter = chars[0];
			int charsize = chars.length;
			try {
			if(RoomInfoMap.getRoomOwnerPair().containsKey(roomid)||!Character.isLetter(firstCharacter)
					||charsize<3||charsize>32||!isValid(chars)){
				String content = "Room"+" "+ roomid+" "+"is invalid or already in use."; 
				String error = jsonServer.sendMessage("System", content)+ "\n";
				System.out.println(error);
				out.write(error.getBytes("UTF-8"));
				out.flush();
			}else{
			RoomInfoMap.createRoom(roomid, owner);
			List<String> roomList = RoomInfoMap.getRoomList();
			String rooms = jsonServer.roomList(roomList)+ "\n";
			System.out.println(rooms);
			
			out.write(rooms.getBytes("UTF-8"));
			out.flush();
			}				
			} catch (EOFException e) {
				if (socket != null)
					socket.close();
				System.out.println("Client disconnected.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage(String content){
		try {
			String identity = RoomInfoMap.getUserBySocket(socket);
			String roomid = RoomInfoMap.getRoomBySocket(socket);
			List<Socket> sockets = RoomInfoMap.getSocketsOfRoom(roomid);
			try {
				String msg = jsonServer.sendMessage(identity, content)+ "\n";
				for(int i=0; i< sockets.size();i++){
				DataOutputStream dataout = new DataOutputStream(sockets.get(i).getOutputStream());
				dataout.write(msg.getBytes("UTF-8"));
				dataout.flush();
				}
				System.out.println(msg);
			} catch (EOFException e) {
				if (socket != null)
					socket.close();
				System.out.println("Client disconnected.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteRoom(String roomid){
		try{
			List<String> users = RoomInfoMap.getUsers(roomid);
			String identity = RoomInfoMap.getUserBySocket(socket);
			String owner = RoomInfoMap.getOwner(roomid);
			try{
				if(!identity.equals(owner)){
					String content = "Sorry, command is invalid.";
					String error = jsonServer.sendMessage("System", content)+ "\n";
					out.write(error.getBytes("UTF-8"));
					out.flush();
				}else if(roomid.equals("MainHall")|| !RoomInfoMap.getRoomOwnerPair().containsKey(roomid)){
					String content = "Room name"+ roomid+ " "+"is invalid or non existent";
					String error = jsonServer.sendMessage("System", content)+ "\n";
					out.write(error.getBytes("UTF-8"));
					out.flush();
				}else{		
					for(int i=0; i< users.size();i++){
						Socket socket_temp = RoomInfoMap.getRoomInfo().get(roomid).get(users.get(i));
						RoomInfoMap.removeUser(roomid, users.get(i));
						RoomInfoMap.newJoin("MainHall", users.get(i), socket_temp);
						String roomchange = jsonServer.roomChange(users.get(i), roomid, "MainHall")+ "\n";
						List<Socket> socketOldRoom = RoomInfoMap.getSocketsOfRoom(roomid);
						List<Socket> socketMain = RoomInfoMap.getSocketsOfRoom("MainHall");
						for(int j=0; j < socketOldRoom.size();j++){
						DataOutputStream dataout = new DataOutputStream(socketOldRoom.get(j).getOutputStream());
						dataout.writeUTF(roomchange);
						dataout.flush();
						}
						for(int j=0; j < socketMain.size();j++){
							DataOutputStream dataout = new DataOutputStream(socketMain.get(j).getOutputStream());
							dataout.write(roomchange.getBytes("UTF-8"));
							dataout.flush();;
							}
					}
					RoomInfoMap.getRoomOwnerPair().remove(roomid);
					showRoomList();
				}
			}catch (EOFException e) {
				if (socket != null)
					socket.close();
					System.out.println("Client disconnected.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void kickUser(String roomid, int time, String identity){
		try{
			String owner = (String) RoomInfoMap.getOwner(roomid);
			String sender = (String) RoomInfoMap.getUserBySocket(socket);
			Socket socket_id = RoomInfoMap.getSocketById(roomid, identity);
			try{
				if(!owner.equals(sender)){
					String error = "Sorry, the command is invalid";
					String msg = jsonServer.sendMessage("System", error)+ "\n";
					out.write(msg.getBytes("UTF-8"));
					out.flush();
				}else if(!RoomInfoMap.getRoomOwnerPair().containsKey(roomid)
						||!RoomInfoMap.getRoomInfo().get(roomid).containsKey(identity)){
					String error = "Room name"+" "+roomid+" "+"or"+" "+"identity"+" "+identity+" "+ "is invalid";
					String msg = jsonServer.sendMessage("System", error)+ "\n";
					out.write(msg.getBytes("UTF-8"));
					out.flush();
				}else{					
					RoomInfoMap.removeUser(roomid, identity);
					RoomInfoMap.newJoin("MainHall", identity, socket_id);
					long joketime = System.currentTimeMillis();
					int kicktime = time*1000;
					RoomInfoMap.jokeJoin(roomid, identity, kicktime);
					RoomInfoMap.storeTime(roomid, identity, joketime);
					String roomchange = jsonServer.roomChange(identity, roomid, "MainHall")+ "\n";
					List<Socket> socketOldRoom = RoomInfoMap.getSocketsOfRoom(roomid);
					List<Socket> socketMain = RoomInfoMap.getSocketsOfRoom("MainHall");
					for(int j=0; j < socketOldRoom.size();j++){
					DataOutputStream dataout = new DataOutputStream(socketOldRoom.get(j).getOutputStream());
					dataout.write(roomchange.getBytes("UTF-8"));
					dataout.flush();
					}
					for(int j=0; j < socketMain.size();j++){
						DataOutputStream dataout = new DataOutputStream(socketMain.get(j).getOutputStream());
						dataout.write(roomchange.getBytes("UTF-8"));
						dataout.flush();;
						}
				}			
		}catch (EOFException e) {
			if (socket != null)
				socket.close();
				System.out.println("Client disconnected.");
		}
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void quit(){
		try{
			String identity = RoomInfoMap.getUserBySocket(socket);
			String roomid = RoomInfoMap.getRoomBySocket(socket);
			try{
				RoomInfoMap.removeUser(roomid, identity);
				if(identity.matches("^guest[1-9][1-9]*$")){
					char[] former_arr = identity.toCharArray();
					String id_no ="";
					for(int i=0;i<former_arr.length;i++){
						if(Character.isDigit(former_arr[i])){
							id_no+=former_arr[i];
						}
					}
					int id = Integer.parseInt(id_no);
					RoomInfoMap.getReuseIdentities().put(id, identity);
				}
				// if this identity is a owner of other rooms
				if(RoomInfoMap.getRoomOwnerPair().containsValue(identity)){	
					List<String> rooms = RoomInfoMap.getRoomsByOwner(identity);
					RoomInfoMap.changeOwner(identity, "");
					for(int i=0; i < rooms.size(); i++){
						if(RoomInfoMap.isRoomEmpty(rooms.get(i))){
							RoomInfoMap.getRoomOwnerPair().remove(rooms.get(i));
							RoomInfoMap.getRoomInfo().remove(rooms.get(i));
						}
					}
				}
				// if the room become empty after leaving
				if(RoomInfoMap.getRoomInfo().get(roomid)==null){
					String content = jsonServer.roomChange(identity, roomid, "")+ "\n";
					out.write(content.getBytes("UTF-8"));
					out.flush();
					out.close();
					socket.close();
				}else{
					//send message to other users in the current room
					List<Socket> sockets = RoomInfoMap.getSocketsOfRoom(roomid);
					String content = jsonServer.roomChange(identity, roomid, "")+ "\n";
					for(int i=0; i< sockets.size(); i++){
						DataOutputStream outdata = new DataOutputStream(sockets.get(i).getOutputStream());
						outdata.write(content.getBytes("UTF-8"));
						outdata.flush();
					}
					out.write(content.getBytes("UTF-8"));
					out.flush();
					out.close();
					socket.close();
				}				
			}catch (EOFException e) {
				if (socket != null)
					socket.close();
					System.out.println("Client disconnected.");
			}
		}catch (IOException e) {
			System.out.println("Client disconnected.");
			e.printStackTrace();
		}
	}

	public boolean isValid(char[] chars) {
		for (int i = 0; i < chars.length; i++) {
			if (Character.isDigit(chars[i]) || Character.isLetter(chars[i])) {
				return true;
			}
		}
		return false;
	}
	
}
