package cs455.overlay.node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import cs455.overlay.dijkstra.RoutingCache;
import cs455.overlay.transport.TCPSender;
import cs455.overlay.wireformats.DeregistrationRequest;
import cs455.overlay.wireformats.DeregistrationResponse;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.LinkWeightsList;
import cs455.overlay.wireformats.MapServerToSocket;
import cs455.overlay.wireformats.Message;
import cs455.overlay.wireformats.MessagingNodesList;
import cs455.overlay.wireformats.RegistrationRequest;
import cs455.overlay.wireformats.RegistrationResponse;
import cs455.overlay.wireformats.TaskCompleteMessage;
import cs455.overlay.wireformats.TaskInitiate;
import cs455.overlay.wireformats.TrafficSummary;

public class MessagingNode extends Node{
	String registryHost;
	int registryPort;
	int sendMessageTracker;
	int receiveMessageTracker;
	int relayTracker;
	long sendSummation;
	long receiveSummation;
	AdjacencyMatrix grid;
	ArrayList<String> connectedList;
	RoutingCache paths;
	public static void main(String[] args)
	{
		Scanner scan = new Scanner(System.in);
		String strInput;
		try
		{
			MessagingNode m = new MessagingNode(0, args[0], Integer.parseInt(args[1]));
			m.register();
			while(true)
			{
				strInput = scan.nextLine();
				if(strInput.equals("print-shortest-path"))
				{
					m.printPaths();
				}
				else if(strInput.equals("exit-overlay"))
				{
					m.deregister();
					scan.close();
				}
			}
		}
		catch(IOException ioe)
		{
			System.out.println(ioe.getMessage());
		}
	}
	public MessagingNode(int portNumber, String registryHost, int registryPort) throws IOException
	{
		super(portNumber);
		this.registryHost = registryHost;
		this.registryPort = registryPort;
		sendMessageTracker = 0;
		receiveMessageTracker = 0;
		relayTracker = 0;
		connectedList = new ArrayList<String>();
	}
	synchronized public void onEvent(Event e, byte[] data)
	{
			int messageType = e.getType();
			//Receive registration response
			if(messageType == 2)
			{
				try
				{
					RegistrationResponse rr = new RegistrationResponse(0, (byte)0, "0");
					rr.readMessage(data);
					if(rr.getStatus() == (byte)0)
					{
						System.out.println("Registration Request unsuccessful");
					}
					else
					{
						System.out.println("Registration Request successful");
					}
				}
				catch(IOException ioe)
				{
					System.out.println(ioe.getMessage());
				}
			}
			//receive messaging nodes list
			else if(messageType == 3)
			{
				try
				{
					MessagingNodesList mnl = new MessagingNodesList(0, 0, connectedList);
					mnl.readMessage(data);
					connectedList = mnl.getList();
					//loop through list to parse host from port and use it to establish a TCPSender
					for(int i = 0; i < connectedList.size(); i++)
					{
						String IPandPort = connectedList.get(i);
						//splits the ipaddress:port at the colon and dumps it into an array
						String parse[] = IPandPort.split(":");
						String IPAddress = parse[0];
						String portNum = parse[1];
						int portNumber = Integer.parseInt(portNum);
						establishConnection(IPAddress, portNumber);
						sendServerToSocketMessage(IPAddress, portNumber);
					}
					System.out.println("All connections are established. Number of connections: " + connectedList.size());
				}
				catch(IOException ioe)
				{
					System.out.println(ioe.getMessage());
				}
			}
			else if(messageType == 4)
			{
				try
				{
					LinkWeightsList lwl = new LinkWeightsList(0, 0, linkList);
					lwl.readMessage(data);
					linkList = lwl.getList();
					grid = new AdjacencyMatrix(linkList);
					System.out.println("Link weights are received and processed. Ready to send messages.");
					paths = new RoutingCache(grid, IPAddress + ":" + portNumber);
				}
				catch(IOException ioe)
				{
					System.out.println(ioe.getMessage());
				}
			}
			else if(messageType == 5)
			{
				int rounds;
				try
				{
					TaskInitiate ti = new TaskInitiate(0, 0);
					ti.readMessage(data);
					rounds = ti.getRounds();
					sendTonsOfMessages(rounds);
				}
				catch(IOException ioe)
				{
					System.out.println(ioe.getMessage());
				}
			}
			else if(messageType == 6)
			{
				try
				{
					ArrayList<String> tempList = new ArrayList<String>();
					Message m = new Message(0, 0, 0, 0, tempList);
					m.readMessage(data);
					m.incrementIndexTracker();
					if(m.getNumber() <= m.getIndexTracker())
					{
						receiveSummation = receiveSummation + m.getPayload();
						receiveMessageTracker++;
					}
					else
					{
						relayMessage(m);
					}
				}
				catch(IOException ioe)
				{
					System.out.println(ioe.getMessage());
				}
			}
			//map server to socket
			else if(messageType == 7)
			{
				try
				{
					MapServerToSocket msts = new MapServerToSocket(7, "0", 0, "0", 0);
					msts.readMessage(data);
					String serverIP = msts.getServerIP();
					int serverPort  = msts.getServerPort();
					String socketIP = msts.getSocketIP();
					int socketPort = msts.getSocketPort();
					serverToSocket.put(serverIP + ":" + serverPort, socketIP + ":" + socketPort);
				}
				catch(IOException ioe)
				{
					System.out.println(ioe.getMessage());
				}
			}
			else if(messageType == 9)
			{
				TrafficSummary ts = new TrafficSummary(10, sendMessageTracker, sendSummation, receiveMessageTracker, receiveSummation, relayTracker, IPAddress, portNumber);
				//send traffic summary to registry
				sendEvent(ts, registryHost, registryPort);
				sendMessageTracker = 0;
				sendSummation = 0;
				receiveMessageTracker = 0;
				receiveSummation = 0;
				relayTracker = 0;
			}
			else if(messageType == 11)
			{
				try
				{
					DeregistrationResponse dr = new DeregistrationResponse(0, (byte)0, "0");
					dr.readMessage(data);
					if(dr.getStatus() == (byte)0)
					{
						System.out.println("Deregistration Request unsuccessful");
					}
					else
					{
						System.out.println("Deregistration Request successful");
						System.exit(0);
					}
				}
				catch(IOException ioe)
				{
					System.out.println(ioe.getMessage());
				}
			}
	}
	public void register()
	{
		try
		{
			establishConnection(registryHost, registryPort);
			TCPSender t = findSender(registryHost, registryPort);
			RegistrationRequest rr = new RegistrationRequest(0, IPAddress, portNumber, t.getLocalAddress(), t.getLocalPort());
			sendEvent(rr, registryHost, registryPort);
		}
		catch(IOException ioe)
		{
			System.out.println(ioe.getMessage());
		}
	}
	public void deregister()
	{
		try
		{
			DeregistrationRequest dd = new DeregistrationRequest(1, getIPAddress(), getPortNumber());
			sendEvent(dd, registryHost, registryPort);
		}
		catch(IOException ioe)
		{
			System.out.println(ioe.getMessage());
		}
	}
	public void sendServerToSocketMessage(String destIP, int destPort)
	{
		TCPSender t = findSender(destIP, destPort);
		int type = 7;
		String serverIP = IPAddress;
		int serverPort = portNumber;
		String socketIP = t.getLocalAddress();
		int socketPort = t.getLocalPort();
		MapServerToSocket msts = new MapServerToSocket(type, serverIP, serverPort, socketIP, socketPort);
		sendEvent(msts, destIP, destPort);
	}
	public void sendMessage(ArrayList<String> thisPath)
	{
		int type = 6;
		int payload = ThreadLocalRandom.current().nextInt(-2147483648, 2147483647);
		String nextStop = thisPath.get(1);
		String[] parse = nextStop.split(":");
		String nextIP = parse[0];
		int nextPort = Integer.parseInt(parse[1]);
		int indexTracker = 1;
		Message m = new Message(type, payload, thisPath.size(), indexTracker, thisPath);
		sendEvent(m, nextIP, nextPort);
		sendSummation = sendSummation + payload;
		sendMessageTracker++;
	}
	public void relayMessage(Message m)
	{
		String nextStop = m.getNextStop();
		String[] parse = nextStop.split(":");
		String nextIP = parse[0];
		int nextPort = Integer.parseInt(parse[1]);
		sendEvent(m, nextIP, nextPort);
		relayTracker++;
	}
	public void sendTaskComplete()
	{
		int type = 8;
		TaskCompleteMessage tcm = new TaskCompleteMessage(type, IPAddress, portNumber);
		sendEvent(tcm, registryHost, registryPort);
	}
	public void sendTonsOfMessages(int rounds)
	{
		for(int i = 0; i < rounds; i++)
		{
			ArrayList<String> thisPath = paths.getRandomPath();
			for(int j = 0; j < 5; j++)
			{
				sendMessage(thisPath);
			}
		}
		sendTaskComplete();
	}
	public void printPaths()
	{
		paths.print();
	}
}
