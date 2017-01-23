package cs455.overlay.transport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPSender {
	
	private Socket socket;
	private DataOutputStream dout;
	
	public TCPSender(Socket socket) throws IOException {
		//creates a reference to the inputted socket
		this.socket = socket;
		//creates a data output stream for data going through the socket
		dout = new DataOutputStream(socket.getOutputStream());
	}
	
	//takes in a byte array which will be sent over the socket
	public void sendData(byte[] dataToSend) throws IOException {
		//records the length of the byte array
		int dataLength = dataToSend.length;
		//writes an integer to the data output stream, addressing the length of the byte array
		dout.writeInt(dataLength);
		//writes the byte array to the data output stream
		dout.write(dataToSend, 0, dataLength);
		//cleans up the data output stream
		dout.flush();
	}

}
