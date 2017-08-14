package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class RegistrationRequest extends Event{
	private String serverIPAddress;
	private int serverPortNumber;
	private String socketIPAddress;
	private int socketPortNumber;
	public RegistrationRequest(int type, String serverIPAddress, int serverPortNumber, String socketIPAddress, int socketPortNumber) throws IOException
	{
		super(type);
		this.serverIPAddress = serverIPAddress;
		this.serverPortNumber = serverPortNumber;
		this.socketIPAddress = socketIPAddress;
		this.socketPortNumber = socketPortNumber;
	}
	public void readMessage(byte[] marshalledBytes) throws IOException
	{
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();
		
		int identifierLength = din.readInt();
		byte[] identifierBytes = new byte[identifierLength];
		din.readFully(identifierBytes);
		
		serverIPAddress = new String(identifierBytes);
		
		serverPortNumber = din.readInt();
		
		int identifier2Length = din.readInt();
		byte[] identifier2Bytes = new byte[identifier2Length];
		din.readFully(identifier2Bytes);
		
		socketIPAddress = new String(identifier2Bytes);
		
		socketPortNumber = din.readInt();
		baInputStream.close();
		din.close();
	}
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeInt(type);
		
		byte[] identifierBytes = serverIPAddress.getBytes();
		int elementLength = identifierBytes.length;
		dout.writeInt(elementLength);
		dout.write(identifierBytes);
		
		dout.writeInt(serverPortNumber);
		
		byte[] identifier2Bytes = socketIPAddress.getBytes();
		int element2Length = identifier2Bytes.length;
		dout.writeInt(element2Length);
		dout.write(identifier2Bytes);
		
		dout.writeInt(socketPortNumber);
		
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}
	public String getServerIPAddress()
	{
		return serverIPAddress;
	}
	public int getServerPortNumber()
	{
		return serverPortNumber;
	}
	public String getSocketIPAddress()
	{
		return socketIPAddress;
	}
	public int getSocketPortNumber()
	{
		return socketPortNumber;
	}
}
