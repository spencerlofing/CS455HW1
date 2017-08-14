package cs455.overlay.dijkstra;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import cs455.overlay.node.AdjacencyMatrix;

public class RoutingCache {
	private ArrayList<ArrayList<String>> cache;
	private ShortestPath sp;
	private String source;
	private AdjacencyMatrix am;
	public RoutingCache(AdjacencyMatrix am, String source)
	{
		this.am = am;
		this.source = source;
		sp = new ShortestPath(am, source);
		cache = new ArrayList<ArrayList<String>>();
		for(int i = 1; i < am.size(); i++)
		{
			String destination = am.get(0).get(i);
			cache.add(getPath(destination));
		}
	}
	public ArrayList<String> getRandomPath()
	{
		int randomIndex;
		ArrayList<String> randomPath;
		String destination;
		do
		{
			randomIndex = ThreadLocalRandom.current().nextInt(1, cache.size());
			randomPath = cache.get(randomIndex);
			destination = randomPath.get(randomPath.size() - 1);
		}
		while(destination.equals(source));
		return randomPath;
	}
	public ArrayList<String> getPath(String destination)
	{
		return sp.getPath(destination);
	}
	public void print()
	{
		System.out.println("Source equals " + source);
		for(int i = 0; i < cache.size(); i++)
		{
			ArrayList<String> thisRow = cache.get(i);
			if(thisRow.size() != 1)
			{
				for(int j = 0; j < thisRow.size(); j++)
				{
					if(j != thisRow.size() - 1)
					{
						int coordinateOne = am.get(0).indexOf(thisRow.get(j));
						int coordinateTwo = am.get(0).indexOf(thisRow.get(j+1));
						String weight = am.get(coordinateOne).get(coordinateTwo);
						System.out.print(thisRow.get(j) + "--" + weight + "--");
					}
					else
					{
						System.out.print(thisRow.get(j));
					}
				}
				System.out.println("\n");
			}
		}
	}
}
