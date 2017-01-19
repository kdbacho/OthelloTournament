package othellotournament;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

import player.OthelloPlayer;

@SuppressWarnings("serial")
public class BracketPanel extends JPanel
{
	final int FONT_SIZE = 14;
	private ArrayList<OthelloPlayer[]> bracket;
	private String[] names;
	private int[] coordinatesX;

	Font font = new Font("Serif", Font.PLAIN, FONT_SIZE);


	public BracketPanel(ArrayList<OthelloPlayer[]> bracket, String[] names,
			int[] coordinatesX)
	{
		this.bracket = bracket;
		this.names = names;
		this.coordinatesX = coordinatesX;
	}


	public void drawing()
	{
		repaint();
	}


	public void paintComponent(Graphics shape)
	{
		super.paintComponent(shape);

		for(int i = 0; i < names.length; i++)
		{
			coordinatesX[i] = bracket.get(0)[i].returnAmountWins() * 50 + 20;
		}

		for(int i = 0; i < names.length; i++)
		{
			shape.drawString(names[i], coordinatesX[i], (i + 1) * 20 + 10);
		}

	}

}
