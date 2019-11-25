package a7;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import a7.JSpotBoard.BoardStyle;

public class OthelloWidget extends JPanel implements ActionListener, SpotListener{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private enum Player {BLACK, WHITE};	// Enum to identify player
	
	private JSpotBoard _board;		//SpotBoard playing area
	private JLabel _message;		// Label for messages
	private boolean _gameWon;		// Game Won boolean
	private Player _nextToPlay;		// Turn of player 2
	private Spot _selection;		// Selected spot
	private boolean _validSpot;		// Boolean for validity of spot
	private List<Spot> _p1Spots;		// List of player 1 spots
	
	public OthelloWidget() {
		
		// Create SpotBoard and message label, initialize selection spot
		_board = new JSpotBoard(8,8, BoardStyle.CHECKERED);
		_message = new JLabel();
		_selection = null;
		_p1Spots = new ArrayList<>();
		
		// Set layout and place spotboard at center
		setLayout(new BorderLayout());
		add(_board, BorderLayout.CENTER);
		
		// Subpanel for message and reset button
		JPanel reset_message_panel = new JPanel();
		reset_message_panel.setLayout(new BorderLayout());
		
		// Reset button, adding this as action listener
		JButton reset_button = new JButton("Restart");
		reset_button.addActionListener(this);
		reset_message_panel.add(reset_button, BorderLayout.EAST);
		reset_message_panel.add(_message, BorderLayout.CENTER);
		
		// Add subpanel to bottom of widget
		add(reset_message_panel, BorderLayout.SOUTH);
		
		// Add this as spot listener for all spots
		_board.addSpotListener(this);
		
		// Reset game
		resetGame();
	}
	
	/* resetGame
	 * 
	 * Resets the game by clearing all spots on the board,
	 * resetting game status fields, adding 4 spots to middle,
	 * and displaying start message.
	 */
	private void resetGame() {
		// Clear spots from board
		for (Spot s : _board) {
			s.clearSpot();
			s.unhighlightSpot();
		}
		// Clear player spot lists
		_p1Spots.clear();
		
		int x[] = {3, 4, 4, 3};
		int y[] = {3, 3, 4, 4};
		Color[] c = {Color.WHITE, Color.BLACK};
		
		for (int i = 0; i < 4; i++) {
			Spot s = _board.getSpotAt(x[i], y[i]);
			s.setSpotColor(c[i%2]);
			s.toggleSpot();
		}
		
		// Reset game won and next to play
		_gameWon = false;
		_nextToPlay = Player.WHITE;
		
		// Start message
		_message.setText("Welcome to Othello. Black to play.");
		
	}
	
	// Implementation of SpotListener, logic to run connect four game
	
	public void spotClicked(Spot s) {
		// Checking game won
		if(_gameWon || !s.isEmpty()) {return;}
		
		// Setting global selection spot variable equal to s
		_selection = s;
		
		// Variables for player name, color, next player assuming start of game
		String player1 = "Black";
		String player2 = "White";
		Color playerColor = Color.BLACK;
		
		// Checking to see if players need to be changed
		if(_nextToPlay == Player.WHITE) {
			player1 = "White";
			player2 = "Black";
			playerColor = Color.WHITE;
			_nextToPlay = Player.BLACK;
		}
		else { _nextToPlay = Player.WHITE;}	
		
		// Clearing and Adding to list of player 1 spots
		_p1Spots.clear();
		for (Spot spot : _board) {
			if (!spot.isEmpty() && spot.getSpotColor().equals(playerColor)) {
				_p1Spots.add(spot);
			}
		}
		
		// Setting global selection spot variable equal to s
		_selection = s;
		
		checkValidSpots();
		
		// Change color of spot and highlights of column
		_selection.setSpotColor(playerColor);
		_selection.toggleSpot();
		spotExited(_selection);
		spotEntered(_selection);
		
		// Check to see if game is won
		checkWin();
		
		if (_gameWon) {
			// Setting won message
			_message.setText(player1 + " Wins!");
		}
		else {
			// Checking to see if draw game
			boolean drawGame = true;
			for (Spot spot : _board) {
				if (spot.isEmpty()) {drawGame = false; break;}
			}
			
			// Message for draw game or for next turn
			if (drawGame) { _message.setText("Draw Game."); _gameWon = true;}
			else {_message.setText(player2 + " to play."); }
		}
	}
	
	private void checkValidSpots() {
		List<Spot> row = new ArrayList<>();
		//List<Spot> col = new ArrayList<>();
		//List<Spot> dia = new ArrayList<>();
		
		for (Spot spot : _p1Spots) {
			if (_selection.getSpotX() == spot.getSpotX()) {
				//col.add(spot);
			}
			if (_selection.getSpotY() == spot.getSpotY()) {
				row.add(spot);
			}
			if (_selection.getSpotX() - _selection.getSpotY() ==
				spot.getSpotX() - spot.getSpotY()) {
				//dia.add(spot);
			}
		}
		row = getRow(row);
		//col = getListOfSpots(col);
		//dia = getListOfSpots(dia);
		
		_validSpot = false; return;
	}
	
