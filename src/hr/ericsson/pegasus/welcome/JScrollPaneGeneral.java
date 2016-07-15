package hr.ericsson.pegasus.welcome;

import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class JScrollPaneGeneral extends JScrollPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JScrollPaneGeneral() {
		
		JFrameWelcome.jCheckBoxEnableGUI = new JCheckBox("", true);
		JFrameWelcome.jCheckBoxEnableGUI.setToolTipText("Select to start application with GUI window.");
		
		JFrameWelcome.jTextFieldCountLimit = new JTextField("128", 20);
		JFrameWelcome.jTextFieldCountLimit.setToolTipText("Set server-side size limit for LDAP SEARCH requests. Max. number of sub-entries pre branch to return.");
		
		JFrameWelcome.jCheckBoxEnableDebugging = new JCheckBox("", false);
		JFrameWelcome.jCheckBoxEnableDebugging.setToolTipText("Enable debug messages on console (std.out or std.err)");
		
		JFrameWelcome.jCheckBoxEnableMulticastReplication = new JCheckBox("", false);
		JFrameWelcome.jCheckBoxEnableMulticastReplication.setToolTipText("Multicast replication sends LDAP MODIFY requests accross network to other instances of this application.");
		
		JFrameWelcome.jTextFieldMulticastInterface = new JTextField("eth0", 20);
		JFrameWelcome.jTextFieldMulticastInterface.setToolTipText("Ethernet Network Interface for multicast replication.");
		
		JFrameWelcome.jTextFieldMulticastGroup = new JTextField("230.100.100.1", 20);
		JFrameWelcome.jTextFieldMulticastGroup.setToolTipText("Leave default unless in same subnet are more groups.");
		
		JFrameWelcome.jTextFieldMulticastPort = new JTextField("7100", 20);
		JFrameWelcome.jTextFieldMulticastPort.setToolTipText("Default udp port for multicast replication.");
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(0, 2));
		
		mainPanel.add(new JLabel("General ", JLabel.RIGHT));
		mainPanel.add(new JLabel("settings:", JLabel.LEFT));
		
		mainPanel.add(new JLabel("Enable GUI: ", JLabel.RIGHT));
		mainPanel.add(JFrameWelcome.jCheckBoxEnableGUI);
		
		mainPanel.add(new JLabel("Count limit: ", JLabel.RIGHT));
		mainPanel.add(JFrameWelcome.jTextFieldCountLimit);	
		
		mainPanel.add(new JLabel("Enable debugging: ", JLabel.RIGHT));
		mainPanel.add(JFrameWelcome.jCheckBoxEnableDebugging);
		
		mainPanel.add(new JLabel("--- Multicast ", JLabel.RIGHT));
		mainPanel.add(new JLabel("replication ---", JLabel.LEFT));
		
		mainPanel.add(new JLabel("Enable multicast replication: ", JLabel.RIGHT));
		mainPanel.add(JFrameWelcome.jCheckBoxEnableMulticastReplication);
		
		mainPanel.add(new JLabel("Multicast interface: ", JLabel.RIGHT));
		mainPanel.add(JFrameWelcome.jTextFieldMulticastInterface);

		mainPanel.add(new JLabel("Multicast group: ", JLabel.RIGHT));
		mainPanel.add(JFrameWelcome.jTextFieldMulticastGroup);

		mainPanel.add(new JLabel("Multicast port: ", JLabel.RIGHT));
		mainPanel.add(JFrameWelcome.jTextFieldMulticastPort);
		
		setViewportView(mainPanel);
		
		
	}
}
