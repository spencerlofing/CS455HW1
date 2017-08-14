package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MessagingNodesList extends Event{
	private int number;
	private ArrayList<String> list;
	public MessagingNodesList(int type, int number, ArrayList<String> list) throws IOException
	{
		super(type);
		this.number = number;
		this.list = list;
	}
	public void readMessage(byte[] marshalledBytes) throws IOException
	{
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();
		
		number = din.readInt();
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
		dout.writeInt(number);

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
	public int getType()
	{
		return type;
	}
	public int getNumber()
	{
		return number;
	}
	public ArrayList<String> getList()
	{
		return list;
	}
}

