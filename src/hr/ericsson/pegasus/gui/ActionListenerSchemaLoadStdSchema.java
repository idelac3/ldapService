package hr.ericsson.pegasus.gui;

import hr.ericsson.pegasus.Pegasus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.schema.Schema;

/**
 * <H1>Schema, Load std. schema, ActionListener</H1>
 * <HR>
 * This action defined here will enabled schema validation and load <B>only</B> standard schema
 * file provided by UnboundID.
 * <HR>
 * @author igor.delac@gmail.com
 *
 */
public class ActionListenerSchemaLoadStdSchema implements ActionListener {

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			Pegasus.schema = Schema.getDefaultStandardSchema();
			Pegasus.debug("Std. schema loaded, schema validation enabled now.");
			Pegasus.debug("Please load additional schema file(s) if needed.");
		} catch (LDAPException e1) {
			Pegasus.debug(e1.getDiagnosticMessage());
			e1.printStackTrace();
		}		
	}

}
