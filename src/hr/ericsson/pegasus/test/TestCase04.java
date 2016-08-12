package hr.ericsson.pegasus.test;

import com.unboundid.ldap.sdk.LDAPException;

/**
 * <H1>TestCase04</H1>
 * This test case will test LDAP operations:
 * <UL>
 *  <LI>ADD</LI>
 *  <LI>SEARCH with scope ONE, filters and attribute list</LI>
 *  <LI>DELETE</LI>
 * </UL>
 * Test level: BASIC
 * @author igor.delac@gmail.com
 *
 */
public class TestCase04 extends LdapTestCase {

	/**
	 * {@link TestCase04}
	 */
	public TestCase04() {
		id = 04;
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

		int subId = 1;
		
		/*
		 * Add entry and sub entry. 
		 */
		if (AddEntry.addEntry(ldapConnection, id).getResultCode().intValue() != 0) 
		{
			setVerdict(FAIL);
			return;
		}
		
		if (AddSubEntry.addSubEntry(ldapConnection, id, subId).getResultCode().intValue() != 0) 
		{
			setVerdict(FAIL);
			return;
		}
		
		/*
		 * Check both entries.
		 */		
		if (CheckEntry.checkSubEntry(ldapConnection, id, subId) &&
				CheckEntry.checkSubEntryAttributeSet(ldapConnection, id, subId) &&
				CheckEntry.checkSubEntryFilterSet(ldapConnection, id, subId) && 
				CheckEntry.checkSubEntryFilterSetAttributeSet(ldapConnection, id, subId) )
		{
			// setVerdict(PASS);
		}
		else
		{
			setVerdict(FAIL);
			
			DeleteSubEntry.delSubEntry(ldapConnection, id, subId);
			DeleteEntry.delEntry(ldapConnection, id);
			
			return;
		}
		
		/*
		 * Delete created entries.
		 */
		if ( (DeleteSubEntry.delSubEntry(ldapConnection, id, subId).getResultCode().intValue() != 0) ||
				(DeleteEntry.delEntry(ldapConnection, id).getResultCode().intValue() != 0) )
		{
			setVerdict(FAIL);
			return;
		}

		setVerdict(PASS);
	}
}
