package hr.ericsson.pegasus.test;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchResultEntry;

/**
 * <H1>TestCase12</H1>
 * This test case will test LDAP operations with empty DN:
 * <UL>
 *  <LI>SEARCH</LI>
 * </UL>
 * Test level: NEGATIVE
 * @author eigorde
 *
 */
public class TestCase12 extends LdapTestCase {

	/**
	 * {@link TestCase12}
	 */
	public TestCase12() {
		id = 12;
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
		 * Perform LDAP search operation with base DN set to empty value.
		 */
		try {
			SearchResultEntry result = ldapConnection.getEntry("");
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
