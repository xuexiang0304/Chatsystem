package au.edu.unimelb.tcp.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ClientReciver implements Runnable {
	private Socket socket;
	private JsonClientParser jsonParser;
	private static  BufferedReader in;
	public ClientReciver(Socket socket){
		this.socket = socket;		
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			jsonParser = new JsonClientParser();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void run(){
		try {
			int i=0;
			while(i<4){
				String response = in.readLine();
				JSONParser parser = new JSONParser();
				JSONObject object = (JSONObject) parser.parse(response);
				jsonParser.jsonParser(object);				
				i++;
			}
			System.out.print("["+ Client.getRoomName()+"]"+ " "+ Client.getUserName()+">");
			while(true){
				String response = in.readLine();
				if(response!=null){
				JSONParser parser = new JSONParser();
				JSONObject object = (JSONObject) parser.parse(response);

				jsonParser.jsonParser(object);
				
				if(jsonParser.isQiut()){
					break;
				}
				System.out.print("["+ Client.getRoomName()+"]"+ " "+ Client.getUserName()+">");
				}
			}
			
			in.close();
			socket.close();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
