package hr.ericsson.pegasus.test;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;

public class AddSubEntry {

	/**
	 * Add second level, root DN child sub-entry.
	 * This function performs LDAP ADD operation.
	 */
	public static LDAPResult addSubEntry(LDAPConnection ldapConnection, int id, int subId) throws LDAPException {
		Attribute objectClass = new Attribute("objectClass", "top", "UserContainer");
		Attribute applicationName = new Attribute("UserContainerName", "MOD" + subId);
		
		Entry entry = new Entry("UserContainerName=MOD" + subId + ",ApplicationName=APP" + id + ",o=ericsson,dc=com", objectClass, applicationName);
	
		LDAPResult result = ldapConnection.add(entry);
		return result;

	}
}
