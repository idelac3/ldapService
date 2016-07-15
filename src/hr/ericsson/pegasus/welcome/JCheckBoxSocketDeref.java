package hr.ericsson.pegasus.welcome;

import javax.swing.JCheckBox;

public class JCheckBoxSocketDeref extends JCheckBox {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JCheckBoxSocketDeref(JPanelSocketItem parentContainer) {
		addActionListener(new ActionListenerSocketDeref(parentContainer));
		setText("Deref.");
		setToolTipText("Mark interface for dereferencing alias entry on LDAP MODIFY operation.");
		setSelected(false);
	}
}
