package hr.ericsson.pegasus.test;

import com.unboundid.ldap.sdk.DereferencePolicy;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;

public class CheckEntry {

	/**
	 * Check first level, root DN child, alias entry.<BR>
	 * This function performs LDAP SEARCH operation with 
	 *  scope BASE, default filter and empty attribute list
	 *  and does alias dereferencing.
	 */
	public static boolean checkAliasEntry(LDAPConnection ldapConnection, int id) throws LDAPException {
		
		/*
		 * Check first level entry.
		 */
		String baseDN = "APP=" + id + ",o=ericsson,dc=com";
		SearchScope scope = SearchScope.BASE;
		Filter filter = Filter.create("(objectClass=*)");
		String[] attributes = null;
		
		SearchRequest arg0 = new SearchRequest(baseDN, scope, filter, attributes);
		arg0.setDerefPolicy(DereferencePolicy.ALWAYS);
		
		SearchResult searchResult = ldapConnection.search(arg0);
		
		int count = 0;
		
		for ( SearchResultEntry resultEntry : searchResult.getSearchEntries())
		{
			if (resultEntry.hasAttributeValue("ApplicationName", "APP" + id) )
			{
				count++;
			}
			
		}
		
		return (count == 1);
	}
	
	/**
	 * Check first level, root DN child, entry.<BR>
	 * This function performs LDAP SEARCH operation with 
	 *  scope BASE, default filter and empty attribute list.
	 */
	public static boolean checkEntry(LDAPConnection ldapConnection, int id) throws LDAPException {
		
		/*
		 * Check first level entry.
		 */
		String baseDN = "ApplicationName=APP" + id + ",o=ericsson,dc=com";
		SearchScope scope = SearchScope.BASE;
		Filter filter = Filter.create("(objectClass=*)");
		String[] attributes = null;
		
		SearchRequest arg0 = new SearchRequest(baseDN, scope, filter, attributes);
		
		SearchResult searchResult = ldapConnection.search(arg0);
		
		int count = 0;
		
		for ( SearchResultEntry resultEntry : searchResult.getSearchEntries())
		{
			if (resultEntry.hasAttributeValue("ApplicationName", "APP" + id) )
			{
				count++;
			}
			
		}
		
		return (count == 1);
	}
	
	/**
	 * Check first level, root DN child, entry.<BR>
	 * This function performs LDAP SEARCH operation with 
	 *  scope BASE, default filter and attribute list set.
	 */
	public static boolean checkEntryAttributeSet(LDAPConnection ldapConnection, int id) throws LDAPException {
		
		/*
		 * Check first level entry.
		 */
		String baseDN = "ApplicationName=APP" + id + ",o=ericsson,dc=com";
		SearchScope scope = SearchScope.BASE;
		Filter filter = Filter.create("(objectClass=*)");
		String[] attributes = { "ApplicationName" };
		
		SearchRequest arg0 = new SearchRequest(baseDN, scope, filter, attributes);
		
		SearchResult searchResult = ldapConnection.search(arg0);
		
		int count = 0;
		
		for ( SearchResultEntry resultEntry : searchResult.getSearchEntries())
		{
			if (resultEntry.hasAttributeValue("ApplicationName", "APP" + id) )
			{
				count++;
			}
			
		}
		
		return (count == 1);
	}
	

	/**
	 * Check first level, root DN child, entry.<BR>
	 * This function performs LDAP SEARCH operation with 
	 *  scope BASE, custom filter and empty attribute list.
	 */
	public static boolean checkEntryFilterSet(LDAPConnection ldapConnection, int id) throws LDAPException {
		
		/*
		 * Check first level entry.
		 */
		String baseDN = "ApplicationName=APP" + id + ",o=ericsson,dc=com";
		SearchScope scope = SearchScope.BASE;
		Filter filter = Filter.create("(ApplicationName=APP" + id + ")");
		String[] attributes = {};
		
		SearchRequest arg0 = new SearchRequest(baseDN, scope, filter, attributes);
		
		SearchResult searchResult = ldapConnection.search(arg0);
		
		int count = 0;
		
		for ( SearchResultEntry resultEntry : searchResult.getSearchEntries())
		{
			if (resultEntry.hasAttributeValue("ApplicationName", "APP" + id) &&
					resultEntry.hasAttribute("objectClass"))
			{
				count++;
			}
			
		}
		
		return (count == 1);
	}


