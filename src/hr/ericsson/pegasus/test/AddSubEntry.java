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
		
		String rootDN = LdapFunctionTest.getDN();
		
		Attribute objectClass = new Attribute("objectClass", "top", "room");
		Attribute applicationName = new Attribute("cn", "MOD" + subId);
		
		Entry entry = new Entry("cn=MOD" + subId + ",cn=room" + id + "," + rootDN, objectClass, applicationName);
	
		LDAPResult result = ldapConnection.add(entry);
		return result;

	}
}
