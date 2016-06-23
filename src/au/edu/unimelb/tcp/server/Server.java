package au.edu.unimelb.tcp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Server {
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = null;
		//count the number of guests
		int client_id = 0;
		// Mapping between name and roomid		
		try {
			//RoomInfoMap roomMap = new RoomInfoMap();
			//Server is listening on port 4444
			serverSocket = new ServerSocket(4444);			
			System.out.println("Server is listening...");

			while (true) {
				//Server waits for a new connection
				Socket socket = serverSocket.accept();
				// Java creates new socket object for each connection.
				client_id += 1;
				
				System.out.println("Client Connected...");
														
				// A new thread is created per client
				Thread client = new Thread(new ClientThread(client_id,socket));
				// It starts running the thread by calling run() method
				client.start();
			}

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (serverSocket != null)
				serverSocket.close();
		}
	}

}
