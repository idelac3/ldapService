package hr.ericsson.pegasus.gui;

import javax.swing.JScrollPane;

/**
 * <H1>Log Console panel</H1>
 * <HR>
 * This panel has a text area where log messages are printed.<BR>
 * See {@link JFrameGui}, functions <I>append(String)</I> and <I>log(String)</I>.
 * <HR>
 * @author eigorde
 *
 */
public class JScrollPaneLogs extends JScrollPane {

	private static final long serialVersionUID = 1L;

	public JScrollPaneLogs() {
		setViewportView(new JTextAreaLogs());
	}
}
