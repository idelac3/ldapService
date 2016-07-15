package hr.ericsson.pegasus.test;

import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;

/**
 * <H1>TestCase01</H1>
 * This test case will test LDAP operations:
 * <UL>
 *  <LI>SEARCH with scope BASE</LI>
 * </UL>
 * Test level: BASIC
 * @author eigorde
 *
 */
public class TestCase01 extends LdapTestCase {

	/**
	 * {@link TestCase01}
	 */
	public TestCase01() {
		id = 01;
		setName("TestCase" + id);
	}
	
	@Override
	public void run() {
			try {
				test();
			}
			catch (LDAPException ex) {
				setVerdict(FAIL);
				ex.printStackTrace();
			}
	}

	private void test() throws LDAPException {
		
		/*
		 * Simple SEARCH request with base DN set to:
		 *  "o=ericsson,dc=com"
		 */
		String baseDN = "o=ericsson,dc=com";
		SearchScope scope = SearchScope.BASE;
		Filter filter = Filter.create("(objectClass=*)");
		String[] attributes = null;
		
		SearchRequest searchRequest = new SearchRequest(baseDN, scope, filter, attributes);
		
		SearchResult searchResult = ldapConnection.search(searchRequest);
		
		for ( SearchResultEntry entry : searchResult.getSearchEntries())
		{
			if (entry.hasAttributeValue("o", "ericsson") )
			{
				setVerdict(PASS);
			}
			else
			{
				setVerdict(FAIL);
			}
		}		
	}
}
