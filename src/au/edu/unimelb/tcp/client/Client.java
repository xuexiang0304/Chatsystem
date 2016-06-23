package au.edu.unimelb.tcp.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.json.simple.parser.ParseException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class Client {
	private static String userName;
	private static String roomName;
	private static JsonUtilsClient jsonClient = new JsonUtilsClient();
    private static JsonClientParser jsonParser = new JsonClientParser();
	public static String getUserName() {
		return userName;
	}

	public static void setUserName(String userName) {
		Client.userName = userName;
	}

	public static String getRoomName() {
		return roomName;
	}

	public static void setRoomName(String roomName) {
		Client.roomName = roomName;
	}

	public static void main(String[] args) throws ParseException {
		CommandLineValues values = new CommandLineValues();
		CmdLineParser parser = new CmdLineParser(values);
		Socket socket = null;
		try {
			parser.parseArgument(args);			 
			// connect to a server listening on port 4444 on localhost
			socket = new Socket(values.getHost(), values.getPort());
			Thread reciver = new Thread(new ClientReciver(socket));
			reciver.start();
			// Preparing receiving streams
			DataOutputStream out = new DataOutputStream(
					socket.getOutputStream());

			// Reading from console
			Scanner cmdin = new Scanner(System.in);
			while (true) {
				String msg = cmdin.nextLine();
				String[] message = msg.split(" ");
				String output = "";
				// if input form console start with "#"--command
				if (msg.startsWith("#")) {
					if (msg.startsWith("#identitychange")) {
						if(message.length<2){
							System.out.println("Please enter a new identity!");
							System.out.print("["+Client.getRoomName()+"]"+" "+ Client.getUserName()+">");
						}else{
						String identity = message[1];						
						output = jsonClient.identityChange(identity)+ "\n";
						}
					}else if(msg.startsWith("#join")){
						if(message.length<2){
							System.out.println("Please enter a roomid!");
							System.out.print("["+Client.getRoomName()+"]"+" "+ Client.getUserName()+">");
						}else{
						String roomid = message[1];
						output = jsonClient.join(roomid)+ "\n";
						}
					}else if(msg.startsWith("#who")){
						if(message.length<2){
							System.out.println("Please enter a roomid!");
							System.out.print("["+Client.getRoomName()+"]"+" "+ Client.getUserName()+">");
						}else{
						String roomid = message[1];
						output = jsonClient.who(roomid)+ "\n";
						}
					}else if(msg.startsWith("#list")){
						output = jsonClient.requestList()+ "\n";
					}else if(msg.startsWith("#createroom")){
						if(message.length<2){
							System.out.println("Please enter a new roomid!");
							System.out.print("["+Client.getRoomName()+"]"+" "+ Client.getUserName()+">");
						}else{
						String roomid = message[1];
						output = jsonClient.createRoom(roomid)+ "\n";
						}
					}else if(msg.startsWith("#kick")){
						if(message.length<4){
							System.out.println("Please enter all needed information!");
							System.out.print("["+Client.getRoomName()+"]"+" "+ Client.getUserName()+">");
						}else{
						String roomid = message[1];
						int time = Integer.parseInt(message[2]);
						String identity = message[3];
						output = jsonClient.Kick(roomid, time, identity)+ "\n";
						}
					}else if(msg.startsWith("#delete")){
						if(message.length<2){
							System.out.println("Please enter all needed information!");
							System.out.print("["+Client.getRoomName()+"]"+" "+ Client.getUserName()+">");
						}else{
						String roomid = message[1];
						output = jsonClient.deleteRoom(roomid)+ "\n";
						}
					}else if(msg.startsWith("#quit")){
						output = jsonClient.quit()+ "\n";					
					}else {
						System.out.println("Please enter valid command!");
						System.out.print("["+Client.getRoomName()+"]"+" "+ Client.getUserName()+">");
					}
				}else{
					if(!(msg == null||msg.equals(""))){
					output = jsonClient.sendMessage(msg)+ "\n";
					}else{
						System.out.println("Please enter a valid message!");
					}
				}
				if (jsonParser.isQiut()) {
					break;
				}else if(!(output == null||output.equals(""))){
				out.write(output.getBytes("UTF-8"));
				out.flush();
				// forcing TCP to send data immediately
				}
			}
			cmdin.close();
			out.close();
			socket.close();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (CmdLineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
