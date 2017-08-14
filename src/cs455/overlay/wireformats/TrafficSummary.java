package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TrafficSummary extends Event{
	private int sent;
	private long sentSum;
	private int received;
	private long receivedSum;
	private int relayed;
	private String IPAddress;
	private int portNumber;
	public TrafficSummary(int type, int sent, long sentSum, int received, long receivedSum, int relayed, String IPAddress, int portNumber)
	{
		super(type);
		this.sent = sent;
		this.sentSum = sentSum;
		this.received = received;
		this.receivedSum = receivedSum;
		this.relayed = relayed;
		this.IPAddress = IPAddress;
		this.portNumber = portNumber;
	}
	public void readMessage(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();
		
		sent = din.readInt();
		
		sentSum = din.readLong();
		
		received = din.readInt();
		
		receivedSum = din.readLong();
		
		relayed = din.readInt();
		
		int IPAddressLength = din.readInt();
		byte[] IPAddressBytes = new byte[IPAddressLength];
		din.readFully(IPAddressBytes);
		IPAddress = new String(IPAddressBytes);
		
		portNumber = din.readInt();
		
		//System.out.println("Received traffic summary containing " + type + ", " + sent + ", " + sentSum + ", " + received + ", " + receivedSum + ", " + relayed + ", " + IPAddress + ", " + portNumber);
		
		baInputStream.close();
		din.close();
	}
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeInt(type);
		
		dout.writeInt(sent);
		
		dout.writeLong(sentSum);
		
		dout.writeInt(received);
		
		dout.writeLong(receivedSum);
		
		dout.writeInt(relayed);
			
		byte[] IPAddressBytes = IPAddress.getBytes(); 
		int IPAddressLength = IPAddress.length();
		dout.writeInt(IPAddressLength);
		dout.write(IPAddressBytes);
		
		dout.writeInt(portNumber);
		
		//System.out.println("Sent traffic summary containing " + type + ", " + sent + ", " + sentSum + ", " + received + ", " + receivedSum + ", " + relayed + ", " + IPAddress + ", " + portNumber);
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}
	
	public int getSentTotal()
	{
		return sent;
	}
	public long getSentSum()
	{
		return sentSum;
	}
	public int getReceivedTotal()
	{
		return received;
	}
	public long getReceivedSum()
	{
		return receivedSum;
	}
	public int getRelayedTotal()
	{
		return relayed;
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
