package hr.ericsson.pegasus.test;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;

/**
 * <H1>TestCase09</H1>
 * This test case will test LDAP operations:
 * <UL>
 *  <LI>Schema query, SEARCH with scope BASE</LI>
 * </UL>
 * Test level: BASIC
 * @author igor.delac@gmail.com
 *
 */
public class TestCase09 extends LdapTestCase {

	/**
	 * {@link TestCase09}
	 */
	public TestCase09() {
		id = 9;
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
		 * Simple schema query.
		 */
		String baseDN = "";
		SearchScope scope = SearchScope.BASE;
		Filter filter = Filter.create("(objectClass=*)");
		String[] attributes = {"subschemaSubentry"};
		
		SearchRequest searchRequest = new SearchRequest(baseDN, scope, filter, attributes);
		
		SearchResult searchResult = ldapConnection.search(searchRequest);
		
		for ( SearchResultEntry entry : searchResult.getSearchEntries())
		{
			/*
			 * Look for
			 *  Attribute(name=subschemaSubentry, values={'cn=Subschema'})
			 * result.
			 */
			boolean found = false;
			
			for (Attribute attr : entry.getAttributes()) {
			
				if (attr.getName().equalsIgnoreCase("subschemaSubentry")) {
					if (attr.getValue().equalsIgnoreCase("cn=Subschema")) {
						found = true;
						break;
					}
				}
			
			}
			
			if (found)
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
