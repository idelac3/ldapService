package hr.ericsson.pegasus.gui;

import hr.ericsson.pegasus.Pegasus;

import javax.swing.JScrollPane;

/**
 * <H1>Statistic data panel</H1>
 * <HR>
 * This panel has a table where statistic are printed.<BR>
 * See {@link Pegasus} definition of variables for statistic 
 * ({@link Pegasus#addRequest}, {@link Pegasus#clientConnections}, {@link Pegasus#entryResults}, etc.)<BR>
 * <HR>
 * @author eigorde
 *
 */
public class JScrollPaneStat extends JScrollPane {

	private static final long serialVersionUID = 1L;
	
	public JScrollPaneStat() {
		setViewportView(new JTableStat());
	}
}
