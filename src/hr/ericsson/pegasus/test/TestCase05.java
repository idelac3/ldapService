package hr.ericsson.pegasus.test;

import com.unboundid.ldap.sdk.LDAPException;

/**
 * <H1>TestCase05</H1>
 * This test case will test LDAP operations:
 * <UL>
 *  <LI>ADD</LI>
 *  <LI>SEARCH with scope SUB, filters and attribute list</LI>
 *  <LI>DELETE</LI>
 * </UL>
 * Test level: BASIC
 * @author eigorde
 *
 */
public class TestCase05 extends LdapTestCase {

	/**
	 * {@link TestCase05}
	 */
	public TestCase05() {
		id = 05;
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
		if (CheckEntry.checkEntryAndSubEntry(ldapConnection, id, subId) &&
				CheckEntry.checkEntryAndSubEntryAttributeSet(ldapConnection, id, subId) &&
				CheckEntry.checkEntryAndSubEntryFilterSet(ldapConnection, id, subId) && 
				CheckEntry.checkEntryAndSubEntryFilterSetAttributeSet(ldapConnection, id, subId) )
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
