package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class DeregistrationRequest extends Event{
	//private int type;
	private String IPAddress;
	private int portNumber;
	public DeregistrationRequest(int type, String IPAddress, int portNumber) throws IOException
	{
		//this.type = type;
		super(type);
		this.IPAddress = IPAddress;
		this.portNumber = portNumber;
	}
	public void readMessage(byte[] marshalledBytes) throws IOException
	{
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();
		//timestamp = din.readLong();
		//IPAddress = din.readLine();
		
		int identifierLength = din.readInt();
		byte[] identifierBytes = new byte[identifierLength];
		din.readFully(identifierBytes);
		
		IPAddress = new String(identifierBytes);
		
		//tracker = din.readInt();
		portNumber = din.readInt();
		
		baInputStream.close();
		din.close();
	}
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeInt(type);
		//dout.writeLong(timestamp);
		
		byte[] identifierBytes = IPAddress.getBytes();
		int elementLength = identifierBytes.length;
		dout.writeInt(elementLength);
		dout.write(identifierBytes);
		
		//dout.writeInt(tracker);
		dout.writeInt(portNumber);
		
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}
	public int getType()
	{
		return type;
	}
	public String getIPAddress()
	{
		return IPAddress;
	}
	public int getPortNumber()
	{
		return portNumber;
	}
}

