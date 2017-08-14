package cs455.overlay.node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import cs455.overlay.util.Graph;
import cs455.overlay.util.OverlayCreator;
import cs455.overlay.wireformats.DeregistrationRequest;
import cs455.overlay.wireformats.DeregistrationResponse;
import cs455.overlay.wireformats.Event;
import cs455.overlay.wireformats.LinkWeightsList;
import cs455.overlay.wireformats.MessagingNodesList;
import cs455.overlay.wireformats.PullTrafficSummary;
import cs455.overlay.wireformats.RegistrationRequest;
import cs455.overlay.wireformats.RegistrationResponse;
import cs455.overlay.wireformats.TaskCompleteMessage;
import cs455.overlay.wireformats.TaskInitiate;
import cs455.overlay.wireformats.TrafficSummary;

public class Registry extends Node{
	public static void main(String[] args)
	{		
		Registry r = null;
		Scanner scan = new Scanner(System.in);
		int numConnections = 0;
		int numRounds = 0;
		boolean overlaySet = false;
		boolean linksSent = false;
		try
		{
			r = new Registry(Integer.parseInt(args[0]));
			System.out.println("Registry created on:" + r.getIPAddress() + ":" + r.getPortNumber());
			while(true)
			{
				//if there is a space in the input line
				String[] arguments = scan.nextLine().split(" ");
				if(arguments.length == 2)
				{
					if(arguments[0].equals("setup-overlay"))
					{
						int number = Integer.parseInt(arguments[1]);
						if(number < r.listSize())
						{
							numConnections = number;
							r.setupOverlay(numConnections);
							overlaySet = true;
							r.sendMessagingNodesLists(numConnections);
						}
						else if(overlaySet == true)
						{
							System.out.println("Overlay has already been setup");
						}
						else
						{
							System.out.println("Number of connections cannot be larger than or equal to the number of nodes in the overlay");
						}
					}
					else if(arguments[0].equals("start"))
					{
						if(linksSent == true)
						{
							numRounds = Integer.parseInt(arguments[1]);
							r.sendTaskInitiate(numRounds);
						}
						else
						{
							System.out.println("Issue command: 'send-overlay-link-weights' before initiating message passing");
						}
						//need messaging nodes to respond when they've finished their tasks
					}
					else if(arguments[0].equals("list-messaging") && arguments[1].equals("nodes"))
					{
						r.listMessagingNodes();
					}
				}
				//if there is no space in input line
				else
				{
					if(arguments[0].equals("list-weights"))
					{
						if(overlaySet == false)
						{
							System.out.println("Overlay has not been set up yet with corresponding link weights. Issue command 'setup-overlay' to do so");
						}
						else
						{
							r.listWeights();
						}
					}
					else if(arguments[0].equals("send-overlay-link-weights"))
					{
						if(overlaySet == false)
						{
							System.out.println("Overlay has not been set up yet with corresponding link weights. Issue command 'setup-overlay' to do so");
						}
						else if(linksSent == true)
						{
							System.out.println("Overlay link weights have already been sent");
						}
						else
						{
							r.sendLinkWeights();
							linksSent = true;
						}
					}
					else if(arguments[0].equals("setup-overlay"))
					{
						r.setupOverlay(4);
						overlaySet = true;
						r.sendMessagingNodesLists(4);
					}
					else
					{
						System.out.println("Invalid option");
					}
				}
			}
		}
		catch(IOException ioe)
		{
			System.out.println(ioe.getMessage());
		}
	}
	private ArrayList<String> nodesList;
	private ArrayList<String> completeList;
	private ArrayList<TrafficSummary> trafficSumList;
	private Graph graph;
	private OverlayCreator oc;
	public Registry(int portNumber) throws IOException
	{
		super(portNumber);
		nodesList = new ArrayList<String>();
		completeList = new ArrayList<String>();
		trafficSumList = new ArrayList<TrafficSummary>();
	}
	synchronized public void onEvent(Event e, byte[] data)
	{
			int messageType = e.getType();
			//zero is a registration request
			if(messageType == 0)
			{
				try
				{
					RegistrationRequest request = new RegistrationRequest(0, "0", 0, "0", 0);
					request.readMessage(data);
					Register(request.getServerIPAddress(), request.getServerPortNumber(), request.getSocketIPAddress(), request.getSocketPortNumber());
				}
				catch(IOException ioe)
				{
					System.out.println(ioe.getMessage());
				}
			}
			//one is a deregistration request
			else if(messageType == 1)
			{
				try
				{
					DeregistrationRequest dr = new DeregistrationRequest(0, "0", 0);
					dr.readMessage(data);
					Deregister(dr.getIPAddress(), dr.getPortNumber());
				}
				catch(IOException ioe)
				{
					System.out.println(ioe.getMessage());
				}
			}
			else if(messageType == 8)
			{
				System.out.print("Received task complete from ");
				try
				{
					TaskCompleteMessage tcm = new TaskCompleteMessage(0, "0", 0);
					tcm.readMessage(data);
					System.out.println(tcm.getIPAddress() + ":" + tcm.getPortNumber());
					if(nodesList.contains(tcm.getIPAddress() + ":" + tcm.getPortNumber()))
					{
						System.out.println("Received task complete from " + tcm.getIPAddress() + tcm.getPortNumber());
						completeList.add(tcm.getIPAddress() + ":" + tcm.getPortNumber());
						if(completeList.size() == nodesList.size())
						{
							System.out.println("Received all task complete messages, sleeping for twenty seconds now");
							try
							{
								Thread.sleep(20 * 1000);
							}
							catch(InterruptedException ie)
							{
								System.out.println(ie.getMessage());
							}
							System.out.println("Sleeping complete, sending pull traffic summary requests");
							for(int i = 0; i < completeList.size(); i++)
							{
								int type = 9;
								PullTrafficSummary pts = new PullTrafficSummary(type);
								String IPandPort = completeList.get(i);
								String parse[] = IPandPort.split(":");
								String destIP = parse[0];
								int destPort = Integer.parseInt(parse[1]);
								sendEvent(pts, destIP, destPort);
							}
						}
					}
					else
					{
						System.out.println("Received a traffic completion corresponding to a node outside of the overlay");
					}
					//issue pull traffic summary
				}
				catch(IOException ioe)
				{
					System.out.println(ioe.getMessage());
				}
			}
			else if(messageType == 10)
			{
				try
				{
					TrafficSummary ts = new TrafficSummary(0, 0, 0, 0, 0, 0, "0", 0);
					ts.readMessage(data);
					if(completeList.contains(ts.getIPAddress() + ":" + ts.getPortNumber()))
					{
						System.out.println("Received traffic summary from " + ts.getIPAddress() + ":" + ts.getPortNumber());
						trafficSumList.add(ts);
						if(trafficSumList.size() == completeList.size())
						{
							printTrafficSummaries();
							completeList.clear();
							trafficSumList.clear();
						}
					}
				}
				catch(IOException ioe)
				{
					System.out.println(ioe.getMessage());
				}
			}
	}
	public void Register(String serverIPAddress, int serverPortNumber, String socketIPAddress, int socketPortNumber)
	{
		int type = 2;
		byte response;
		String details;
		if(nodeExists(serverIPAddress, serverPortNumber))
		{
			response = (byte)0;
			details = "failed to register";
		}
		else
		{
			response = (byte)1;
			nodesList.add(serverIPAddress + ":" + serverPortNumber);
			serverToSocket.put(serverIPAddress + ":" + serverPortNumber, socketIPAddress + ":" + socketPortNumber);
			details = "Registration request successful. The number of messaging nodes currently constituting the overlay is (" + nodesList.size() + ")";
		}
		System.out.println(details);
		try
		{
			RegistrationResponse rr = new RegistrationResponse(type, response, details);
			sendEvent(rr, serverIPAddress, serverPortNumber);
		}
		catch(IOException ioe)
		{
			System.out.println(ioe.getMessage());
		}
	}
	
