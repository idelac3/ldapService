package hr.ericsson.pegasus.welcome;

import javax.swing.JScrollPane;

public class JScrollPaneSchema extends JScrollPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JScrollPaneSchema() {
		
		JTextAreaLDIFSchema jtextAreaSchema = new JTextAreaLDIFSchema("schema");
		
		setViewportView(jtextAreaSchema);
		
		JFrameWelcome.jTextAreaSchema = jtextAreaSchema;
	}
}
