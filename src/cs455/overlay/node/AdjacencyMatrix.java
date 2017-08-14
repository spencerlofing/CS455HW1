package cs455.overlay.node;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class AdjacencyMatrix {
	private ArrayList<ArrayList<String>> grid;
	ArrayList<String> rowHeader;
	private ArrayList<String> connectionsInfo;
	public AdjacencyMatrix(ArrayList<String> connectionsInfo)
	{
		grid = new ArrayList<ArrayList<String>>();
		this.connectionsInfo = connectionsInfo;
		buildRowHeader();
		buildColumnHeader();
		fillGrid();
	}
	private void buildRowHeader()
	{
		rowHeader = new ArrayList<String>();
		rowHeader.add("         ");
		for(int i = 0; i < connectionsInfo.size(); i++)
		{
			String[] parse = connectionsInfo.get(i).split(" ");
			String nodeNameOne = parse[0];
			String nodeNameTwo = parse[1];
			if(!(rowHeader.contains(nodeNameOne)))
			{
				rowHeader.add(nodeNameOne);
			}
			if(!(rowHeader.contains(nodeNameTwo)))
			{
				rowHeader.add(nodeNameTwo);
			}
		}
		grid.add(rowHeader);
	}
	private void buildColumnHeader()
	{
		for(int i = 1; i < rowHeader.size(); i++)
		{
			ArrayList<String> newNode = new ArrayList<String>();
			newNode.add(rowHeader.get(i));
			for(int j = 1; j < rowHeader.size(); j++)
			{
				newNode.add(" - ");
			}
			grid.add(newNode);
		}
	}
	private void fillGrid()
	{
		for(int i = 0; i < connectionsInfo.size(); i++)
		{
			String[] parse = connectionsInfo.get(i).split(" ");
			String ipOne = parse[0];
			String ipTwo = parse[1];
			String weight = parse[2];
			int indexOne = rowHeader.indexOf(ipOne);
			int indexTwo = rowHeader.indexOf(ipTwo);
			grid.get(indexOne).set(indexTwo, weight);
			grid.get(indexTwo).set(indexOne, weight);
		}
	}
	public ArrayList<String> getConnections(int nodeIndex)
	{
		ArrayList<String> returnList = new ArrayList<String>();
		ArrayList<String> connectionsList = grid.get(nodeIndex);
		for(int i = 1; i<connectionsList.size(); i++)
		{
			if(!(connectionsList.get(i).equals(" - ")))
			{
				returnList.add(grid.get(0).get(i));
			}
		}
		return returnList;
	}
	public int size()
	{
		return grid.size();
	}
	public ArrayList<String> get(int index)
	{
		return grid.get(index);
	}
}
