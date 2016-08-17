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

		String baseDN = LdapFunctionTest.getDN();
		
		Attribute objectClass = new Attribute("objectClass", "top", "room");
		Attribute applicationName = new Attribute("cn", "room" + id);
		
		Entry entry = new Entry("cn=room" + id + "," + baseDN, objectClass, applicationName);
		
		LDAPResult result = ldapConnection.add(entry);
		return result;

	}

	/**
	 * Try to add entry with DN.<BR>
	 * This function performs LDAP ADD operation.
	 */
	public static LDAPResult addInvalidEntry(LDAPConnection ldapConnection) throws LDAPException {

		Attribute objectClass = new Attribute("objectClass", "top", "room");
		Attribute applicationName = new Attribute("cn", "roomXXX");
		
		Entry entry = new Entry("cn=roomXXX,yyy,o=company,dc=com", objectClass, applicationName);
		
		LDAPResult result = ldapConnection.add(entry);
		return result;

	}
}
