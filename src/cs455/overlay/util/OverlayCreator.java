package cs455.overlay.util;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class OverlayCreator {
	//ArrayList<String> nodeList;
	int totalConnections;
	Graph graph;
	public OverlayCreator(ArrayList<String> nodeList, int totalConnections)
	{
		//this.nodeList = nodeList;
		this.totalConnections = totalConnections;
		boolean success = false;
		while(success == false)
		{
			graph = new Graph(nodeList);
			success = setupOverlay();
		}
	}
	public boolean setupOverlay()
	{
		//construct a linear topology then connect randomly
		for(int i = 1; i<graph.size()-1; i++)
		{
			assignLinkWeights(i, i+1);
		}
		boolean success = assignAllWeights();
		return success;
	}
	public void assignLinkWeights(int loc1, int loc2)
	{
		//generates a random link weight between 1 and 10
		int linkWeight = ThreadLocalRandom.current().nextInt(1, 11);
		graph.assignLinkWeight(linkWeight, loc1, loc2);
	}
	public int getNumberOfConnections(int index)
	{
		//find the specific node in the graph and get a reference to it
		ArrayList<String> connectionsList = graph.get(index);
		//start a counter for how many times the node is connected
		int numberOfConnections = 0;
		//loop through the list of weights
		for(int i = 1; i<connectionsList.size(); i++)
		{
			//increment the counter every time a connection is found
			if(!(connectionsList.get(i).equals(" - ")))
			{
				numberOfConnections++;
			}
		}
		return numberOfConnections;
	}
	public boolean assignAllWeights()
	{
		//number of connections in existence for this node
		int numberOfConnections;
		//number of connections still needed
		int remainingConnections;
		//random index
		int index;
		//keeps track of connections count
		int currentConnectionsCount;
		//keeps track of the random indices that have been checked for a connection
		ArrayList<Integer> triedNumbers;
		//loop through the graph
		for(int i = 1; i < graph.size(); i++)
		{
			triedNumbers = new ArrayList<Integer>();
			//get the number of connections still needed for this node
			numberOfConnections = getNumberOfConnections(i);
			remainingConnections = totalConnections - numberOfConnections;
			currentConnectionsCount = 0;
			while(currentConnectionsCount < remainingConnections)
			{
				//if the bounds are equal ThreadLocalRandom throws an exception, so this handles it
				if(i + 1 == graph.size())
				{
					index = graph.size() - 1;
				}
				else
				{
					//generates a random number between i and the graph size
					//this is so that an index is picked that is larger than any of the indices that have already been taken care of, including the current one
					index = ThreadLocalRandom.current().nextInt(i+1, graph.size());
				}
				//ensure a connection does not already exist between the two nodes and that the proposed node isn't already full with connections
				if(graph.get(i).get(index).equals(" - ") && getNumberOfConnections(index) < totalConnections)
				{
					assignLinkWeights(i, index);
					currentConnectionsCount++;
				}
				//if we don't add the node check if this possible connection has been tried before/add it to the list of tried connections
				else
				{
					if(triedNumbers.contains(index))
					{
						if(triedNumbers.size() >= graph.size() - (i+1))
						{
							return false;
						}
					}
					else
					{
						triedNumbers.add(index);
					}
				}
			}
		}
		return true;
	}
	public Graph getGraph()
	{
		return graph;
	}
	public void printGraph()
	{
		graph.printGrid();
	}
}
