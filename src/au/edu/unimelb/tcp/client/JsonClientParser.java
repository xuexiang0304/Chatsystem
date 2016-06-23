package au.edu.unimelb.tcp.client;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JsonClientParser {
	private boolean isQiut;
	
	public boolean isQiut() {
		return isQiut;
	}
	public void setQiut(boolean isQiut) {
		this.isQiut = isQiut;
	}
		public void jsonParser(JSONObject object){
		String type = (String) object.get("type");
		if(type.equals("newidentity")){
			String identity = (String) object.get("identity");
			String former = (String) object.get("former");
			if(former.equals("")){
			Client.setUserName(identity);
			System.out.println("Connected to localhost as" +" "+ identity);
			}else if(!former.equals(Client.getUserName())){
				System.out.println();
				System.out.println(object.get("former").toString()+" "+ "is"+" "+ identity+ " "+"now!");
			}else if(former.equals(identity)){
				System.out.println("Request identity invalid or in use");
			}else{
				Client.setUserName(identity);
				System.out.println(object.get("former").toString()+" "+ "is"+" "+ identity+ " "+"now!");
			}
		} else if (type.equals("roomlist")){
			JSONArray rooms = (JSONArray) object.get("rooms");
			for(int i=0; i< rooms.size();i++){
				JSONObject room = (JSONObject) rooms.get(i);
				System.out.println(room.get("roomid")+":"+ room.get("count")+ " "+"guests");
			}
		}else if(type.equals("roomchange")){
			String identity = (String) object.get("identity");
			String newRoom = (String) object.get ("roomid");
			String former =(String) object.get("former");
			if(!identity.equals(Client.getUserName())){
				System.out.println();
			}
			if(former.equals("")&&newRoom.equals("MainHall")){
				Client.setRoomName(newRoom);
				System.out.println(identity+ " "+ "moves to"+ " "+ newRoom);
			}
			//else if(former.equals("MainHall")&&newRoom.equals("MainHall")){
//				System.out.println("Kick time");
//			}
		else if(former.equals(newRoom)){
				System.out.println("The requested room is invalid or non existent.");
			}else if(newRoom.equals("")){
				System.out.println(identity+ " "+ "disconnected");
				if(identity.equals(Client.getUserName())){
					setQiut(true);
				}
			}else if(identity.equals(Client.getUserName())){
				Client.setRoomName(newRoom);
				System.out.println(identity+" "+ "moves from"+" "+ former+" "+ "to"+ " "+ newRoom);
			}else{
				System.out.println(identity+" "+ "moves from"+" "+ former+" "+ "to"+ " "+ newRoom);
			}
			
		}else if(type.equals("roomcontents")){
			String roomid = (String) object.get("roomid");
			System.out.print(roomid + " "+ "contains:");
			String owner = (String) object.get("owner");
			JSONArray UserList = (JSONArray) object.get("identities");
			for(int i=0; i< UserList.size();i++){
				String identity = UserList.get(i).toString();
				if(owner.equals(identity)){
				System.out.print(identity+"*"+" ");
				}else{
					System.out.print(identity+" ");
				}
			}
			System.out.println();
		}else if(type.equals("message")){
			String identity = (String) object.get("identity");
			String content = (String) object.get("content");
			if(!identity.equals(Client.getUserName())&& !identity.equals("System")){
			System.out.println();
			}
			System.out.println(identity+":"+" "+content);
		}
	}

}
