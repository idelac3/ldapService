package hr.ericsson.pegasus.test;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;

public class AddAliasEntry {

	/**
	 * Add first level, root DN child, alias entry.<BR>
	 * This function performs LDAP ADD operation.
	 */
	public static LDAPResult addAlias(LDAPConnection ldapConnection, int id) throws LDAPException {

		Attribute objectClass = new Attribute("objectClass", "alias", "extensibleObject");
		Attribute applicationName = new Attribute("aliasedObjectName", "ApplicationName=APP" + id + ",o=ericsson,dc=com");
		
		Entry entry = new Entry("APP=" + id + ",o=ericsson,dc=com", objectClass, applicationName);
		
		LDAPResult result = ldapConnection.add(entry);
		return result;

	}
}
