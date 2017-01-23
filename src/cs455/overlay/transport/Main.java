package cs455.overlay.transport;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		System.out.println("Testing the server-client interaction");
		System.out.println("Input a port number for the interaction");
		Scanner scan = new Scanner(System.in);
		{
			int portNum = scan.nextInt();
			System.out.println(portNum);
			TCPServer server = new TCPServer(portNum);
		}
		Socket clientSocket = new Socket();
		try
		{
			TCPSender client = new TCPSender(clientSocket);
			client.sendData("Hello".getBytes());
		}
		catch(IOException ioe)
		{
			System.out.println(ioe.getMessage());
		}
	}

}
