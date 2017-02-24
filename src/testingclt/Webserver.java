package testingclt;
import java.util.*;
import java.io.*;
import java.net.*;
public class Webserver {
	private static ServerSocket socket;

	public static void main(String[] args)throws IOException {
		// listening to port 8888
		socket =new ServerSocket(8888);
		System.out.println("Listening for connection on port 8888......");
		while(true){
		  try{
			  // waiting the client to connect
			  Socket client = socket.accept();
			 new ClientRequest(client);// handles client in another thread
			/*Thread tr = new Thread(request);
			tr.start();*/
		  }
		  catch (Exception x){
			  System.out.println(x);
		  }
			
		}
	}
}
	// Reads HTTP request and responds
	class ClientRequest implements Runnable {
		final static String CRLF = "\r\n";
		private Socket socket; // Socket  from the server
		// Here  we start the thread
		public ClientRequest(Socket socket)throws Exception{
			this.socket = socket;
			
		}
		// Read the HTTP Request,
		public void run(){
			try{
				// open the socket connection
				BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintStream output = new PrintStream(new BufferedOutputStream(socket.getOutputStream()));
				//read the file name from the first inputline
				String requestLine =input.readLine();
				//Display the requestLine
				System.out.println();
				System.out.println(requestLine); 
				String filename ="";
				StringTokenizer st = new StringTokenizer(requestLine);
				try{
					
			// parse the file from the GET command
					if(st.hasMoreElements() && st.nextToken().equalsIgnoreCase("GET") && st.hasMoreElements())
						filename = st.nextToken();
					else
						throw new FileNotFoundException();
					// open the file
					InputStream f = new FileInputStream(filename);
					// determine the mime type and print the Header
					String mimeType = "text/palin";
					if(filename.endsWith(".htm") ||  filename.endsWith(".html"))
						mimeType ="text/html";
					else if(filename.endsWith(".png"))
						mimeType = "image/png";
					// send file contents to client
					byte[] bt = new byte[4096];
					int n;
					while((n=f.read(bt))>0)
						output.write(bt, 0, n);
					output.close();
					
				}
				catch (FileNotFoundException x){
					output.println("HTTP/1.1 404 Not Found\r\n" + "Content-Type: text/html\r\n\r\n" +
				"<html><head></head><body>" + filename +"notfound</body></html>\n");
					output.close();
				}
				 
			}
			catch (IOException x){
				System.out.println(x);
			}
		}
     
	}

