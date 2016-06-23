package au.edu.unimelb.tcp.client;

import org.json.simple.JSONObject;

public class JsonUtilsClient {
	private static final String Join = "join", IdentityChange="identitychange", Who="who", RequestList = "list",
			CreateRoom="createroom",Kick="kick", Delete="delete", Message="message",Quit="quit";	
	
	@SuppressWarnings("unchecked")
	public String identityChange(String identity){
		String json = null;
		JSONObject obj = new JSONObject();
		obj.put("type", IdentityChange);
		obj.put("identity", identity);
		
		json = obj.toString();
		
		return json;
	}
		
	@SuppressWarnings("unchecked")
	public String join(String room_id){
		String json = null;
		JSONObject obj = new JSONObject();
		obj.put("type", Join);
		obj.put("roomid", room_id);
		
		json = obj.toString();
		
		return json;
	}
	
	@SuppressWarnings("unchecked")
	public String who(String room_id){
		String json = null;
		JSONObject obj = new JSONObject();
		obj.put("type", Who);		
		obj.put("roomid", room_id);
		
		json = obj.toString();
		
		return json;
	}
	
	@SuppressWarnings("unchecked")
	public String createRoom(String room_id){
		String json = null;
		JSONObject obj = new JSONObject();
		obj.put("type", CreateRoom);		
		obj.put("roomid", room_id);
		
		json = obj.toString();
		
		return json;
	}
	
	@SuppressWarnings("unchecked")
	public String requestList(){
		String json = null;
		JSONObject obj = new JSONObject();
		obj.put("type", RequestList);		
		
		json = obj.toString();
		
		return json;
	}
	
	@SuppressWarnings("unchecked")
	public String Kick(String roomid, Integer time, String identity){
		String json = null;
		JSONObject obj = new JSONObject();
		obj.put("type", Kick);
		obj.put("roomid", roomid);
		obj.put("time", time);
		obj.put("identity", identity);
		
		json = obj.toString();
		
		return json;
	}
	
	@SuppressWarnings("unchecked")
	public String deleteRoom(String room_id){
		String json = null;
		JSONObject obj = new JSONObject();
		obj.put("type", Delete);		
		obj.put("roomid", room_id);
		
		json = obj.toString();
		
		return json;
	}
	
	@SuppressWarnings("unchecked")
	public String sendMessage(String content){
		String json = null;
		JSONObject obj = new JSONObject();
		obj.put("type", Message);		
		obj.put("content", content);
		
		json = obj.toString();
		
		return json;
	}
	
	@SuppressWarnings("unchecked")
	public String quit(){
		String json = null;
		JSONObject obj = new JSONObject();
		obj.put("type", Quit);		
		
		json = obj.toString();
		
		return json;
	}

}
