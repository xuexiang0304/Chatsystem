package au.edu.unimelb.tcp.server;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class JsonUtilsServer {
	// type to identify the type of the protocol message
	private static final String NewIdentity="newidentity",
			RoomChange="roomchange", RoomContents="roomcontents",RoomList="roomlist", Message = "message",
			Error = "error";
	
	
	/**
	 * this method is called when the client connects to the server first time 
	 * OR when the client want to change identity.
	 * @param former
	 * @param identity
	 * @return the string of a Json Object
	 */
	@SuppressWarnings("unchecked")
	public String newIdentity(String former,String identity){
		String json = null;
		JSONObject obj = new JSONObject();
		obj.put("type", NewIdentity);
		obj.put("former", former);
		obj.put("identity", identity);
		
		json = obj.toString();
		
		return json;
	}

	@SuppressWarnings("unchecked")
	public String roomChange(String identity,String former,String room_id){
		String json = null;
		JSONObject obj = new JSONObject();
		obj.put("type", RoomChange);
		obj.put("identity", identity);
		obj.put("former", former);
		obj.put("roomid", room_id);
		
		json = obj.toString();
		
		return json;
	}
	
	@SuppressWarnings("unchecked")
	public String roomContents(String room){
		String json = null;
		JSONObject obj = new JSONObject();
		JSONArray guests = new JSONArray();
		List<String> userList = new ArrayList<String>();
		userList = RoomInfoMap.getUsers(room);
		for(int i=0; i< userList.size();i++){
			guests.add(userList.get(i));
		}
		String owner = RoomInfoMap.getOwner(room);
		obj.put("type", RoomContents);
		obj.put("roomid", room);
		obj.put("identities", guests);
		obj.put("owner", owner);
		
		json = obj.toString();
		
		return json;
	}	

	@SuppressWarnings("unchecked")
	public String roomList(List<String> roomlist){
		String json = null;
		JSONObject obj = new JSONObject();
		JSONArray rooms = new JSONArray();
		for(int i=0; i< roomlist.size();i++){
			JSONObject room = new JSONObject();
			String roomname = roomlist.get(i);				
			List<String> users = new ArrayList<String>();
			int size;
			users = RoomInfoMap.getUsers(roomlist.get(i));
			if(users == null){
				size =0;
			}else{
			size = users.size();
			}
			room.put("roomid", roomname);
			room.put("count", size);
			rooms.add(room);
		}
		obj.put("type",RoomList);
		obj.put("rooms", rooms);
		
		json = obj.toString();
		
		return json;
	}
	
	@SuppressWarnings("unchecked")
	public String sendMessage(String identity,String content){
		String json = null;
		JSONObject obj = new JSONObject();
		obj.put("type", Message);
		obj.put("identity", identity);
		obj.put("content", content);
		
		json = obj.toString();
		
		return json;
	}
}
