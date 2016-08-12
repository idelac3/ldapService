package hr.ericsson.pegasus.gui;

import hr.ericsson.pegasus.Pegasus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <H1>Refresh statistic data, ActionListener</H1>
 * <HR>
 * This action defined here will get new values from variables {@link Pegasus}
 * which hold statistic data and apply them to {@link TableModelStat}.<BR>
 *  For more information, see {@link JScrollPaneStat}.
 * <HR>
 * @author igor.delac@gmail.com
 *
 */
public class ActionListenerRefresh implements ActionListener {

	private final TableModelStat tableModelStat;
	
	private long clientConnections = 0,
			searchRequestsBase = 0,
			searchRequestsOne = 0,
			searchRequestsSub = 0,
			entryResults = 0,
			failedSearch = 0,
			modifyRequests = 0,
			failedModify = 0,
			addRequest = 0,
			failedAdd = 0,
			deleteRequest = 0,
			failedDelete = 0;
	
	public ActionListenerRefresh(TableModelStat tableModelStat) {
		this.tableModelStat = tableModelStat;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		int columnIndex = 1;
		
		boolean delta = JFrameGui.deltaStatistic.isSelected();
		
		long memAvail = 100 * Runtime.getRuntime().freeMemory() / Runtime.getRuntime().totalMemory();

		if (delta) {
			
			// Calculate delta values and show in grid.
			tableModelStat.setValueAt(Pegasus.clientConnections  - clientConnections,  0, columnIndex);
			tableModelStat.setValueAt(Pegasus.searchRequestsBase - searchRequestsBase, 1, columnIndex);
			tableModelStat.setValueAt(Pegasus.searchRequestsOne  - searchRequestsOne,  2, columnIndex);
			tableModelStat.setValueAt(Pegasus.searchRequestsSub  - searchRequestsSub,  3, columnIndex);
			tableModelStat.setValueAt(Pegasus.entryResults       - entryResults,       4, columnIndex);
			tableModelStat.setValueAt(Pegasus.failedSearch       - failedSearch,       5, columnIndex);
			tableModelStat.setValueAt(Pegasus.modifyRequests     - modifyRequests,     6, columnIndex);
			tableModelStat.setValueAt(Pegasus.failedModify       - failedModify,       7, columnIndex);
			tableModelStat.setValueAt(Pegasus.addRequest         - addRequest,         8, columnIndex);
			tableModelStat.setValueAt(Pegasus.failedAdd          - failedAdd,          9, columnIndex);
			tableModelStat.setValueAt(Pegasus.deleteRequest      - deleteRequest,     10, columnIndex);
			tableModelStat.setValueAt(Pegasus.failedDelete       - failedDelete,      11, columnIndex);
			tableModelStat.setValueAt(memAvail,                                       12, columnIndex);
			
			// Update local values.
			clientConnections = Pegasus.clientConnections;
			searchRequestsBase = Pegasus.searchRequestsBase;
			searchRequestsOne = Pegasus.searchRequestsOne;
			searchRequestsSub = Pegasus.searchRequestsSub;
			entryResults = Pegasus.entryResults;
			failedSearch = Pegasus.failedSearch;
			modifyRequests = Pegasus.modifyRequests;
			failedModify = Pegasus.failedModify;
			addRequest = Pegasus.addRequest;
			failedAdd = Pegasus.failedAdd;
			deleteRequest = Pegasus.deleteRequest;
			failedDelete = Pegasus.failedDelete;
			
		}
		else {
			tableModelStat.setValueAt(Pegasus.clientConnections,  0, columnIndex);
			tableModelStat.setValueAt(Pegasus.searchRequestsBase, 1, columnIndex);
			tableModelStat.setValueAt(Pegasus.searchRequestsOne,  2, columnIndex);
			tableModelStat.setValueAt(Pegasus.searchRequestsSub,  3, columnIndex);
			tableModelStat.setValueAt(Pegasus.entryResults,       4, columnIndex);
			tableModelStat.setValueAt(Pegasus.failedSearch,       5, columnIndex);
			tableModelStat.setValueAt(Pegasus.modifyRequests,     6, columnIndex);
			tableModelStat.setValueAt(Pegasus.failedModify,       7, columnIndex);
			tableModelStat.setValueAt(Pegasus.addRequest,         8, columnIndex);
			tableModelStat.setValueAt(Pegasus.failedAdd,          9, columnIndex);
			tableModelStat.setValueAt(Pegasus.deleteRequest,     10, columnIndex);
			tableModelStat.setValueAt(Pegasus.failedDelete,      11, columnIndex);
			tableModelStat.setValueAt(memAvail,                  12, columnIndex);
		}
	}

}
