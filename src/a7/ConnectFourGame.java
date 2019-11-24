package a7;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ConnectFourGame {
	public static void main(String args[]) {

		/* Create top level window. */
		
		JFrame main_frame = new JFrame();
		main_frame.setTitle("Connect Four");
		main_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		/* Create panel for content. Uses BorderLayout. */
		JPanel top_panel = new JPanel();
		top_panel.setLayout(new BorderLayout());
		main_frame.setContentPane(top_panel);

		/* Create Connect Four component and put into center
		 * of content panel.
		 */
		
		ConnectFourWidget c4 = new ConnectFourWidget();
		top_panel.add(c4, BorderLayout.CENTER);


		/* Pack main frame and make visible. */
		
		main_frame.pack();
		main_frame.setVisible(true);	
	}
}
