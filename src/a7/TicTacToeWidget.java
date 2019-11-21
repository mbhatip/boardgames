package a7;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TicTacToeWidget extends JPanel implements ActionListener, SpotListener{

	// Enum to identify player
	
	private enum Player {BLACK, WHITE};
	
	private JSpotBoard _board;		//SpotBoard playing area
	private JLabel _message;		// Label for messages
	private boolean _gameWon;		// Game Won boolean
	private Player _nextToPlay;		// Turn of player 2
	
	// Variables to hold positions of players
	private SpotBoard _player1Board;
	private SpotBoard _player2Board;
	
	public TicTacToeWidget() {
		
		// Create SpotBoard and message label
		_board = new JSpotBoard(3,3, false);
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
		_nextToPlay = Player.BLACK;
		
		// Start message
		
		_message.setText("Welcome to Tic Tac Toe. Black to play.");
	}
	
	// Implementation of SpotListener, logic to run tic tac toe game
	
	public void spotClicked(Spot s) {
		// Checking game won or if spot has already been clicked on
		if(_gameWon || !s.isEmpty()) {return;}
		
		// Variables for player name, color, and next player, assuming start of game
		String player1 = "Black";
		String player2 = "White";
		Color playerColor = Color.BLACK;
		
		if(_nextToPlay == Player.WHITE) {
			player1 = "White";
			player2 = "Black";
			playerColor = Color.WHITE;
			_nextToPlay = Player.BLACK;
		}
		else { _nextToPlay = Player.WHITE;}
		
		// Set color of spot, toggle to color
		s.setSpotColor(playerColor);
		s.toggleSpot();
		
		
		
		
	}
	
	private void checkWin(Color playerColor) {
		// Checking each row
		
		for (int y = 0; y < _board.getHeight(); y++) {
			Spot test = _board.getSpotAt(0, y);
			if (test.isEmpty()) {continue;}
			
			_gameWon = true;
			for (int x = 0; x < _board.getWidth(); x++) {
				if (playerColor != test.getSpotColor()) {
					_gameWon = false;
				}
			}
		}
		if (_gameWon) {return;}
		
		// Checking each column
		for (int x = 0; x < _board.getWidth(); x++) {
			Spot test = _board.getSpotAt(x, 0);
			if (test.isEmpty()) {continue;}
			
			_gameWon = true;
			for (int y = 0; y < _board.getWidth(); y++) {
				if (playerColor != test.getSpotColor()) {
					_gameWon = false;
				}
			}
		}
		if (_gameWon) {return;}
		
		// Variable for middle spot at 1,1
		Spot middle = _board.getSpotAt(1, 1);
		
		// Checking middle point
		if (middle.isEmpty() || middle.getSpotColor() != playerColor) {return; }
			
		// Variable for middle color
		Color middleColor = middle.getSpotColor();
		
		if ((_board.getSpotAt(0, 0).getSpotColor().equals(middleColor) &&
			 _board.getSpotAt(2, 2).getSpotColor().equals(middleColor)) ||
			(_board.getSpotAt(2, 0).getSpotColor().equals(middleColor) &&
			 _board.getSpotAt(0, 2).getSpotColor().equals(middleColor))) {
				_gameWon = true; return;
		}
		return;
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