	/**
	 * Check first level, root DN child, entry.<BR>
	 * This function performs LDAP SEARCH operation with 
	 *  scope BASE, custom filter and attribute list set.
	 */
	public static boolean checkEntryFilterSetAttributeSet(LDAPConnection ldapConnection, int id) throws LDAPException {
		
		/*
		 * Check first level entry.
		 */
		String baseDN = "ApplicationName=APP" + id + ",o=ericsson,dc=com";
		SearchScope scope = SearchScope.BASE;
		Filter filter = Filter.create("(ApplicationName=APP" + id + ")");
		String[] attributes = { "ApplicationName" };
		
		SearchRequest arg0 = new SearchRequest(baseDN, scope, filter, attributes);
		
		SearchResult searchResult = ldapConnection.search(arg0);
		
		int count = 0;
		
		for ( SearchResultEntry resultEntry : searchResult.getSearchEntries())
		{
			if (resultEntry.hasAttributeValue("ApplicationName", "APP" + id) &&
					!(resultEntry.hasAttribute("objectClass")) )
			{
				count++;
			}
			
		}
		
		return (count == 1);
	}
	
	/**
	 * Check second level, root DN child's child entry.<BR>
	 * This function performs LDAP SEARCH operation with 
	 *  scope ONE, default filter and empty attribute list.
	 */
	public static boolean checkSubEntry(LDAPConnection ldapConnection, int id, int subId) throws LDAPException {
		
		/*
		 * Check second level entry.
		 */
		String baseDN = "ApplicationName=APP" + id + ",o=ericsson,dc=com";
		SearchScope scope = SearchScope.ONE;
		Filter filter = Filter.create("(objectClass=*)");
		String[] attributes = null;
		
		SearchRequest arg0 = new SearchRequest(baseDN, scope, filter, attributes);
		
		SearchResult searchResult = ldapConnection.search(arg0);
		
		int count = 0;
		
		for ( SearchResultEntry resultEntry : searchResult.getSearchEntries())
		{
			if (resultEntry.hasAttributeValue("UserContainerName", "MOD" + subId) )
			{
				count++;
			}
			
		}
		
		return (count == 1);
	}
	
	
	/**
	 * Check second level, root DN child's child entry.<BR>
	 * This function performs LDAP SEARCH operation with 
	 *  scope ONE, default filter and attribute list set.
	 */
	public static boolean checkSubEntryAttributeSet(LDAPConnection ldapConnection, int id, int subId) throws LDAPException {
		
		/*
		 * Check second level entry.
		 */
		String baseDN = "ApplicationName=APP" + id + ",o=ericsson,dc=com";
		SearchScope scope = SearchScope.ONE;
		Filter filter = Filter.create("(objectClass=*)");
		String[] attributes = { "UserContainerName" };
		
		SearchRequest arg0 = new SearchRequest(baseDN, scope, filter, attributes);
		
		SearchResult searchResult = ldapConnection.search(arg0);
		
		int count = 0;
		
		for ( SearchResultEntry resultEntry : searchResult.getSearchEntries())
		{
			if (resultEntry.hasAttributeValue("UserContainerName", "MOD" + subId) )
			{
				count++;
			}
			
		}
		
		return (count == 1);
	}

	/**
	 * Check second level, root DN child's child entry.<BR>
	 * This function performs LDAP SEARCH operation with 
	 *  scope ONE, custom filter and empty attribute list.
	 */
	public static boolean checkSubEntryFilterSet(LDAPConnection ldapConnection, int id, int subId) throws LDAPException {
		
		/*
		 * Check second level entry.
		 */
		String baseDN = "ApplicationName=APP" + id + ",o=ericsson,dc=com";
		SearchScope scope = SearchScope.ONE;
		Filter filter = Filter.create("(UserContainerName=MOD" + subId + ")");
		String[] attributes = { };
		
		SearchRequest arg0 = new SearchRequest(baseDN, scope, filter, attributes);
		
		SearchResult searchResult = ldapConnection.search(arg0);
		
		int count = 0;
		
		for ( SearchResultEntry resultEntry : searchResult.getSearchEntries())
		{
			if (resultEntry.hasAttributeValue("UserContainerName", "MOD" + subId) &&
					resultEntry.hasAttribute("objectClass"))
			{
				count++;
			}
			
		}
		
		return (count == 1);
	}


	/**
	 * Check second level, root DN child's child entry.<BR>
	 * This function performs LDAP SEARCH operation with 
	 *  scope ONE, custom filter and attribute list set.
	 */
	public static boolean checkSubEntryFilterSetAttributeSet(LDAPConnection ldapConnection, int id, int subId) throws LDAPException {
		
		/*
		 * Check second level entry.
		 */
		String baseDN = "ApplicationName=APP" + id + ",o=ericsson,dc=com";
		SearchScope scope = SearchScope.ONE;
		Filter filter = Filter.create("(UserContainerName=MOD" + subId + ")");
		String[] attributes = { "UserContainerName" };
		
		SearchRequest arg0 = new SearchRequest(baseDN, scope, filter, attributes);
		
		SearchResult searchResult = ldapConnection.search(arg0);
		
		int count = 0;
		
		for ( SearchResultEntry resultEntry : searchResult.getSearchEntries())
		{
			if (resultEntry.hasAttributeValue("UserContainerName", "MOD" + subId) &&
					!(resultEntry.hasAttribute("objectClass")) )
			{
				count++;
			}
			
		}
		
		return (count == 1);
	}
	
