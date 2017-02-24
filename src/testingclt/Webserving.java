package testingclt;
import java.io.* ;
import java.net.* ;
import java.util.* ;
public class Webserving {

	public static void main(String[] args) throws Exception {
		// Establish the listen socket.
		ServerSocket socket = new ServerSocket(8888);
		System.out.println("Listening for connection on port 8888......");
		// Process HTTP service requests in an infinite loop.
		while (true) {
		// Listen for a TCP connection request.
		Socket client = socket.accept();

		// Construct an object to process the HTTP request message.
		CltRequest request = new CltRequest( client );

		// Create a new thread to process the request.
		Thread thread = new Thread(request);
       // Start the thread.
		thread.start();
		}
		}
	}

		 class CltRequest implements Runnable
		{
		//returning carriage return (CR) and a line feed (LF)
		final static String CRLF = "\r\n";
		Socket socket;

		// Constructor
		 public CltRequest(Socket socket) throws Exception
		 {
		  this.socket = socket;
		 }

		// Implement the run() method of the Runnable interface.
		 //Within run(), we explicitly catch and handle exceptions with a try/catch block.
		public void run()
		{
		try {processRequest();
		}
		catch (Exception e) 
		{
			System.out.println(e);
		}
		}

		private void processRequest() throws Exception
		{
		// Get a reference to the socket's input and output streams.
		InputStream input = socket.getInputStream();
		DataOutputStream output = new DataOutputStream(socket.getOutputStream());

		// Set up input stream filters.
		// Page 169 10th line down or so...
		BufferedReader br = new BufferedReader(new InputStreamReader(input));//reads the input data

		// Get the request line of the HTTP request message.
		String requestLine = br.readLine();// get /path/file.html version of http

		// Display the request line.
		System.out.println();
		System.err.println(requestLine);
	
		// Extract the filename from the request line.
		StringTokenizer tokens = new StringTokenizer(requestLine);
		// this is a input method with deliminators
		
		tokens.nextToken(); // skip over the method, which should be "GET"
		
		String fileName = tokens.nextToken();
		String fileName2=fileName.replaceFirst("/", "");
		
		if ( fileName.contains("Admin")){
			System.err.println( fileName2 + "Exist = " + new File(fileName2).exists());
			 statusLine = "HTTP/1.0 404 Not Found" + CRLF;//common error message
			 contentTypeLine = "Content-type: " + "text/html" + CRLF;//content info
			 entityBody = "<html>" +
			  " <head> <title> Not Found </title> </head> " +
			  "<body> Not Found </body> </html>";
		}
		// Append  "/" with "index.html"
		if(fileName.endsWith("/"))
        	 fileName+="index.html";
		 // Remove leading / from filename
		while(fileName.indexOf("/")==0)
			fileName=fileName.substring(1);
		// Prepend a "." so that file request is within the current directory.
		//fileName = "." + fileName;
         //Replace "/" with "\" in the path on the pc
		fileName =fileName.replace('/', File.separator.charAt(0));
		//Open the requested file.
		FileInputStream fis = null;
		boolean fileExists = new File(fileName2).exists();
		try {
		 fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
		 fileExists = false;
		}
		//Construct the response message.
		String statusLine = null;
		String contentTypeLine = null;
		String entityBody = null;
		
		if (fileExists) {
		 statusLine = "HTTP/1.0 200 OK" + CRLF; //common success message
		 contentTypeLine = "Content-type: " + contentType( fileName ) + CRLF;
		 }
		

		else {
			System.err.println( fileName2 + "Exist = " + new File(fileName2).exists());
		 statusLine = "HTTP/1.0 404 Not Found" + CRLF;//common error message
		 contentTypeLine = "Content-type: " + "text/html" + CRLF;//content info
		 entityBody = "<html>" +
		  " <head> <title> Not Found </title> </head> " +
		  "<body> Not Found </body> </html>";
		
		}
		
			
		

		System.out.println("Response: "+statusLine +contentTypeLine + "\n" + entityBody);
		//Send the status line.
		output.writeBytes(statusLine);
    //Send the content type line.
		output.writeBytes(contentTypeLine);
	
      //Send the entity body.
		if (fileExists) {
		 sendBytes(fis, output);
		 output.writeBytes(statusLine);//Send the status line.
		output.writeBytes(contentTypeLine);//Send the content type line.
		 fis.close();
		} else {
		 output.write(statusLine.getBytes());//Send the status line.
		 output.write(contentTypeLine.getBytes());//Send the an html error message info body.
		 output.write(entityBody.getBytes());//Send the content type line.
		}
		//print out file request to console
		System.out.println(fileName);
		// Get and display the header lines.
		String headerLine = null;
		while ((headerLine = br.readLine()).length() != 0) {
		System.out.println(headerLine);
		}
		// Close streams and socket.
		output.close();
		br.close();
		socket.close();
		}
		//return the file types
		private static String contentType(String fileName)
		{
		 if(fileName.endsWith(".htm") || fileName.endsWith(".html")) 
		 {
			 return "text/html";
		 }
		 if(fileName.endsWith(".png")) 
		 {
			 return "image/png";
		 }
		 return "application/octet-stream";
		}
        //set up input output streams
		private static void sendBytes(FileInputStream fis, OutputStream output) throws Exception
		{
		   // Construct a 1K buffer to hold bytes on their way to the socket.
		   byte[] buffer = new byte[2048];
		   int bytes = 0;

		   // Copy requested file into the socket's output stream.
		   while((bytes = fis.read(buffer)) != -1 )// read() returns minus one, indicating that the end of the file
		   {
		      output.write(buffer, 0, bytes);
	}

}

		}