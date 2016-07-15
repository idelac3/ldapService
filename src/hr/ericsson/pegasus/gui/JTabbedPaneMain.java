package hr.ericsson.pegasus.gui;

import javax.swing.JTabbedPane;

/**
 * <H1>Tab panel</H1>
 * <HR>
 * Holds panel for <I>Log Console</I> {@link JScrollPaneLogs} 
 *  and <I>Statistic data</I> {@link JScrollPaneStat}.
 * <HR>
 * @author eigorde
 *
 */
public class JTabbedPaneMain extends JTabbedPane {

	private static final long serialVersionUID = 1L;
	
	public JTabbedPaneMain() {
		add("Log console", new JScrollPaneLogs());
		add("Statistic grid", new JScrollPaneStat());
	}
}
