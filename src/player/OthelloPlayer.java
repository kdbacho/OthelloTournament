package player;

public class OthelloPlayer
{
	private String name;
	private int originalIndex;
	private int amtWins;
	
	public OthelloPlayer(String name, int originalIndex, int amtWins)
	{
		this.name = name;
		this.originalIndex = originalIndex;
		this.amtWins = amtWins;
	}
	
	public int returnOriginalIndex()
	{
		return this.originalIndex;
	}
	
	public void increaseWins()
	{
		this.amtWins++;
	}
	
	public String returnName()
	{
		return this.name;
	}
	
	public int returnAmountWins()
	{
		return this.amtWins;
	}
}
