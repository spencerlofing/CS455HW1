package cs455.overlay.transport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import cs455.overlay.node.Node;

public class TCPServerThread implements Runnable {

	private ServerSocket serverSocket;
	private Node creationNode;
	private int portNumber;
	private String ipaddress;
	private Socket connectionSocket;
	
	public TCPServerThread(int portNum, Node n) throws IOException {
		creationNode = n;
		boolean listening = true;
		serverSocket = new ServerSocket(portNum);
		portNumber = serverSocket.getLocalPort();
		//ipaddress = serverSocket.getLocalSocketAddress().toString();
		ipaddress = n.getIPAddress();
		System.out.println("Created serverSocket on " + ipaddress + ":" + portNumber);
	}
		public void run()
		{
			boolean listening = true;
			while(listening)
			{
				try
				{
					Socket connectionSocket = serverSocket.accept();
					TCPSender t = new TCPSender(connectionSocket);
					creationNode.addTosocketToSender(connectionSocket.getInetAddress().toString().split("/")[1] + ":" + connectionSocket.getPort(), t);
					//create a new thread
					TCPReceiverThread receive = new TCPReceiverThread(connectionSocket, creationNode);
					new Thread(receive).start();
				}
				catch (IOException ioe)
				{
					System.out.println(ioe.getMessage());
				}
			}
		}
		public int getPortNumber()
		{
			return portNumber;
		}
}
