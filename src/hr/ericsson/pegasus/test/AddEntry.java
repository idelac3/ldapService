package hr.ericsson.pegasus.test;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;

public class AddEntry {

	/**
	 * Add first level, root DN child, entry.<BR>
	 * This function performs LDAP ADD operation.
	 */
	public static LDAPResult addEntry(LDAPConnection ldapConnection, int id) throws LDAPException {

		Attribute objectClass = new Attribute("objectClass", "top", "UserDatabaseApplication");
		Attribute applicationName = new Attribute("ApplicationName", "APP" + id);
		
		Entry entry = new Entry("ApplicationName=APP" + id + ",o=ericsson,dc=com", objectClass, applicationName);
		
		LDAPResult result = ldapConnection.add(entry);
		return result;

	}

	/**
	 * Try to add entry with DN.<BR>
	 * This function performs LDAP ADD operation.
	 */
	public static LDAPResult addInvalidEntry(LDAPConnection ldapConnection) throws LDAPException {

		Attribute objectClass = new Attribute("objectClass", "top", "UserDatabaseApplication");
		Attribute applicationName = new Attribute("ApplicationName", "APPxxx");
		
		Entry entry = new Entry("ApplicationName=APPxxx,yyy,o=ericsson,dc=com", objectClass, applicationName);
		
		LDAPResult result = ldapConnection.add(entry);
		return result;

	}
}
