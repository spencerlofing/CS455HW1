package cs455.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MapServerToSocket extends Event{

	private String serverIP;
	private int serverPort;
	private String socketIP;
	private int socketPort;
	public MapServerToSocket(int type, String serverIP, int serverPort, String socketIP, int socketPort)
	{
		super(type);
		this.serverIP = serverIP;
		this.serverPort = serverPort;
		this.socketIP = socketIP;
		this.socketPort = socketPort;
	}
	public void readMessage(byte[] marshalledBytes) throws IOException {
		ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
		DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
		
		type = din.readInt();
		
		int serverIPLength = din.readInt();
		byte[] serverIPBytes = new byte[serverIPLength];
		din.readFully(serverIPBytes);
		
		serverIP = new String(serverIPBytes);
		
		serverPort = din.readInt();
		
		int socketIPLength = din.readInt();
		byte[] socketIPBytes = new byte[socketIPLength];
		din.readFully(socketIPBytes);
		
		socketIP = new String(socketIPBytes);
		
		socketPort = din.readInt();
		
		baInputStream.close();
		din.close();
	}
	public byte[] getBytes() throws IOException {
		byte[] marshalledBytes = null;
		ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
		DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
		
		dout.writeInt(type);
		
		byte[] serverIPBytes = serverIP.getBytes();
		int serverIPLength = serverIP.length();
		dout.writeInt(serverIPLength);
		dout.write(serverIPBytes);
		
		dout.writeInt(serverPort);
		
		byte[] socketIPBytes = socketIP.getBytes();
		int socketIPLength = socketIP.length();
		dout.writeInt(socketIPLength);
		dout.write(socketIPBytes);
		
		dout.writeInt(socketPort);
			
		dout.flush();
		marshalledBytes = baOutputStream.toByteArray();
		
		baOutputStream.close();
		dout.close();
		return marshalledBytes;
	}
	public String getServerIP()
	{
		return serverIP;
	}
	public int getServerPort()
	{
		return serverPort;
	}
	public String getSocketIP()
	{
		return socketIP;
	}
	public int getSocketPort()
	{
		return socketPort;
	}
}
