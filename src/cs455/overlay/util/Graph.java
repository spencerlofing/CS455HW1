package cs455.overlay.util;

import java.util.ArrayList;
import java.util.Random;

public class Graph {
	private ArrayList<ArrayList<String>> graph;
	ArrayList<String> rowHeaders;
	Random numberGenerator = new Random();
	public Graph(ArrayList<String> nodesList)
	{
		graph = new ArrayList<ArrayList<String>>();
		rowHeaders = new ArrayList<String>();
		//add a blank in the top left corner to line up row headers and column headers
		rowHeaders.add(0, "      ");
		//initialize left side to the ips of pre-existing nodes
		rowHeaders.addAll(nodesList);
		//add the initial list to the far left column
		graph.add(rowHeaders);
		//cycle through the remaining columns to create a square graph
		//each column should start with an ip:port string then fill the rest of the spots with "-" until they can be replaced by weights
		for(int i = 1; i < rowHeaders.size(); i++)
		{
			ArrayList<String> newNode = new ArrayList<String>(rowHeaders.size());
			newNode.add(rowHeaders.get(i));
			//cycle through column and add "-" for link weight
			for(int j = 1; j < rowHeaders.size(); j++)
			{
				newNode.add(" - ");
			}
			graph.add(newNode);
		}
	}
	public void assignLinkWeight(int weight, int loc1, int loc2)
	{
		//convert link weight to a string
		String linkWeight = Integer.toString(weight);
		graph.get(loc1).set(loc2, linkWeight);
		graph.get(loc2).set(loc1, linkWeight);
		//System.out.println("Published connection between: " + graph.get(loc1).get(0) + " and " + graph.get(loc2).get(0));
	}
	public ArrayList<String> get(int index)
	{
		return graph.get(index);
	}
	public void printGrid()
	{
		for(int i = 0; i<rowHeaders.size(); i++)
		{
			for(int j = 0; j<graph.size(); j++)
			{
				System.out.print(graph.get(j).get(i));
				System.out.print(" ");
				if(i > 0 && j > 0)
				{
					System.out.print("              ");
				}
			}
			System.out.print("\n");
		}
	}
	public int size()
	{
		return graph.size();
	}
	public ArrayList<String> getConnections(int nodeIndex)
	{
		ArrayList<String> returnList = new ArrayList<String>();
		ArrayList<String> connectionsList = graph.get(nodeIndex);
		for(int i = nodeIndex; i<connectionsList.size(); i++)
		{
			if(!(connectionsList.get(i).equals(" - ")))
			{
				returnList.add(graph.get(0).get(i));
			}
		}
		return returnList;
	}
	public ArrayList<Integer> getLinkWeights(int nodeIndex)
	{
		ArrayList<Integer> returnList = new ArrayList<Integer>();
		ArrayList<String> linkWeightsList = graph.get(nodeIndex);
		for(int i = 1; i<linkWeightsList.size(); i++) 
		{
			if(!(linkWeightsList.get(i).equals(" - ")))
			{
				returnList.add(Integer.parseInt(graph.get(nodeIndex).get(i)));
			}
		}
		return returnList;
	}
	public ArrayList<String> getLinkWeights()
	{
		ArrayList<String> connectionsList = new ArrayList<String>();
		for(int i = 1; i < graph.size(); i++)
		{
			for(int j = i; j < graph.size(); j++)
			{
				if(!graph.get(i).get(j).equals(" - "))
				{
					connectionsList.add(graph.get(i).get(0) + " " + graph.get(j).get(0) + " " + graph.get(i).get(j));
					//System.out.println(graph.get(i).get(0) + " " + graph.get(j).get(0) + " " + graph.get(i).get(j));
				}
			}
		}
		return connectionsList;
	}
}
