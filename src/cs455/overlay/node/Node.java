package cs455.overlay.node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cs455.overlay.transport.TCPReceiverThread;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.transport.TCPServerThread;
import cs455.overlay.wireformats.Event;

public class Node {
	private TCPServerThread server;
	protected String IPAddress;
	protected int portNumber;
	protected Map<String, TCPSender> socketToSender;
	protected Map<String, String> serverToSocket;
	protected ArrayList<String> linkList;
	public Node(int portNum) throws IOException
	{
		InetAddress IP = InetAddress.getLocalHost();
		String thisHost = IP.getHostAddress();
		IPAddress = thisHost;
		socketToSender = new HashMap<String, TCPSender>();
		serverToSocket = new HashMap<String, String>();
		linkList = new ArrayList<String>();
		try
		{
			server = new TCPServerThread(portNum, this);
			portNumber = server.getPortNumber();
		}
		catch(IOException ioe)
		{
			System.out.println(ioe.getMessage());
		}
		initializeServer();
	}
	public TCPSender findSender(String IPAddress, int portNumber)
	{
		String socket = serverToSocket.get(IPAddress + ":" + portNumber);
		TCPSender t = socketToSender.get(socket);
		return t;
	}
	public boolean getConnection(String ipaddress, int portNumber)
	{
		TCPSender t = findSender(ipaddress, portNumber);
		if(t == null)
		{
			establishConnection(ipaddress, portNumber);
			return true;
		}
		else
		{
			return false;
		}
	}
	public void establishConnection(String ipaddress, int portNumber)
	{
		try
		{
			Socket socket = new Socket(ipaddress, portNumber);
			TCPSender sender = new TCPSender(socket);
			socketToSender.put(sender.getRemoteAddress() + ":" + sender.getRemotePort(), sender);
			serverToSocket.put(ipaddress + ":" + portNumber, sender.getRemoteAddress() + ":" + sender.getRemotePort());
			TCPReceiverThread receiver = new TCPReceiverThread(socket, this);
			new Thread(receiver).start();
		}
		catch(IOException ioe)
		{
			System.out.println(ioe.getMessage());
		}
	}
	public void sendEvent(Event e, String ipAddress, int portNumber)
	{
		TCPSender t = findSender(ipAddress, portNumber);
		try
		{
			byte[] data = e.getBytes();
			t.sendData(data);
		}
		catch(IOException ioe)
		{
			System.out.println(ioe.getMessage());
		}
	}
	public void addTosocketToSender(String IPandPort, TCPSender t)
	{
		socketToSender.put(IPandPort, t);
	}
	public void onEvent(Event e, byte[] data)
	{
	}
	public String getIPAddress()
	{
		return IPAddress;
	}
	public int getPortNumber()
	{
		return portNumber;
	}
	public void initializeServer()
	{
		new Thread(server).start();
	}
}
