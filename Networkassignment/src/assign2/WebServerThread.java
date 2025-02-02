package assign2;
import java.io.*;
import java.net.*;
//import java.util.Date;

public class WebServerThread extends Thread {
	
	private Socket clientsocket;
	private final String ROOT_DIRECTORY = "C:/Users/Harrison/Desktop";
	private PrintWriter out;
	private String content;
	


	public WebServerThread(Socket sock) {
		clientsocket=sock;
	}
	
	public void run() {
		try {
			// Create reader and writer
			System.out.println("Connection to client......................");
	    BufferedReader br = new BufferedReader(new InputStreamReader(clientsocket.getInputStream()));
	    PrintWriter  out = new PrintWriter(new OutputStreamWriter(clientsocket.getOutputStream()));
	       
	        // Handle the input
	        String input = br.readLine();
	        if (input == null) {
	        	System.out.println("No content to read");
	        	System.exit(1);
	        }
	        
	        // Using PuTTY makes the input start with charachters like ���'��
	        // So we substring to get a proper input
			if (!input.startsWith("GET")) {
				System.out.println("Using PuTTY");
				String[] array = input.split("GET");
				input = "GET" + array[1];
			}
	        //
	        System.out.println(input);
	        
	        // Get the asked ressource from input, and set content
	        String ressource = manageInput(input);
	        System.out.println("Ressource asked :" + ressource);
	        
	        // Manage ressource to get it and send an HTTP response
	        processHandling(ressource);
	        
	        // Close reader/writer and socket
	        out.close();
	        br.close();
	        clientsocket.close();
	        System.err.println("Connection closed");
		} catch (Exception e) {
			System.err.println("Error : An exception occurred : " + e);
			e.printStackTrace();
			System.exit(1);
		}
		interrupt();
	}
	
	public String manageInput(String input) {
		// The input is in format GET /ressource HTTP/1.1
        // We split the string by " " and get the 2nd 
		String[] inputArray = input.split(" ");
		String ressource = inputArray[1].toLowerCase();
		
		// Handle if extension is .htm and transform it in .html		
		if (ressource.endsWith(".htm")) {
			ressource += "l";
		}
		// Handle if there is a space in the ressource name
		if (ressource.contains("%20")) {
			ressource = ressource.replaceAll("%20", " ");
		}
		
		// Set the differents contents in function of the extension
		// Here we only handle .html, .png and directory
		if (ressource.endsWith(".html")) {
			content = "text/html";
		} else if (ressource.endsWith(".png")) {
			content = "image/png";
		} else {
			/* If it is a folder, get index.html file*/
			content = "text/html";
			ressource += "/index.html";
		}
		return ressource;
	}
	
	public void processHandling(String ressource){
		File ressourceFile = new File(ROOT_DIRECTORY, ressource);
        System.out.println(ressourceFile);
		// Reader for the file
		FileInputStream fileStream = null;
		String httpCode = null;
		byte[] dataBuffer = new byte[(int)ressourceFile.length()];
		try {
			// Read file
			fileStream = new FileInputStream(ressourceFile);
			Thread.sleep(5000);
			fileStream.read(dataBuffer);
			fileStream.close();
			// No problem detected during the process
			httpCode = "200";
//			throw new Exception(); // Code to generate a 500 error
		} catch (FileNotFoundException fnfe) {
			// File can't be found, error 404
			String message = fnfe.getMessage();
			// Check if access is authorized
			if(message.contains("Access denied") || message.contains("Accès refusé")) {
				httpCode = "403";
			} else {
				httpCode = "404";
			}
		} catch (Exception e) {
			// Internal error, error 500
			httpCode = "500";
		}
		System.out.println("Response = " + httpCode);
		sendResponse(httpCode, dataBuffer);
	}
	
	// Send a HTTP response, depending on p parameter, that is a error or a successfull response
	public void sendResponse (String httpCode, byte[] dataBuffer) {
		try {
			String header = null;
			String title = null;
			boolean error = false;
			switch (httpCode) {
				case "200" :
					header = "HTTP/1.1 200 OK";
					break;
				case "404" :
					header = "HTTP/1.1 404 File Not Found";
					title = "404 File Not Found";
					error = true;
					break;
				case "403" :
					header = "HTTP/1.1 403 Access Forbidden";
					title = "403 Access Forbidden";
					error = true;
					break;
				case "500" :
					// equivalent of error 500
					header = "HTTP/1.1 500 Internal Server Error";
					title = "500 Internal Server Error";
					error = true;
					break;
				default :
					System.err.println("Error : Exception in HTTP code");
					System.exit(1);
					break;
			}
			// Send the response
			out.println(header);
			out.println("Server: Java Webserver ");
			//out.println("Date: " + new Date());
			if (error) {
				content = "text/html";
			}
			out.println("Content-Type: " + content);
			out.println();
			if (error) {
				out.println("<HTML>");
				out.println("<HEAD><TITLE>Error</TITLE></HEAD>");
				out.println("<BODY>");
				out.println("<H1>" + title + "</H1>");
				out.println("</BODY>");
				out.println("</HTML>");
				out.flush();
			} else {
				out.flush();
				// Output data in buffer with a BufferedOutputStream
				BufferedOutputStream bufferOut = new BufferedOutputStream(clientsocket.getOutputStream());
				bufferOut.write(dataBuffer, 0, dataBuffer.length);
				bufferOut.flush();
			}
			System.out.println("Response sent");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}