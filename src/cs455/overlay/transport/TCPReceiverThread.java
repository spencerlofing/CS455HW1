package cs455.overlay.transport;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class TCPReceiverThread implements Runnable {
	
	private Socket socket;
	private DataInputStream din;
	
	public TCPReceiverThread(Socket socket) throws IOException {
		//feed a socket into the constructor and then the thread references it
		this.socket = socket;
		//allows outside connections to write to a data stream
		din = new DataInputStream(socket.getInputStream());
	}
	
	public void run() {
		//length of the message
		int dataLength;
		//while the socket has an open connection:
		while(socket != null) {
			//try to read in an integer that specifies the number of incoming bytes
			try {
				dataLength = din.readInt();
				
				//create a byte array of the same length of the dataLength
				byte[] data = new byte[dataLength];
				//reads the input stream into the byte array
				din.readFully(data, 0, dataLength);
			}
			//throws an error indicating a problem with the socket connection
			catch (SocketException se) {
				System.out.println(se.getMessage());
				break;
			}
			//throws an error indicating a problem with the inputted message
			catch (IOException ioe) {
				System.out.println(ioe.getMessage());
				break;
			}
		}
	}
}
