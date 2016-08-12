package hr.ericsson.pegasus.test;

import com.unboundid.ldap.sdk.DereferencePolicy;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;

public class ModifyEntry {
	
	/**
	 * Modify first level, root DN child, alias entry.<BR>
	 * This function performs LDAP MODIFY operation, add attributes.
	 */
	public static LDAPResult modifyAliasAddAttributes(LDAPConnection ldapConnection, int id) throws LDAPException {
		
		Modification[] mod = {
				new Modification(ModificationType.ADD, "cn", "value1"),
				new Modification(ModificationType.ADD, "cn", "value2"),
				new Modification(ModificationType.ADD, "NodeIdentity", "value1"),
		};
		
		String rootDN = LdapFunctionTest.getDN();
		
		LDAPResult result = ldapConnection.modify("APP=" + id + "," + rootDN, mod);
		return result;

	}
	
	/**
	 * Modify first level, root DN child, alias entry.<BR>
	 * This function performs LDAP MODIFY operation, delete attribute, value pair.
	 */
	public static LDAPResult modifyAliasDelAttribute(LDAPConnection ldapConnection, int id) throws LDAPException {
		
		Modification[] mod = {
				new Modification(ModificationType.DELETE, "cn", "value2"),
		};
		
		String rootDN = LdapFunctionTest.getDN();
		
		LDAPResult result = ldapConnection.modify("APP=" + id + "," + rootDN, mod);
		return result;

	}

	/**
	 * Modify first level, root DN child, alias entry.<BR>
	 * This function performs LDAP MODIFY operation, replace attribute value.
	 */
	public static LDAPResult modifyAliasReplaceAttributes(LDAPConnection ldapConnection, int id) throws LDAPException {
		
		Modification[] mod = {
				new Modification(ModificationType.REPLACE, "NodeIdentity", "value3"),
		};
		
		String rootDN = LdapFunctionTest.getDN();
		
		LDAPResult result = ldapConnection.modify("APP=" + id + "," + rootDN, mod);
		return result;

	}
	
	/**
	 * Modify first level, root DN child, entry.<BR>
	 * This function performs LDAP MODIFY operation, add attributes.
	 */
	public static LDAPResult modifyEntryAddAttributes(LDAPConnection ldapConnection, int id) throws LDAPException {
		
		Modification[] mod = {
				new Modification(ModificationType.ADD, "cn", "value1"),
				new Modification(ModificationType.ADD, "cn", "value2"),
				new Modification(ModificationType.ADD, "NodeIdentity", "value1"),
		};
		
		String rootDN = LdapFunctionTest.getDN();
		
		LDAPResult result = ldapConnection.modify("ApplicationName=APP" + id + "," + rootDN, mod);
		return result;

	}
	
	/**
	 * Modify second level, root DN child sub-entry.<BR>
	 * This function performs LDAP MODIFY operation, add attributes.
	 */
	public static LDAPResult modifySubentryAddAttributes(LDAPConnection ldapConnection, int id, int subId) throws LDAPException {
		
		Modification[] mod = {
				new Modification(ModificationType.ADD, "cn", "value1"),
				new Modification(ModificationType.ADD, "cn", "value2"),
		};
		
		String rootDN = LdapFunctionTest.getDN();
		
		LDAPResult result = ldapConnection.modify("UserContainerName=MOD" + subId + ",ApplicationName=APP" + id + "," + rootDN, mod);
		return result;

	}
	
	/**
	 * Modify first level, root DN child, entry.<BR>
	 * This function performs LDAP MODIFY operation, delete attribute, value pair.
	 */
	public static LDAPResult modifyEntryDelAttribute(LDAPConnection ldapConnection, int id) throws LDAPException {
		
		Modification[] mod = {
				new Modification(ModificationType.DELETE, "cn", "value2"),
		};
		
		String rootDN = LdapFunctionTest.getDN();
		
		LDAPResult result = ldapConnection.modify("ApplicationName=APP" + id + "," + rootDN, mod);
		return result;

	}
	
	/**
	 * Modify second level, root DN child sub-entry.<BR>
	 * This function performs LDAP MODIFY operation, delete attribute, value pair.
	 */
	public static LDAPResult modifySubentryDelAttribute(LDAPConnection ldapConnection, int id, int subId) throws LDAPException {
		
		Modification[] mod = {
				new Modification(ModificationType.DELETE, "cn", "value2"),
		};
		
		String rootDN = LdapFunctionTest.getDN();
		
		LDAPResult result = ldapConnection.modify("UserContainerName=MOD" + subId + ",ApplicationName=APP" + id + "," + rootDN, mod);
		return result;

	}
	
