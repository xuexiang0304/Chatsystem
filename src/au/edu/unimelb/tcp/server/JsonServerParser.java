package au.edu.unimelb.tcp.server;

import java.net.Socket;

import org.json.simple.JSONObject;

public class JsonServerParser {
	private Socket socket;
	private ServerHandler serverhandler;
	public JsonServerParser(Socket socket){
		this.socket = socket;
		serverhandler = new ServerHandler(socket);
	}
	public void jsonServer(JSONObject object){
		String type = (String) object.get("type");
		if(type.equals("identitychange")){
			String identity = (String) object.get("identity");
			serverhandler.identityChange(identity);
		}else if (type.equals("join")){
			String roomid = (String) object.get("roomid");
			serverhandler.joinRoom(roomid);
		}else if (type.equals("who")){
			String roomid = (String) object.get("roomid");
			serverhandler.showRoomContent(roomid);
		}else if (type.equals("list")){
			serverhandler.showRoomList();
		}else if (type.equals("createroom")){
			String roomid = (String) object.get("roomid");
			serverhandler.createRoom(roomid);
		}else if (type.equals("kick")){
			String roomid = (String) object.get("roomid");
			int time = Integer.parseInt(object.get("time").toString());
			String identity = (String) object.get("identity");
			serverhandler.kickUser(roomid, time, identity);
		}else if (type.equals("delete")){
			String roomid = (String) object.get("roomid");
			serverhandler.deleteRoom(roomid);
		}else if (type.equals("message")){
			String content = (String) object.get("content");
			serverhandler.sendMessage(content);
		}else if(type.equals("quit")){
			serverhandler.quit();
		}		
	}

}
