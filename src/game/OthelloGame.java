package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;

import java.lang.NullPointerException;

import lib.OthelloPiece;
import lib.Resources;
import sound.Sound;
import othellotournament.OthelloTournament;

public class OthelloGame implements ActionListener, Runnable
{

	private JFrame frame;
	private/* static */JButton[][] grid;
	private OthelloPiece[][] pieces;
	public int gridSize;
	public boolean isPlayerOnesTurn, isWinner, player1Win;
	private JButton player1Forfeit, player2Forfeit, load;
	private JPanel mainPanel, gamePanel, controlPanel1, controlPanel2,
			masterPanel;
	private GridBagConstraints constraints = new GridBagConstraints();
	private JLabel player1Score, player2Score, blackIcon, whiteIcon;
	private String player1, player2;
	public static JLabel gameLogo;
	public int scorePlayer1, scorePlayer2;
	private boolean isPlayer1Forfeit, isPlayer2Forfeit;


	public OthelloGame(String player1, String player2, int size)
	{
		Sound.init();
		this.player1 = player1;
		this.player2 = player2;
		gridSize = size;
		grid = new JButton[size][size];
		pieces = new OthelloPiece[size][size];
	}


	public boolean frameClosed()
	{
		return mainPanel == null;
	}


	public void determineWinner()
	{
		if(isPlayer1Forfeit)
		{
			JOptionPane.showMessageDialog(null, this.player2 + " Wins by forfeit",
					"Result", JOptionPane.PLAIN_MESSAGE);
			player1Win = false;
		}

		if(isPlayer2Forfeit)
		{
			JOptionPane.showMessageDialog(null, this.player1 + " Wins by forfeit",
					"Result", JOptionPane.PLAIN_MESSAGE);
			player1Win = true;
		}

		if(scorePlayer1 + scorePlayer2 == gridSize * gridSize)
		{
			String result;
			if(scorePlayer1 > scorePlayer2)
			{
				result = this.player1 + " wins!";
				player1Win = true;
			}
			else if(scorePlayer2 > scorePlayer1)
			{
				result = this.player2 + " wins!";
				player1Win = false;
			}
			else
				result = "TIE!";

			JOptionPane.showMessageDialog(null, result, "Result",
					JOptionPane.PLAIN_MESSAGE);
		}

		isWinner = isPlayer1Forfeit || isPlayer2Forfeit
				|| scorePlayer1 + scorePlayer2 == gridSize * gridSize;

	}


	public boolean returnWinner()
	{
		return player1Win;
	}


	public JPanel createMainPanel()
	{
		mainPanel = new JPanel();
		gamePanel = new JPanel();
		controlPanel1 = new JPanel();
		controlPanel2 = new JPanel();
		masterPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());

		mainPanel.setBackground(new Color(255, 255, 255));

		Border borderLine = BorderFactory.createLineBorder(Color.BLACK);

		isPlayerOnesTurn = true;

		player1Forfeit = createButton(controlPanel1, "Forfeit", false, 0, 0);
		player1Score = createLabel(controlPanel1, "Score: 0");

		player2Forfeit = createButton(controlPanel2, "Forfeit", false, 0, 0);
		player2Score = createLabel(controlPanel2, "Score: 0");

		blackIcon = new JLabel(Resources.BLACK);
		whiteIcon = new JLabel(Resources.WHITE);

		controlPanel1.add(blackIcon);
		controlPanel2.add(whiteIcon);

		controlPanel1.setBorder(BorderFactory.createTitledBorder(borderLine,
				this.player1));
		controlPanel2.setBorder(BorderFactory.createTitledBorder(borderLine,
				this.player2));

		controlPanel1.setOpaque(false);
		controlPanel2.setOpaque(false);

		load = createButton(masterPanel, "Load", false, 0, 0);

		masterPanel.add(controlPanel1);
		masterPanel.add(controlPanel2);

		masterPanel.setOpaque(false);

		constraints.gridy = 3;
		constraints.anchor = GridBagConstraints.PAGE_END;

		mainPanel.add(masterPanel, constraints);

		initializeAndAddButtonsToPanel(false);
		reDraw();

		constraints.anchor = GridBagConstraints.PAGE_START;
		constraints.gridx = 0;
		constraints.gridy = 2;
		gamePanel.setLayout(new GridLayout(gridSize, gridSize));
		gamePanel.setOpaque(false);
		mainPanel.add(gamePanel, constraints);

