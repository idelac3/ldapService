package hr.ericsson.pegasus.welcome;

import javax.swing.JButton;

public class JButtonSocketItemAdd extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JButtonSocketItemAdd() {
		setText("Add");
		setToolTipText("Add new socket interface. Usually first interface is not dereferencing and second is marked for dereferencing.");
		addActionListener(new ActionListenerSocketItemAdd());
	}
}
