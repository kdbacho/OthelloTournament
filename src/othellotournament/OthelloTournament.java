package othellotournament;

import game.OthelloGame;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import player.OthelloPlayer;

public class OthelloTournament implements ActionListener
{
	public static String[] names;
	public static JFrame mainFrame;
	private ArrayList<OthelloPlayer[]> bracket;
	private int roundsCompleted;
	private int currentSpotAvailable;
	private int player;
	private String currentMatchup;
	private String player1;
	private String player2;
	private int player1Index, player2Index;
	private JButton playButton;
	private int[] displayX;
	private JLabel currentMatchupDisplay;
	private JPanel main, matchup;
	private JTextField textField;
	private OthelloPlayer[] players;
	private boolean winnerDeterminedYet, bracketRenderedYet;
	private BracketPanel panel;
	private int gridSize;
	public static int windowIndex = 0;
	public static boolean readyForNextRound;
	private JTabbedPane mainPanel = new JTabbedPane();


	public OthelloTournament(String[] players)
	{
		names = players;

		this.bracket = assembleBracket(names.length);
		this.mainFrame = new JFrame("Unreal Tournament: Othello");
		this.roundsCompleted = 0;

		bracketRenderedYet = false;
		this.initializeBracket();
		this.displayX = new int[players.length];

		mainPanel.addTab("Tournament", createMainPanel());
		this.mainFrame.setContentPane(mainPanel/* this.createMainPanel() */);
		this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.mainFrame.setSize(600, 600);
	}


	public JPanel createMainPanel()
	{
		this.main = new JPanel();
		this.main.setLayout(new GridLayout(2, 0));
		this.matchup = new JPanel();

		Border borderLine = BorderFactory.createLineBorder(Color.BLACK);

		this.matchup.setBorder(BorderFactory.createTitledBorder(borderLine,
				"Current Match"));

		this.playButton = new JButton("Play");
		this.playButton.setEnabled(false);
		this.playButton.addActionListener(this);
		this.matchup.add(this.playButton);

		this.currentMatchupDisplay = new JLabel(this.currentMatchup);
		this.matchup.add(this.currentMatchupDisplay);

		textField = new JTextField(10);
		textField.addActionListener(this);
		this.matchup.add(textField);

		panel = new BracketPanel(bracket, names, displayX);
		panel.setSize(names.length * 50, names.length * 10);
		panel.setBorder(BorderFactory.createTitledBorder(borderLine, "Bracket"));

		this.main.add(panel);
		this.main.add(this.matchup);

		this.setUpGame();

		return main;
	}


	public void initializeBracket()
	{
		for(int i = 0; i < names.length; i++)
			bracket.get(0)[i] = new OthelloPlayer(names[i], i, 0);
	}


	public void setUpGame()
	{

		if(player == bracket.get(roundsCompleted).length)
		{
			player = 0;
			currentSpotAvailable = 0;
			roundsCompleted++;
		}

		if(bracket.get(roundsCompleted).length != 1)
		{
			player1 = bracket.get(roundsCompleted)[player].returnName();
			player1Index = player;
			player2 = bracket.get(roundsCompleted)[player + 1].returnName();
			player2Index = player + 1;

			currentMatchup = player1 + " vs " + player2;
			this.currentMatchupDisplay.setText(currentMatchup);
			player += 2;
		}
	}


	public void processWinner(int winnerIndex)
	{
		bracket.get(roundsCompleted + 1)[currentSpotAvailable] = bracket
				.get(roundsCompleted)[winnerIndex];
		bracket.get(0)[bracket.get(roundsCompleted)[winnerIndex]
				.returnOriginalIndex()].increaseWins();

		currentSpotAvailable++;

		if(bracket.get(roundsCompleted + 1).length == 1)
		{
			this.currentMatchupDisplay.setText(bracket.get(roundsCompleted + 1)[0]
					.returnName() + " wins!");
			this.playButton.setText("Game Finished!");
			this.playButton.setEnabled(false);
		}
		panel.repaint();
	}


	public static ArrayList<OthelloPlayer[]> assembleBracket(int amtNames)
	{
		ArrayList<OthelloPlayer[]> finalBracket = new ArrayList<OthelloPlayer[]>();

		for(int roundSize = amtNames; roundSize >= 1; roundSize /= 2)
			finalBracket.add(new OthelloPlayer[roundSize]);

		return finalBracket;
	}


	public static String[] readData()
	{
		try
		{
			ArrayList<String> playersRaw = new ArrayList<String>();
			BufferedReader reader = new BufferedReader(new FileReader(
					"tournament.txt"));

			String temp;
			while((temp = reader.readLine()) != null)
				playersRaw.add(temp);

			String[] names = playersRaw.toArray(new String[playersRaw.size()]);

			reader.close();

			return names;
		}
		catch(IOException e)
		{
		}

		return null;
	}


	public void playGame()
	{
		this.mainFrame.setVisible(true);
	}


	public void updateBracketPanel()
	{
		for(int i = 0; i < names.length; i++)
		{
			displayX[i] = bracket.get(0)[i].returnAmountWins();
			System.out.println("i");
		}
	}


	public static void main(String[] arguments)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				OthelloTournament tournament = new OthelloTournament(
						readData());
				tournament.playGame();
			}
		});
	}


	public void reEnablePlayButton()
	{
		this.playButton.setText("Play");
		this.playButton.setEnabled(true);
		this.textField.setEnabled(true);
	}


	public void panelSwitch(JComponent component)
	{
		mainPanel.setSelectedComponent(component);
		mainFrame.pack();
	}


	public void isComplete()
	{
		boolean isCompleted = true;
		for(int i = 0; i < bracket.get(roundsCompleted + 1).length; i++)
		{
			System.out.println(i);
			if(bracket.get(roundsCompleted + 1)[i] == null)
			{
				isCompleted = false;
				break;
			}
		}

		if(isCompleted) setUpGame();
	}

	public void disableButton()
	{
		this.playButton.setText("Round Not Complete");
		this.playButton.setEnabled(false);
	}

	public void actionPerformed(ActionEvent e)
	{
		Object objectPressed = e.getSource();

		if(objectPressed == textField)
		{
			gridSize = Integer.parseInt(textField.getText());
			this.playButton.setEnabled(true);
			//this.textField.setEnabled(false);
		}
		else if(objectPressed == this.playButton)
		{
			Thread checker = new Thread(new Runnable()
			{
				public void run()
				{
					OthelloGame board = new OthelloGame(player1, player2, gridSize);
					JPanel panel = board.createMainPanel();
					mainPanel.addTab(currentMatchup, panel);
					int p1Index = player1Index;
					int p2Index = player2Index;
					panelSwitch(panel);
					Thread game = new Thread(board);
					game.start();

					if(player1Index != bracket.get(roundsCompleted).length - 2)
						setUpGame();
					else
						disableButton();
						

					try
					{
						while(!board.isWinner)
							Thread.sleep(200);
					}
					catch(InterruptedException e)
					{
						e.printStackTrace();
					}

					reEnablePlayButton();
					mainPanel.remove(panel);
					processWinner(board.player1Win ? p1Index : p2Index);
					isComplete();
				}
			});
			checker.start();
		}
	}
}