		constraints.gridy = 0;
		gameLogo = new JLabel(Resources.LOGO);
		mainPanel.add(gameLogo, constraints);

		return mainPanel;
	}


	public void initializeAndAddButtonsToPanel(boolean fromAutoSave)
	{
		for(int i = 0; i < gridSize; i++)
		{
			for(int j = 0; j < gridSize; j++)
			{
				grid[i][j] = createButton(gamePanel, "", false, 0, 0);
				grid[i][j].setOpaque(false);
				grid[i][j].setContentAreaFilled(false);
			}
		}
		if(!fromAutoSave) initializeConfig();
		reDraw();
		disableIllegalMoves();
		checkAndUpdateScore();
	}


	public void checkAndUpdateScore()
	{
		scorePlayer1 = 0;
		scorePlayer2 = 0;
		for(OthelloPiece[] row : pieces)
			for(OthelloPiece individualPiece : row)
				if(individualPiece == OthelloPiece.BLACK)
					scorePlayer1++;
				else if(individualPiece == OthelloPiece.WHITE) scorePlayer2++;

		player1Score.setText("Score: " + Integer.toString(scorePlayer1));
		player2Score.setText("Score: " + Integer.toString(scorePlayer2));
	}


	public void reDraw()
	{
		for(int i = 0; i < gridSize; i++)
		{
			for(int j = 0; j < gridSize; j++)
			{
				if(pieces[i][j] != OthelloPiece.EMPTY)
				{
					if(gridSize < 10)
					{
						grid[i][j]
								.setIcon(pieces[i][j].equals(OthelloPiece.BLACK) ? Resources.BLACK
										: Resources.WHITE);
					}
					else
					{
						grid[i][j]
								.setIcon(pieces[i][j].equals(OthelloPiece.BLACK) ? Resources.BLACK_SMALL
										: Resources.WHITE_SMALL);
					}
				}
			}
		}
	}


	public JButton createButton(JPanel pane, String text, boolean isGrid,
			int preferedSizeX, int preferedSizeY)
	{
		JButton button = new JButton(text);
		button.addActionListener(this);

		if(preferedSizeX != 0)
			button.setPreferredSize(new Dimension(preferedSizeX, preferedSizeY));

		pane.add(button);

		return button;
	}


	public JLabel createLabel(JPanel pane, String text)
	{
		JLabel label = new JLabel();
		label.setText(text);

		pane.add(label);

		return label;
	}


	public void disableIllegalMoves()
	{
		for(int i = 0; i < gridSize; i++)
		{
			for(int j = 0; j < gridSize; j++)
			{
				if(!isPieceAdjacent(i, j))
					grid[i][j].setEnabled(false);
				else
					grid[i][j].setEnabled(true);
			}
		}
	}


	public void initializeConfig()
	{
		for(int i = 0; i < gridSize; i++)
		{
			for(int j = 0; j < gridSize; j++)
			{
				if((i == gridSize / 2 - 1 || i == gridSize / 2)
						&& (j == gridSize / 2 - 1 || j == gridSize / 2))
					pieces[i][j] = i == j ? OthelloPiece.WHITE : OthelloPiece.BLACK;
				else
					pieces[i][j] = OthelloPiece.EMPTY;
			}
		}
	}


	public void autoSave()
	{
		try
		{
			PrintWriter printer = new PrintWriter(new BufferedWriter(
					new FileWriter("autoSave.txt")));

			printer.println(gridSize);
			printer.println(isPlayerOnesTurn);

			for(int i = 0; i < gridSize; i++)
			{
				for(int j = 0; j < gridSize; j++)
				{
						printer.println(pieces[i][j].name());
				}
			}

			printer.close();
		}
		catch(IOException e)
		{

		}
	}


	public void removeButtons()
	{
		for(JButton[] row : grid)
			for(JButton button : row)
				gamePanel.remove(button);

	}


	public void loadSave()
	{
		int response = JOptionPane.showConfirmDialog(null,
				"Are you sure you would like to load the most recent save",
				"Load Save", JOptionPane.YES_NO_OPTION);

		if(response == JOptionPane.YES_OPTION)
		{
			try
			{
				BufferedReader reader = new BufferedReader(new FileReader(
						"autosave.txt"));

				gridSize = Integer.parseInt(reader.readLine());
				System.out.println();

				removeButtons();

				grid = new JButton[gridSize][gridSize];
				pieces = new OthelloPiece[gridSize][gridSize];

				isPlayerOnesTurn = Boolean.parseBoolean(reader.readLine());
				String temp;

				for(int i = 0; i < gridSize; i++)
				{
					for(int j = 0; j < gridSize; j++)
					{
						temp = reader.readLine();
						System.out.println(temp);
						if(temp.equals("BLACK") || temp.equals("WHITE")
								|| temp.equals("NONE"))
							pieces[i][j] = OthelloPiece.identify(temp);
					}
				}
				reader.close();
				gamePanel.setLayout(new GridLayout(gridSize, gridSize));
				initializeAndAddButtonsToPanel(true);

				gamePanel.revalidate();
				gamePanel.repaint();

				OthelloTournament.mainFrame.pack();

			}
			catch(IOException e)
			{
				JOptionPane.showMessageDialog(null, "No recent savegames exist!");
			}
			catch(NumberFormatException e)
			{
				JOptionPane.showMessageDialog(null, "This save game is corrupted1");
			}
		}
		else
		{
			JOptionPane.showMessageDialog(null, "GOODBYE");
		}
	}


	public void processButton(Object objectPressed)
	{
		for(int i = 0; i < gridSize; i++)
		{
			for(int j = 0; j < gridSize; j++)
			{
				if(grid[i][j] == objectPressed)
				{
					if(pieces[i][j] == OthelloPiece.EMPTY)
					{

						if(isPlayerOnesTurn)
							pieces[i][j] = OthelloPiece.BLACK;
						else
							pieces[i][j] = OthelloPiece.WHITE;

						Sound.handleCombos(horizVertDiscsOutFlanked(i, j, pieces[i][j])
								+ diagonalOutFlanked(i, j, pieces[i][j]));

						isPlayerOnesTurn = !isPlayerOnesTurn;
					}
				}
			}
		}
	}


	public void actionPerformed(ActionEvent event)
	{
		Object objectPressed = event.getSource();

		this.isPlayer1Forfeit = objectPressed == this.player1Forfeit;
		this.isPlayer2Forfeit = objectPressed == this.player2Forfeit;

		if(objectPressed == load)
			loadSave();
		else
			processButton(objectPressed);

		reDraw();
		disableIllegalMoves();
		checkAndUpdateScore();
		determineWinner();
		autoSave();
	}


	public void run()
	{
		try
		{
			isWinner = false;
			Thread.sleep(1000);
		}
		catch(InterruptedException e)
		{
		}
	}


	public int horizVertDiscsOutFlanked(int x, int y, OthelloPiece piece)
	{
		int pointerLeft, pointerRight;
		int piecesFlipped = 0;
		boolean emptyReachedLeft, emptyReachedRight, isLeftFinished, isRightFinished;
		int direction = 1;
		OthelloPiece leftTemp, rightTemp;
		while(direction <= 2)
		{
			pointerLeft = direction == 1 ? y : x;
			pointerRight = direction == 1 ? y : x;
			emptyReachedLeft = false;
			emptyReachedRight = false;

			while((pointerLeft > 0 || pointerRight < gridSize - 1))
			{
				isLeftFinished = !(pointerLeft > 0);
				if(!isLeftFinished) pointerLeft--;

				isRightFinished = !(pointerRight < gridSize - 1);
				if(!isRightFinished) pointerRight++;

				leftTemp = direction == 1 ? pieces[x][pointerLeft]
						: pieces[pointerLeft][y];
				rightTemp = direction == 1 ? pieces[x][pointerRight]
						: pieces[pointerRight][y];

				if(leftTemp != OthelloPiece.EMPTY)
				{
					if(piece == leftTemp && !isLeftFinished && !emptyReachedLeft)
					{
						piecesFlipped += direction == 1 ? switchPiecesHoriz(
								pointerLeft, true, x, y, piece) : switchPiecesVert(
								pointerLeft, true, x, y, piece);

						pointerLeft = 0;
					}

				}
				else
					emptyReachedLeft = true;

				if(rightTemp != OthelloPiece.EMPTY)
				{
					if(piece == rightTemp && !isRightFinished && !emptyReachedRight)
					{
						piecesFlipped += direction == 1 ? switchPiecesHoriz(
								pointerRight, false, x, y, piece) : switchPiecesVert(
								pointerRight, false, x, y, piece);

						pointerRight = gridSize - 1;
					}
				}
				else
					emptyReachedRight = true;
			}
			direction++;
		}
		return piecesFlipped;
	}


	public int diagonalOutFlanked(int x, int y, OthelloPiece piece)
	{
		return processSouth(true, x, y, piece) + processSouth(false, x, y, piece)
				+ processNorth(true, x, y, piece)
				+ processNorth(false, x, y, piece);
	}


	public int switchPiecesHoriz(int pointer, boolean isLeft, int x,
			int y, OthelloPiece piece)
	{
		int piecesFlipped = 0;
		int increment = isLeft ? 1 : -1;
		for(int i = pointer - (increment * -1); isLeft ? (i < y) : (i > y); i += increment)
		{
			if(pieces[x][i] != OthelloPiece.EMPTY)
			{
				pieces[x][i] = piece;
				piecesFlipped++;
			}
		}

		return piecesFlipped;
	}


	public int switchPiecesVert(int pointer, boolean isLeft, int x,
			int y, OthelloPiece piece)
	{
		int piecesFlipped = 0;
		int increment = isLeft ? 1 : -1;
		for(int i = pointer - (isLeft ? -1 : 1); isLeft ? (i < x) : (i > x); i += increment)
		{
			if(pieces[i][y] != OthelloPiece.EMPTY)
			{
				pieces[i][y] = piece;
				piecesFlipped++;
			}
		}

		return piecesFlipped;
	}


	public int processNorth(boolean isWest, int x, int y,
			OthelloPiece piece)
	{

		int incrementY = isWest ? -1 : 1;
		int pointerX = x;
		int pointerY = y;
		int i = 0;
		int j = 0;
		int piecesFlipped = 0;

		while(pointerX > 0)
		{
			pointerX--;
			pointerY += incrementY;

			if(pointerX >= 0 && isWest ? pointerY >= 0
					: pointerY < gridSize)
			{
				if(pieces[pointerX][pointerY] != OthelloPiece.EMPTY)
				{
					if(pieces[pointerX][pointerY] == piece
							&& (piece != OthelloPiece.EMPTY))
					{
						i = pointerX + 1;
						j = pointerY + (incrementY * -1);
						while(i < x && isWest ? j < y : j > y)
						{
							pieces[i][j] = piece;
							piecesFlipped++;
							i++;
							j += (incrementY * -1);
						}
						pointerX = 0;
					}
				}
				else
					pointerX = 0;
			}
		}

		return piecesFlipped;
	}


	public int processSouth(boolean isWest, int x, int y,
			OthelloPiece piece)
	{
		int incrementY = isWest ? -1 : 1;
		int pointerX = x;
		int pointerY = y;
		int i = 0;
		int j = 0;
		int piecesFlipped = 0;

		while(pointerX < 7)
		{
			pointerX++;
			pointerY += incrementY;

			if(pointerX < gridSize
					&& (isWest ? pointerY >= 0 : pointerY < gridSize))
			{
				if(pieces[pointerX][pointerY] != OthelloPiece.EMPTY)
				{
					if(pieces[pointerX][pointerY] == piece
							&& (piece != OthelloPiece.EMPTY))
					{
						i = pointerX - 1;
						j = pointerY + (incrementY * -1);
						while(i > x && isWest ? j < y : j > y)
						{
							pieces[i][j] = piece;
							piecesFlipped++;
							i--;
							j += (incrementY * -1);
						}
						pointerX = 7;
					}
				}
				else
					pointerX = 7;
			}
		}

		return piecesFlipped;

	}


	public boolean isPieceAdjacent(int x, int y)
	{
		int differenceX1 = x == gridSize - 1 ? 0 : 1;
		int differenceY1 = y == gridSize - 1 ? 0 : 1;
		int differenceX2 = x == 0 ? 0 : 1;
		int differenceY2 = y == 0 ? 0 : 1;

		if(pieces[x + differenceX1][y] != OthelloPiece.EMPTY) return true;
		if(pieces[x - differenceX2][y] != OthelloPiece.EMPTY) return true;
		if(pieces[x][y + differenceY1] != OthelloPiece.EMPTY) return true;
		if(pieces[x][y - differenceY2] != OthelloPiece.EMPTY) return true;

		// Diagonals
		// South East
		if(pieces[x + differenceX1][y + differenceY1] != OthelloPiece.EMPTY)
			return true;

		// North West
		if(pieces[x - differenceX2][y - differenceY2] != OthelloPiece.EMPTY)
			return true;

		// North East
		if(pieces[x - differenceX2][y + differenceY1] != OthelloPiece.EMPTY)
			return true;

		// South West
		if(pieces[x + differenceX1][y - differenceY2] != OthelloPiece.EMPTY)
			return true;

		return false;
	}
}