	/**
	 * Modify first level, root DN child, entry.<BR>
	 * This function performs LDAP MODIFY operation, replace attribute value.
	 */
	public static LDAPResult modifyEntryReplaceAttributes(LDAPConnection ldapConnection, int id) throws LDAPException {
		
		Modification[] mod = {
				new Modification(ModificationType.REPLACE, "NodeIdentity", "value3"),
		};
		
		String rootDN = LdapFunctionTest.getDN();
		
		LDAPResult result = ldapConnection.modify("ApplicationName=APP" + id + "," + rootDN, mod);
		return result;

	}
	
	/**
	 * Modify second level, root DN child sub-entry.<BR>
	 * This function performs LDAP MODIFY operation, replace attribute value.
	 */
	public static LDAPResult modifySubentryReplaceAttributes(LDAPConnection ldapConnection, int id, int subId) throws LDAPException {
		
		Modification[] mod = {
				new Modification(ModificationType.REPLACE, "cn", "value3"),
		};
		
		String rootDN = LdapFunctionTest.getDN();
		
		LDAPResult result = ldapConnection.modify("UserContainerName=MOD" + subId + ",ApplicationName=APP" + id + "," + rootDN, mod);
		return result;

	}
	
	/**
	 * Check first level, root DN child, modified entry.<BR>
	 * This function performs LDAP SEARCH operation with 
	 *  scope BASE, default filter and empty attribute list.
	 */
	public static boolean checkModifiedEntry(LDAPConnection ldapConnection, int id) throws LDAPException {
		
		String rootDN = LdapFunctionTest.getDN();
		
		/*
		 * Check first level entry.
		 */
		String baseDN = "ApplicationName=APP" + id + "," + rootDN;
		SearchScope scope = SearchScope.BASE;
		Filter filter = Filter.create("(objectClass=*)");
		String[] attributes = null;
		
		SearchRequest arg0 = new SearchRequest(baseDN, scope, filter, attributes);
		
		SearchResult searchResult = ldapConnection.search(arg0);
		
		int count = 0;
		
		for ( SearchResultEntry resultEntry : searchResult.getSearchEntries())
		{
			if (resultEntry.hasAttributeValue("cn", "value1") &&
					resultEntry.hasAttributeValue("NodeIdentity","value3"))
			{
				count++;
			}
			
		}
		
		return (count == 1);
	}
	
	
	/**
	 * Check first level, root DN child, modified alias entry.<BR>
	 * This function performs LDAP SEARCH operation with 
	 *  scope BASE, default filter and empty attribute list
	 *  and dereference policy set to ALWAYS.
	 */
	public static boolean checkModifiedAlias(LDAPConnection ldapConnection, int id) throws LDAPException {
		
		String rootDN = LdapFunctionTest.getDN();
		
		/*
		 * Check first level entry.
		 */
		String baseDN = "APP=" + id + "," + rootDN;
		SearchScope scope = SearchScope.BASE;
		Filter filter = Filter.create("(objectClass=*)");
		String[] attributes = null;
		
		SearchRequest arg0 = new SearchRequest(baseDN, scope, filter, attributes);
		arg0.setDerefPolicy(DereferencePolicy.ALWAYS);
		
		SearchResult searchResult = ldapConnection.search(arg0);
		
		int count = 0;
		
		for ( SearchResultEntry resultEntry : searchResult.getSearchEntries())
		{
			if (resultEntry.hasAttributeValue("cn", "value1") &&
					resultEntry.hasAttributeValue("NodeIdentity","value3"))
			{
				count++;
			}
			
		}
		
		return (count == 1);
	}
	
	/**
	 * Modify invalid DN entry.<BR>
	 * This function performs LDAP MODIFY operation.
	 */
	public static LDAPResult modifyInvalidEntry(LDAPConnection ldapConnection) throws LDAPException {
		
		Modification[] mod = {
				new Modification(ModificationType.REPLACE, "cn", "value3"),
		};
		
		LDAPResult result = ldapConnection.modify("UserContainerName=MODxx,ApplicationName=APPyy,o=company,dc=com", mod);
		return result;

	}
	
}
