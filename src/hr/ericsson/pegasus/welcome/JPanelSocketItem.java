package hr.ericsson.pegasus.welcome;

import java.awt.FlowLayout;

import javax.swing.JPanel;

public class JPanelSocketItem extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanelSocketItem me = this;
	
	public String socketInterface = "";
	public String socketPort = "";
	public boolean deref = false;
	
	public JPanelSocketItem(String addr, String port) {
		
		setLayout(new FlowLayout());
		
		add(new JTextFieldSocketInterface(addr, me));
		socketInterface = addr;
		
		add(new JTextFieldSocketPort(port, me));
		socketPort = port;
		
		add(new JCheckBoxSocketDeref(me));
	}
}
