package hr.ericsson.pegasus.test;

import com.unboundid.ldap.sdk.LDAPException;

/**
 * <H1>TestCase07</H1>
 * This test case will test LDAP operations:
 * <UL>
 *  <LI>ADD</LI>
 *  <LI>SEARCH with dereferencing set to ALWAYS</LI>
 *  <LI>MODIFY of alias entry</LI>
 *  <LI>DELETE</LI>
 * </UL>
 * Test level: BASIC
 * @author igor.delac@gmail.com
 *
 */
public class TestCase07 extends LdapTestCase {

	/**
	 * {@link TestCase07}
	 */
	public TestCase07() {
		id = 07;
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
		 * Add entry and alias reference for entry. 
		 */
		if ((AddEntry.addEntry(ldapConnection, id).getResultCode().intValue() != 0) ||
				(AddAliasEntry.addAlias(ldapConnection, id).getResultCode().intValue() != 0) )
		{
			setVerdict(FAIL);
			return;
		}
		
		/*
		 * Check alias entry.
		 */
		if ( CheckEntry.checkAliasEntry(ldapConnection, id))
		{
			// OK, continue.
		}
		else
		{
			setVerdict(FAIL);
		}
		
		/*
		 * Modify alias entry, add, delete, replace attributes.
		 */
		if ( (ModifyEntry.modifyAliasAddAttributes(ldapConnection, id).getResultCode().intValue() != 0) ||
				(ModifyEntry.modifyAliasDelAttribute(ldapConnection, id).getResultCode().intValue() != 0) ||
				(ModifyEntry.modifyAliasReplaceAttributes(ldapConnection, id).getResultCode().intValue() != 0)
				)
		{
			setVerdict(FAIL);
		}
		
		if ( !ModifyEntry.checkModifiedAlias(ldapConnection, id) )
		{
			setVerdict(FAIL);
		}
		
		
		/*
		 * Delete created entry and its alias entry.
		 */
		if ( (DeleteEntry.delEntry(ldapConnection, id).getResultCode().intValue() != 0) || 
				(DeleteAliasEntry.delAliasEntry(ldapConnection, id).getResultCode().intValue() != 0) )
		{
			setVerdict(FAIL);
			return;
		}

		setVerdict(PASS);
	}
}