	public void Deregister(String IPAddress, int portNumber)
	{
		int type = 11;
		byte response;
		String details;
		if(nodeExists(IPAddress, portNumber))
		{
			response = (byte)1;
			String IPandPort = IPAddress + ":" + portNumber;
			nodesList.remove(IPandPort);
			details = "Deregistration request successful. The number of messaging nodes currently constituting the overlay is (" + nodesList.size() + ")";
		}
		else
		{
			response = (byte)0;
			details = "Failed to deregister";
		}
		System.out.println(details);
		try
		{
			DeregistrationResponse dr = new DeregistrationResponse(type, response, details);
			sendEvent(dr, IPAddress, portNumber);
		}
		catch(IOException ioe)
		{
			System.out.println(ioe.getMessage());
		}
	}
	public boolean nodeExists(String IPAddress, int portNumber)
	{
		if(nodesList.contains(IPAddress + ":" + portNumber))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public void sendMessagingNodesLists(int connectionsNumber)
	{
		int type = 3;
		//create a list of nodes to send for connections
		String destIPAddress;
		int destPortNumber;
		for(int i = 0; i<nodesList.size(); i++)
		{
			String IPandPort = nodesList.get(i);
			String parse[] = IPandPort.split(":");
			destIPAddress = parse[0];
			destPortNumber = Integer.parseInt(parse[1]);
			//this gets connections from i+1 because there the 0th row/column in the graph is a label
			ArrayList<String> listToSend = graph.getConnections(i + 1);
			//add destination nodes for the number of connections specified
			try
			{
				MessagingNodesList mnl = new MessagingNodesList(type, listToSend.size(), listToSend);
				sendEvent(mnl, destIPAddress, destPortNumber);
			}
			catch(IOException ioe)
			{
				System.out.println(ioe.getMessage());
			}
		}
	}
	public void sendLinkWeights()
	{
		int type = 4;
		//create a list of nodes to send for connections
		String destIPAddress;
		int destPortNumber;
		for(int i = 0; i<nodesList.size(); i++)
		{
			String IPandPort = nodesList.get(i);
			String parse[] = IPandPort.split(":");
			destIPAddress = parse[0];
			destPortNumber = Integer.parseInt(parse[1]);
			//this gets connections from i+1 because there the 0th row/column in the graph is a label
			ArrayList<String> listToSend = graph.getLinkWeights();
			//add destination nodes for the number of connections specified
			try
			{
				LinkWeightsList lwl = new LinkWeightsList(type, listToSend.size(), listToSend);
				sendEvent(lwl, destIPAddress, destPortNumber);
			}
			catch(IOException ioe)
			{
				System.out.println(ioe.getMessage());
			}
		}
	}
	public void sendTaskInitiate(int rounds)
	{
		int type = 5;
		for(int i = 0; i<nodesList.size(); i++)
		{
			String IPandPort = nodesList.get(i);
			String parse[] = IPandPort.split(":");
			String destIPAddress = parse[0];
			int destPortNumber = Integer.parseInt(parse[1]);
			//add destination nodes for the number of connections specified
			try
			{
				TaskInitiate ti = new TaskInitiate(type, rounds);
				sendEvent(ti, destIPAddress, destPortNumber);
			}
			catch(IOException ioe)
			{
				System.out.println(ioe.getMessage());
			}
		}
	}
	public void listMessagingNodes()
	{
		for(int i = 0; i < nodesList.size(); i++)
		{
			System.out.println(nodesList.get(i));
		}
	}
	public void listWeights()
	{
		ArrayList<String> connectionsList = graph.getLinkWeights();
		for(int i = 0; i < connectionsList.size(); i++)
		{
			System.out.println(connectionsList.get(i));
		}
	}
	public void setupOverlay(int numberOfConnections)
	{
		oc = new OverlayCreator(nodesList, numberOfConnections);
		graph = oc.getGraph();
	}
	public int listSize()
	{
		return nodesList.size();
	}
	public void printTrafficSummaries()
	{
		int totalSent = 0;
		int totalReceived = 0;
		long totalSentSum = 0;
		long totalReceivedSum = 0;
		System.out.println("               Number of messages sent:    Number of messages received:    Summation of sent messages:    Summation of received messages:    Number of relayed messages:");
		for(int i = 0; i < trafficSumList.size(); i++)
		{
			TrafficSummary ts = trafficSumList.get(i);
			String IPandPort = ts.getIPAddress() + ":" + ts.getPortNumber();
			int sent = ts.getSentTotal();
			totalSent+=sent;
			int received = ts.getReceivedTotal();
			totalReceived+=received;
			long sentSum = ts.getSentSum();
			totalSentSum+=sentSum;
			long receivedSum = ts.getReceivedSum();
			totalReceivedSum+=receivedSum;
			int relayed = ts.getRelayedTotal();
			System.out.println(IPandPort + "            " + sent + "                 " + received + "                   " + sentSum + "                    " + receivedSum + "                   " + relayed);
		}
		System.out.println("            Sum              " + totalSent + "                " + totalReceived + "                " + totalSentSum + "                " + totalReceivedSum);
	}
}
