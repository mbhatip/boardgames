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
	private Color _playerColor;		// Player 1 color
	
	public OthelloWidget() {
		
		// Create SpotBoard and message label, initialize selection spot
		_board = new JSpotBoard(8,8, BoardStyle.CHECKERED);
		_message = new JLabel();
		_selection = null;
		
		// Set layout and place spot board at center
		setLayout(new BorderLayout());
		add(_board, BorderLayout.CENTER);
		
		// Sub-panel for message and reset button
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
			s.setSpotColor(Color.RED);
		}
		
		// coordinates for points
		int x[] = {3, 4, 4, 3};
		int y[] = {3, 3, 4, 4};
		
		Color[] c = {Color.WHITE, Color.BLACK};
		
		// putting points on board with loop
		for (int i = 0; i < x.length; i++) {
			Spot s = _board.getSpotAt(x[i], y[i]);
			s.setSpotColor(c[i%2]);
			s.toggleSpot();
		}
		
		// Reset game variables
		_gameWon = false;
		_nextToPlay = Player.BLACK;
		_playerColor = Color.BLACK;
		_selection = null;
		
		// Start message
		_message.setText("Welcome to Othello. Black to play.");
		
	}
	
	// Implementation of SpotListener
	
	public void spotClicked(Spot s) {
		
		// Setting global selection spot variable equal to s
		_selection = s;
		
		// if spot move is not valid (highlighted), return
		if (!_selection.isHighlighted()) return;
		
		// getting list of spots to change
		List<Spot> spotsToChange = returnValidSpots();
		
		// Variables for player name, color, next player assuming start of game
		String player1 = "Black";
		String player2 = "White";
		_playerColor = Color.BLACK;
		
		// Checking to see if players need to be changed
		if(_nextToPlay == Player.WHITE) {
			player1 = "White";
			player2 = "Black";
			_playerColor = Color.WHITE;
			_nextToPlay = Player.BLACK;
		}
		else { _nextToPlay = Player.WHITE;}	
		
		// adding selection spot to list of spots that need to change
		spotsToChange.add(_selection);
		
		// Change color of each spot in list
		for (Spot spot : spotsToChange) {
			spot.clearSpot();
			spot.setSpotColor(_playerColor);
			spot.toggleSpot();
		}
		
		// unhighlight spot after finished
		_selection.unhighlightSpot();

		// checking if moves can be made
		checkWin();
		
		if (_gameWon) {
			// calculating score
			int player1Count = 0;
			int player2Count = 0;
			
			for (Spot spot : _board) {
				if (!spot.isEmpty()) {
					if (spot.getSpotColor().equals(_playerColor)) {
						player1Count++;
					}
					else {
						player2Count++;
					}
				}
			}
			
			// variables based on who won
			
			String winner = player1Count > player2Count ? player1 : player2;
			int score = player1Count > player2Count ? player1Count : player2Count;
			int score2 = player1Count > player2Count ? player2Count : player1Count;
			
			if (score == score2) winner = "Draw. ";
			else winner += " Wins!";
			
			// display winning message
			_message.setText("Game over. " + winner + " Score: " + score + " to " + score2);
			return;
		}
		// continue game and update text
		else if (_nextToPlay == Player.BLACK) {
			_message.setText("Black to play.");
			_playerColor = Color.BLACK;
		}
		else {
			_message.setText("White to play.");
			_playerColor = Color.WHITE;
		}
	}
	
	private List<Spot> returnValidSpots() {
		
		if (!_selection.isEmpty()) {return new ArrayList<>();}
		
		List<Spot> row = new ArrayList<>();
		List<Spot> col = new ArrayList<>();
		List<Spot> dia = new ArrayList<>();
		List<Spot> dia2 = new ArrayList<>();
		List<Spot> validSpots = new ArrayList<>();
		
		for (Spot spot : _board) {
			if (_selection.getSpotX() == spot.getSpotX()) {
				col.add(spot);
			}
			if (_selection.getSpotY() == spot.getSpotY()) {
				row.add(spot);
			}
			if (_selection.getSpotX() - _selection.getSpotY() ==
				spot.getSpotX() - spot.getSpotY()) {
				dia.add(spot);
			}
			if (_selection.getSpotX() + _selection.getSpotY() ==
					spot.getSpotX() + spot.getSpotY()) {
					dia2.add(spot);
			}
		}
		
		validSpots.addAll(getList(row));
		validSpots.addAll(getList(col));
		validSpots.addAll(getList(dia));
		validSpots.addAll(getList(dia2));
		
		return validSpots;
	}
	
	private List<Spot> getList(List<Spot> spots) {
		List<Spot> allSpots = new ArrayList<>();
		List<Spot> temp = new ArrayList<>();
		boolean started = false;
		for (Spot s : spots) {
			if (s.equals(_selection)) {
				temp.clear();
				started = true;
				continue;
			}
			if (!started) {continue;}
			
			if (s.isEmpty()) {
				temp.clear(); break;
			}
			if (s.getSpotColor().equals(_playerColor)) {
				started = false;
				break;																																																						
			}
			temp.add(s);
		}
		if (!started) {
			allSpots.addAll(temp);
		}
		
		temp.clear();
		started = false;
		for (Spot s : spots) {
			if (s.getSpotColor().equals(_playerColor)) {
				temp.clear();
				started = true;
				continue;
			}
			if (!started) {continue;}
			
			if (s.equals(_selection)) {
				started = false;
				break;
			}
			if (s.isEmpty()) {
				temp.clear(); started = false; continue;
			}
			temp.add(s);
		}
		if (!started) {
			allSpots.addAll(temp);
		}
		return allSpots;
	}
	
	/*
	 * function checks to see if game is finished
	 * both players dont have valid moves
	 */
	private void checkWin() {
		boolean noMoves = false;
		while (true) {
			List<Spot> temp = new ArrayList<>();			
			if (_nextToPlay == Player.WHITE) {
				_playerColor = Color.WHITE;
			}
			else {
				_playerColor = Color.BLACK;
			}
			
			for (Spot spot : _board) {
				_selection = spot;
				temp.addAll(returnValidSpots());
			}
			if (temp.isEmpty()) {
				if (noMoves) {
					_gameWon = true;
					return;
				}
				noMoves = true;
				if(_nextToPlay == Player.WHITE)
				{ _nextToPlay = Player.BLACK;}
				else {_nextToPlay = Player.WHITE;}
			}
			else {
				return;
			}
		}
	}

	public void spotEntered(Spot s) {
		if (_gameWon) {return;}
		
		// Make global variable selection equal to s
		_selection = s;
		
		// Highlight valid spots where move can be made
		if (!returnValidSpots().isEmpty()) {
			_selection.highlightSpot();
		}
	}

	public void spotExited(Spot s) {
		if (_gameWon) {
			return;
		}
		s.unhighlightSpot();
	}

	public void actionPerformed(ActionEvent e) {
		// Reset game button is pressed
		resetGame();
	}

}
