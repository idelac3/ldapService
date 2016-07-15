package hr.ericsson.pegasus.test;

import com.unboundid.ldap.sdk.LDAPException;

/**
 * <H1>TestCase06</H1>
 * This test case will test LDAP operations:
 * <UL>
 *  <LI>ADD</LI>
 *  <LI>MODIFY with attribute add, delete, replace</LI>
 *  <LI>DELETE</LI>
 * </UL>
 * Test level: BASIC
 * @author eigorde
 *
 */
public class TestCase06 extends LdapTestCase {

	/**
	 * {@link TestCase06}
	 */
	public TestCase06() {
		id = 06;
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
		 * Add entry. 
		 */
		if (AddEntry.addEntry(ldapConnection, id).getResultCode().intValue() != 0) 
		{
			setVerdict(FAIL);
			return;
		}
		
		/*
		 * Modify entry, add, delete, replace attributes.
		 */
		if ( (ModifyEntry.modifyEntryAddAttributes(ldapConnection, id).getResultCode().intValue() != 0) ||
				(ModifyEntry.modifyEntryDelAttribute(ldapConnection, id).getResultCode().intValue() != 0) ||
				(ModifyEntry.modifyEntryReplaceAttributes(ldapConnection, id).getResultCode().intValue() != 0)
				)
		{
			setVerdict(FAIL);
		}
		
		if ( !ModifyEntry.checkModifiedEntry(ldapConnection, id))
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
