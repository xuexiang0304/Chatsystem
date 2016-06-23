package au.edu.unimelb.tcp.server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RoomInfoMap {
	private static Map<String,Map<String,Socket>> roomInfo=new ConcurrentHashMap<String,Map<String,Socket>>();
    private static Map<String,String> roomOwnerPair = new ConcurrentHashMap<String,String>();
    // store the identity and time set by the owner
    private static Map<String,Map<String,Integer>> jokeInfo=new ConcurrentHashMap<String,Map<String,Integer>>();
    //store the start time of the kick and the identity
    private static Map<String,Map<String,Long>> timeInfo=new ConcurrentHashMap<String,Map<String,Long>>();
    // store the identities can be reused
    private static Map<Integer,String> reuseIdentities = new ConcurrentHashMap<Integer,String>();
    
	public static Map<String, Map<String, Socket>> getRoomInfo() {
		return roomInfo;
	}

	public static void setRoomInfo(Map<String, Map<String, Socket>> roomInfo) {
		RoomInfoMap.roomInfo = roomInfo;
	}

	public static Map<String, String> getRoomOwnerPair() {
		return roomOwnerPair;
	}

	public static void setRoomOwnerPair(Map<String, String> roomOwnerPair) {
		RoomInfoMap.roomOwnerPair = roomOwnerPair;
	}
	
	public static Map<String, Map<String, Integer>> getJokeInfo() {
		return jokeInfo;
	}

	public static void setJokeInfo(Map<String, Map<String, Integer>> jokeInfo) {
		RoomInfoMap.jokeInfo = jokeInfo;
	}

	public static Map<Integer, String> getReuseIdentities() {
		return reuseIdentities;
	}

	public static void setReuseIdentities(Map<Integer, String> reuseIdentities) {
		RoomInfoMap.reuseIdentities = reuseIdentities;
	}

	/**
	 * create a room or join a room
	 * @param roomid
	 * @param username
	 * @param Owner
	 */
	@SuppressWarnings("unchecked")
	public static void newJoin(String roomid, String username, Socket socket){
		Map subInfo = (Map) roomInfo.get(roomid);
		if(subInfo == null){
			subInfo = new HashMap();
			roomInfo.put(roomid, subInfo);
		}
		subInfo.put(username, socket);
	}
	
	/**
	 * to store people who are kicked out the room
	 * @param roomid
	 * @param username
	 * @param socket
	 */
	public static void jokeJoin(String roomid, String username,int time){
		Map<String,Integer> subInfo = jokeInfo.get(roomid);
		if(subInfo == null){
			subInfo = new HashMap<String,Integer>();
			jokeInfo.put(roomid, subInfo);
		}
		subInfo.put(username, time);
	}
	
	public static void storeTime(String roomid, String username,Long time){
		Map<String,Long> subInfo = timeInfo.get(roomid);
		if(subInfo == null){
			subInfo = new HashMap<String,Long>();
			timeInfo.put(roomid, subInfo);
		}
		subInfo.put(username, time);
	}
	
	public static Long getTime(String roomid,String identity){
		Map<String,Long> users = timeInfo.get(roomid);
		for(Map.Entry<String,Long> entry: users.entrySet()){
			if(entry.getKey().equals(identity)){
				return entry.getValue();
			}
		}
		return null;
	}
	
	public static Integer getCreateTime(String roomid,String identity){
		Map<String,Integer> users = jokeInfo.get(roomid);
		for(Map.Entry<String,Integer> entry: users.entrySet()){
			if(entry.getKey().equals(identity)){
				return entry.getValue();
			}
		}
		return null;
	}
	
	public static boolean isKick(String roomid, String identity){
		Map<String,Integer> users = jokeInfo.get(roomid);
		if(users!=null){
			return users.containsKey(identity);
		}
		return false;
	}
	
	/**
	 * create a new room and put in the record in the Map
	 * @param roomid
	 * @param Owner
	 */
	public static void createRoom(String roomid, String Owner){
		if(!roomOwnerPair.containsKey(roomid)){
			roomOwnerPair.put(roomid, Owner);
		}
	}
	
	/**
	 * get all users of a particular room
	 * @param roomid
	 * @return
	 */
	public static List<String> getUsers(String roomid){
		List<String> userList = new ArrayList<String>();
		Map subInfo = (Map)roomInfo.get(roomid);
		if(subInfo==null){
			return null;
		}else{
		Set users = subInfo.keySet();
		for(Object user: users){
			userList.add(user.toString());
		}		
		return userList;
		}
	}
	
	public static String getUserBySocket(Socket socket){
		String user ="";
		Set rooms = roomInfo.keySet();
		for(Object room : rooms){
			String roomName = room.toString();
			Map<String, Socket> subinfo = roomInfo.get(roomName);
			for(Map.Entry<String, Socket> entry : subinfo.entrySet()){
				if(entry.getValue()!=null && entry.getValue().equals(socket)){
					user = entry.getKey();
				}
			}
		}
		return user;
	}
	
	public static String getRoomBySocket(Socket socket){
		String roomid = "";
		Set rooms = roomInfo.keySet();
		for(Object room : rooms){
			String roomName = room.toString();
			Map<String, Socket> subinfo = roomInfo.get(roomName);
			for(Map.Entry<String, Socket> entry : subinfo.entrySet()){
				if(entry.getValue()!=null && entry.getValue().equals(socket)){
					roomid = roomName;
				}
			}
		}
		return roomid;
	}
	/**
	 * get all room names
	 * @return
	 */
	public static List<String> getRoomList(){
		List<String> roomList = new  ArrayList<String>();
		Set rooms = roomOwnerPair.keySet();
		for(Object room : rooms){
			roomList.add(room.toString());
		}
		return roomList;
	}
	/**
	 * get the owner of a particular room
	 * @param roomid
	 * @return
	 */
	public static String getOwner(String roomid){
		String owner = null;
		if(roomid.equals("")){
			owner = "MainHall";
		}else{
		owner= (String)roomOwnerPair.get(roomid);
		}
		return owner;
	}
	/**
	 * remove a particular from a particular room
	 * @param roomid
	 * @param username
	 */
	public static void removeUser(String roomid, String username){
		if(roomInfo.containsKey(roomid)){
			Map<String,Socket> subInfo = roomInfo.get(roomid);
			if(subInfo.containsKey(username)){
				subInfo.remove(username);
			}
		}
	}
	
	public static void removeKick(String roomid, String username){
		if(jokeInfo.containsKey(roomid)){
			Map<String,Integer> subInfo = jokeInfo.get(roomid);
			if(subInfo.containsKey(username)){
				subInfo.remove(username);
			}
			Set<String> users = subInfo.keySet();
			System.out.println("remove"+users);
		}
		if(timeInfo.containsKey(roomid)){
			Map<String,Long> subInfo = timeInfo.get(roomid);
			if(subInfo.containsKey(username)){
				subInfo.remove(username);
			}
		}
	}
	
	public static List<Socket> getAllSocket(){
		List<Socket> sockets = new ArrayList<Socket>();
		Set rooms = roomInfo.keySet();
		for(Object room : rooms){
			String roomName = room.toString();
			Map subinfo = (Map) roomInfo.get(roomName);
			Set users = subinfo.keySet();
			for(Object user: users){
				 String userName = user.toString();
				 sockets.add((Socket) subinfo.get(userName));
			}
		}
		return sockets;
	}
	
	public static List<Socket> getSocketsOfRoom(String roomid){
		List<Socket> sockets = new ArrayList<Socket>();
		Map<String, Socket> subInfo = roomInfo.get(roomid);	
		if(subInfo!=null&&!subInfo.isEmpty()){			
		for(Map.Entry<String, Socket> entry : subInfo.entrySet()){
			sockets.add(entry.getValue());
		}
		}
		return sockets;
	}
	
	public static Socket getSocketById(String roomid,String identity){
		Socket socket = null;
		Map<String, Socket> subInfo = roomInfo.get(roomid);
		for(Map.Entry<String, Socket> entry : subInfo.entrySet()){
			if(entry.getKey().equals(identity)){
				socket = entry.getValue();
			}
		}
		return socket;
	}
	
	public static boolean isExistId(String newIdentity){
		Set rooms = roomInfo.keySet();
		for(Object room : rooms){
			String roomName = room.toString();
			Map subinfo = (Map) roomInfo.get(roomName);
			return subinfo.containsKey(newIdentity);
		}
		return false;
	}
	
	public static void changeOwner(String former, String newIdentity){
		for(Map.Entry<String, String> entry : roomOwnerPair.entrySet()){
			if(entry.getValue().equals(former)){
				roomOwnerPair.put(entry.getKey(), newIdentity);
			}
		}
	}
	
	public static boolean isRoomEmpty(String roomid){
			return roomInfo.get(roomid).isEmpty();
	}
	
	public static List<String> getRoomsByOwner(String owner){
		List<String> rooms = new ArrayList<String>();
		for(Map.Entry<String, String> entry : roomOwnerPair.entrySet()){
			if(entry.getValue().equals(owner)){
				rooms.add(entry.getKey());
			}
		}
		return rooms;
	}
	
	public static String getsmallestIdenity(){
		if(!reuseIdentities.isEmpty()){
			Object[] key_arr =  reuseIdentities.keySet().toArray();
			Arrays.sort(key_arr);
			String identity = reuseIdentities.get(key_arr[0]);
			reuseIdentities.remove(key_arr[0]);
			return identity;
		}else{
			return null;
		}
	}
}
