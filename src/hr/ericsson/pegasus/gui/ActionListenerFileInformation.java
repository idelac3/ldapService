package hr.ericsson.pegasus.gui;

import hr.ericsson.pegasus.ClientListener;
import hr.ericsson.pegasus.Pegasus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.unboundid.util.Debug;

/**
 * <H1>Information, ActionListener</H1>
 * <HR>
 * This action defined here will show dialog with various system information.
 * <HR>
 * @author eigorde
 *
 */
public class ActionListenerFileInformation implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {

		/*
		 * General information, like CPU and memory.
		 */
		String[] sysObj = {
				"System information",
				"    CPU core count: " + Runtime.getRuntime().availableProcessors(),
				"  Available memory: " + Pegasus.formatInteger(Runtime.getRuntime().freeMemory()),
				" Debugging enabled: " + (Debug.debugEnabled() ? "yes" : "no"),
				" ",
		};
		
		/*
		 * Join all information into single list.
		 */
		List<String> obj = new ArrayList<String>();
		for (String item : sysObj) {
			obj.add(item);
		}
		
		obj.add("Client Listener instances:");
		for (ClientListener listener : Pegasus.clientListenerList) {
			obj.add(listener.toString());
			if (listener.getMessageHandlerVersion().length() > 0) {
				/*
				 * Here a return value is only available if at least one client
				 * has an open tcp connection. Otherwise message handlers are not
				 * even loaded into channel pipeline.
				 */
				obj.add("  MessageHandler version: " + listener.getMessageHandlerVersion());
			}
		}
		obj.add(" ");
		
		obj.add("Loaded LDIF files:");
		for (File file : Pegasus.myBackend.getLoadedLDIFList()) {
			obj.add(file.getAbsolutePath());
		}
		obj.add(" ");
		
		JOptionPane.showMessageDialog(Pegasus.gui, obj.toArray(), "Information", JOptionPane.INFORMATION_MESSAGE);
		
	}

}
