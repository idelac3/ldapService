package hr.ericsson.pegasus.gui;

import hr.ericsson.pegasus.Pegasus;

import javax.swing.JTable;
import javax.swing.Timer;

/**
 * <H1>Statistic data, Table</H1>
 * <HR>
 * Shows statistic data collected by {@link Pegasus}
 * and refresh every 1 sec.
 * <HR>
 * @author eigorde
 *
 */
public class JTableStat extends JTable {

	private static final long serialVersionUID = 1L;

	final TableModelStat tableModelStat;
	
	public JTableStat() {
		
		setFont(JFrameGui.defaultFont);
		
		tableModelStat = new TableModelStat(); 
		setModel(tableModelStat);
		
		getColumnModel().getColumn(0).setPreferredWidth(50);
		getColumnModel().getColumn(1).setPreferredWidth(25);
		
		getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRendererRightAligned());
		
		Timer timerRefresh = new Timer(1000, new ActionListenerRefresh(tableModelStat));
		timerRefresh.start();
	}
}
