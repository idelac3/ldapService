package hr.ericsson.pegasus.test;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;

public class DeleteSubEntry {

	/**
	 * Delete second level, root DN child's child entry.<BR>
	 * This function performs LDAP DELETE operation.
	 */
	public static LDAPResult delSubEntry(LDAPConnection ldapConnection, int id, int subId) throws LDAPException {
		
		String rootDN = LdapFunctionTest.getDN();
		
		LDAPResult result = ldapConnection.delete("UserContainerName=MOD" + subId + ",ApplicationName=APP" + id + "," + rootDN);
		return result;

	}
}
