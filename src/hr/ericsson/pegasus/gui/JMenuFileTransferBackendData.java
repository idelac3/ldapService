package hr.ericsson.pegasus.gui;

import javax.swing.JMenuItem;

/**
 * <H1>Transfer backend data, File, Menu Item</H1>
 * <HR>
 * Please refer to {@link ActionListenerFileTransferBackendData}.
 * <HR>
 * @author eigorde
 *
 */
public class JMenuFileTransferBackendData extends JMenuItem {

	private static final long serialVersionUID = 1L;

	public JMenuFileTransferBackendData() {
		setText("Transfer Backend data");
		
		addActionListener(
				new ActionListenerFileTransferBackendData());
	}
}
