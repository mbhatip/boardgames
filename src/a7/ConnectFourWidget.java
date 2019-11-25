package a7;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import a7.JSpotBoard.BoardStyle;

public class ConnectFourWidget extends JPanel implements ActionListener, SpotListener{

	// Enum to identify player
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private enum Player {RED, BLACK};
	
	private JSpotBoard _board;		//SpotBoard playing area
	private JLabel _message;		// Label for messages
	private boolean _gameWon;		// Game Won boolean
	private Player _nextToPlay;		// Turn of player 2
	
	public ConnectFourWidget() {
		
		// Create SpotBoard and message label
		_board = new JSpotBoard(7,6, BoardStyle.COLUMNS);
		_message = new JLabel();
		
		// Create player boards
		
		
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
	 * resetting game status fields, 
	 * and displaying start message.
	 */
	private void resetGame() {
		// Clear spots from board
		for (Spot s : _board) {
			s.clearSpot();
			s.unhighlightSpot();
		}
		
		// Reset game won and next to play
		_gameWon = false;
		_nextToPlay = Player.RED;
		
		// Start message
		_message.setText("Welcome to Connect Four. Red to play.");
		
	}
	
	// Implementation of SpotListener, logic to run tic tac toe game
	
	public void spotClicked(Spot s) {
		// Checking game won
		if(_gameWon) {return;}
		
		// Variables for player name, color, and next player, assuming start of game
		String player1 = "Red";
		String player2 = "Black";
		Color playerColor = Color.RED;
		
		if(_nextToPlay == Player.BLACK) {
			player1 = "Black";
			player2 = "Red";
			playerColor = Color.BLACK;
			_nextToPlay = Player.RED;
		}
		else { _nextToPlay = Player.BLACK;}
		
		// Set color of spot, toggle to color
		/*
		for (int y = _board.getSpotHeight()-1; y >= 0; y--) {
			Spot spot = _board.getSpotAt(s.getSpotX(), y);
			if (spot.isEmpty()) { s = spot; break;}
			if (y == 0) {return;}
		}*/
		
		s.setSpotColor(playerColor);
		s.toggleSpot();
		spotExited(s);
		spotEntered(s);
		
		checkWin(s);
		
		// Someone won the game
		if (_gameWon) {
			// Setting won message
			_message.setText(player1 + " Wins!");
		}
		else {
			boolean drawGame = true;
			for (Spot spot : _board) {
				if (spot.isEmpty()) {drawGame = false; break;}
			}
			if (drawGame) { _message.setText("Draw Game."); _gameWon = true;}
			else {_message.setText(player2 + " to play."); }
		}
	}
	
	private void checkWin(Spot s) {
		Color playerColor = s.getSpotColor();
		
		// Checking each column and row
		
		int width = _board.getSpotWidth();
		int height = _board.getSpotHeight();
		
		for (int dimension = 0; dimension < 2; dimension++) {
			boolean checkRows = dimension == 1;
			int limit1 = checkRows ? height : width;
			int limit2 = checkRows ? width : height;
			
			for (int var1 = 0; var1 < limit1; var1++) {
				int counter = 0;
				Spot start = null;
				for (int var2 = 0; var2 < limit2; var2++) {
					int x = checkRows ? var1 : var2;
					int y = checkRows ? var2 : var1;
					Spot spot = _board.getSpotAt(x, y);
					if (spot.isEmpty() || playerColor != spot.getSpotColor()) {
						counter = 0;
						continue;
					}
					if (counter == 0) {
						start = spot;
					}
					counter++;
					
					if (counter >= 4) {
						spotExited(s);
						_gameWon = true;
						for (int i = 0; i < counter; i++) {
							int row = start.getSpotY();
							int col = start.getSpotX();
							if (checkRows) { row += i; }
							else { col += i;}
							_board.getSpotAt(col, row).highlightSpot();
						}
						return;
					}
				}
			}
		}
		
		// Checking diagonals
		for (int i = 0; i < 12; i++) {
			int counter = 0;
			Spot start = null;
			boolean leftToRight = i < 6;
			int x;
			int y;
			if (leftToRight) {
				x = i < 3 ? 0 : i-2;
				y = i < 3 ? i+3 : 5;
			}
			else {
				i -= 6;
				x = i < 3 ? i+3 : 6;
				y = i < 3 ? 5 : 8-i;
				i+=6;
			}
			while (true) {
				if (!validXY(x,y)) {
					break;
				}
				Spot spot = _board.getSpotAt(x, y);
				x--; y--;
				if (leftToRight) {x += 2;}
				
				if (spot.isEmpty() || playerColor != spot.getSpotColor()) {
					counter = 0;
					continue;
				}
				if (counter == 0) {
					start = spot;
				}
				counter++;
				if (counter >= 4) {
					spotExited(s);
					_gameWon = true;
					for (int j = 0; j < counter; j++) {
						int col = start.getSpotX();
						int row = start.getSpotY();
						if (leftToRight) {col += 2*j;}
						_board.getSpotAt(col - j, row - j).highlightSpot();
					}
					return;
				}
			}
		}
		
		// Checking diagonals from bottom right to top left
		/*
		for (int i = 0; i < 6; i++) {
			int counter = 0;
			Spot start = null;
			int x = i < 3 ? i+3 : 6;
			int y = i < 3 ? 5 : 8-i;
			
			while (true) {
				if (!validXY(x,y)) {
					break;
				}
				Spot spot = _board.getSpotAt(x, y);
				x--; y--;
				
				if (spot.isEmpty() || playerColor != spot.getSpotColor()) {
					counter = 0;
					continue;
				}
				if (counter == 0) {
					start = spot;
				}
				counter++;
				
				if (counter >= 4) {
					spotExited(s);
					_gameWon = true;
					for (int j = 0; j < counter; j++) {
						int column = start.getSpotX();
						int row = start.getSpotY();
						_board.getSpotAt(column - j, row - j).highlightSpot();
					}
					return;
				}
			}
		}*/
	}
	
	private boolean validXY(int x, int y) {
		return !(x < 0 || x == _board.getSpotWidth() || y < 0 || y == _board.getSpotHeight());
	}
		
	

	public void spotEntered(Spot s) {
		// Highlight spots on column when game is continuing
		if (_gameWon) {return;}
		for (int y = 0; y < _board.getSpotHeight(); y++) {
			Spot spot = _board.getSpotAt(s.getSpotX(), y);
			if (spot.isEmpty()) {
				spot.highlightSpot();
			}
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
