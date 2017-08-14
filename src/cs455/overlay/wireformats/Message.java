package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Message extends Event{

	private int payload;
	private String destIP;
	private int destPort;
	int number;
	int indexTracker;
	ArrayList<String> list;
	public Message(int type, int payload, int number, int indexTracker, ArrayList<String> list)
	{
		super(type);
		this.payload = payload;
		this.number = number;
		this.indexTracker = indexTracker;
		this.list = list;
	}
	public void readMessage(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();
		payload = din.readInt();
		
		number = din.readInt();
		
		indexTracker = din.readInt();
		
		for(int i = 0; i < number; i++)
		{
			int identifierLength = din.readInt();
			byte[] identifierBytes = new byte[identifierLength];
			din.readFully(identifierBytes);
			list.add(new String(identifierBytes));
		}
		
		baInputStream.close();
		din.close();
	}
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeInt(type);
		dout.writeInt(payload);
		
		dout.writeInt(number);
		dout.writeInt(indexTracker);
		
		for(int i = 0; i < number; i++)
		{
			byte[] identifierBytes = (list.get(i)).getBytes();
			int elementLength = identifierBytes.length;
			dout.writeInt(elementLength);
			dout.write(identifierBytes);
		}
			
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}
	public int getPayload()
	{
		return payload;
	}
	public String getNextStop()
	{
		return list.get(indexTracker);
	}
	public int getNumber()
	{
		return number;
	}
	public int getIndexTracker()
	{
		return indexTracker;
	}
	public void incrementIndexTracker()
	{
		indexTracker++;
	}
}
