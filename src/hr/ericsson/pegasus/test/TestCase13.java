package hr.ericsson.pegasus.test;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchResultEntry;

/**
 * <H1>TestCase13</H1>
 * This test case will test LDAP operations with invalid DN:
 * <UL>
 *  <LI>SEARCH</LI>
 * </UL>
 * Test level: NEGATIVE
 * @author igor.delac@gmail.com
 *
 */
public class TestCase13 extends LdapTestCase {

	/**
	 * {@link TestCase13}
	 */
	public TestCase13() {
		id = 13;
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
		 * Perform LDAP search operation with base DN set to wrong value.
		 */
		try {
			SearchResultEntry result = ldapConnection.getEntry("o=company,dc=com");
			if (result == null)
			{
				setVerdict(PASS);
			}
			else
			{
				setVerdict(FAIL);
			}
		}
		catch (LDAPException ex)
		{
			setVerdict(PASS);
		}
		
		setVerdict(FAIL);
	}
}
