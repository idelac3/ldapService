package hr.ericsson.pegasus.gui;

import javax.swing.JTextArea;

/**
 * <H1>Log Console text area</H1>
 * <HR>
 * This is text area where log messages are printed.<BR>
 * See {@link JFrameGui}, functions <I>append(String)</I> and <I>log(String)</I>.
 * <HR>
 * @author eigorde
 *
 */
public class JTextAreaLogs extends JTextArea {

	private static final long serialVersionUID = 1L;

	public JTextAreaLogs() {
		JFrameGui.jLogArea = this;
		setFont(JFrameGui.defaultFont);
	}
}
