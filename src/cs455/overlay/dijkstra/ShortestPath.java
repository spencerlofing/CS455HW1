package cs455.overlay.dijkstra;

import java.util.ArrayList;

import cs455.overlay.node.AdjacencyMatrix;

public class ShortestPath {
	private ArrayList<ArrayList<WeightSourcePair>> paths;
	private ArrayList<WeightSourcePair> absolutePath;
	private ArrayList<String> header;
	private String sourceNode;
	private AdjacencyMatrix am;
	private int sourceIndex;
	
	public ShortestPath(AdjacencyMatrix am, String source)
	{
		paths = new ArrayList<ArrayList<WeightSourcePair>>();
		header = new ArrayList<String>();
		sourceNode = source;
		this.am = am;
		
		//contains information about that particular row
		ArrayList<WeightSourcePair> rowHeader = new ArrayList<WeightSourcePair>();
		//adds a blank space at (0,0)
		rowHeader.add(new WeightSourcePair(" ", " "));
		header.add(" ");
		//loop through and add info to the rowHeader and also a column header
		for(int i = 1; i < am.size(); i++)
		{
			if(am.get(0).get(i).equals(source))
			{
				sourceIndex = i;
			}
			rowHeader.add(new WeightSourcePair(" ", am.get(0).get(i)));
			header.add(am.get(0).get(i));
			ArrayList<WeightSourcePair> newNode = new ArrayList<WeightSourcePair>();
			newNode.add(new WeightSourcePair(" ", am.get(0).get(i)));
			paths.add(newNode);
		}
		paths.add(0, rowHeader);
		calculateShortestPath();
	}
	private void calculateShortestPath()
	{
		ArrayList<Integer> visitedIndices = new ArrayList<Integer>();
		//start with the source node in the paths' matrix
		int nextIndex = sourceIndex;
		//until all indices have been visited
		while(visitedIndices.size() < paths.size() - 1)
		{
			//get arraylist of the current node
			ArrayList<WeightSourcePair> distances = paths.get(nextIndex);
			//loop through a particular node's list of weightsources
			for(int j = 1; j < paths.size(); j++)
			{
				//create a new entry
				WeightSourcePair newEntry;
				//if this iteration is equivalent to the source, a 
				if(j == sourceIndex)
				{
					newEntry = new WeightSourcePair("0", sourceNode);
				}
				//need another condition for this if to check if it is referenced by another node
				else if(am.get(nextIndex).get(j).equals(" - "))
				{
					//check if there is a previous entry for this distance, otherwise set it to 1000
					if(visitedIndices.size() > 0)
					{
						int lastVisited = visitedIndices.get(visitedIndices.size()-1);
						newEntry = paths.get(lastVisited).get(j);
					}
					else
					{
						newEntry = new WeightSourcePair("1000", am.get(0).get(nextIndex));
					}
				}
				//otherwise, if the entry in am is a number, add that number to the list
				else
				{
					//check if there is a previous entry that is larger
					//get the last index to be visited
					if(visitedIndices.size() > 0)
					{
						int lastVisited = visitedIndices.get(visitedIndices.size()-1);
						//this is the index of the weightsourcepair we are concerned with
						int comparisonLocation = j;
						//this is the weight at the index of the shortest currently held path to get here
						Integer comparisonWeight = Integer.parseInt(paths.get(lastVisited).get(comparisonLocation).getWeight());
						//this is the weight it takes our current node to get here
						int thisWeight = Integer.parseInt(am.get(j).get(nextIndex));
						//this is the weight it took to get to the current node
						int toGetHere = Integer.parseInt(paths.get(lastVisited).get(nextIndex).getWeight());
						//this is the total weight it would take to get to the node in question
						Integer thisTotalWeight = thisWeight + toGetHere;
						//check if the currently held shortest path is longer than the suggested one
						if(comparisonWeight > thisTotalWeight)
						{
							//update the entry to reflect a new shortest distance found
							newEntry = new WeightSourcePair(thisTotalWeight.toString(), paths.get(0).get(nextIndex).getSource());
						}
						//otherwise, maintain the shortest path
						else
						{
							newEntry = paths.get(lastVisited).get(j);
									//new WeightSourcePair(comparisonWeight.toString(), paths.get(0).get(lastVisited).getSource());
						}
					}
					else
					{
						newEntry = new WeightSourcePair(am.get(j).get(nextIndex), am.get(0).get(nextIndex));
					}
				}			
				distances.add(newEntry);
				//System.out.print(newEntry.getWeight() + ", ");
			}
			absolutePath = paths.get(nextIndex);
			//System.out.println("\n\n\n\n\n");
			visitedIndices.add(nextIndex);
			//System.out.println("Distances list: ")
			nextIndex = findNextIndex(distances, visitedIndices);
		}
	}
	private int findNextIndex(ArrayList<WeightSourcePair> distances, ArrayList<Integer> visitedIndices)
	{
		int minDist = 1000;
		int nextIndex = 0;
		for(int i = 1; i < distances.size(); i++)
		{
			//System.out.println("\n\n\n\n\n Problem here: " + distances.get(i).getWeight() + "\n\n\n\n");
			int alt = Integer.parseInt(distances.get(i).getWeight());
			if(alt < minDist && !(visitedIndices.contains(i)))
			{
				alt = minDist;
				nextIndex = i;
			}
		}
		return nextIndex;
	}
	public ArrayList<String> getPath(String destination)
	{
		ArrayList<String> shortestPath = new ArrayList<String>();
		//initialize arraylist so that destination exists as the end goal
		//that way when getNextStop is called, the arraylist will still contain something even if the source node
		//is directly connected to the destination
		shortestPath.add(0, destination);
		//System.out.println("Trying to find " + destination + " in the list: " + am.get(0));
		int destIndex = header.indexOf(destination);
		//this is the weightsourcepair from the last updated row in the matrix and the destination index
		WeightSourcePair ref = absolutePath.get(destIndex);
		while(ref.getSource() != sourceNode)
		{
			//add each source to the front of the path
			shortestPath.add(0, ref.getSource());
			destIndex = header.indexOf(ref.getSource());
			ref = absolutePath.get(destIndex);
		}
		//System.out.println(shortestPath);
		return shortestPath;
	}
	public void print()
	{
		for(int i = 0; i<paths.size(); i++)
		{
			for(int j = 0; j<paths.size(); j++)
			{
				paths.get(i).get(j).print();
				System.out.print(" ");
				if(i > 0 && j > 0)
				{
					System.out.print("   ");
				}
			}
			System.out.print("\n");
		}
	}
}
