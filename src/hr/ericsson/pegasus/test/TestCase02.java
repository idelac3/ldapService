package hr.ericsson.pegasus.test;

import com.unboundid.ldap.sdk.LDAPException;

/**
 * <H1>TestCase02</H1>
 * This test case will test LDAP operations:
 * <UL>
 *  <LI>ADD</LI>
 *  <LI>SEARCH with scope BASE, filters set and attribute list set</LI>
 *  <LI>DELETE</LI>
 * </UL>
 * Test level: BASIC
 * @author eigorde
 *
 */
public class TestCase02 extends LdapTestCase {

	/**
	 * {@link TestCase02}
	 */
	public TestCase02() {
		id = 02;
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
		 * Add entry and sub entry. 
		 */
		if (AddEntry.addEntry(ldapConnection, id).getResultCode().intValue() != 0) 
		{
			setVerdict(FAIL);
			return;
		}
		
		/*
		 * Check newly created entry.
		 */
		if ( CheckEntry.checkEntry(ldapConnection, id) && 
				CheckEntry.checkEntryAttributeSet(ldapConnection, id) &&
				CheckEntry.checkEntryFilterSet(ldapConnection, id) &&
				CheckEntry.checkEntryFilterSetAttributeSet(ldapConnection, id) )
		{
			// OK
		}
		else
		{
			setVerdict(FAIL);
		}
		
		/*
		 * Delete created entries.
		 */
		if ( DeleteEntry.delEntry(ldapConnection, id).getResultCode().intValue() != 0 )
		{
			setVerdict(FAIL);
			return;
		}

		setVerdict(PASS);
	}
}
