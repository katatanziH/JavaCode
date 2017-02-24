/*
  UDPEchoClient.java
  A simple echo client with no error handling
*/

package testingclt;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class UDPEchoClient {
    public static final int BUFSIZE= 1024;
    public static final int MYPORT= 0;
    public static final String MSG= "An Echo Message which is oke!";
	private static int messageTransferRate=0;
      
    public static void main(String[] args) throws IOException, InterruptedException {
	byte[] buf= new byte[BUFSIZE];
	if (args.length < 2) {
	    System.err.printf("usage: %s server_name port\n", args[1]);
	    System.exit(1);
	}
	messageTransferRate=Integer.valueOf(args[3]);
	/* Create socket */

	DatagramSocket socket= new DatagramSocket(null);
    /* Create local endpoint using bind() */
	SocketAddress localBindPoint= new InetSocketAddress(MYPORT);
	socket.bind(localBindPoint);
	
	
	/* Create remote endpoint */
	
	SocketAddress remoteBindPoint= new InetSocketAddress(args[0],Integer.valueOf(args[1]));

	/* Create datagram packet for sending message */
	DatagramPacket sendPacket=new DatagramPacket(MSG.getBytes(),MSG.length(),remoteBindPoint);
	
	System.out.println("Sending packets to" +  remoteBindPoint);
	
	/* Create datagram packet for receiving echoed message */
	DatagramPacket receivePacket= new DatagramPacket(buf, BUFSIZE);
	
	long beginTime = System.currentTimeMillis();
	for(int i=0; i<messageTransferRate;i++){
		socket.send(sendPacket);
		System.out.print("Waiting for Response.......");
		socket.receive(receivePacket);

		String receivedString= new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
		if (receivedString.compareTo(MSG) == 0)
		    System.out.printf("%d bytes sent and received\n", receivePacket.getLength());
		else
		    System.out.printf("Sent and received msg not equal!\n");
		InetAddress remote_addr = receivePacket.getAddress();
		System.out.println("Sent by :" + remote_addr.getHostAddress());
		System.out.println("Sent from :" + receivePacket.getPort());
		
		}
	if(beginTime >= 1000){
	System.err.println("YOu cant send more messages");
	}
	socket.close();
    }
}