	/* 
	 * Checks to see if list of spots passed to it match the selection color and have 4 in a 
	 * row. If there is a 4 in a row, highlights each spot and makes global win variable true,
	 * then returns
	 */
	
	private List<Spot> getRow(List<Spot> spots) {
		int var = -1;
		Spot start = null;
		Spot stop = null;
		List<Spot> row = new ArrayList<>();
		for (Spot s : spots) {
			int temp = s.getSpotX() - _selection.getSpotX();
			if (var == -1 || Math.abs(temp) < Math.abs(var)) {
				var = temp;
				if (var > 0) {
					start = _selection;
					stop = s;
				}
				else if (var == 0) {
					return row;
				}
				else {
					start = s;
					stop = _selection;
				}
			}
		}
		row.add(start);
		for (int x = start.getSpotX() + 1; x < stop.getSpotX(); x++) {
			Spot s = _board.getSpotAt(x, start.getSpotY());
			if (s.isEmpty()) {
				row.clear(); return row;
			}
			row.add(s);
		}
		row.add(stop);
		return row;
	}
	
	
	private void checkWin() {
		// Checking game won variable
		if (_gameWon) {return;}
		
		// Checking each row
		checkRows();
		if (_gameWon) {return;}
		
		// Checking each column
		checkCols();
		if (_gameWon) {return;}
		
		// Checking diagonals
		checkDiagonals();
		if (_gameWon) {return;}
		
	}
	
	/* checkRows iterates through each possible row in the board and passes
	 * them to the checkListOfSpots function. Returns if _gameWon is true
	 * or out of rows
	 */
	
	private void checkRows() {
		List<Spot> row = new ArrayList<>();
		for (Spot s : _board) {
			row.add(s);
			if (s.getSpotX() >= _board.getSpotWidth()-1) {
				//checkListOfSpots(row);
				if (_gameWon) {return;}
				row.clear();
			}
		}
	}
	
	/* checkCols iterates through each possible column in the board and passes
	 * them to the checkListOfSpots function. Returns if _gameWon is true
	 * or out of columns
	 */
	
	private void checkCols() {
		List<Spot> col = new ArrayList<>();
		for (int x = 0; x < _board.getSpotWidth(); x++) {
			for (int y = 0; y < _board.getSpotHeight(); y++) {
				col.add(_board.getSpotAt(x, y));
			}
			//checkListOfSpots(col);
			if (_gameWon) {return;}
			col.clear();
		}
	}
	
	/* checkDiagonals iterates through each possible diagonal in the board and passes
	 * them to the checkListOfSpots function. Returns if _gameWon is true
	 * or out of diagonals
	 */
	private void checkDiagonals() {
		boolean leftToRight = true;
		for (int i = 0; i < 6; i++) {
			List<Spot> diagonal = new ArrayList<>();
			int x; int y;
			if (leftToRight) {
				x = i < 3 ? 0 : i-2;
				y = i < 3 ? i+3 : 5;
			}
			else {
				x = i < 3 ? i+3 : 6;
				y = i < 3 ? 5 : 8-i;
			}
			while (true) {
				if (invalidXY(x,y)) {break;}
				diagonal.add(_board.getSpotAt(x, y));
				if (leftToRight) {x++;} else {x--;} y--;
			}
			//checkListOfSpots(diagonal);
			if (_gameWon) {return;}
			
			if (i == 5) {
				leftToRight = !leftToRight;
				if (leftToRight) {break;}
				i = -1;
			}
		}
	}
	
	/*
	 * Checks to see if the passed x and y values are out of bounds, used by checkDiagonals 
	 * function	
	 */
	
	private boolean invalidXY(int x, int y) {
		return (x < 0 || x >= _board.getSpotWidth() || y < 0 || y >= _board.getSpotHeight());
	}

	public void spotEntered(Spot s) {
		
		if (_gameWon) {return;}
		
		// Make global variable selection equal to s
		_selection = s;
		
		// Highlight valid spots where move can be made
		checkValidSpots();
		if (_validSpot) {
			s.highlightSpot();
		}
	}

	public void spotExited(Spot s) {
		if (_gameWon) {
			return;
		}
		for (int y = 0; y < _board.getSpotHeight(); y++) {
			_board.getSpotAt(s.getSpotX(), y).unhighlightSpot();
		}
	}

	public void actionPerformed(ActionEvent e) {
		// Reset game button is pressed
		resetGame();
	}

}
