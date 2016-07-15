package hr.ericsson.pegasus.gui;

import hr.ericsson.pegasus.Pegasus;
import hr.ericsson.pegasus.backend.TransferBackendData;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * <H1>Transfer Backend data, ActionListener</H1>
 * <HR>
 * This action defined here will synchronize this instance of {@link Pegasus} application and
 * remote peer instance.
 * <HR>
 * @author eigorde
 *
 */
public class ActionListenerFileTransferBackendData implements ActionListener {

	private static String hostname = "";
	private static String port = "389";
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		Component parentComponent = Pegasus.gui;
		String title = "Transfer Backend data";
		int optionType = JOptionPane.YES_NO_OPTION;
		int messageType = JOptionPane.QUESTION_MESSAGE;

		final JTextField txtRemoteHost = new JTextField(14);
		txtRemoteHost.setText(hostname);
		txtRemoteHost.setToolTipText("Please check that remote host has at least one non-dereferencing socket available.");
		
		final JTextField txtPort = new JTextField(14);
		txtPort.setText(port);
		txtPort.setToolTipText("Please check that remote host port is not dereferencing aliases on modification.");
		
		JPanel message = new JPanel(new GridLayout(0, 1));
		
		JPanel row1 = new JPanel();
		row1.add(new JLabel("Remote host:", SwingConstants.RIGHT));
		row1.add(txtRemoteHost);
		
		JPanel row2 = new JPanel();
		row2.add(new JLabel("Remote port:", SwingConstants.RIGHT));
		row2.add(txtPort);
		
		message.add(row1);
		message.add(row2);
		message.add(new JLabel("Start backend data transfer to remote peer ?"));
		
		if (JOptionPane.showConfirmDialog(parentComponent, message, title, optionType, messageType) == JOptionPane.YES_OPTION) {
			
			hostname = txtRemoteHost.getText();
			port = txtPort.getText();
			
			String remoteHost = hostname;
			int remotePort = Pegasus.toInteger(port, 389);
			
			if (remoteHost.trim().length() == 0) {
				JOptionPane.showMessageDialog(parentComponent, "Invalid remote host value.", title, JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			TransferBackendData transferBackendData = new TransferBackendData(Pegasus.myBackend, remoteHost, remotePort);
			Thread transferThread = new Thread(transferBackendData, "transferBackendData[" + remoteHost + ":" + remotePort + "]");
			transferThread.setPriority(Thread.MIN_PRIORITY);
			transferThread.start();
			
		}
		
				
	}

}
