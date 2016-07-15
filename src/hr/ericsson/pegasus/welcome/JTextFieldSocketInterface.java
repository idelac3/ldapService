package hr.ericsson.pegasus.welcome;

import javax.swing.JTextField;

public class JTextFieldSocketInterface extends JTextField {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JTextFieldSocketInterface(String initialText, JPanelSocketItem parentContainer) {
		getDocument().addDocumentListener(new DocumentListenerSocketInterface(parentContainer));
		setText(initialText);
		setToolTipText("Enter local ip interface or wildcard (0.0.0.0) here to bind.");
		setColumns(20);
	}
}
