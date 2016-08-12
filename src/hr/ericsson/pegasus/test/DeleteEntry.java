package hr.ericsson.pegasus.test;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;

public class DeleteEntry {

	/**
	 * Delete first level, root DN child, entry.<BR>
	 * This function performs LDAP DELETE operation.
	 */
	public static LDAPResult delEntry(LDAPConnection ldapConnection, int id) throws LDAPException {
		
		String rootDN = LdapFunctionTest.getDN();
		
		LDAPResult result = ldapConnection.delete("ApplicationName=APP" + id + "," + rootDN);
		return result;

	}
	
	/**
	 * Delete invalid DN entry.<BR>
	 * This function performs LDAP DELETE operation.
	 */
	public static LDAPResult delInvalidEntry(LDAPConnection ldapConnection) throws LDAPException {
		
		String rootDN = LdapFunctionTest.getDN();
		
		LDAPResult result = ldapConnection.delete("ApplicationName=APPxxx," + rootDN);
		return result;

	}
}
