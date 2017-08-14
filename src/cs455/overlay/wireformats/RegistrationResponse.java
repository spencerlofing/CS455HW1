package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegistrationResponse extends Event{

	private byte status;
	private String info;
	public RegistrationResponse(int type, byte status, String info) throws IOException
	{
		super(type);
		this.status = status;
		this.info = info;
	}
	public void readMessage(byte[] marshalledBytes) throws IOException
	{
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();
		status = din.readByte();
		
		int identifierLength = din.readInt();
		byte[] identifierBytes = new byte[identifierLength];
		din.readFully(identifierBytes);
		
		info = new String(identifierBytes);
		
		baInputStream.close();
		din.close();
	}
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeInt(type);
		
		dout.write(status);
		
		byte[] identifierBytes = info.getBytes();
		int elementLength = identifierBytes.length;
		dout.writeInt(elementLength);
		dout.write(identifierBytes);
		
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}
	
	public byte getStatus()
	{
		return status;
	}
	public String getInfo()
	{
		return info;
	}
}
