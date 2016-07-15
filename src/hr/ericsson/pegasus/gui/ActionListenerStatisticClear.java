package hr.ericsson.pegasus.gui;

import hr.ericsson.pegasus.Pegasus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * <H1>Clear statistic data, ActionListener</H1>
 * <HR>
 * This action defined here will clear variables in {@link Pegasus}
 * which hold statistic data. For more information, see {@link JScrollPaneStat}.
 * <HR>
 * @author eigorde
 *
 */
public class ActionListenerStatisticClear implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		Pegasus.clientConnections  = 0;
		Pegasus.searchRequestsBase = 0;
		Pegasus.searchRequestsOne  = 0;
		Pegasus.searchRequestsSub  = 0;
		Pegasus.entryResults       = 0;
		Pegasus.failedSearch       = 0;
		Pegasus.modifyRequests     = 0;
		Pegasus.failedModify       = 0;
		Pegasus.addRequest         = 0;
		Pegasus.failedAdd          = 0;
		Pegasus.deleteRequest      = 0;
		Pegasus.failedDelete       = 0;
	}

}
