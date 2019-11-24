package a7;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import a7.JSpotBoard.BoardStyle;

public class TicTacToeWidget extends JPanel implements ActionListener, SpotListener{

	// Enum to identify player
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private enum Player {BLACK, WHITE};
	
	private JSpotBoard _board;		//SpotBoard playing area
	private JLabel _message;		// Label for messages
	private boolean _gameWon;		// Game Won boolean
	private Player _nextToPlay;		// Turn of player 2
	
	public TicTacToeWidget() {
		
		// Create SpotBoard and message label
		_board = new JSpotBoard(3,3, BoardStyle.UNIFORM);
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
		}
		
		// Reset game won and next to play
		_gameWon = false;
		_nextToPlay = Player.WHITE;
		
		// Start message
		_message.setText("Welcome to Tic Tac Toe. White to play.");
		
	}
	
	// Implementation of SpotListener, logic to run tic tac toe game
	
	public void spotClicked(Spot s) {
		// Checking game won or if spot has already been clicked on
		if(_gameWon || !s.isEmpty()) {return;}
		
		// Variables for player name, color, and next player, assuming start of game
		String player1 = "White";
		String player2 = "Black";
		Color playerColor = Color.WHITE;
		
		if(_nextToPlay == Player.BLACK) {
			player1 = "Black";
			player2 = "White";
			playerColor = Color.BLACK;
			_nextToPlay = Player.WHITE;
		}
		else { _nextToPlay = Player.BLACK;}
		
		// Set color of spot, toggle to color
		s.setSpotColor(playerColor);
		s.toggleSpot();
		spotExited(s);
		
		checkWin(playerColor);
		
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
	
	private void checkWin(Color playerColor) {
		// Checking each row
		
		for (int y = 0; y < _board.getSpotHeight(); y++) {
			Spot test = _board.getSpotAt(0, y);
			if (test.isEmpty() || playerColor != test.getSpotColor()) {continue;}
			
			_gameWon = true;
			for (int x = 1; x < _board.getSpotWidth(); x++) {
				Spot spot = _board.getSpotAt(x, y);
				if (spot.isEmpty() || playerColor != spot.getSpotColor()) {
					_gameWon = false;
				}
			}
		}
		if (_gameWon) {return;}
		
		// Checking each column
		for (int x = 0; x < _board.getSpotWidth(); x++) {
			Spot test = _board.getSpotAt(x, 0);
			if (test.isEmpty() || playerColor != test.getSpotColor()) {continue;}
			
			_gameWon = true;
			for (int y = 0; y < _board.getSpotWidth(); y++) {
				Spot spot = _board.getSpotAt(x, y);
				if (spot.isEmpty() || playerColor != spot.getSpotColor()) {
					_gameWon = false;
				}
			}
		}
		if (_gameWon) {return;}
		
		// Variable for middle spot at 1,1
		Spot middle = _board.getSpotAt(1, 1);
		
		// Checking middle point
		if (middle.isEmpty() || middle.getSpotColor() != playerColor) {return; }
			
		if ((_board.getSpotAt(0, 0).getSpotColor().equals(playerColor) &&
			 _board.getSpotAt(2, 2).getSpotColor().equals(playerColor)) ||
			(_board.getSpotAt(2, 0).getSpotColor().equals(playerColor) &&
			 _board.getSpotAt(0, 2).getSpotColor().equals(playerColor))) {
				_gameWon = true;
		}
	}
		
	

	public void spotEntered(Spot s) {
		// Highlight spot when game is continuing and when spot is not filled
		if (_gameWon || !s.isEmpty()) {	return;	}
		s.highlightSpot();
	}

	public void spotExited(Spot s) {
		// Unhighlight spot on exit
		s.unhighlightSpot();
	}

	public void actionPerformed(ActionEvent e) {
		// Reset game button is pressed
		resetGame();
	}

}
