package au.edu.unimelb.tcp.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ClientThread implements Runnable {
	private Socket socket;
	private int id;	
	private ServerHandler handler;
	private BufferedReader in;
	private JsonServerParser serverParser;
	public ClientThread(Integer id,Socket socket) {
		this.socket = socket;
		this.id = id;	
		handler = new ServerHandler(socket);
		serverParser = new JsonServerParser(socket);
	}	
	@Override
	public void run() {
		try {				
			onOpen();
			
			   BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			try {
				while (true) {
					String msg = in.readLine();
					JSONParser parser = new JSONParser();
				//	System.out.println(msg);
     				JSONObject object = (JSONObject) parser.parse(msg);
					serverParser.jsonServer(object);

					if (object.get("type").equals("quit"))
						break;
				}
				in.close();
				socket.close();
			} catch (EOFException e) {
				if (socket != null)
					socket.close();						
				System.out.println("Client disconnected.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e){
			handler.quit();
			System.out.println("Client disconnected.");
		}		
	
	}	
	public void onOpen(){
			String identity;
		    if(RoomInfoMap.getReuseIdentities().isEmpty()){
		    identity = "guest"+id;
		    }else{
		    identity =RoomInfoMap.getsmallestIdenity();
		    }
			handler.newIdentity("", identity,socket);
			handler.joinRoom("MainHall");
	}


}
