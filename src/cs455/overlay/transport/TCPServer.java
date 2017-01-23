package cs455.overlay.transport;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer {
	public TCPServer(int portNumber) {
		ServerSocket serverSocket = null;
		boolean listening = true;
		try 
		{
			serverSocket = new ServerSocket(portNumber);
			System.out.println("Created new server on port: " + portNumber);
		}
		catch(IOException ioe)
		{
			System.out.println(ioe.getMessage());
		}
		while(listening)
		{
			try
			{
				Socket clientSocket = serverSocket.accept();
				//create a new thread
				TCPReceiverThread receive = new TCPReceiverThread(clientSocket);
				new Thread(receive).start();
			}
			catch (IOException ioe)
			{
				System.out.println(ioe.getMessage());
			}
		}
	}
}
