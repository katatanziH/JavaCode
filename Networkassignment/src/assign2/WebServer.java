package assign2;

import java.io.*;
import java.net.*;
import java.util.Locale;

import java.net.ServerSocket;

public class WebServer {
public static final int MYPORT= 8888;
	
	public static void main(String[] args) throws IOException {
		try {
			ServerSocket serverSocket = new ServerSocket(MYPORT);
			System.out.println("Server started");
			Socket clientSocket = null;
			while (true) {
				clientSocket = serverSocket.accept(); 
				/* Create the thread */
				new WebServerThread(clientSocket).start();
			}
		} catch (IOException io) {
			System.err.println("Error : Could not listen on port " + MYPORT);
			System.exit(1);
		} catch (Exception e) {
			System.err.println("Error : An exception occurred : " + e);
			e.printStackTrace();
			System.exit(1);
		}
	}

}
