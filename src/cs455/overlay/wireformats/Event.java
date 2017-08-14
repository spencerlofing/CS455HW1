package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Event{

	protected int type;
	public Event(int type)
	{
		this.type = type;
	}
	public void readMessage(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();
		
		baInputStream.close();
		din.close();
	}
	public byte[] getBytes() throws IOException
	{
		byte[] data = {6, 7, 8};
		return data;
	}
	public int getType()
	{
		return type;
	}
}
