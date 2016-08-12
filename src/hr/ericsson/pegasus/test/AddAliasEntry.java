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
		
		String rootDN = LdapFunctionTest.getDN();
		
		Attribute objectClass = new Attribute("objectClass", "alias", "extensibleObject");
		Attribute applicationName = new Attribute("aliasedObjectName", "ApplicationName=APP" + id + "," + rootDN);
		
		Entry entry = new Entry("APP=" + id + "," + rootDN, objectClass, applicationName);
		
		LDAPResult result = ldapConnection.add(entry);
		return result;

	}
}