	/**
	 * Check first & second level, root DN child entry and it's sub entry.<BR>
	 * This function performs LDAP SEARCH operation with 
	 *  scope SUB, default filter and empty attribute list.
	 */
	public static boolean checkEntryAndSubEntry(LDAPConnection ldapConnection, int id, int subId) throws LDAPException {
		
		/*
		 * Check second level entry.
		 */
		String baseDN = "ApplicationName=APP" + id + ",o=ericsson,dc=com";
		SearchScope scope = SearchScope.SUB;
		Filter filter = Filter.create("(objectClass=*)");
		String[] attributes = null;
		
		SearchRequest arg0 = new SearchRequest(baseDN, scope, filter, attributes);
		
		SearchResult searchResult = ldapConnection.search(arg0);
		
		int count = 0;
		
		for ( SearchResultEntry resultEntry : searchResult.getSearchEntries())
		{
			if (resultEntry.hasAttributeValue("UserContainerName", "MOD" + subId) ||
					resultEntry.hasAttributeValue("ApplicationName", "APP" + id) )
			{
				count++;
			}
			
		}
		
		return (count == 2);
	}
	

	/**
	 * Check first & second level, root DN child entry and it's sub entry.<BR>
	 * This function performs LDAP SEARCH operation with 
	 *  scope SUB, default filter and attribute list set.
	 */
	public static boolean checkEntryAndSubEntryAttributeSet(LDAPConnection ldapConnection, int id, int subId) throws LDAPException {
		
		/*
		 * Check second level entry.
		 */
		String baseDN = "ApplicationName=APP" + id + ",o=ericsson,dc=com";
		SearchScope scope = SearchScope.SUB;
		Filter filter = Filter.create("(objectClass=*)");
		String[] attributes = { "ApplicationName", "UserContainerName" };
		
		SearchRequest arg0 = new SearchRequest(baseDN, scope, filter, attributes);
		
		SearchResult searchResult = ldapConnection.search(arg0);
		
		int count = 0;
		
		for ( SearchResultEntry resultEntry : searchResult.getSearchEntries())
		{
			if (resultEntry.hasAttributeValue("UserContainerName", "MOD" + subId) ||
					resultEntry.hasAttributeValue("ApplicationName", "APP" + id) )
			{
				count++;
			}
			
		}
		
		return (count == 2);
	}
	

	/**
	 * Check first & second level, root DN child entry and it's sub entry.<BR>
	 * This function performs LDAP SEARCH operation with 
	 *  scope SUB, custom filter and empty attribute list.
	 */
	public static boolean checkEntryAndSubEntryFilterSet(LDAPConnection ldapConnection, int id, int subId) throws LDAPException {
		
		/*
		 * Check second level entry.
		 */
		String baseDN = "ApplicationName=APP" + id + ",o=ericsson,dc=com";
		SearchScope scope = SearchScope.SUB;
		Filter filter = Filter.create("(|(ApplicationName=APP" + id + ")(UserContainerName=MOD" + subId + "))");
		String[] attributes = { };
		
		SearchRequest arg0 = new SearchRequest(baseDN, scope, filter, attributes);
		
		SearchResult searchResult = ldapConnection.search(arg0);
		
		int count = 0;
		
		for ( SearchResultEntry resultEntry : searchResult.getSearchEntries())
		{
			if (resultEntry.hasAttributeValue("UserContainerName", "MOD" + subId) ||
					resultEntry.hasAttributeValue("ApplicationName", "APP" + id) )
			{
				count++;
			}
			
		}
		
		return (count == 2);
	}

	/**
	 * Check first & second level, root DN child entry and it's sub entry.<BR>
	 * This function performs LDAP SEARCH operation with 
	 *  scope SUB, custom filter and empty attribute list.
	 */
	public static boolean checkEntryAndSubEntryFilterSetAttributeSet(LDAPConnection ldapConnection, int id, int subId) throws LDAPException {
		
		/*
		 * Check second level entry.
		 */
		String baseDN = "ApplicationName=APP" + id + ",o=ericsson,dc=com";
		SearchScope scope = SearchScope.SUB;
		Filter filter = Filter.create("(|(ApplicationName=APP" + id + ")(UserContainerName=MOD" + subId + "))");
		String[] attributes = { "ApplicationName", "UserContainerName" };
		
		SearchRequest arg0 = new SearchRequest(baseDN, scope, filter, attributes);
		
		SearchResult searchResult = ldapConnection.search(arg0);
		
		int count = 0;
		
		for ( SearchResultEntry resultEntry : searchResult.getSearchEntries())
		{
			if (resultEntry.hasAttributeValue("UserContainerName", "MOD" + subId) ||
					resultEntry.hasAttributeValue("ApplicationName", "APP" + id) &&
					!(resultEntry.hasAttribute("objectClass")) )
			{
				count++;
			}
			
		}
		
		return (count == 2);
	}
}
