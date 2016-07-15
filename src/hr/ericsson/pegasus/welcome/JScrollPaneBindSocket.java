package hr.ericsson.pegasus.welcome;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class JScrollPaneBindSocket extends JScrollPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JScrollPaneBindSocket() {
		
		JPanel pnl1 = new JPanel();
		
		pnl1.setLayout(new BoxLayout(pnl1, BoxLayout.Y_AXIS));
		
		pnl1.add(new JLabel("Socket interfaces to bind:"));
		
		pnl1.add(new JPanelSocketItem("0.0.0.0", "389"));
		pnl1.add(new JPanelSocketItem("0.0.0.0", "1030"));
		
		setViewportView(pnl1);
		
		JFrameWelcome.jPanelBindSocket = pnl1;
	}
}
