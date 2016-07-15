package hr.ericsson.pegasus.welcome;

import javax.swing.JTextField;

public class JTextFieldSocketPort extends JTextField {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JTextFieldSocketPort(String initialText, JPanelSocketItem parentContainer) {
		getDocument().addDocumentListener(new DocumentListenerSocketPort(parentContainer));
		setText(initialText);
		setToolTipText("Enter local tcp port number. Usually 389 for LDAP and 1030 for LDAP with dereferencing.");
		setColumns(5);
	}
}
