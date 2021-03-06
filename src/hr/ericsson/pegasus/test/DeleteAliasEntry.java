package hr.ericsson.pegasus.test;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;

public class DeleteAliasEntry {

	/**
	 * Delete first level, root DN child, alias entry.<BR>
	 * This function performs LDAP DELETE operation.
	 */
	public static LDAPResult delAliasEntry(LDAPConnection ldapConnection, int id) throws LDAPException {
		
		String rootDN = LdapFunctionTest.getDN();
		
		LDAPResult result = ldapConnection.delete("roomNumber=" + id + "," + rootDN);
		return result;

	}
}
