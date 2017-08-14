package cs455.overlay.dijkstra;

public class WeightSourcePair {
	private String weight;
	private String source;
	public WeightSourcePair(String weight, String source)
	{
		this.weight = weight;
		this.source = source;
	}
	public String getWeight()
	{
		return weight;
	}
	public String getSource()
	{
		return source;
	}
	public void setWeight(String w)
	{
		weight = w;
	}
	public void setSource(String s)
	{
		source = s;
	}
	public void print()
	{
		System.out.print("(" + weight + ", " + source + ")");
	}
}
