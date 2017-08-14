package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TaskCompleteMessage extends Event{
	private String IPAddress;
	private int portNumber;
	public TaskCompleteMessage(int type, String IPAddress, int portNumber)
	{
		super(type);
		this.IPAddress = IPAddress;
		this.portNumber = portNumber;
	}
	public void readMessage(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();
		
		int IPAddressLength = din.readInt();
		byte[] IPAddressBytes = new byte[IPAddressLength];
		din.readFully(IPAddressBytes);

		IPAddress = new String(IPAddressBytes);
	
		portNumber = din.readInt();
		baInputStream.close();
		din.close();
	}
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeInt(type);
		
		byte[] IPAddressBytes = IPAddress.getBytes(); 
		int IPAddressLength = IPAddress.length();
		dout.writeInt(IPAddressLength);
		dout.write(IPAddressBytes);
		
		dout.writeInt(portNumber);
			
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
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
